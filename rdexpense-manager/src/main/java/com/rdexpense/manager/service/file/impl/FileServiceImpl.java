package com.rdexpense.manager.service.file.impl;

import com.common.base.dao.mysql.BaseDao;
import com.common.entity.PageData;
import com.rdexpense.manager.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("FileService")
public class FileServiceImpl implements FileService {

    @Autowired
    @Resource(name = "baseDao")
    private BaseDao dao;

    /**
     * 查询附件
     * @param pd
     * @return
     */
    @Override
    public List<PageData> query(PageData pd) {
        List<PageData> list = (List<PageData>) dao.findForList("FileMapper.queryFile", pd);
        return list;
    }

    /**
     * 删除附件
     * @param pd
     * @return
     */
    @Override
    public void delete(PageData pd) {
        dao.delete("FileMapper.deleteFile",pd);
    }
}
