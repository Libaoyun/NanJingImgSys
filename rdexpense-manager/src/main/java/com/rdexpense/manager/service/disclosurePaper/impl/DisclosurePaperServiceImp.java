package com.rdexpense.manager.service.disclosurePaper.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.entity.PageData;
import com.common.util.*;
import com.rdexpense.manager.service.disclosurePaper.DisclosurePaperService;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

@Service
public class DisclosurePaperServiceImp implements DisclosurePaperService {

    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;

    @Override
    public List<PageData> queryDisclosurePaperList(PageData pd) {
        List<PageData> DisclosurePaperList = (List<PageData>) baseDao.findForList("DisclosurePaperMapper.selectDisclosurePaperInfoAll", pd);

        return DisclosurePaperList;

    }

    @Override
    @Transactional
    public void addDisclosurePaper(PageData pd) {
        String businessId = "XMJDS-" + UUID.randomUUID().toString(); //生成业务id主键
        String serialNumber = "MXJDS" + SequenceUtil.generateSerialNo().substring(4); //生成流水号
        pd.put("serialNumber", serialNumber);
        pd.put("businessId", businessId);
        pd.put("creatorUserId", pd.getString("createUserId"));
        pd.put("createUserName", pd.getString("createUser"));


        baseDao.insert("DisclosurePaperMapper.insertDisclosurePaper", pd);

    }


    @Override
    @Transactional
    public void deleteDisclosurePaper(PageData pd) {

        List<String> idList = JSONArray.parseArray(pd.getString("idList"), String.class);

        if (!CollectionUtils.isEmpty(idList)) {
            baseDao.batchDelete("DisclosurePaperMapper.deleteDisclosurePaper", idList);
        }

    }

    @Override
    @Transactional
    public void updateDisclosurePaper(PageData pd) {
        // 修改项目表内容
        baseDao.update("DisclosurePaperMapper.updateDisclosurePaper", pd);
    }


    @Override
    public PageData getDisclosurePaperDetail(PageData pd) {
        PageData request = (PageData) baseDao.findForObject("DisclosurePaperMapper.queryById", pd);
        return request;
    }



    @Override
    public List<PageData> getDisclosurePaperDetailList(PageData pd) {

        // 前端的多个状态
        List<String> idList = JSONArray.parseArray(pd.getString("idList"), String.class);

        // 查询数据信息
        List<PageData> reportInfoList = (List<PageData>) baseDao.findForList("DisclosurePaperMapper.queryByIds", idList);
        return reportInfoList;
    }



    @Override
    public HSSFWorkbook exportExcel(PageData pageData) {
        String title = "项目交底书信息";
        String[] head = {"序号", "单据编号", "项目名称", "项目负责人", "岗位", "联系电话", "交底书编制人", "编制日期", "编制年度", "编制季度", "编制人", "创建日期", "更新日期"};
        String idStr = pageData.getString("idList");
        List<String> listId = JSONObject.parseArray(idStr, String.class);
        List<PageData> DisclosurePaperList = (List<PageData>) baseDao.findForList("DisclosurePaperMapper.queryByIds", listId);
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

        if (!CollectionUtils.isEmpty(DisclosurePaperList)) {
            for (int i = 0; i < DisclosurePaperList.size(); i++) {
                PageData pd = DisclosurePaperList.get(i);

                HSSFRow row = sheet.createRow(i + 2);
                int j = 0;

                HSSFCell cell = row.createCell(j++);
                cell.setCellValue(i + 1);
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("serialName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("projectName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("projectLeaderName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("leaderPostName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("contactNumber"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("preparedName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                String time = pd.getString("preparedDate");
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("preparedYear"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("preparedQuarter"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("updateTime"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("createTime"));
                cell.setCellStyle(styleCell);


            }
        }
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
     * 创建存放临时文件的文件夹
     *
     * @return
     */
    @Override
    public File createFile() {
        //根据文件的项目路径，获得文件的系统路径。
        String projectPath = System.getProperty("user.dir");
        //存放临时文件
        File file = new File(projectPath + "\\temporaryfiles");
        //如果文件夹不存在
        if (!file.exists()) {
            //创建文件夹
            file.mkdir();
        }
        return file;
    }



}

