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


    /**
     * 新增附件
     * @param pd
     * @return
     */
    void insert(PageData pd);

    /**
     * 编辑附件
     * @param pd
     * @return
     */
    void update(PageData pd);

    /**
     * 查询附件详情
     * @param pd
     * @return
     */
    PageData queryFileByBusinessId(PageData pd);


    /**
     * 插入审批的附件，并返回i拼接的ID字符串
     * @param pd
     * @return
     */
    PageData insertApproveFile(PageData pd);

    /**
     * 删除附件
     * @param pd
     */
    void deleteAttachment(PageData pd);

}
