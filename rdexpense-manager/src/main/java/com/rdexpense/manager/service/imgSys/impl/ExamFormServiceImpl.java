package com.rdexpense.manager.service.imgSys.impl;

import com.common.base.dao.mysql.BaseDao;
import com.common.entity.PageData;
import com.rdexpense.manager.service.imgSys.ExamFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: 图像采集检查单信息
 * @author: Libaoyun
 * @date: 2022-10-31 19:19
 **/

@Service
public class ExamFormServiceImpl implements ExamFormService {

    @Autowired
    private BaseDao baseDao;


    /**
     * 新增影像请求单信息
     * @param pd
     */
    @Override
    public void addExamForm(PageData pd) {
        Integer maxIdData = baseDao.findForObject("ExamFormMapper.selectMaxId") == null ? 0 :
                (Integer) baseDao.findForObject("ExamFormMapper.selectMaxId");
        pd.put("id", maxIdData + 1);
        baseDao.insert("ExamFormMapper.addExamForm", pd);
    }

    @Override
    public PageData getPatientByParams(PageData pd) {
        return null;
    }

//    @Override
//    public PageData getPatientByParams(PageData pd) {
//
//    }
}
