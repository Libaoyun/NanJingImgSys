package com.rdexpense.manager.service.system;

import com.common.entity.PageData;

import java.util.List;

/**
 * @author luxiangbao
 * @date 2021/11/12 11:47
 * @description
 */
public interface DepartmentService {


    /**
     * 查询 组织管理 树结构
     *
     * @param pd
     * @return
     */
    List<PageData> queryOrgTree(PageData pd);

    /**
     * 更新 树结构排序
     * @param pd
     * @return
     */
    List<PageData> updateOrgTree(PageData pd);


    /**
     * 新增部门节点
     *
     * @param pd
     * @return
     */
    void addDepartment(PageData pd);

    /**
     * 编辑部门
     * @param pd
     */
    void updateDepartment(PageData pd);

    /**
     * 查询部门详情
     * @param pd
     */
    PageData getDepartment(PageData pd);


    /**
     * 新增职务
     *
     * @param pd
     * @return
     */
    void addPost(PageData pd);

    /**
     * 编辑职务
     * @param pd
     */

    void updatePost(PageData pd);


    /**
     * 查询职务详情
     * @param pd
     */
    PageData getPost(PageData pd);
    /**
     * 删除节点
     * @param pd
     */
    void deleteOrgNode(PageData pd);


}
