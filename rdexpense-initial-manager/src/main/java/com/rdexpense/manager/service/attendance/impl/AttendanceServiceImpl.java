package com.rdexpense.manager.service.attendance.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.*;
import com.rdexpense.manager.service.attendance.AttendanceService;
import com.rdexpense.manager.service.file.FileService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.LinkedMap;
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
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:11
 */
@Slf4j
@Service
public class AttendanceServiceImpl implements AttendanceService {


    @Autowired
    @Resource(name = "baseDao")
    private BaseDao dao;

    @Autowired
    private FileService fileService;



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
        pd.put("processStatus1", ConstantValUtil.APPROVAL_STATUS[3]);
        //处理日期搜索条件
        String createTime = pd.getString("createTime");
        if(StringUtils.isNotBlank(createTime)){
            JSONArray createTimeArr = JSONArray.parseArray(createTime);
            pd.put("beginCreateTime",createTimeArr.get(0) + " 00:00:00");
            pd.put("lastCreateTime",createTimeArr.get(1) + " 23:59:59");
        }

        String updateTime = pd.getString("updateTime");
        if(StringUtils.isNotBlank(updateTime)){
            JSONArray updateTimeArr = JSONArray.parseArray(updateTime);
            pd.put("beginUpdateTime",updateTimeArr.get(0) + " 00:00:00");
            pd.put("lastUpdateTime",updateTimeArr.get(1) + " 23:59:59");
        }


        List<PageData> list = (List<PageData>) dao.findForList("AttendanceMapper.queryByParams", pd);

        //查询本次单据的人员费，即每个单据的五险一金+该项目的工资合计 userCost
        if(!CollectionUtils.isEmpty(list)){
            List<PageData> costList = (List<PageData>) dao.findForList("AttendanceMapper.queryUserCosts",list);
            if(!CollectionUtils.isEmpty(costList)){
                for(PageData data : list){
                    String businessId = data.getString("businessId");
                    for(PageData costData : costList){
                        if(costData.getString("businessId").equals(businessId)){
                            data.put("amountTotal",costData.getString("userCost"));
                        }
                    }
                }
            }

        }




