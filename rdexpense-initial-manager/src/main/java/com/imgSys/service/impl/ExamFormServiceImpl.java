package com.imgSys.service.impl;

import com.alibaba.fastjson.JSON;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.AwsUtil;
import com.common.util.ConstantValUtil;

import com.common.util.baiduUtils.AgeUtil;
import com.imgSys.service.ExamFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
         // FIXME 考虑是否能够获取请求单信息，如果可以的话就可以修改，但因此也要考虑请求单号是否存在，存在则为修改请求单，否则新增请求单
//        PageData pageData = baseDao.findForObject()
        Integer maxId = baseDao.findForObject("ExamFormMapper.selectMaxId") == null ? 0 :
                (Integer) baseDao.findForObject("ExamFormMapper.selectMaxId");
        pd.put("id", maxId + 1);
        if (pd.getString("formSerialNum") == null){
            // FIXME 检查单号自动生成,待修改
            Random random = new Random();
            pd.put("formSerialNum", random.nextInt(100000 - 10000) + 10000 + 1);
            pd.put("formIssuerName", pd.getString("createUser"));
            pd.put("formIssuerCode", pd.getString("createUserId"));
            pd.put("departmentCode", pd.getString("departmentId"));
            pd.put("departmentName", pd.getString("department"));
        }
        pd.put("isPay", "未缴费");
        pd.put("isRegister", "未登记");

        baseDao.insert("ExamFormMapper.addExamForm", pd);


    }


    /**
     * 新增与修改患者信息（并返回患者信息用于展示）
     */
    @Override
    public PageData addOrModifyPatient(PageData pd) {
//        先根据 *就诊卡号* 查询是否存在该患者，存在则更新，反之新增
        PageData pageData = (PageData) baseDao.findForObject("PatientMapper.isExistPatient", pd);
        if (pageData == null){
            Integer maxIdData = baseDao.findForObject("PatientMapper.selectMaxId") == null ? 0 :
                    (Integer) baseDao.findForObject("PatientMapper.selectMaxId");
            pd.put("id", maxIdData + 1);
            baseDao.insert("PatientMapper.addPatientInfo", pd);
        }else {
            baseDao.update("PatientMapper.updatePatient", pd);
        }
        pageData = pd;
        pageData.put("formIssuerName", pd.getString("createUser"));
        pageData.put("formIssuerCode", pd.getString("createUserId"));
        pageData.remove("token");
        pageData.remove("companyId");
        pageData.remove("createUser");
        pageData.remove("createUserId");
        pageData.remove("post");
        pageData.remove("postId");
        // FIXME 序列号待修改
        Random random = new Random();
        pd.put("formSerialNum", random.nextInt(100000 - 10000) + 10000 + 1);
        return pageData;
    }


    /**
     * 查询患者信息，在查询时需要自动生成检查单号、就诊科室以及诊断医师（当前用户）
     * @param pd
     * @return
     */
    @Override
    public PageData getPatientByParams(PageData pd) throws ParseException {
        if ((pd.getString("patientIdCardNo") == null) && (pd.getString("identifierId") == null)){
            throw new MyException("请输入患者身份证或就诊卡号信息！");
        }
        PageData pageData = (PageData) baseDao.findForObject("PatientMapper.getPatientByParams", pd);
        if (pageData == null){
            throw new MyException("未找到该患者信息，请确认重试信息！");
        }
        // 计算年龄
        if (pageData.getString("birthday")!= null && pageData.getString("birthday")!= ""){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pageData.put("age", AgeUtil.getAgeByBirth(sdf.parse(pageData.getString("birthday"))));
            // 将Date类型转为String类型，否则返回的是String的时间戳类型。
            pageData.put("birthday", String.valueOf(pageData.getString("birthday")));
        }
        // FIXME 检查单号自动生成,待修改
        Random random = new Random();
        pageData.put("formSerialNum", random.nextInt(100000 - 10000) + 10000 + 1);
        pageData.put("formIssuerName", pd.getString("createUser"));
        pageData.put("formIssuerCode", pd.getString("createUserId"));
        pageData.put("departmentCode", pd.getString("departmentId"));
        pageData.put("departmentName", pd.getString("department"));
        return pageData;
    }

    /**
     * 获取所有标准部位及其示意图，从云上获取，Base64编码
     * @return
     */
    @Override
    public List<PageData> getStandardBodyPart() {
        List<PageData> list = (List<PageData>) baseDao.findForList("ExamFormMapper.getStandardBodyPart");
        for (PageData pageData : list){
//            pageData.put("isDetail", "0");
//            pageData.put("requireDetail", "");
            if (pageData.getString("partSketchFile")!=null && pageData.getString("partSketchFile")!= ""){

                pageData.put("base64Str", AwsUtil.downloadBase64(pageData.getString("partSketchFile"), ConstantValUtil.BODY_PART_IMAGE));
            }
            else pageData.put("base64Str", "");
        }
        return list;
    }

    /**
     * 新增患者检查单勾选的部位信息
     * @param pd
     */
    @Override
    @Transactional
    public void addPartOfForm(PageData pd) {

            // 判断是否存在检查单对应部位信息，如果存在，则可能这里是想修改，只需全部删除相关部位后再添加所需即可
            int isExistBodyParts = baseDao.findForObject("ExamFormMapper.findBodyPartByFormSerialNum" ,pd) == null ? 0 :
                    (Integer) baseDao.findForObject("ExamFormMapper.findBodyPartByFormSerialNum" ,pd);
            if (isExistBodyParts > 0){
                baseDao.delete("ExamFormMapper.deleteBodyPartByFormSerialNum" ,pd);
            }
            if (pd.get("bodyParts") != null && pd.get("bodyParts") != ""){
                Integer maxId = baseDao.findForObject("ExamFormMapper.selectPartMaxId") == null ? 0 :
                        (Integer) baseDao.findForObject("ExamFormMapper.selectPartMaxId");
                List<PageData> bodyParts = JSON.parseArray((String) pd.get("bodyParts"), PageData.class);
                for (PageData pageData : bodyParts){
                    // FIXME 序列号暂未确定,暂用id代替
                    pageData.put("serialNum", ++maxId);
                    pageData.put("id", maxId);
                    pageData.put("formSerialNum", pd.getString("formSerialNum"));
                    pageData.put("createUser", pd.getString("createUser"));
                    pageData.put("createUserId", pd.getString("createUserId"));
                    baseDao.insert("ExamFormMapper.addPartOfForm", pageData);
                }
            }
    }


    /**
     * 添加检查单额外部位
     */
    @Override
    public void addExtraBodyPart(PageData pd) {
        Integer maxId = baseDao.findForObject("ExamFormMapper.selectPartMaxId") == null ? 0 :
                (Integer) baseDao.findForObject("ExamFormMapper.selectPartMaxId");
            // FIXME 序列号暂未确定,暂用id代替
            pd.put("id", ++maxId);
            if (pd.getString("requireDetail") == null || pd.getString("requireDetail") == ""){
                pd.put("requireDetail", "无");
            }
            baseDao.insert("ExamFormMapper.addPartOfForm", pd);
    }

    /**
     * 删除检查单额外部位
     */
    @Override
    public void deleteExtraBodyPart(PageData pd) {
        baseDao.delete("ExamFormMapper.deleteExtraBodyPart", pd);
    }

    /**
     * 根据患者id获取请求单信息，以便登记
     */
    @Override
    public PageData getPatientOfForm(PageData pd) throws ParseException {
        if ((pd.getString("patientIdCardNo") == null) && (pd.getString("identifierId") == null)){
            throw new MyException("请输入患者身份证或就诊卡号信息！");
        }
        PageData pageData = (PageData) baseDao.findForObject("PatientMapper.getPatientByParams", pd);
        if (pageData == null){
            throw new MyException("未找到该患者信息，请确认重试信息！");
        }
        PageData pageData2 = (PageData) baseDao.findForObject("ExamFormMapper.findExamFormByPatientId", pageData);
        if (pageData2 == null){
            throw new MyException("未找到该患者的请求单信息，请患者先挂号取请求单后重试！");
        }
        // 计算年龄
        if (pageData.getString("birthday")!= null && pageData.getString("birthday")!= ""){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pageData.put("age", AgeUtil.getAgeByBirth(sdf.parse(pageData.getString("birthday"))));
            // 将Date类型转为String类型，否则返回的是String的时间戳类型。
            pageData.put("birthday", String.valueOf(pageData.getString("birthday")));
        }
        pageData.put("formSerialNum", pageData2.getString("formSerialNum"));
        pageData.put("formIssuerName", pd.getString("createUser"));
        pageData.put("formIssuerCode", pd.getString("createUserId"));
        pageData.put("imgDepartmentCode", pd.getString("departmentId"));
        pageData.put("imgDepartmentName", pd.getString("department"));
        pageData.put("createTime", pageData2.getString("createTime"));
        pageData.remove("phoneNum");
        pageData.remove("id");
        pageData.remove("homeAddress");
        pageData.remove("outpatientNo");
        pageData.remove("educationDegree");
        pageData.put("isPay", pageData2.getString("isPay"));
        pageData.put("isRegister", pageData2.getString("isRegister"));
        return pageData;
    }

    /**
     * 登记采集请求单（采集时）
     * @param pd
     */
    @Override
    public void registerExamForm(PageData pd) {
        pd.put("imgDepartmentName", pd.getString("department"));
        pd.put("isPay", "已登记");
        pd.put("isRegister", "已缴费");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        pd.put("registerTime", dateFormat.format(date));
        baseDao.update("ExamFormMapper.updateExamForm", pd);
    }






    /* FIXME 采集登记列表暂不使用，因此搁置先不管
    *//**
     * 查询患者影像采集登记列表
     * @return
     *//*
    @Override
    public List<PageData> getListOfRegister() {
        List<PageData> result = (List<PageData>) baseDao.findForList("ExamFormMapper.getListOfRegister");
        return result;
    }


    *//**
     * 编辑患者采集登记列表（可更改采集人员、采集状态、设备分配情况）
     *//*
    @Override
    public void updateFormList(PageData pd) {
        baseDao.update("ExamFormMapper.updateFormList", pd);
    }
*/


}
