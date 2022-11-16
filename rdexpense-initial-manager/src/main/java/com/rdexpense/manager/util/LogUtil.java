package com.rdexpense.manager.util;

import com.alibaba.ttl.TtlRunnable;
import com.common.entity.PageData;

import com.rdexpense.manager.service.system.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

/**
 * @Author rdexpense
 * @create 2020/5/14
 * @description 日志打印工具类
 */
@Component
public class LogUtil {
    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    @Qualifier("asyncServiceExecutor")
    private Executor executor;


    public void saveLogData(Integer resultCode, Integer operateCode, String operateContent, PageData pageData) {
        executor.execute(TtlRunnable.get(new Runnable() {
            @Override
            public void run() {
                //保存日志记录
                operationLogService.saveLogData(resultCode, operateCode, operateContent, pageData);
            }
        }));
    }

}
