package com.common.util;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

/**
 * 功能描述: poi导出excel工具类
 * @Author: rdexpense
 * @Date: 2020/4/24 11:25
 */
public class ExcelUtil {

    /**
     * 设置表头样式
     *
     * @param wb
     * @param sheet
     * @return
     */
    public static HSSFCellStyle setHeader(HSSFWorkbook wb, HSSFSheet sheet) {
        sheet.setDefaultColumnWidth(15);// 设置默认行宽
        // 表头样式（加粗，水平居中，垂直居中）
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平居中
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直居中
        // 设置边框样式
        HSSFFont fontStyle = wb.createFont();
        fontStyle.setFontHeightInPoints((short) 13);
        fontStyle.setFontName("微软雅黑");
        fontStyle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
        cellStyle.setFont(fontStyle);
        return cellStyle;
    }

    /**
     * 设置表头居左样式
     *
     * @param wb
     * @param sheet
     * @return
     */
    public static HSSFCellStyle setHeaderLeft(HSSFWorkbook wb, HSSFSheet sheet) {
        // 字段样式
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);// 水平居中
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直居中
        HSSFFont fontStyle = wb.createFont();
        fontStyle.setFontHeightInPoints((short) 10);
        fontStyle.setFontName("微软雅黑");
        cellStyle.setFont(fontStyle);
        cellStyle.setWrapText(true);
        // 设置边框样式
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
        return cellStyle;
    }

    public static HSSFCellStyle setHeaderBlod(HSSFWorkbook wb, HSSFSheet sheet) {
        // 副表头样式
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平居中
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直居中
        // 设置边框样式
        HSSFFont fontStyle = wb.createFont();
        fontStyle.setFontHeightInPoints((short) 10);
        fontStyle.setFontName("微软雅黑");
        fontStyle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
        cellStyle.setFont(fontStyle);

        // 设置边框样式
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框

        return cellStyle;
    }


    public static HSSFCellStyle setHeader2(HSSFWorkbook wb, HSSFSheet sheet) {
        // 副表头样式
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平居中
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直居中
        // 设置边框样式
        HSSFFont fontStyle = wb.createFont();
        fontStyle.setFontHeightInPoints((short) 10);
        fontStyle.setFontName("微软雅黑");
        cellStyle.setFont(fontStyle);

        return cellStyle;
    }

    /**
     * 设置标题样式
     *
     * @param wb
     * @param sheet
     * @return
     */
    public static HSSFCellStyle setTitle(HSSFWorkbook wb, HSSFSheet sheet) {
        // 标题样式
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平居中
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直居中
        HSSFFont fontStyle = wb.createFont();
        fontStyle.setFontHeightInPoints((short) 10);
        fontStyle.setFontName("微软雅黑");
        cellStyle.setFont(fontStyle);
        // 设置边框样式
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
        return cellStyle;
    }

    /**
     * 设置内容样式
     *
     * @param wb
     * @param sheet
     * @return
     */
    public static HSSFCellStyle setCell(HSSFWorkbook wb, HSSFSheet sheet) {
        // 字段样式
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平居中
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直居中
        HSSFFont fontStyle = wb.createFont();
        fontStyle.setFontHeightInPoints((short) 10);
        fontStyle.setFontName("微软雅黑");
        cellStyle.setFont(fontStyle);
        cellStyle.setWrapText(true);// 设置自动换行
        // 设置边框样式
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
        return cellStyle;
    }

    /**
     * 合并单元格 参数：起始行号，终止行号， 起始列号，终止列号
     *
     * @param wb
     * @param sheet
     * @param starRow
     * @param endRow
     * @param starColumn
     * @param endColumn
     */
    public static void merge(HSSFWorkbook wb, HSSFSheet sheet, int starRow, int endRow, int starColumn, int endColumn) {
        CellRangeAddress cra =new CellRangeAddress(starRow, endRow, starColumn, endColumn);

        sheet.addMergedRegion(cra);

        RegionUtil.setBorderBottom(1, cra, sheet,wb); // 下边框

        RegionUtil.setBorderLeft(1, cra, sheet,wb); // 左边框

        RegionUtil.setBorderRight(1, cra, sheet,wb); // 右边框

        RegionUtil.setBorderTop(1, cra, sheet,wb); // 上边框

    }

}
