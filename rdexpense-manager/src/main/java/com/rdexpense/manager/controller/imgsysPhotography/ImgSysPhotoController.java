package com.rdexpense.manager.controller.imgsysPhotography;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.PropertyUtil;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.controller.projContract.ProjContractSignController;
import com.rdexpense.manager.dto.imgsysPhotography.ImgSysPhotoAddDto;
import com.rdexpense.manager.dto.imgsysPhotography.ImgSysPhotoSearchDto;
import com.rdexpense.manager.dto.imgsysPhotography.ImgSysSerialNumListDto;
import com.rdexpense.manager.service.imgsysPhotography.ImgSysPhotoService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ImgSysPhotography")
@Api(value = "影像信息", tags = "影像信息")
public class ImgSysPhotoController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ProjContractSignController.class);

    @Autowired
    private ImgSysPhotoService imgSysPhotoService;

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

    /*
    * 将患者影像缩略图（多个）的名称、序列号、时间以及内容（base4格式）返回至前端
     */
    @PostMapping("/queryPhotoList")
    @ApiOperation(value = "查询影像列表", notes = "查询影像列表")
    public ResponseEntity<List<PageData>> queryProjectList(ImgSysPhotoSearchDto imgSysPhotoSearchDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;

        // 1. 查询满足要求的患者影像
        List<PageData> list = imgSysPhotoService.queryImgMain(pd);

        /* 2. 将每张影像缩略图转为base64格式，放入影像信息尾部 */
        List<PageData> listResp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            PageData pData = list.get(i);

            //绝对路径
            String imgSrc = pData.getString("thumbnailFile");
            String suffix = imgSrc.substring(imgSrc.lastIndexOf(".")+1);

            String b64Str = Img2Base64Str(imgSrc, "读取缩略图,thumbnailFile=[{}]");

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
    * 用于患者影像图放大查看或对比图查看
    * 将患者影像修改图（多个）的名称、序列号、时间以及内容（base4格式）等信息返回至前端
     */
    @PostMapping("/queryPhotoDetail")
    @ApiOperation(value = "查询影像列表", notes = "查询影像列表")
    public ResponseEntity<List<PageData>> queryProjectDetail(ImgSysSerialNumListDto imgSysSerialNumDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;

        CheckParameter.checkDefaultParams(pd);
        //校验取出参数
        String snList = pd.getString("serialNumList");
        if (StringUtils.isBlank(snList)) {
            throw new MyException("序列码serialNum不能为空");
        }

        // 1. 查询满足要求的患者影像修改图
        List<PageData> list = imgSysPhotoService.queryImgMainBySNList(pd);

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

    /*
    *
     */
    @ApiOperation(value = "上传患者影像")
    @PostMapping("/uploadPatientImg")
    public ResponseEntity uploadPatientImg(ImgSysPhotoAddDto imgSysPhotoAddDto) {
        PageData pd = this.getParams();
        //checkParam(pd);
        ResponseEntity result = null;



        logUtil.saveLogData(result.getCode(), 3, "上传患者影像", pd);
        return null;
    }


}
