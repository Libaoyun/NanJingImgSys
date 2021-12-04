package com.common.util;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import java.util.List;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/4/21 15:35
 */
public class ExportWordHelper {


    /**
     * 导出表格到word    有序号
     * @param tableList 填充表格数据
     * @param document  word文档对象
     * @param tableNum 填充的第几个表格
     * @return
     * @throws Exception
     */
     public XWPFDocument exportWord(List<List<String>> tableList, XWPFDocument document, int tableNum) throws Exception {

        //获取所有表格
        List<XWPFTable> tables = document.getTables();
        //这里简单取第一个表格
        XWPFTable table = tables.get(tableNum);
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        tblPr.getTblW().setType(STTblWidth.AUTO);

        int newRowNum = table.getNumberOfRows();
        for (int i = 0; i < tableList.size(); i++) {
            //为表格添加行
            XWPFTableRow newRow = table.insertNewTableRow(i+newRowNum);
            newRow.createCell();
            //获取list中的字符串数组
            List<String> strings =  tableList.get(i);
            int ro = i+1;
            int col = 1;
            newRow.getCell(0).setText(String.valueOf(ro));
            for (int j = 0; j < strings.size(); j++) {
                String strings1 =  strings.get(j);
                newRow.createCell();
                setCellText(newRow.getCell(col),strings1);
                col++;
            }
        }
        //换行  
        XWPFParagraph paragraph2 = document.createParagraph();
        XWPFRun paragraphRun2 = paragraph2.createRun();
        paragraphRun2.setText("\r");
        return document;

    }

    /**
     * 导出表格无序号字段
     * @param tableList 填充表格数据
     * @param document  word文档对象
     * @param tableNum 填充的第几个表格
     * @return
     * @throws Exception
     */
    public XWPFDocument exportWordDisorder(List<List<String>> tableList, XWPFDocument document, int tableNum) throws Exception {

        //获取所有表格
        List<XWPFTable> tables = document.getTables();
        //这里简单取第一个表格
        XWPFTable table = tables.get(tableNum);
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        tblPr.getTblW().setType(STTblWidth.AUTO);

        int newRowNum = table.getNumberOfRows();
        for (int i = 0; i < tableList.size(); i++) {
            //为表格添加行
            XWPFTableRow newRow = table.insertNewTableRow(i+newRowNum);
            //获取list中的字符串数组
            List<String> strings =  tableList.get(i);
            for (int j = 0; j < strings.size(); j++) {
                String strings1 =  strings.get(j);
                newRow.createCell();
                setCellText(newRow.getCell(j),strings1);
            }
        }
        //换行  
        XWPFParagraph paragraph2 = document.createParagraph();
        XWPFRun paragraphRun2 = paragraph2.createRun();
        paragraphRun2.setText("\r");
        return document;

    }


    /**
     * 导出表格无序号字段 从指定位置行写
     * @param tableList 填充表格数据
     * @param document  word文档对象
     * @param tableNum 填充的第几个表格
     * @return
     * @throws Exception
     */
    public XWPFDocument exportWordAppoint(List<List<String>> tableList, XWPFDocument document, int tableNum,int newRowNum) throws Exception {

        //获取所有表格
        List<XWPFTable> tables = document.getTables();
        //这里简单取第一个表格
        XWPFTable table = tables.get(tableNum);
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        tblPr.getTblW().setType(STTblWidth.AUTO);

        for (int i = 0; i < tableList.size(); i++) {
            //为表格添加行
            XWPFTableRow newRow = table.insertNewTableRow(i+newRowNum);
            //获取list中的字符串数组
            List<String> strings =  tableList.get(i);
            for (int j = 0; j < strings.size(); j++) {
                String strings1 =  strings.get(j);
                newRow.createCell();
                setCellText(newRow.getCell(j),strings1);
            }
        }
        //换行  
        XWPFParagraph paragraph2 = document.createParagraph();
        XWPFRun paragraphRun2 = paragraph2.createRun();
        paragraphRun2.setText("\r");
        return document;

    }

    public void setCellText(XWPFTableCell cell, String text) throws Exception {
        XWPFParagraph cellP = cell.getParagraphs().get(0);
        XWPFRun cellR = cellP.createRun();
        cellR.setText(text);
        cellR.setFontFamily("宋体");
        cellR.setBold(false);
        cellR.setFontSize(12);
    }

    /**
          * 跨列合并   表格的下标是从0开始的
          * @param table 操作的是哪个表格
          * @param col  在第几行合并 
          * @param fromRow 在哪个列上开始合并
          * @param toRow  在哪个列上结束合并
          */
//    public void mergeCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {
//        for (int i = fromRow; i <= toRow; i++) {
//            XWPFTableCell cell = table.getRow(i).getCell(col);
//            if ( i == fromRow ) {
//                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
//            } else {
//                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
//            }
//        }
//    }
    /**
          * 跨行合并   表格的下标是从0开始的
          * @param table 操作的是哪个表格
          * @param row 在第几列合并 
          * @param fromCell 在哪个行上开始合并
          * @param toCell  在哪个行上结束合并
          */
//    public void mergeCellsHorizontal(XWPFTable table, int row, int fromCell, int toCell) {
//        for (int i = fromCell; i <= toCell; i++) {
//            XWPFTableCell cell = table.getRow(row).getCell(i);
//            if ( i == fromCell ) {
//                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
//            } else {
//                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
//            }
//        }
//    }




}
