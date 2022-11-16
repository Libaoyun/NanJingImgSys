package com.imgSys.controller;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.*;
import com.imgSys.dto.BodyPart;
import com.imgSys.dto.upload.BodyPartImgDto;
import com.imgSys.service.BodyPartService;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.file.AttachmentDto;

import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.common.util.ConstantMsgUtil.*;

/**
 * @description: 科室身体部位表相关
 * @author: Libaoyun
 * @date: 2022-10-17 18:58
 **/

@RestController
@RequestMapping("/body-part")
@Api(value = "科室身体部位信息管理", tags = "科室身体部位信息管理")
public class BodyPartController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BodyPart.class);

    @Autowired
    private LogUtil logUtil;

    @Autowired
    private BodyPartService bodyPartService;

    @ApiOperation("新增科室人体部位信息")
    @PostMapping()
    public ResponseEntity addBodyPart(BodyPart bodyPart){
        PageData pd = this.getParams();

        CheckParameter.stringLengthAndEmpty(pd.getString("parentSerialNum"), "父部位序列号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("partName"), "部位名称",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("partCode"), "部位编码",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("departmentCode"), "所属科室编码",128);

        ResponseEntity result = null;
        try {
            bodyPartService.addBodyPart(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            logger.error("新增部位信息失败,request=[{}]", pd);
            throw new MyException(ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "部位", pd);
        }
    }

    @ApiOperation(value = "对相应部位添加(上传)示意图")
    @PostMapping(value = "/upload")
    public ResponseEntity<AttachmentDto> upload(BodyPartImgDto bodyPartImgDto) {
        try {
            MultipartFile file = bodyPartImgDto.getFile();
            String fileName = file.getOriginalFilename();
            PageData pageData = AwsUtil.upload(file, ConstantValUtil.BODY_PART_IMAGE);
            pageData.put("fileName", fileName);
            pageData.put("partSerialNum", bodyPartImgDto.getPartSerialNum());
            bodyPartService.addBodyPartImg(pageData);
            return PropertyUtil.pushData(pageData, AttachmentDto.class,INFO_UPLOAD_SUCCESS.desc());
        } catch (MyException e) {
            ResponseEntity.failure(ERR_UPLOAD_FAIL.val(),ERR_UPLOAD_FAIL.desc());
            throw e;
        } catch (Exception e) {
            return ResponseEntity.failure(ERR_UPLOAD_FAIL.val(),ERR_UPLOAD_FAIL.desc());
        }
    }
    @ApiOperation(value = "删除某部位信息")
    @DeleteMapping()
    public ResponseEntity deleteBodyPart(BodyPart bodyPart) {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("partSerialNum"), "partSerialNum",128);
        ResponseEntity result = null;
        try {
            bodyPartService.deleteBodyPart(pd);
            result = ResponseEntity.success(null, INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            logger.error("删除部位信息失败,request=[{}]", pd);
            throw new MyException(ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "部位", pd);
        }
    }

    @ApiOperation(value = "编辑部位信息")
    @PostMapping(value = "/update",  consumes = "application/json")
    public ResponseEntity updateBodyPart(BodyPart bodyPart) {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("partSerialNum"), "部位序列号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("partName"), "部位名称",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("partCode"), "部位编码",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("departmentCode"), "所属科室编码",128);
        ResponseEntity result = null;
        try {
            bodyPartService.updateBodyPart(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            logger.error("编辑部位失败,request=[{}]", pd);
            throw new MyException(ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "部位", pd);
        }
    }

    @GetMapping()
    @ApiOperation(value = "树形查询所有部位信息")
//    @ApiImplicitParam(name = "orgId", value = "部门ID", required = true, dataType = "String")
    public ResponseEntity<List<BodyPart>> getAllBodyPart() {
        PageData pd = this.getParams();
        try {
            List<PageData> dataList = bodyPartService.getAllBodyPart("0", pd);
            return ResponseEntity.success(PropertyUtil.covertListModel(dataList, BodyPart.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询所有部位信息,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }

    @GetMapping("/standard")
    @ApiOperation(value = "查询所有标准部位及图片信息（Base64）")
//    @ApiImplicitParam(name = "orgId", value = "部门ID", required = true, dataType = "String")
    public ResponseEntity<List<BodyPart>> getStandardBodyPart() {
        PageData pd = this.getParams();
        try {
            List<PageData> dataList = bodyPartService.getStandardBodyPart(pd);
            return ResponseEntity.success(PropertyUtil.covertListModel(dataList, BodyPart.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询所有部位信息,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }

    // FIXME 这里可能暂时用不到，因为部位图都是缩略图，无需放大
    @GetMapping("/partImg")
    @ApiOperation(value = "查询某部位完整图片（二进制流）")
//    @ApiImplicitParam(name = "orgId", value = "部门ID", required = true, dataType = "String")
    public void getOneBodyPartImg(HttpServletResponse resp, BodyPart bodyPart , HttpServletRequest request) {
        PageData pd = this.getParams();
        try {
            AwsUtil.download(resp, pd.getString("partSketchFile"), ConstantValUtil.BODY_PART_IMAGE);
            resp.setContentType(request.getContentType());

//            bodyPartService.getOneBodyPartImg(resp);
//            return ResponseEntity.success(null, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询某部位信息,request=[{}]", pd);
            resp.setContentType(request.getContentType());
//            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
//        return null;
    }
}
