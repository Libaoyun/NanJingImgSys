package com.rdexpense.manager.service.system.impl;

import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.entity.PageData;
import com.rdexpense.manager.service.system.RuleConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luxiangbao
 * @date 2020/8/19 11:48
 * @description 规则配置实现类
 */
@Service
public class RuleConfigServiceImpl implements RuleConfigService {


    @Autowired
    @Resource(name = "baseDao")
    private BaseDao dao;


    /**
     * 编辑规则配置
     * @param pd
     */
    @Override
    @Transactional
    public void saveRuleConfig(PageData pd) {
        String ruleListStr = pd.getString("ruleList");
        List<PageData> ruleList = JSONObject.parseArray(ruleListStr, PageData.class);


        List<PageData> addList = new ArrayList<>();
        List<PageData> updateList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(ruleList)) {
            for(PageData pageData : ruleList){

                pageData.put("createUser",pd.getString("createUser"));
                pageData.put("createUserId",pd.getString("createUserId"));

                if(StringUtils.isNotEmpty(pageData.getString("id"))){
                    updateList.add(pageData);
                }else{
                    addList.add(pageData);
                }
            }
        }

        //更新明细表
        if(!CollectionUtils.isEmpty(updateList)){
            dao.batchUpdate("RuleConfigMapper.updateRule", updateList);
        }

        //插入明细表
        if(!CollectionUtils.isEmpty(addList)){
            dao.batchInsert("RuleConfigMapper.batchInsertRule",addList);

        }



    }


    /**
     * 查询规则配置
     * @param pd
     * @return
     */
    @Override
    public List<PageData> getRuleConfig(PageData pd) {

        List<PageData> list = (List<PageData>)dao.findForList("RuleConfigMapper.queryAllData",pd);

        return list;
    }



}
