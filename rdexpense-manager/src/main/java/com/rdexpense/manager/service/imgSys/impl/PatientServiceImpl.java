package com.rdexpense.manager.service.imgSys.impl;

import com.alibaba.fastjson.JSONArray;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.rdexpense.manager.service.imgSys.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @description: 患者基本信息管理
 * @author: Libaoyun
 * @date: 2022-10-29 10:24
 **/

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private BaseDao baseDao;


    /**
     * 增
     * @param pd
     */
    @Override
    public void addPatientInfo(PageData pd) {
        List<PageData> patientInfoList = (List<PageData>) baseDao.findForList("PatientMapper.isExistPatient", pd);
        if (!CollectionUtils.isEmpty(patientInfoList)) {
            throw new MyException("患者信息已存在,请检查确认!");
        }
        Integer maxIdData = baseDao.findForObject("PatientMapper.selectMaxId") == null ? 0 :
                (Integer) baseDao.findForObject("PatientMapper.selectMaxId");
        pd.put("id", maxIdData + 1);
        baseDao.insert("PatientMapper.addPatientInfo", pd);
    }

    /**
     * 删
     * @param pd
     */
    @Override
    public void deletePatient(PageData pd) {
        List<String> idList = JSONArray.parseArray(pd.getString("idList"), String.class);
        for (String patientIdCardNo : idList){
            PageData pageData = new PageData();
            pageData.put("patientIdCardNo", patientIdCardNo);
            List<PageData> patientInfoList = (List<PageData>) baseDao.findForList("PatientMapper.isExistPatient", pageData);
            if (CollectionUtils.isEmpty(patientInfoList)) {
                throw new MyException("患者信息不存在，请刷新确认!");
            }
            baseDao.delete("PatientMapper.deletePatient", pageData);
            // FIXME 删除患者时，考虑其他关联表相关信息也要删除！
        }
    }

    /**
     * 改
     * @param pd
     */
    @Override
    public void updatePatient(PageData pd) {
        List<PageData> patientInfoList = (List<PageData>) baseDao.findForList("PatientMapper.isExistPatient", pd);
        if (CollectionUtils.isEmpty(patientInfoList)) {
            throw new MyException("患者信息不存在，请刷新确认!");
        }
        baseDao.update("PatientMapper.updatePatient", pd);
    }

    /**
     * 查
     * @param pd
     */
    @Override
    public List<PageData> getAllPatient(PageData pd) {
        return (List<PageData>) baseDao.findForList("PatientMapper.getAllPatient");
    }


}
