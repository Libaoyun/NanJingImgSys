package com.imgSys.service;

import com.common.entity.PageData;

import java.util.List;

/**
 * @description: 设备管理
 * @author: Libaoyun
 * @date: 2022-10-19 19:21
 **/
public interface DeviceService {
    void addDevice(PageData pd);

    void deleteDevice(PageData pd);

    void updateDevice(PageData pd);

    List<PageData> getAllDevice(PageData pageData);

    List<PageData> getBodyPartByDevice(PageData pd);

    List<PageData> queryGraphingDeviceByIPC(PageData pd);
}
