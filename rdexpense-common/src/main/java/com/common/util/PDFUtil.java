package com.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class PDFUtil {


	/**
	 * 嵌套表格
	 *
	 * @param table  主表
	 * @param tables 被合并的从表，可变参数
	 */
/*    public static void mergeTable(PdfPTable table, PdfPTable... tables) {
        for (int i = 0; i < tables.length; i++) {
            PdfPCell tempCell = new PdfPCell(tables[i]);
            tables[i].setSpacingBefore(0);
            tempCell.setPadding(0);
            tempCell.setColspan(table.getNumberOfColumns());
            tempCell.setBorderWidthBottom(0);
            table.addCell(tempCell);
        }

    }*/
	public static void mergeTable(PdfPTable table, PdfPTable... tables) {
		for (int i = 0; i < tables.length; i++) {
			PdfPCell tempCell = new PdfPCell(tables[i]);
			tempCell.setBorderWidthRight(0);
			tempCell.setBorderWidthLeft(0);
			tempCell.setBorderWidthTop(0);
			tables[i].setSpacingBefore(0);//设置表格直接间距
			tempCell.setPadding(0);
			tempCell.setColspan(table.getNumberOfColumns());
			tables[i].setSplitLate(false);
			tables[i].setSplitRows(true);
			table.setSplitLate(false);
			table.setSplitRows(true);
			table.addCell(tempCell);
		}

	}

	public static void mergeTable2(PdfPTable table, PdfPTable... tables) {
		for (int i = 0; i < tables.length; i++) {
			PdfPCell tempCell = new PdfPCell(tables[i]);
			tempCell.setBorderWidthRight(0);
			tempCell.setBorderWidthLeft(0);
			tempCell.setBorderWidthTop(0);
			tempCell.setBorderWidthBottom(0);
			tables[i].setSpacingBefore(0);//设置表格直接间距
			tempCell.setPadding(0);
			tempCell.setColspan(table.getNumberOfColumns());
			tables[i].setSplitLate(false);
			tables[i].setSplitRows(true);
			table.setSplitLate(false);
			table.setSplitRows(true);
			table.addCell(tempCell);
		}

	}

	public static String toCH(int intInput) {
		String si = String.valueOf(intInput);
		String sd = "";
		if (si.length() == 1) // 個
		{
			sd += getCH(intInput);
			return sd;
		} else if (si.length() == 2)// 十
		{
			if (si.substring(0, 1).equals("1"))
				sd += "十";
			else
				sd += (getCH(intInput / 10) + "十");
			sd += toCH(intInput % 10);
		} else if (si.length() == 3)// 百
		{
			sd += (getCH(intInput / 100) + "百");
			if (String.valueOf(intInput % 100).length() < 2)
				sd += "零";
			sd += toCH(intInput % 100);
		} else if (si.length() == 4)// 千
		{
			sd += (getCH(intInput / 1000) + "千");
			if (String.valueOf(intInput % 1000).length() < 3)
				sd += "零";
			sd += toCH(intInput % 1000);
		} else if (si.length() == 5)// 萬
		{
			sd += (getCH(intInput / 10000) + "萬");
			if (String.valueOf(intInput % 10000).length() < 4)
				sd += "零";
			sd += toCH(intInput % 10000);
		}

		return sd;
	}

	private static String getCH(int input) {
		String sd = "";
		switch (input) {
			case 1:
				sd = "一";
				break;
			case 2:
				sd = "二";
				break;
			case 3:
				sd = "三";
				break;
			case 4:
				sd = "四";
				break;
			case 5:
				sd = "五";
				break;
			case 6:
				sd = "六";
				break;
			case 7:
				sd = "七";
				break;
			case 8:
				sd = "八";
				break;
			case 9:
				sd = "九";
				break;
			default:
				break;
		}
		return sd;
	}

	public static Map<String, Font> getFont() {
		Map<String, Font> map = new HashMap<>();
		//中文字体,解决中文不能显示问题
		BaseFont bfChinese = null;
		try {
			bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		} catch (DocumentException e) {
			log.info("requirement exportPDF errorMsg: 设置文档字体编码失败 " + e.getMessage());
			throw new MyException(ConstantMsgUtil.FAIL_STATUS.val(), "系统内部异常，请联系管理员", e);
		} catch (IOException e) {
			log.info("requirement exportPDF errorMsg: 设置文档字体编码 输入输出流 失败  " + e.getMessage());
			throw new MyException(ConstantMsgUtil.FAIL_STATUS.val(), "系统内部异常，请联系管理员", e);
		}
		com.itextpdf.text.Font keyfont = new com.itextpdf.text.Font(bfChinese, 12, com.itextpdf.text.Font.BOLD);
		com.itextpdf.text.Font textfont = new com.itextpdf.text.Font(bfChinese, 10, com.itextpdf.text.Font.NORMAL);
		com.itextpdf.text.Font keyBigFont = new com.itextpdf.text.Font(bfChinese, 20, Font.BOLD);
		map.put("keyFont", keyfont);
		map.put("textFont", textfont);
		map.put("keyBigFont", keyBigFont);
		return map;
	}

	public static String formatStr(double number, int newValue) {
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(newValue);
		return numberFormat.format(number);
	}


	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
		}
		return output.toByteArray();
	}

	//设置logo和标题
	public static void addTitle(Document document, String titleName) throws DocumentException, IOException {
		//   String path = PDFUtil.class.getResource("/static/logo.png").getPath();
		//String property = System.getProperty("user.dir");
		//String path = property + "\\equipmentBusiness-manager\\src\\main\\resources\\static\\logo.png";
		//      Image image = Image.getInstance(path);

		InputStream inStream = PDFUtil.class.getResourceAsStream("/static/logo.png");
		Image image = Image.getInstance(toByteArray(inStream));

		image.setIndentationLeft(10f);
		image.scaleAbsolute(168, 29);
		document.add(image);
		Paragraph blankRow1 = new Paragraph(titleName, PDFUtil.getFont().get("keyBigFont"));
		blankRow1.setAlignment(Element.ALIGN_CENTER);
		document.add(blankRow1);
	}


	/**
	 * 设置带合并的基本表格
	 *
	 * @param widths   宽度
	 * @param data     数据
	 * @param mergeMap 合并设置
	 * @return
	 * @throws DocumentException
	 */
	public static PdfPTable createBaseTableHaveMerge(int[] widths, String[] data, HashMap<Integer, Integer> mergeMap) throws DocumentException {
		int numColumns = widths.length;
		PdfPTable table1 = new PdfPTable(numColumns);
		table1.setSpacingBefore(30f);
		table1.setWidthPercentage(100);
		table1.setWidths(widths); // 设置宽度
		int b = 0;
		for (int i = 0; i < data.length; i++) {
			PdfPCell cell = new PdfPCell(new Paragraph(data[i], PDFUtil.getFont().get("textFont")));
			cell.setMinimumHeight(20);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorderWidthBottom(0);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			if (i == data.length - 1) {
				if (mergeMap != null) {
					int a = 0;
					for (Map.Entry<Integer, Integer> entry : mergeMap.entrySet()) {
						a += entry.getValue() - 1;
					}
					if ((i + a) % numColumns != (numColumns - 1)) {
						cell.setBorderWidthRight(0);
					}
				}
			}
			Integer integer = null;
			if (mergeMap != null) {
				integer = mergeMap.get(i);
			}
			if (integer != null) {
				b += (integer - 1);
			}
			if ((i + b) % numColumns != (numColumns - 1) && i != data.length - 1) {
				cell.setBorderWidthRight(0);
			}
			if (mergeMap != null) {
				for (Map.Entry<Integer, Integer> entry : mergeMap.entrySet()) {
					Integer key = entry.getKey();
					Integer value = entry.getValue();
					if (i == key) {
						cell.setColspan(value);
					}
				}

			}

			table1.addCell(cell);
		}
		return table1;
	}

	/**
	 * 设置带合并的基本表格
	 *
	 * @param widths   宽度
	 * @param data     数据
	 * @param mergeMap 合并设置
	 * @return
	 * @throws DocumentException
	 */
	public static PdfPTable createBaseTableOnlyOne(int[] widths, String[] data, HashMap<Integer, Integer> mergeMap) throws DocumentException {
		int numColumns = widths.length;
		PdfPTable table1 = new PdfPTable(numColumns);
		table1.setSpacingBefore(30f);
		table1.setWidthPercentage(100);
		table1.setWidths(widths); // 设置宽度
		int b = 0;
		for (int i = 0; i < data.length; i++) {
			PdfPCell cell = new PdfPCell(new Paragraph(data[i], PDFUtil.getFont().get("textFont")));
			cell.setMinimumHeight(20);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorderWidthBottom(0);
			if (i == data.length - 1) {
				if (mergeMap != null) {
					int a = 0;
					for (Map.Entry<Integer, Integer> entry : mergeMap.entrySet()) {
						a += entry.getValue() - 1;
					}
					if ((i + a) % numColumns != (numColumns - 1)) {
						cell.setBorderWidthRight(0);
					}
				}
			}
			Integer integer = null;
			if (mergeMap != null) {
				integer = mergeMap.get(i);
			}
			if (integer != null) {
				b += (integer - 1);
			}
			if ((i + b) % numColumns != (numColumns - 1) && i != data.length - 1) {
				cell.setBorderWidthRight(0);
			}
			if (mergeMap != null) {
				for (Map.Entry<Integer, Integer> entry : mergeMap.entrySet()) {
					Integer key = entry.getKey();
					Integer value = entry.getValue();
					if (i == key) {
						cell.setColspan(value);
					}
				}

			}

			table1.addCell(cell);
		}
		PdfPTable temp = new PdfPTable(5);
		int[] width2 = {100, 200, 200, 150, 200};
		temp.setWidths(width2);
		// 设置明细标题，并合并单元格
		PdfPCell keyCell = new PdfPCell(new Paragraph(""));
		keyCell.setMinimumHeight(0);
		keyCell.setRowspan(1);
		keyCell.setColspan(5);
		keyCell.setHorizontalAlignment(Element.ALIGN_CENTER);// 水平居中
		keyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		keyCell.setBorderWidthBottom(0);
		keyCell.setBorderWidthRight(0);
		keyCell.setBorderWidthTop(0);
		temp.addCell(keyCell);
		PDFUtil.mergeTable(table1, temp);

		return table1;
	}


	/**
	 * 创建明细表格(不带合并)
	 *
	 * @param titleName 标题
	 * @param width2    宽度
	 * @param argHead   表头数据
	 * @param argDetail 明细数据
	 * @param argTotal  合计数据
	 * @param right     水平靠右显示序号(从0开始)
	 * @return
	 * @throws DocumentException
	 */
	public static PdfPTable createDetailTableNotMerge(String titleName, int[] width2, String[] argHead, List<String> argDetail, String[] argTotal, Integer[] right) throws DocumentException {
		int columnNumber = argHead.length;
		if (columnNumber != width2.length) {
			throw new MyException("pdf列数设置不一致");
		}
		//背景颜色
		BaseColor baseColor = new BaseColor(224, 239, 255);
		//字体
		Font textFont = getFont().get("textFont");

		PdfPTable table3 = new PdfPTable(columnNumber);
		table3.setSpacingBefore(10f);
		table3.setWidthPercentage(100);
		table3.resetColumnCount(columnNumber);
		table3.setWidths(width2); // 设置宽度
		// 设置明细标题，并合并单元格
		PdfPCell keyCell = new PdfPCell(new Paragraph(titleName, textFont));
		keyCell.setMinimumHeight(20);
		keyCell.setRowspan(1);
		keyCell.setColspan(columnNumber);
		keyCell.setHorizontalAlignment(Element.ALIGN_CENTER);// 水平居中
		keyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		keyCell.setBorderWidthBottom(0);
		keyCell.setBackgroundColor(baseColor);
		table3.addCell(keyCell);

		String[] argArr3 = argHead;
		for (int i = 0; i < argArr3.length; i++) {
			PdfPCell cell = new PdfPCell(new Paragraph(argArr3[i], textFont));
			cell.setMinimumHeight(20);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);//水平居中
			cell.setBorderWidthBottom(0);
			if (i != argArr3.length - 1) {
				cell.setBorderWidthRight(0);
			}
			cell.setBackgroundColor(baseColor);
			table3.addCell(cell);
		}
		// 明细数据写入
		if (!CollectionUtils.isEmpty(argDetail)) {
			for (String detailStr : argDetail) {
				JSONArray detailArr = JSONObject.parseArray(detailStr);
				for (int i = 0; i < detailArr.size(); i++) {
					PdfPCell cell = new PdfPCell(new Paragraph(detailArr.getString(i), textFont));
					cell.setMinimumHeight(20);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setBorderWidthBottom(0);
					if (i != detailArr.size() - 1) {
						cell.setBorderWidthRight(0);
					}
/*                    if (right != null) {
                        List<Integer> list = Arrays.asList(right);
                        if (list.contains(i)) {
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        }
                    }*/
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table3.addCell(cell);
				}

			}

		}
		//合计数据写入
		if (argTotal != null) {
			for (int i = 0; i < argTotal.length; i++) {
				PdfPCell cell = new PdfPCell(new Paragraph(argTotal[i], textFont));
				cell.setMinimumHeight(20);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setBorderWidthBottom(0);
				if (i != argTotal.length - 1) {
					cell.setBorderWidthRight(0);
				}
/*                if (right != null) {
                    List<Integer> list = Arrays.asList(right);
                    if (list.contains(i)) {
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    }
                }*/
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(cell);
			}
		}

		return table3;
	}


	/**
	 * 创建明细表格(不带合并)
	 *
	 * @param titleName    标题
	 * @param width2       宽度
	 * @param argHead      表头数据
	 * @param argHeadMerge 表头合并数据
	 * @param mergeBegin   表头合并索引
	 * @param mergeNum     表头合并列数
	 * @param argDetail    明细数据
	 * @param argTotal     合计数据
	 * @param right        水平靠右显示序号(从0开始)
	 * @return
	 * @throws DocumentException
	 */
	public static PdfPTable createDetailTableHaveMerge(String titleName, int[] width2, String[] argHead, String[] argHeadMerge, int mergeBegin, int mergeNum, List<String> argDetail, String[] argTotal, Integer[] right) throws DocumentException {
		//表每行列数
		int columnNumber = width2.length;
		//背景颜色
		BaseColor baseColor = new BaseColor(224, 239, 255);
		//字体
		Font textFont = getFont().get("textFont");

		PdfPTable table3 = new PdfPTable(columnNumber);
		table3.setSpacingBefore(10f);
		table3.setWidthPercentage(100);
		table3.resetColumnCount(columnNumber);
		table3.setWidths(width2); // 设置宽度
		// 设置明细标题，并合并单元格
		PdfPCell keyCell = new PdfPCell(new Paragraph(titleName, textFont));
		keyCell.setMinimumHeight(20);
		keyCell.setRowspan(1);
		keyCell.setColspan(columnNumber);
		keyCell.setHorizontalAlignment(Element.ALIGN_CENTER);// 水平居中
		keyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		keyCell.setBorderWidthBottom(0);
		keyCell.setBackgroundColor(baseColor);
		table3.addCell(keyCell);

		for (int i = 0; i < argHead.length; i++) {
			PdfPCell cell = null;
			if (i == mergeBegin) {
				table3.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.setWidthPercentage(100);
				cell = new PdfPCell(new Paragraph(argHead[i], textFont));
				cell.setColspan(mergeNum);
				cell.setBackgroundColor(baseColor);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				if (i != argHead.length - 1) {
					cell.setBorderWidthRight(0);
				}
				table3.addCell(cell);
				for (int j = mergeBegin + 1; j < argHead.length; j++) {
					cell = new PdfPCell(new Paragraph(argHead[j], textFont));
					cell.setMinimumHeight(20);
					cell.setRowspan(2);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBorderWidthBottom(0);
					cell.setBackgroundColor(baseColor);
					if (j != argHead.length - 1) {
						cell.setBorderWidthRight(0);
					}
					table3.addCell(cell);
				}
				for (int l = 0; l < argHeadMerge.length; l++) {
					cell = new PdfPCell(new Paragraph(argHeadMerge[l], textFont));
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBorderWidthBottom(0);
					cell.setBorderWidthTop(0);
					cell.setBorderWidthRight(0);
					cell.setBackgroundColor(baseColor);
					table3.addCell(cell);
				}
			}
			if (i < mergeBegin) {
				cell = new PdfPCell(new Paragraph(argHead[i], textFont));
				cell.setMinimumHeight(20);
				cell.setRowspan(2);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);//水平居中
				cell.setBorderWidthBottom(0);
				cell.setBorderWidthRight(0);
				cell.setBackgroundColor(baseColor);
				table3.addCell(cell);
			}
		}
		// 明细数据写入
		if (!CollectionUtils.isEmpty(argDetail)) {
			for (String detailStr : argDetail) {
				JSONArray detailArr = JSONObject.parseArray(detailStr);
				for (int i = 0; i < detailArr.size(); i++) {
					PdfPCell cell = new PdfPCell(new Paragraph(detailArr.getString(i), textFont));
					cell.setMinimumHeight(20);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setBorderWidthBottom(0);
					if (i != detailArr.size() - 1) {
						cell.setBorderWidthRight(0);
					}
/*                    if (right != null) {
                        List<Integer> list = Arrays.asList(right);
                        if (list.contains(i)) {
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        }
                    }*/
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table3.addCell(cell);
				}

			}

		}
		//合计数据写入
		if (argTotal != null) {
			for (int i = 0; i < argTotal.length; i++) {
				PdfPCell cell = new PdfPCell(new Paragraph(argTotal[i], textFont));
				cell.setMinimumHeight(20);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setBorderWidthBottom(0);
				if (i != argTotal.length - 1) {
					cell.setBorderWidthRight(0);
				}
/*                if (right != null) {
                    List<Integer> list = Arrays.asList(right);
                    if (list.contains(i)) {
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    }
                }*/
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table3.addCell(cell);
			}
		}

		return table3;
	}

	//保留两位小数
	public static String formatTwo(String s) {
		DecimalFormat df = new DecimalFormat("0.00");
		if (StringUtils.isBlank(s)) {
			s = "";
		} else {
			s = df.format(new BigDecimal(s));
		}
		return s;
	}
}
