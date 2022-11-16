package com.common.util;

import com.common.entity.PageData;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @Auther: weixuesong
 * @Date: 2020/10/27 17:20
 * @Description:
 */
@Slf4j
@SuppressWarnings("all")
public class AppExportExcelUtil {

    public static PageData excelUpload(HSSFWorkbook wb, String fileName) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        wb.write(bos);
        byte[] barray = bos.toByteArray();
        InputStream is = new ByteArrayInputStream(barray);
        MultipartFile multipartFile = new MockMultipartFile(fileName, fileName, "application/vnd.ms-excel", is);
        PageData pageData = AwsUtil.uploadForServer(multipartFile, ConstantValUtil.BUCKET_PRIVATE);
        return pageData;
    }

    public static PageData pdfUpload(MultipartFile multipartFile, String fileName) {
        PageData pageData = AwsUtil.uploadForServer(multipartFile, ConstantValUtil.BUCKET_PRIVATE);
        return pageData;
    }

    public static PageData fileResponseForApp(String path, HttpServletResponse response) throws Exception {
        File file = new File(path);
        String fileName = file.getName();
        String fN = fileName;
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("将pdf转为输出流异常");
            throw e;
        }
//        BufferedInputStream bis = null;
//        OutputStream os = null;
//        response.reset();
//        response.setCharacterEncoding("utf-8");
        // word格式
       /* if (ext.equals("docx")) {
            response.setContentType("application/msword");
        } else if (ext.equals("pdf")) {
            // pdf
            response.setContentType("application/pdf");
        }
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);*/
        try {
            InputStream inputStream = new FileInputStream(file);
            MultipartFile multipartFile = null;
            // word格式
            if (ext.equals("docx")) {
                multipartFile = new MockMultipartFile(fN, fN, "application/msword", inputStream);
            } else if (ext.equals("pdf")) {
                // pdf
                multipartFile = new MockMultipartFile(fN, fN, "application/pdf", inputStream);
            }
            PageData pageData = AppExportExcelUtil.pdfUpload(multipartFile, fileName);
            //os.flush();
            //bis.close();
            //os.close();
            return pageData;

        } catch (IOException e) {
            log.error("将pdf转为输出流异常");
            throw e;
        } finally {
 /*           if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("将pdf转为输出流异常");
                    throw e;
                }
            }*/
        }
    }
}
