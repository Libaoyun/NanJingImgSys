package com.rdexpense.manager.service.system;

import com.common.entity.PageData;
import com.common.entity.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 操作日志实现接口
 * @Author: rdexpense
 * @Date: 2020/12/30 17:59
 * @Version: 1.0
 */
public interface OperationLogService {
    void saveLogData(HttpServletRequest request, String operateType, String operateContent, PageData pageData);

    void saveLogData(Integer resultCode, Integer code, String operateContent, PageData pageData);

    List<PageData> findOperationLogList(PageData pd);

    ResponseEntity exportExcel(PageData pageData);
}
