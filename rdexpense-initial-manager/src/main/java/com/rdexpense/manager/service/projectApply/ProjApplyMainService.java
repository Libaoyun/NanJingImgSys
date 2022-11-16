package com.rdexpense.manager.service.projectApply;

import com.common.entity.PageData;

import java.util.List;

public interface ProjApplyMainService {
    /**
     * 查询项目合同列表
     * @return
     */
    List<PageData> queryProjApplyMainList(PageData pd);

    /**
     * 按照businessId查询项目合同
     * @return
     */
    PageData queryByBusinessId(PageData pd);
}
