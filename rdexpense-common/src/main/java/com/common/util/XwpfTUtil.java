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
                    p2run.setText(cellTextString);
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
                    run.setText(object.toString(), 0);
                    run.setUnderline(UnderlinePatterns.SINGLE);
                }
            }
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