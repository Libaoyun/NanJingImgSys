package com.rdexpense.manager.service.system.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.dao.redis.RedisDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;

import com.common.util.ConstantMsgUtil;
import com.rdexpense.manager.util.LogUtil;
import com.rdexpense.manager.util.UseTokenInfo;
import com.rdexpense.manager.service.system.PermissionService;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {


    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;


    /**
     * 查询已授权对象
     *
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryAuthorizedUser(PageData pd) {

        //查询用户信息
        List<PageData> list = (List<PageData>) baseDao.findForList("AuthorityUserMapper.findAuthUser", pd);

        return list;

    }

    /**
     * 新增授权用户
     *
     * @param pd
     */

    @Override
    public void addAuthorization(PageData pd) {

        //前端的用户
        String userListStr = pd.getString("userList");
        List<PageData> userList = JSONObject.parseArray(userListStr, PageData.class);


        List<String> idList = (List<String>) baseDao.findForList("AuthorityUserMapper.queryUserByCode",pd);

        StringBuffer buffer = new StringBuffer();
        List<PageData> addList = new ArrayList<>();
        for (PageData data : userList) {

            if(!CollectionUtils.isEmpty(idList) && idList.contains(data.getString("userId"))){
                buffer.append(data.getString("authName"));
            }

            PageData newData = new PageData();
            newData.put("scopeCode", pd.getString("organizationId"));
            newData.put("userFlag", pd.getString("userFlag"));
            newData.put("authStatus", 0);
            newData.put("createUser", pd.getString("createUser"));
            newData.put("createUserId", pd.getString("createUserId"));

            newData.put("userId", data.getString("userId"));
            newData.put("authName", data.getString("authName"));

            if (pd.getString("userFlag").equals("0")) {//人员

                String[] arr = getDepartment(data);
                newData.put("departmentCode", arr[0]);
                newData.put("departmentName", arr[1]);
            } else {//岗位
                newData.put("departmentCode", data.getString("userId"));
                newData.put("departmentName", data.getString("authName"));
            }


            addList.add(newData);//新增的用户

        }

        if(StringUtils.isNotBlank(buffer.toString())){
            throw new MyException("以下用户已重复授权："+buffer.toString());
        }

        if (!CollectionUtils.isEmpty(addList)) {
            baseDao.batchInsert("AuthorityUserMapper.saveUser", addList);

        }


    }


    /**
     * 查询授权菜单
     *
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryAuthMenu(PageData pd) {
        List<PageData> list = (List<PageData>) baseDao.findForList("RouteMenuMapper.queryAuthMenuList",pd);
        return list;

    }



    /**
     * 根据员工编码查询所属部门及职务
     *
     * @param pd
     * @return
     */
    private String[] getDepartment(PageData pd) {
        String[] result = {"", ""};
        List<PageData> list = (List<PageData>) baseDao.findForList("AuthorityUserMapper.queryDataById", pd);

        StringBuffer departName = new StringBuffer();
        StringBuffer departCode = new StringBuffer();
        if (!CollectionUtils.isEmpty(list)) {
            for (PageData data : list) {
                departName.append(data.getString("departmentName") + "/" + data.getString("postName")).append(";");
                departCode.append(data.getString("departmentCode") + "/" + data.getString("postCode")).append(";");
            }

            String nameStr = departName.toString();
            String codeStr = departCode.toString();

            result[1] = nameStr.substring(0, nameStr.length() - 1);
            result[0] = codeStr.substring(0, codeStr.length() - 1);
        }

        return result;
    }


    /**
     * 编辑授权菜单
     *
     * @param pd
     */

    @Override
    @Transactional
    public void updateAuthMenu(PageData pd) {

        //取出用户信息,自己不能对自己授权
        if (pd.getString("userId").equals(pd.getString("userCode"))) {
            throw new MyException("不能对自己进行授权");
        }

        String menuStr = pd.getString("menuButton");
        List<PageData> menuList = JSONObject.parseArray(menuStr, PageData.class);


        List<PageData> addList = new ArrayList<>();
        List<PageData> updateList = new ArrayList<>();
        List<String> deleteList = new ArrayList<>();
        List<String> menuCodeList = new ArrayList<>();

        //查询已经授权的
        List<String> authMenuList = (List<String>) baseDao.findForList("AuthorityUserMapper.findMenuCodeByUserId",pd);
        //遍历两个集合
        if(!CollectionUtils.isEmpty(menuList) && !CollectionUtils.isEmpty(authMenuList)){

            for (PageData pageData : menuList) {
                String menuCode = pageData.getString("menuCode");
                if (authMenuList.contains(menuCode)) {
                    pageData.put("userId", pd.getString("userId"));
                    pageData.put("organizationId", pd.getString("organizationId"));
                    updateList.add(pageData);

                    menuCodeList.add(menuCode);
                } else {
                    pageData.put("userId", pd.getString("userId"));
                    pageData.put("userFlag", pd.getString("userFlag"));
                    pageData.put("organizationId", pd.getString("organizationId"));
                    pageData.put("createUser", pd.getString("createUser"));
                    pageData.put("createUserId", pd.getString("createUserId"));
                    addList.add(pageData);

                    menuCodeList.add(menuCode);
                }
            }

            for (String code : authMenuList) {
                if (!menuCodeList.contains(code)) {
                    deleteList.add(code);
                }
            }



        }else if(CollectionUtils.isEmpty(menuList) && !CollectionUtils.isEmpty(authMenuList)){

            deleteList.addAll(authMenuList);

        }else if(!CollectionUtils.isEmpty(menuList) && CollectionUtils.isEmpty(authMenuList)){
            for (PageData pageData : menuList) {
                pageData.put("createUser", pd.getString("createUser"));
                pageData.put("createUserId", pd.getString("createUserId"));
                pageData.put("userId", pd.getString("userId"));
                pageData.put("userFlag", pd.getString("userFlag"));
                pageData.put("organizationId", pd.getString("organizationId"));

                addList.add(pageData);

            }

        }

        //删除权限菜单表
        if(!CollectionUtils.isEmpty(deleteList)){
            pd.put("deleteList",deleteList);
            baseDao.delete("AuthorityUserMapper.batchDeleteMenuByCode", pd);
        }

        //更新权限菜单表
        if(!CollectionUtils.isEmpty(updateList)){
            baseDao.batchUpdate("AuthorityUserMapper.updateMenu", updateList);
        }

        //插入权限菜单表
        if(!CollectionUtils.isEmpty(addList)){
            baseDao.batchInsert("AuthorityUserMapper.batchInsertMenu",addList);

        }

        //变更授权表为已授权
        if(!CollectionUtils.isEmpty(updateList) || !CollectionUtils.isEmpty(addList)){
            baseDao.update("AuthorityUserMapper.updateAuthStatus", pd);
        }


    }

    /**
     * 删除授权对象
     *
     * @param pd
     */
    @Override
    @Transactional
    public void removeAuthorization(PageData pd) {
        List<String> userList = JSONArray.parseArray(pd.getString("userList"), String.class);

        //删除当前权限对应的人员
        pd.put("userList", userList);
        baseDao.delete("AuthorityUserMapper.batchDeleteUser", pd);

        //删除这些用户或者岗位的菜单权限
        baseDao.delete("AuthorityUserMapper.batchDeleteMenu", pd);


    }


    /**
     * 查询部门职务树
     *
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryDeptTree(PageData pd) {
        List<PageData> result = new ArrayList<>();
        //查询所有数据
        List<PageData> dataList = (List<PageData>) baseDao.findForList("DepartmentMapper.queryAllData", pd);
        if (!CollectionUtils.isEmpty(dataList)) {
            result = recursiveTreeList("-1", dataList);
        }

        return result;
    }

    /**
     * 递归树
     *
     * @param parentId
     * @param dataList
     * @return
     */
    public List<PageData> recursiveTreeList(String parentId, List<PageData> dataList) {
        List<PageData> childOrg = new ArrayList<>();
        for (PageData menu : dataList) {
            String orgCode = menu.getString("orgId");
            String pCode = menu.getString("parentId");
            if (pCode.equals(parentId)) {
                List<PageData> children = recursiveTreeList(orgCode, dataList);
                menu.put("children", children);
                childOrg.add(menu);
            }

        }

        return childOrg;
    }


    /**
     * 查询员工
     *
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryUser(PageData pd) {

        List<PageData> list = (List<PageData>) baseDao.findForList("AuthorityUserMapper.queryUser", pd);

        return list;

    }


    /**
     * 查询组织树
     *
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryOrganization(PageData pd) {
        List<PageData> list = (List<PageData>) baseDao.findForList("AuthorityUserMapper.queryOrganization", pd);
        return list;
    }


}
