package com.rdexpense.manager.service.system;


import com.common.entity.PageData;
import com.common.entity.ResponseEntity;

import java.util.List;

/**
 * @author rdexpense
 * @date 2020/5/26 14:58
 * @describe
 */
public interface MenuService {

    List<PageData> queryTree(PageData pd);

    void saveNode(PageData pd);

    void updateNode(PageData pd);

    void deleteNode(PageData pd);

    PageData queryNode(PageData pd);

    List<PageData> updateTree(PageData pd);

}
