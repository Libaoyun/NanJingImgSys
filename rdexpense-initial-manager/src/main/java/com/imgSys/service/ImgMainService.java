package com.imgSys.service;

import com.common.entity.PageData;

import java.util.List;

public interface ImgMainService {
    /* 获取符合条件的影像主信息列表 */
    List<PageData> queryImgMain(PageData pd);
    /* 获取某个影像的扩展属性信息 */
    List<PageData> queryImgExProperties(PageData pd);
    /* 根据序列号获取影像主信息列表 */
    List<PageData> queryImgMainBySNList(PageData pd);
    /* 根据影像采集设备号获取影像主信息列表 */
    List<PageData> queryByDeviceCode(PageData pd);
//    /* 根据影像序列号获取影像扩展属性列表 */
//    List<PageData> queryPropertiesByImgSNList(PageData pd);

    /* 删除满足特定条件的影像的属性信息 */
    void deleteImgProperties(PageData pd);
    /* 删除满足特定条件的影像 */
    void deleteImgMain(PageData pd);

    /* 向影像主表中添加记录 */
    void insertImgMain(PageData pd);
    /* 向影像属性表中添加相关属性 */
    void insertImgProperties(List<PageData> pdList);

    /* 更新影像主表中的某条记录 */
    void updateImgMain(PageData pd);
    /* 更新影像属性表中的相关记录 */
    void updateImgProperty(PageData pd);

}
