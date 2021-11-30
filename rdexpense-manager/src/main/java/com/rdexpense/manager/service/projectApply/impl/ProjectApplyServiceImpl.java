package com.rdexpense.manager.service.projectApply.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.dao.redis.RedisDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.AwsUtil;
import com.common.util.ConstantValUtil;
import com.common.util.ExcelUtil;
import com.common.util.PDFUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.rdexpense.manager.dto.base.UserInfoDTO;
import com.rdexpense.manager.service.file.FileService;
import com.rdexpense.manager.service.projectApply.ProjectApplyService;
import com.rdexpense.manager.util.UseTokenInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:11
 */
@Slf4j
@Service
public class ProjectApplyServiceImpl implements ProjectApplyService {

    private static final String INITIAL_PASSWORD = "CRCC123456";


    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;

    @Autowired
    private FileService fileService;


    /**
     * 查询外部用户列表
     *
     * @param
     * @return
     */
    @Override
    public List queryList(PageData pd) {

        List<String> educationCodeList = JSONArray.parseArray(pd.getString("educationCode"), String.class);
        pd.put("educationCodeList", educationCodeList);
        List<String> employeeStatusCodeList = JSONArray.parseArray(pd.getString("employeeStatusCode"), String.class);
        pd.put("employeeStatusCodeList", employeeStatusCodeList);
        List<String> employeeTypeCodeList = JSONArray.parseArray(pd.getString("employeeTypeCode"), String.class);
        pd.put("employeeTypeCodeList", employeeTypeCodeList);

        List<PageData> userInfoList = (List<PageData>) baseDao.findForList("UserMapper.selectUserInfoAll", pd);


        return userInfoList;
    }


    /**
     * 新增立项申请
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void addApply(PageData pd) {
        List<PageData> userInfoList = (List<PageData>) baseDao.findForList("UserMapper.checkUser", pd);
        if (!CollectionUtils.isEmpty(userInfoList)) {
            throw new MyException("员工编号已存在");
        }

        String businessId = "YGGL" + UUID.randomUUID().toString();//生成业务主键ID

        pd.put("password", encoder.encode(INITIAL_PASSWORD));
        pd.put("version", 0);
        pd.put("businessId", businessId);


        //插入员工表
        baseDao.insert("UserMapper.insertUser", pd);


        // 插入到附件表
        fileService.insert(pd);

    }

    /**
     * 更新立项申请
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void updateApply(PageData pd) {


        //更新部门表
        List<PageData> departmentList = (List<PageData>) baseDao.findForList("UserMapper.queryUserDepartment", pd);

        String departmentListStr = pd.getString("departmentList");

        List<String> deleteList = new ArrayList<>();
        List<PageData> addList = new ArrayList<>();
        List<PageData> updateList = new ArrayList<>();
        List<String> idList = new ArrayList<>();


        //修改员工表
        baseDao.update("UserMapper.updateUser",pd);


        if(!CollectionUtils.isEmpty(departmentList)) {
            for(PageData pageData : departmentList){
                if(!idList.contains(pageData.getString("id"))){
                    deleteList.add(pageData.getString("id"));
                }
            }
        }

        //删除部门表
        if(!CollectionUtils.isEmpty(deleteList)){
            baseDao.batchDelete("UserMapper.deleteDepartment", deleteList);
        }

        //更新部门表
        if(!CollectionUtils.isEmpty(updateList)){
            baseDao.batchUpdate("UserMapper.updateDepartment", updateList);
        }

        //插入部门表
        if(!CollectionUtils.isEmpty(addList)){
            baseDao.batchInsert("UserMapper.insertDepartment",addList);

        }

        //编辑附件表
        //更新附件表，前端只传无id的，将这些数据入库
        fileService.update(pd);



    }

    /**
     * 删除立项申请
     *
     * @param
     * @return
     */
    @Override
    @Transactional
    public void deleteApply(PageData pd) {

        List<String> idList = JSONArray.parseArray(pd.getString("idList"), String.class);

        List<PageData> userList = (List<PageData>) baseDao.findForList("UserMapper.queryUserById", idList);

        if(!CollectionUtils.isEmpty(userList)){
            //删除用户信息
            baseDao.batchDelete("UserMapper.deleteUserInfo", userList);

            //删除用户部门信息
            baseDao.batchDelete("UserMapper.deleteUserDepartment", userList);


            //删除文件
            baseDao.batchDelete("UserMapper.batchDeleteFile", userList);
        }



    }

