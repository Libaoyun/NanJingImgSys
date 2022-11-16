package com.rdexpense.manager.service.projectApply;


import com.common.entity.PageData;
import com.itextpdf.text.Document;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:10
 */
public interface ProjectApplyService {

    /**
     * 查询列表
     * @return
     */
    List<PageData> queryList(PageData pd);

    /**
     * 新增立项申请
     * @param pd
     * @return
     */
    void addApply(PageData pd);

    /**
     * 更新立项申请
     * @param pd
     * @return
     */
    void updateApply(PageData pd);


    /**
     * 删除立项申请
     * @param pd
     * @return
     */
    void deleteApply(PageData pd);

    /**
     * 查询立项申请详情
     * @param pd
     * @return
     */
   PageData getApplyDetail(PageData pd);


    /**
     * 审批流程
     * @return
     */
    void approveRecord(PageData pd);

    /**
     * 列表提交
     * @param pd
     */
    void submitRecord(PageData pd);

    /**
     * 导入全部数据
     *
     * @param pd
     * @return
     */
    void uploadAll(MultipartFile file, PageData pd);



    /**
     * 导出excel
     */
    HSSFWorkbook exportExcel(String businessId) throws Exception;

    /**
     * 导出excel压缩包
     * @param businessIdList
     * @param zos
     * @param bos
     * @param filePrefix
     * @throws Exception
     */
    void exportZip(int flag,List<String> businessIdList, ZipOutputStream zos, ByteArrayOutputStream bos, String filePrefix) throws Exception;

    /**
     * 导出word或者pdf
     * @param  flag 文件标识 1：word 2:pdf
     * @param businessId
     */
    void exportWordPdf(int flag, String businessId, HttpServletResponse response,String filePrefix) throws Exception;


    /**
     * 预览
     * @param pd
     * @throws Exception
     */
    void preview(PageData pd,HttpServletResponse response ) throws Exception;


    /**
     * 导入主信息
     * @param file
     * @param pd
     * @return
     */
    PageData uploadMain(MultipartFile file, PageData pd);

    /**
     * 导入调查信息
     * @param file
     * @param pd
     * @return
     */
    PageData uploadSurvey(MultipartFile file, PageData pd);

    /**
     * 导入进度计划
     * @param file
     * @param pd
     * @return
     */
    List<PageData> uploadProgress(MultipartFile file, PageData pd);

    /**
     * 导入参加单位
     * @param file
     * @param pd
     * @return
     * @throws Exception
     */
    List<PageData> uploadUnit(MultipartFile file, PageData pd);

    /**
     * 导入研究人员
     * @param file
     * @param pd
     * @return
     */
    List<PageData> uploadUser(MultipartFile file, PageData pd);

    /**
     * 导入经费预算
     * @param file
     * @param pd
     * @return
     */
    List<PageData> uploadBudget(MultipartFile file, PageData pd);

    /**
     * 导入经费预算-每月预算
     * @param file
     * @param pd
     * @return
     */
    List<PageData> uploadMonth(MultipartFile file, PageData pd) ;

    /**
     * 导入拨款计划
     * @param file
     * @param pd
     * @return
     */
    List<PageData> uploadAppropriation(MultipartFile file, PageData pd);

}
