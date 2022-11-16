package com.imgSys.service;

import com.common.entity.PageData;

import java.util.List;

/**
 * @description:
 * @author: Libaoyun
 * @date: 2022-10-29 10:24
 **/
public interface PatientService {


    void addPatientInfo(PageData pd);

    void deletePatient(PageData pd);

    void updatePatient(PageData pd);

    List<PageData> getAllPatient(PageData pd);
}
