package com.rdexpense.manager.service.system;

import com.common.entity.PageData;
import com.common.entity.ResponseEntity;

import java.util.List;


public interface PermissionService {


    /**
     * 查询已授权对象
     * @param pd
     * @return
     */
    List<PageData> queryAuthorizedUser( PageData pd);


    /**
     * 新增 授权用户
     * @param pd
     */
    void addAuthorization(PageData pd);


    /**
     * 查询授权菜单
     * @param pd
     * @return
     */
    List<PageData> queryAuthMenu(PageData pd);


    /**
     * 编辑授权菜单
     * @param pd
     */
    void updateAuthMenu(PageData pd);

    /**
     * 删除用户
     * @param pd
     */
    void removeAuthorization( PageData pd);


    /**
     * 查询部门职务树
     * @return
     */
    List<PageData> queryDeptTree(PageData pd);

    /**
     * 查询员工
     * @param pd
     * @return
     */
    List<PageData> queryUser(PageData pd);


    /**
     * 查询授权组织
     * @param pd
     * @return
     */
    List<PageData> queryOrganization(PageData pd);

    List<PageData> queryDeptTreeAll(PageData pd);

    ResponseEntity getProjectTree();
}
