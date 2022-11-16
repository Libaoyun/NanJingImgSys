package com.rdexpense.manager.service.attendance;


import com.common.entity.PageData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:10
 */
public interface AttendanceService {

    /**
     * 查询列表
     * @return
     */
    List<PageData> queryList(PageData pd);

    /**
     * 新增研发人员考勤管理
     * @param pd
     * @return
     */
    void addAttendance(PageData pd);

    /**
     * 更新研发人员考勤管理
     * @param pd
     * @return
     */
    void updateAttendance(PageData pd);


    /**
     * 删除研发人员考勤管理
     * @param pd
     * @return
     */
    void deleteAttendance(PageData pd);

    /**
     * 查询研发人员考勤管理
     * @param pd
     * @return
     */
   PageData queryDetail(PageData pd);



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
    void exportZip( List<String> businessIdList, ZipOutputStream zos, ByteArrayOutputStream bos, String filePrefix) throws Exception;


    /**
     * 导入考勤表
     * @param file
     * @param pd
     * @return
     * @throws Exception
     */
    PageData uploadAttendance(MultipartFile file, PageData pd) throws Exception;

    /**
     * 导入工资表
     * @param file
     * @param pd
     * @return
     * @throws Exception
     */
    PageData uploadSalary(MultipartFile file, PageData pd) throws Exception;

    /**
     * 生成支出申请单
     * @param pd
     * @return
     * @throws Exception
     */
    PageData generateApply(PageData pd);

    /**
     * 生成分摊表
     * @param pd
     * @return
     * @throws Exception
     */
    List<PageData> generateShare( PageData pd);

    /**
     * 查询项目弹框
     * @param pd
     * @return
     */
    List<PageData> queryProject(PageData pd);


    /**
     * 查询项目人员
     * @param pd
     * @return
     */
    List<PageData> queryUser(PageData pd);

    List<PageData> alreadyGenerateApply(PageData pd);
}
