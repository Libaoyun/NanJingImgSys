package com.rdexpense.manager.service.system.impl;

import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.*;

import com.google.common.base.Strings;
import com.rdexpense.manager.service.system.OperationLogService;
import com.rdexpense.manager.util.RailStatic;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.List;


/**
 * @Description: 操作日志实现接口
 * @Author: rdexpense
 * @Date: 2020/12/30 17:59
 * @Version: 1.0
 */
@Slf4j
@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;


    @Override
    public void saveLogData(HttpServletRequest request, String operateType, String operateContent, PageData pageData) {
        String strMenuId = pageData.getString("menuCode");
        PageData pd = new PageData();
        //获取ip
        String ip = "";
        try {
            ip = getIpAddr(request);
            //取出用户信息
            String userId = pageData.getString("userId");
            String userName = pageData.getString("userName");

            if (!pageData.getString("position").equals("")) {
                //岗位
                String[] positionArr = PositionUtil.getSinglePosition(pageData.get("position").toString());
                //部门
                String[] departArr = PositionUtil.getSingleDepart(pageData.get("position").toString());
                //组织机构
                String[] orgArr = PositionUtil.getSingleOrg(pageData.get("position").toString());

                //岗位
                pd.put("userPositionId", positionArr[0]);
                pd.put("userPositionName", positionArr[1]);
                //部门
                pd.put("userDepartId", departArr[0]);
                pd.put("userDepartName", departArr[1]);
                //组织机构
                pd.put("userOrgId", orgArr[0]);
                pd.put("userOrgName", orgArr[1]);
            } else {
                //岗位
                pd.put("userPositionId", null);
                pd.put("userPositionName", null);
                //部门
                pd.put("userDepartId", pageData.getString("creatorOrgId"));
                pd.put("userDepartName", pageData.getString("creatorOrgName"));
                //组织机构
                pd.put("userOrgId", null);
                pd.put("userOrgName", null);
            }

            //赋值
            pd.put("userId", userId);
            pd.put("userName", userName);

            pd.put("operateType", operateType);
            pd.put("operateContent", operateContent);
            pd.put("loginIp", ip);
            pd.put("menuId", strMenuId);

            PageData menuData = (PageData) baseDao.findForObject("OperationLogMapper.queryMenuInfo",pd);
            if (null != menuData && StringUtils.isNotBlank(menuData.getString("title"))) {
                pd.put("menuName", menuData.getString("title"));
            } else {
                pd.put("menuName", "");
            }
            baseDao.insert("OperationLogMapper.insertOperationLog",pd);
        } catch (Exception e) {
            log.warn(ConstantMsgUtil.ERR_LOG_SAVE.desc());
        }

    }

    /**
     *
     * @param resultCode 结果状态码 0-成功 null-失败
     * @param operateCode 操作码，见dealStatus如1-新增，2-编辑
     * @param operateContent 业务信息，比如配件材料采购
     * @param pageData
     */
    @Override
    public void saveLogData(Integer resultCode, Integer operateCode, String operateContent, PageData pageData) {
        PageData pd = new PageData();

        if(StringUtils.isNotBlank(pageData.getString("userCode"))){
            pd.put("userId", pageData.getString("userCode"));

        }else{
            pd.put("userId", RailStatic.getUserId());
        }

        if(StringUtils.isNotBlank(RailStatic.getUserName())){
            pd.put("userName", RailStatic.getUserName());
        }else{
            pd.put("userName", pageData.getString("userName"));
        }

        pd.put("userFlag", RailStatic.getUserFlag());


        //岗位
        if(StringUtils.isNotBlank(pageData.getString("postId"))){
            pd.put("userPositionId", pageData.getString("postId"));
        }else{
            pd.put("userPositionId", Strings.emptyToNull(RailStatic.getPostId()));
        }
        if(StringUtils.isNotBlank(pageData.getString("post"))){
            pd.put("userPositionName", pageData.getString("post"));

        }else{
            pd.put("userPositionName", Strings.emptyToNull(RailStatic.getPost()));
        }

        //部门
        if(StringUtils.isNotBlank(pageData.getString("departmentId"))){
            pd.put("userDepartId", pageData.getString("departmentId"));
        }else{
            pd.put("userDepartId", Strings.emptyToNull(RailStatic.getDepartmentId()));
        }
        if(StringUtils.isNotBlank(pageData.getString("department"))){
            pd.put("userDepartName", pageData.getString("department"));
        }else{
            pd.put("userDepartName", Strings.emptyToNull(RailStatic.getDepartment()));
        }

        //组织机构
        if (operateCode.equals(4)){
            //登录
            pd.put("userOrgId", pageData.getString("organizationId"));
            pd.put("userOrgName", pageData.getString("organizationName"));
        } else if (operateCode.equals(5)){
            //退出
            pd.put("userOrgId", pageData.getString("organizationId"));
            pd.put("userOrgName", pageData.getString("organizationName"));
        } else {
            pd.put("userOrgId", pageData.getString("creatorOrgId"));
            pd.put("userOrgName", pageData.getString("creatorOrgName"));
        }


        pd.put("operateType", DealStatus.getType(operateCode));
        if (resultCode == 0) {
            pd.put("operateContent", String.format("%s%s%s", DealStatus.getName(operateCode), operateContent, ConstantMsgUtil.INFO_SUCCESS.desc()));
        } else {
            pd.put("operateContent", String.format("%s%s%s", DealStatus.getName(operateCode), operateContent, ConstantMsgUtil.ERR_FAIL.desc()));
        }

        if(StringUtils.isNotBlank(RailStatic.getUserName())){
            pd.put("loginIp", RailStatic.getIp());
        }else{
            pd.put("loginIp", pageData.getString("loginIp"));
        }

        pd.put("menuId", pageData.getString("menuCode"));
        try {
            PageData menuData = (PageData) baseDao.findForObject("OperationLogMapper.queryMenuInfo",pd);
            if (null != menuData && StringUtils.isNotBlank(menuData.getString("title"))) {
                pd.put("menuName", menuData.getString("title"));
            } else {
                pd.put("menuName", "");
            }
            baseDao.insert("OperationLogMapper.insertOperationLog",pd);
        } catch (Exception e) {
            log.warn(ConstantMsgUtil.ERR_LOG_SAVE.desc(),e);
        }

    }

    /**
     * 通过HttpServletRequest 返回ip地址
     *
     * @param request
     * @return ip String
     * @author mql
     * @date 2016年6月27日
     * 参考：https://www.cnblogs.com/chenglc/p/6856734.html
     */
    private static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip.contains(":") && ip.substring(ip.lastIndexOf(":") + 1, ip.length()).equals("1")) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
                //logger.info("用localhost进行了访问，取服务器本地ip地址：" + ip);
            } catch (UnknownHostException e) {
//				log.error(e.getMessage(),e);
            }
        }
        return ip;
    }



    @Override
    public List<PageData> findOperationLogList(PageData pd) {

        List<PageData> list = (List<PageData>) baseDao.findForList("OperationLogMapper.queryOperationLogList",pd);
        return list;

    }


    /**
     * 导出EXCEL
     * @param pageData
     * @return
     */
    @Override
    public ResponseEntity exportExcel(PageData pageData) {
        // excel常量
        String title = "操作日志";
        String[] head = {"序号", "用户名","所属组织","所属部门","所属职务", "IP","操作时间", "操作类型", "操作内容"};
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


        String idStr = pageData.getString("idList");
        List<String> list = JSONObject.parseArray(idStr, String.class);
        List<PageData> allList = (List<PageData>) baseDao.findForList("OperationLogMapper.queryOperationLogById",list);

        if(!CollectionUtils.isEmpty(allList)){
            for(int i=0;i<allList.size();i++){
                PageData pd = allList.get(i);

                HSSFRow row = sheet.createRow(i + 2);
                int j = 0;

                HSSFCell cell = row.createCell(j++);
                cell.setCellValue(i + 1); // 序号
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("userName")); //用户名
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("userOrgName")); //所属单位
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("userDepartName")); //所属部门
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("userPositionName")); //所属岗位
                cell.setCellStyle(styleCell);


                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("loginIp")); //IP
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);

                String time = pd.getString("createTime");
                cell.setCellValue(time.substring(0,time.lastIndexOf("."))); //操作时间
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("operateType")); //操作类型
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("operateContent")); //操作内容
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

        return ResponseEntity.success(wb, ConstantMsgUtil.INFO_EXPORT_SUCCESS.desc());
    }



}