    /**
     * 查询立项申请详情
     *
     * @param pd
     * @return
     */
    @Override
    public PageData getApplyDetail(PageData pd) {

        PageData request = (PageData) baseDao.findForObject("UserMapper.queryOneRecord", pd);

        if (request != null){
            // 查询明细表
            List<PageData> departmentList = (List<PageData>) baseDao.findForList("UserMapper.queryUserDepartment", request);
            if(!CollectionUtils.isEmpty(departmentList)){
                request.put("departmentList", departmentList);
            }


            // 查询附件表
            List<PageData> attachmentList = (List<PageData>) baseDao.findForList("FileMapper.queryFile", request);
            if(!CollectionUtils.isEmpty(attachmentList)){
                AwsUtil.queryOneUrl(attachmentList, ConstantValUtil.BUCKET_PRIVATE);
                request.put("attachmentList", attachmentList);

            }
        }


        return request;
    }


    /**
     * 审批流程
     * @param pd
     */
    @Override
    public void approveRecord(PageData pd) {

    }


    /**
     * 列表提交
     * @param pd
     */
    @Override
    public void submitRecord(PageData pd) {

    }

    /**
     * 导出Excel
     *
     * @param pageData
     * @return
     */
    @Override
    public HSSFWorkbook exportExcel(PageData pageData) {
        String title = "员工信息";
        String[] head = {"序号","用户编号","用户姓名","手机号码","所属部门","所属职务","学历","员工状态","员工类型","创建人","创建日期","更新人","更新日期"};
        String idStr = pageData.getString("idList");
        List<String> listId = JSONObject.parseArray(idStr, String.class);
        //根据idList查询主表
        List<PageData> userInfoList = (List<PageData>) baseDao.findForList("UserMapper.queryUserById", listId);

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(title + 1);
        HSSFCellStyle styleHeader = ExcelUtil.setHeader(wb, sheet);// 表头
        HSSFCellStyle styleCell = ExcelUtil.setCell(wb, sheet);// 单元格
        // 创建表头
        HSSFRow rows = sheet.createRow(0);
        rows.setHeightInPoints(20);// 行高
        HSSFCell cells = rows.createCell(0);
        cells.setCellValue(title);
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 0, 0, 0, (head.length-1));
        // 第一行  表头
        HSSFRow rowTitle = sheet.createRow(1);
        HSSFCell hc;
        for (int j = 0; j < head.length; j++) {
            hc = rowTitle.createCell(j);
            hc.setCellValue(head[j]);
            hc.setCellStyle(styleCell);
        }

        if(!CollectionUtils.isEmpty(userInfoList)){
            for(int i=0;i<userInfoList.size();i++){
                PageData pd = userInfoList.get(i);

                HSSFRow row = sheet.createRow(i + 2);
                int j = 0;

                HSSFCell cell = row.createCell(j++);
                cell.setCellValue(i + 1);
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("userCode"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("userName"));
                cell.setCellStyle(styleCell);



                cell = row.createCell(j++);
                String time1 = pd.getString("updateTime");
                cell.setCellValue(time1.substring(0,time1.lastIndexOf(".")));
                cell.setCellStyle(styleCell);


            }
        }

        //设置自适应宽度
        for (int j = 0; j < head.length; j++) {
            sheet.autoSizeColumn(j);
            int colWidth = sheet.getColumnWidth(j) * 17 / 10;
            if (colWidth < 255 * 256) {
                sheet.setColumnWidth(j, colWidth < 3000 ? 3000 : colWidth);
            } else {
                sheet.setColumnWidth(j, 6000);
            }
        }
        sheet.setAutobreaks(true);
        return wb;
    }



}
