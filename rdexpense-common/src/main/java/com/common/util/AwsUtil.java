package com.common.util;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.*;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * AWS对象存储使用的工具类
 * @author rdexpense
 */
@Component
public class AwsUtil {
    private static String awsId;
    private static String awsKey;
    private static String region;


    @Value("${aws.access_key_id}")
    public void setAwsKey(String awsId) {
        this.awsId = awsId;
    }

    @Value("${aws.secret_access_key}")
    public void setSecretAccessKey(String awsKey) {
        this.awsKey = awsKey;
    }

    @Value("${aws.s3.region}")
    public void setRegion(String region) {
        this.region = region;
    }


    private final static Logger logger = LoggerFactory.getLogger(AwsUtil.class);

    //获取连接
    public static AmazonS3 createConnection() {
        AmazonS3 s3 = null;
        try {
            AWSCredentials credentials = new BasicAWSCredentials(awsId, awsKey);
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setProtocol(Protocol.HTTPS);
            logger.info("开始建立连接");
            s3 = AmazonS3ClientBuilder
                    .standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(region, Regions.US_EAST_1.name()))
                    .withPathStyleAccessEnabled(false)
                    .withClientConfiguration(clientConfiguration)
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();
            logger.info("连接创建成功");
        } catch (Exception e) {
            logger.error("====建立连接失败,region[{}]====", region);
            throw new MyException("Aws建立连接失败", e);
        }
        return s3;
    }


    /**
     * 验证s3上是否存在名称为bucketName的Bucket
     *
     * @param s3
     * @param bucketName
     * @return
     */
    public static boolean checkBucketExists(AmazonS3 s3, String bucketName) {
        List<Bucket> buckets = s3.listBuckets();
        for (Bucket bucket : buckets) {
            if (Objects.equals(bucket.getName(), bucketName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 文件上传
     */
    public static PageData upload(MultipartFile file, String bucketName) {
        PageData pageData = new PageData();
        if (file.getSize() < 0) {
            throw new MyException("请先上传文件");
        }
        AmazonS3 s3 = createConnection();
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");// 设置日期格式
        String timeFormate = df.format(now);// now为获取当前系统时间
        String yearMonth = timeFormate.substring(0, 6);
        String day = timeFormate.substring(6, 8);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        long fileSize = file.getSize();
        String contentType = file.getContentType();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(contentType);
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        //生成服务器上的文件名，使用uuid保证唯一
        String serveFileName = UUID.randomUUID().toString();
        String filePath = yearMonth + "/" + day + "/" + serveFileName + "." + suffix;
        pageData.put("filePath", filePath);
        //验证名称为bucketName的bucket是否存在，不存在则创建
        if (!checkBucketExists(s3, bucketName)) {
            s3.createBucket(bucketName);
        }
        try {
            logger.info("上传开始");
            long a = System.currentTimeMillis();
            // 上传文件
            s3.putObject(bucketName, filePath, file.getInputStream(), objectMetadata);
            long b = System.currentTimeMillis();
            logger.info("上传成功:" + (b - a));
            //设置有效期
            Date expiration = new Date(new Date().getTime() + 3600 * 1000);
            logger.info("获取URL");
            GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, filePath).withMethod(HttpMethod.GET).withExpiration(expiration);
//            GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, filePath);
//            urlRequest.setExpiration(expiration);
            String url = s3.generatePresignedUrl(urlRequest).toString();
            logger.info("获取URL成功");
            pageData.put("url", url);
            pageData.put("fileSize", fileSize);
        } catch (SdkClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            logger.warn("====获取文件流异常====");
            throw new MyException("获取文件流异常", e);
        } catch (Exception e) {
            logger.warn("====文件上传异常,key[{}],bucket[{}]====", filePath, bucketName);
            throw new MyException("上传失败", e);
        }
        return pageData;
    }

    /**
     * 文件上传(后端上传)
     */
    public static PageData uploadForServer(MultipartFile file, String bucketName) {
        PageData pageData = new PageData();
        if (file.getSize() < 0) {
            throw new MyException("请先上传文件");
        }
        AmazonS3 s3 = createConnection();
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");// 设置日期格式
        String timeFormate = df.format(now);// now为获取当前系统时间
        String yearMonth = timeFormate.substring(0, 6);
        String day = timeFormate.substring(6, 8);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        long fileSize = file.getSize();
        String contentType = file.getContentType();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(contentType);
        String fileName = file.getOriginalFilename();
        String filePath = yearMonth + "/" + day + "/" + fileName;
        pageData.put("filePath", filePath);
        //验证名称为bucketName的bucket是否存在，不存在则创建
        if (!checkBucketExists(s3, bucketName)) {
            s3.createBucket(bucketName);
        }
        try {
            logger.info("上传开始");
            long a = System.currentTimeMillis();
            // 上传文件
            s3.putObject(bucketName, filePath, file.getInputStream(), objectMetadata);
            long b = System.currentTimeMillis();
            logger.info("上传成功:" + (b - a));
            //设置有效期
            Date expiration = new Date(new Date().getTime() + 3600 * 1000);
            logger.info("获取URL");
            GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, filePath).withMethod(HttpMethod.GET).withExpiration(expiration);
//            GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, filePath);
//            urlRequest.setExpiration(expiration);
            String url = s3.generatePresignedUrl(urlRequest).toString();
            logger.info("获取URL成功");
       //      url = URLDecoder.decode(url, "UTF-8");
            pageData.put("url", url);
            pageData.put("fileSize", fileSize);
        } catch (SdkClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            logger.warn("====获取文件流异常====");
            throw new MyException("获取文件流异常", e);
        } catch (Exception e) {
            logger.warn("====文件上传异常,key[{}],bucket[{}]====", filePath, bucketName);
            throw new MyException("上传失败", e);
        }
        return pageData;
    }


    /**
     * 下载文件
     */
    public static void download(HttpServletResponse resp, String object_keys, String bucketName) {
        AmazonS3 s3 = createConnection();
        try {
            S3Object o = s3.getObject(bucketName, object_keys);
            S3ObjectInputStream s3is = o.getObjectContent();
            resp.reset();
            resp.setContentType("application/octet-stream; charset=utf-8");
            OutputStream os = resp.getOutputStream();
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3is.read(read_buf)) > 0) {
                os.write(read_buf, 0, read_len);
            }
            os.close();
            s3is.close();
        } catch (AmazonServiceException e) {
            e.getErrorMessage();
        } catch (FileNotFoundException e) {
            e.getMessage();
        } catch (IOException e) {
            e.getMessage();
        }
    }


    /**
     * 删除文件
     */
    public static void delFile(String[] object_keys, String bucketName) {
        AmazonS3 s3 = createConnection();
        try {
            DeleteObjectsRequest dor = new DeleteObjectsRequest(bucketName)
                    .withKeys(object_keys);
            s3.deleteObjects(dor);
            logger.info("删除成功");
        } catch (AmazonServiceException e) {
            e.getErrorMessage();
        }
    }

    /**
     * 模糊删除文件
     */
    public static void delFileByFuzzyName(String fuzzyName, String bucketName) {
        AmazonS3 s3 = createConnection();
        ObjectListing objectListing = s3.listObjects(bucketName);
        for (S3ObjectSummary s3ObjectSummary : objectListing.getObjectSummaries()) {
            String key = s3ObjectSummary.getKey();
            if (key.contains(fuzzyName)) {
                s3.deleteObject(bucketName, key);
            }
        }

    }


 /*   public static List<PageData> readImg(PageData pd) {
//        String bucketName = pd.getString("bucketName");
        String bucketName = "private1";
        List<PageData> retList = new ArrayList<PageData>();
        if ("public1".equals(bucketName)) {
            String detailStr = pd.getString("details");
            if (StringUtils.isNotBlank(detailStr)) {
                List<PageData> list = JSONObject.parseArray(detailStr, PageData.class);
                for (PageData data : list) {
                    String path = data.getString("file_path");
                    String aws_url = url + path;
                    data.put("file_path", aws_url);
                    retList.add(data);
                }
            }
        } else if ("private1".equals(bucketName)) {
            AmazonS3 s3 = null;
            String detailStr = pd.getString("details");
            try {
                s3 = AwsUtil.createConnection();
                if (StringUtils.isNotBlank(detailStr)) {
                    List<PageData> list = JSONObject.parseArray(detailStr, PageData.class);
                    for (PageData data : list) {
                        String path = data.getString("file_path");
                        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, path);
                        String url = s3.generatePresignedUrl(urlRequest).toString();

                        data.put("file_path", url);
                        retList.add(data);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return retList;
    }*/


    /**
     * queryOne 接口 将url 返回
     */
    public static void queryOneUrl(List<PageData> list, String bucketName) {
        if (list.size() > 0) {
            AmazonS3 s3 = createConnection();
            //验证名称为bucketName的bucket是否存在，不存在则创建
            if (checkBucketExists(s3, bucketName)) {
                for (PageData data : list) {
                    String filePath = data.getString("filePath");

                    if (StringUtils.isNotBlank(filePath)) {
                        Date expiration = new Date(new Date().getTime() + 3600 * 1000);
                        logger.info("开始获取URL请求");
                        GeneratePresignedUrlRequest urlRequest = null;
                        try {
                            urlRequest = new GeneratePresignedUrlRequest(bucketName, filePath).withMethod(HttpMethod.GET).withExpiration(expiration);
                            // urlRequest.setExpiration(expiration);
                            String url = s3.generatePresignedUrl(urlRequest).toString();
                            logger.info("获取URL请求成功");
                            data.put("url", url);
                        } catch (Exception e) {
                            logger.warn("====获取URL异常,key[{}],bucket[{}]====", filePath, bucketName);
                            throw new MyException("获取URL失败", e);
                        }
                    }
                }
            }
        }
    }
}
