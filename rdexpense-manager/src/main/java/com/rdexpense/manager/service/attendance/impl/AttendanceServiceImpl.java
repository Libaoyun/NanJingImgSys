package com.rdexpense.manager.service.attendance.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.*;
import com.rdexpense.manager.service.attendance.AttendanceService;
import com.rdexpense.manager.service.file.FileService;
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
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
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
    public void addAttendance(PageData pd) {

        String businessId = "RDPI" + UUID.randomUUID().toString();//生成业务主键ID
        String serialNumber = SequenceUtil.generateSerialNo("RDPI");//生成流水号

        pd.put("businessId", businessId);
        pd.put("serialNumber", serialNumber);
        pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[0]);


        //1、插入主表
        dao.insert("ProjectApplyMapper.insertMain", pd);


        //3、插入进度计划
        String progressPlan = pd.getString("progressPlan");
        List<PageData> progressPlanList = JSONObject.parseArray(progressPlan, PageData.class);
        if (!CollectionUtils.isEmpty(progressPlanList)) {
            for (PageData detailData : progressPlanList) {
                detailData.put("businessId", businessId);
            }

            dao.batchInsert("ProjectApplyMapper.batchInsertProgressPlan", progressPlanList);
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
    public void updateAttendance(PageData pd) {

        //判断是否是提交 1:保存 2:提交
        String businessId = pd.getString("businessId");
        pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[0]);
        List<String> removeList = new ArrayList<>();
        removeList.add(businessId);



        //1、编辑主表
        dao.update("ProjectApplyMapper.updateMain", pd);

        //5、插入研究人员
        String researchUser = pd.getString("researchUser");
        dao.batchDelete("ProjectApplyMapper.deleteResearchUser", removeList);
        List<PageData> researchUserList = JSONObject.parseArray(researchUser, PageData.class);
        if (!CollectionUtils.isEmpty(researchUserList)) {
            for (PageData detailData : researchUserList) {
                detailData.put("businessId", businessId);
            }


            dao.batchInsert("ProjectApplyMapper.batchInsertResearchUser", researchUserList);
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
    public void deleteAttendance(PageData pd) {

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



            // 查询附件表
            fileService.queryFileByBusinessId(request);

        }


        return request;
    }





    /**
     *
     * 导入出勤表
     * @param file
     * @param pd
     * @return
     */
    @Override
    public List<PageData> uploadAttendance(MultipartFile file, PageData pd) throws Exception{
        XSSFWorkbook wb = getWorkbook(file);

        List<PageData> list = new ArrayList<>();

        return list;
    }





    /**
     * 读取工资表
     * @return
     */
    @Override
    public List<PageData> uploadSalary(MultipartFile file,PageData pd)throws Exception {
        XSSFWorkbook wb = getWorkbook(file);

        List<PageData> list = new ArrayList<>();

        return list;
    }


    /**
     * 生成分摊表
     * @param file
     * @param pd
     * @return
     * @throws Exception
     */
    @Override
    public List<PageData> generateShare(MultipartFile file, PageData pd) throws Exception {
        return null;
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
     * 导出压缩包
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
        pd.put("businessId",businessId);
        PageData data = getApplyDetail(pd);

        HSSFWorkbook wb = new HSSFWorkbook();


        return wb;
    }







}
