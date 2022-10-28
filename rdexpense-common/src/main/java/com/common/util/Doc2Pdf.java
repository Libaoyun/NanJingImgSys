package com.common.util;

import com.aspose.words.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author rdexpense
 * 导出word转成pdf工具类
 * @date 2020/4/30 13:27
 */
//@Slf4j
public class Doc2Pdf {
    private static Logger log = Logger.getLogger(Doc2Pdf.class);

    public static boolean getLicense() throws Exception{
        boolean result = false;
        try {
            //新建一个空白pdf文档
            // license.xml找个路径放即可。
            InputStream is = Doc2Pdf.class.getResourceAsStream("/config/License.xml");
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     *
     * @param inPath word文件路径
     * @param outPath pdf文件路径
     * @throws Exception
     */
    public static void doc2pdf(String inPath, String outPath) throws Exception{
        // 验证License 若不验证则转化出的pdf文档会有水印产生
        if (!getLicense()) {
            return;
        }
        File file = new File(outPath);
        FileOutputStream os = null;
        try {
            long old = System.currentTimeMillis();
            os = new FileOutputStream(file);
            Document doc = new Document(inPath);

            //思源宋体
            String fontSerif = System.getProperty("user.dir")+"/config/";
            //思源黑体
            String fontSans = System.getProperty("user.dir")+"/config/";
            String simsun = System.getProperty("user.dir")+"/config/";

//            String fontSerif = "C:\\Users\\ghf96\\Desktop\\字体";
//            //思源黑体
//            String fontSans = "C:\\Users\\ghf96\\Desktop\\字体";;
//            String simsun = "C:\\Users\\ghf96\\Desktop\\字体";;

            log.info("==============思源黑体========="+fontSans);
            log.info("==============思源宋体========="+fontSerif);
            log.info("==============宋体========="+simsun);
            String[] fontsPath = new String[]{fontSerif, fontSans,simsun};
            FontSettings.setFontsFolders(fontsPath, false);
//            DocumentBuilder builder = new DocumentBuilder(doc);
//            Font font = builder.getFont();
//            font.setSize(12);

            DocumentBuilder builder = new DocumentBuilder(doc);
            Font font = builder.getFont();
            font.setSize(12);
            // 全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF,
            doc.save(os, SaveFormat.PDF);
            long now = System.currentTimeMillis();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒");
        } catch (Exception e) {
            throw e;
        }finally {
            if(os!=null) {
                try {
                    os.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }
    }
}
