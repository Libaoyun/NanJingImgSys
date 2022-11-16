package com.rdexpense.manager.imgsys;

import com.common.base.dao.redis.RedisDao;
import com.common.entity.PageData;
import com.rdexpense.manager.util.MacAddressUtil;
import com.rdexpense.manager.util.WaitFileUnlock;
import jodd.util.StringUtil;
import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyAdapter;
import net.contentobjects.jnotify.JNotifyException;
import net.coobird.thumbnailator.Thumbnails;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

@Component
public class ImgFileCreationMonitor extends JNotifyAdapter {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImgFileCreationMonitor.class);

    /** 需要监听的文件路径地址 */
    private static String monitorPath;
    private static String thumbnailPath;

    /** 影像缩略图的尺度*/
    private int thumbnailHeight;
    private int thumbnailWith;
    private static String strThumbnailHeight;
    private static String strThumbnailWidth;

    /** 支持的影像文件格式*/
    private static String imgFileSuffixs;


    /** 关注目录的事件 */
    int mask = JNotify.FILE_CREATED;// | JNotify.FILE_DELETED | JNotify.FILE_MODIFIED | JNotify.FILE_RENAMED;
    /** 是否监视子目录，即级联监视 */
    boolean watchSubtree = true;
    /** 监听程序Id */
    public int watchID;

    RedisDao redisDao;
    private String appToken;

    ImgFileInfoSubmit imgFileInfoSubmit;

    public ImgFileCreationMonitor(RedisDao redisDao) {
        this.redisDao = redisDao;
        imgFileInfoSubmit = new ImgFileInfoSubmit();
        imgFileInfoSubmit.setRedisDao(redisDao);
    }

