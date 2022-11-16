package com.rdexpense.manager.imgsys;

import com.common.base.dao.redis.RedisDao;
import com.common.entity.PageData;
import com.common.util.AwsUtil;
import com.common.util.ConstantValUtil;
import com.rdexpense.manager.util.OutRequestUtil;
import com.rdexpense.manager.util.WaitFileUnlock;
import io.lettuce.core.dynamic.output.OutputRegistry;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class ImgFileUpload {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImgFileUpload.class);

    private static String awsId;
    private static String awsKey;
    private static String awsRegion;
    private static String awsServiceUrl;

    private static String imgBKTempPath;

    private RedisDao redisDao;

    @Value("img.bk.temp.path")
    public void setImgBKTempPath(String imgBKTempPath) {this.imgBKTempPath = imgBKTempPath; }

    @Value("${aws.access_key_id}")
    public void setAwsKey(String awsId) {this.awsId = awsId; }

    @Value("${aws.secret_access_key}")
    public void setSecretAccessKey(String awsKey) {
        this.awsKey = awsKey;
    }

    @Value("${aws.s3.region}")
    public void setRegion(String region) {
        this.awsRegion = region;
    }

    @Value("${oauth2.service.url}")
    public void setAwsServiceUrl(String serviceUrl) {
        this.awsServiceUrl = serviceUrl;
    }

    public void setRedisDao(RedisDao redisDao) {
        this.redisDao = redisDao;
    }

    /**
     * honjun zhai, 2022-10-26
     * 将检测到的文件上传至对象存储
     * @param fname：文件名
     * @return：上传成功后入库所需的短文件名
     */
    public PageData fileUpload(String fname) {
        // 1. 等待文件的占用进程释放文件
        File f1 = new File(fname);
        new WaitFileUnlock().waitFileUnlock(f1);

        // 2. 上传文件
        // 2.1 准备multiPartFile
        FileInputStream fis = null;
        MultipartFile mpFile = createMfileByPath(fname);

        // 2.2 链接对象存储服务器，上传文件
        AwsUtil aswUtil = new AwsUtil();
        aswUtil.setAwsKey(awsId);
        aswUtil.setSecretAccessKey(awsKey);
        aswUtil.setRegion(awsRegion);
        PageData pageData = aswUtil.upload(mpFile, ConstantValUtil.BUCKET_PRIVATE);

        logger.info("文件被上传, 创建位置为： " + fname);

        // 2.3 关闭文件流
        try {
            mpFile.getInputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pageData;
    }


    /**
     * honjun zhai, 2022-10-26
     * 创建MultiPart文件
     * @param path：文件路径
     * @return：创建成功的MultiPart文件
     */
    private static MultipartFile createMfileByPath(String path) {
        MultipartFile mFile = null;

        try {
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);

            String fileName = file.getName();
            fileName = fileName.substring((fileName.lastIndexOf("/") + 1));
            mFile = new MockMultipartFile(fileName, fileName, ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);

        } catch (Exception e) {
            logger.info("multipart文件封装出现错误{}" + path);
            e.printStackTrace();
        }
        return mFile;
    }

//    /**
//     * honjun zhai, 2022-10-26
//     * 文件上传对象存储失败后的处理：1.获取采集患者; 2.转存上传失败的文件（患者id_时间_文件名）
//     * @param pageData：文件上传对象存储时返回的PageData
//     * @param fileName：失败的文件名
//     */
//    private void ImgFileUploadFailure(PageData pageData, String fileName) {
//        /** 文件上传失败 */
//        if (null == pageData || StringUtils.isNotBlank(pageData.getString("url"))) {
//
//            /** TODO:获取患者信息和服务器时间 */
//            ImgFileInfoSubmit
//
//            /** TODO:转存文件 */
//        }
//    }

    /**
     * honjun zhai, 2022-10-26
     * 影像信息上传应用服务器
     * @param pd：文件信息
     * @param path：后台接口url
     * @return
     */
    public PageData submitImgFileInfo2Svr(PageData pd, String path) {
        OutRequestUtil outRequestUtil = new OutRequestUtil();
        outRequestUtil.setRedisDao(redisDao);

        String url = awsServiceUrl + path;

        PageData response = outRequestUtil.get(pd, url);

        return response;
    }


}
