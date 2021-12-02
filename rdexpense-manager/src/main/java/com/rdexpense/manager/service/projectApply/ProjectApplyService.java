package com.rdexpense.manager.service.projectApply;


import com.common.entity.PageData;
import com.itextpdf.text.Document;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    void uploadAll(MultipartFile file, PageData pd) throws Exception;



    /**
     * 导出excel
     */
    HSSFWorkbook exportExcel(PageData pd) throws Exception;

    /**
     * 导入主信息
     * @param file
     * @param pd
     * @return
     * @throws Exception
     */
    PageData uploadMain(MultipartFile file, PageData pd) throws Exception;

    /**
     * 导入调查信息
     * @param file
     * @param pd
     * @return
     * @throws Exception
     */
    PageData uploadSurvey(MultipartFile file, PageData pd) throws Exception;

    /**
     * 导入进度计划
     * @param file
     * @param pd
     * @return
     * @throws Exception
     */
    List<PageData> uploadProgress(MultipartFile file, PageData pd) throws Exception;

    /**
     * 导入参加单位
     * @param file
     * @param pd
     * @return
     * @throws Exception
     */
    List<PageData> uploadUnit(MultipartFile file, PageData pd) throws Exception;

    /**
     * 导入研究人员
     * @param file
     * @param pd
     * @return
     * @throws Exception
     */
    List<PageData> uploadUser(MultipartFile file, PageData pd) throws Exception;

    /**
     * 导入经费预算
     * @param file
     * @param pd
     * @return
     * @throws Exception
     */
    PageData uploadBudget(MultipartFile file, PageData pd) throws Exception;

    /**
     * 导入经费预算-每月预算
     * @param file
     * @param pd
     * @return
     * @throws Exception
     */
    PageData uploadMonth(MultipartFile file, PageData pd) throws Exception;

    /**
     * 导入拨款计划
     * @param file
     * @param pd
     * @return
     * @throws Exception
     */
    List<PageData> uploadAppropriation(MultipartFile file, PageData pd)throws Exception;

}
