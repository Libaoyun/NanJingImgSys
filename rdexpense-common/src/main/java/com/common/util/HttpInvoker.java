package com.common.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * http请求工具类
 */
public class HttpInvoker {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpInvoker.class);

    /**
     * 微信官方允许一天刷2000次 所以拟定2分钟刷新一次 24*60 ／ 2
     *
     * @param sUrl
     * @param sMethod
     * @param sOutput
     * @return
     */
    public static JSONObject exec(String sUrl, String sMethod, String sOutput) {
        JSONObject json = null;
        StringBuffer buffer = new StringBuffer();

        HttpURLConnection con = null;
        try {
            URL url = new URL(sUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestMethod(sMethod);
            con.setReadTimeout(60000);
            con.setConnectTimeout(60000);

            if (sOutput != null) {
                OutputStream os = con.getOutputStream();
                try {
                    os.write(sOutput.getBytes("UTF-8"));
                } catch (Exception e) {
                    LOGGER.info("HttpInvoker exec error: {}", e);
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            LOGGER.info("HttpInvoker exec error: {}", e);
                        }
                    }
                }
            }

            InputStream is = null;
            InputStreamReader inputReader = null;
            BufferedReader reader = null;
            try {
                is = con.getInputStream();
                inputReader = new InputStreamReader(is, "UTF-8");
                reader = new BufferedReader(inputReader);
                String temp;
                while ((temp = reader.readLine()) != null) {
                    buffer.append(temp);
                }
            } catch (Exception e) {
                LOGGER.info("HttpInvoker exec error: {}", e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        LOGGER.info("HttpInvoker exec error: {}", e);
                    }
                }
                if (inputReader != null) {
                    try {
                        inputReader.close();
                    } catch (IOException e) {
                        LOGGER.info("HttpInvoker exec error: {}", e);
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        LOGGER.info("HttpInvoker exec error: {}", e);
                    }
                }
            }

            // con.disconnect();
            json = JSONObject.parseObject(buffer.toString());

            if (json != null) {
                LOGGER.info("OK, http连接Url: {}, 返回数据,json: {}", sUrl, json);
            } else {
                LOGGER.info("return json is null, http连接Url: {}, 返回数据,json: {}", sUrl, json);
            }
        } catch (IOException e) {
            LOGGER.info("HttpInvoker exec error: {}", e);
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return json;
    }

}
