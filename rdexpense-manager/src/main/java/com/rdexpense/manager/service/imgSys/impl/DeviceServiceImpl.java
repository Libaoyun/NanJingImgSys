package com.rdexpense.manager.service.imgSys.impl;

import cn.hutool.db.Page;
import com.alibaba.fastjson.JSON;
import com.common.base.dao.mysql.BaseDao;
import com.common.entity.PageData;
import com.rdexpense.manager.dto.imgSys.BodyPart;
import com.rdexpense.manager.service.imgSys.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description: 设备管理
 * @author: Libaoyun
 * @date: 2022-10-19 19:21
 **/

@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private BaseDao baseDao;


    @Override
    @Transactional
    public void addDevice(PageData pd) {
        PageData maxIdData = (PageData) baseDao.findForObject("DeviceMapper.selectMaxIdAndSerialNum", pd);
        if (maxIdData == null || maxIdData.getInt("id") == null){
            pd.put("id", 0);
        }
        else {
            pd.put("id", maxIdData.getInt("id") + 1);
        }
        if (pd.getInt("deviceSerialNum") == null){
        // FIXME 这里考虑下是否有必要取最大值，若带字母则只能手动输入
            pd.put("deviceSerialNum", maxIdData.getInt("deviceSerialNum") + 1);
        }
//        先将设备保存信息，然后再单独将部位设备关联保存
        baseDao.insert("DeviceMapper.insertDevice", pd);
        List<PageData> list = JSON.parseArray((String) pd.get("bodyParts"), PageData.class);
        PageData pageData = (PageData) baseDao.findForObject("DeviceMapper.selectPartOfDeviceMaxId", pd);
        int recordId = pageData == null ? 0 : pageData.getInt("id");
        for(PageData data1 : list){
            data1.put("id", ++recordId);
            data1.put("deviceSerialNum", pd.getString("deviceSerialNum"));
            data1.put("createUser",pd.getString("createUser"));
            data1.put("createUserId",pd.getString("createUserId"));
        }
        baseDao.batchInsert("DeviceMapper.insertBodyPart", list);

    }

    /**
     * 删除设备时要考虑将设备关联的数据也删除
     * @param pd
     */
    @Override
    @Transactional
    public void deleteDevice(PageData pd) {
        // 删除设备
        baseDao.delete("DeviceMapper.deleteDevice", pd);

        // 删除设备-部位表相关条目
        baseDao.delete("DeviceMapper.deleteBodyPartWithDevice", pd);
    }

    /**
     * 更新设备信息时也要考虑关联部位情况
     * @param pd
     */
    @Override
    public void updateDevice(PageData pd) {

//        先将设备信息更改，然后将部位设备关联信息用两个for循环进行先后比对，进行相应增删。
        // 或者直接删除原有数据，并将现有数据全部添加即可---这里采用此方式。
        baseDao.update("DeviceMapper.updateDevice", pd);
        baseDao.delete("DeviceMapper.deleteBodyPartWithDevice", pd);
        // 前端传来的现有的关联数据
        List<PageData> list = JSON.parseArray((String) pd.get("bodyParts"), PageData.class);
        // FIXME 查询原有的数据
//        List<PageData> list2 = (List<PageData>) baseDao.findForList("DeviceMapper.findPartOfDeviceByDevice", pd);
        PageData pageData = (PageData) baseDao.findForObject("DeviceMapper.selectPartOfDeviceMaxId", pd);
        int recordId = pageData == null ? 0 : pageData.getInt("id");
        for(PageData data1 : list){
            data1.put("id", ++recordId);
            data1.put("deviceSerialNum", pd.getString("deviceSerialNum"));
            data1.put("createUser",pd.getString("createUser"));
            data1.put("createUserId",pd.getString("createUserId"));
        }
        baseDao.batchInsert("DeviceMapper.insertBodyPart", list);
    }

    /**
     * 查询所有设备以及相关信息
     * @param pageData
     * @return
     */
    @Override
    public List<PageData> getAllDevice(PageData pageData){

        List<PageData> pd = (List<PageData>) baseDao.findForList("DeviceMapper.getAllDevice");
        for (PageData pageData1 : pd){
            List<PageData> bodyParts = (List<PageData>) baseDao.findForList("DeviceMapper.findPartOfDeviceByDevice", pageData1.getString("deviceSerialNum"));
            pageData1.put("bodyParts", bodyParts);
        }
        return pd;
    }
}
