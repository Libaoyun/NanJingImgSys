package com.imgSys.service;

import com.common.entity.PageData;

import java.util.List;

/**
 * @description: 科室身体部位
 * @author: Libaoyun
 * @date: 2022-10-17 19:01
 **/
public interface BodyPartService {

    void addBodyPart(PageData pd);

    void deleteBodyPart(PageData pd) throws Exception;

    void updateBodyPart(PageData pd);

    List<PageData> getAllBodyPart(String parentSerialNum, PageData pageData);

    void addBodyPartImg(PageData pageData);

    List<PageData> getStandardBodyPart(PageData pd);

//List<PageData> getAllBodyPart(String parentSerialNum);
}
