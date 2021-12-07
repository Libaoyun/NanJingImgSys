package com.rdexpense.manager.service.itemClosureCheck.itemClosureCheck.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.entity.PageData;
import com.common.util.ConstantValUtil;
import com.common.util.ExcelUtil;
import com.common.util.PDFUtil;
import com.common.util.SequenceUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.rdexpense.manager.service.file.FileService;
import com.rdexpense.manager.service.flow.FlowService;
import com.rdexpense.manager.service.itemClosureCheck.ItemClosureCheckService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@EnableAspectJAutoProxy(proxyTargetClass = true,exposeProxy = true)
@Slf4j
@Service
public class ItemClosureCheckServiceImpl implements ItemClosureCheckService {

    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;

    @Autowired
    private FileService fileService;

    @Autowired
    @Resource(name = "FlowService")
    private FlowService flowService;

    /**
     * 查询列表
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryItemClosureCheckList(PageData pd) {

        List<String> pageDataList = JSONArray.parseArray(pd.getString("statusList"), String.class);
        pd.put("processStatusList", pageDataList);
        pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[0]);
        pd.put("selsctCreateUser", pd.getString("selsctCreateUser"));
        List<PageData> userInfoList = (List<PageData>) baseDao.findForList("ItemClosureCheckMapper.queryItemClosureCheckList", pd);

        return userInfoList;
    }

    /**
     * 查询已审批完成的研发项目申请列表
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryProjectApplyList(PageData pd) {
        pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[4]);
        List<String> projectTypeCodeList = JSONArray.parseArray(pd.getString("projectTypeCodeList"), String.class);
        pd.put("projectTypeCodeList", projectTypeCodeList);
        List<String> professionalCategoryCodeList = JSONArray.parseArray(pd.getString("professionalCategoryCodeList"), String.class);
        pd.put("professionalCategoryCodeList", professionalCategoryCodeList);
        List<PageData> pageDataList = (List<PageData>) baseDao.findForList("ItemClosureCheckMapper.queryProjectApplyList", pd);
        return pageDataList;
    }

    /**
     * 查询已审批完成的研发项目申请的用户信息
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryApplyUserList(PageData pd) {
        //查询研发项目变更表下的人员信息
        List<PageData> pageDataList = null;
        pageDataList = (List<PageData>) baseDao.findForList("ItemClosureCheckMapper.queryItemChangeUserList", pd);
        if (pageDataList == null){
            //查询研发项目申请表下的人员信息
            pageDataList = (List<PageData>) baseDao.findForList("ProjectApplyMapper.queryResearchUser", pd);
        }
        return pageDataList;
    }

    /**
     * 保存或编辑
     * @param pd
     */
    @Override
    @Transactional
    public void saveOrUpdate(PageData pd) {
        //查询是否已经结项
//        if (!pd.getString("jobTitle").isEmpty()){
//            List<PageData> checkList = (List<PageData>) baseDao.findForList("ItemClosureCheckMapper.query", pd);
//            if (!CollectionUtils.isEmpty(checkList)) {
//                throw new MyException("研发项目结题验收已结题或存在已提交的单据");
//            }
//        }

        //如果ID为空，则为保存
        if (pd.getString("id").isEmpty()){
            //判断是直接保存，还是提交时保存
            //直接保存
            if (pd.getString("flag").isEmpty()){
                pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[0]);
                String serialNumber = SequenceUtil.generateSerialNo("ICCK");//生成流水号
                String businessId = "ICCK" + UUID.randomUUID().toString();//生成业务主键ID
                pd.put("serialNumber", serialNumber);
                pd.put("businessId", businessId);
            }

            //1、插入主表
            baseDao.insert("ItemClosureCheckMapper.insertMain", pd);

            //插入人员表
            String researchUser = pd.getString("userInfoList");
            List<PageData> researchUserList = JSONObject.parseArray(researchUser, PageData.class);
            if (!CollectionUtils.isEmpty(researchUserList)) {
                for (PageData detailData : researchUserList) {
                    detailData.put("businessId", pd.getString("businessId"));
                }

                baseDao.batchInsert("ItemClosureCheckMapper.batchInsertResearchUser", researchUserList);
            }

            // 插入到附件表
            fileService.insert(pd);

        } else {
            //编辑
            //1、更新主表
            baseDao.update("ItemClosureCheckMapper.updateMain", pd);

            //删除研发项目结项验收研发人员信息表
            baseDao.delete("ItemClosureCheckMapper.deleteUserInfo", pd);

            //2、插入人员表
            String researchUser = pd.getString("userInfoList");
            List<PageData> researchUserList = JSONObject.parseArray(researchUser, PageData.class);
            if (!CollectionUtils.isEmpty(researchUserList)) {
                for (PageData detailData : researchUserList) {
                    detailData.put("businessId", pd.getString("businessId"));
                }

                baseDao.batchInsert("ItemClosureCheckMapper.batchInsertResearchUser", researchUserList);
            }

            //先删除附件表
            fileService.deleteAttachment(pd);
            //再插入附件表
            fileService.insert(pd);
        }
    }


    /**
     * 提交
     * @param pd
     */
    @Override
    @Transactional
    public void submit(PageData pd) {

        //1：列表提交，2：保存提交，3：编辑提交
        if (pd.getString("flag").equals("2")){
            String serialNumber = SequenceUtil.generateSerialNo("ICCK");//生成流水号
            String businessId = "ICCK" + UUID.randomUUID().toString();//生成业务主键ID
            pd.put("serialNumber", serialNumber);
            pd.put("businessId", businessId);
            //保存提交，先生成单据编号，再启动工作流，然后再保存数据
            flowService.startFlow(pd);

            saveOrUpdate(pd);
        } else if (pd.getString("flag").equals("3")){
            //编辑提交，先启动工作流，然后再保存数据
            flowService.startFlow(pd);

            saveOrUpdate(pd);
        } else {

            //列表提交，直接启动工作流
            flowService.startFlow(pd);
        }

        //更改主表状态
        pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[1]);
        baseDao.update("ItemClosureCheckMapper.updateMainProcessStatus", pd);

    }

    /**
     * 审批
     * @param pd
     */
    @Override
    public void approveRecord(PageData pd) {
        //插入附件表，并返回主键id的拼接字符串
        PageData data = fileService.insertApproveFile(pd);
        pd.put("fileId", data.getString("fileId"));
        pd.put("fileName", data.getString("fileName"));

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
        baseDao.update("ItemClosureCheckMapper.updateMainApproveProcessStatus", pd);
    }

    /**
     * 删除
     * @param pd
     */
    @Override
    @Transactional
    public void deleteItemClosure(PageData pd) {
        List<String> idList = JSONArray.parseArray(pd.getString("idList"), String.class);
        //删除主表
        baseDao.batchDelete("ItemClosureCheckMapper.deleteItemClosure", idList);
        //删除研发项目结项验收研发人员信息表
        baseDao.batchDelete("ItemClosureCheckMapper.deleteItemClosureUserInfo", idList);

        //先删除附件表
        fileService.deleteAttachment(pd);
    }

    /**
     * 查询详情
     * @param pd
     * @return
     */
    @Override
    public PageData getItemClosureCheckDetail(PageData pd) {
        //1、查询主表
        PageData request = (PageData) baseDao.findForObject("ItemClosureCheckMapper.queryItemClosureCheckDetail", pd);

        //查询人员信息
        List<PageData> userInfoList = (List<PageData> ) baseDao.findForList("ItemClosureCheckMapper.queryItemClosureUserInfoList", pd);
        request.put("userInfoList",userInfoList);

        // 查询附件表
        fileService.queryFileByBusinessId(request);

        return request;
    }

    /**
     * 导出excel
     * @param pageData
     * @return
     */
    @Override
    public HSSFWorkbook exportExcel(PageData pageData) {
        String title = "研发项目结题验收";
        String[] head = {"序号", "单据编号", "成果名称", "当前审批人", "申请评审验收单位", "结题申报人", "申请评审日期", "项目负责人", "岗位", "联系电话", "起始年度",
                "结束年度", "成果内容简介", "编制人", "创建日期", "更新日期"};
        String idStr = pageData.getString("businessIdList");
        List<String> listId = JSONObject.parseArray(idStr, String.class);
        //根据idList查询主表
        List<PageData> checkInfoList = (List<PageData>) baseDao.findForList("ItemClosureCheckMapper.queryItemClosureCheckDetailExportExcel", listId);

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

        if (!CollectionUtils.isEmpty(checkInfoList)) {
            for (int i = 0; i < checkInfoList.size(); i++) {
                PageData pd = checkInfoList.get(i);

                HSSFRow row = sheet.createRow(i + 2);
                int j = 0;

                HSSFCell cell = row.createCell(j++);
                cell.setCellValue(i + 1);
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("serialNumber"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("jobTitle"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("approveUserName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("creatorOrg"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("createUser"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("createdDate"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("applyUserName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("postName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("telephone"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("startYear"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("endYear"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("projectAbstract"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("createUser"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                String createTime = pd.getString("createTime");
                cell.setCellValue(createTime.substring(0, createTime.lastIndexOf(".")));
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

    /**
     * 导出PDF
     * @param data
     * @param document
     */
    @Override
    public void exportPDF(PageData data, Document document) throws Exception {
        //设置logo和标题
        PDFUtil.addTitle(document,"研发项目结题验收");

        //设置基本信息
        int width1[] = {140, 400, 140, 300, 140, 160, 150, 250};//每栏的宽度
        String[] argArr1 = {
                "编制单位", data.getString("creatorOrgName"), "创建人", data.getString("createUser"),
                "创建时间", data.getString("createdDate"), "单据编号", data.getString("serialNumber"),
                "成果名称",data.getString("jobTitle"),"项目负责人",data.getString("applyUserName"),
                "负责人岗位",data.getString("postName"),"联系电话",data.getString("telephone"),
                "起始年度",data.getString("startYear"),"结束年度",data.getString("endYear"),
                "结题申报人",data.getString("creatorUser"),"申请评审日期",data.getString("createdDate"),
                "成果内容简介",data.getString("projectAbstract"), "经济技术文件目录及提供单位",data.getString("directoryAndUnit")};
        HashMap<Integer,Integer> mergeMap=new HashMap<>();
        mergeMap.put(25,7);
        mergeMap.put(27, 7);
        PdfPTable baseTableHaveMerge = PDFUtil.createBaseTableHaveMerge(width1, argArr1, mergeMap);

        //人员信息
        String[] argArr2 = {"序号", "姓名", "身份证", "年龄", "性别", "学历", "所属部门", "所属职务", "所学专业", "从事专业", "所在单位", "研究任务及分工",
                "全时率", "联系电话", "参与研究开始日期", "参与研究结束日期", "编制人"};
        int width2[] = {100, 200, 250, 150, 150, 150, 200, 200, 200, 200,200, 200, 200, 200, 200,200, 200};//每栏的宽度
        //明细数据写入
        List<PageData> list = (List<PageData>) baseDao.findForList("ItemClosureCheckMapper.queryItemClosureUserInfoList", data);

        String[] argDetail = null;
        List<String> detailList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            PageData pageData = (PageData) list.get(i);
            argDetail = new String[]{String.valueOf(i + 1), pageData.getString("userName"), pageData.getString("idCard"),
                    pageData.getString("age"), pageData.getString("gender"), pageData.getString("education"),
                    pageData.getString("belongDepartment"), pageData.getString("belongPost"), pageData.getString("majorStudied"),
                    pageData.getString("majorWorked"), pageData.getString("belongUnit"), pageData.getString("taskDivision"),
                    pageData.getString("workRate"), pageData.getString("telephone"), pageData.getString("startDate"),
                    pageData.getString("endDate"), pageData.getString("creatorUser")};
            String jsonString2 = JSON.toJSONString(argDetail);
            detailList.add(jsonString2);
        }
        PdfPTable detailTable = PDFUtil.createDetailTableNotMerge("研发项目人员信息", width2, argArr2, detailList, null, null);


        //设置审批记录
        flowService.getApproveTable2(data,detailTable);

        //将表2合到表1中
        PDFUtil.mergeTable(baseTableHaveMerge,detailTable);
        //添加表格
        document.add(baseTableHaveMerge);
    }

}
