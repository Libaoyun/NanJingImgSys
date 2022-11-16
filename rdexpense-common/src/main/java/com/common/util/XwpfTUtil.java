package com.common.util;

import org.apache.poi.xwpf.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author helu
 * @version 1.0
 * @date 2020/4/21 15:21
 */
public class XwpfTUtil {

    /**
     * 替换段落里面的变量
     *
     * @param doc    要替换的文档
     * @param params 参数
     */
    public void replaceInPara(XWPFDocument doc, Map<String, Object> params) {
        Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
        XWPFParagraph para;
        while (iterator.hasNext()) {
            para = iterator.next();

            this.replaceInPara(para, params);
        }
        //替换表格中的指定文字
        Iterator<XWPFTable> itTable = doc.getTablesIterator();
        while (itTable.hasNext()) {
            XWPFTable xwpfTable = itTable.next();
            int rcount = xwpfTable.getNumberOfRows();
            for (int i = 0; i < rcount; i++) {
                XWPFTableRow row = xwpfTable.getRow(i);
                List<XWPFTableCell> cells = row.getTableCells();
                for (XWPFTableCell cell : cells) {
                    String cellTextString = cell.getText();
                    for (Map.Entry<String, Object> e : params.entrySet()) {
                        Object object = e.getValue();
                        String val = "";
                        if (null != object) {
                            val = e.getValue().toString();
                        }
                        cellTextString = cellTextString.replace(e.getKey(), val);
                    }
                    //表格中字体的设置
                    XWPFParagraph paragraph = cell.addParagraph();
                    cell.removeParagraph(0);
                    XWPFRun p2run = paragraph.createRun();
                    if (cellTextString.contains("\n")){
                        String[] textArr = cellTextString.split("\n");
                        for (int j = 0; j < textArr.length; j++) {
                            if (j == 0){
                                p2run.setText(textArr[j]);
                                //todo 换行
                                p2run.addBreak(BreakClear.ALL);
                            } else if ((j > 0 && j < textArr.length-1)){
                                //todo 缩进两个字符空格
                                p2run.setText("    "+textArr[j]);
                                //todo 换行
                                p2run.addBreak(BreakClear.ALL);
                            } else {
                                //todo 缩进两个字符空格
                                p2run.setText("    "+textArr[j]);
                            }
//                            //todo 换行
//                            p2run.addBreak();
//                            //todo 硬回车
//                            p2run.addCarriageReturn();
                        }
                    } else {
                        p2run.setText(cellTextString);
                    }
//                    p2run.setText(cellTextString);
                    p2run.setFontFamily("宋体");
                    p2run.setFontSize(12);
                    p2run.setBold(false);
                }
            }
        }
    }

    /**
     * 替换段落里面的变量
     *
     * @param para   要替换的段落
     * @param params 参数
     */
    public void replaceInPara(XWPFParagraph para, Map<String, Object> params) {
        List<XWPFRun> runs = para.getRuns();
        for (int i = 0; i < runs.size(); i++) {
            XWPFRun run = runs.get(i);
            String runText = run.toString().trim();
            if (params.containsKey(runText)) {
                Object object = params.get(runText);
                if (null == object) {
                    run.setText("", 0);
                } else {
                    //todo 判断是否包含换行符
                    if (object.toString().contains("\n")){
                        //todo 设置占位符字段为空值
                        run.setText("", 0);
                        run.setUnderline(UnderlinePatterns.NONE);
                        //todo 替换占位符字段的内容
                        String[] textArr = object.toString().split("\n");
                        for (int j = 0; j < textArr.length; j++) {
                            XWPFRun run1 = para.createRun();
                            if (j == 0){
                                run1.setText(textArr[j]);
                                //todo 换行
                                run1.addBreak(BreakClear.ALL);
                            } else if ((j > 0 && j < textArr.length-1)){
                                //todo 缩进两个字符空格
                                run1.setText("    "+textArr[j]);
                                //todo 换行
                                run1.addBreak(BreakClear.ALL);
                            } else {
                                //todo 缩进两个字符空格
                                run1.setText("    "+textArr[j]);
                            }

//                            //todo 换行
//                            run1.addBreak();
//                            //todo 硬回车
//                            run1.addCarriageReturn();
                            run1.setUnderline(UnderlinePatterns.NONE);
                        }
                    } else {
                        run.setText(object.toString(), 0);
                        run.setUnderline(UnderlinePatterns.NONE);
                    }
    //                run.setUnderline(UnderlinePatterns.SINGLE);
                }
            }
            run.setFontFamily("宋体");
            run.setFontSize(12);
            run.setBold(false);
        }
    }


    /**
     * 关闭输入流
     */
    public void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭输出流
     *
     * @param os
     */
    public void close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}