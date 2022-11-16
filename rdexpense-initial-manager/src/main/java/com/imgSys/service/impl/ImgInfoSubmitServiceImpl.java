package com.imgSys.service.impl;

import com.common.base.dao.mysql.BaseDao;
import com.common.entity.PageData;
import com.imgSys.service.DeviceService;
import com.imgSys.service.ImgInfoSubmitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@EnableAspectJAutoProxy(proxyTargetClass = true,exposeProxy = true)
@Slf4j
@Service
public class ImgInfoSubmitServiceImpl implements ImgInfoSubmitService {

    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;

    @Autowired
    private DeviceService deviceService;

    /**
     *
     * @param pd 入参，携带影像相关信息
     */
    @Override
    public void realtimeInfoAppSubmit(PageData pd) {
        String imgSerialNum = "APRT" + UUID.randomUUID().toString(); //记录序列号
        pd.put("imgSerialNum", imgSerialNum);

        /** 影像采集时间为空，采用上传时间为采集时间 */
        if(pd.getString("graphingTime").isEmpty()) {
            Date dat = new Date();
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");

            pd.put("graphingTime", ft.format(dat).toString());
        }

        /** 采集设备归属科室即为影像归属科室 */
//        if (pd.get("graphingDeviceCode") != null && !pd.getString("graphingDeviceCode").isEmpty()) {
//            PageData pdData = deviceInfoService.queryDeviceInfoByCode(pd.getString("graphingDeviceCode"));
//            pd.put("departmentCode", pdData.getString("departmentCode"));
//        }

        /** 确保PageData中包含激光号字段 */
        if(null == pd.get("imgLaserId")) {
            pd.put("imgLaserId", "");
        }

        baseDao.insert("PhotoMainMapper.infoAppSubmit", pd);

        return;
    }
}
