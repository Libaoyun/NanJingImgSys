package com.rdexpense.manager.controller.flow;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.PropertyUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.flow.*;
import com.rdexpense.manager.dto.system.permission.UserAuthMenuListDto;
import com.rdexpense.manager.dto.system.user.IdListDto;
import com.rdexpense.manager.dto.system.user.UserListDTO;
import com.rdexpense.manager.service.flow.FlowService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.common.util.ConstantMsgUtil.*;

/**
 * @author luxiangbao
 * @date 2021/11/12 11:44
 * @description 流程管理
 */
@RestController
@RequestMapping("/flow")
@Api(value = "流程管理", tags = "流程管理")
public class FlowController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(FlowController.class);

    @Autowired
    private LogUtil logUtil;

    @Autowired
    private FlowService flowService;


    @ApiOperation(value = "新增")
    @PostMapping(value = "/addFlow")
    public ResponseEntity addFlow(FlowAddDTO flowAddDTO) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("menuId"), "菜单ID",128);
        CheckParameter.stringIsNull(pd.getString("flowContent"), "流程内容");

        ResponseEntity result = null;
        try {
            String id = flowService.addFlow(pd);
            result = ResponseEntity.success(id, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            logger.error("新增流程配置失败,request=[{}]", pd);
            throw new MyException(ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "流程配置", pd);
        }
    }


    @ApiOperation(value = "编辑")
    @PostMapping(value = "/updateFlow")
    public ResponseEntity updateFlow(FlowUpdateDTO flowUpdateDTO) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("id"), "主键ID",128);
        CheckParameter.stringIsNull(pd.getString("flowContent"), "流程内容");

        ResponseEntity result = null;
        try {
            String id = flowService.updateFlow(pd);
            result = ResponseEntity.success(id, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            logger.error("编辑流程配置失败,request=[{}]", pd);
            throw new MyException(ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "流程配置", pd);
        }
    }



    @ApiOperation(value = "删除")
    @PostMapping(value = "/deleteFlow")
    public ResponseEntity deleteFlow(IdListDto idListDto) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        //校验取出参数
        String idList = pd.getString("idList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }
        ResponseEntity result = null;
        try {
            flowService.deleteFlow(pd);
            result = ResponseEntity.success(null, INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            logger.error("删除流程配置失败,request=[{}]", pd);
            throw new MyException(ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "流程配置", pd);
        }
    }


    @PostMapping("/getFlow")
    @ApiOperation(value = "查看流程配置")
    @ApiImplicitParam(name = "menuCode", value = "菜单编码", required = true, dataType = "String")
    public ResponseEntity getFlow() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("menuCode"), "菜单编码",128);
        try {
            PageData pageData = flowService.getFlow(pd);
            return ResponseEntity.success(pageData);
        } catch (MyException e) {
            logger.error("查看流程配置失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }


    @PostMapping(value = "/queryFlowUser")
    @ApiOperation(value = "查询审批人")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "页码,值需大于等于1", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量,值需大于等于1", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "orgId", value = "部门或职务ID", required = true, dataType = "String")})
    public ResponseEntity<PageInfo<UserListDTO>> queryFlowUser() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("orgId"), "部门或职务ID", 64);
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = flowService.queryFlowUser(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, UserListDTO.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());

        } catch (Exception e) {
            logger.error("查询审批人,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());

        }

    }



    @ApiOperation(value = "查询待办")
    @PostMapping(value = "/queryWaitDone")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "页码,值需大于等于1", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量,值需大于等于1", required = true, dataType = "Integer")})
    public ResponseEntity<PageInfo<ApproveDoneListDTO>> queryWaitDone() {
        PageData pd = this.getParams();
        try {

            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = flowService.queryWaitDone(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, ApproveDoneListDTO.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询待办失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }


    @ApiOperation(value = "查询已办")
    @PostMapping(value = "/queryIsDone")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "页码,值需大于等于1", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量,值需大于等于1", required = true, dataType = "Integer")})
    public ResponseEntity<PageInfo<ApproveDoneListDTO>> queryIsDone( ) {
        PageData pd = this.getParams();
        try {

            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = flowService.queryIsDone(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, ApproveDoneListDTO.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询已办失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }


    @PostMapping("/getSerialFlow")
    @ApiOperation(value = "查看每个单据的流程")
    @ApiImplicitParam(name = "serialNumber", value = "单据号", required = true, dataType = "String")
    public ResponseEntity<FlowSerialDataDTO> getSerialFlow() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("serialNumber"), "单据号",128);
        try {
            PageData pageData = flowService.getSerialFlow(pd);
            return PropertyUtil.pushData(pageData, FlowSerialDataDTO.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查看流程配置失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }



    @ApiOperation(value = "启动流程")
    @PostMapping(value = "/start")
    public ResponseEntity test() {
        PageData pd = this.getParams();
        try {
            flowService.startFlow(pd);
            return ResponseEntity.success("成功");
        } catch (Exception e) {
            throw new MyException("启动流程失败:"+e.getMessage());
        }
    }

    @ApiOperation(value = "审批流程")
    @PostMapping(value = "/approve")
    public ResponseEntity approve() {
        PageData pd = this.getParams();
        try {
            flowService.approveFlow(pd);
            return ResponseEntity.success("成功");
        } catch (Exception e) {
            throw new MyException("审批流程失败:"+e.getMessage());
        }
    }

    @ApiOperation(value = "退回上一个节点")
    @PostMapping(value = "/backPreviousNode")
    public ResponseEntity backPreviousNode() {
        PageData pd = this.getParams();
        try {
            flowService.backPreviousNode(pd);
            return ResponseEntity.success("成功");
        } catch (Exception e) {
            throw new MyException("退回上一个节点失败:"+e.getMessage());
        }
    }

    @ApiOperation(value = "退回发起人")
    @PostMapping(value = "/backOriginalNode")
    public ResponseEntity backOriginalNode() {
        PageData pd = this.getParams();
        try {
            flowService.backOriginalNode(pd);
            return ResponseEntity.success("成功");
        } catch (Exception e) {
            throw new MyException("退回发起人失败:"+e.getMessage());
        }
    }
}
