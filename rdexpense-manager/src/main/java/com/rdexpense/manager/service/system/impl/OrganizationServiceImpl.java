package com.rdexpense.manager.service.system.impl;


import com.alibaba.fastjson.JSONArray;
import com.common.base.dao.mysql.BaseDao;

import com.common.entity.PageData;

import com.rdexpense.manager.service.system.OrganizationService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author luxiangbao
 * @date 2021/11/12 11:48
 * @description 组织管理实现类
 */
@Service
public class OrganizationServiceImpl implements OrganizationService {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    @Autowired
    private BaseDao baseDao;


    @Override
    public List<PageData> queryOrganizationList(PageData pd) {
        List<PageData> list = (List<PageData>) baseDao.findForList("OrganizationMapper.selectByParams",pd);

        return list;
    }


    @Override
    public PageData getOrganizationDetail(PageData pd) {
        PageData request = (PageData) baseDao.findForObject("OrganizationMapper.queryById", pd);

        return request;
    }

    /**
     * 新增项目
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void addOrganization(PageData pd) {

        //新增项目表
        baseDao.insert("OrganizationMapper.insertOrganization", pd);


    }


    /**
     * 编辑项目
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void updateOrganization(PageData pd) {

        // 修改项目表内容
        baseDao.update("OrganizationMapper.updateOrganization", pd);

    }


    /**
     * 删除项目节点
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void deleteOrganization(PageData pd) {

        List<String> idList = JSONArray.parseArray(pd.getString("idList"), String.class);

        if(!CollectionUtils.isEmpty(idList)){
            //删除组织信息
            baseDao.batchDelete("OrganizationMapper.deleteOrganization", idList);

            //删除用户权限表
            baseDao.batchDelete("OrganizationMapper.deleteAuthorityUser", idList);

            //删除权限菜单表
            baseDao.batchDelete("OrganizationMapper.deleteAuthorityMenu", idList);


        }




    }



}
