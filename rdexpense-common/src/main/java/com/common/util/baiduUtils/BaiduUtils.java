package com.common.util.baiduUtils;


/**
 * @Description 调用百度云的相关接口
 * @Author rdexpense
 * @Date 2020/3/17 12:06
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BaiduUtils {



    /**
     * 获取API访问token
     * 该token有一定的有效期，需要自行管理，当失效时需重新获取.
     *
     * @param apiKey - 百度云官网获取的 API Key
     * @param secretKey - 百度云官网获取的 Securet Key
     */
    public static String getAuth(String apiKey, String secretKey) {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + apiKey
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + secretKey;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            JSONObject jsonObject = JSONObject.parseObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }


    /**
     * 人脸注册
     * @Param accessToken 标识
     * @Param imageUrl 图片url
     * @Param externalUserCode 人员编码
     * @return
     */
    public static String add(String accessToken,String imageUrl,String externalUserCode) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add";
        try {
            Map<String, Object> map = new HashMap<>();

            String imgParam = getImageParam(imageUrl);

            map.put("image", imgParam);
            map.put("group_id", "1");
            map.put("user_id", externalUserCode);
            map.put("user_info", externalUserCode);
            map.put("liveness_control", "NORMAL");
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");

            String param = GsonUtils.toJson(map);

            String result = HttpUtil.post(url, accessToken, "application/json", param);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 人脸搜索
     * @Param accessToken 标识
     * @Param imageUrl 图片url
     * @return
     */
    public static String faceSearch(String accessToken,String imageUrl) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/search";
        try {
            Map<String, Object> map = new HashMap<>();
            String imgParam = getImageParam(imageUrl);

            map.put("image", imgParam);
            map.put("liveness_control", "NORMAL");
            map.put("group_id_list", "1");
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");

            String param = GsonUtils.toJson(map);

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 人脸删除
     * @Param accessToken 标识
     * @Param imageUrl 图片url
     * @Param externalUserCode 人员编码
     * @return
     */
    public static String deleteFace(String accessToken,String faceToken,String externalUserCode) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/face/delete";
        try {
            Map<String, Object> map = new HashMap<>();

            map.put("face_token", faceToken);
            map.put("group_id", "1");
            map.put("user_id", externalUserCode);
            String param = GsonUtils.toJson(map);

            String result = HttpUtil.post(url, accessToken, "application/json", param);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 人脸更新
     * @Param accessToken 标识
     * @Param imageUrl 图片url
     * @Param externalUserCode 人员编码
     * @return
     */
    public static String faceUpdate(String accessToken,String imageUrl,String externalUserCode) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/update";
        try {
            Map<String, Object> map = new HashMap<>();
            String imgParam = getImageParam(imageUrl);
            map.put("image", imgParam);
            map.put("group_id", "1");
            map.put("user_id", externalUserCode);
            map.put("user_info", externalUserCode);
            map.put("liveness_control", "NORMAL");
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");

            String param = GsonUtils.toJson(map);
            String result = HttpUtil.post(url, accessToken, "application/json", param);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    private static String getImageParam(String imageUrl){
        try {
         //   byte[] imgData = FileUtil.readFileByBytes(imageUrl);

            byte[] imgData = HttpsUtils.doGet(imageUrl);
            String imgParam = Base64Util.encode(imgData);
     //       String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            return imgParam;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void main(String[] org) {
        //百度密码
        String apiKey = "rjcQ4L8FMQ9lZHAB7QFTtmGo";
        String secretKey = "1xUjuPW0EUAdW5i1gKkGeqMVT7VfbKGo";

        String accessToken = getAuth(apiKey,secretKey);
        System.out.println("百度云秘钥：" + accessToken);


        String imageUrl = "http://m.qpic.cn/psc?/V52fOU1W06Wfjs4Azqqh10N7iI4BCSnR/ruAMsa53pVQWN7FLK88i5p5dAWgFbEdrM5getBFqbujtuABe3xIDNsoEc4lBAnbqQ9scptHAUPF*.K.zOBOr4C*kUCFKSLhxAp7lr2.tDSc!/mnull&bo=NAScBQAAAAABB4k!&rf=photolist&t=5";
       String add = add(accessToken,imageUrl,"qwe0111");


        String search = faceSearch(accessToken,imageUrl);
        String faceToken = "";
        String externalUserCode = "";
        if(search != null){
            JSONObject jsonObject = JSONObject.parseObject(search);
            String errorCode = jsonObject.getString("error_code");
            if(errorCode.equals("0")){
                String result = jsonObject.getString("result");//等到result
                if(StringUtils.isNoneBlank(result)){

                    faceToken = JSONObject.parseObject(result).getString("face_token");
                    String userList = JSONObject.parseObject(result).getString("user_list");//得到user_list
                    if(StringUtils.isNoneBlank(userList)){
                        JSONArray jsonArray = JSONArray.parseArray(userList);
                        for(int i=0;i<jsonArray.size();i++){
                            JSONObject object = (JSONObject) jsonArray.get(i);
                            externalUserCode = object.getString("user_info");
                        }
                    }
                }

            }

        }


        String delete = deleteFace(accessToken,faceToken,externalUserCode);

        if(delete != null){
            JSONObject jsonObject = JSONObject.parseObject(search);
            String errorCode = jsonObject.getString("error_code");
            if(errorCode.equals("0")){
                System.out.println("ASSSS");
            }
        }





    }

}
