package com.rdexpense.manager.service.projectApply.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.*;
import com.rdexpense.manager.service.file.FileService;
import com.rdexpense.manager.service.flow.FlowService;
import com.rdexpense.manager.service.projectApply.ProjectApplyService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.common.util.ConstantMsgUtil.ERR_EXPORT_FAIL;
import static com.common.util.ConstantMsgUtil.INFO_EXPORT_SUCCESS;


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

    private  static final String[] arr = {"january","february","march","april","may","june","july","august","september","october","november","december"};

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

        //6、插入经费预算
        String budgetListStr = pd.getString("budgetList");
        List<PageData> budgetList = JSONObject.parseArray(budgetListStr, PageData.class);
        if (!CollectionUtils.isEmpty(budgetList)) {
            for (PageData detailData : budgetList) {
                detailData.put("businessId", businessId);

            }

            dao.batchInsert("ProjectApplyMapper.batchInsertBudget", budgetList);

        }

        //7、插入经费预算-每月预算
        String monthListStr = pd.getString("monthList");
        List<PageData> monthList = JSONObject.parseArray(monthListStr, PageData.class);
        if (!CollectionUtils.isEmpty(monthList)) {

            //处理按月填写的预算，进行列转行的封装
            List<PageData> detailList = getMonthDetailList(monthList,businessId);
            if(!CollectionUtils.isEmpty(detailList)){
                dao.batchInsert("ProjectApplyMapper.batchInsertBudgetMonthDetail", detailList);
            }

            for (PageData detailData : monthList) {
                detailData.put("businessId", businessId);
            }

            dao.batchInsert("ProjectApplyMapper.batchInsertBudgetMonth1", monthList);

        }


        //8、插入拨款计划
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

        //6、插入经费预算
        String budgetListStr = pd.getString("budgetList");
        List<PageData> budgetList = JSONObject.parseArray(budgetListStr, PageData.class);
        if (!CollectionUtils.isEmpty(budgetList)) {
            for (PageData detailData : budgetList) {
                detailData.put("businessId", businessId);

            }
            dao.batchDelete("ProjectApplyMapper.deleteBudget", removeList);
            dao.batchInsert("ProjectApplyMapper.batchInsertBudget", budgetList);

        }

        //7、插入经费预算-每月预算
        String monthListStr = pd.getString("monthList");
        List<PageData> monthList = JSONObject.parseArray(monthListStr, PageData.class);
        if (!CollectionUtils.isEmpty(monthList)) {

            //处理按月填写的预算，进行列转行的封装
            List<PageData> detailList = getMonthDetailList(monthList,businessId);
            dao.batchDelete("ProjectApplyMapper.deleteBudgetMonthDetail", removeList);
            if(!CollectionUtils.isEmpty(detailList)){
                dao.batchInsert("ProjectApplyMapper.batchInsertBudgetMonthDetail", detailList);
            }

            for (PageData detailData : monthList) {
                detailData.put("businessId", businessId);
            }

            dao.batchDelete("ProjectApplyMapper.deleteBudgetMonth", removeList);
            dao.batchInsert("ProjectApplyMapper.batchInsertBudgetMonth", monthList);


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
     * 封装前端传来的每月预算
     * @param monthList
     * @param businessId
     * @return
     */
    private List<PageData> getMonthDetailList(List<PageData> monthList,String businessId){
        List<PageData> detailList = new ArrayList<>();
        //遍历出年份
        Set<String> yearSet = new TreeSet<>(new Comparator<String>(){
            @Override
            public int compare(String s1,String s2){
                return s1.compareTo(s2);
            }
        });

        if (!CollectionUtils.isEmpty(monthList)) {
            for (PageData detailData : monthList) {
                for (Object key : detailData.keySet()) {
                    String keyStr = (String)key;

                    if(keyStr.length() > 8 && StringUtils.isNumeric(keyStr.substring(5,9))){
                        yearSet.add(keyStr.substring(5,9));
                    }

                }
            }
        }


        for (PageData detailData : monthList) {
            if(!CollectionUtils.isEmpty(yearSet)){

                for(String year : yearSet){
                    PageData data = new PageData();
                    for(int i=0;i<12;i++){

                        String key = "month"+year+""+(i+1);
                        String value = detailData.getString(key);

                        if(StringUtils.isBlank(value)){
                            value = "0";
                        }
                        data.put(arr[i], value);

                    }

                    data.put("expenseaccount", detailData.getString("expenseaccount"));
                    data.put("businessId", businessId);
                    data.put("years", year);
                    detailList.add(data);
                }

            }
        }


        return detailList;
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

        //6、删除预算
        dao.batchDelete("ProjectApplyMapper.deleteBudget", removeList);

        //7、删除预算-每月预算
        dao.batchDelete("ProjectApplyMapper.deleteBudgetMonth", removeList);

        //8、删除年度预算（按月填报）
        dao.batchDelete("ProjectApplyMapper.deleteBudgetMonthDetail", removeList);

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
            List<PageData> budgetList = (List<PageData>) dao.findForList("ProjectApplyMapper.queryBudget", request);
            if (!CollectionUtils.isEmpty(budgetList)) {
                request.put("budgetList", budgetList);
            }


            // 8、经费预算（每月预算) 年度预算（按月填报)
            List<PageData> monthList = (List<PageData>) dao.findForList("ProjectApplyMapper.queryMonthList", request);
            if(!CollectionUtils.isEmpty(monthList)){
                List<PageData> monthDetailList = (List<PageData>) dao.findForList("ProjectApplyMapper.queryMonthDetailList", request);
                packageMonthList(monthList,monthDetailList);
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
//        PageData fileData = fileService.insertApproveFile(pd);
//        pd.put("fileId", fileData.getString("fileId"));
//        pd.put("fileName", fileData.getString("fileName"));
//        String businessId = (String)dao.findForObject("");

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
        pd.put("creatorUserId",pd.getString("createUserId"));
        pd.put("creatorUserName",pd.getString("createUser"));




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

        //6、读取经费预算
        List<PageData> budgetList = getBudget(wb,pd);

        //9、经费预算（每月预算
        List<PageData> monthList = getBudgetMonth(wb,pd);

        //9、年度预算（按月填报)
        List<PageData> monthDetailList = getBudgetMonthDetail(wb,pd,monthList);


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

        //6、插入经费预算
        if (!CollectionUtils.isEmpty(budgetList)) {
 //           dao.batchInsert("ProjectApplyMapper.batchInsertBudget", budgetList);
            dao.batchInsert("ProjectApplyMapper.batchInsertBudget1", budgetList);
        }

        //7、插入经费预算-每月预算
        if(!CollectionUtils.isEmpty(monthDetailList)){
            dao.batchInsert("ProjectApplyMapper.batchInsertBudgetMonthDetail", monthDetailList);
        }

        if(!CollectionUtils.isEmpty(monthList)){
            dao.batchInsert("ProjectApplyMapper.batchInsertBudgetMonth1", monthList);
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
    public List<PageData> uploadBudget(MultipartFile file, PageData pd) throws Exception{
        XSSFWorkbook wb = getWorkbook(file);

        List<PageData> budgetList = getBudget(wb,pd);

        return budgetList;


    }

    /**
     *
     * 导入经费预算-每月预算
     * @param file
     * @param pd
     * @return
     */
    @Override
    public List<PageData> uploadMonth(MultipartFile file, PageData pd) throws Exception{
        XSSFWorkbook wb = getWorkbook(file);

        //9、经费预算（每月预算
        List<PageData> monthList = getBudgetMonth(wb,pd);

        //9、年度预算（按月填报)
        List<PageData> monthDetailList = getBudgetMonthDetail(wb,pd,monthList);

        packageMonthList(monthList,monthDetailList);

        return monthList;
    }


    private List<PageData> packageMonthList(List<PageData> monthList,List<PageData> monthDetailList){
        if(!CollectionUtils.isEmpty(monthDetailList)){
            for(PageData data : monthList){
                String expenseaccount = data.getString("expenseaccount");
                for (PageData detailData : monthDetailList) {
                    String year = detailData.getString("years");
                    if (detailData.getString("expenseaccount").equals(expenseaccount)) {
                        for(int i=0;i<12;i++){
                            String key = "month"+year+""+(i+1);
                            data.put(key,detailData.getString(arr[0]));

                        }

                    }
                }

            }

        }

        return monthList;
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

        cell = row.getCell(10);
        String zipCode = ReadExcelUtil.readCellFormat(cell, 4, "编码", false, 256);
        pd.put("zipCode",zipCode);

        //4、申请人
        row = sheet.getRow(4);
        cell = row.getCell(1);
        String applyUserName = ReadExcelUtil.readCellStr(cell, 5, "申请人", false, 256);
        pd.put("applyUserName",applyUserName);

        //5、性别
        cell = row.getCell(4);
        String gender = ReadExcelUtil.readCellStr(cell, 5, "性别", false, 256);
        pd.put("gender",gender);
        pd.put("genderCode",getCode(gender,map));

        //6、年龄
        cell = row.getCell(6);
        String age = ReadExcelUtil.readCellFormat(cell, 5, "年龄", false, 256);
        pd.put("age",age);

        //7、职务
        cell = row.getCell(8);
        String postName = ReadExcelUtil.readCellStr(cell, 5, "职务", false, 256);
        pd.put("postName",postName);

        //8、电话
        cell = row.getCell(10);
        String telephone = ReadExcelUtil.readCellFormat(cell, 5, "电话", false, 256);
        pd.put("telephone",telephone);

        //9、读取申请经费
        row = sheet.getRow(5);
        cell = row.getCell(1);
        String applyAmount = ReadExcelUtil.readCellDecimal(cell, 6, "申请经费", false, 20,2);
        pd.put("applyAmount",applyAmount);

        //10、起始年度
        cell = row.getCell(6);
        String startYear = ReadExcelUtil.readCellDate(cell, 6, "起始年度", false);
        pd.put("startYear",startYear);

        //11、结束年度
        cell = row.getCell(9);
        String endYear = ReadExcelUtil.readCellDate(cell, 6, "结束年度", false);
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
        cell = row.getCell(9);
        String identify = ReadExcelUtil.readCellStr(cell, 8, "项目类型", false, 256);
        pd.put("identify",identify.equals("是")?"1":"0");

        //15、研究内容提要
        row = sheet.getRow(9);
        cell = row.getCell(0);
        String researchContents = ReadExcelUtil.readCellMinStr(cell, 10, "研究内容提要", 200);
        pd.put("researchContents",researchContents);

        //16、申报单位审查意见
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


        //遍历文件
        int end = sheet.getLastRowNum();
        for (int i = 2; i <= end; i++) {
            PageData data = packageData(pd);

            XSSFRow row = sheet.getRow(i);
            XSSFCell cell0 = row.getCell(0);
            XSSFCell cell1 = row.getCell(1);

            int rowNumber = i + 1;
            //解析第一个单元格 年度
            String years = ReadExcelUtil.readCellFormat(cell0, rowNumber, "年度", false, 256);
            data.put("years",years);

            //解析第二个单元格 计划及目标
            String planTarget = ReadExcelUtil.readCellStr(cell1, rowNumber, "计划及目标", false, 2048);
            data.put("planTarget",planTarget);

            if(StringUtils.isBlank(years) && StringUtils.isBlank(planTarget)){
                continue;
            }

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


 //       String[] head = {"参加单位", "研究任务及分工"};
//        checkHead(sheet,head);

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

            if(StringUtils.isBlank(unitName) && StringUtils.isBlank(taskDivision)){
                continue;
            }

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

//        String[] head = {"姓名","身份证号码","年龄","性别","学历","所属部门","职务职称","所学专业","现从事专业","所在单位","研究任务及分工","全时率","联系电话","参与研发开始日期","参与研发结束日期"};
//        checkHead(sheet,head);

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
            String userName = ReadExcelUtil.readCellStr(cell1, rowNumber, "姓名", false, 256);
            data.put("userName",userName);

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
            String startDate = ReadExcelUtil.readCellDate(cell14, rowNumber, "参与研发开始日期", false);
            data.put("startDate",startDate);

            //解析第15个单元格 参与研发结束日期
            String endDate = ReadExcelUtil.readCellDate(cell15, rowNumber, "参与研发结束日期", false);
            data.put("endDate",endDate);


            if(StringUtils.isBlank(userName) && StringUtils.isBlank(idCard)&& StringUtils.isBlank(age)&& StringUtils.isBlank(gender)
                    && StringUtils.isBlank(education)&& StringUtils.isBlank(belongDepartment)
                    && StringUtils.isBlank(belongPost)&& StringUtils.isBlank(majorStudied)&& StringUtils.isBlank(majorWorked)
                    && StringUtils.isBlank(belongUnit)&& StringUtils.isBlank(taskDivision)&& StringUtils.isBlank(workRate)
                    && StringUtils.isBlank(telephone)&& StringUtils.isBlank(startDate)&& StringUtils.isBlank(endDate)){

                continue;
            }

            list.add(data);


        }
        return list;
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

 //       String[] head = {"年度", "计划（万元）"};
 //       checkHead(sheet,head);

        //遍历文件
        int end = sheet.getLastRowNum();
        for (int i = 2; i <= end; i++) {
            PageData data = packageData(pd);

            XSSFRow row = sheet.getRow(i);
            XSSFCell cell0 = row.getCell(0);
            XSSFCell cell1 = row.getCell(1);

            int rowNumber = i + 1;
            //解析第一个单元格 年度
            String years = ReadExcelUtil.readCellFormat(cell0, rowNumber, "年度", false, 256);
            data.put("years",years);

            //解析第二个单元格 计划（万元）
            String planAmount = ReadExcelUtil.readCellDecimal(cell1, rowNumber, "二、国家拨款", false, 20, 2);
            data.put("planAmount",planAmount);

            if(StringUtils.isBlank(years) && StringUtils.isBlank(planAmount)){
                continue;
            }

            list.add(data);


        }
        return list;
    }



    /**
     * 读取经费预算
     * @return
     */
    private List<PageData> getBudget(XSSFWorkbook wb,PageData pd) {
        List<PageData> list = new ArrayList<>();

        //判断有无sheet页
        XSSFSheet sheet = wb.getSheet("经费预算");
        if (sheet == null) {
            throw new MyException("请上传正确的模板，检查经费预算sheet页是否正确");
        }

        packageBudget(sheet,list,pd);
        return list;
    }



    /**
     * 读取经费预算（每月预算）
     * @return
     */
    private List<PageData> getBudgetMonth(XSSFWorkbook wb,PageData pd) {
        List<PageData> list = new ArrayList<>();

        //判断有无sheet页
        XSSFSheet sheet = wb.getSheet("每月预算");
        if (sheet == null) {
            throw new MyException("请上传正确的模板，检查每月预算sheet页是否正确");
        }

        packageBudget(sheet,list,pd);

        return list;
    }


    private List<PageData> packageBudget(XSSFSheet sheet,List<PageData> list,PageData pd){
        //解析第1行 支出预算合计
        setBudgetValue(sheet,4,0,1,2,3,list,pd);

        //解析第1个单元格 一、人员费
        setBudgetValue(sheet,5,0,1,2,3,list,pd);

        //解析第2个单元格 二、设备费
        setBudgetValue(sheet,6,0,1,2,3,list,pd);

        //解析第3个单元格 三、材料费
        setBudgetValue(sheet,7,0,1,2,3,list,pd);

        //解析第4个单元格  四、燃料及动力费
        setBudgetValue(sheet,8,0,1,2,3,list,pd);

        //解析第5个单元格 五、测试及化验费
        setBudgetValue(sheet,9,0,1,2,3,list,pd);

        //解析第6个单元格 六、差旅费
        setBudgetValue(sheet,10,0,1,2,3,list,pd);

        //解析第7个单元格 七、会议费
        setBudgetValue(sheet,11,-1,-1,2,3,list,pd);

        //解析第8个单元格 八、课题管理费
        setBudgetValue(sheet,12,-1,-1,2,3,list,pd);

        //解析第9个单元格 九、其他费用
        setBudgetValue(sheet,13,-1,-1,2,3,list,pd);

        //解析第10个单元格 9.1、国际合作交流费
        setBudgetValue(sheet,14,-1,-1,2,3,list,pd);

        //解析第11个单元格 9.2、出版/文献/信息传播
        setBudgetValue(sheet,15,-1,-1,2,3,list,pd);

        //解析第12个单元格 9.3、知识产权事务
        setBudgetValue(sheet,16,-1,-1,2,3,list,pd);

        //解析第13个单元格 9.4、专家费
        setBudgetValue(sheet,17,-1,-1,2,3,list,pd);

        //解析第14个单元格 9.5其他
        setBudgetValue(sheet,18,-1,-1,2,3,list,pd);

        //解析第15个单元格 十、新产品设计费
        setBudgetValue(sheet,19,-1,-1,2,3,list,pd);

        //解析第16个单元格 十一、委托研发费用
        setBudgetValue(sheet,20,-1,-1,2,3,list,pd);

        return list;
    }



    /**
     * 读取年度预算（按月填报）
     * @param wb
     * @param pd
     * @return
     */
    List<PageData> getBudgetMonthDetail(XSSFWorkbook wb,PageData pd,List<PageData> monthList){
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
                XSSFCell yearCell = yearRow.getCell(num);
                String yearValue = ReadExcelUtil.readCellStr(yearCell, 4, "年度", false, 256);
                if(yearValue.length() >4){
                    yearValue = yearValue.substring(0,4);
                }

                int cellNum = 4 + i;
                for (int y = 4; y <= 20; y++) {
                    PageData data = new PageData();
                    data.put("businessId", pd.getString("businessId"));
                    data.put("years", yearValue);
                    data.put("expenseaccount",monthList.get(y-4).getString("expenseaccount"));

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



    private List<PageData> setBudgetValue(XSSFSheet sheet,int rowNum,int row1,int row2,int row3,int row4,List<PageData> list,PageData pd){
        PageData data = new PageData();
        data.put("businessId", pd.getString("businessId"));

        XSSFRow row = sheet.getRow(rowNum);
        XSSFCell cell = null;
        String sourceaccount = "";
        if(row1 >= 0){
            cell = row.getCell(row1);
            sourceaccount = ReadExcelUtil.readCellStr(cell, 3, "来源科目", false, 256);
        }

        if(row2 >= 0){
            cell = row.getCell(row2);
            String sourceBudget = ReadExcelUtil.readCellDecimal(cell, 3, sourceaccount, false, 20, 2);
            data.put("sourcebudget", sourceBudget);
        }

        String expenseaccount = "";
        if(row3 >= 0){
            cell = row.getCell(row3);
            expenseaccount = ReadExcelUtil.readCellStr(cell, 3, "支出科目", false, 256);
        }

        if(row4 >= 0){
            cell = row.getCell(row4);
            String expenseBudget = ReadExcelUtil.readCellDecimal(cell, 3, expenseaccount, false, 20, 2);
            data.put("expensebudget", expenseBudget);

        }

        data.put("sourceaccount",sourceaccount);
        data.put("expenseaccount",expenseaccount);

        list.add(data);


        return list;
    }


    /**
     * 封装特定的参数
     * @param pd
     * @return
     */
    private PageData packageData(PageData pd) {
        PageData data = new PageData();

        data.put("creatorUserId", pd.getString("createUserId"));
        data.put("creatorUser", pd.getString("createUser"));
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
     * 预览合同
     * @param pd
     * @throws Exception
     */
    @Override
    public void preview(PageData pd,HttpServletResponse response) throws Exception {


        File file = FileUtil.createFile();
        XWPFDocument doc = null;
        InputStream is = null;

        try {
            is = this.getClass().getResourceAsStream("/template/研究与开发项目合同.docx");
            doc = new XWPFDocument(is);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //3、插入进度计划
        String progressPlan = pd.getString("progressPlan");
        List<PageData> progressPlanList = JSONObject.parseArray(progressPlan, PageData.class);


        //4、插入参与单位
        String attendUnit = pd.getString("attendUnit");
        List<PageData> attendUnitList = JSONObject.parseArray(attendUnit, PageData.class);


        //5、插入研究人员
        String researchUser = pd.getString("researchUser");
        List<PageData> researchUserList = JSONObject.parseArray(researchUser, PageData.class);


        //6、插入经费预算
        String budgetListStr = pd.getString("budgetList");
        List<PageData> budgetList = JSONObject.parseArray(budgetListStr, PageData.class);


        //8、插入拨款计划
        String appropriationPlan = pd.getString("appropriationPlan");
        List<PageData> appropriationPlanList = JSONObject.parseArray(appropriationPlan, PageData.class);

        XwpfTUtil xwpfTUtil = new XwpfTUtil();


        try {

            //获取文件路径

            //文本
            Map<String, Object> params = new HashMap<>();
            //1、主信息
            String year = pd.getString("createdDate").substring(0,4);
            params.put("(year)", year);

            params.put("(projectName)", pd.getString("projectName"));
            params.put("(unitName)", pd.getString("unitName"));
            params.put("(projectLeader)", pd.getString("applyUserName"));
            params.put("(dateRange)", pd.getString("startYear")+"至"+pd.getString("endYear"));


            //2、调研信息
            params.put("(currentSituation)", pd.getString("currentSituation"));
            params.put("(contentMethod)", pd.getString("contentMethod"));
            params.put("(targetResults)", pd.getString("targetResults"));

            //3、项目负责人
            if(!CollectionUtils.isEmpty(researchUserList)){
                PageData leader = researchUserList.get(0);
                params.put("(name)", leader.getString("userName"));
                params.put("(sax)", leader.getString("gender"));
                params.put("(age)", leader.getString("age"));
                params.put("(post)", leader.getString("belongPost"));
                params.put("(task)", leader.getString("taskDivision"));
                params.put("(rate)", leader.getString("workRate"));
                params.put("(unit)", leader.getString("belongUnit"));
            }else {
                params.put("(name)", "");
                params.put("(sax)", "");
                params.put("(age)", "");
                params.put("(post)", "");
                params.put("(task)", "");
                params.put("(rate)", "");
                params.put("(unit)", "");
            }

            //替换掉文档中对应的字段
            xwpfTUtil.replaceInPara(doc, params);

            ExportWordHelper exportWordHelper = new ExportWordHelper();
            //4、进度计划
            List<List<String>> progressList = new ArrayList<>();
            if(!CollectionUtils.isEmpty(progressPlanList)){
                for(PageData progress : progressPlanList){
                    List<String> progressDetailList = new ArrayList<>();
                    progressDetailList.add(progress.getString("years"));
                    progressDetailList.add(progress.getString("planTarget"));

                    progressList.add(progressDetailList);

                }

            }

            exportWordHelper.exportWordDisorder(progressList, doc, 0);


            //5、参加单位
            List<List<String>> unitList = new ArrayList<>();
            if(!CollectionUtils.isEmpty(attendUnitList)){
                for(PageData unit : attendUnitList){
                    List<String> unitDetailList = new ArrayList<>();
                    unitDetailList.add(unit.getString("unitName"));
                    unitDetailList.add(unit.getString("taskDivision"));

                    unitList.add(unitDetailList);

                }

            }

            exportWordHelper.exportWordDisorder(unitList, doc, 1);


            //6、研究人员
            List<List<String>> userList = new ArrayList<>();
            if(!CollectionUtils.isEmpty(researchUserList)){
                for(PageData user : researchUserList){
                    List<String> userDetailList = new ArrayList<>();
                    userDetailList.add(user.getString("userName"));
                    userDetailList.add(user.getString("gender"));
                    userDetailList.add(user.getString("age"));
                    userDetailList.add(user.getString("belongPost"));
                    userDetailList.add(user.getString("taskDivision"));
                    userDetailList.add(user.getString("workRate"));
                    userDetailList.add(user.getString("belongUnit"));

                    userList.add(userDetailList);

                }

            }

            exportWordHelper.exportWordDisorder(userList, doc, 2);

            //7、经费预算
            List<List<String>> budgetList1 = new ArrayList<>();
            if(!CollectionUtils.isEmpty(budgetList)){
                for(PageData budget : budgetList){
                    List<String> budgetDetailList = new ArrayList<>();
                    budgetDetailList.add(budget.getString("sourceAccount"));
                    budgetDetailList.add(budget.getString("sourceBudget"));
                    budgetDetailList.add(budget.getString("expenseAccount"));
                    budgetDetailList.add(budget.getString("expenseBudget"));

                    budgetList1.add(budgetDetailList);

                }

            }

            exportWordHelper.exportWordDisorder(budgetList1, doc, 3);



            //8、拨款计划
            List<List<String>> appList = new ArrayList<>();
            if(!CollectionUtils.isEmpty(appropriationPlanList)){
                for(PageData budget : appropriationPlanList){
                    List<String> appDetailList = new ArrayList<>();
                    appDetailList.add(budget.getString("years"));
                    appDetailList.add(budget.getString("planAmount"));

                    appList.add(appDetailList);

                }

            }

            exportWordHelper.exportWordDisorder(appList, doc, 4);


            String number = SerialNumberUtil.generateSerialNo("projectApplyPreview");

            String fileName = "研究与开发项目合同" + "_" + new SimpleDateFormat("yyyyMMdd").format(new Date())+"_"+number;
            File newWordTempFile = new File(file, fileName + ".docx");
            String newWordTempFilePath = newWordTempFile.getCanonicalPath();
            File pdfTempFile = new File(file, fileName + ".pdf");
            String pdfTempFilePath = pdfTempFile.getCanonicalPath();

            //下载到临时word文件中 
            FileOutputStream os = new FileOutputStream(newWordTempFilePath);
            doc.write(os);
            xwpfTUtil.close(is);
            xwpfTUtil.close(os);
            //将word转为pdf
            Doc2Pdf.doc2pdf(newWordTempFilePath, pdfTempFilePath);
            //输出pdf文件
            Word2pdfUtil.fileResponse(pdfTempFilePath, response);
            //删除临时文件
            newWordTempFile.delete();
            pdfTempFile.delete();

        } catch (Exception e) {
            xwpfTUtil.close(is);
            throw new MyException("研究与开发项目合同导出异常");

        }
    }


    /**
     * 导出压缩包
     * @param flag 文件类型标识 1:excel 2:pdf 3:word
     * @param businessIdList
     * @param zos
     * @param bos
     * @param filePrefix
     * @throws Exception
     */
    @Override
    public void exportZip(int flag,List<String> businessIdList, ZipOutputStream zos, ByteArrayOutputStream bos, String filePrefix) throws Exception {

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
        XwpfTUtil xwpfTUtil = new XwpfTUtil();

        if(flag == 1){//导出excel

            for(String businessId : businessIdList){
                HSSFWorkbook wb = exportExcel(businessId);
                String number = SerialNumberUtil.generateSerialNo("projectApplyExcel");
                //文件名
                String fileName = filePrefix + date + "_" + number + ".xls";
                //重点开始,创建压缩文件
                //后端生成文件,可直接put
                ZipEntry z = new ZipEntry(fileName);
                zos.putNextEntry(z);
                wb.write(zos);
            }


        }else if(flag == 2){//导出pdf

            for(String businessId : businessIdList){

                String number = SerialNumberUtil.generateSerialNo("projectApplyPdf");

                String wordFileName = filePrefix +date+"_"+number+ ".docx";
                String pdfFileName = filePrefix +date+"_"+number+".pdf";

                File file = FileUtil.createFile();
                XWPFDocument doc = null;
                InputStream is = null;

                try {
                    is = this.getClass().getResourceAsStream("/template/研发项目立项申请信息.docx");
                    doc = new XWPFDocument(is);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    File newWordTempFile = packageWord( businessId, doc, is,wordFileName,file);

                    String newWordTempFilePath = newWordTempFile.getCanonicalPath();
                    File pdfTempFile = new File(file, pdfFileName);
                    String pdfTempFilePath = pdfTempFile.getCanonicalPath();

                    //下载到临时word文件中 
                    FileOutputStream os = new FileOutputStream(newWordTempFilePath);
                    doc.write(os);
                    xwpfTUtil.close(is);
                    xwpfTUtil.close(os);
                    //将word转为pdf
                    Doc2Pdf.doc2pdf(newWordTempFilePath, pdfTempFilePath);

                    //将数据写入到zip流中
                    //已读出图片
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(getBytes(pdfTempFilePath));
                    DownloadUtil.zipFile(pdfFileName, inputStream, zos);
                    inputStream.close();
                    //删除临时文件
                    newWordTempFile.delete();
                    pdfTempFile.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }



        }else if(flag == 3){//导出word
            for(String businessId : businessIdList){

                String number = SerialNumberUtil.generateSerialNo("projectApplyWord");
                String wordFileName = filePrefix +date+"_"+number+ ".docx";

                File file = FileUtil.createFile();
                XWPFDocument doc = null;
                InputStream is = null;

                try {
                    is = this.getClass().getResourceAsStream("/template/研发项目立项申请信息.docx");
                    doc = new XWPFDocument(is);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    File newWordTempFile = packageWord( businessId, doc, is,wordFileName,file);

                    String newWordTempFilePath = newWordTempFile.getCanonicalPath();
                    //下载到临时word文件中 
                    FileOutputStream os = new FileOutputStream(newWordTempFilePath);
                    doc.write(os);
                    xwpfTUtil.close(is);
                    xwpfTUtil.close(os);

                    //将数据写入到zip流中
                    //已读出图片
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(getBytes(newWordTempFilePath));
                    DownloadUtil.zipFile(wordFileName, inputStream, zos);
                    inputStream.close();
                    //删除临时文件
                    newWordTempFile.delete();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

    }


    private static byte[] getBytes(String filePath){
        ByteArrayOutputStream out = null;
        File file = new File(filePath);
        try {
            FileInputStream in = new FileInputStream(file);
            out = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int i=0;
            while ((i = in.read(b)) != -1){
                out.write(b,0,b.length);

            }
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }


        byte[] result = out.toByteArray();

        return result;

    }


    /**
     * 导出word或者pdf
     * @param  flag 文件标识 1：word 2:pdf
     * @param businessId
     */

    @Override
    public void exportWordPdf(int flag, String businessId,HttpServletResponse response,String filePrefix) {
        XwpfTUtil xwpfTUtil = new XwpfTUtil();
//        XWPFDocument doc = null;
//        InputStream is = null;
        File file = FileUtil.createFile();
        XWPFDocument doc = null;
        InputStream is = null;
        try {
            is = this.getClass().getResourceAsStream("/template/研发项目立项申请信息.docx");
            doc = new XWPFDocument(is);

            File newWordTempFile = packageWord( businessId, doc, is,filePrefix + ".docx",file);

            //  在默认文件夹下创建临时文件
            if(flag == 1){
                String newWordTempFilePath = newWordTempFile.getCanonicalPath();
                //下载到临时word文件中 
                FileOutputStream os = new FileOutputStream(newWordTempFilePath);
                doc.write(os);
                xwpfTUtil.close(is);
                xwpfTUtil.close(os);
                //输出pdf文件
                Word2pdfUtil.fileResponse(newWordTempFilePath, response);
                //删除临时文件
                newWordTempFile.delete();

            }else {
                String newWordTempFilePath = newWordTempFile.getCanonicalPath();
                File pdfTempFile = new File(file, filePrefix + ".pdf");
                String pdfTempFilePath = pdfTempFile.getCanonicalPath();

                //下载到临时word文件中 
                FileOutputStream os = new FileOutputStream(newWordTempFilePath);
                doc.write(os);
                xwpfTUtil.close(is);
                xwpfTUtil.close(os);
                //将word转为pdf
                Doc2Pdf.doc2pdf(newWordTempFilePath, pdfTempFilePath);
                //输出pdf文件
                Word2pdfUtil.fileResponse(pdfTempFilePath, response);
                //删除临时文件
                newWordTempFile.delete();
                pdfTempFile.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private File packageWord(String businessId,XWPFDocument doc,InputStream is,String fileName,File file){

        PageData businessData = new PageData();
        businessData.put("businessId", businessId);
        PageData data = getApplyDetail(businessData);
        List<PageData> progressPlan = (List<PageData>)data.get("progressPlan");
        List<PageData> attendUnit = (List<PageData>)data.get("attendUnit");
        List<PageData> researchUser = (List<PageData>)data.get("researchUser");
        List<PageData> budgetList = (List<PageData>)data.get("budgetList");
        List<PageData> appropriationPlan = (List<PageData>)data.get("appropriationPlan");

        XwpfTUtil xwpfTUtil = new XwpfTUtil();

        try {

            //获取文件路径

            //文本
            Map<String, Object> params = new HashMap<>();
            //1、主信息
            String year = data.getString("createdDate").substring(0,4);
            params.put("(year)", year);

            params.put("(projectName)", data.getString("projectName"));
            params.put("(unitName)", data.getString("unitName"));
            params.put("(unitAddress)", data.getString("unitAddress"));
            params.put("(zipCode)", data.getString("zipCode"));
            params.put("(applyUserName)", data.getString("applyUserName"));
            params.put("(gender)", data.getString("gender"));
            params.put("(age)", data.getString("age"));
            params.put("(postName)", data.getString("postName"));
            params.put("(telephone)", data.getString("telephone"));
            params.put("(applyAmount)", data.getString("applyAmount"));
            params.put("(startYear)", data.getString("startYear"));
            params.put("(endYear)", data.getString("endYear"));
            params.put("(professionalCategory)", data.getString("professionalCategory"));
            params.put("(researchContents)", data.getString("researchContents"));
            params.put("(reviewComments)", data.getString("reviewComments"));

            //2、调研信息
            params.put("(currentSituation)", data.getString("currentSituation"));
            params.put("(purposeSignificance)", data.getString("purposeSignificance"));
            params.put("(contentMethod)", data.getString("contentMethod"));
            params.put("(targetResults)", data.getString("targetResults"));
            params.put("(basicConditions)", data.getString("basicConditions"));

            //3、项目负责人
            if(!CollectionUtils.isEmpty(researchUser)){
                PageData leader = researchUser.get(0);
                params.put("(projectLeader)", leader.getString("userName"));
                params.put("(leaderAge)", leader.getString("age"));
                params.put("(leaderPost)", leader.getString("belongPost"));
                params.put("(leaderAction)", leader.getString("taskDivision"));
            }else {
                params.put("(projectLeader)", "");
                params.put("(leaderAge)", "");
                params.put("(leaderPost)", "");
                params.put("(leaderAction)", "");
            }

            //替换掉文档中对应的字段
            xwpfTUtil.replaceInPara(doc, params);

            ExportWordHelper exportWordHelper = new ExportWordHelper();
            //4、进度计划
            List<List<String>> progressList = new ArrayList<>();
            if(!CollectionUtils.isEmpty(progressPlan)){
                for(PageData progress : progressPlan){
                    List<String> progressDetailList = new ArrayList<>();
                    progressDetailList.add(progress.getString("years"));
                    progressDetailList.add(progress.getString("planTarget"));

                    progressList.add(progressDetailList);

                }

            }

            exportWordHelper.exportWordDisorder(progressList, doc, 2);


            //5、参加单位
            List<List<String>> unitList = new ArrayList<>();
            if(!CollectionUtils.isEmpty(attendUnit)){
                for(PageData unit : attendUnit){
                    List<String> unitDetailList = new ArrayList<>();
                    unitDetailList.add(unit.getString("unitName"));
                    unitDetailList.add(unit.getString("taskDivision"));

                    unitList.add(unitDetailList);

                }

            }

            exportWordHelper.exportWordDisorder(unitList, doc, 3);


            //6、研究人员
            List<List<String>> userList = new ArrayList<>();
            if(!CollectionUtils.isEmpty(researchUser)){
                for(int i=0;i<researchUser.size();i++){
                    PageData user = researchUser.get(i);
                    List<String> userDetailList = new ArrayList<>();
                    int number = i+1;
                    userDetailList.add(String.valueOf(number));
                    userDetailList.add(user.getString("userName"));
                    userDetailList.add(user.getString("age"));
                    userDetailList.add(user.getString("belongPost"));
                    userDetailList.add(user.getString("taskDivision"));

                    userList.add(userDetailList);

                }

            }

            exportWordHelper.exportWordDisorder(userList, doc, 4);

            //7、经费预算
            List<List<String>> budgetList1 = new ArrayList<>();
            if(!CollectionUtils.isEmpty(budgetList)){
                for(PageData budget : budgetList){
                    List<String> budgetDetailList = new ArrayList<>();
                    budgetDetailList.add(budget.getString("sourceAccount"));
                    budgetDetailList.add(budget.getString("sourceBudget"));
                    budgetDetailList.add(budget.getString("expenseAccount"));
                    budgetDetailList.add(budget.getString("expenseBudget"));

                    budgetList1.add(budgetDetailList);

                }

            }

            exportWordHelper.exportWordDisorder(budgetList1, doc, 5);



            //8、拨款计划
            List<List<String>> appList = new ArrayList<>();
            if(!CollectionUtils.isEmpty(appropriationPlan)){
                for(PageData budget : appropriationPlan){
                    List<String> appDetailList = new ArrayList<>();
                    appDetailList.add(budget.getString("years"));
                    appDetailList.add(budget.getString("planAmount"));

                    appList.add(appDetailList);

                }

            }

            exportWordHelper.exportWordDisorder(appList, doc, 6);


            //9、审批记录
            List<List<String>> approveList = new ArrayList<>();
            List<PageData> approveLists = (List<PageData>) dao.findForList("ProjectApplyMapper.queryApproveById",data);
            if(!CollectionUtils.isEmpty(approveLists)){
                for(PageData approve : approveLists){
                    List<String> approveDetailList = new ArrayList<>();
                    approveDetailList.add(approve.getString("approveNodeName"));
                    approveDetailList.add(approve.getString("approveUserName"));
                    approveDetailList.add(approve.getString("approveResultName"));
                    approveDetailList.add(approve.getString("approveComment"));
                    approveDetailList.add(approve.getString("approveEndTime"));
                    approveDetailList.add(approve.getString("nextApproveNodeName"));
                    approveDetailList.add(approve.getString("nextApproveUserName"));

                    approveList.add(approveDetailList);

                }

            }


            exportWordHelper.exportWordDisorder(approveList, doc, 7);

            File newWordTempFile = new File(file, fileName);

            return newWordTempFile;

        } catch (Exception e) {
            xwpfTUtil.close(is);
            throw new MyException("立项申请导出异常");

        }
    }


    /**
     * 导出Excel
     *
     * @param businessId
     * @return
     */
    @Override
    public HSSFWorkbook exportExcel(String businessId) {
        //查询单据的所有数据
        PageData pd = new PageData();
        pd.put("businessId",businessId);
        PageData data = getApplyDetail(pd);

        HSSFWorkbook wb = new HSSFWorkbook();

        //1、写入主表信息
        HSSFSheet mainSheet = wb.createSheet();
        setMainData(wb, mainSheet, data);
        wb.setSheetName(0, "主信息");

        //2、写入立项调研信息
        HSSFSheet surveySheet = wb.createSheet();
        setSurveyData(wb, surveySheet, data);
        wb.setSheetName(1, "立项调研信息");

        //2、写入进度计划
        HSSFSheet progressSheet = wb.createSheet();
        setProgressData(wb, progressSheet, data);
        wb.setSheetName(2, "进度计划");

        //3、写入参加单位
        HSSFSheet unitSheet = wb.createSheet();
        setUnitData(wb, unitSheet, data);
        wb.setSheetName(3, "参加单位");

        //4、写入研究人员（初始）
        HSSFSheet userSheet = wb.createSheet();
        setUserData(wb, userSheet, data);
        wb.setSheetName(4, "研究人员（初始）");

        //5、写入研究人员（初始）
        HSSFSheet userChangeSheet = wb.createSheet();
        setUserChangeData(wb, userChangeSheet, data);
        wb.setSheetName(5, "研究人员（变更）");

        //6、写入经费预算
        HSSFSheet budgetSheet = wb.createSheet();
        setBudgetData(wb, budgetSheet, data);
        wb.setSheetName(6, "经费预算");

        //7、写入每月预算
        HSSFSheet monthSheet = wb.createSheet();
        setMonthData(wb, monthSheet, data);
        wb.setSheetName(7, "每月预算");

        //8、写入拨款计划
        HSSFSheet appSheet = wb.createSheet();
        setAppData(wb, appSheet, data);
        wb.setSheetName(8, "拨款计划");


        return wb;
    }


    /**
     * 写入主表信息
     * @param wb
     * @param sheet
     * @param pd
     */
    private void setMainData(HSSFWorkbook wb,HSSFSheet sheet,PageData pd){
        HSSFCellStyle styleHeader = ExcelUtil.setHeader(wb, sheet);// 表头
        HSSFCellStyle styleCell = ExcelUtil.setWrapCell(wb, sheet);// 单元格


        // 一、标题
        HSSFRow rows = sheet.createRow(0);
        rows.setHeightInPoints(20);// 行高
        HSSFCell cells = rows.createCell(0);
        String year = pd.getString("createdDate").substring(0,4);
        cells.setCellValue(year + "年度公司科技研究开发计划课题申请表");
        cells.setCellStyle(styleHeader);

        ExcelUtil.merge(wb, sheet, 0, 0, 0, 10);

        //1、项目名称
        rows = sheet.createRow(1);
        cells = rows.createCell(0);
        cells.setCellValue("项目名称");
        cells.setCellStyle(styleHeader);

        cells = rows.createCell(1);
        cells.setCellValue(pd.getString("projectName"));
        cells.setCellStyle(styleCell);
        ExcelUtil.merge(wb, sheet, 1, 1, 1, 10);

        //2、单位名称
        rows = sheet.createRow(2);
        cells = rows.createCell(0);
        cells.setCellValue("项目名称");
        cells.setCellStyle(styleHeader);

        cells = rows.createCell(1);
        cells.setCellValue(pd.getString("unitName"));
        cells.setCellStyle(styleCell);
        ExcelUtil.merge(wb, sheet, 2, 2, 1, 10);

        //3、单位地址
        rows = sheet.createRow(3);
        cells = rows.createCell(0);
        cells.setCellValue("单位地址");
        cells.setCellStyle(styleHeader);

        cells = rows.createCell(1);
        cells.setCellValue(pd.getString("unitAddress"));
        cells.setCellStyle(styleCell);
        ExcelUtil.merge(wb, sheet, 3, 3, 1, 8);

        //4、邮编
        cells = rows.createCell(9);
        cells.setCellValue("邮编");
        cells.setCellStyle(styleHeader);

        cells = rows.createCell(10);
        cells.setCellValue(pd.getString("zipCode"));
        cells.setCellStyle(styleCell);

        //5、申请人姓名
        rows = sheet.createRow(4);
        cells = rows.createCell(0);
        cells.setCellValue("申请人姓名");
        cells.setCellStyle(styleHeader);

        cells = rows.createCell(1);
        cells.setCellValue(pd.getString("applyUserName"));
        cells.setCellStyle(styleCell);
        ExcelUtil.merge(wb, sheet, 4, 4, 1, 2);
        //6、性别
        cells = rows.createCell(3);
        cells.setCellValue("性别");
        cells.setCellStyle(styleHeader);

        cells = rows.createCell(4);
        cells.setCellValue(pd.getString("gender"));
        cells.setCellStyle(styleCell);

        //7、年龄
        cells = rows.createCell(5);
        cells.setCellValue("年龄");
        cells.setCellStyle(styleHeader);

        cells = rows.createCell(6);
        cells.setCellValue(pd.getString("age"));
        cells.setCellStyle(styleCell);

        //8、职称
        cells = rows.createCell(7);
        cells.setCellValue("职称");
        cells.setCellStyle(styleHeader);

        cells = rows.createCell(8);
        cells.setCellValue(pd.getString("postName"));
        cells.setCellStyle(styleCell);

        //9、电话
        cells = rows.createCell(9);
        cells.setCellValue("电话");
        cells.setCellStyle(styleHeader);

        cells = rows.createCell(10);
        cells.setCellValue(pd.getString("telephone"));
        cells.setCellStyle(styleCell);

        //10、申请经费（万元）
        rows = sheet.createRow(5);
        cells = rows.createCell(0);
        cells.setCellValue("申请经费（万元）");
        cells.setCellStyle(styleHeader);

        cells = rows.createCell(1);
        cells.setCellValue(pd.getString("applyAmount"));
        cells.setCellStyle(styleCell);
        ExcelUtil.merge(wb, sheet, 5, 5, 1, 2);

        //11、起始年度
        cells = rows.createCell(3);
        cells.setCellValue("起始年度");
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 5, 5, 3, 5);

        cells = rows.createCell(6);
        cells.setCellValue(pd.getString("startYear"));
        cells.setCellStyle(styleCell);
        ExcelUtil.merge(wb, sheet, 5, 5, 6, 7);

        //12、结束年度
        cells = rows.createCell(8);
        cells.setCellValue("结束年度");
        cells.setCellStyle(styleHeader);

        cells = rows.createCell(9);
        cells.setCellValue(pd.getString("endYear"));
        cells.setCellStyle(styleCell);
        ExcelUtil.merge(wb, sheet, 5, 5, 9, 10);

        //13、专业类别
        rows = sheet.createRow(6);
        cells = rows.createCell(0);
        cells.setCellValue("专业类别");
        cells.setCellStyle(styleHeader);

        cells = rows.createCell(1);
        cells.setCellValue(pd.getString("professionalCategory"));
        cells.setCellStyle(styleCell);
        ExcelUtil.merge(wb, sheet, 6, 6, 1, 10);

        //14、项目类型
        rows = sheet.createRow(7);
        cells = rows.createCell(0);
        cells.setCellValue("项目类型");
        cells.setCellStyle(styleHeader);

        cells = rows.createCell(1);
        cells.setCellValue(pd.getString("projectType"));
        cells.setCellStyle(styleCell);
        ExcelUtil.merge(wb, sheet, 7, 7, 1, 7);

        //15、是否进行成果鉴定
        cells = rows.createCell(8);
        cells.setCellValue("是否进行成果鉴定");
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 7, 7, 8, 9);

        cells = rows.createCell(10);
        cells.setCellValue(pd.getString("identify").equals("0")?"否":"是");
        cells.setCellStyle(styleCell);

        //16、研究内容提要
        rows = sheet.createRow(8);
        cells = rows.createCell(0);
        cells.setCellValue("研究内容提要");
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 8, 8, 0, 10);

        //
        rows = sheet.createRow(9);
        rows.setHeightInPoints(50);
        cells = rows.createCell(0);
        cells.setCellValue(pd.getString("researchContents"));
        cells.setCellStyle(styleCell);
        ExcelUtil.merge(wb, sheet, 9, 10, 0, 10);

        //17、申报单位审查意见：
        rows = sheet.createRow(11);
        rows.setHeightInPoints(80);
        cells = rows.createCell(0);
        cells.setCellValue("研究内容提要");
        cells.setCellStyle(styleHeader);

        //
        cells = rows.createCell(1);
        cells.setCellValue(pd.getString("reviewComments"));
        cells.setCellStyle(styleCell);
        ExcelUtil.merge(wb, sheet, 11, 11, 1, 6);

        //18、盖章：
        cells = rows.createCell(7);
        cells.setCellValue("盖章");
        cells.setCellStyle(styleHeader);

        cells = rows.createCell(8);
        cells.setCellValue("");
        cells.setCellStyle(styleCell);
        ExcelUtil.merge(wb, sheet, 11, 11, 8, 10);

        sheet.setAutobreaks(true);
    }

    /**
     * 写入立项调研信息
     * @param wb
     * @param sheet
     * @param pd
     */
    private void setSurveyData(HSSFWorkbook wb,HSSFSheet sheet,PageData pd){
        HSSFCellStyle styleHeader = ExcelUtil.setHeaderLeft(wb, sheet);// 表头
        HSSFCellStyle styleCell = ExcelUtil.setWrapCell(wb, sheet);// 单元格

        // 一、国内外现状
        HSSFRow rows = sheet.createRow(0);
        rows.setHeightInPoints(20);// 行高
        HSSFCell cells = rows.createCell(0);
        cells.setCellValue("一、国内外现状");
        cells.setCellStyle(styleHeader);

        rows = sheet.createRow(1);
        cells = rows.createCell(0);
        cells.setCellValue(pd.getString("currentSituation"));
        cells.setCellStyle(styleCell);

        // 二、研发目的和意义
        rows = sheet.createRow(2);
        rows.setHeightInPoints(20);// 行高
        cells = rows.createCell(0);
        cells.setCellValue("二、研发的目的和意义");
        cells.setCellStyle(styleHeader);

        rows = sheet.createRow(3);
        cells = rows.createCell(0);
        cells.setCellValue(pd.getString("purposeSignificance"));
        cells.setCellStyle(styleCell);


        // 三、主要研究内容及研究方法
        rows = sheet.createRow(4);
        rows.setHeightInPoints(20);// 行高
        cells = rows.createCell(0);
        cells.setCellValue("三、主要研究内容及研究方法");
        cells.setCellStyle(styleHeader);

        rows = sheet.createRow(5);
        cells = rows.createCell(0);
        cells.setCellValue(pd.getString("contentMethod"));
        cells.setCellStyle(styleCell);


        // 四、要达到的目标、成果形式及主要技术指标
        rows = sheet.createRow(6);
        rows.setHeightInPoints(20);// 行高
        cells = rows.createCell(0);
        cells.setCellValue("四、要达到的目标、成果形式及主要技术指标");
        cells.setCellStyle(styleHeader);

        rows = sheet.createRow(7);
        cells = rows.createCell(0);
        cells.setCellValue(pd.getString("targetResults"));
        cells.setCellStyle(styleCell);


        // 五、现有研发条件和工作基础
        rows = sheet.createRow(8);
        rows.setHeightInPoints(20);// 行高
        cells = rows.createCell(0);
        cells.setCellValue("五、现有研发条件和工作基础");
        cells.setCellStyle(styleHeader);

        rows = sheet.createRow(9);
        cells = rows.createCell(0);
        cells.setCellValue(pd.getString("basicConditions"));
        cells.setCellStyle(styleCell);


        // 六、研发项目创新点
        rows = sheet.createRow(10);
        rows.setHeightInPoints(20);// 行高
        cells = rows.createCell(0);
        cells.setCellValue("六、研发项目创新点");
        cells.setCellStyle(styleHeader);

        rows = sheet.createRow(11);
        cells = rows.createCell(0);
        cells.setCellValue(pd.getString("innovationPoints"));
        cells.setCellStyle(styleCell);


        // 七、成果转化的可行性分析
        rows = sheet.createRow(12);
        rows.setHeightInPoints(20);// 行高
        cells = rows.createCell(0);
        cells.setCellValue("七、成果转化的可行性分析");
        cells.setCellStyle(styleHeader);

        rows = sheet.createRow(13);
        cells = rows.createCell(0);
        cells.setCellValue(pd.getString("feasibilityAnalysis"));
        cells.setCellStyle(styleCell);

        sheet.setColumnWidth(0,20000);
        sheet.setAutobreaks(true);
    }

    /**
     * 写入进度计划
     * @param wb
     * @param sheet
     * @param pd
     */
    private void setProgressData(HSSFWorkbook wb,HSSFSheet sheet,PageData pd){

        String title = "八、研发项目立项申请-进度计划";

        String[] head = {"年度","计划及目标"};
        HSSFCellStyle styleTitle = ExcelUtil.setHeaderLeft(wb, sheet);// 标题
        HSSFCellStyle styleHeader = ExcelUtil.setHeader(wb, sheet);// 单元格
        HSSFCellStyle styleCell = ExcelUtil.setCell(wb, sheet);// 单元格
        // 创建表头
        HSSFRow rows = sheet.createRow(0);
        rows.setHeightInPoints(20);// 行高
        HSSFCell cells = rows.createCell(0);
        cells.setCellValue(title);
        cells.setCellStyle(styleTitle);
        ExcelUtil.merge(wb, sheet, 0, 0, 0, (head.length - 1));
        // 第一行  表头
        HSSFRow rowTitle = sheet.createRow(1);
        HSSFCell hc;
        for (int j = 0; j < head.length; j++) {
            hc = rowTitle.createCell(j);
            hc.setCellValue(head[j]);
            hc.setCellStyle(styleHeader);
        }

        List<PageData> planList = (List<PageData>) pd.get("progressPlan");
        if (!CollectionUtils.isEmpty(planList)) {
            for (int i = 0; i < planList.size(); i++) {
                PageData data = planList.get(i);

                HSSFRow row = sheet.createRow(i + 2);
                int j = 0;

                HSSFCell cell = row.createCell(j++);
                cell.setCellValue(data.getString("years"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(data.getString("planTarget"));
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
    }

    /**
     * 写入初始人员
     * @param wb
     * @param sheet
     * @param pd
     */
    private void setUserData(HSSFWorkbook wb,HSSFSheet sheet,PageData pd){
        String title = "九、9.2研发项目立项申请-研究人员 （初始）";
        List<PageData> list = (List<PageData>) pd.get("researchUser");
        setUser(wb,sheet,list,title);

    }

    /**
     * 写入参与单位
     * @param wb
     * @param sheet
     * @param pd
     */
    private void setUnitData(HSSFWorkbook wb,HSSFSheet sheet,PageData pd){

        String title = "九、9.1研发项目立项申请-参加单位";

        String[] head = {"参加单位","研究任务及分工"};
        HSSFCellStyle styleTitle = ExcelUtil.setHeaderLeft(wb, sheet);// 标题
        HSSFCellStyle styleHeader = ExcelUtil.setHeader(wb, sheet);// 单元格
        HSSFCellStyle styleCell = ExcelUtil.setCell(wb, sheet);// 单元格
        // 创建表头
        HSSFRow rows = sheet.createRow(0);
        rows.setHeightInPoints(20);// 行高
        HSSFCell cells = rows.createCell(0);
        cells.setCellValue(title);
        cells.setCellStyle(styleTitle);
        ExcelUtil.merge(wb, sheet, 0, 0, 0, (head.length - 1));
        // 第一行  表头
        HSSFRow rowTitle = sheet.createRow(1);
        HSSFCell hc;
        for (int j = 0; j < head.length; j++) {
            hc = rowTitle.createCell(j);
            hc.setCellValue(head[j]);
            hc.setCellStyle(styleHeader);
        }

        List<PageData> attendUnit = (List<PageData>) pd.get("attendUnit");
        if (!CollectionUtils.isEmpty(attendUnit)) {
            for (int i = 0; i < attendUnit.size(); i++) {
                PageData data = attendUnit.get(i);

                HSSFRow row = sheet.createRow(i + 2);
                int j = 0;

                HSSFCell cell = row.createCell(j++);
                cell.setCellValue(data.getString("unitName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(data.getString("taskDivision"));
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
    }

    /**
     * 写入主表信息（变更）
     * @param wb
     * @param sheet
     * @param pd
     */
    private void setUserChangeData(HSSFWorkbook wb,HSSFSheet sheet,PageData pd){
        String title = "九、9.2研发项目立项申请-研究人员 （变更）";
        List<PageData> list = (List<PageData>) pd.get("researchUserChange");
        setUser(wb,sheet,list,title);
    }



    private void setUser(HSSFWorkbook wb,HSSFSheet sheet,List<PageData> list,String title){

        String[] head = {"序号","姓名","身份证号码","年龄","性别","学历","所属部门","职务职称","所学专业","现从事专业","所在单位","研究任务及分工","全时率","联系电话","参与研发开始日期","参与研发结束日期"};
        HSSFCellStyle styleTitle = ExcelUtil.setHeaderLeft(wb, sheet);// 标题
        HSSFCellStyle styleHeader = ExcelUtil.setHeader(wb, sheet);// 单元格
        HSSFCellStyle styleCell = ExcelUtil.setCell(wb, sheet);// 单元格
        // 创建表头
        HSSFRow rows = sheet.createRow(0);
        rows.setHeightInPoints(20);// 行高
        HSSFCell cells = rows.createCell(0);
        cells.setCellValue(title);
        cells.setCellStyle(styleTitle);
        ExcelUtil.merge(wb, sheet, 0, 0, 0, (head.length - 1));
        // 第一行  表头
        HSSFRow rowTitle = sheet.createRow(1);
        HSSFCell hc;
        for (int j = 0; j < head.length; j++) {
            hc = rowTitle.createCell(j);
            hc.setCellValue(head[j]);
            hc.setCellStyle(styleHeader);
        }


        if (!CollectionUtils.isEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                PageData data = list.get(i);

                HSSFRow row = sheet.createRow(i + 2);
                int j = 0;

                HSSFCell cell = row.createCell(j++);
                cell.setCellValue(i + 1);
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(data.getString("userName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(data.getString("idCard"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(data.getString("age"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(data.getString("gender"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(data.getString("education"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(data.getString("belongDepartment"));
                cell.setCellStyle(styleCell);


                cell = row.createCell(j++);
                cell.setCellValue(data.getString("belongPost"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(data.getString("majorStudied"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(data.getString("majorWorked"));
                cell.setCellStyle(styleCell);


                cell = row.createCell(j++);
                cell.setCellValue(data.getString("belongUnit"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(data.getString("taskDivision"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(data.getString("workRate"));
                cell.setCellStyle(styleCell);


                cell = row.createCell(j++);
                cell.setCellValue(data.getString("telephone"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(data.getString("startDate"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(data.getString("endDate"));
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
    }

    /**
     * 写入经费预算
     * @param wb
     * @param sheet
     * @param pd
     */
    private void setBudgetData(HSSFWorkbook wb,HSSFSheet sheet,PageData pd){

        String title = "十、10.1研发项目立项申请-经费预算（单位：万元）";
        List<PageData> budgetList = (List<PageData>) pd.get("budgetList");
        //判断是不是有预算变更
        //bool=true有变更 bool=false没有变更
        Boolean bool = false;
        if(!CollectionUtils.isEmpty(budgetList)){
            for(PageData data : budgetList){
                String sourceBudgetChange = data.getString("sourceBudgetChange");
                String expenseBudgetChange = data.getString("expenseBudgetChange");
                if(StringUtils.isNotBlank(sourceBudgetChange) || StringUtils.isNotBlank(expenseBudgetChange)){
                    bool = true;

                    break;
                }
            }
        }

        if(bool == false){
            String[] head = {"经费来源预算","经费来源预算","经费支出预算","经费支出预算"};
            HSSFCellStyle styleTitle = ExcelUtil.setHeaderLeft(wb, sheet);// 标题
            HSSFCellStyle styleHeader = ExcelUtil.setHeader(wb, sheet);// 单元格
            HSSFCellStyle styleCell = ExcelUtil.setCell(wb, sheet);// 单元格
            // 创建表头
            HSSFRow rows = sheet.createRow(0);
            rows.setHeightInPoints(20);// 行高
            HSSFCell cells = rows.createCell(0);
            cells.setCellValue(title);
            cells.setCellStyle(styleTitle);
            ExcelUtil.merge(wb, sheet, 0, 0, 0, (head.length - 1));
            // 第一行  表头
            HSSFRow rowTitle = sheet.createRow(1);
            HSSFCell hc;
            for (int j = 0; j < head.length; j++) {
                hc = rowTitle.createCell(j);
                hc.setCellValue(head[j]);
                hc.setCellStyle(styleHeader);
            }
            ExcelUtil.merge(wb, sheet, 1, 1, 0, 1);
            ExcelUtil.merge(wb, sheet, 1, 1, 2, 3);

            String[] head2 = {"科目","预算数","科目","预算数"};

            // 第二行  表头
            rowTitle = sheet.createRow(2);
            for (int j = 0; j < head2.length; j++) {
                hc = rowTitle.createCell(j);
                hc.setCellValue(head2[j]);
                hc.setCellStyle(styleHeader);
            }

            if (!CollectionUtils.isEmpty(budgetList)) {
                for (int i = 0; i < budgetList.size(); i++) {
                    PageData data = budgetList.get(i);

                    HSSFRow row = sheet.createRow(i + 3);
                    int j = 0;

                    HSSFCell cell = row.createCell(j++);
                    cell.setCellValue(data.getString("sourceAccount"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("sourceBudget"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("expenseAccount"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("expenseBudget"));
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

        }else {
            String[] head = {"经费来源预算","经费来源预算","经费来源预算","经费支出预算","经费支出预算","经费支出预算"};
            HSSFCellStyle styleTitle = ExcelUtil.setHeaderLeft(wb, sheet);// 标题
            HSSFCellStyle styleHeader = ExcelUtil.setHeader(wb, sheet);// 单元格
            HSSFCellStyle styleCell = ExcelUtil.setCell(wb, sheet);// 单元格
            // 创建表头
            HSSFRow rows = sheet.createRow(0);
            rows.setHeightInPoints(20);// 行高
            HSSFCell cells = rows.createCell(0);
            cells.setCellValue(title);
            cells.setCellStyle(styleTitle);
            ExcelUtil.merge(wb, sheet, 0, 0, 0, (head.length - 1));
            // 第一行  表头
            HSSFRow rowTitle = sheet.createRow(1);
            HSSFCell hc;
            for (int j = 0; j < head.length; j++) {
                hc = rowTitle.createCell(j);
                hc.setCellValue(head[j]);
                hc.setCellStyle(styleHeader);
            }
            ExcelUtil.merge(wb, sheet, 1, 1, 0, 2);
            ExcelUtil.merge(wb, sheet, 1, 1, 3, 5);

            String[] head2 = {"科目","预算数","变更数","科目","预算数","变更数"};

            // 第二行  表头
            rowTitle = sheet.createRow(2);
            for (int j = 0; j < head2.length; j++) {
                hc = rowTitle.createCell(j);
                hc.setCellValue(head2[j]);
                hc.setCellStyle(styleHeader);
            }


            if (!CollectionUtils.isEmpty(budgetList)) {
                for (int i = 0; i < budgetList.size(); i++) {
                    PageData data = budgetList.get(i);

                    HSSFRow row = sheet.createRow(i + 3);
                    int j = 0;

                    HSSFCell cell = row.createCell(j++);
                    cell.setCellValue(data.getString("sourceAccount"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("sourceBudget"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("sourceBudgetChange"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("expenseAccount"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("expenseBudget"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("expenseBudgetChange"));
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

        }


        sheet.setAutobreaks(true);
    }

    /**
     * 写入经费预算=每月预算
     * @param wb
     * @param sheet
     * @param pd
     */
    private void setMonthData(HSSFWorkbook wb,HSSFSheet sheet,PageData pd){
        List<PageData> monthList = (List<PageData>) pd.get("monthList");
        //遍历出年份
        Set<String> yearSet = new TreeSet<>(new Comparator<String>(){
            @Override
            public int compare(String s1,String s2){
                return s1.compareTo(s2);
            }
        });

        if (!CollectionUtils.isEmpty(monthList)) {
            for (PageData detailData : monthList) {
                for (Object key : detailData.keySet()) {
                    String keyStr = (String)key;

                    if(keyStr.length() > 8 && StringUtils.isNumeric(keyStr.substring(5,9))){
                        yearSet.add(keyStr.substring(5,9));
                    }

                }
            }
        }

        int len = yearSet.size();
        String title = "十、10.2研发项目立项申请-经费预算（每月预算）（单位：万元）";

        HSSFCellStyle styleTitle = ExcelUtil.setHeaderLeft(wb, sheet);// 标题
        // 创建标题
        HSSFRow rows = sheet.createRow(0);
        rows.setHeightInPoints(20);// 行高
        HSSFCell cells = rows.createCell(0);
        cells.setCellValue(title);
        cells.setCellStyle(styleTitle);
        ExcelUtil.merge(wb, sheet, 0, 0, 0, 3+(12*len));

        //创建第一行表头

        HSSFCellStyle styleHeader = ExcelUtil.setHeader(wb, sheet);// 单元格

        String[] head = new String[ len * 12 +4];
        head[0] = "经费来源预算";
        head[1] = "经费来源预算";
        head[2] = "经费支出预算";
        head[3] = "经费支出预算";

        HSSFRow rowTitle = sheet.createRow(1);
        HSSFCell hc;
        if(!CollectionUtils.isEmpty(yearSet)){
            int count = 0;
            for (int i = 1; i <= 12; i++) {
                head[i + count + 3] = "年度预算（按月填报）";
                count++;
            }

        }

        for (int j = 0; j < head.length; j++) {
            hc = rowTitle.createCell(j);
            hc.setCellValue(head[j]);
            hc.setCellStyle(styleHeader);
        }

        ExcelUtil.merge(wb, sheet, 1, 1, 0, 1);
        ExcelUtil.merge(wb, sheet, 1, 1, 2, 3);
        ExcelUtil.merge(wb, sheet, 1, 1, 4, 3+(12*len));



        //创建第二行表头

        String[] head2 = new String[ len * 12 +4];
        head2[0] = "科目";
        head2[1] = "预算数";
        head2[2] = "科目";
        head2[3] = "预算数";


        if(!CollectionUtils.isEmpty(yearSet)){
            int count = 0;
            for(String year : yearSet){

                for(int i=0;i<12;i++) {
                    head2[count+4] = year+"年";
                    count ++;
                }
            }
        }

        rowTitle = sheet.createRow(2);
        for (int j = 0; j < head2.length; j++) {
            hc = rowTitle.createCell(j);
            hc.setCellValue(head2[j]);
            hc.setCellStyle(styleHeader);
        }

        if(!CollectionUtils.isEmpty(yearSet)){
            for(int i=0;i<yearSet.size();i++){
                ExcelUtil.merge(wb, sheet, 2, 2, 4+(i*12), 15+(i*12));
            }

        }

        // 第三行  表头
        String[] head3 = new String[ len * 12 +4];
        head3[0] = "科目";
        head3[1] = "预算数";
        head3[2] = "科目";
        head3[3] = "预算数";

        String[] months = {"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"};
        if(!CollectionUtils.isEmpty(yearSet)){
            for(int j=0;j<yearSet.size();j++){
                for(int i=0;i<12;i++) {
                    head3[4+(j*12)+i] = months[i];

                }
            }
        }

        rowTitle = sheet.createRow(3);

        for (int j = 0; j < head3.length; j++) {
            hc = rowTitle.createCell(j);
            hc.setCellValue(head3[j]);
            hc.setCellStyle(styleHeader);
        }
        ExcelUtil.merge(wb, sheet, 2, 3, 0, 0);
        ExcelUtil.merge(wb, sheet, 2, 3, 1, 1);
        ExcelUtil.merge(wb, sheet, 2, 3, 2, 2);
        ExcelUtil.merge(wb, sheet, 2, 3, 3, 3);

        HSSFCellStyle styleCell = ExcelUtil.setCell(wb, sheet);// 单元格

        if (!CollectionUtils.isEmpty(monthList)) {
            for (int i = 0; i < monthList.size(); i++) {
                PageData data = monthList.get(i);

                HSSFRow row = sheet.createRow(i + 4);
                int j = 0;

                HSSFCell cell = row.createCell(j++);
                cell.setCellValue(data.getString("sourceAccount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(data.getString("sourceBudget"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(data.getString("expenseAccount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(data.getString("expenseBudget"));
                cell.setCellStyle(styleCell);


                if(!CollectionUtils.isEmpty(yearSet)){

                    for(String year : yearSet){
                        for(int n=0;n<12;n++){
                            String key = "month"+year+""+(n+1);
                            String value = data.getString(key);
                            cell = row.createCell(j++);
                            cell.setCellValue(value);
                            cell.setCellStyle(styleCell);

                        }
                    }

                }
            }
        }



        //设置自适应宽度
        for (int j = 0; j < head3.length; j++) {
            sheet.autoSizeColumn(j);
            int colWidth = sheet.getColumnWidth(j) * 17 / 10;
            if (colWidth < 255 * 256) {
                sheet.setColumnWidth(j, colWidth < 3000 ? 3000 : colWidth);
            } else {
                sheet.setColumnWidth(j, 6000);
            }
        }

        sheet.setAutobreaks(true);

    }

    /**
     * 写入拨款计划
     * @param wb
     * @param sheet
     * @param pd
     */
    private void setAppData(HSSFWorkbook wb,HSSFSheet sheet,PageData pd){

        String title = "十一、研发项目立项申请-拨款计划";

        String[] head = {"年度","计划（万元）"};
        HSSFCellStyle styleTitle = ExcelUtil.setHeaderLeft(wb, sheet);// 标题
        HSSFCellStyle styleHeader = ExcelUtil.setHeader(wb, sheet);// 单元格
        HSSFCellStyle styleCell = ExcelUtil.setCell(wb, sheet);// 单元格
        // 创建表头
        HSSFRow rows = sheet.createRow(0);
        rows.setHeightInPoints(20);// 行高
        HSSFCell cells = rows.createCell(0);
        cells.setCellValue(title);
        cells.setCellStyle(styleTitle);
        ExcelUtil.merge(wb, sheet, 0, 0, 0, (head.length - 1));
        // 第一行  表头
        HSSFRow rowTitle = sheet.createRow(1);
        HSSFCell hc;
        for (int j = 0; j < head.length; j++) {
            hc = rowTitle.createCell(j);
            hc.setCellValue(head[j]);
            hc.setCellStyle(styleHeader);
        }

        List<PageData> appropriationPlan = (List<PageData>) pd.get("appropriationPlan");
        if (!CollectionUtils.isEmpty(appropriationPlan)) {
            for (int i = 0; i < appropriationPlan.size(); i++) {
                PageData data = appropriationPlan.get(i);

                HSSFRow row = sheet.createRow(i + 2);
                int j = 0;

                HSSFCell cell = row.createCell(j++);
                cell.setCellValue(data.getString("years"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(data.getString("planAmount"));
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
    }

}
