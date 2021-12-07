package com.rdexpense.manager.service.projectApply.impl;

import com.common.base.dao.mysql.BaseDao;
import com.common.entity.PageData;
import com.rdexpense.manager.service.projectApply.ProjApplyMainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class ProjApplyMainServiceImpl implements ProjApplyMainService {
    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;

    /**
     * 查询项目合同列表
     * @return
     */
    @Override
    @Transactional
    public List<PageData> queryProjApplyMainList(PageData pd){
        List<PageData> projApplyMainList =
                (List<PageData>) baseDao.findForList("ProjApplyMainMapper.selectAll", pd);

        if( !CollectionUtils.isEmpty(projApplyMainList) ) {
            return projApplyMainList;
        }

        return projApplyMainList;
    }

    @Override
    @Transactional
    public PageData queryByBusinessId(PageData pd) {
        PageData result = (PageData) baseDao.findForObject("ProjApplyMainMapper.queryOneByBusinessId", pd);
        result.put("projectId", result.getString("businessId"));
        return result;
    }
}
