package com.rdexpense.manager.service.file.impl;

import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.entity.PageData;
import com.rdexpense.manager.service.file.FileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
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


    /**
     * 新增附件
     * @param pd
     */
    @Override
    public void insert(PageData pd) {
        String file = pd.getString("attachmentList");
        if (StringUtils.isNotBlank(file)) {
            List<PageData> listFile = JSONObject.parseArray(file, PageData.class);
            if (listFile.size() > 0) {
                for (PageData data : listFile) {
                    data.put("businessId", pd.getString("businessId"));
                }
                dao.batchInsert("FileMapper.saveFileDetail", listFile);
            }
        }

    }


    /**
     * 编辑附件
     * @param pd
     */

    @Override
    public void update(PageData pd) {
        List<PageData> fileList = new ArrayList<PageData>();
        String attachmentDetailStr = pd.getString("attachmentList");
        if (StringUtils.isNotBlank(attachmentDetailStr)) {
            List<PageData> listFile = JSONObject.parseArray(attachmentDetailStr, PageData.class);
            if(!CollectionUtils.isEmpty(listFile)){
                for (PageData data : listFile) {
                    String frontId = data.getString("id");
                    if (frontId == null || frontId.equals("")) {
                        data.put("businessId", pd.getString("businessId"));
                        fileList.add(data);
                    }
                }
                if (fileList.size() > 0) {
                    dao.batchInsert("FileMapper.saveFileDetail", fileList);
                }
            }
        }
    }
}
