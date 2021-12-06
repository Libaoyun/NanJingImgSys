package com.rdexpense.manager.service.system.impl;

import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.dao.redis.RedisDao;
import com.common.entity.PageData;
import com.common.util.AwsUtil;
import com.common.util.ConstantValUtil;
import com.rdexpense.manager.service.system.TemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author luxiangbao
 * @date 2020/6/3 16:50
 * @describe
 */
@Service("TemplateService")
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    @Resource(name = "baseDao")
    private BaseDao dao;



    /**
     * 新建
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void addRecord(PageData pd) {
        String businessId = "ZSKGL-" + UUID.randomUUID().toString();//生成业务主键ID
        pd.put("businessId", businessId);

        //插入到附件表
        String file = pd.getString("fileList");
        if (StringUtils.isNotBlank(file)) {
            List<PageData> listFile = JSONObject.parseArray(file, PageData.class);
            if (!CollectionUtils.isEmpty(listFile)) {
                for (PageData data : listFile) {
                    data.put("businessId", businessId);

                    String fileStr = data.getString("fileName");
                    if(StringUtils.isNotBlank(fileStr)){
                        String suffix = fileStr.substring(fileStr.lastIndexOf(".") + 1);
                        String frofix = fileStr.substring(0,fileStr.lastIndexOf("."));

                        pd.put("fileFormat",suffix);
                        pd.put("fileName",frofix);

                    }
                    pd.put("fileSize",data.getString("fileSize"));


                }
                dao.batchInsert("FileMapper.saveFileDetail", listFile);
            }
        }

        //插入主表
        dao.insert("TemplateMapper.insertRecord", pd);

    }

    /**
     * 编辑
     *
     * @param pd
     * @throws Exception
     */
    @Override
    @Transactional
    public void updateRecord(PageData pd) {
        String businessId = pd.getString("businessId");


        //编辑附件表
        //更新附件表，前端只传无id的，将这些数据入库
        List<PageData> fileList = new ArrayList<PageData>();
        String attachmentDetailStr = pd.getString("fileList");
        if (StringUtils.isNotBlank(attachmentDetailStr)) {
            List<PageData> listFile = JSONObject.parseArray(attachmentDetailStr, PageData.class);
            if (!CollectionUtils.isEmpty(listFile)) {
                for (PageData data : listFile) {
                    String frontId = data.getString("id");
                    if (frontId == null || frontId.equals("")) {
                        data.put("businessId", businessId);
                        fileList.add(data);

                        String fileStr = data.getString("fileName");
                        if(StringUtils.isNotBlank(fileStr)){
                            String suffix = fileStr.substring(fileStr.lastIndexOf(".") + 1);
                            String frofix = fileStr.substring(0,fileStr.lastIndexOf("."));

                            pd.put("fileFormat",suffix);
                            pd.put("fileName",frofix);

                        }
                        pd.put("fileSize",data.getString("fileSize"));
                    }




                }
                if (fileList.size() > 0) {

                    dao.delete("FileMapper.deleteFileDetailByBusinessIds",fileList);
                    dao.batchInsert("FileMapper.saveFileDetail", fileList);
                }
            }

        }

        //更新主表
        dao.update("TemplateMapper.updateRecord", pd);

    }



    /**
     * 查询列表
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public List<PageData> searchRecord(PageData pd) {
        //查询数据信息
        List<PageData> result = (List<PageData>) dao.findForList("TemplateMapper.queryRecordByParams", pd);

        if(!CollectionUtils.isEmpty(result)){
            List<PageData> fileList = (List<PageData>) dao.findForList("FileMapper.queryByBusinessIds",result);
            TreeMap<String, List<PageData>> map = null;
            if (!CollectionUtils.isEmpty(fileList)) {
                map = fileList.stream().collect(Collectors.groupingBy(o -> o.getString("businessId"), TreeMap::new, Collectors.toList()));
            }

            if(map != null){
                for(PageData data : result){
                    String businessId = data.getString("businessId");
                    if(StringUtils.isNotBlank(businessId)){
                        List<PageData> list = map.get(businessId);
                        if(!CollectionUtils.isEmpty(list)){
                            AwsUtil.queryOneUrl(list, ConstantValUtil.BUCKET_PRIVATE);
                            data.put("fileUrl", list.get(0).getString("url"));
                            data.put("fileOriginalName", list.get(0).getString("fileName"));
                        }

                    }
                }
            }

        }


        return result;
    }



    /**
     * 删除
     *
     * @param pd
     */
    @Override
    @Transactional
    public void deleteRecord(PageData pd) {

        //校验不通过的部分
        List<String> removeList = JSONObject.parseArray(pd.getString("businessIdList"), String.class);
        //删除主表
        dao.batchDelete("TemplateMapper.bacthDeleteRecord", removeList);

        //删除文件
        dao.batchDelete("TemplateMapper.batchDeleteFile", removeList);


    }

    /**
     * 查看详情
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public PageData recordDetail(PageData pd){

        PageData request = (PageData) dao.findForObject("TemplateMapper.queryOneRecord", pd);

        // 查询附件表
        List<PageData> attachmentList = (List<PageData>) dao.findForList("FileMapper.queryFile", request);
        AwsUtil.queryOneUrl(attachmentList, ConstantValUtil.BUCKET_PRIVATE);
        request.put("fileList", attachmentList);

        return request;
    }





}
