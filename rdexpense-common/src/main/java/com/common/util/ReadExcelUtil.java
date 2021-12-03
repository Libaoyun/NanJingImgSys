package com.common.util;

import com.common.base.exception.MyException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;


/**
 * @Auther: luxiangbao
 * @Date: 2020/10/9 16:05
 * @Description: 读取上传的excel文件中的单元格中的值
 */
public class ReadExcelUtil {

    private static DecimalFormat df = new DecimalFormat("0.00");

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 读取字符串类型的单元格数据
     *
     * @param cell      单元格对象
     * @param rowNum    行数 从1开始
     * @param cellName  单元格名称
     * @param flag      标识是否必填 false:否 true:是
     * @param length 校验字符串最大长度
     * @return
     */
    public static String readCellStr(XSSFCell cell, Integer rowNum, String cellName, Boolean flag, Integer length) {
        String value = "";
        if (cell != null && (cell.getCellType() == HSSFCell.CELL_TYPE_STRING || cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)) {
            if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                value = cell.getStringCellValue().trim();
            } else {
                value = cell.getNumericCellValue() + "";
            }

        }

        //校验字符串最大长度
        if (StringUtils.isNotBlank(value)) {
            CheckParameter.stringLength(value, rowNum, cellName, length);
        }


        //校验是否必填
        if (flag == true && StringUtils.isBlank(value)) {
            String error = rowNum + ";" + cellName;
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.ERR_CELL_EMPTY.desc(), error));
        }


        return value;
    }

    /**
     * 读取数值类型的单元格数据
     *
     * @param cell      单元格对象
     * @param rowNum    行数 从1开始
     * @param cellName  单元格名称
     * @param flag      标识是否必填 false:否 true:是
     * @param length 字符串最大长度
     * @param point 小数点位数
     * @return
     */
    public static String readCellDecimal(XSSFCell cell, Integer rowNum, String cellName, Boolean flag, Integer length, Integer point) {
        String value = "";
        if (cell != null && cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
            value = cell.getNumericCellValue()+"";

        }

        //校验数字类型的长度及小数点位数
        if (StringUtils.isNotBlank(value)) {
//            value = df.format(value);
            CheckParameter.checkDecimal(value, rowNum, cellName, length, point);
        }

        if (flag == true && StringUtils.isBlank(value)) {
            String error = rowNum + ";" + cellName;


            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.ERR_CELL_EMPTY.desc(), error));
        }

        return value;
    }


    /**
     * 读取需要格式化的单元格数据
     * @param cell      单元格对象
     * @param rowNum    行数 从1开始
     * @param cellName  单元格名称
     * @param flag      标识是否必填 false:否 true:是
     * @param length 校验字符串最大长度
     * @return
     */
    public static String readCellFormat(XSSFCell cell, Integer rowNum, String cellName, Boolean flag, Integer length) {
        DataFormatter dataFormatter = new DataFormatter();
        String value = dataFormatter.formatCellValue(cell);


        if (StringUtils.isNotBlank(value)) {
            CheckParameter.stringLength(value, rowNum, cellName, length);
        }

        if (flag == true && StringUtils.isBlank(value)) {
            String error = rowNum + ";" + cellName;
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.ERR_CELL_EMPTY.desc(), error));
        }

        return value;
    }

    /**
     * 读取日期格式的数据
     * @param cell      单元格对象
     * @param rowNum    行数 从1开始
     * @param cellName  单元格名称
     * @param flag      标识是否必填 false:否 true:是
     * @return
     */
    public static String readCellDate(XSSFCell cell, Integer rowNum, String cellName, Boolean flag) {
        DataFormatter dataFormatter = new DataFormatter();
        String value = dataFormatter.formatCellValue(cell);


        if (StringUtils.isNotBlank(value)) {
            try {
                dateFormat.parse(value);
            } catch (Exception e) {
                String error = rowNum + ";" + cellName;
                throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.ERR_CELL_DATE.desc(), error));
            }
        }

        if (flag == true && StringUtils.isBlank(value)) {
            String error = rowNum + ";" + cellName;
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.ERR_CELL_EMPTY.desc(), error));
        }

        return value;
    }


    /**
     * 读取时间格式的数据
     * @param cell      单元格对象
     * @param rowNum    行数 从1开始
     * @param cellName  单元格名称
     * @param flag      标识是否必填 false:否 true:是
     * @param length 校验字符串最大长度
     * @return
     */
    public static String readCelltime(XSSFCell cell, Integer rowNum, String cellName, Boolean flag) {
        DataFormatter dataFormatter = new DataFormatter();
        String value = dataFormatter.formatCellValue(cell);


        if (StringUtils.isNotBlank(value)) {
            try {
                timeFormat.parse(value);
            } catch (Exception e) {
                String error = rowNum + ";" + cellName;
                throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.ERR_CELL_TIME.desc(), error));
            }
        }

        if (flag == true && StringUtils.isBlank(value)) {
            String error = rowNum + ";" + cellName;
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.ERR_CELL_EMPTY.desc(), error));
        }

        return value;
    }


    /**
     * 读取字符串类型的单元格数据
     *
     * @param cell      单元格对象
     * @param rowNum    行数 从1开始
     * @param cellName  单元格名称
     * @param flag      标识是否必填 false:否 true:是
     * @param length 校验字符串最大长度
     * @return
     */
    public static String readCellMinStr(XSSFCell cell, Integer rowNum, String cellName,Integer length) {
        String value = "";
        if (cell != null && (cell.getCellType() == HSSFCell.CELL_TYPE_STRING || cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)) {
            if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                value = cell.getStringCellValue().trim();
            } else {
                value = cell.getNumericCellValue() + "";
            }

        }

        //校验字符串最小长度
        if (StringUtils.isNotBlank(value)) {
            CheckParameter.stringMinLength(value, rowNum, cellName, length);
        }


        return value;
    }

}
