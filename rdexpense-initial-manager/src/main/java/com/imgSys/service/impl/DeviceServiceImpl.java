package com.imgSys.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.AwsUtil;
import com.common.util.ConstantValUtil;

import com.imgSys.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
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
        List<PageData> deviceList = (List<PageData>) baseDao.findForList("DeviceMapper.isExistDevice", pd);
        if (!CollectionUtils.isEmpty(deviceList)) {
            throw new MyException("设备名称/编码信息已存在,请检查确认!");
        }
        PageData maxIdData = (PageData) baseDao.findForObject("DeviceMapper.selectMaxIdAndSerialNum", pd);
        pd.put("id", maxIdData.getInt("id") == null ? 0 : maxIdData.getInt("id") + 1);
        if (pd.getString("deviceSerialNum") == null){
        // FIXME 这里考虑序列号情况，暂定为递增
            pd.put("deviceSerialNum", maxIdData.getInt("deviceSerialNum") + 1);
        }
//        先将设备保存信息，然后再单独将部位设备关联保存
        baseDao.insert("DeviceMapper.insertDevice", pd);
        if (pd.get("bodyParts") != null && pd.get("bodyParts") != ""){
            List<PageData> list = JSON.parseArray((String) pd.get("bodyParts"), PageData.class);
            PageData pageData = (PageData) baseDao.findForObject("DeviceMapper.selectPartOfDeviceMaxId", pd);
            int recordId = pageData == null ? 0 : pageData.getInt("id");
            for(PageData data1 : list){
                PageData bodyPart = (PageData) baseDao.findForObject("BodyPartMapper.findBodyPartBySerialNum", data1);
                data1.put("partName", bodyPart.getString("partName"));
                data1.put("partCode", bodyPart.getString("partCode"));
                data1.put("id", ++recordId);
                data1.put("deviceSerialNum", pd.getString("deviceSerialNum"));
                data1.put("createUser",pd.getString("createUser"));
                data1.put("createUserId",pd.getString("createUserId"));
            }
            baseDao.batchInsert("DeviceMapper.insertBodyPart", list);
        }

    }

    /**
     * 删除设备时要考虑将设备关联的数据也删除
     * @param pd
     */
    @Override
    @Transactional
    public void deleteDevice(PageData pd) {
        List<String> serialNumList = JSONArray.parseArray(pd.getString("idList"), String.class);
        for (String  deviceSerialNum: serialNumList){
            PageData pageData = new PageData();
            pageData.put("deviceSerialNum", deviceSerialNum);
            List<PageData> deviceList = (List<PageData>) baseDao.findForList("DeviceMapper.isExistDevice", pageData);
            if (CollectionUtils.isEmpty(deviceList)) {
                throw new MyException("该设备不存在,请刷新后确认!");
            }
            // 删除设备
            baseDao.delete("DeviceMapper.deleteDevice", pageData);

            // 删除设备-部位表相关条目
            baseDao.delete("DeviceMapper.deleteBodyPartWithDevice", pageData);
        }

    }

    /**
     * 更新设备信息时也要考虑关联部位是否有改动
     * @param pd
     */
    @Override
    public void updateDevice(PageData pd) {
        List<PageData> deviceList = (List<PageData>) baseDao.findForList("DeviceMapper.isExistDevice", pd);
        if (CollectionUtils.isEmpty(deviceList)) {
            throw new MyException("该设备不存在,请刷新后确认!");
        }
// 可以考虑先将设备信息更改，然后将部位设备关联信息用两个for循环进行先后比对，进行相应增删。
        // 或者简单起见直接删除原有数据，并将现有数据全部添加即可---这里采用此方式。
        baseDao.update("DeviceMapper.updateDevice", pd);
        baseDao.delete("DeviceMapper.deleteBodyPartWithDevice", pd);
        // 前端传来的现有的关联数据
        List<PageData> list = JSON.parseArray((String) pd.get("bodyParts"), PageData.class);
        // 查询原有的数据（暂时忽略）
//        List<PageData> list2 = (List<PageData>) baseDao.findForList("DeviceMapper.findPartOfDeviceByDevice", pd);
        PageData pageData = (PageData) baseDao.findForObject("DeviceMapper.selectPartOfDeviceMaxId", pd);
        int recordId = pageData == null ? 0 : pageData.getInt("id");
        for(PageData data1 : list){
            PageData bodyPart = (PageData) baseDao.findForObject("BodyPartMapper.findBodyPartBySerialNum", data1);
            data1.put("partName", bodyPart.getString("partName"));
            data1.put("partCode", bodyPart.getString("partCode"));
            data1.put("id", ++recordId);
            data1.put("deviceSerialNum", pd.getString("deviceSerialNum"));
            data1.put("createUser",pd.getString("createUser"));
            data1.put("createUserId",pd.getString("createUserId"));
        }
        baseDao.batchInsert("DeviceMapper.insertBodyPart", list);
    }

    /**
     * 关键词查询所有设备以及相关信息
     * @param pageData
     * @return
     */
    @Override
    public List<PageData> getAllDevice(PageData pageData){
        List<String> departments = Arrays.asList(pageData.getString("departmentId").split(","));
        if (departments.size()>1){
            pageData.put("departments", departments);
            pageData.remove("departmentId");
        }
        List<PageData> pd = (List<PageData>) baseDao.findForList("DeviceMapper.getAllDevice", pageData);
        for (PageData pageData1 : pd){
            List<PageData> bodyParts = (List<PageData>) baseDao.findForList("DeviceMapper.findPartOfDeviceByDevice", pageData1.getString("deviceSerialNum"));
            pageData1.put("bodyParts", bodyParts);
        }
        return pd;
    }

    /**
     * 根据设备id查找设备相关部位的完整信息（主要用于获取部位示意图）
     * @param pd
     * @return
     */
    @Override
    public List<PageData> getBodyPartByDevice(PageData pd) {
        List<PageData> pageDataList = (List<PageData>) baseDao.findForList("DeviceMapper.getBodyPartByDevice", pd);
        for (PageData pageData : pageDataList){
            if (pageData.getString("partSketchFile")!=null && pageData.getString("partSketchFile")!= ""){
                // 转为Base64时需要加上头
                pageData.put("base64Str", "data:image/jpeg;base64," + AwsUtil.downloadBase64(
                        pageData.getString("partSketchFile"), ConstantValUtil.BODY_PART_IMAGE));
            }
        }
        return pageDataList;
    }

    @Override
    public List<PageData> queryGraphingDeviceByIPC(PageData pd) {
        List<PageData> graphingDeviceCodeList =
                (List<PageData>) baseDao.findForList("DeviceBindInfoMapper.queryIpcDevice", pd);
        return graphingDeviceCodeList;
    }
}
