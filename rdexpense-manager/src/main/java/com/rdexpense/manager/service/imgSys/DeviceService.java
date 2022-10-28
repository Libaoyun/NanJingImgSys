package com.rdexpense.manager.service.imgSys;

import com.common.entity.PageData;
import com.rdexpense.manager.dto.imgSys.Device;

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
}
