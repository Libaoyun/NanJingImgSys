package com.rdexpense.manager.service.itemClosureCheck;


import com.common.entity.PageData;

import java.util.List;

public interface ItemClosureCheckService {
    List<PageData> queryItemClosureCheckList(PageData pd);

    List<PageData> queryProjectApplyList(PageData pd);
}
