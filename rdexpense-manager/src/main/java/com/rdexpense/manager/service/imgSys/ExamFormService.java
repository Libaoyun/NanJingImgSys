package com.rdexpense.manager.service.imgSys;

import com.common.entity.PageData;
import com.common.entity.ResponseEntity;

import java.text.ParseException;
import java.util.List;

/**
 * @description: 图像采集检查单信息
 * @author: Libaoyun
 * @date: 2022-10-31 19:19
 **/
public interface ExamFormService {
    void addExamForm(PageData pd);

    PageData getPatientByParams(PageData pd) throws ParseException;

    List<PageData> getStandardBodyPart();

    void addPartOfForm(PageData pd);

 /*   List<PageData> getListOfRegister();

    void updateFormList(PageData pd);*/

    PageData addOrModifyPatient(PageData pd);
}
