package com.rdexpense.manager.service.file.impl;

import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.AwsUtil;
import com.common.util.ConstantValUtil;
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
                    data.put("uploadUserId", pd.getString("createUserId"));
                    data.put("uploadUserName", pd.getString("createUser"));
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
                        data.put("uploadUserId", pd.getString("createUserId"));
                        data.put("uploadUserName", pd.getString("createUser"));

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


    /**
     * 删除附件
     * @param pd
     */

    @Override
    public void deleteAttachment(PageData pd) {
        String businessIdListStr = pd.getString("businessIdList");
        if (!businessIdListStr.isEmpty()){
            List<String> businessIdList = JSONObject.parseArray(businessIdListStr, String.class);
            //删除文件
            dao.batchDelete("FileMapper.batchDeleteFile", businessIdList);
        } else {
            //删除文件
            dao.delete("FileMapper.deleteFileAttachmentByBusinessId", pd);
        }



    }

    /**
     * 查询附件详情
     * @param pd
     * @return
     */
    @Override
    public PageData queryFileByBusinessId(PageData pd) {
        List<PageData> attachmentList = (List<PageData>) dao.findForList("FileMapper.queryFile", pd);
        if(!CollectionUtils.isEmpty(attachmentList)){
            AwsUtil.queryOneUrl(attachmentList, ConstantValUtil.BUCKET_PRIVATE);
            pd.put("attachmentList", attachmentList);

        } else {
            List<PageData> List = new ArrayList<>();
            if(pd != null){
                pd.put("attachmentList", List);
            } else {
                throw new MyException("查询失败");
            }
        }

        List<PageData> approvalAttachmentList = (List<PageData>) dao.findForList("FileMapper.queryApproveFile", pd);
        if(!CollectionUtils.isEmpty(approvalAttachmentList)){
            AwsUtil.queryOneUrl(approvalAttachmentList, ConstantValUtil.BUCKET_PRIVATE);
            pd.put("approvalAttachmentList", approvalAttachmentList);

        } else {
            List<PageData> List = new ArrayList<>();
            if(pd != null){
                pd.put("approvalAttachmentList", List);
            } else {
                throw new MyException("查询失败");
            }
        }
        return pd;
    }


    /**
     *
     * @param pd
     * @return
     */
    @Override
    public PageData insertApproveFile(PageData pd) {
        PageData result = new PageData();
        StringBuffer idBuffer = new StringBuffer();
        StringBuffer nameBuffer = new StringBuffer();

        PageData wait = (PageData)dao.findForObject("FlowMapper.queryApproveWaitData",pd);
        if(wait == null){
            return result;
        }

        String file = pd.getString("approvalAttachmentList");
        if (StringUtils.isNotBlank(file)) {
            List<PageData> listFile = JSONObject.parseArray(file, PageData.class);
            if (listFile.size() > 0) {
                for (PageData data : listFile) {
                    data.put("uploadUserId", pd.getString("createUserId"));
                    data.put("uploadUserName", pd.getString("createUser"));

                    data.put("businessId", wait.getString("businessId"));
                    data.put("fileType",1);
                    dao.insert("FileMapper.saveFile", data);

                    nameBuffer.append(data.getString("fileName")).append("、");

                    idBuffer.append(data.getString("id")).append("、");
                }

            }
        }

        String idBufferStr = idBuffer.toString();
        String nameBufferStr = nameBuffer.toString();



        if(StringUtils.isNotBlank(idBufferStr) && StringUtils.isNotBlank(nameBufferStr)){
            result.put("fileId",idBufferStr.substring(0,idBufferStr.length() - 1));
            result.put("fileName",nameBufferStr.substring(0,nameBufferStr.length() - 1));
        }




        return result;
    }


    @Override
    public PageData selectZipName(PageData pd) {
        return (PageData) dao.findForObject("FileMapper.selectZipName",pd);
    }
}
