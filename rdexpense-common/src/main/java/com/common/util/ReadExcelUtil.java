package com.common.util;

import com.common.base.exception.MyException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;


/**
 * @Auther: luxiangbao
 * @Date: 2020/10/9 16:05
 * @Description: 读取上传的excel文件中的单元格中的值
 */
public class ReadExcelUtil {


    /**
     * 读取字符串类型的单元格数据
     *
     * @param cell      单元格对象
     * @param rowNum    行数
     * @param cellName  单元格名称
     * @param flag      标识是否必填 false:否 true:是
     * @param length 校验字符串最大长度
     * @return
     */
    public String readCellStr(XSSFCell cell, Integer rowNum, String cellName, Boolean flag, Integer length) {
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
     * @param rowNum    行数
     * @param cellName  单元格名称
     * @param flag      标识是否必填 false:否 true:是
     * @param length 字符串最大长度
     * @param point 小数点位数
     * @return
     */
    public String readCellDecimal(XSSFCell cell, Integer rowNum, String cellName, Boolean flag, Integer length) {
        String value = "";
        if (cell != null && cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
            value = cell.getNumericCellValue()+"";

        }

        //校验数字类型的长度及小数点位数
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
     * 读取需要格式化的单元格数据
     * @param cell      单元格对象
     * @param rowNum    行数
     * @param cellName  单元格名称
     * @param flag      标识是否必填 false:否 true:是
     * @param length 校验字符串最大长度
     * @return
     */
    public String readCellFormat(XSSFCell cell, Integer rowNum, String cellName, Boolean flag, Integer length, Integer point) {
        DataFormatter dataFormatter = new DataFormatter();
        String value = dataFormatter.formatCellValue(cell);

        //校验数字类型的长度及小数点位数
        if (StringUtils.isNotBlank(value)) {
            CheckParameter.checkDecimal(value, rowNum, cellName, length, point);
        }

        if (flag == true && StringUtils.isBlank(value)) {
            String error = rowNum + ";" + cellName;
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.ERR_CELL_EMPTY.desc(), error));
        }

        return value;
    }

}
