package com.rdexpense.manager.service.system.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;

import com.common.entity.PageData;

import com.common.util.ExcelUtil;
import com.common.util.PDFUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.rdexpense.manager.service.system.OrganizationService;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * @author luxiangbao
 * @date 2021/11/12 11:48
 * @description 组织管理实现类
 */
@Service
public class OrganizationServiceImpl implements OrganizationService {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    @Autowired
    private BaseDao baseDao;


    @Override
    public List<PageData> queryOrganizationList(PageData pd) {
        List<PageData> list = (List<PageData>) baseDao.findForList("OrganizationMapper.selectByParams",pd);

        return list;
    }


    @Override
    public PageData getOrganizationDetail(PageData pd) {
        PageData request = (PageData) baseDao.findForObject("OrganizationMapper.queryById", pd);

        return request;
    }

    /**
     * 新增项目
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void addOrganization(PageData pd) {

        //新增项目表
        String orgNumber =  UUID.randomUUID().toString();
        pd.put("orgNumber",orgNumber);

        baseDao.insert("OrganizationMapper.insertOrganization", pd);


    }


    /**
     * 编辑项目
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void updateOrganization(PageData pd) {

        // 修改项目表内容
        baseDao.update("OrganizationMapper.updateOrganization", pd);

    }


    /**
     * 删除项目节点
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void deleteOrganization(PageData pd) {

        List<String> idList = JSONArray.parseArray(pd.getString("idList"), String.class);

        if(!CollectionUtils.isEmpty(idList)){
            //删除组织信息
            baseDao.batchDelete("OrganizationMapper.deleteOrganization", idList);

            //删除用户权限表
            baseDao.batchDelete("OrganizationMapper.deleteAuthorityUser", idList);

            //删除权限菜单表
            baseDao.batchDelete("OrganizationMapper.deleteAuthorityMenu", idList);


        }




    }



    /**
     * 导出Excel
     *
     * @param pageData
     * @return
     */
    @Override
    public HSSFWorkbook exportExcel(PageData pageData) {
        String title = "项目信息";
        String[] head = {"序号","项目名称","项目编号","项目描述","是否启用","创建人","创建日期","更新人","更新日期"};
        String idStr = pageData.getString("idList");
        List<String> listId = JSONObject.parseArray(idStr, String.class);
        //根据idList查询主表
        List<PageData> userInfoList = (List<PageData>) baseDao.findForList("OrganizationMapper.queryByIds", listId);

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
        ExcelUtil.merge(wb, sheet, 0, 0, 0, (head.length-1));
        // 第一行  表头
        HSSFRow rowTitle = sheet.createRow(1);
        HSSFCell hc;
        for (int j = 0; j < head.length; j++) {
            hc = rowTitle.createCell(j);
            hc.setCellValue(head[j]);
            hc.setCellStyle(styleCell);
        }

        if(!CollectionUtils.isEmpty(userInfoList)){
            for(int i=0;i<userInfoList.size();i++){
                PageData pd = userInfoList.get(i);

                HSSFRow row = sheet.createRow(i + 2);
                int j = 0;

                HSSFCell cell = row.createCell(j++);
                cell.setCellValue(i + 1);
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("orgName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("orgNumber"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("remark"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("status").equals("0")?"否":"是");
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("createUser"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                String time = pd.getString("createTime");
                cell.setCellValue(time.substring(0,time.lastIndexOf(".")));
                cell.setCellStyle(styleCell);


                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("updateUser"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                String time1 = pd.getString("updateTime");
                cell.setCellValue(time1.substring(0,time1.lastIndexOf(".")));
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
     *
     * @param pageData
     * @param document
     * @throws Exception
     */
    @Override
    public void exportPDF(PageData pageData, Document document) throws Exception {
        // 中文字体,解决中文不能显示问题
        BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        com.itextpdf.text.Font keyfont = new com.itextpdf.text.Font(bfChinese, 12, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font textfont = new com.itextpdf.text.Font(bfChinese, 10, com.itextpdf.text.Font.NORMAL);
        // 设置标题
        Paragraph blankRow1 = new Paragraph("项目信息", keyfont);
        blankRow1.setAlignment(Element.ALIGN_CENTER);
        document.add(blankRow1);


        //根据idList查询主表
        PageData pd = (PageData) baseDao.findForObject("OrganizationMapper.queryById", pageData);

        //创建一个表格,10为一行有几栏

        PdfPTable table1 = new PdfPTable(8);
        table1.setSpacingBefore(10f);
        table1.setWidthPercentage(100);
        int[] width1 = {150, 300, 150, 300, 150, 300, 150, 300};//每栏的宽度
        table1.setWidths(width1); //设置宽度

        String[] argArr1 = {"项目名称", pd.getString("orgName"), "项目编号", pd.getString("orgNumber"), "项目描述", pd.getString("remark"),  "是否启用", pd.getString("status").equals("0")?"否":"是"};
        for (String arg : argArr1) {
            PdfPCell cell = new PdfPCell(new Paragraph(arg, textfont));
            cell.setMinimumHeight(20);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
        }


        PdfPTable table2 = new PdfPTable(8);
        table2.setSpacingBefore(10f);
        table2.setWidthPercentage(100);
        int[] width2 = {150, 300, 150, 300, 150, 300, 150, 300};//每栏的宽度
        table2.setWidths(width2); //设置宽度

        String createTime = pd.getString("createTime");
        createTime = createTime.substring(0,createTime.lastIndexOf("."));
        String updateTime = pd.getString("updateTime");
        updateTime = updateTime.substring(0,updateTime.lastIndexOf("."));

        String[] argArr2 = {"创建人", pd.getString("createUser"), "创建时间", createTime, "更新人", pd.getString("updateUser"), "更新时间", updateTime};
        for (String arg : argArr2) {
            PdfPCell cell = new PdfPCell(new Paragraph(arg, textfont));
            cell.setMinimumHeight(20);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table2.addCell(cell);
        }



        //将表格二，表格三合入表格一中
        PDFUtil.mergeTable(table1,table2);

        // 添加表格
        document.add(table1);
    }


}