//    public static void main(String[] args) {
//        new ImgFileCreationMonitor().beginWatch();
//    }

    @Value("${img.monitor.path}")
    public void setMonitorPath(String monitorPath) {
        this.monitorPath = monitorPath;
    }

    @Value("${img.thumbnail.path}")
    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    @Value("${img.thumbnail.height}")
    public void setThumbnailHeight(String thumbnailHeight) {
        this.strThumbnailHeight = thumbnailHeight;
    }

    @Value("${img.thumbnail.width}")
    public void setThumbnailWidth(String thumbnailWidth) {
        this.strThumbnailWidth = thumbnailWidth;
    }

    @Value("${img.suffix}")
    public void setImgFileSuffixs(String imgFileSuffixs){
        this.imgFileSuffixs = imgFileSuffixs;
    }


    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public void setRedisDao(RedisDao redisDao) {
        this.redisDao = redisDao;
    }

    //bmp，jpg，png，tif，gif，pcx

    public void beginWatch() {
        /** 添加到监视队列中 */
        try {
            this.watchID = JNotify.addWatch(monitorPath, mask, watchSubtree, this);

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            logger.info("开始文件监听:df.format(new Date())" + "\t 文件目录:" + monitorPath);
        } catch (JNotifyException e) {
            e.printStackTrace();
        }
        // 死循环，线程一直执行，休眠一分钟后继续执行，主要是为了让主线程一直执行
        // 休眠时间和监测文件发生的效率无关（就是说不是监视目录文件改变一分钟后才监测到，监测几乎是实时的，调用本地系统库）
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {// ignore it
            }
        }
    }

    /**
     * 当监听目录下一旦有新的文件被创建，则即触发该事件
     *
     * @param wd
     *            监听线程id
     * @param rootPath
     *            监听目录
     * @param name
     *            文件名称
     */
    @Override
    public void fileCreated(int wd, String rootPath, String name) {
        logger.info("文件被创建, 创建位置为： " + rootPath + "\\" + name);

        /** 1.检查文件是否被其他进程使用中 */
        File f1 = new File(rootPath + "\\" + name);
        new WaitFileUnlock().waitFileUnlock(f1);

        /** 2.影像文件格式有效性检查，创建缩略图 */
        String fn = f1.getName();
        String fp = f1.getParent();
        String orgFn = fp + "\\" + fn;
        String thumbFn = thumbnailPath + "\\" + "thumbnail_" + fn;
        if (!validImgFile(fp, fn)) {
            return;
        } else if (!createThumbnailFile(orgFn, thumbFn)) {
            return;
        }

        /** 3.将原图和缩略图上传至服务器（对象存储） */
        PageData pageData = uploadOrgThumbFiles2Svr(orgFn, thumbFn);

        /** 4.将原图和缩略图信息存入应用服务器 */
        submitImgFileInfo2Svr(pageData);

        logger.info("文件及其信息均成功提交server： " + orgFn + "," + thumbFn + "!");
        //System.err.println(pageData.getString("filePath"));
    }

    @Override
    public void fileRenamed(int wd, String rootPath, String oldName, String newName) {
        System.err.println("文件被重命名, 原文件名为：" + rootPath + "/" + oldName
                + ", 现文件名为：" + rootPath + "/" + newName);
    }
    @Override
    public void fileModified(int wd, String rootPath, String name) {
        System.err.println("文件内容被修改, 文件名为：" + rootPath + "/" + name);
    }
    @Override
    public void fileDeleted(int wd, String rootPath, String name) {
        System.err.println("文件被删除, 被删除的文件名为：" + rootPath + name);
    }

    /**
     * author: honjun zhai, 2022-10.26
     * 根据原图创建缩略图
     * @param orgFn: 原始图片文件名称
     * @param thumbFn：缩略图文件名称
     * @return：true-缩略图创建成功，false-创建失败
     */
    private boolean createThumbnailFile(String orgFn, String thumbFn) {
        try {
            int with = Integer.parseInt(strThumbnailWidth);
            int height = Integer.parseInt(strThumbnailHeight);
            Thumbnails.of(orgFn).size(with, height).toFile(thumbFn);
            logger.info("Thumbnail文件被创建, 创建位置为： " + thumbFn);
        } catch (IOException e) {
            logger.info("Thumbnail文件创建异常，" + "原因: " + e.getMessage() + "; 位置:" + thumbFn);
            return false;
        }

        return true;
    }

    private PageData failureUploadFile2Aws(String fn)  {

        PageData pd = new PageData();

        MacAddressUtil macAddressUtil = new MacAddressUtil();
        String localMacAddr = macAddressUtil.getLocalMac();
        //String value = (String) redisDao.vGet(appToken);
        pd.put("graphingDeviceCode", localMacAddr);

        /** 获得利用当前采集设备间采集影像的患者信息和服务器时间 */
        PageData res = imgFileInfoSubmit.getImgGraphingPatient(pd);

        if (null != res && StringUtil.isNotBlank(res.getString("patientIdCardNo"))
                && StringUtil.isNotBlank(res.getString("svrCurTime"))) {
            String newFilename = res.getString("patientIdCardNo") + "_" + res.getString("svrCurTime") + fn;


            File startFile = new File("D:\\source\\1.txt");
            File endFile = new File("D:\\target\\2.txt");

        }



        return null;
    }

    /**
     * author: honjun zhai, 2022-10.26
     * 将原始图片文件及其缩略图上传至对象存储服务器
     * @param orgFName：原始图片文件名
     * @param thumbFName：缩略图文件名
     * @return 采集设备id，原始图像文件短路径和缩略图文件短路径
     */
    private PageData uploadOrgThumbFiles2Svr(String orgFName, String thumbFName) {
        /** 1. 原图与缩略图上传对象存储 */
        ImgFileUpload imgFileUpload = new ImgFileUpload();
        PageData pdOrg = imgFileUpload.fileUpload(orgFName);
        PageData pdThumb = imgFileUpload.fileUpload(thumbFName);

        /** 2. TODO:文件上传失败处理 */

        /** 3. 删除创建的缩略图临时文件 */
        File f = new File(thumbFName);
        if (f.exists()) {
            f.delete();
        }

        /** 4. 返回必要信息: 采集设备id，原始影像文件和缩略图短路径 */
        MacAddressUtil macAddressUtil = new MacAddressUtil();
        String localMacAddr = macAddressUtil.getLocalMac();
        //String value = (String) redisDao.vGet(appToken);
        pdOrg.put("graphingDeviceCode", localMacAddr);
        pdOrg.put("originalFile", pdOrg.getString("filePath"));
        pdOrg.put("thumbnailFile", pdThumb.getString("filePath"));

        return pdOrg;
    }

    /**
     * author: honjun zhai, 2022-10.26
     * 判断影像文件后缀名合法
     * @param fp：文件路径
     * @param fn：文件名称
     * @return true-后缀合法，false-后缀非法
     */
    private boolean validImgFile(String fp, String fn) {
        boolean suffix_valid = false;
        String[] imgFileSuffixList = imgFileSuffixs.split(",");
        for (int i = 0; i < imgFileSuffixList.length; i++) {
            if (imgFileSuffixList[i].contains(fn.substring(fn.lastIndexOf(".")+1, fn.length()))){
                suffix_valid = true;
                return suffix_valid;
            }
        }

        logger.info("无效的影像文件： " + fp + "\\" + fn);
        return false;
    }

    /**
     * author: honjun zhai, 2022-10.26
     * 上传文件信息至应用服务器
     * @param pageData：文件信息map
     */
    private void submitImgFileInfo2Svr(PageData pageData) {
        PageData pd = new PageData();

        pd.put("originalFile", pageData.getString("originalFile"));
        pd.put("thumbnailFile", pageData.getString("thumbnailFile"));
        pd.put("graphingDeviceCode", pageData.getString("graphingDeviceCode"));

        //ImgFileInfoSubmit imgFileInfoSubmit = new ImgFileInfoSubmit();
        //imgFileInfoSubmit.setRedisDao(redisDao);
        PageData result = imgFileInfoSubmit.realTimeImgInfoSubmit(pd);
    }

}
