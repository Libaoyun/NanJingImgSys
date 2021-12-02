package com.rdexpense.manager.service.projectApply.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.*;
import com.rdexpense.manager.service.file.FileService;
import com.rdexpense.manager.service.flow.FlowService;
import com.rdexpense.manager.service.projectApply.ProjectApplyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:11
 */
@Slf4j
@Service
public class ProjectApplyServiceImpl implements ProjectApplyService {


    @Autowired
    @Resource(name = "baseDao")
    private BaseDao dao;

    @Autowired
    private FileService fileService;

    @Autowired
    @Resource(name = "FlowService")
    private FlowService flowService;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 查询列表
     *
     * @param
     * @return
     */
    @Override
    public List queryList(PageData pd) {

        //查询数据信息
        List<String> pageDataList = JSONArray.parseArray(pd.getString("statusList"), String.class);
        pd.put("processStatusList", pageDataList);
        pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[0]);

        List<String> projectTypeCodeList = JSONArray.parseArray(pd.getString("projectTypeCode"), String.class);
        pd.put("projectTypeCodeList", projectTypeCodeList);

        List<String> professionalCategoryCodeList = JSONArray.parseArray(pd.getString("professionalCategoryCode"), String.class);
        pd.put("professionalCategoryCodeList", professionalCategoryCodeList);

        List<PageData> list = (List<PageData>) dao.findForList("ProjectApplyMapper.queryByParams", pd);

