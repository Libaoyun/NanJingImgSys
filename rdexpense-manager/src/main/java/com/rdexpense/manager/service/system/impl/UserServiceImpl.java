package com.rdexpense.manager.service.system.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.dao.redis.RedisDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.rdexpense.manager.dto.base.UserInfoDTO;
import com.rdexpense.manager.service.system.UserService;
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
import java.util.*;


/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:11
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private static final String INITIAL_PASSWORD = "CRCC123456";


    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UseTokenInfo useTokenInfo;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;


    /**
     * 查询外部用户列表
     *
     * @param
     * @return
     */
    @Override
    public List queryUserList(PageData pd) {
        List<PageData> userInfoList = (List<PageData>) baseDao.findForList("UserMapper.selectUserInfoAll", pd);
        if(!CollectionUtils.isEmpty(userInfoList)){
            //查询部门职务表
            List<PageData> postList = (List<PageData>) baseDao.findForList("UserMapper.queryAllPostData",userInfoList);
            if(!CollectionUtils.isEmpty(postList)){
                for(PageData data : userInfoList){
                    String userCode = data.getString("userCode");
                    for(PageData postData : postList){
                        if(userCode.equals(postData.getString("userCode"))){
                            data.put("departmentName",postData.getString("departmentName"));
                            data.put("postName",postData.getString("postName"));
                            data.put("departmentCode",postData.getString("departmentCode"));
                            data.put("postCode",postData.getString("postCode"));
                        }
                    }

                }
            }
        }

        return userInfoList;
    }


    /**
     * 新增员工
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void addUser(PageData pd) {
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

        //插入部门表
        List<PageData> departmentList = JSONObject.parseArray(pd.getString("departmentList"), PageData.class);
        if(!CollectionUtils.isEmpty(departmentList)){
            for(PageData data : departmentList){
                data.put("userCode",pd.getString("userCode"));
            }
            baseDao.batchInsert("UserMapper.insertDepartment", departmentList);
        }

        // 插入到附件表
        String file = pd.getString("attachmentList");
        if (StringUtils.isNotBlank(file)) {
            List<PageData> listFile = JSONObject.parseArray(file, PageData.class);
            if (listFile.size() > 0) {
                for (PageData data : listFile) {
                    data.put("businessId", businessId);
                }
                baseDao.batchInsert("FileMapper.saveFileDetail", listFile);
            }
        }


    }

    /**
     * 更新用户信息
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void updateUser(PageData pd) {

        //修改员工表
        baseDao.update("UserMapper.updateUser",pd);

        //更新部门表
        List<PageData> departmentList = (List<PageData>) baseDao.findForList("UserMapper.queryUserDepartment", pd);

        String departmentListStr = pd.getString("departmentList");

        List<String> deleteList = new ArrayList<>();
        List<PageData> addList = new ArrayList<>();
        List<PageData> updateList = new ArrayList<>();
        List<String> idList = new ArrayList<>();


        List<PageData> departmentLists = JSONObject.parseArray(departmentListStr, PageData.class);
        if(!CollectionUtils.isEmpty(departmentLists)) {
            for(PageData pageData : departmentLists){
                if(StringUtils.isNotEmpty(pageData.getString("id"))){
                    updateList.add(pageData);
                    idList.add(pageData.getString("id"));
                }else{
                    pageData.put("userCode",pd.getString("userCode"));
                    addList.add(pageData);
                }
            }
        }

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
        List<PageData> fileList = new ArrayList<PageData>();
        String attachmentDetailStr = pd.getString("attachmentList");
        if (StringUtils.isNotBlank(attachmentDetailStr)) {
            List<PageData> listFile = JSONObject.parseArray(attachmentDetailStr, PageData.class);
            if(!CollectionUtils.isEmpty(listFile)){
                for (PageData data : listFile) {
                    String frontId = data.getString("id");
                    if (frontId == null || frontId.equals("")) {
                        data.put("businessId", pd.getString("businessId"));
                        fileList.add(data);
                    }
                }
                if (fileList.size() > 0) {
                    baseDao.batchInsert("FileMapper.saveFileDetail", fileList);
                }
            }
        }

        //更新redis中缓存用户信息
        Set<String> keys = redisDao.getScan("EXTERNAL*");
        for (String str : keys) {
            UserInfoDTO userInfoToken = useTokenInfo.getUserInfoByToken(str);

            if (userInfoToken.getUserCode().equals(pd.getString("userCode"))) {
                userInfoToken.setUserName(pd.getString("userName"));
                //更新用户信息
                if (redisDao.existsKey(str)) {
                    redisDao.vSet(str, userInfoToken);
                }
            }

        }

    }

    /**
     * 重置密码
     *
     * @param
     * @return
     */
    @Override
    @Transactional
    public void resetPassword(PageData pd) {

        List<String> idList = JSONArray.parseArray(pd.getString("idList"), String.class);
        pd.put("idList", idList);
        pd.put("password", encoder.encode(INITIAL_PASSWORD));

        baseDao.update("UserMapper.resetPassword", pd);

    }

    /**
     * 更新密码
     *
     * @param
     * @return
     */
    @Override
    @Transactional
    public void updatePassword(PageData pd) {

        UserInfoDTO userInfoToken = useTokenInfo.getUserInfoByToken(pd.getString("token"));

        pd.put("userCode", userInfoToken.getUserCode());

        PageData userInfo = (PageData) baseDao.findForObject("UserMapper.selectUserInfoById", pd);

        if (userInfo != null) {
            if (!encoder.matches(pd.getString("oldPassword"), userInfo.getString("password"))) {
                throw new MyException("旧密码不正确");
            }

            pd.put("password", encoder.encode(pd.getString("password")));
            baseDao.update("UserMapper.updatePassword", pd);

        }else {
            throw new MyException("该用户不存在");
        }

    }

    /**
     * 删除用户信息
     *
     * @param
     * @return
     */
    @Override
    @Transactional
    public void deleteUser(PageData pd) {

        List<String> idList = JSONArray.parseArray(pd.getString("idList"), String.class);

        List<PageData> userList = (List<PageData>) baseDao.findForList("UserMapper.queryUserById", idList);

        if(!CollectionUtils.isEmpty(userList)){
            //删除用户信息
            baseDao.batchDelete("UserMapper.deleteUserInfo", userList);

            //删除用户部门信息
            baseDao.batchDelete("UserMapper.deleteUserDepartment", userList);

            //删除用户权限表
            baseDao.batchDelete("UserMapper.deleteAuthorityUser", userList);

            //删除权限菜单表
            baseDao.batchDelete("UserMapper.deleteAuthorityMenu", userList);

            //删除文件
            baseDao.batchDelete("UserMapper.batchDeleteFile", userList);
        }



    }

    /**
     * 查询用户信息
     *
     * @param pd
     * @return
     */
    @Override
    public PageData getUserDetail(PageData pd) {

        PageData request = (PageData) baseDao.findForObject("UserMapper.queryOneRecord", pd);

        // 查询明细表
        List<PageData> departmentList = (List<PageData>) baseDao.findForList("UserMapper.queryUserDepartment", request);
        if(!CollectionUtils.isEmpty(departmentList)){
            request.put("departmentList", departmentList);
        }


        // 查询附件表
        List<PageData> attachmentList = (List<PageData>) baseDao.findForList("FileMapper.queryFile", pd);
        if(!CollectionUtils.isEmpty(attachmentList)){
            AwsUtil.queryOneUrl(attachmentList, ConstantValUtil.BUCKET_PRIVATE);
            request.put("attachmentList", attachmentList);

        }

        return request;
    }

    /**
     * 查询职务
     * @param pd
     * @return
     */
    @Override
    public List<PageData> getPost(PageData pd) {

        List<PageData> result = new ArrayList<>();
        //查询所有数据
        List<PageData> dataList = (List<PageData>) baseDao.findForList("DepartmentMapper.queryAllData", pd);
        if (!CollectionUtils.isEmpty(dataList)) {
            result = recursiveTreeList(pd.getString("departmentCode"), dataList);
        }

        return result;
    }

    /**
     * 递归树
     *
     * @param parentId
     * @param dataList
     * @return
     */
    public List<PageData> recursiveTreeList(String parentId, List<PageData> dataList) {
        List<PageData> childOrg = new ArrayList<>();
        for (PageData menu : dataList) {
            String orgCode = menu.getString("orgId");
            String pCode = menu.getString("parentId");
            if (pCode.equals(parentId)) {
                List<PageData> children = recursiveTreeList(orgCode, dataList);
                menu.put("children", children);
                childOrg.add(menu);
            }

        }

        return childOrg;
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
        String[] head = {"序号","编号","姓名","英文名","部门","职务","性别","出生日期","身高","学历","婚姻状况","血型","移动电话","办公电话",
                "电子邮箱","传真","员工状态","员工类型","参工日期","入职日期","转正日期","离职日期","国籍","籍贯","民族","宗教",
                "创建人","修改人","创建日期","修改日期"};
        String idStr = pageData.getString("idList");
        List<String> listId = JSONObject.parseArray(idStr, String.class);
        //根据idList查询主表
        List<PageData> userInfoList = (List<PageData>) baseDao.findForList("UserMapper.queryUserById", listId);
        if(!CollectionUtils.isEmpty(userInfoList)){
            //查询部门职务表
            List<PageData> postList = (List<PageData>) baseDao.findForList("UserMapper.queryAllPostData",userInfoList);
            if(!CollectionUtils.isEmpty(postList)){
                for(PageData data : userInfoList){
                    String userCode = data.getString("userCode");
                    for(PageData postData : postList){
                        if(userCode.equals(postData.getString("userCode"))){
                            data.put("departmentName",postData.getString("departmentName"));
                            data.put("postName",postData.getString("postName"));
                        }
                    }

                }
            }
        }

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
                cell.setCellValue(pd.getString("englishUserName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("departmentName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("postName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("gender"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("birthDate"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("height"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("education"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("maritalStatus").equals("0")?"否":"是");
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("bloodType"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("mobilePhone"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("officeTelephone"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("email"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("fax"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("employeeStatus"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("employeeType"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("participationDate"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("entryDate"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("confirmationDate"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("leaveDate"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("nationality"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("nativePlace"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("nation"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("religion"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("createUser"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("updateUser"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                String time = pd.getString("createTime");
                cell.setCellValue(time.substring(0,time.lastIndexOf(".")));
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

    /**
     * 导出PDF
     *
     * @param pd
     * @param document
     * @throws Exception
     */
    @Override
    public void exportPDF(PageData pageData, Document document) throws Exception {
        // 中文字体,解决中文不能显示问题
        BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        com.itextpdf.text.Font keyfont = new com.itextpdf.text.Font(bfChinese, 12, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font textfont = new com.itextpdf.text.Font(bfChinese, 10, com.itextpdf.text.Font.NORMAL);
        // 设置标题
        Paragraph blankRow1 = new Paragraph("员工信息", keyfont);
        blankRow1.setAlignment(Element.ALIGN_CENTER);
        document.add(blankRow1);


        //根据idList查询主表
        PageData pd = (PageData) baseDao.findForObject("UserMapper.queryOneRecord", pageData);
        if(pd != null){
            //查询部门职务表
            List<PageData> userInfoList = new ArrayList<>();
            userInfoList.add(pd);
            List<PageData> postList = (List<PageData>) baseDao.findForList("UserMapper.queryAllPostData",userInfoList);
            if(!CollectionUtils.isEmpty(postList)){
                for (PageData postData : postList) {
                    pd.put("departmentName", postData.getString("departmentName"));
                    pd.put("postName", postData.getString("postName"));
                }

            }
        }



        //创建一个表格,10为一行有几栏

        PdfPTable table1 = new PdfPTable(10);
        table1.setSpacingBefore(10f);
        table1.setWidthPercentage(100);
        int[] width1 = {150, 300, 150, 300, 150, 300, 150, 300, 150, 300};//每栏的宽度
        table1.setWidths(width1); //设置宽度

        String[] argArr1 = {"编号", pd.getString("userCode"), "姓名", pd.getString("userName"), "英文名", pd.getString("englishUserName"), "部门", pd.getString("departmentName"), "职务", pd.getString("postName")};
        for (String arg : argArr1) {
            PdfPCell cell = new PdfPCell(new Paragraph(arg, textfont));
            cell.setMinimumHeight(20);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
        }


        PdfPTable table2 = new PdfPTable(10);
        table2.setSpacingBefore(10f);
        table2.setWidthPercentage(100);
        int[] width2 = {150, 300, 150, 300, 150, 300, 150, 300, 150, 300};//每栏的宽度
        table2.setWidths(width2); //设置宽度

        String[] argArr2 = {"性别", pd.getString("gender"), "出生日期", pd.getString("birthDate"), "身高", pd.getString("height"), "学历", pd.getString("education"), "婚姻状况", pd.getString("maritalStatus").equals("0")?"否":"是"};
        for (String arg : argArr2) {
            PdfPCell cell = new PdfPCell(new Paragraph(arg, textfont));
            cell.setMinimumHeight(20);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table2.addCell(cell);
        }


        PdfPTable table3 = new PdfPTable(10);
        table3.setSpacingBefore(10f);
        table3.setWidthPercentage(100);
        int[] width3 = {150, 300, 150, 300, 150, 300, 150, 300, 150, 300};//每栏的宽度
        table3.setWidths(width3); //设置宽度

        String[] argArr3 = {"血型", pd.getString("bloodType"), "移动电话", pd.getString("mobilePhone"), "办公电话", pd.getString("officeTelephone"), "电子邮箱", pd.getString("email"), "传真", pd.getString("fax")};
        for (String arg : argArr3) {
            PdfPCell cell = new PdfPCell(new Paragraph(arg, textfont));
            cell.setMinimumHeight(20);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table3.addCell(cell);
        }


        PdfPTable table4 = new PdfPTable(10);
        table4.setSpacingBefore(10f);
        table4.setWidthPercentage(100);
        int[] width4 = {150, 300, 150, 300, 150, 300, 150, 300, 150, 300};//每栏的宽度
        table4.setWidths(width4); //设置宽度

        String[] argArr4 = {"员工状态", pd.getString("employeeStatus"), "员工类型", pd.getString("employeeType"), "参工日期", pd.getString("participationDate"), "入职日期", pd.getString("entryDate"), "转正日期", pd.getString("confirmationDate")};
        for (String arg : argArr4) {
            PdfPCell cell = new PdfPCell(new Paragraph(arg, textfont));
            cell.setMinimumHeight(20);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table4.addCell(cell);
        }


        PdfPTable table5 = new PdfPTable(10);
        table5.setSpacingBefore(10f);
        table5.setWidthPercentage(100);
        int[] width5 = {150, 300, 150, 300, 150, 300, 150, 300, 150, 300};//每栏的宽度
        table5.setWidths(width1); //设置宽度

        String[] argArr5 = {"离职日期", pd.getString("leaveDate"), "国籍", pd.getString("nationality"), "籍贯", pd.getString("nativePlace"), "民族", pd.getString("nation"), "宗教", pd.getString("religion")};
        for (String arg : argArr5) {
            PdfPCell cell = new PdfPCell(new Paragraph(arg, textfont));
            cell.setMinimumHeight(20);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table5.addCell(cell);
        }



        //将表格二，表格三合入表格一中
        PDFUtil.mergeTable(table1,table2,table3,table4,table5);

        // 添加表格
        document.add(table1);
    }


}
