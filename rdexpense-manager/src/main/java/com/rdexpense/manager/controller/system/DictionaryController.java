package com.rdexpense.manager.controller.system;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.PropertyUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.system.dictionary.*;
import com.rdexpense.manager.service.system.DictionaryService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.common.util.ConstantMsgUtil.ERR_QUERY_FAIL;


/**
 * @Description: 数据字典
 * @Author: rdexpense
 * @Date: 2020/12/30 09:32
 * @Version: 1.0
 */
@RestController
@RequestMapping(value = "/dataDictionary")
@Api(value = "数据字典", tags = "数据字典")
public class DictionaryController extends BaseController {

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private LogUtil logUtil;

    private final static Logger logger = LoggerFactory.getLogger(DictionaryController.class);

    @ApiOperation(value="获取字典树状数据")
    @GetMapping(value = "/queryDictionaryTree")
    public ResponseEntity<List<DictionaryDto>> findAllDictionaryInfo() {
        PageData pd = this.getParams();
        try {
            List<PageData> list = dictionaryService.findDictionaryTreeInfo();
            return ResponseEntity.success(PropertyUtil.covertListModel(list, DictionaryDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询,request=[{}]", pd);
            throw new MyException(ConstantMsgUtil.ERR_QUERY_FAIL.desc(), e);

        }
    }


    @ApiOperation(value="查询字典枚举值列表")
    @PostMapping(value = "/queryDictionaryList")
    public ResponseEntity<PageInfo<DictionaryDto>> findDictionariesList(QueryDictionaryDto queryDictionaryDto) {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("dicTypeId"), "字典类别ID",16);
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = dictionaryService.findDictionariesList(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, DictionaryDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询字典枚举值列表,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }


    @ApiOperation(value = "新增字典枚举值")
    @PostMapping("/saveDictionary")
    public ResponseEntity createDictionaryInfo(CreateDictionaryDto createUqDictionaryDto) {

        PageData pd = this.getParams();
        checkParams(pd);
        CheckParameter.checkDefaultParams(pd);
        ResponseEntity result = null;
        try {

            dictionaryService.createDictionaryInfo(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("新增字典枚举值,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "字典枚举值", pd);
        }
    }


    @ApiOperation(value = "更新字典枚举值")
    @PostMapping("/updateDictionary")
    public ResponseEntity updateDictionaryInfo(UpdateDictionaryDto updateDictionaryDto) {
        PageData pd = this.getParams();
        checkParams(pd);
        CheckParameter.checkDefaultParams(pd);
        ResponseEntity result = null;
        try {
            dictionaryService.updateDictionaryInfo(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("更新字典枚举值,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "字典枚举值", pd);
        }
    }



    @ApiOperation(value = "删除字典枚举值")
    @PostMapping("/deleteDictionary")
    public ResponseEntity deleteDictionaryInfo(DeleteDictionaryDto deleteUqDictionaryDto) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        ResponseEntity result = null;
        try {
            //校验取出参数
            String businessIdList = pd.getString("ids");
            if (StringUtils.isBlank(businessIdList)) {
                throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
            }

            dictionaryService.deleteDictionaryInfo(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("删除字典枚举值,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "字典枚举值", pd);
        }
    }

    @ApiOperation(value="获取下拉字典列表")
    @PostMapping(value = "/queryPullDownDictionariesList")
    ResponseEntity<List<DictionaryQueryDto>> findPullDownDictionariesList(QueryUqDictionaryListDto queryUqDictionaryListDto){
        PageData pd = this.getParams();
        try{
            //校验取出参数
            String dicTypeIds = pd.getString("dicTypeIds");
            if (StringUtils.isBlank(dicTypeIds)) {
                throw new MyException("字典类型不能不为空");
            }

            List<PageData> list = dictionaryService.findPullDownDictionariesList(pd);

            return ResponseEntity.success(PropertyUtil.covertListModel(list, DictionaryQueryDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("获取下拉字典列表,request=[{}]", pd);
            throw new MyException(ERR_QUERY_FAIL.desc(), e);
        }


    }

    /**
     -------------------------------------------------------------------------------------------------
     * 以下是对  数据字典类别 进行操作
     */
    @ApiOperation(value = "查询字典类型数据")
    @GetMapping("/queryDictionaryType")
    ResponseEntity<List<DictionaryTypeListDto>> queryDictionaryType(){
        PageData pd = this.getParams();
        try{

            List<PageData> list = dictionaryService.queryDictionaryType();

            return ResponseEntity.success(PropertyUtil.covertListModel(list, DictionaryTypeListDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询字典类型数据,request=[{}]", pd);
            throw new MyException(ERR_QUERY_FAIL.desc(), e);
        }


    }



    @ApiOperation(value = "新增字典类型")
    @PostMapping("/saveDictionType")
    public ResponseEntity saveDictionType(DictionaryTypeDto dictionaryTypeDto) {
        PageData pd = this.getParams();
//        checkParams(pd);
        CheckParameter.checkDefaultParams(pd);
        ResponseEntity result = null;
        try {

            dictionaryService.saveDictionType(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("新增字典类型,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "字典类型", pd);
        }
    }




    @ApiOperation(value = "修改字典类型")
    @PostMapping("/modifyDictionType")
    public ResponseEntity modifyDictionType(DictionaryTypeDto dictionaryTypeDto) {
        PageData pd = this.getParams();
 //       checkParams(pd);
        CheckParameter.checkDefaultParams(pd);
        ResponseEntity result = null;
        try {
            dictionaryService.modifyDictionType(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("修改字典类型,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "字典类型", pd);
        }
    }



    @ApiOperation(value = "删除字典类型")
    @PostMapping("/removeDictionType")
    public ResponseEntity removeDictionType(DeleteDictionaryDto deleteDictionaryDto) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        ResponseEntity result = null;
        try {
            //校验取出参数
            String businessIdList = pd.getString("ids");
            if (StringUtils.isBlank(businessIdList)) {
                throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
            }

            dictionaryService.removeDictionType(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("删除字典类型,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "字典类型", pd);
        }
    }


    private void checkParams(PageData pd){

        CheckParameter.checkDefaultParams(pd);

        String  dicTypeId = pd.getString("dicTypeId");
        CheckParameter.stringLengthAndEmpty(dicTypeId, "字典类型ID",16);

        String  dicEnumName = pd.getString("dicEnumName");
        CheckParameter.stringLengthAndEmpty(dicEnumName, "枚举值",64);

        String  dicEnumId = pd.getString("dicEnumId");
        CheckParameter.stringLengthAndEmpty(dicEnumId, "枚举值编码",16);

        String  remark = pd.getString("remark");
        CheckParameter.stringLengthAndEmpty(remark, "描述",1024);

        String  isValid = pd.getString("isValid");
        CheckParameter.stringLengthAndEmpty(isValid, "是否启用",8);

    }



}
