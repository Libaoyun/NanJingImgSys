package com.rdexpense.manager.service.itemClosureCheck.itemClosureCheck.impl;

import com.alibaba.fastjson.JSONArray;
import com.common.base.dao.mysql.BaseDao;
import com.common.entity.PageData;
import com.rdexpense.manager.service.itemClosureCheck.ItemClosureCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class ItemClosureCheckImpl implements ItemClosureCheckService {

    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;

    /**
     * 查询列表
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryItemClosureCheckList(PageData pd) {

        List<PageData> userInfoList = (List<PageData>) baseDao.findForList("ItemClosureCheckMapper.queryItemClosureCheckList", pd);

        return userInfoList;
    }

    /**
     * 查询已审批完成的研发项目申请列表
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryProjectApplyList(PageData pd) {
        List<PageData> userInfoList = (List<PageData>) baseDao.findForList("ItemClosureCheckMapper.queryProjectApplyList", pd);
        return userInfoList;
    }
}
