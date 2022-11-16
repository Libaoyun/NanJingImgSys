package com.imgSys.controller;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.*;
import com.imgSys.dto.img.ImgCapturingDto;
import com.imgSys.dto.img.ImgFileInfoDto;
import com.imgSys.dto.img.ImgPhotoSearchDto;
import com.imgSys.dto.img.ImgSerialNumListDto;
import com.imgSys.service.DeviceService;
import com.imgSys.service.ImgMainService;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.controller.projContract.ProjContractSignController;

import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/patientImg")
@Api(value = "影像信息", tags = "影像信息")
public class PatientImgController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ProjContractSignController.class);

    @Autowired
    private ImgMainService imgMainService;

    @Autowired
    private DeviceService deviceInfoService;

    @Autowired
    private LogUtil logUtil;


    /*
    * 功能函数：将图片转换成base64字符串
     */
    private String Img2Base64Str(String imgSrc, String errStr) {
        String b64Str = null;
        try {
            File imgFile = new File(imgSrc);
            FileInputStream fin = new FileInputStream(imgFile);

            byte[] data = new byte[fin.available()];
            fin.read(data);
            fin.close();

            b64Str = Base64.encodeBase64String(data);
            b64Str.replaceAll("\n", "").replaceAll("\r", "");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("读取图片,File=[{}]", imgSrc);
        }

        return b64Str;
    }

    /**
     * 根据查询条件查询患者影像, 返回缩略图、序列号、拍摄时间及aws url
     * @param imgPhotoSearchDto
     * @return
     */

     /*
    * 将患者影像缩略图（多个）的名称、序列号、时间以及内容（base4格式）返回至前端
     */
    @PostMapping("/queryPhotoList")
    @ApiOperation(value = "查询影像列表", notes = "查询影像列表")
    public ResponseEntity<List<PageData>> queryProjectList(ImgPhotoSearchDto imgPhotoSearchDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;

        // 1. 查询满足要求的患者影像
        List<PageData> list = imgMainService.queryImgMain(pd);

        /* 2. 将每张影像缩略图转为base64格式，放入影像信息尾部 */
        List<PageData> listResp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            PageData pData = list.get(i);

            //相对路径
            String imgSrc = pData.getString("thumbnailFile");
            String suffix = imgSrc.substring(imgSrc.lastIndexOf(".")+1);

            String b64Str = readAwsFileAsBase64(imgSrc, ConstantValUtil.BUCKET_PRIVATE);
//            String b64Str = Img2Base64Str(imgSrc, "读取缩略图,thumbnailFile=[{}]");

            PageData pdTmp = new PageData();
            pdTmp.put("imgName", pData.getString("imgName"));
            pdTmp.put("imgSerialNum", pData.getString("imgSerialNum"));
            pdTmp.put("graphingTime", pData.getString("graphingTime"));
            pdTmp.put("base64Str", "data:image/" + suffix + ";base64,"+ b64Str);

            listResp.add(i, pdTmp);
        }

        logUtil.saveLogData(result.getCode(), 1, "查询影像列表", pd);

        return ResponseEntity.success(PropertyUtil.covertListModel(listResp, PageData.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
    }

    /*
    * 用于患者影像图放大查看或对比图查看,每次请求一张影像
    * 将患者影像修改图（多个）的名称、序列号、时间以及内容（base4格式）等信息返回至前端
     */
    @PostMapping("/queryPhotoDetail")
    @ApiOperation(value = "查询影像列表", notes = "查询影像列表")
    public ResponseEntity<List<PageData>> queryProjectDetail(ImgSerialNumListDto imgSysSerialNumDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;

        CheckParameter.checkDefaultParams(pd);
        //校验取出参数
        String snList = pd.getString("serialNumList");
        if (StringUtils.isBlank(snList)) {
            throw new MyException("序列码serialNum不能为空");
        }

        // 1. 查询满足要求的患者影像修改图
        List<PageData> list = imgMainService.queryImgMainBySNList(pd);

        /* 2. 将每张影像修改图转为base64格式，放入影像信息尾部 */
        List<PageData> listResp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            PageData pgData = list.get(i);

            String imgSrc = pgData.getString("modifiedFile");
            String suffix = imgSrc.substring(imgSrc.lastIndexOf(".")+1);

            String b64Str = Img2Base64Str(imgSrc, "读取修改图,modifiedFile=[{}]");

            pgData.remove("originalFile");
            pgData.remove("modifiedFile");
            pgData.remove("thumbnailFile");
            pgData.put("base64Str", "data:image/" + suffix + ";base64,"+ b64Str);

            listResp.add(i, pgData);
        }

        logUtil.saveLogData(result.getCode(), 2, "查询影像放大图列表", pd);

        return ResponseEntity.success(PropertyUtil.covertListModel(listResp, PageData.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
    }


    @ApiOperation(value = "影像采集查询")
    @PostMapping("/imgCapturingQuery")
    public ResponseEntity imgCapturingQuery(ImgCapturingDto imgCapturingDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;

        boolean bThumbnailImg = false;
        if (StringUtils.equals(pd.getString("thumbnailImgFlag"), "1")) {
            bThumbnailImg = true;
        }

        /** 1. 查询受查询设备监控的影像采集设备 */
        List<PageData> graphingDeviceList = deviceInfoService.queryGraphingDeviceByIPC(pd);

        /** 2. 从数据库中查看符合要求的影像的文件路径 */
        List<PageData> imgList = new ArrayList<PageData>();
        for (PageData graphingDevicePD: graphingDeviceList) {
            String plcDeviceCode = graphingDevicePD.getString("plcDeviceCode");

            if (StringUtils.isNotBlank(plcDeviceCode)) {
                graphingDevicePD.put("graphingDeviceCode", plcDeviceCode);
                graphingDevicePD.put("graphingStartTime", pd.getString("graphingStartTime"));
                List<PageData> imgMainList = imgMainService.queryByDeviceCode(graphingDevicePD);

                if (CollectionUtils.isNotEmpty(imgMainList)) {
                    for (PageData imgMainPD: imgMainList) {
                        PageData imgFilePd = new PageData();
                        String fn = imgMainPD.getString("thumbnailFile");
                        String suffix = fn.substring(fn.lastIndexOf(".") + 1, fn.length());
                        imgFilePd.put("fileExt", suffix);
                        imgFilePd.put("imgName", imgMainPD.getString("imgName"));

                        imgFilePd.put("filePath", imgMainPD.getString("originalFile"));
                        if (bThumbnailImg) {
                            imgFilePd.put("filePath", imgMainPD.getString("thumbnailFile"));
                        }

                        imgList.add(imgFilePd);
                    }
                }
            }
        }

        /** 3. 从亚马逊的对象存储中获取所需文件url */
        AwsUtil.queryOneUrl(imgList, ConstantValUtil.BUCKET_PRIVATE);
//        for(PageData pageData : imgList) {
//            String b64Str = null;
//            b64Str = readAwsFileAsBase64(pageData.getString("filePath"), ConstantValUtil.BUCKET_PRIVATE);
//            pageData.put("base64Str", "data:image/" + pageData.getString("fileExt") + ";base64,"+ b64Str);
//        }

        result = ResponseEntity.success(PropertyUtil.covertListModel(imgList, ImgFileInfoDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        logUtil.saveLogData(result.getCode(), 1, "查询患者影像", pd);
        return result;
        //download
    }

    private String readAwsFileAsBase64(String object_keys, String bucketName) {


        AwsUtil aswUtil = new AwsUtil();

        String base64Str = aswUtil.downloadBase64(object_keys, bucketName);
        return base64Str;
    }


    private void readAwsFileAsStream(HttpServletResponse resp,
                                                    String object_keys, String bucketName) {


        return;
    }

    @ApiOperation(value = "影像放大图查询")
    @PostMapping("/imgZoomInQuery")
    public ResponseEntity imgZoomInQuery(ImgCapturingDto imgCapturingDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;


        HttpServletResponse resp = this.getResponse();

        AwsUtil aswUtil = new AwsUtil();
        //aswUtil.download(resp, object_keys, bucketName);


        return null;
    }

}
