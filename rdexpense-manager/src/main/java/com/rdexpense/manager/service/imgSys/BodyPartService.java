package com.rdexpense.manager.service.imgSys;

import com.common.entity.PageData;
import com.rdexpense.manager.dto.imgSys.BodyPart;

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

    List<PageData> getAllBodyPart(String parentSerialNum);

    void addBodyPartImg(PageData pageData);

    List<PageData> getAvailableBodyPart();

//List<PageData> getAllBodyPart(String parentSerialNum);
}
