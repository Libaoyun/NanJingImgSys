package com.imgSys.service;

import com.common.entity.PageData;

import java.util.List;

/**
 * @description: 科室诊疗项目
 * @author: Libaoyun
 * @date: 2022-10-19 17:53
 **/
public interface DiagnosticItemService {
    void addDiagnosticItem(PageData pd);

    void deleteDiagnosticItem(PageData pd) throws Exception;

    void updateDiagnosticItem(PageData pd);

    List<PageData> getAllDiagnosticItem(String s, PageData pd);
}
