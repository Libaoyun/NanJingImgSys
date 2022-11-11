package com.rdexpense.manager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

//@Component
public class Base64ImageUtil {
	//配置文件中配置
//    @Value("${uploadImg.imgPath}")
    private String uploadPath;//此处路径为 D:/upload/

    /**
     * base64字符串转化成图片
     *
     * @param imgData     图片编码
     * @return * @throws IOException
     */
    @SuppressWarnings("finally")//去除警告提示
    public String GenerateImage(String imgData) throws IOException { // 对字节数组字符串进行Base64解码并生成图片
        if (imgData == null) // 图像数据为空
            throw new IllegalArgumentException("图像数据为空");
		//以逗号分割，保留后面部分
        imgData = imgData.split(",")[1];
        OutputStream out = null;
        String filePath = null;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
			//拼接地址
            filePath = uploadPath + randomName() + ".jpg";

            out = new FileOutputStream(filePath);
            // Base64解码
            byte[] b = decoder.decodeBuffer(imgData);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            out.write(b);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
            out.flush();
            out.close();
            return filePath;
        }
    }

    /***
     * 随机生成文件名
     * @return
     */
    public String randomName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Random r = new Random();
        StringBuilder tempName = new StringBuilder();
        tempName.append(sdf.format(new Date())).append(r.nextInt(100));
        return tempName.toString();
    }

}
