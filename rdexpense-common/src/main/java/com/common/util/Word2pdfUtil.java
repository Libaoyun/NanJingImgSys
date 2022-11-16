package com.common.util;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @author rdexpense
 * @version 输出pdf文件工具类
 * @date 2020/4/22 9:58
 */
@Slf4j
public class Word2pdfUtil {
    public static void fileResponse(String path, HttpServletResponse response) throws Exception{
        File file=new File(path);
        String fileName=file.getName();
        String ext=fileName.substring(fileName.lastIndexOf(".")+1);
        try {
            fileName= URLEncoder.encode(fileName,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("将pdf转为输出流异常");
            throw e;
        }
        BufferedInputStream bis=null;
        OutputStream os=null;
        response.reset();
        response.setCharacterEncoding("utf-8");
        // word格式
        if(ext=="docx") {
            response.setContentType("application/msword");
        }else if(ext=="pdf") {
            // pdf
            response.setContentType("application/pdf");
        }
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        try {
            bis=new BufferedInputStream(new FileInputStream(file));
            byte[] b=new byte[bis.available()+1000];
            int i=0;
            //直接下载导出
            os = response.getOutputStream();
            while((i=bis.read(b))!=-1) {
                os.write(b, 0, i);
            }
            os.flush();
            bis.close();
            os.close();
        } catch (IOException e) {
            log.error("将pdf转为输出流异常");
            throw e;
        }finally {
            if(os!=null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("将pdf转为输出流异常");
                    throw e;
                }
            }
        }
    }

}
