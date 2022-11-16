package com.common.util;

import lombok.val;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAlignType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

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
     * 导出表格无序号字段
     * @param tableList 填充表格数据
     * @param document  word文档对象
     * @param tableNum 填充的第几个表格
     * @return
     * @throws Exception
     */
    public XWPFDocument exportWordDisorder4(List<List<String>> tableList, XWPFDocument document, int tableNum) throws Exception {

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
                if (j == 0){
                    setCellText(newRow.getCell(j),strings1);
                } else {
                    XWPFParagraph cellP = newRow.getCell(j).getParagraphs().get(0);
                    XWPFRun cellR = cellP.createRun();
                    cellP.setAlignment(ParagraphAlignment.CENTER);
                    cellR.setText(strings1);
                    cellR.setFontFamily("宋体");
                    cellR.setBold(false);
                    cellR.setFontSize(12);
                }

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
    public XWPFDocument exportWordDisorder19(List<List<String>> tableList, XWPFDocument document, int tableNum) throws Exception {

        //获取所有表格
        List<XWPFTable> tables = document.getTables();
        //这里简单取第一个表格
        XWPFTable table = tables.get(tableNum);
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        tblPr.getTblW().setType(STTblWidth.AUTO);

        setCellStyle(document,tableNum,0,0);
        setCellStyle(document,tableNum,1,0);

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
//        XWPFParagraph paragraph2 = document.createParagraph();
//        XWPFRun paragraphRun2 = paragraph2.createRun();
//        paragraphRun2.setText("\r");
        return document;

    }


    /**
     * 跨行合并 单元格
     * @param document
     * @param tableNum
     * @param beginRowIndex 开始行
     * @param endRowIndex 结束行
     * @param colIndex 合并列

     */

    public void mergeCellsHorizontal(XWPFDocument document,int tableNum,int beginRowIndex,int endRowIndex ,int startColIndex,int colIndex){
        //获取所有表格
        List<XWPFTable> tables = document.getTables();
        //这里简单取第一个表格
        XWPFTable table = tables.get(tableNum);

        if(beginRowIndex == endRowIndex || beginRowIndex > endRowIndex){
            return;
        }

        CTVMerge startMerge = CTVMerge.Factory.newInstance();
        startMerge.setVal(STMerge.RESTART);


        CTVMerge endMerge = CTVMerge.Factory.newInstance();
        endMerge.setVal(STMerge.CONTINUE);

        table.getRow(beginRowIndex).getCell(colIndex).getCTTc().getTcPr().setVMerge(startMerge);
        for(int i = beginRowIndex +1;i<=endRowIndex;i++){
            table.getRow(i).getCell(colIndex).getCTTc().getTcPr().setVMerge(endMerge);
        }
    }


    /**
     * 跨列合并单元格
     * @param document
     * @param tableNum
     * @param beginColIndex 开始列
     * @param endColIndex   结束列
     * @param rowIndex 合并行
     */

    public void mergeCellsVertically(XWPFDocument document,int tableNum,int beginColIndex,int endColIndex ,int rowIndex){
        //获取所有表格
        List<XWPFTable> tables = document.getTables();
        //这里简单取第一个表格
        XWPFTable table = tables.get(tableNum);

        if(beginColIndex == endColIndex || beginColIndex > endColIndex){
            return;
        }

        if (table.getRow(rowIndex) != null){
            for(int i = beginColIndex;i <= endColIndex;i++){
                XWPFTableCell cell = table.getRow(rowIndex).getCell(i);

                if(i == beginColIndex){
                    cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
                    //设置合并后字体居中显示
                    cell.getCTTc().addNewTcPr().addNewVAlign().setVal(STVerticalJc.CENTER);
                    cell.getCTTc().getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
                }else {
                    cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
                }
            }
        }
    }



    /**
     * 设置字体居中显示
     * @param document
     * @param tableNum
     * @param colIndex 列
     * @param rowIndex 行
     */

    public void setCellStyle(XWPFDocument document,int tableNum,int colIndex ,int rowIndex) {
        //获取所有表格
        List<XWPFTable> tables = document.getTables();
        //这里简单取第一个表格
        XWPFTable table = tables.get(tableNum);

        XWPFTableCell cell = table.getRow(rowIndex).getCell(colIndex);

        cell.getCTTc().addNewTcPr().addNewVAlign().setVal(STVerticalJc.CENTER);
        cell.getCTTc().getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
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
        cellP.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun cellR = cellP.createRun();
        cellR.setText(text);
        cellR.setFontFamily("宋体");
        cellR.setBold(false);
        cellR.setFontSize(12);
    }

    public XWPFDocument exportWordDisorder1(String param, XWPFDocument document, int tableNum,int rowNUm,int cellNum) throws Exception {
        //获取所有表格
        List<XWPFTable> tables = document.getTables();
        //这里简单取第一个表格
        XWPFTable table = tables.get(tableNum);
        XWPFTableRow row = table.getRow(rowNUm);
        XWPFTableCell cell = row.getCell(cellNum);
        for (XWPFParagraph paragraph : cell.getParagraphs()) {
            for (XWPFRun run : paragraph.getRuns()) {
                if(param.contains("\n")){
                    String[] lines = param.split("\n");
                    if (lines.length > 0) {
                        // set first line into XWPFRun
                        run.setText(lines[0], 0);
                        for (int i = 1; i < lines.length; i++) {
                            // add break and insert new text
                            run.addBreak();
                            run.setText(lines[i]);
                        }
                    }
                } else {
                    run.addBreak();
                    run.setText(param);
                }
            }
        }
        return document;
    }

    /**
          * 跨行合并   表格的下标是从0开始的
          * @param table 操作的是哪个表格
          * @param col  在第几列合并 
          * @param fromRow 在哪个列上开始合并
          * @param toRow  在哪个列上结束合并
          */
    public void MergeCellsVertically1(XWPFTable table, int col, int fromRow, int toRow) {
        for (int i = fromRow; i <= toRow; i++) {
            XWPFTableCell cell = table.getRow(i).getCell(col);
            if ( i == fromRow ) {
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
                //设置合并后字体居中显示
                cell.getCTTc().addNewTcPr().addNewVAlign().setVal(STVerticalJc.CENTER);
                cell.getCTTc().getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
            } else {
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
            }
        }
    }
    /**
          * 跨列合并   表格的下标是从0开始的
          * @param table 操作的是哪个表格
          * @param row 在第几行合并 
          * @param fromCell 在哪个行上开始合并
          * @param toCell  在哪个行上结束合并
          */
    public void mergeCellsHorizontal1(XWPFTable table, int row, int fromCell, int toCell) {
        for (int i = fromCell; i <= toCell; i++) {
            XWPFTableCell cell = table.getRow(row).getCell(i);
            if ( i == fromCell ) {
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
            } else {
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
            }
        }
    }




}
