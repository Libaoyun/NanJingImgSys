package com.rdexpense.manager.imgsys;

import com.common.base.dao.redis.RedisDao;
import com.common.entity.PageData;
import com.rdexpense.manager.util.OutRequestUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Component
public class ImgFileInfoSubmit {
    private static final Logger logger = LoggerFactory.getLogger(ImgFileInfoSubmit.class);

    private RedisDao redisDao;
    private static String awsServiceUrl;

    /** 影像文件及信息实时上传path */
    private static String imgInfoAppSubmitRtUrlPath;
    /** 遗留影像文件及信息上传path */
    private static String imgInfoAppSubmitLgcUrlPath;

    private static String imgGraphingPatientUrlPath;

    @Value("/imgFileInfoSubmit/appGraphingPatientInfo")
    public void setImgGraphingPatientUrlPath(String patientInfo) {this.imgGraphingPatientUrlPath = patientInfo; }

    @Value("${oauth2.service.url}")
    public void setAwsServiceUrl(String serviceUrl) { this.awsServiceUrl = serviceUrl; }

    @Value("${realtime.info.submit.url.path}")
    public void setImgInfoAppSubmitRtPath(String submitPath) {this.imgInfoAppSubmitRtUrlPath = submitPath; }
    @Value("${legacy.info.submit.url.path}")
    public void setImgInfoAppSubmitLgcPath(String submitPath) {this.imgInfoAppSubmitLgcUrlPath = submitPath; }

    public void setRedisDao(RedisDao redisDao) {
        this.redisDao = redisDao;
    }

    public PageData getImgGraphingPatient(PageData pd) {
        OutRequestUtil outRequestUtil = new OutRequestUtil();
        outRequestUtil.setRedisDao(redisDao);

        String url = imgGraphingPatientUrlPath;
        PageData pageData = outRequestUtil.get(pd, url);

        return pageData;
    }

    private PageData submitInfo2Svr(PageData pd,  String path) {
        OutRequestUtil outRequestUtil = new OutRequestUtil();
        outRequestUtil.setRedisDao(redisDao);

        String url = awsServiceUrl + path;
        PageData pageData = outRequestUtil.get(pd, url);

        return pageData;
    }

    public PageData realTimeImgInfoSubmit(PageData pd) {
        String url = imgInfoAppSubmitRtUrlPath;
        PageData pageData = submitInfo2Svr(pd, url);

        return pageData;
    }

    public PageData legacyImgInfoSubmit(PageData pd) {
        String url = imgInfoAppSubmitLgcUrlPath;
        PageData pageData = submitInfo2Svr(pd, url);

        return pageData;
    }
}
