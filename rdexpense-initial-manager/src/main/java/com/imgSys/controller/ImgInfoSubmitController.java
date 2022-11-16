package com.imgSys.controller;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.ConstantMsgUtil;
import com.imgSys.dto.img.ImgInfoAppSubmitDto;
import com.imgSys.service.ImgInfoSubmitService;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.controller.projContract.ProjContractSignController;


import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/imgFileInfoSubmit")
@Api(value = "信息App提交", tags = "影像信息")
public class ImgInfoSubmitController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ProjContractSignController.class);

    @Autowired
    private ImgInfoSubmitService imgInfoSubmitService;

    @Autowired
    private LogUtil logUtil;

    /**
     *
     * @param imgInfoAppSubmitDto
     * @return
     */
    @ApiOperation(value = "App提交患者影像信息")
    @GetMapping("/appSubmitRealTime")
    public ResponseEntity realTimeImgInfoAppSubmit(ImgInfoAppSubmitDto imgInfoAppSubmitDto) {
        PageData pd = this.getPageData();
        //checkParam(pd);
        ResponseEntity result = null;

        try {
            imgInfoSubmitService.realtimeInfoAppSubmit(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
        } catch(Exception e) {
            logger.error("App提交患者影像信息失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SUBMIT_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SUBMIT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "影像信息App提交", pd);
        }

//        logUtil.saveLogData(result.getCode(), 3, "上传患者影像", pd);
        return result;
    }




}
