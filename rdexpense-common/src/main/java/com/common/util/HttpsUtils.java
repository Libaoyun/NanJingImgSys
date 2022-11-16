package com.common.util;

/**
 * @auther luxiangbao
 * @date 2020/7/21 11:12
 * @describe 实现https请求，可用于加载https路径的存储图片，避开信任证书的验证。
 */

import cn.hutool.http.ssl.DefaultTrustManager;

import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class HttpsUtils {


    private static HttpsURLConnection getHttpsURLConnection(String uri, String method) throws IOException {

        SSLContext ctx = null;

        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());

        } catch (KeyManagementException e) {
            e.printStackTrace();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

        }

        SSLSocketFactory ssf = ctx.getSocketFactory();

        URL url = new URL(uri);

        HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();

        httpsConn.setSSLSocketFactory(ssf);

        httpsConn.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }

        });

        httpsConn.setRequestMethod(method);

        httpsConn.setDoInput(true);

        httpsConn.setDoOutput(true);

        return httpsConn;

    }

    private static byte[] getBytesFromStream(InputStream is) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] kb = new byte[1024];

        int len;

        while ((len = is.read(kb)) != -1) {
            baos.write(kb, 0, len);

        }

        byte[] bytes = baos.toByteArray();

        baos.close();

        is.close();

        return bytes;

    }


    public static byte[] doGet(String uri) throws IOException {

        HttpsURLConnection httpsConn = getHttpsURLConnection(uri, "GET");

        return getBytesFromStream(httpsConn.getInputStream());

    }


    public static InputStream getInputStream(String url) throws IOException{
        HttpsURLConnection httpsConn = getHttpsURLConnection(url, "GET");
        return httpsConn.getInputStream();
    }


}
