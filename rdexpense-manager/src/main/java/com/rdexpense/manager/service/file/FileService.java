package com.rdexpense.manager.service.file;

import com.common.entity.PageData;

import java.util.List;

public interface FileService {

    /**
     * 查询附件
     * @param pd
     * @return
     */
    List<PageData> query(PageData pd);

    /**
     * 删除附件
     * @param pd
     * @return
     */
    void delete(PageData pd);
}