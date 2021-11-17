package com.common.util;

import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;


/**
 * @Auther: rdexpense
 * @Date: 2021/2/4 14:02
 * @Description: 导出PDF工具类
 */
public class PDFUtil {
	
	/**
	 * 嵌套表格
	 * @param table 主表
	 * @param tables 被合并的从表，可变参数
	 */
	public static void mergeTable(PdfPTable table, PdfPTable... tables) {
		for(int i=0; i<tables.length; i++) {
			PdfPCell tempCell = new PdfPCell(tables[i]);
			tables[i].setSpacingBefore(0);
			tempCell.setPadding(0);
			tempCell.setColspan(table.getNumberOfColumns());
			table.addCell(tempCell);
		}
	}
}
