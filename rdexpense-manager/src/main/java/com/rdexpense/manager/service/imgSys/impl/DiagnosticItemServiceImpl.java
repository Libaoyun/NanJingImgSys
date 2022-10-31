package com.rdexpense.manager.service.imgSys.impl;

import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.rdexpense.manager.service.imgSys.DiagnosticItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @description: 科室诊疗项目
 * @author: Libaoyun
 * @date: 2022-10-19 17:53
 **/
@Service
public class DiagnosticItemServiceImpl implements DiagnosticItemService {

    @Autowired
    private BaseDao baseDao;

    @Override
    public void addDiagnosticItem(PageData pd) {
        List<PageData> itemList = (List<PageData>) baseDao.findForList("DiagnosticItemMapper.isExistItem", pd);
        if (!CollectionUtils.isEmpty(itemList)) {
            throw new MyException("该项目名称/编码已存在,请检查确认!");
        }
        PageData maxIdData = (PageData) baseDao.findForObject("DiagnosticItemMapper.selectMaxIdAndSerialNum", pd);
        Integer maxId = (maxIdData == null || maxIdData.getInt("id") == null) ? 0 : maxIdData.getInt("id") + 1;
        // FIXME 这里待讨论序列号初始值为多少，暂定默认200000
        Integer maxSerialNum = (maxIdData == null || maxIdData.getInt("itemSerialNum") == null) ? 200000
                : maxIdData.getInt("id") + 1;
        pd.put("id", maxId);
        pd.put("itemSerialNum", maxSerialNum);
        baseDao.insert("DiagnosticItemMapper.insertDiagnosticItem", pd);
    }

    @Override
    @Transactional
    public void deleteDiagnosticItem(PageData pd) throws Exception {
        List<PageData> itemList = (List<PageData>) baseDao.findForList("DiagnosticItemMapper.isExistItem", pd);
        if (CollectionUtils.isEmpty(itemList)) {
            throw new MyException("该项目不存在,请刷新后确认!");
        }
        PageData hasChild = (PageData) baseDao.findForObject("DiagnosticItemMapper.selectCountChild", pd);
        if (hasChild.getInt("countChild") > 0) {
            throw new Exception("该项目下有子项目，请先删除子项目信息！");
        }
        baseDao.delete("DiagnosticItemMapper.deleteDiagnosticItem", pd);

        //FIXME 注意这里在删除时考虑是否删除关联信息，

    }

    @Override
    @Transactional
    public void updateDiagnosticItem(PageData pd) {
        List<PageData> itemList = (List<PageData>) baseDao.findForList("DiagnosticItemMapper.isExistItem", pd);
        if (CollectionUtils.isEmpty(itemList)) {
            throw new MyException("该项目不存在,请刷新后确认!");
        }
        baseDao.update("DiagnosticItemMapper.updateDiagnosticItem", pd);
    }


    /**
     * 树状查询所有项目结果（以一个起始节点作为开始）
     * @param rootSerialNum
     * @return
     */
    @Override
    public List<PageData> getAllDiagnosticItem(String rootSerialNum) {
        List<PageData> result = new LinkedList<>();
//        先找出所有项目
        List<PageData> pd = (List<PageData>) baseDao.findForList("DiagnosticItemMapper.getAllDiagnosticItem");
        if (pd != null && pd.size() > 0) {
            // 然后将所有项目进行整理，以父-子顺序排序
            List<PageData> list = recursiveTreeDiagnosticItemList(rootSerialNum, pd);
            result.addAll(list);
        }
        return result;
    }

    // 递归查询子项目
    public List<PageData> recursiveTreeDiagnosticItemList(String parentId, List<PageData> pd) {
        List<PageData> child = new ArrayList<>();
        for (PageData pageData : pd) {
            String itemSerialNum = pageData.getString("itemSerialNum");
            String pid = pageData.getString("parentSerialNum");
            if (parentId.equals(pid)) {
                // 递归查询，以父序列id作为线索进行整理
                List<PageData> children = recursiveTreeDiagnosticItemList(itemSerialNum, pd);

                pageData.put("children", children);
                child.add(pageData);
            }

        }
        return child;
    }
}
