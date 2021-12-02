package com.rdexpense.manager.controller.itemClosureCheck;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.PropertyUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.itemClosureCheck.ItemClosureCheckDetailDto;
import com.rdexpense.manager.dto.itemClosureCheck.ItemClosureCheckListDto;
import com.rdexpense.manager.dto.projectApply.ProjectApplyListDto;
import com.rdexpense.manager.dto.projectApply.ProjectApplySearchDto;
import com.rdexpense.manager.dto.system.user.IdListDto;
import com.rdexpense.manager.service.itemClosureCheck.ItemClosureCheckService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.common.util.ConstantMsgUtil.ERR_QUERY_FAIL;

@RestController
@RequestMapping("/itemClosureCheck")
@Api(value = "研发项目结项验收", tags = "研发项目结项验收")
public class ItemClosureCheckController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ItemClosureCheckController.class);

    @Autowired
    private ItemClosureCheckService itemClosureCheckService;

    @Autowired
    private LogUtil logUtil;

    @PostMapping("/queryItemClosureCheckList")
    @ApiOperation(value = "查询研发项目结题验收列表", notes = "查询研发项目结题验收列表")
    public ResponseEntity<PageInfo<ItemClosureCheckListDto>> queryUserList(ItemClosureCheckListDto itemClosureCheckListDto) {
        PageData pd = this.getParams();
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = itemClosureCheckService.queryItemClosureCheckList(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, ItemClosureCheckListDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询研发项目结题验收列表,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }

    @PostMapping("/queryApplyList")
    @ApiOperation(value = "查询已审批完成的研发项目申请列表", notes = "查询已审批完成的研发项目申请列表")
    public ResponseEntity<PageInfo<ProjectApplyListDto>> queryApplyList(ProjectApplySearchDto projectApplySearchDto) {
        PageData pd = this.getParams();
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = itemClosureCheckService.queryProjectApplyList(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, ProjectApplyListDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询项目立项申请列表,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }

    @ApiOperation(value = "删除研发项目结题验收申请")
    @PostMapping("/delete")
    public ResponseEntity deleteApply(IdListDto idListDto) {
        PageData pd = this.getParams();

        //校验取出参数
        String idList = pd.getString("idList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException("主键ID不能为空");
        }
        CheckParameter.checkDefaultParams(pd);

        ResponseEntity result = null;
        try {
//            projectApplyService.deleteApply(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("删除研发项目结题验收申请失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "研发项目结题验收申请", pd);
        }
    }

    @PostMapping("/queryDetail")
    @ApiOperation(value = "查询研发项目结题验收申请详情", notes = "查询研发项目结题验收申请详情")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "String")
    public ResponseEntity<ItemClosureCheckDetailDto> queryDetail() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("id"), "主键ID",64);
        try {
            PageData pageData = null;
//            PageData pageData = projectApplyService.getApplyDetail(pd);
            return PropertyUtil.pushData(pageData, ItemClosureCheckDetailDto.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询项目立项申请详情失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }
}
