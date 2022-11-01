package com.rdexpense.manager.service.imgSys;

import com.common.entity.PageData;

/**
 * @description: 图像采集检查单信息
 * @author: Libaoyun
 * @date: 2022-10-31 19:19
 **/
public interface ExamFormService {
    void addExamForm(PageData pd);

    PageData getPatientByParams(PageData pd);
}