        return list;
    }


    /**
     * 新增立项申请
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void addApply(PageData pd) {

        String businessId = "RDPI" + UUID.randomUUID().toString();//生成业务主键ID
        String serialNumber = SequenceUtil.generateSerialNo("RDPI");//生成流水号

        pd.put("businessId", businessId);
        pd.put("serialNumber", serialNumber);
        pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[0]);


        //判断是否是提交 1:保存 2:提交
        String operationType = pd.getString("operationType");
        if (operationType.equals("2")) {
            flowService.startFlow(pd);
        }


        //1、插入主表
        dao.insert("ProjectApplyMapper.insertMain", pd);

        //2、插入立项调研信息
        dao.insert("ProjectApplyMapper.insertSurvey", pd);

        //3、插入进度计划
        String progressPlan = pd.getString("progressPlan");
        List<PageData> progressPlanList = JSONObject.parseArray(progressPlan, PageData.class);
        if (!CollectionUtils.isEmpty(progressPlanList)) {
            for (PageData detailData : progressPlanList) {
                detailData.put("businessId", businessId);
            }

            dao.batchInsert("ProjectApplyMapper.batchInsertProgressPlan", progressPlanList);
        }

        //4、插入参与单位
        String attendUnit = pd.getString("attendUnit");
        List<PageData> attendUnitList = JSONObject.parseArray(attendUnit, PageData.class);
        if (!CollectionUtils.isEmpty(attendUnitList)) {
            for (PageData detailData : attendUnitList) {
                detailData.put("businessId", businessId);
            }

            dao.batchInsert("ProjectApplyMapper.batchInsertAttendUnit", attendUnitList);
        }

        //5、插入研究人员
        String researchUser = pd.getString("researchUser");
        List<PageData> researchUserList = JSONObject.parseArray(researchUser, PageData.class);
        if (!CollectionUtils.isEmpty(researchUserList)) {
            for (PageData detailData : researchUserList) {
                detailData.put("businessId", businessId);
            }

            dao.batchInsert("ProjectApplyMapper.batchInsertResearchUser", researchUserList);
        }

        //6、经费来源预算
        String budgetSource = pd.getString("budgetSource");
        List<PageData> budgetSourceList = JSONObject.parseArray(budgetSource, PageData.class);
        if (!CollectionUtils.isEmpty(budgetSourceList)) {
            for (PageData detailData : budgetSourceList) {
                detailData.put("businessId", businessId);
                detailData.put("status", 1);

                dao.insert("ProjectApplyMapper.insertBudgetSource", detailData);
            }


        }

        //7、经费支出预算
        String budgetExpenses = pd.getString("budgetExpenses");
        List<PageData> budgetExpensesList = JSONObject.parseArray(budgetExpenses, PageData.class);
        if (!CollectionUtils.isEmpty(budgetExpensesList)) {
            for (PageData detailData : budgetExpensesList) {
                detailData.put("businessId", businessId);
                detailData.put("status", 1);

                dao.insert("ProjectApplyMapper.insertBudgetExpenses", detailData);
            }

        }

        pd.put("status", 3);
        //8、插入经费来源每月预算
        dao.insert("ProjectApplyMapper.insertBudgetSource", pd);

        //9、插入经费支出每月预算
        dao.insert("ProjectApplyMapper.insertBudgetExpenses", pd);

        //10、插入年度预算（按月填报）
        String monthListStr = pd.getString("monthList");
        List<PageData> monthList = JSONObject.parseArray(monthListStr, PageData.class);
        if (!CollectionUtils.isEmpty(monthList)) {
            for (PageData detailData : monthList) {
                detailData.put("businessId", businessId);
            }

            dao.batchInsert("ProjectApplyMapper.batchInsertMonthList", monthList);
        }

        //11、插入拨款计划
        String appropriationPlan = pd.getString("appropriationPlan");
        List<PageData> appropriationPlanList = JSONObject.parseArray(appropriationPlan, PageData.class);
        if (!CollectionUtils.isEmpty(appropriationPlanList)) {
            for (PageData detailData : appropriationPlanList) {
                detailData.put("businessId", businessId);
            }

            dao.batchInsert("ProjectApplyMapper.batchAppropriationPlan", appropriationPlanList);
        }

        // 插入到附件表
        fileService.insert(pd);

    }


    /**
     * 更新立项申请
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void updateApply(PageData pd) {

        //判断是否是提交 1:保存 2:提交
        String businessId = pd.getString("businessId");
        List<String> removeList = new ArrayList<>();
        removeList.add(businessId);

        String operationType = pd.getString("operationType");
        if (operationType.equals("2")) {
            flowService.startFlow(pd);
        }

        //1、编辑主表
        dao.update("ProjectApplyMapper.updateMain", pd);

        //2、编辑立项调研信息
        dao.update("ProjectApplyMapper.updateSurvey", pd);

        //3、插入进度计划
        String progressPlan = pd.getString("progressPlan");
        List<PageData> progressPlanList = JSONObject.parseArray(progressPlan, PageData.class);
        if (!CollectionUtils.isEmpty(progressPlanList)) {
            for (PageData detailData : progressPlanList) {
                detailData.put("businessId", businessId);
            }

            dao.batchDelete("ProjectApplyMapper.deleteProgressPlan", removeList);
            dao.batchInsert("ProjectApplyMapper.batchInsertProgressPlan", progressPlanList);
        }

        //4、插入参与单位
        String attendUnit = pd.getString("attendUnit");
        List<PageData> attendUnitList = JSONObject.parseArray(attendUnit, PageData.class);
        if (!CollectionUtils.isEmpty(attendUnitList)) {
            for (PageData detailData : attendUnitList) {
                detailData.put("businessId", businessId);
            }

            dao.batchDelete("ProjectApplyMapper.deleteAttendUnit", removeList);
            dao.batchInsert("ProjectApplyMapper.batchInsertAttendUnit", attendUnitList);
        }

        //5、插入研究人员
        String researchUser = pd.getString("researchUser");
        List<PageData> researchUserList = JSONObject.parseArray(researchUser, PageData.class);
        if (!CollectionUtils.isEmpty(researchUserList)) {
            for (PageData detailData : researchUserList) {
                detailData.put("businessId", businessId);
            }

            dao.batchDelete("ProjectApplyMapper.deleteResearchUser", removeList);
            dao.batchInsert("ProjectApplyMapper.batchInsertResearchUser", researchUserList);
        }

        //6、经费来源预算
        PageData deleteData = new PageData();
        deleteData.put("removeList", removeList);
        deleteData.put("status", 1);

        String budgetSource = pd.getString("budgetSource");
        List<PageData> budgetSourceList = JSONObject.parseArray(budgetSource, PageData.class);
        if (!CollectionUtils.isEmpty(budgetSourceList)) {

            dao.delete("ProjectApplyMapper.deleteBudgetSource", deleteData);
            for (PageData detailData : budgetSourceList) {
                detailData.put("businessId", businessId);
                detailData.put("status", 1);

                dao.insert("ProjectApplyMapper.insertBudgetSource", detailData);
            }


        }

        //7、经费支出预算
        String budgetExpenses = pd.getString("budgetExpenses");
        List<PageData> budgetExpensesList = JSONObject.parseArray(budgetExpenses, PageData.class);
        if (!CollectionUtils.isEmpty(budgetExpensesList)) {

            dao.delete("ProjectApplyMapper.deleteBudgetExpenses", deleteData);
            for (PageData detailData : budgetExpensesList) {
                detailData.put("businessId", businessId);
                detailData.put("status", 1);

                dao.insert("ProjectApplyMapper.insertBudgetExpenses", detailData);
            }


        }


        //8、编辑经费来源每月预算
        pd.put("status", 3);
        dao.update("ProjectApplyMapper.updateBudgetSource", pd);

        //9、编辑经费支出每月预算
        dao.update("ProjectApplyMapper.updateBudgetExpenses", pd);

        //10、插入年度预算（按月填报）
        String monthListStr = pd.getString("monthList");
        List<PageData> monthList = JSONObject.parseArray(monthListStr, PageData.class);
        if (!CollectionUtils.isEmpty(monthList)) {
            for (PageData detailData : monthList) {
                detailData.put("businessId", businessId);
            }

            dao.batchDelete("ProjectApplyMapper.deleteMonthList", removeList);
            dao.batchInsert("ProjectApplyMapper.batchInsertMonthList", monthList);
        }

        //11、插入拨款计划
        String appropriationPlan = pd.getString("appropriationPlan");
        List<PageData> appropriationPlanList = JSONObject.parseArray(appropriationPlan, PageData.class);
        if (!CollectionUtils.isEmpty(appropriationPlanList)) {
            for (PageData detailData : appropriationPlanList) {
                detailData.put("businessId", businessId);
            }

            dao.batchDelete("ProjectApplyMapper.deleteAppropriationPlan", removeList);
            dao.batchInsert("ProjectApplyMapper.batchAppropriationPlan", appropriationPlanList);
        }


        //编辑附件表
        fileService.update(pd);

    }

    /**
     * 删除立项申请
     *
     * @param
     * @return
     */
    @Override
    @Transactional
    public void deleteApply(PageData pd) {

        //校验不通过的部分
        List<String> removeList = new ArrayList<>();
        String businessIdListStr = pd.getString("businessIdList");

        List<String> businessIdList = JSONObject.parseArray(businessIdListStr, String.class);
        //根据businessIdList查询主表
        List<PageData> recordList = (List<PageData>) dao.findForList("ProjectApplyMapper.selectByBusinessId", businessIdList);
        if (!CollectionUtils.isEmpty(recordList)) {
            for (PageData data : recordList) {
                String requestStatus = data.getString("processStatus");
                if (requestStatus.equals(ConstantValUtil.APPROVAL_STATUS[0])) {
                    removeList.add(data.getString("businessId"));
                }
            }
        }


        if (removeList.size() != businessIdList.size()) {
            throw new MyException("只有未提交的单据才能删除");
        }

        pd.put("removeList", removeList);

        //1、删除主表
        dao.batchDelete("ProjectApplyMapper.deleteMain", removeList);

        //2、删除立项调研信息
        dao.batchDelete("ProjectApplyMapper.deleteSurvey", removeList);

        //3、删除进度计划
        dao.batchDelete("ProjectApplyMapper.deleteProgressPlan", removeList);

        //4、删除参与单位
        dao.batchDelete("ProjectApplyMapper.deleteAttendUnit", removeList);

        //5、删除研究人员
        dao.batchDelete("ProjectApplyMapper.deleteResearchUser", removeList);

        //6、删除来源预算
        dao.delete("ProjectApplyMapper.deleteBudgetSource", pd);

        //7、删除经费支出预算
        dao.delete("ProjectApplyMapper.deleteBudgetExpenses", pd);

        //8、删除年度预算（按月填报）
        dao.batchDelete("ProjectApplyMapper.deleteMonthList", removeList);

        //9、删除拨款计划
        dao.batchDelete("ProjectApplyMapper.deleteAppropriationPlan", removeList);

        //删除文件
        dao.batchDelete("ProjectApplyMapper.batchDeleteFile", removeList);

    }

    /**
     * 查询立项申请详情
     *
     * @param pd
     * @return
     */
    @Override
    public PageData getApplyDetail(PageData pd) {

        //1、查询主表
        PageData request = (PageData) dao.findForObject("ProjectApplyMapper.queryApplyDetail", pd);
        if (request != null) {
            // 2、进度计划
            List<PageData> progressPlan = (List<PageData>) dao.findForList("ProjectApplyMapper.queryProgressPlan", request);
            if (!CollectionUtils.isEmpty(progressPlan)) {
                request.put("progressPlan", progressPlan);
            }

            // 3、参加单位
            List<PageData> attendUnit = (List<PageData>) dao.findForList("ProjectApplyMapper.queryAttendUnit", request);
            if (!CollectionUtils.isEmpty(attendUnit)) {
                request.put("attendUnit", attendUnit);
            }

            // 4、研究人员（初始）
            List<PageData> researchUser = (List<PageData>) dao.findForList("ProjectApplyMapper.queryResearchUser", request);
            if (!CollectionUtils.isEmpty(researchUser)) {
                request.put("researchUser", researchUser);
            }

            // 5、研究人员（变更）
            List<PageData> researchUserChange = (List<PageData>) dao.findForList("ProjectApplyMapper.queryRsearchUserChange", request);
            if (!CollectionUtils.isEmpty(researchUserChange)) {
                request.put("researchUserChange", researchUserChange);
            }

            // 6、经费来源预算
            List<PageData> budgetSource = (List<PageData>) dao.findForList("ProjectApplyMapper.queryBudgetSource", request);
            if (!CollectionUtils.isEmpty(budgetSource)) {
                request.put("budgetSource", budgetSource);
            }

            // 7、经费支出预算
            List<PageData> budgetExpenses = (List<PageData>) dao.findForList("ProjectApplyMapper.queryBudgetExpenses", request);
            if (!CollectionUtils.isEmpty(budgetExpenses)) {
                request.put("budgetExpenses", budgetExpenses);
            }

            // 8、年度预算（按月填报
            List<PageData> monthList = (List<PageData>) dao.findForList("ProjectApplyMapper.queryMonthList", request);
            if (!CollectionUtils.isEmpty(monthList)) {
                request.put("monthList", monthList);
            }

            // 9、拨款计划
            List<PageData> appropriationPlan = (List<PageData>) dao.findForList("ProjectApplyMapper.queryAppropriationPlan", request);
            if (!CollectionUtils.isEmpty(appropriationPlan)) {
                request.put("appropriationPlan", appropriationPlan);
            }


            // 查询附件表
            fileService.queryFileByBusinessId(request);

        }


        return request;
    }


    /**
     * 审批流程
     *
     * @param pd
     */
    @Override
    public void approveRecord(PageData pd) {


        //插入附件表，并返回主键id的拼接字符串
        String fileIdStr = fileService.insertApproveFile(pd);
        pd.put("fileId", fileIdStr);

        //审批类型 1:同意 2:回退上一个节点 3：回退到发起人
        String approveType = pd.getString("approveType");
        if (approveType.equals("1")) {
            flowService.approveFlow(pd);

        } else if (approveType.equals("2")) {
            flowService.backPreviousNode(pd);

        } else if (approveType.equals("3")) {
            flowService.backOriginalNode(pd);


        }


        //编辑主表的审批状态、审批人等信息
        dao.update("ProjectApplyMapper.updateApproveStatus", pd);

    }


    /**
     * 列表提交
     *
     * @param pd
     */
    @Override
    public void submitRecord(PageData pd) {
        //启动审批流程
        flowService.startFlow(pd);

        //编辑主表的审批状态、审批人等信息
        dao.update("ProjectApplyMapper.updateApproveStatus", pd);

    }

    /**
     * 导入全部数据
     *
     * @param file
     * @param pd
     * @throws Exception
     */
    @Override
    @Transactional
    public void uploadAll(MultipartFile file, PageData pd) throws Exception {

        String businessId = "RDPI" + UUID.randomUUID().toString();//生成业务主键ID
        String serialNumber = SequenceUtil.generateSerialNo("RDPI");//生成流水号

        pd.put("businessId", businessId);
        pd.put("serialNumber", serialNumber);
        pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[0]);
        pd.put("createdDate", dateFormat.format(new Date()));

        XSSFWorkbook wb = getWorkbook(file);

        //1、读取主表信息
        getMainData(wb,pd);

        //2、读取立项调研信息
        getSurveyInfo(wb,pd);

        //3、读取进度计划
        List<PageData> progressPlanList = getProgressPlan(wb,pd);

        //4、读取参加单位
        List<PageData> attendUnitList = getAttendUnit(wb,pd);

        //5、读取研究人员（初始）
        List<PageData> researchUserList = getResearchUser(wb,pd);

        //6、读取经费来源预算
        PageData budgetSource = new PageData();
        getBudgetSource(wb,pd,budgetSource);

        //7、读取经费支出预算
        PageData budgetExpenses = new PageData();
        getBudgetExpenses(wb,pd,budgetExpenses);

        //8、读取经费预算-每月预算
        getBudgetMonth(wb,pd);

        //9、年度预算（按月填报)
        List<PageData> monthList = getMonthList(wb,pd);

        //10、读取拨款
        List<PageData> appropriationPlanList = getAppropriationPlan(wb,pd);


        //将数据插入数据库
        //1、插入主表
        dao.insert("ProjectApplyMapper.insertMain", pd);

        //2、插入立项调研信息
        dao.insert("ProjectApplyMapper.insertSurvey", pd);

        //3、插入进度计划
        if (!CollectionUtils.isEmpty(progressPlanList)) {
            dao.batchInsert("ProjectApplyMapper.batchInsertProgressPlan", progressPlanList);

        }

        //4、插入参与单位
        if (!CollectionUtils.isEmpty(attendUnitList)) {
            dao.batchInsert("ProjectApplyMapper.batchInsertAttendUnit", attendUnitList);

        }

        //5、插入研究人员
        if (!CollectionUtils.isEmpty(researchUserList)) {
            dao.batchInsert("ProjectApplyMapper.batchInsertResearchUser", researchUserList);

        }

        //6、经费来源预算
        dao.insert("ProjectApplyMapper.insertBudgetSource", budgetSource);


        //7、经费支出预算
        dao.insert("ProjectApplyMapper.insertBudgetExpenses", budgetExpenses);


        //8、插入经费来源每月预算
        dao.insert("ProjectApplyMapper.insertBudgetSource", pd);

        //9、插入经费支出每月预算
        dao.insert("ProjectApplyMapper.insertBudgetExpenses", pd);

        //10、插入年度预算（按月填报）
        if (!CollectionUtils.isEmpty(monthList)) {
            dao.batchInsert("ProjectApplyMapper.batchInsertMonthList", monthList);

        }

        //11、插入拨款计划
        if (!CollectionUtils.isEmpty(appropriationPlanList)) {
            dao.batchInsert("ProjectApplyMapper.batchAppropriationPlan", appropriationPlanList);

        }


    }


    /**
     *
     * 导入主信息
     * @param file
     * @param pd
     * @return
     */
    @Override
    public PageData uploadMain(MultipartFile file, PageData pd) throws Exception{

        XSSFWorkbook wb = getWorkbook(file);

        getMainData(wb,pd);

        return pd;
    }

    /**
     *
     * 导入立项调研信息
     * @param file
     * @param pd
     * @return
     */
    @Override
    public PageData uploadSurvey(MultipartFile file, PageData pd) throws Exception{
        XSSFWorkbook wb = getWorkbook(file);

        getSurveyInfo(wb,pd);

        return pd;
    }

    /**
     *
     * 导入进度计划
     * @param file
     * @param pd
     * @return
     */
    @Override
    public List<PageData> uploadProgress(MultipartFile file, PageData pd) throws Exception{
        XSSFWorkbook wb = getWorkbook(file);

        List<PageData> progressPlanList = getProgressPlan(wb,pd);

        return progressPlanList;
    }

    /**
     *
     * 导入参加单位
     * @param file
     * @param pd
     * @return
     */
    @Override
    public List<PageData> uploadUnit(MultipartFile file, PageData pd) throws Exception{
        XSSFWorkbook wb = getWorkbook(file);

        List<PageData> attendUnitList = getAttendUnit(wb,pd);

        return attendUnitList;
    }

    /**
     *
     * 导入读取研究人员（初始）
     * @param file
     * @param pd
     * @return
     */
    @Override
    public List<PageData> uploadUser(MultipartFile file, PageData pd) throws Exception{
        XSSFWorkbook wb = getWorkbook(file);

        List<PageData> researchUserList = getResearchUser(wb,pd);

        return researchUserList;
    }

    /**
     *
     * 导入经费预算
     * @param file
     * @param pd
     * @return
     */
    @Override
    public PageData uploadBudget(MultipartFile file, PageData pd) throws Exception{
        XSSFWorkbook wb = getWorkbook(file);

        PageData data = new PageData();
        //读取经费来源预算
        getBudgetSource(wb,pd,data);

        //读取经费支出预算
        getBudgetExpenses(wb,pd,data);

        return data;
    }

    /**
     *
     * 导入经费预算-每月预算
     * @param file
     * @param pd
     * @return
     */
    @Override
    public PageData uploadMonth(MultipartFile file, PageData pd) throws Exception{
        XSSFWorkbook wb = getWorkbook(file);

        //读取经费预算
        getBudgetMonth(wb,pd);

        List<PageData> monthList = getMonthList(wb,pd);
        pd.put("monthList",monthList);

        return pd;
    }


    /**
     *
     * 导入拨款计划
     * @param file
     * @param pd
     * @return
     */
    @Override
    public List<PageData> uploadAppropriation(MultipartFile file, PageData pd) throws Exception{
        XSSFWorkbook wb = getWorkbook(file);

        List<PageData> appropriationPlanList = getAppropriationPlan(wb,pd);

        return appropriationPlanList;
    }


    private XSSFWorkbook getWorkbook(MultipartFile file) throws Exception{
        XSSFWorkbook wb = null;
        String fileName = file.getOriginalFilename();
        InputStream inputStream = new ByteArrayInputStream(file.getBytes());

        //判断文件格式
        if (fileName.endsWith("xlsx") || fileName.endsWith("xls")) {
            wb = new XSSFWorkbook(inputStream);
        } else {
            throw new MyException(ConstantMsgUtil.ERR_FILE_TYPE.desc());
        }

        return wb;
    }

    /**
     * 读取主表信息
     *
     * @return
     */
    private PageData getMainData(XSSFWorkbook wb,PageData pd) {

        //查询出盾构机类型字典表
        List<String> dicList = new ArrayList<>();
        dicList.add("1012");//性别
        dicList.add("1019");//项目类型
        dicList.add("1020");//专业类别

        List<PageData> dicTypeList = (List<PageData>) dao.findForList("DictionaryMapper.getDicValue", dicList);
        Map<String, String> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(dicTypeList)) {
            for (PageData dic : dicTypeList) {
                map.put(dic.getString("dicEnumName"), dic.getString("dicEnumId"));
            }
        }

        XSSFSheet sheet = wb.getSheet("主信息");
        if (sheet == null) {
            throw new MyException("请上传正确的模板，检查主信息sheet页是否正确");
        }

        //1、读取项目名称
        XSSFRow row = sheet.getRow(1);
        XSSFCell cell = row.getCell(1);
        String projectName = ReadExcelUtil.readCellStr(cell, 2, "项目名称", false, 256);
        pd.put("projectName",projectName);

        //2、读取单位名称
        row = sheet.getRow(2);
        cell = row.getCell(1);
        String unitName = ReadExcelUtil.readCellStr(cell, 3, "单位名称", false, 256);
        pd.put("unitName",unitName);

        //3、读取单位地址
        row = sheet.getRow(3);
        cell = row.getCell(1);
        String unitAddress = ReadExcelUtil.readCellStr(cell, 4, "单位地址", false, 256);
        pd.put("unitAddress",unitAddress);

        cell = row.getCell(3);
        String zipCode = ReadExcelUtil.readCellStr(cell, 4, "编码", false, 256);
        pd.put("unitAddress",zipCode);

        //4、申请人
        row = sheet.getRow(4);
        cell = row.getCell(1);
        String applyUserName = ReadExcelUtil.readCellStr(cell, 5, "申请人", false, 256);
        pd.put("applyUserName",applyUserName);

        //5、性别
        cell = row.getCell(3);
        String gender = ReadExcelUtil.readCellStr(cell, 5, "性别", false, 256);
        pd.put("gender",gender);
        pd.put("genderCode",getCode(gender,map));

        //6、年龄
        cell = row.getCell(5);
        String age = ReadExcelUtil.readCellStr(cell, 5, "年龄", false, 256);
        pd.put("age",age);

        //7、职务
        cell = row.getCell(7);
        String postName = ReadExcelUtil.readCellStr(cell, 5, "职务", false, 256);
        pd.put("postName",postName);

        //8、电话
        cell = row.getCell(9);
        String telephone = ReadExcelUtil.readCellStr(cell, 5, "电话", false, 256);
        pd.put("telephone",telephone);

        //9、读取申请经费
        row = sheet.getRow(5);
        cell = row.getCell(1);
        String applyAmount = ReadExcelUtil.readCellDecimal(cell, 6, "申请经费", false, 20,2);
        pd.put("applyAmount",applyAmount);

        //10、起始年度
        cell = row.getCell(3);
        String startYear = ReadExcelUtil.readCellStr(cell, 6, "起始年度", false, 256);
        pd.put("startYear",startYear);

        //11、结束年度
        cell = row.getCell(5);
        String endYear = ReadExcelUtil.readCellStr(cell, 6, "结束年度", false, 256);
        pd.put("endYear",endYear);

        //12、专业类别
        row = sheet.getRow(6);
        cell = row.getCell(1);
        String professionalCategory = ReadExcelUtil.readCellStr(cell, 7, "专业类别", false, 256);
        pd.put("professionalCategory",professionalCategory);
        pd.put("professionalCategoryCode",getCode(professionalCategory,map));

        //13、项目类型
        row = sheet.getRow(7);
        cell = row.getCell(1);
        String projectType = ReadExcelUtil.readCellStr(cell, 8, "项目类型", false, 256);
        pd.put("projectType",projectType);
        pd.put("projectTypeCode",getCode(projectType,map));

        //14、是否鉴定
        cell = row.getCell(3);
        String identify = ReadExcelUtil.readCellStr(cell, 8, "项目类型", false, 256);
        pd.put("identify",identify.equals("是")?"1":"0");

        //13、项目类型
        row = sheet.getRow(9);
        cell = row.getCell(1);
        String researchContents = ReadExcelUtil.readCellMinStr(cell, 10, "研究内容提要", 200);
        pd.put("researchContents",researchContents);

        //13、项目类型
        row = sheet.getRow(11);
        cell = row.getCell(1);
        String reviewComments = ReadExcelUtil.readCellMinStr(cell, 12, "申报单位审查意见", 200);
        pd.put("reviewComments",reviewComments);

        return pd;
    }


    /**
     * 立项调研信息
     *
     * @return
     */
    private PageData getSurveyInfo(XSSFWorkbook wb,PageData pd) {

        XSSFSheet sheet = wb.getSheet("立项调研信息");
        if (sheet == null) {
            throw new MyException("请上传正确的模板，检查立项调研信息sheet页是否正确");
        }

        //一、国内外现状
        XSSFRow row = sheet.getRow(1);
        XSSFCell cell = row.getCell(0);
        String currentSituation = ReadExcelUtil.readCellMinStr(cell, 2, "国内外现状", 200);
        pd.put("currentSituation",currentSituation);

        //二、研发的目的和意义
        row = sheet.getRow(3);
        cell = row.getCell(0);
        String purposeSignificance = ReadExcelUtil.readCellMinStr(cell, 4, "研发的目的和意义", 200);
        pd.put("purposeSignificance",purposeSignificance);

        //三、主要研究内容及研究方法
        row = sheet.getRow(5);
        cell = row.getCell(0);
        String contentMethod = ReadExcelUtil.readCellMinStr(cell, 6, "主要研究内容及研究方法", 200);
        pd.put("contentMethod",contentMethod);

        //四、要达到的目标、成果形式及主要技术指标
        row = sheet.getRow(7);
        cell = row.getCell(0);
        String targetResults = ReadExcelUtil.readCellMinStr(cell, 8, "要达到的目标、成果形式及主要技术指标", 200);
        pd.put("targetResults",targetResults);

        //五、现有研发条件和工作基础
        row = sheet.getRow(9);
        cell = row.getCell(0);
        String basicConditions = ReadExcelUtil.readCellMinStr(cell, 10, "现有研发条件和工作基础", 200);
        pd.put("basicConditions",basicConditions);

        //六、研发项目创新点
        row = sheet.getRow(11);
        cell = row.getCell(0);
        String innovationPoints = ReadExcelUtil.readCellMinStr(cell, 12, "研发项目创新点", 200);
        pd.put("innovationPoints",innovationPoints);

        //七、成果转化的可行性分析
        row = sheet.getRow(13);
        cell = row.getCell(0);
        String feasibilityAnalysis = ReadExcelUtil.readCellMinStr(cell, 14, "成果转化的可行性分析", 200);
        pd.put("feasibilityAnalysis",feasibilityAnalysis);

        return pd;
    }


    /**
     * 读取进度计划
     * @return
     */
    private List<PageData> getProgressPlan(XSSFWorkbook wb,PageData pd) {
        List<PageData> list = new ArrayList<>();

        //判断有无sheet页
        XSSFSheet sheet = wb.getSheet("进度计划");
        if (sheet == null) {
            throw new MyException("请上传正确的模板，检查进度计划sheet页是否正确");
        }

        String[] head = {"年度", "计划及目标"};
        checkHead(sheet,head);

        //遍历文件
        int end = sheet.getLastRowNum();
        for (int i = 2; i <= end; i++) {
            PageData data = packageData(pd);

            XSSFRow row = sheet.getRow(i);
            XSSFCell cell0 = row.getCell(0);
            XSSFCell cell1 = row.getCell(1);

            int rowNumber = i + 1;
            //解析第一个单元格 年度
            String years = ReadExcelUtil.readCellStr(cell0, rowNumber, "年度", false, 256);
            data.put("years",years);

            //解析第二个单元格 计划及目标
            String planTarget = ReadExcelUtil.readCellStr(cell1, rowNumber, "计划及目标", false, 2048);
            data.put("planTarget",planTarget);

            list.add(data);


        }
        return list;
    }


    /**
     * 读取参加单位
     * @return
     */
    private List<PageData> getAttendUnit(XSSFWorkbook wb,PageData pd) {
        List<PageData> list = new ArrayList<>();

        //判断有无sheet页
        XSSFSheet sheet = wb.getSheet("参加单位");
        if (sheet == null) {
            throw new MyException("请上传正确的模板，检查参加单位sheet页是否正确");
        }


        String[] head = {"参加单位", "研究任务及分工"};
        checkHead(sheet,head);

        //遍历文件
        int end = sheet.getLastRowNum();
        for (int i = 2; i <= end; i++) {
            PageData data = packageData(pd);

            XSSFRow row = sheet.getRow(i);
            XSSFCell cell0 = row.getCell(0);
            XSSFCell cell1 = row.getCell(1);

            int rowNumber = i + 1;
            //解析第一个单元格 参加单位
            String unitName = ReadExcelUtil.readCellStr(cell0, rowNumber, "参加单位", false, 256);
            data.put("unitName",unitName);

            //解析第二个单元格 研究任务及分工
            String taskDivision = ReadExcelUtil.readCellStr(cell1, rowNumber, "研究任务及分工", false, 2048);
            data.put("taskDivision",taskDivision);

            list.add(data);


        }
        return list;
    }


    /**
     * 读取研究人员（初始）
     * @param wb
     * @return
     */
    private List<PageData> getResearchUser(XSSFWorkbook wb,PageData pd) {
        List<PageData> list = new ArrayList<>();

        //判断有无sheet页
        XSSFSheet sheet = wb.getSheet("研究人员（初始）");
        if (sheet == null) {
            throw new MyException("请上传正确的模板，检查研究人员（初始）sheet页是否正确");
        }

        String[] head = {"姓名","身份证号码","年龄","性别","学历","所属部门","职务职称","所学专业","现从事专业","所在单位","研究任务及分工","全时率","联系电话","参与研发开始日期","参与研发结束日期"};
        checkHead(sheet,head);

        //遍历文件
        int end = sheet.getLastRowNum();
        for (int i = 2; i <= end; i++) {
            PageData data = packageData(pd);

            XSSFRow row = sheet.getRow(i);

            XSSFCell cell1 = row.getCell(1);
            XSSFCell cell2 = row.getCell(2);
            XSSFCell cell3 = row.getCell(3);
            XSSFCell cell4 = row.getCell(4);
            XSSFCell cell5 = row.getCell(5);
            XSSFCell cell6 = row.getCell(6);
            XSSFCell cell7 = row.getCell(7);
            XSSFCell cell8 = row.getCell(8);
            XSSFCell cell9 = row.getCell(9);
            XSSFCell cell10 = row.getCell(10);
            XSSFCell cell11 = row.getCell(11);
            XSSFCell cell12 = row.getCell(12);
            XSSFCell cell13 = row.getCell(13);
            XSSFCell cell14 = row.getCell(14);
            XSSFCell cell15 = row.getCell(15);

            int rowNumber = i + 1;
            //解析第1个单元格 姓名
            String unitName = ReadExcelUtil.readCellStr(cell1, rowNumber, "姓名", false, 256);
            data.put("unitName",unitName);

            //解析第2个单元格 身份证号码
            String idCard = ReadExcelUtil.readCellStr(cell2, rowNumber, "身份证号码", false, 256);
            data.put("idCard",idCard);

            //解析第3个单元格 年龄
            String age = ReadExcelUtil.readCellStr(cell3, rowNumber, "年龄", false, 256);
            data.put("age",age);

            //解析第4个单元格 性别
            String gender = ReadExcelUtil.readCellStr(cell4, rowNumber, "性别", false, 256);
            data.put("gender",gender);

            //解析第5个单元格 学历
            String education = ReadExcelUtil.readCellStr(cell5, rowNumber, "学历", false, 256);
            data.put("education",education);

            //解析第6个单元格 所属部门
            String belongDepartment = ReadExcelUtil.readCellStr(cell6, rowNumber, "所属部门", false, 256);
            data.put("belongDepartment",belongDepartment);

            //解析第7个单元格 职务职称
            String belongPost = ReadExcelUtil.readCellStr(cell7, rowNumber, "职务职称", false, 256);
            data.put("belongPost",belongPost);

            //解析第8个单元格 所学专业
            String majorStudied = ReadExcelUtil.readCellStr(cell8, rowNumber, "所学专业", false, 256);
            data.put("majorStudied",majorStudied);

            //解析第9个单元格 现从事专业
            String majorWorked = ReadExcelUtil.readCellStr(cell9, rowNumber, "现从事专业", false, 256);
            data.put("majorWorked",majorWorked);

            //解析第10个单元格 所在单位
            String belongUnit = ReadExcelUtil.readCellStr(cell10, rowNumber, "所在单位", false, 256);
            data.put("belongUnit",belongUnit);

            //解析第11个单元格 研究任务及分工
            String taskDivision = ReadExcelUtil.readCellStr(cell11, rowNumber, "研究任务及分工", false, 256);
            data.put("taskDivision",taskDivision);

            //解析第12个单元格 全时率
            String workRate = ReadExcelUtil.readCellStr(cell12, rowNumber, "全时率", false, 256);
            data.put("workRate",workRate);

            //解析第13个单元格 联系电话
            String telephone = ReadExcelUtil.readCellStr(cell13, rowNumber, "联系电话", false, 256);
            data.put("telephone",telephone);

            //解析第14个单元格 参与研发开始日期
            String startDate = ReadExcelUtil.readCellStr(cell14, rowNumber, "参与研发开始日期", false, 256);
            data.put("startDate",startDate);

            //解析第15个单元格 参与研发结束日期
            String endDate = ReadExcelUtil.readCellStr(cell15, rowNumber, "参与研发结束日期", false, 256);
            data.put("endDate",endDate);

            list.add(data);


        }
        return list;
    }


    /**
     * 读取经费来源预算
     * @return
     */
    private PageData getBudgetSource(XSSFWorkbook wb,PageData pd,PageData data) {

        //判断有无sheet页
        XSSFSheet sheet = wb.getSheet("经费预算");
        if (sheet == null) {
            throw new MyException("请上传正确的模板，检查经费预算sheet页是否正确");
        }

        String[] head = {"一、股份公司计划拨款","二、国家拨款","三、省市拨款","四、单位自筹款","五、银行贷款","六、其他来源款","来源预算合计"};
        checkHead(sheet,head);


        data.put("businessId", pd.getString("businessId"));
        data.put("status", 1);

        XSSFRow row = sheet.getRow(2);

        //解析第1个单元格 一、股份公司计划拨款
        XSSFCell cell = row.getCell(1);
        String companyAppropriation = ReadExcelUtil.readCellDecimal(cell, 3, "一、股份公司计划拨款", false, 20, 2);
        data.put("companyAppropriation", companyAppropriation);

        //解析第2个单元格 二、国家拨款
        cell = row.getCell(2);
        String stateAppropriation = ReadExcelUtil.readCellDecimal(cell, 3, "二、国家拨款", false, 20, 2);
        data.put("stateAppropriation", stateAppropriation);

        //解析第3个单元格 三、省市拨款
        cell = row.getCell(3);
        String provincesAppropriation = ReadExcelUtil.readCellDecimal(cell, 3, "三、省市拨款", false, 20, 2);
        data.put("provincesAppropriation", provincesAppropriation);

        //解析第4个单元格 四、单位自筹款
        cell = row.getCell(4);
        String unitFunds = ReadExcelUtil.readCellDecimal(cell, 3, "四、单位自筹款", false, 20, 2);
        data.put("unitFunds", unitFunds);

        //解析第5个单元格 五、银行贷款
        cell = row.getCell(5);
        String bankLoans = ReadExcelUtil.readCellDecimal(cell, 3, "五、银行贷款", false, 20, 2);
        data.put("bankLoans", bankLoans);

        //解析第6个单元格 六、其他来源款
        cell = row.getCell(6);
        String otherSource = ReadExcelUtil.readCellDecimal(cell, 3, "六、其他来源款", false, 20, 2);
        data.put("otherSource", otherSource);

        //解析第7个单元格 来源预算合计
        cell = row.getCell(7);
        String totalSourceBudget = ReadExcelUtil.readCellDecimal(cell, 3, "来源预算合计", false, 20, 2);
        data.put("totalSourceBudget", totalSourceBudget);


        return data;
    }


    /**
     * 读取经费支出预算
     * @return
     */
    private PageData getBudgetExpenses(XSSFWorkbook wb,PageData pd,PageData data) {

        //判断有无sheet页
        XSSFSheet sheet = wb.getSheet("经费预算");
        if (sheet == null) {
            throw new MyException("请上传正确的模板，检查经费预算sheet页是否正确");
        }

        String[] head = {"一、人员费","二、设备费","三、材料费","四、燃料及动力费","五、测试及化验费","六、差旅费","七、会议费",
                "八、课题管理费","九、其他费用","9.1、国际合作交流费","9.2、出版/文献/信息传播","9.3、知识产权事务","9.4、专家费","9.5其他",
                "十、新产品设计费","十一、委托研发费用","支出预算合计"};
        checkHead(sheet,head);


        data.put("businessId", pd.getString("businessId"));
        data.put("status", 1);

        XSSFRow row = sheet.getRow(2);

        //解析第1个单元格 一、人员费
        XSSFCell cell = row.getCell(8);
        String staffCost = ReadExcelUtil.readCellDecimal(cell, 3, "一、人员费", false, 20, 2);
        data.put("staffCost", staffCost);

        //解析第2个单元格 二、设备费
        cell = row.getCell(9);
        String equipmentCost = ReadExcelUtil.readCellDecimal(cell, 3, "二、设备费", false, 20, 2);
        data.put("equipmentCost", equipmentCost);

        //解析第3个单元格 三、材料费
        cell = row.getCell(10);
        String materialCost = ReadExcelUtil.readCellDecimal(cell, 3, "三、材料费", false, 20, 2);
        data.put("materialCost", materialCost);

        //解析第4个单元格  四、燃料及动力费
        cell = row.getCell(11);
        String fuelCost = ReadExcelUtil.readCellDecimal(cell, 3, "四、燃料及动力费", false, 20, 2);
        data.put("fuelCost", fuelCost);

        //解析第5个单元格 五、测试及化验费
        cell = row.getCell(12);
        String assayCost = ReadExcelUtil.readCellDecimal(cell, 3, "五、测试及化验费", false, 20, 2);
        data.put("assayCost", assayCost);

        //解析第6个单元格 六、差旅费
        cell = row.getCell(13);
        String travelCost = ReadExcelUtil.readCellDecimal(cell, 3, "六、差旅费", false, 20, 2);
        data.put("travelCost", travelCost);

        //解析第7个单元格 七、会议费
        cell = row.getCell(14);
        String meetingCost = ReadExcelUtil.readCellDecimal(cell, 3, "七、会议费", false, 20, 2);
        data.put("meetingCost", meetingCost);

        //解析第8个单元格 八、课题管理费
        cell = row.getCell(15);
        String managementCost = ReadExcelUtil.readCellDecimal(cell, 3, "八、课题管理费", false, 20, 2);
        data.put("managementCost", managementCost);


        //解析第9个单元格 九、其他费用
        cell = row.getCell(16);
        String otherCost = ReadExcelUtil.readCellDecimal(cell, 3, "九、其他费用", false, 20, 2);
        data.put("otherCost", otherCost);


        //解析第10个单元格 9.1、国际合作交流费
        cell = row.getCell(17);
        String exchangeCost = ReadExcelUtil.readCellDecimal(cell, 3, "9.1、国际合作交流费", false, 20, 2);
        data.put("exchangeCost", exchangeCost);


        //解析第11个单元格 9.2、出版/文献/信息传播
        cell = row.getCell(18);
        String communicationCost = ReadExcelUtil.readCellDecimal(cell, 3, "9.2、出版/文献/信息传播", false, 20, 2);
        data.put("communicationCost", communicationCost);

        //解析第12个单元格 9.3、知识产权事务
        cell = row.getCell(19);
        String propertyCost = ReadExcelUtil.readCellDecimal(cell, 3, "9.3、知识产权事务", false, 20, 2);
        data.put("propertyCost", propertyCost);

        //解析第13个单元格 9.4、专家费
        cell = row.getCell(20);
        String expertCost = ReadExcelUtil.readCellDecimal(cell, 3, "9.4、专家费", false, 20, 2);
        data.put("expertCost", expertCost);

        //解析第14个单元格 9.5其他
        cell = row.getCell(21);
        String other = ReadExcelUtil.readCellDecimal(cell, 3, "9.5其他", false, 20, 2);
        data.put("other", other);

        //解析第15个单元格 十、新产品设计费
        cell = row.getCell(22);
        String designCost = ReadExcelUtil.readCellDecimal(cell, 3, "十、新产品设计费", false, 20, 2);
        data.put("designCost", designCost);

        //解析第16个单元格 十一、委托研发费用
        cell = row.getCell(23);
        String expensesCost = ReadExcelUtil.readCellDecimal(cell, 3, "十一、委托研发费用", false, 20, 2);
        data.put("expensesCost", expensesCost);

        //解析第17个单元格 支出预算合计
        cell = row.getCell(24);
        String totalExpenseBudget = ReadExcelUtil.readCellDecimal(cell, 3, "支出预算合计", false, 20, 2);
        data.put("totalExpenseBudget", totalExpenseBudget);


        return data;
    }


    //读取经费每月预算
    private PageData getBudgetMonth(XSSFWorkbook wb,PageData pd){
        //判断有无sheet页
        XSSFSheet sheet = wb.getSheet("经费预算");
        if (sheet == null) {
            throw new MyException("请上传正确的模板，检查经费预算sheet页是否正确");
        }

        pd.put("status", 3);

        //来源预算
        //来源预算合计
        XSSFRow row = sheet.getRow(3);
        XSSFCell cell = row.getCell(1);
        String totalSourceBudget = ReadExcelUtil.readCellDecimal(cell, 4, "来源预算合计", false, 20, 2);
        pd.put("totalSourceBudget",totalSourceBudget);

        //一、股份公司计划拨款
        row = sheet.getRow(4);
        cell = row.getCell(1);
        String companyAppropriation = ReadExcelUtil.readCellDecimal(cell, 5, "一、股份公司计划拨款", false, 20, 2);
        pd.put("companyAppropriation",companyAppropriation);

        //二、国家拨款
        row = sheet.getRow(5);
        cell = row.getCell(1);
        String stateAppropriation = ReadExcelUtil.readCellDecimal(cell, 6, "二、国家拨款", false, 20, 2);
        pd.put("stateAppropriation",stateAppropriation);

        //三、省市拨款
        row = sheet.getRow(6);
        cell = row.getCell(1);
        String provincesAppropriation = ReadExcelUtil.readCellDecimal(cell, 7, "三、省市拨款", false, 20, 2);
        pd.put("provincesAppropriation",provincesAppropriation);

        //四、单位自筹款
        row = sheet.getRow(7);
        cell = row.getCell(1);
        String unitFunds = ReadExcelUtil.readCellDecimal(cell, 8, "四、单位自筹款", false, 20, 2);
        pd.put("unitFunds",unitFunds);

        //五、银行贷款
        row = sheet.getRow(8);
        cell = row.getCell(1);
        String bankLoans = ReadExcelUtil.readCellDecimal(cell, 9, "五、银行贷款", false, 20, 2);
        pd.put("bankLoans",bankLoans);


        //六、其他来源款
        row = sheet.getRow(9);
        cell = row.getCell(1);
        String otherSource = ReadExcelUtil.readCellDecimal(cell, 10, "六、其他来源款", false, 20, 2);
        pd.put("otherSource",otherSource);

        //支出预算

        //解析第1个单元格 支出预算合计
        row = sheet.getRow(3);
        cell = row.getCell(3);
        String totalExpenseBudget = ReadExcelUtil.readCellDecimal(cell, 4, "支出预算合计", false, 20, 2);
        pd.put("totalExpenseBudget", totalExpenseBudget);


        //解析第1个单元格 一、人员费
        row = sheet.getRow(4);
        cell = row.getCell(3);
        String staffCost = ReadExcelUtil.readCellDecimal(cell, 5, "一、人员费", false, 20, 2);
        pd.put("staffCost", staffCost);

        //解析第2个单元格 二、设备费
        row = sheet.getRow(5);
        cell = row.getCell(3);
        String equipmentCost = ReadExcelUtil.readCellDecimal(cell, 6, "二、设备费", false, 20, 2);
        pd.put("equipmentCost", equipmentCost);

        //解析第3个单元格 三、材料费
        row = sheet.getRow(6);
        cell = row.getCell(3);
        String materialCost = ReadExcelUtil.readCellDecimal(cell, 7, "三、材料费", false, 20, 2);
        pd.put("materialCost", materialCost);

        //解析第4个单元格  四、燃料及动力费
        row = sheet.getRow(7);
        cell = row.getCell(3);
        String fuelCost = ReadExcelUtil.readCellDecimal(cell, 8, "四、燃料及动力费", false, 20, 2);
        pd.put("fuelCost", fuelCost);

        //解析第5个单元格 五、测试及化验费
        row = sheet.getRow(8);
        cell = row.getCell(3);
        String assayCost = ReadExcelUtil.readCellDecimal(cell, 9, "五、测试及化验费", false, 20, 2);
        pd.put("assayCost", assayCost);

        //解析第6个单元格 六、差旅费
        row = sheet.getRow(9);
        cell = row.getCell(3);
        String travelCost = ReadExcelUtil.readCellDecimal(cell, 10, "六、差旅费", false, 20, 2);
        pd.put("travelCost", travelCost);

        //解析第7个单元格 七、会议费
        row = sheet.getRow(10);
        cell = row.getCell(3);
        String meetingCost = ReadExcelUtil.readCellDecimal(cell, 11, "七、会议费", false, 20, 2);
        pd.put("meetingCost", meetingCost);

        //解析第8个单元格 八、课题管理费
        row = sheet.getRow(11);
        cell = row.getCell(3);
        String managementCost = ReadExcelUtil.readCellDecimal(cell, 12, "八、课题管理费", false, 20, 2);
        pd.put("managementCost", managementCost);


        //解析第9个单元格 九、其他费用
        row = sheet.getRow(12);
        cell = row.getCell(3);
        String otherCost = ReadExcelUtil.readCellDecimal(cell, 13, "九、其他费用", false, 20, 2);
        pd.put("otherCost", otherCost);


        //解析第10个单元格 9.1、国际合作交流费
        row = sheet.getRow(13);
        cell = row.getCell(3);
        String exchangeCost = ReadExcelUtil.readCellDecimal(cell, 14, "9.1、国际合作交流费", false, 20, 2);
        pd.put("exchangeCost", exchangeCost);


        //解析第11个单元格 9.2、出版/文献/信息传播
        row = sheet.getRow(14);
        cell = row.getCell(3);
        String communicationCost = ReadExcelUtil.readCellDecimal(cell, 15, "9.2、出版/文献/信息传播", false, 20, 2);
        pd.put("communicationCost", communicationCost);

        //解析第12个单元格 9.3、知识产权事务
        row = sheet.getRow(15);
        cell = row.getCell(3);
        String propertyCost = ReadExcelUtil.readCellDecimal(cell, 16, "9.3、知识产权事务", false, 20, 2);
        pd.put("propertyCost", propertyCost);

        //解析第13个单元格 9.4、专家费
        row = sheet.getRow(16);
        cell = row.getCell(3);
        String expertCost = ReadExcelUtil.readCellDecimal(cell, 17, "9.4、专家费", false, 20, 2);
        pd.put("expertCost", expertCost);

        //解析第14个单元格 9.5其他
        row = sheet.getRow(17);
        cell = row.getCell(3);
        String other = ReadExcelUtil.readCellDecimal(cell, 18, "9.5其他", false, 20, 2);
        pd.put("other", other);

        //解析第15个单元格 十、新产品设计费
        row = sheet.getRow(18);
        cell = row.getCell(3);
        String designCost = ReadExcelUtil.readCellDecimal(cell, 19, "十、新产品设计费", false, 20, 2);
        pd.put("designCost", designCost);

        //解析第16个单元格 十一、委托研发费用
        row = sheet.getRow(19);
        cell = row.getCell(3);
        String expensesCost = ReadExcelUtil.readCellDecimal(cell, 20, "十一、委托研发费用", false, 20, 2);
        pd.put("expensesCost", expensesCost);


        return pd;

    }


    /**
     * 读取拨款计划
     * @return
     */
    private List<PageData> getAppropriationPlan(XSSFWorkbook wb,PageData pd) {
        List<PageData> list = new ArrayList<>();

        //判断有无sheet页
        XSSFSheet sheet = wb.getSheet("拨款计划");
        if (sheet == null) {
            throw new MyException("请上传正确的模板，检查拨款计划sheet页是否正确");
        }

        String[] head = {"年度", "计划（万元）"};
        checkHead(sheet,head);

        //遍历文件
        int end = sheet.getLastRowNum();
        for (int i = 2; i <= end; i++) {
            PageData data = packageData(pd);

            XSSFRow row = sheet.getRow(i);
            XSSFCell cell0 = row.getCell(0);
            XSSFCell cell1 = row.getCell(1);

            int rowNumber = i + 1;
            //解析第一个单元格 年度
            String years = ReadExcelUtil.readCellStr(cell0, rowNumber, "年度", false, 256);
            data.put("years",years);

            //解析第二个单元格 计划（万元）
            String planAmount = ReadExcelUtil.readCellDecimal(cell1, rowNumber, "二、国家拨款", false, 20, 2);
            data.put("planAmount",planAmount);

            list.add(data);


        }
        return list;
    }

    /**
     * 读取年度预算（按月填报）
     * @return
     */
    private List<PageData> getMonthList(XSSFWorkbook wb,PageData pd) {
        List<PageData> list = new ArrayList<>();

        //判断有无sheet页
        XSSFSheet sheet = wb.getSheet("每月预算");
        if (sheet == null) {
            throw new MyException("请上传正确的模板，检查每月预算sheet页是否正确");
        }


        //遍历文件
        //判断有没有月份
        XSSFRow headRow = sheet.getRow(3);
        for (int i = 0; i < 100; i++) {
            int num = 4 + i * 12;
            XSSFCell headCell = headRow.getCell(num);
            String value = ReadExcelUtil.readCellStr(headCell, 4, "年度", false, 256);
            if (value.equals("1月")) {//表示填数据了

                //读取年份
                XSSFRow yearRow = sheet.getRow(2);
                XSSFCell yearCell = yearRow.getCell(4 + i);
                String yearValue = ReadExcelUtil.readCellStr(yearCell, 4, "年度", false, 256);
                String years = yearValue.substring(0,4);


                int cellNum = 4 + i;
                for (int y = 4; y <= 19; y++) {
                    PageData data = new PageData();
                    data.put("businessId", pd.getString("businessId"));
                    data.put("years", years);

                    XSSFRow row = sheet.getRow(y);
                    XSSFCell cell1 = row.getCell(cellNum);
                    XSSFCell cell2 = row.getCell(cellNum + 1);
                    XSSFCell cell3 = row.getCell(cellNum + 2);
                    XSSFCell cell4 = row.getCell(cellNum + 3);
                    XSSFCell cell5 = row.getCell(cellNum + 4);
                    XSSFCell cell6 = row.getCell(cellNum + 5);
                    XSSFCell cell7 = row.getCell(cellNum + 6);
                    XSSFCell cell8 = row.getCell(cellNum + 7);
                    XSSFCell cell9 = row.getCell(cellNum + 8);
                    XSSFCell cell10 = row.getCell(cellNum + 9);
                    XSSFCell cell11 = row.getCell(cellNum + 10);
                    XSSFCell cell12 = row.getCell(cellNum + 11);


                    int rowNumber = y + 1;

                    //解析第1个单元格 1月
                    String january = ReadExcelUtil.readCellDecimal(cell1, rowNumber, "1月", false, 20, 2);
                    data.put("january",january);

                    //解析第2个单元格 2月
                    String february = ReadExcelUtil.readCellDecimal(cell2, rowNumber, "2月", false, 20, 2);
                    data.put("february",february);

                    //解析第3个单元格 3月
                    String march = ReadExcelUtil.readCellDecimal(cell3, rowNumber, "3月", false, 20, 2);
                    data.put("march",march);

                    //解析第4个单元格 4月
                    String april = ReadExcelUtil.readCellDecimal(cell4, rowNumber, "4月", false, 20, 2);
                    data.put("april",april);

                    //解析第5个单元格 5月
                    String may = ReadExcelUtil.readCellDecimal(cell5, rowNumber, "5月", false, 20, 2);
                    data.put("may",may);

                    //解析第6个单元格 6月
                    String june = ReadExcelUtil.readCellDecimal(cell6, rowNumber, "6月", false, 20, 2);
                    data.put("june",june);

                    //解析第7个单元格 7月
                    String july = ReadExcelUtil.readCellDecimal(cell7, rowNumber, "7月", false, 20, 2);
                    data.put("july",july);

                    //解析第8个单元格 8月
                    String august = ReadExcelUtil.readCellDecimal(cell8, rowNumber, "8月", false, 20, 2);
                    data.put("august",august);

                    //解析第9个单元格 9月
                    String september = ReadExcelUtil.readCellDecimal(cell9, rowNumber, "9月", false, 20, 2);
                    data.put("september",september);

                    //解析第10个单元格 10月
                    String october = ReadExcelUtil.readCellDecimal(cell10, rowNumber, "10月", false, 20, 2);
                    data.put("october",october);

                    //解析第11个单元格 11月
                    String november = ReadExcelUtil.readCellDecimal(cell11, rowNumber, "11月", false, 20, 2);
                    data.put("november",november);

                    //解析第12个单元格 12月
                    String december = ReadExcelUtil.readCellDecimal(cell12, rowNumber, "12月", false, 20, 2);
                    data.put("december",december);

                    list.add(data);


                }


            }else if(StringUtils.isBlank(value)){

                break;
            }

        }


        return list;
    }




    /**
     * 封装特定的参数
     * @param pd
     * @return
     */
    private PageData packageData(PageData pd) {
        PageData data = new PageData();

        data.put("createUserId", pd.getString("createUserId"));
        data.put("createUser", pd.getString("createUser"));
        data.put("createTime", timeFormat.format(new Date()));
        data.put("businessId", pd.getString("businessId"));

        return data;
    }


    /**
     * 判断表头是否正确
     * @param sheet
     * @param head
     */
    private void checkHead(XSSFSheet sheet,String[] head){
        XSSFRow row  = sheet.getRow(1);
        //判断表头是否正确
        for (int i = 0; i < head.length; i++) {
            if (!row.getCell(i).getStringCellValue().trim().equals(head[i])) {
                throw new MyException(ConstantMsgUtil.ERR_FILE_HEAD.desc());
            }
        }
    }



    /**
     * 根据名称查询字典表的编码
     * @param name
     * @param map
     * @return
     */
    private String getCode(String name ,Map<String, String> map){
        String code = null;
        if(map != null && map.get(name) != null){
            code = map.get(name);
        }

        return code;

    }
    /**
     * 导出Excel
     *
     * @param pageData
     * @return
     */
    @Override
    public HSSFWorkbook exportExcel(PageData pageData) {
        String title = "员工信息";
        String[] head = {"序号", "用户编号", "用户姓名", "手机号码", "所属部门", "所属职务", "学历", "员工状态", "员工类型", "创建人", "创建日期", "更新人", "更新日期"};
        String idStr = pageData.getString("idList");
        List<String> listId = JSONObject.parseArray(idStr, String.class);
        //根据idList查询主表
        List<PageData> userInfoList = (List<PageData>) dao.findForList("UserMapper.queryUserById", listId);

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(title + 1);
        HSSFCellStyle styleHeader = ExcelUtil.setHeader(wb, sheet);// 表头
        HSSFCellStyle styleCell = ExcelUtil.setCell(wb, sheet);// 单元格
        // 创建表头
        HSSFRow rows = sheet.createRow(0);
        rows.setHeightInPoints(20);// 行高
        HSSFCell cells = rows.createCell(0);
        cells.setCellValue(title);
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 0, 0, 0, (head.length - 1));
        // 第一行  表头
        HSSFRow rowTitle = sheet.createRow(1);
        HSSFCell hc;
        for (int j = 0; j < head.length; j++) {
            hc = rowTitle.createCell(j);
            hc.setCellValue(head[j]);
            hc.setCellStyle(styleCell);
        }

        if (!CollectionUtils.isEmpty(userInfoList)) {
            for (int i = 0; i < userInfoList.size(); i++) {
                PageData pd = userInfoList.get(i);

                HSSFRow row = sheet.createRow(i + 2);
                int j = 0;

                HSSFCell cell = row.createCell(j++);
                cell.setCellValue(i + 1);
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("userCode"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("userName"));
                cell.setCellStyle(styleCell);


                cell = row.createCell(j++);
                String time1 = pd.getString("updateTime");
                cell.setCellValue(time1.substring(0, time1.lastIndexOf(".")));
                cell.setCellStyle(styleCell);


            }
        }

        //设置自适应宽度
        for (int j = 0; j < head.length; j++) {
            sheet.autoSizeColumn(j);
            int colWidth = sheet.getColumnWidth(j) * 17 / 10;
            if (colWidth < 255 * 256) {
                sheet.setColumnWidth(j, colWidth < 3000 ? 3000 : colWidth);
            } else {
                sheet.setColumnWidth(j, 6000);
            }
        }
        sheet.setAutobreaks(true);
        return wb;
    }


}
