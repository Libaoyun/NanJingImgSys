package com.rdexpense.manager.service.system.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.common.base.dao.mysql.BaseDao;
import com.common.entity.PageData;
import com.common.util.ListHandleUtil;
import com.rdexpense.manager.service.system.DepartmentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author luxiangbao
 * @date 2021/11/12 11:48
 * @description 部门职务管理实现类
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private BaseDao baseDao;


    /**
     * 查询 组织管理 树结构
     *
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryOrgTree(PageData pd) {
        List<PageData> result = new ArrayList<>();
        //查询所有数据
        List<PageData> dataList = (List<PageData>) baseDao.findForList("DepartmentMapper.queryAllData", pd);
        if (!CollectionUtils.isEmpty(dataList)) {
            result = recursiveTreeList("-1", dataList);
        }

        return result;

    }


    /**
     * 拖拽整个树
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public List<PageData> updateOrgTree(PageData pd) {
        int i = 0;
        String data = pd.getString("data");
        List<PageData> orgPdList = JSON.parseArray(data, PageData.class);

        // 整理成集合
        List<PageData> newTreeDataList = rebuild(i, orgPdList);

        if(!CollectionUtils.isEmpty(newTreeDataList)){
            for(PageData data1 : newTreeDataList){
                data1.put("createUser",pd.getString("createUser"));
                data1.put("createUserId",pd.getString("createUserId"));
            }

            // 删除数据库组织数据
            baseDao.delete("DepartmentMapper.deleteAllData", pd);

            //分批插入数据
            if(newTreeDataList.size() > 50){
                List<List<PageData>> resultList = ListHandleUtil.splitMobile(newTreeDataList,newTreeDataList.size(),50);
                for(List<PageData> dataList : resultList){
                    //插入数据
                    baseDao.batchInsert("DepartmentMapper.insertNewTreeData", dataList);
                }

            }else{
                //插入数据
                baseDao.batchInsert("DepartmentMapper.insertNewTreeData", newTreeDataList);
            }


            // 整理成树形结构
        }




        return queryOrgTree(pd);
    }

    private List<PageData> rebuild(Integer i, List<PageData> orgPdList) {
        List<PageData> result = new LinkedList<>();
        for (PageData orgPd : orgPdList) {
            i++;
            orgPd.put("orderNumber", i);
            result.add(orgPd);
            String children = orgPd.getString("children");

            List<PageData> newChild = new ArrayList<>();
            if (!org.springframework.util.StringUtils.isEmpty(children)) {
                List<PageData> childList = JSONArray.parseArray(children, PageData.class);
                if (!CollectionUtils.isEmpty(childList)) {
                    for (PageData childData : childList) {
                        newChild.add(childData);

                    }
                }
                if (!CollectionUtils.isEmpty(newChild)) {
                    for (PageData childData : newChild) {
                        i++;
                        List<PageData> list = new ArrayList<>();
                        list.add(childData);
                        List<PageData> child = rebuild(i, list);
                        result.addAll(child);
                    }
                }
            }
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
     * 新增部门
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void addDepartment(PageData pd) {
        //  生成项目编码和排序数值
        getOrgCodeAndOrderNumber(pd);

        //新增部门表
        baseDao.insert("DepartmentMapper.insertDepartmentData", pd);


    }


    /**
     * 编辑部门
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void updateDepartment(PageData pd) {

        //获取节点及一下所有节点，统一修改禁用状态
        List<PageData> codePdList = getAllCode(pd);
        if(!CollectionUtils.isEmpty(codePdList)){
            // 修改部门表节点及其子节点状态
            baseDao.batchUpdate("DepartmentMapper.updateDepartmentStatus", codePdList);
        }


        // 修改部门表内容
        baseDao.update("DepartmentMapper.updateDepartmentData", pd);

    }


    /**
     * 查询部门详情
     * @param pd
     * @return
     */
    @Override
    public PageData getDepartment(PageData pd) {
        PageData data = (PageData) baseDao.findForObject("DepartmentMapper.getDepartment",pd);
        return data;
    }

    /**
     * 新增职务
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void addPost(PageData pd) {
        //  生成项目编码和排序数值
        getOrgCodeAndOrderNumber(pd);

        //新增职务表
        baseDao.insert("DepartmentMapper.insertPostData", pd);


    }


    /**
     * 编辑职务
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void updatePost(PageData pd) {


        List<PageData> codePdList = getAllCode(pd);

        if(!CollectionUtils.isEmpty(codePdList)){
            // 修改项目表节点及其子节点状态
            baseDao.batchUpdate("DepartmentMapper.updatePostStatus", codePdList);
        }

        // 修改项目表内容
        baseDao.update("DepartmentMapper.updatePostData", pd);


    }


    /**
     * 查询职务详情
     * @param pd
     * @return
     */
    @Override
    public PageData getPost(PageData pd) {
        PageData data = (PageData) baseDao.findForObject("DepartmentMapper.getPost",pd);
        return data;
    }

    private List<PageData> getAllCode(PageData pd){

        List<PageData> codePdList = new ArrayList<>();
        Integer status = pd.getInt("status");
        Integer orgId = pd.getInt("orgId");

        String flag = pd.getString("flag");

        //如果将该节点设为禁用，则需要找出该加点下所有节点，都设为禁用
        // 节点及其子节点的编码集合
        List<Integer> codeToShow = new ArrayList<>();
        codeToShow.add(orgId);
        // 查询全部数据
        List<PageData> orgPdList = (List<PageData>) baseDao.findForList("DepartmentMapper.queryAllData", pd);
        if(!CollectionUtils.isEmpty(orgPdList)){
            browseTree(codeToShow, orgPdList, orgId);
            // 类型转换   List<Integer>  -->   List<PageData>
            codePdList = codeToShow.stream().map(item -> {
                PageData codePd = new PageData();
                codePd.put("status", status);
                codePd.put("orgId", item);
                return codePd;
            }).collect(Collectors.toList());


            if(StringUtils.isBlank(flag)){
                // 修改主表节点及其子节点状态
                baseDao.batchUpdate("DepartmentMapper.updateNodeStatus", codePdList);
            }

        }

        if(StringUtils.isBlank(flag)){
            // 修改主表内容
            baseDao.update("DepartmentMapper.updateNodeData", pd);
        }

        return  codePdList;
    }


    /**
     * 删除项目节点
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void deleteOrgNode(PageData pd) {

        //查询节点及一下所有节点
        pd.put("flag",1);
        List<PageData> codePdList = getAllCode(pd);

        //删除节点及其子节点数据
        if(!CollectionUtils.isEmpty(codePdList)){
            // 删除主表
            baseDao.delete("DepartmentMapper.deleteOrgData", codePdList);
            // 删除部门表
            baseDao.delete("DepartmentMapper.deleteDepartmentData", codePdList);
            // 删除职务表
            baseDao.delete("DepartmentMapper.deletePostData", codePdList);

        }


    }

    /**
     * 获取当前节点及其子节点的编码code
     *
     * @param codeList  存放编码的集合
     * @param orgPdList 树形数据
     * @param orgCode   当前节点的编码
     */
    private void browseTree(List<Integer> codeList, List<PageData> orgPdList, Integer orgCode) {
        boolean flag = false;
        for (PageData sysOrg : orgPdList) {
            if (sysOrg.getInt("parentId").equals(orgCode)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            for (PageData datum : orgPdList) {
                if (datum.getInt("parentId").equals(orgCode)) {
                    codeList.add(datum.getInt("orgId"));
                    browseTree(codeList, orgPdList, datum.getInt("orgId"));
                }
            }
        }
    }



    /**
     * 生成项目编码和排序数值
     *
     * @param pd
     */
    private void getOrgCodeAndOrderNumber(PageData pd) {

        PageData orgPd = (PageData) baseDao.findForObject("DepartmentMapper.selectOrgMaxOrderNumber", pd);
        if (null == orgPd) {
            pd.put("orderNumber", 10);
            pd.put("orgId", 10);
        } else {
            pd.put("orderNumber", orgPd.getInt("orderNumber") + 1);
            pd.put("orgId", orgPd.getInt("orgId") + 1);
        }

        //新增主表
        baseDao.insert("DepartmentMapper.insertTreeData", pd);
    }

}