        return list;
    }


    /**
     * 新增人员考勤
     *
     * @param pd
     * @return
     */
    @Transactional
    public void addAttendance(PageData pd) {

        String businessId = "YFKQ" + UUID.randomUUID().toString();//生成业务主键ID
        String serialNumber = SequenceUtil.generateSerialNo("YFKQ");//生成流水号

        pd.put("businessId", businessId);
        pd.put("serialNumber", serialNumber);
        pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[0]);

        String attendanceMonth = pd.getString("attendanceMonth");
        if(StringUtils.isNotBlank(attendanceMonth)){
            pd.put("attendanceMonth",attendanceMonth+"-01");
        }

        //1、插入主表
        dao.insert("AttendanceMapper.insertMain", pd);


        //2、人员
        String researchUser = pd.getString("researchUser");
        List<PageData> researchUserList = JSONObject.parseArray(researchUser, PageData.class);
        if (!CollectionUtils.isEmpty(researchUserList)) {
            for (PageData detailData : researchUserList) {
                detailData.put("businessId", businessId);
            }

            dao.batchInsert("AttendanceMapper.batchInsertResearchUser", researchUserList);
        }


        //3、考勤表
        List<PageData> attendanceList = packageAttend(pd);

        if (!CollectionUtils.isEmpty(attendanceList)) {
            dao.batchInsert("AttendanceMapper.batchInsertAttendanceList", attendanceList);
        }

        //3、工资表
        String salaryListStr = pd.getString("salaryList");
        List<PageData> salaryList = JSONObject.parseArray(salaryListStr, PageData.class);
        if (!CollectionUtils.isEmpty(salaryList)) {
            for (PageData detailData : salaryList) {
                detailData.put("businessId", businessId);
            }

            dao.batchInsert("AttendanceMapper.batchInsertSalaryList", salaryList);
        }


        //4、分摊表
        List<PageData> shareList = packageShare(pd);

        if (!CollectionUtils.isEmpty(shareList)) {
            dao.batchInsert("AttendanceMapper.batchInsertShareList", shareList);
        }


        // 插入到附件表
        fileService.insert(pd);

    }


    /**
     * 更新人员考勤
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void updateAttendance(PageData pd) {

        String businessId = pd.getString("businessId");
        List<String> removeList = new ArrayList<>();
        removeList.add(businessId);

        String attendanceMonth = pd.getString("attendanceMonth");
        if(StringUtils.isNotBlank(attendanceMonth)){
            pd.put("attendanceMonth",attendanceMonth+"-01");
        }
        //1、更新主表
        dao.insert("AttendanceMapper.updateMain", pd);


        //2、人员
        String researchUser = pd.getString("researchUser");
        dao.batchDelete("AttendanceMapper.deleteResearchUser", removeList);
        List<PageData> researchUserList = JSONObject.parseArray(researchUser, PageData.class);
        if (!CollectionUtils.isEmpty(researchUserList)) {
            for (PageData detailData : researchUserList) {
                detailData.put("businessId", businessId);
            }

            dao.batchInsert("AttendanceMapper.batchInsertResearchUser", researchUserList);
        }


        //3、考勤表
        List<PageData> attendanceList = packageAttend(pd);
        dao.batchDelete("AttendanceMapper.deleteAttendanceList", removeList);
        if (!CollectionUtils.isEmpty(attendanceList)) {
            dao.batchInsert("AttendanceMapper.batchInsertAttendanceList", attendanceList);
        }

        //3、工资表
        String salaryListStr = pd.getString("salaryList");
        dao.batchDelete("AttendanceMapper.deleteSalaryList", removeList);
        List<PageData> salaryList = JSONObject.parseArray(salaryListStr, PageData.class);
        if (!CollectionUtils.isEmpty(salaryList)) {
            for (PageData detailData : salaryList) {
                detailData.put("businessId", businessId);
            }

            dao.batchInsert("AttendanceMapper.batchInsertSalaryList", salaryList);
        }


        //4、分摊表
        List<PageData> shareList = packageShare(pd);
        dao.batchDelete("AttendanceMapper.deleteShareList", removeList);
        if (!CollectionUtils.isEmpty(shareList)) {
            dao.batchInsert("AttendanceMapper.batchInsertShareList", shareList);
        }


        //编辑附件表
        fileService.update(pd);

    }


    /**
     * 删除人员考勤
     *
     * @param
     * @return
     */
    @Override
    @Transactional
    public void deleteAttendance(PageData pd) {

        //校验不通过的部分
        List<String> removeList = new ArrayList<>();
        String businessIdListStr = pd.getString("businessIdList");

        List<String> businessIdList = JSONObject.parseArray(businessIdListStr, String.class);
        //根据businessIdList查询主表
        List<PageData> recordList = (List<PageData>) dao.findForList("AttendanceMapper.selectByBusinessId", businessIdList);
        if (!CollectionUtils.isEmpty(recordList)) {
            for (PageData data : recordList) {
                String requestStatus = data.getString("processStatus");
                if (requestStatus.equals(ConstantValUtil.APPROVAL_STATUS[0]) || requestStatus.equals(ConstantValUtil.APPROVAL_STATUS[3])) {
                    removeList.add(data.getString("businessId"));
                }
            }
        }


        if (removeList.size() != businessIdList.size()) {
            throw new MyException("只有未提交的单据才能删除");
        }

        pd.put("removeList", removeList);

        //1、删除主表
        dao.batchDelete("AttendanceMapper.deleteMain", removeList);

        //2、人员
        dao.batchDelete("AttendanceMapper.deleteResearchUser", removeList);

        //3、考勤表
        dao.batchDelete("AttendanceMapper.deleteAttendanceList", removeList);

        //3、工资表
        dao.batchDelete("AttendanceMapper.deleteSalaryList", removeList);

        //4、分摊表
        dao.batchDelete("AttendanceMapper.deleteShareList", removeList);


        //删除文件
        dao.batchDelete("AttendanceMapper.batchDeleteFile", removeList);

    }

    /**
     * 查询人员考勤详情
     *
     * @param pd
     * @return
     */
    @Override
    public PageData queryDetail(PageData pd) {

        //1、查询主表
        PageData request = (PageData) dao.findForObject("AttendanceMapper.queryDetail", pd);
        if (request != null) {
            // 2、人员
            List<PageData> userList = (List<PageData>) dao.findForList("AttendanceMapper.queryResearchUser", request);
            if (!CollectionUtils.isEmpty(userList)) {
                request.put("researchUser", userList);
            }

            // 3、考勤表
            List<PageData> attendanceLists = (List<PageData>) dao.findForList("AttendanceMapper.queryAttendanceList", request);

            if (!CollectionUtils.isEmpty(attendanceLists)) {
                TreeMap<String, List<PageData>> attendanceMap = null;
                if (!CollectionUtils.isEmpty(attendanceLists)) {
                    attendanceMap = attendanceLists.stream().collect(Collectors.groupingBy(o -> o.getString("type"), TreeMap::new, Collectors.toList()));
                }
                //数据类型 1：数据 2：表头
                PageData head = attendanceMap.get("2").get(0);
                Map<String, String> map = new HashMap<>();
                for (int i = 1; i <= 31; i++) {
                    String key = "day" + i;
                    map.put(key, head.getString(key));
                }


                List<PageData> attendanceList = new LinkedList<>();
                List<PageData> list = attendanceMap.get("1");

                for (PageData data : list) {
                    PageData newData = new PageData();
                    newData.put("number", data.getString("number"));
                    newData.put("username", data.getString("userName"));
                    newData.put("idcard", data.getString("idCard"));
                    newData.put("postname", data.getString("postName"));
                    newData.put("weekday", data.getString("weekDay"));

                    for (int i = 1; i <= 31; i++) {
                        String key = "day" + i;
                        newData.put(map.get(key), data.getString(key));
                    }

                    attendanceList.add(newData);

                }


                request.put("attendanceList", attendanceList);
            }

            // 4、工资表
            List<PageData> salaryList = (List<PageData>) dao.findForList("AttendanceMapper.querySalaryList", request);
            if (!CollectionUtils.isEmpty(salaryList)) {
                request.put("salaryList", salaryList);


                // 5、分摊表
                List<PageData> shareLists = (List<PageData>) dao.findForList("AttendanceMapper.queryShareList", request);

                if (!CollectionUtils.isEmpty(shareLists)) {
                    TreeMap<String, List<PageData>> shareMap = null;
                    if (!CollectionUtils.isEmpty(shareLists)) {
                        shareMap = shareLists.stream().collect(Collectors.groupingBy(o -> o.getString("type"), TreeMap::new, Collectors.toList()));
                    }
                    //数据类型 1：数据 2：表头

                    PageData head = shareMap.get("2").get(0);
                    Map<String, String> map = new HashMap<>();
                    for (int i = 1; i <= 31; i++) {
                        String key = "day" + i;
                        map.put(key, head.getString(key));
                    }


                    List<PageData> shareList = new LinkedList<>();
                    List<PageData> list = shareMap.get("1");

                    for (PageData data : list) {
                        PageData newData = new PageData();
                        newData.put("number", data.getString("number"));
                        newData.put("username", data.getString("userName"));
                        newData.put("idcard", data.getString("idCard"));
                        newData.put("postname", data.getString("postName"));
                        newData.put("weekday", data.getString("weekDay"));


                        newData.put("days", data.getString("days"));
                        newData.put("dayssum", data.getString("daysSum"));
                        newData.put("attendrate", data.getString("attendRate"));
                        newData.put("wagessalary", data.getString("wagesSalary"));
                        newData.put("endowmentinsurance", data.getString("endowmentInsurance"));
                        newData.put("medicalinsurance", data.getString("medicalInsurance"));
                        newData.put("unemploymentinsurance", data.getString("unemploymentInsurance"));
                        newData.put("injuryinsurance", data.getString("injuryInsurance"));
                        newData.put("maternityinsurance", data.getString("maternityInsurance"));
                        newData.put("accumulationfund", data.getString("accumulationFund"));
                        newData.put("yearfund", data.getString("yearFund"));
                        newData.put("topicwagessalary", data.getString("topicWagesSalary"));
                        newData.put("topicendowmentinsurance", data.getString("topicEndowmentInsurance"));
                        newData.put("topicmedicalinsurance", data.getString("topicMedicalInsurance"));
                        newData.put("topicunemploymentinsurance", data.getString("topicUnemploymentInsurance"));
                        newData.put("topicinjuryinsurance", data.getString("topicInjuryInsurance"));
                        newData.put("topicmaternityinsurance", data.getString("topicMaternityInsurance"));
                        newData.put("topicaccumulationfund", data.getString("topicAccumulationFund"));
                        newData.put("topicyearfund", data.getString("topicYearFund"));
                        newData.put("topicinsurancesum", data.getString("topicInsuranceSum"));

                        for (int i = 1; i <= 31; i++) {
                            String key = "day" + i;
                            newData.put(map.get(key), data.getString(key));
                        }

                        shareList.add(newData);

                    }

                    request.put("shareList", shareList);
                }
            }


            // 查询附件表
            fileService.queryFileByBusinessId(request);

        }


        return request;
    }


    /**
     * 导入出勤表
     *
     * @param file
     * @param pd
     * @return
     */
    @Override
    public PageData uploadAttendance(MultipartFile file, PageData pd) throws Exception {
        XSSFWorkbook wb = getWorkbook(file);

        PageData result = new PageData();
        List<PageData> list = new ArrayList<>();

        //判断有无sheet页
        XSSFSheet sheet = wb.getSheet("2.1考勤表");
        if (sheet == null) {
            throw new MyException("请上传正确的模板，检查考勤表sheet页是否正确");
        }


        XSSFRow row = sheet.getRow(1);
        XSSFCell cell = row.getCell(2);
        //解析第1个单元格 考勤表单位
        String attendUnit = ReadExcelUtil.readCellStr(cell, 2, "工资表单位", false, 256);
        result.put("attendUnit", attendUnit);

        //解析第2个单元格 考勤表月份
        cell = row.getCell(7);
        String attendDate = ReadExcelUtil.readCellMonth(cell, 2, "考勤表月份", false);
        if(!pd.getString("attendanceMonth").equals(attendDate)){
            throw new MyException("模板中的月份与页面选择的月份不一致，请确认后重新上传");
        }
        result.put("attendDate", attendDate);

        //解析第3个单元格 考勤表天数
        cell = row.getCell(14);
        String attendDays = ReadExcelUtil.readCellFormat(cell, 2, "考勤表天数", false, 256);
        result.put("attendDays", attendDays);

        //解析第4个单元格 考勤表人数
        cell = row.getCell(22);
        String attendPeoples = ReadExcelUtil.readCellFormat(cell, 2, "考勤表人数", false, 256);
        result.put("attendPeoples", attendPeoples);

        //解析第5个单元格 考勤表课题
        cell = row.getCell(30);
        String attendProject = ReadExcelUtil.readCellStr(cell, 2, "考勤表课题", false, 256);

        if(!pd.getString("projectName").equals(attendProject)){
            throw new MyException("模板中的项目名称与页面选择项目名称不一致，请确认后重新上传");
        }
        result.put("attendProject", attendProject);


        //读取表头星期,日期
        XSSFRow weekRow = sheet.getRow(2);
        XSSFRow dayRow = sheet.getRow(3);
        String[] arr = new String[31];
        Map<String, Integer> map = new HashMap<>();
        for(int i=0;i<=30;i++){
            XSSFCell weekCell = weekRow.getCell(i+5);
            XSSFCell dayCell = dayRow.getCell(i+5);

            String week = ReadExcelUtil.readCellFormat(weekCell, 3, "星期", false, 256);
            String day = ReadExcelUtil.readCellFormat(dayCell, 4, "日期", false, 256);

            String key = "day-"+week+"-"+day;
            arr[i] = key;
            map.put(key,0);

        }


        List<String> idCardList = (List<String>)pd.get("idCardList");

        //遍历文件
        int end = sheet.getLastRowNum();
        String userName = "";
        String idCard = "";
        String postName = "";
        for (int i = 4; i <= end; i++) {
            PageData data = new PageData();

            row = sheet.getRow(i);

            Boolean bool = false;
            for(int j=0;j<36;j++){
                cell = row.getCell(j);
                String value = ReadExcelUtil.readCellFormat(cell, i+1, "考勤表", false, 256);
                if(j == 0){//序号
                    data.put("number", String.valueOf(i-3));
                }else if(j == 1){//姓名
                    if(StringUtils.isNotBlank(value)){
                        data.put("userName", value);
                        userName = value;
                    }else {
                        data.put("userName", userName);
                    }

                }else if(j == 2){//身份证号码
                    if(StringUtils.isNotBlank(value)){
                        if(CollectionUtils.isEmpty(idCardList) || !idCardList.contains(value)){
                            idCard = value;
                            bool = true;
                            break;
                        }
                        data.put("idCard", value);
                        idCard = value;
                    }else {
                        if(CollectionUtils.isEmpty(idCardList) || !idCardList.contains(idCard)){
                            bool = true;
                            break;
                        }
                        data.put("idCard", idCard);
                    }
                }else if(j == 3){//职务职称
                    if(StringUtils.isNotBlank(value)){
                        data.put("postName", value);
                        postName = value;
                    }else {
                        data.put("postName", postName);
                    }

                }else if(j == 4){//星期日期
                    data.put("weekDay", value);
                }else {
                    String key = arr[j-5];
                    data.put(key, value);
                    if(StringUtils.isNotBlank(value) && value.equals("√")){
                        if(map.get(key) != null){
                            map.put(key,map.get(key) +1);
                        }else {
                            map.put(key,1);
                        }


                    }

                }
            }

//            if (StringUtils.isBlank(data.getString("number")) && StringUtils.isBlank(data.getString("userName")) && StringUtils.isBlank(data.getString("idCard"))
//                    && StringUtils.isBlank(data.getString("postName"))&& StringUtils.isBlank(data.getString("weekDay"))) {
//
//                continue;
//            }

            if (StringUtils.isBlank(data.getString("weekDay"))) {

                continue;
            }

            if(bool == false){
                list.add(data);
            }



        }

        PageData data = new PageData();
        for(int j=0;j<36;j++){

            if(j == 0){//序号
                data.put("number", "该课题合计");
            }else if(j == 1){//姓名
                data.put("userName", "该课题合计");
            }else if(j == 2){//身份证号码
                data.put("idCard", "该课题合计");
            }else if(j == 3){//职务职称
                data.put("postName", "该课题合计");
            }else if(j == 4){//星期日期
                data.put("weekDay", "该课题合计");
            }else {
                String key = arr[j-5];
                data.put(key, map.get(key));
            }
        }
        list.add(data);

        result.put("attendanceList",list);

        return result;
    }


    /**
     * 导入工资表
     *
     * @return
     */
    @Override
    public PageData uploadSalary(MultipartFile file, PageData pd) throws Exception {
        XSSFWorkbook wb = getWorkbook(file);

        PageData result = new PageData();
        List<PageData> list = new ArrayList<>();

        //判断有无sheet页
        XSSFSheet sheet = wb.getSheet("2.2工资表");
        if (sheet == null) {
            throw new MyException("请上传正确的模板，检查工资表sheet页是否正确");
        }

        String[] head = {"序号","姓名","身份证号码","职务职称","工资（元）","养老保险（元）","医疗保险（元）","失业保险（元）","工伤保险（元）","生育保险（元）","住房公积金（元）" };
        checkHead(sheet,head,2);

        XSSFRow row = sheet.getRow(0);
        XSSFCell cell = row.getCell(2);
        //解析第1个单元格 工资表单位
        String salaryUnit = ReadExcelUtil.readCellStr(cell, 1, "工资表单位", false, 256);
        result.put("salaryUnit", salaryUnit);

        //解析第2个单元格 工资表月份
        cell = row.getCell(5);
        String salaryDate = ReadExcelUtil.readCellMonth(cell, 1, "工资表月份", false);
        if(!pd.getString("attendanceMonth").equals(salaryDate)){
            throw new MyException("模板中的月份与页面选择的月份不一致，请确认后重新上传");
        }
        result.put("salaryDate", salaryDate);

        //解析第3个单元格 工资表项目
        cell = row.getCell(8);
        String salaryProject = ReadExcelUtil.readCellStr(cell, 1, "工资表项目", false,256);

        if(!pd.getString("projectName").equals(salaryProject)){
            throw new MyException("模板中的项目名称与页面选择项目名称不一致，请确认后重新上传");
        }
        result.put("salaryProject", salaryProject);

        List<String> idCardList = (List<String>)pd.get("idCardList");//项目人员身份证号，

        List<String> cardList = new ArrayList<>();
        //遍历文件
        int end = sheet.getLastRowNum();
        for (int i = 3; i <= end; i++) {
            PageData data = new PageData();

            row = sheet.getRow(i);

            XSSFCell cell0 = row.getCell(0);
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

            int rowNumber = i + 1;

            //解析第1个单元格 序号
            String number = ReadExcelUtil.readCellFormat(cell0, rowNumber, "序号", false, 256);
            data.put("number", number);

            //解析第1个单元格 姓名
            String userName = ReadExcelUtil.readCellStr(cell1, rowNumber, "姓名", false, 256);
            data.put("userName", userName);

            //解析第2个单元格 身份证号码
            String idCard = ReadExcelUtil.readCellFormat(cell2, rowNumber, "身份证号码", false, 256);
            data.put("idCard", idCard);
            //不属于项目的人员，不导入数据
            if(CollectionUtils.isEmpty(idCardList) || !idCardList.contains(idCard)){
                continue;
            }

            if(cardList.contains(idCard)){
                throw new MyException("工资表身份证已存在");

            }else {
                cardList.add(idCard);
            }

            //解析第3个单元格 职务职称
            String postName = ReadExcelUtil.readCellStr(cell3, rowNumber, "职务职称", false, 256);
            data.put("postName", postName);

            //解析第4个单元格 工资（元）
            String salary = ReadExcelUtil.readCellDecimal(cell4, rowNumber, "工资（元）", false, 20,2);
            data.put("salary", salary);

            //解析第5个单元格 养老保险（元）
            String endowmentInsurance = ReadExcelUtil.readCellDecimal(cell5, rowNumber, "养老保险（元）", false, 20,2);
            data.put("endowmentInsurance", endowmentInsurance);

            //解析第6个单元格 医疗保险（元）
            String medicalInsurance = ReadExcelUtil.readCellDecimal(cell6, rowNumber, "医疗保险（元）", false, 20,2);
            data.put("medicalInsurance", medicalInsurance);

            //解析第7个单元格 失业保险（元）
            String unemploymentInsurance = ReadExcelUtil.readCellDecimal(cell7, rowNumber, "失业保险（元）", false, 20,2);
            data.put("unemploymentInsurance", unemploymentInsurance);

            //解析第8个单元格 工伤保险（元）
            String injuryInsurance = ReadExcelUtil.readCellDecimal(cell8, rowNumber, "工伤保险（元）", false, 20,2);
            data.put("injuryInsurance", injuryInsurance);

            //解析第9个单元格 生育保险（元）
            String maternityInsurance = ReadExcelUtil.readCellDecimal(cell9, rowNumber, "住房公积金（元）", false, 20,2);
            data.put("maternityInsurance", maternityInsurance);

            //解析第9个单元格 住房公积金（元）
            String  accumulationFund= ReadExcelUtil.readCellDecimal(cell10, rowNumber, "生育保险（元）", false, 20,2);
            data.put("accumulationFund", accumulationFund);


            //解析第9个单元格 企业年金（元）
            String yearFund = ReadExcelUtil.readCellDecimal(cell11, rowNumber, "企业年金（元）", false, 20,2);
            data.put("yearFund", yearFund);

            if (StringUtils.isBlank(userName) && StringUtils.isBlank(idCard) && StringUtils.isBlank(postName) && StringUtils.isBlank(salary)
                    && StringUtils.isBlank(endowmentInsurance) && StringUtils.isBlank(medicalInsurance)&& StringUtils.isBlank(unemploymentInsurance)
                    && StringUtils.isBlank(injuryInsurance) && StringUtils.isBlank(accumulationFund) && StringUtils.isBlank(maternityInsurance)
                    && StringUtils.isBlank(yearFund)) {

                continue;
            }

            list.add(data);


        }

        result.put("salaryList",list);

        return result;
    }




    /**
     * 判断表头是否正确
     * @param sheet
     * @param head
     */
    private void checkHead(XSSFSheet sheet,String[] head,int rowNum){
        XSSFRow row  = sheet.getRow(rowNum);
        //判断表头是否正确
        for (int i = 0; i < head.length; i++) {
            if (!row.getCell(i).getStringCellValue().trim().equals(head[i])) {
                throw new MyException(ConstantMsgUtil.ERR_FILE_HEAD.desc());
            }
        }
    }


    /**
     * 生成分摊表
     *
     * @param pd
     * @return
     * @throws Exception
     */
    @Override
    public List<PageData> generateShare(PageData pd) {

        List<PageData> list = new LinkedList<>();

        String projectName = pd.getString("projectName");
        if(StringUtils.isBlank(projectName)){
            throw new MyException("所选项目不能为空");
        }

        List<PageData> attendanceLists = JSONArray.parseArray(pd.getString("attendanceList"),PageData.class);
        List<PageData> salaryLists = JSONArray.parseArray(pd.getString("salaryList"),PageData.class);
        if(CollectionUtils.isEmpty(attendanceLists)){
            throw new MyException("考勤表数据不能为空");
        }

        if(CollectionUtils.isEmpty(salaryLists)){
            throw new MyException("工资表表数据不能为空");
        }

        LinkedMap<String, List<PageData>> attendanceMap = null;
        if (!CollectionUtils.isEmpty(attendanceLists)) {
            attendanceMap = attendanceLists.stream().collect(Collectors.groupingBy(o -> o.getString("idcard"), LinkedMap::new, Collectors.toList()));
        }

        TreeMap<String, List<PageData>> salaryMap = null;
        if (!CollectionUtils.isEmpty(salaryLists)) {
            salaryMap = salaryLists.stream().collect(Collectors.groupingBy(o -> o.getString("idCard"), TreeMap::new, Collectors.toList()));
        }

        Set<String> daySet = new HashSet<>();
        if (!CollectionUtils.isEmpty(attendanceLists)) {
            for (PageData detailData : attendanceLists) {
                for (Object key : detailData.keySet()) {
                    String keyStr = (String) key;

                    if (keyStr.lastIndexOf("-") > 1) {
                        daySet.add(keyStr);
                    }

                }
            }
        }

        BigDecimal topicwagessalarysum = new BigDecimal(0);
        BigDecimal topicendowmentinsurancesum = new BigDecimal(0);
        BigDecimal topicmedicalinsurancesum = new BigDecimal(0);
        BigDecimal topicunemploymentinsurancesum = new BigDecimal(0);
        BigDecimal topicinjuryinsurancesum = new BigDecimal(0);
        BigDecimal topicmaternityinsurancesum = new BigDecimal(0);
        BigDecimal topicaccumulationfundsum = new BigDecimal(0);
        BigDecimal topicyearfundsum = new BigDecimal(0);
        BigDecimal topicinsurancesum = new BigDecimal(0);

        for(Map.Entry<String,List<PageData>> entry : attendanceMap.entrySet()){

            String attendanceKey = entry.getKey();
            if(attendanceKey.equals("该课题合计")){
                continue;

            }
            List<PageData> attendanceList = entry.getValue();//考勤表数据
            List<PageData> salaryList = salaryMap.get(attendanceKey);//工资表数据


            BigDecimal salary = new BigDecimal(0);
            BigDecimal endowmentInsurance = new BigDecimal(0);
            BigDecimal medicalInsurance = new BigDecimal(0);
            BigDecimal unemploymentInsurance = new BigDecimal(0);
            BigDecimal injuryInsurance = new BigDecimal(0);
            BigDecimal maternityInsurance = new BigDecimal(0);
            BigDecimal accumulationFund = new BigDecimal(0);
            BigDecimal yearFund = new BigDecimal(0);

            if(!CollectionUtils.isEmpty(salaryList)){
                PageData salaryData = salaryList.get(0);

                salary = new BigDecimal(salaryData.getString("salary"));
                endowmentInsurance = new BigDecimal(salaryData.getString("endowmentInsurance"));
                medicalInsurance = new BigDecimal(salaryData.getString("medicalInsurance"));
                unemploymentInsurance = new BigDecimal(salaryData.getString("unemploymentInsurance"));
                injuryInsurance = new BigDecimal(salaryData.getString("injuryInsurance"));
                maternityInsurance = new BigDecimal(salaryData.getString("maternityInsurance"));
                accumulationFund = new BigDecimal(salaryData.getString("accumulationFund"));
                yearFund = new BigDecimal(salaryData.getString("yearFund"));

            }



            //总出勤天数
            int daysSum = 0;

            //该课题出勤天数
            int topicDays = 0;
            //各项目的出勤天数
            Map<String,Integer> map = new HashMap<>();
            for(PageData attendance : attendanceList){
                String type = attendance.getString("weekday");
                int count = 0;
                if(type.equals("出勤")){
                    for(String day : daySet){
                        String value = attendance.getString(day);
                        if(StringUtils.isNotBlank(value)){
                            count ++;
                        }
                    }
                }else {
                    for(String day : daySet){
                        String value = attendance.getString(day);
                        if(value.equals("√")){
                            count ++;
                        }
                    }
                    if(type.equals(projectName)){
                        topicDays = count;
                    }
                }

                daysSum += count;
                map.put(type,count);
            }

            //该课题出勤率
            BigDecimal attendrate = new BigDecimal(0.0);
            if (new BigDecimal(daysSum).compareTo(BigDecimal.ZERO) != 0) {
                attendrate = new BigDecimal(topicDays).divide(new BigDecimal(daysSum), 2, BigDecimal.ROUND_HALF_UP);
            }

            BigDecimal topicwagessalary = salary.multiply(attendrate).setScale(2,BigDecimal.ROUND_HALF_UP);
            BigDecimal topicendowmentinsurance = endowmentInsurance.multiply(attendrate).setScale(2,BigDecimal.ROUND_HALF_UP);
            BigDecimal topicmedicalinsurance = medicalInsurance.multiply(attendrate).setScale(2,BigDecimal.ROUND_HALF_UP);
            BigDecimal topicunemploymentinsurance = unemploymentInsurance.multiply(attendrate).setScale(2,BigDecimal.ROUND_HALF_UP);
            BigDecimal topicinjuryinsurance = injuryInsurance.multiply(attendrate).setScale(2,BigDecimal.ROUND_HALF_UP);
            BigDecimal topicmaternityinsurance = maternityInsurance.multiply(attendrate).setScale(2,BigDecimal.ROUND_HALF_UP);
            BigDecimal topicaccumulationfund = accumulationFund.multiply(attendrate).setScale(2,BigDecimal.ROUND_HALF_UP);
            BigDecimal topicyearfund = yearFund.multiply(attendrate).setScale(2,BigDecimal.ROUND_HALF_UP);
            BigDecimal total = topicendowmentinsurance.add(topicmedicalinsurance).add(topicunemploymentinsurance).add(topicinjuryinsurance)
                    .add(topicmaternityinsurance).add(topicaccumulationfund).add(topicyearfund);


            topicwagessalarysum = topicwagessalarysum.add(topicwagessalary);
            topicendowmentinsurancesum = topicendowmentinsurancesum.add(topicendowmentinsurance);
            topicmedicalinsurancesum = topicmedicalinsurancesum.add(topicmedicalinsurance);
            topicunemploymentinsurancesum = topicunemploymentinsurancesum.add(topicunemploymentinsurance);
            topicinjuryinsurancesum = topicinjuryinsurancesum.add(topicinjuryinsurance);
            topicmaternityinsurancesum = topicmaternityinsurancesum.add(topicmaternityinsurance);
            topicaccumulationfundsum = topicaccumulationfundsum.add(topicaccumulationfund);
            topicyearfundsum = topicyearfundsum.add(topicyearfund);
            topicinsurancesum = topicinsurancesum.add(total);

            for(PageData attendance : attendanceList){
                String type = attendance.getString("weekday");

                attendance.put("days",map.get(type));//天数
                attendance.put("dayssum",daysSum);//总出勤天数
                attendance.put("attendrate",attendrate);//总课题出勤率
                attendance.put("wagessalary",salary);
                attendance.put("endowmentinsurance",endowmentInsurance);
                attendance.put("medicalinsurance",medicalInsurance);
                attendance.put("unemploymentinsurance",unemploymentInsurance);
                attendance.put("injuryinsurance",injuryInsurance);
                attendance.put("maternityinsurance",maternityInsurance);
                attendance.put("accumulationfund",accumulationFund);
                attendance.put("yearfund",yearFund);

                attendance.put("topicwagessalary",topicwagessalary);
                attendance.put("topicendowmentinsurance",topicendowmentinsurance);
                attendance.put("topicmedicalinsurance",topicmedicalinsurance);
                attendance.put("topicunemploymentinsurance",topicunemploymentinsurance);
                attendance.put("topicinjuryinsurance",topicinjuryinsurance);
                attendance.put("topicmaternityinsurance",topicmaternityinsurance);
                attendance.put("topicaccumulationfund",topicaccumulationfund);
                attendance.put("topicyearfund",topicyearfund);



                attendance.put("topicinsurancesum",total.setScale(2,BigDecimal.ROUND_HALF_UP));

                list.add(attendance);

            }



        }

        //封装合计
        PageData data = new PageData();
        data.put("number","该课题合计");
        data.put("username","该课题合计");
        data.put("idcard","该课题合计");
        data.put("postname","该课题合计");

        data.put("topicwagessalary",topicwagessalarysum);
        data.put("topicendowmentinsurance",topicendowmentinsurancesum);
        data.put("topicmedicalinsurance",topicmedicalinsurancesum);
        data.put("topicunemploymentinsurance",topicunemploymentinsurancesum);
        data.put("topicinjuryinsurance",topicinjuryinsurancesum);
        data.put("topicmaternityinsurance",topicmaternityinsurancesum);
        data.put("topicaccumulationfund",topicaccumulationfundsum);
        data.put("topicyearfund",topicyearfundsum);
        data.put("topicinsurancesum",topicinsurancesum);
        list.add(data);

        return list;
    }

    /**
     * 查询已生成支出申请单的信息
     * @param pd
     * @return
     */
    @Override
    public List<PageData> alreadyGenerateApply(PageData pd) {
        pd.put("businessIdOther",pd.getString("businessId"));
        List<PageData> list = (List<PageData>) dao.findForList("ItemExpensesMapper.queryBusinessIdOtherSize", pd);
        return list;
    }

    /**
     * 生成支出申请单
     * @param pd
     * @return
     */

    @Override
    public PageData generateApply(PageData pd) {
//        String[] arr = {"january","february","march","april","may","june","july","august","september","october","november","december"};

        //先查出当前的记录，获取到项目信息
        PageData attendanceData = (PageData) dao.findForObject("AttendanceMapper.queryDetail",pd);
        if(attendanceData == null){
            throw new MyException("该条单据不存在");
        }
        //查询本次单据的人员费，即每个单据的五险一金+该项目的工资合计 userCost
        PageData costData = (PageData) dao.findForObject("AttendanceMapper.queryUserCost",attendanceData);
        if(costData != null){
            if (pd.getString("amountType").equals("1")){
                //todo 选择的是工资薪金
                attendanceData.put("amount",costData.getString("topicWagesSalary"));
            } else if (pd.getString("amountType").equals("2")){
                //todo 选择的是五险一金
                attendanceData.put("amount",costData.getString("topicInsuranceSum"));
            }
//            attendanceData.put("amount",costData.getString("userCost"));
        }else {
            attendanceData.put("amount","0.00");
        }


        PageData projectData = (PageData) dao.findForObject("MaterialRequisitionMapper.queryProject",attendanceData);
        if(projectData != null){
            attendanceData.put("businessId",projectData.getString("businessId"));
            attendanceData.put("bureauLevel",projectData.getString("bureauLevel"));
            attendanceData.put("startYear",projectData.getString("startYear"));
            attendanceData.put("endYear",projectData.getString("endYear"));
        }

 /*       BigDecimal userCost = new BigDecimal(costData.getString("userCost"));


        //根据项目ID，查询人员考勤中该项目已经通过的所有单据的人员费用
        PageData totalCostData = (PageData) dao.findForObject("AttendanceMapper.queryUserCostTotal",attendanceData);
        BigDecimal totalUserCost = new BigDecimal(totalCostData.getString("userCost"));

       //截止当前单据所属月份人员的累计预算
        List<PageData> budgetList = (List<PageData>) dao.findForList("AttendanceMapper.queryBudgetList",attendanceData);

        //当前单据所选的年月
        int year = 0;
        int month = 0;
        try {
            Date endDate = new SimpleDateFormat("yyyy-MM").parse(pd.getString("attendanceMonth"));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH) + 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //比较日期，
        // 当年份小于人员考勤的年份时，统计所有的月份值，
        // 当年份等于人员考勤的年份时，统计小于等于当前月份的值，
        // 当年份大于人员考勤的年份时，不统计任何值
        BigDecimal totalBudget = new BigDecimal(0);
        if(!CollectionUtils.isEmpty(budgetList)){
            for(PageData data : budgetList){
                int years = data.getInt("years");
                if(years < year){
                    for(int i=0;i<12;i++){
                        String value = data.getString(arr[i]);
                        if(StringUtils.isNotBlank(value)){
                            totalBudget = totalBudget.add(new BigDecimal(value));
                        }

                    }
                }else if(years == year){
                    for(int i=0;i<12;i++){

                        int m = i+1;
                        String value = data.getString(arr[i]);
                        if(StringUtils.isNotBlank(value) && m <= month){
                            totalBudget = totalBudget.add(new BigDecimal(value));
                        }

                    }
                }

            }
        }


        BigDecimal rate = new BigDecimal(0.0);
        if (totalBudget.compareTo(BigDecimal.ZERO) != 0) {
            rate = (userCost.add(totalUserCost)).divide(totalBudget).multiply(new BigDecimal(100));
        }

        if(rate.compareTo(new BigDecimal(5)) >0){
            throw new MyException("人员费已超过预算的5%，请调整后重新操作");
        }

*/

        return attendanceData;
    }

    /**
     * 获取已存在的项目
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryProject(PageData pd) {

        List<String> projectTypeCodeList = JSONArray.parseArray(pd.getString("projectTypeCode"), String.class);
        pd.put("projectTypeCodeList", projectTypeCodeList);

        List<String> professionalCategoryCodeList = JSONArray.parseArray(pd.getString("professionalCategoryCode"), String.class);
        pd.put("professionalCategoryCodeList", professionalCategoryCodeList);

        List<PageData> projectList = (List<PageData>) dao.findForList("AttendanceMapper.queryProjectList",pd);
        return projectList;
    }


    /**
     * 查询项目人员
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryUser(PageData pd) {

        List<PageData> list = new ArrayList<>();

        //查询项目立项变更后的人员
        PageData data = (PageData) dao.findForObject("AttendanceMapper.queryByProjectId",pd);
        data.put("attendanceMonth",pd.getString("attendanceMonth"));
        List<PageData> changeUserList = (List<PageData>) dao.findForList("AttendanceMapper.queryChangeUserList", data);
        if (!CollectionUtils.isEmpty(changeUserList)) {
            list.addAll(changeUserList);
        } else {
            //查询项目立项初始人员
            List<PageData> userList = (List<PageData>) dao.findForList("AttendanceMapper.queryUserList", pd);
            if (!CollectionUtils.isEmpty(userList)) {
                list.addAll(userList);
            }
        }

        if (CollectionUtils.isEmpty(list)) {
            throw new MyException("该项目下没有研究人员");
        }

        return list;
    }


    private XSSFWorkbook getWorkbook(MultipartFile file) throws Exception {
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
     * 导出压缩包
     *
     * @param businessIdList
     * @param zos
     * @param bos
     * @param filePrefix
     * @throws Exception
     */
    @Override
    public void exportZip(List<String> businessIdList, ZipOutputStream zos, ByteArrayOutputStream bos, String filePrefix) throws Exception {

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳

        for (String businessId : businessIdList) {
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
        pd.put("businessId", businessId);
        PageData mainData = (PageData) dao.findForObject("AttendanceMapper.queryDetail",pd);
        List<PageData> shareLists = (List<PageData>) dao.findForList("AttendanceMapper.queryShareList", pd);

        String title = "兼职研发人员考勤机费用分摊表";
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(title);

        HSSFCellStyle styleTitle = ExcelUtil.setHeader(wb, sheet);// 标题
        HSSFCellStyle styleHeader = ExcelUtil.setHeaderLeft(wb, sheet);// 表头
        HSSFCellStyle styleCell = ExcelUtil.setWrapCell(wb, sheet);// 单元格

        //创建标题
        HSSFRow rows = sheet.createRow(0);
        rows.setHeightInPoints(20);// 行高
        HSSFCell cells = rows.createCell(0);
        cells.setCellValue(title);
        cells.setCellStyle(styleTitle);
        ExcelUtil.merge(wb, sheet, 0, 0, 0, 55);

        //创建第二行

        //1、单位名称
        rows = sheet.createRow(1);
        cells = rows.createCell(0);
        cells.setCellValue("单位名称");
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 1, 1, 0, 1);

        cells = rows.createCell(2);
        cells.setCellValue(mainData.getString("attendUnit"));
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 1, 1, 2, 4);

        //考勤月份：
        cells = rows.createCell(5);
        cells.setCellValue("考勤月份");
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 1, 1, 5, 6);

        cells = rows.createCell(7);
        cells.setCellValue(mainData.getString("attendDate"));
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 1, 1, 7, 12);

        //当月天数：
        cells = rows.createCell(13);
        cells.setCellValue("当月天数");
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 1, 1, 13, 14);

        cells = rows.createCell(15);
        cells.setCellValue(mainData.getString("attendDays"));
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 1, 1, 15, 20);

        //考勤人数：
        cells = rows.createCell(21);
        cells.setCellValue("考勤人数");
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 1, 1, 21, 22);

        cells = rows.createCell(23);
        cells.setCellValue(mainData.getString("attendPeoples"));
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 1, 1, 23, 29);

        //项目名称
        cells = rows.createCell(30);
        cells.setCellValue("项目名称");
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 1, 1, 30, 31);

        cells = rows.createCell(32);
        cells.setCellValue(mainData.getString("attendProject"));
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 1, 1, 32, 37);

        if (!CollectionUtils.isEmpty(shareLists)) {
            TreeMap<String, List<PageData>> shareMap = null;
            if (!CollectionUtils.isEmpty(shareLists)) {
                shareMap = shareLists.stream().collect(Collectors.groupingBy(o -> o.getString("type"), TreeMap::new, Collectors.toList()));
            }
            //数据类型 1：数据 2：表头
            PageData head = null;
            if(shareMap != null && !CollectionUtils.isEmpty(shareMap.get("2"))){
                head = shareMap.get("2").get(0);
            }

            List<PageData> list = shareMap.get("1");

            if(head != null){
                //填写第三行星期
                rows = sheet.createRow(2);

                String[] head1 = {"序号","姓名","身份证号","职务职称","星期"};
                for (int j = 0; j < 5; j++) {
                    cells = rows.createCell(j);
                    cells.setCellValue(head1[j]);
                    cells.setCellStyle(styleCell);
                }

                for(int i=1;i<=31;i++){
                    cells = rows.createCell(i+4);
                    String s = head.getString("day"+i);
                    if (StringUtils.isNotBlank(s)){
                        cells.setCellValue(s.substring(s.indexOf("-")+1,s.lastIndexOf("-")));
                    }
                    cells.setCellStyle(styleCell);

                }

                String[] head2 = {"天数","总出勤天数","该课题出勤率","应付工资（元）","养老保险（元）","医疗保险（元）","失业保险（元）","工伤保险（元）",
                        "生育保险（元）","住房公积金（元）","企业年金（元）","该课题研发工资（元）","该课题养老保险（元）","该课题医疗保险（元）","该课题失业保险（元）",
                        "该课题工伤保险（元）","该课题生育保险（元）","该课题住房公积金（元）","该课题企业年金（元）","该课题社保合计（元）"};
                for (int j = 0; j < 20; j++) {
                    cells = rows.createCell(j+36);
                    cells.setCellValue(head2[j]);
                    cells.setCellStyle(styleCell);
                }

                //填写第四行日期
                rows = sheet.createRow(3);

                cells = rows.createCell(4);
                cells.setCellValue("日期");
                cells.setCellStyle(styleCell);

                for(int i=1;i<=31;i++){
                    cells = rows.createCell(i+4);
                    String s = head.getString("day"+i);
                    cells.setCellValue(s.substring(s.lastIndexOf("-")+1));
                    cells.setCellStyle(styleCell);

                }

                //合并
                ExcelUtil.merge(wb, sheet, 2, 3, 0, 0);
                ExcelUtil.merge(wb, sheet, 2, 3, 1, 1);
                ExcelUtil.merge(wb, sheet, 2, 3, 2, 2);
                ExcelUtil.merge(wb, sheet, 2, 3, 3, 3);


                ExcelUtil.merge(wb, sheet, 2, 3, 36, 36);
                ExcelUtil.merge(wb, sheet, 2, 3, 37, 37);
                ExcelUtil.merge(wb, sheet, 2, 3, 38, 38);
                ExcelUtil.merge(wb, sheet, 2, 3, 39, 39);
                ExcelUtil.merge(wb, sheet, 2, 3, 40, 40);
                ExcelUtil.merge(wb, sheet, 2, 3, 41, 41);
                ExcelUtil.merge(wb, sheet, 2, 3, 42, 42);
                ExcelUtil.merge(wb, sheet, 2, 3, 43, 43);
                ExcelUtil.merge(wb, sheet, 2, 3, 44, 44);
                ExcelUtil.merge(wb, sheet, 2, 3, 45, 45);
                ExcelUtil.merge(wb, sheet, 2, 3, 46, 46);
                ExcelUtil.merge(wb, sheet, 2, 3, 47, 47);
                ExcelUtil.merge(wb, sheet, 2, 3, 48, 48);
                ExcelUtil.merge(wb, sheet, 2, 3, 49, 49);
                ExcelUtil.merge(wb, sheet, 2, 3, 50, 50);
                ExcelUtil.merge(wb, sheet, 2, 3, 51, 51);
                ExcelUtil.merge(wb, sheet, 2, 3, 52, 52);
                ExcelUtil.merge(wb, sheet, 2, 3, 53, 53);
                ExcelUtil.merge(wb, sheet, 2, 3, 54, 54);
                ExcelUtil.merge(wb, sheet, 2, 3, 55, 55);
            }

            Map<Integer,Integer> map = new HashMap<>();
            Set<String> cardSet = new HashSet<>();


            if(!CollectionUtils.isEmpty(list)){
                int number = 0;
                int startCount = 3;
                int endCount = 3;

                for(int i=0;i<list.size();i++){
                    PageData data = list.get(i);
                    String idCard = data.getString("idCard");

                    HSSFRow row = sheet.createRow(i + 4);
                    int j = 0;

                    HSSFCell cell = row.createCell(j++);
                    endCount ++;
                    if(!cardSet.contains(idCard)){
                        startCount = endCount;
                        number ++;
                        cardSet.add(idCard);
                    }

                    map.put(startCount,endCount);


                    if(idCard.contains("合计")){
                        cell.setCellValue(idCard);
                    }else {
                        cell.setCellValue(number);
                    }

                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("userName"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(idCard);
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("postName"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("weekDay"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day1"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day2"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day3"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day4"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day5"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day6"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day7"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day8"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day9"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day10"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day11"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day12"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day13"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day14"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day15"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day16"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day17"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day18"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day19"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day20"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day21"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day22"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day23"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day24"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day25"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day26"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day27"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day28"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day29"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day30"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("day31"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("days"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("daysSum"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("attendRate"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("wagesSalary"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("endowmentInsurance"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("medicalInsurance"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("unemploymentInsurance"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("injuryInsurance"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("maternityInsurance"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("accumulationFund"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("yearFund"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("topicWagesSalary"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("topicEndowmentInsurance"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("topicMedicalInsurance"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("topicUnemploymentInsurance"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("topicInjuryInsurance"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("topicMaternityInsurance"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("topicAccumulationFund"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("topicYearFund"));
                    cell.setCellStyle(styleCell);

                    cell = row.createCell(j++);
                    cell.setCellValue(data.getString("topicInsuranceSum"));
                    cell.setCellStyle(styleCell);

                }

                ExcelUtil.merge(wb, sheet, 3+list.size(), 3+list.size(), 0, 3);

                for(Map.Entry<Integer,Integer> entry : map.entrySet()){
                    Integer startRow = entry.getKey();
                    Integer endRow = entry.getValue();

                    for(int i=0;i<=3;i++){
                        ExcelUtil.merge(wb, sheet, startRow, endRow, i, i);
                    }

                    for(int i=37;i<=55;i++){
                        ExcelUtil.merge(wb, sheet, startRow, endRow, i, i);
                    }
                }
            }



        }


        sheet.setColumnWidth(0, 2000);
        for (int j = 5; j <= 35; j++) {
            sheet.setColumnWidth(j, 1500);

        }
        sheet.setAutobreaks(true);
        return wb;
    }


    /**
     * 封装考勤信息
     * @param pd
     * @return
     */
    private List<PageData> packageAttend(PageData pd) {
        List<PageData> newAttendanceList = new ArrayList<>();
        String attendanceListStr = pd.getString("attendanceList");
        List<PageData> attendanceList = JSONObject.parseArray(attendanceListStr, PageData.class);
        if (!CollectionUtils.isEmpty(attendanceList)) {

            Set<String> daySet = new HashSet<>();
            if (!CollectionUtils.isEmpty(attendanceList)) {
                for (PageData detailData : attendanceList) {
                    for (Object key : detailData.keySet()) {
                        String keyStr = (String) key;

                        if (keyStr.lastIndexOf("-") > 1) {
                            daySet.add(keyStr);
                        }

                    }
                }
            }

            PageData head = new PageData();
            head.put("businessId", pd.getString("businessId"));
            for (String key : daySet) {
                String day = "day" + key.substring(key.lastIndexOf("-") + 1);
                head.put(day, key);
                head.put("type", 2);
            }
            newAttendanceList.add(head);


            for (PageData detailData : attendanceList) {
                PageData data = new PageData();
                data.put("businessId", pd.getString("businessId"));
                data.put("number", detailData.getString("number"));
                data.put("userName", detailData.getString("username"));
                data.put("idCard", detailData.getString("idcard"));
                data.put("postName", detailData.getString("postname"));
                data.put("weekDay", detailData.getString("weekday"));
                data.put("type", 1);

                for (String key : daySet) {
                    String day = "day" + key.substring(key.lastIndexOf("-") + 1);
                    data.put(day, detailData.getString(key));
                }

                newAttendanceList.add(data);

            }
        }

        return newAttendanceList;
    }


    /**
     * 封装分摊表
     * @param pd
     * @return
     */
    private List<PageData> packageShare(PageData pd) {
        List<PageData> newShareList = new ArrayList<>();
        String shareListStr = pd.getString("shareList");
        List<PageData> shareList = JSONObject.parseArray(shareListStr, PageData.class);
        if (!CollectionUtils.isEmpty(shareList)) {

            Set<String> daySet = new HashSet<>();
            if (!CollectionUtils.isEmpty(shareList)) {
                for (PageData detailData : shareList) {
                    for (Object key : detailData.keySet()) {
                        String keyStr = (String) key;

                        if (keyStr.lastIndexOf("-") > 1) {
                            daySet.add(keyStr);
                        }

                    }
                }
            }

            PageData head = new PageData();
            head.put("businessId", pd.getString("businessId"));
            for (String key : daySet) {
                String day = "day" + key.substring(key.lastIndexOf("-") + 1);
                head.put(day, key);
                head.put("type", 2);
                head.put("days", null);
                head.put("dayssum", null);
                head.put("attendrate", null);
                head.put("wagessalary", null);
                head.put("endowmentinsurance", null);
                head.put("medicalinsurance", null);
                head.put("unemploymentinsurance", null);
                head.put("injuryinsurance", null);
                head.put("maternityinsurance", null);
                head.put("accumulationfund", null);
                head.put("yearfund", null);
                head.put("topicwagessalary", null);
                head.put("topicendowmentinsurance", null);
                head.put("topicmedicalinsurance", null);
                head.put("topicunemploymentinsurance", null);
                head.put("topicinjuryinsurance", null);
                head.put("topicmaternityinsurance", null);
                head.put("topicaccumulationfund", null);
                head.put("topicyearfund", null);
                head.put("topicinsurancesum", null);
            }
            newShareList.add(head);


            for (PageData detailData : shareList) {
                PageData data = new PageData();
                data.put("businessId", pd.getString("businessId"));
                data.put("number", detailData.getString("number"));
                data.put("userName", detailData.getString("username"));
                data.put("idCard", detailData.getString("idcard"));
                data.put("postName", detailData.getString("postname"));
                data.put("weekDay", detailData.getString("weekday"));

                data.put("days", detailData.getString("days"));
                data.put("dayssum", detailData.getString("dayssum"));
                data.put("attendrate", detailData.getString("attendrate"));


                data.put("wagessalary", detailData.getString("wagessalary"));
                data.put("endowmentinsurance", detailData.getString("endowmentinsurance"));
                data.put("medicalinsurance", detailData.getString("medicalinsurance"));
                data.put("unemploymentinsurance", detailData.getString("unemploymentinsurance"));
                data.put("injuryinsurance", detailData.getString("injuryinsurance"));
                data.put("maternityinsurance", detailData.getString("maternityinsurance"));
                data.put("accumulationfund", detailData.getString("accumulationfund"));
                data.put("yearfund", detailData.getString("yearfund"));

                String number = detailData.getString("number");
                if(StringUtils.isNotBlank(number) && number.contains("合计")){
                    data.put("wagessalary", null);
                    data.put("endowmentinsurance", null);
                    data.put("medicalinsurance", null);
                    data.put("unemploymentinsurance", null);
                    data.put("injuryinsurance", null);
                    data.put("maternityinsurance", null);
                    data.put("accumulationfund", null);
                    data.put("yearfund", null);
                }

                data.put("topicwagessalary", detailData.getString("topicwagessalary"));
                data.put("topicendowmentinsurance", detailData.getString("topicendowmentinsurance"));
                data.put("topicmedicalinsurance", detailData.getString("topicmedicalinsurance"));
                data.put("topicunemploymentinsurance", detailData.getString("topicunemploymentinsurance"));
                data.put("topicinjuryinsurance", detailData.getString("topicinjuryinsurance"));
                data.put("topicmaternityinsurance", detailData.getString("topicmaternityinsurance"));
                data.put("topicaccumulationfund", detailData.getString("topicaccumulationfund"));
                data.put("topicyearfund", detailData.getString("topicyearfund"));
                data.put("topicinsurancesum", detailData.getString("topicinsurancesum"));



                data.put("type", 1);

                for (String key : daySet) {
                    String day = "day" + key.substring(key.lastIndexOf("-") + 1);
                    data.put(day, detailData.getString(key));
                }

                newShareList.add(data);

            }
        }

        return newShareList;
    }


}
