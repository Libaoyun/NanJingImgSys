package com.rdexpense.manager.service.imgsysPhotography.impl;

import com.alibaba.fastjson.JSONArray;
import com.common.base.dao.mysql.BaseDao;
import com.common.entity.PageData;
import com.rdexpense.manager.service.imgsysPhotography.ImgSysPhotoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@EnableAspectJAutoProxy(proxyTargetClass = true,exposeProxy = true)
@Slf4j
@Service
public class ImgSysPhotoServiceImpl implements ImgSysPhotoService {
    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;

    /**
     * 查询影像主信息列表,用户患者影像缩略图展示
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryImgMain(PageData pd) {
        List<PageData> imgList = (List<PageData>) baseDao.findForList("PhotoMainMapper.queryList", pd);
        return imgList;
    }

    @Override
    public List<PageData> queryImgExProperties(PageData pd){

        return null;
    }

    @Override
    @Transactional
    public List<PageData> queryImgMainBySNList(PageData pd) {
        List<String> snList = JSONArray.parseArray(pd.getString("serialNumList"), String.class);
        List<PageData> imgMainList = (List<PageData>) baseDao.findForList("PhotoMainMapper.queryBySerialNumList", snList);

        return imgMainList;
    }

    @Override
    @Transactional
    public List<PageData> queryPropertiesByImgSNList(PageData pd) {
        List<String> imgSnList = JSONArray.parseArray(pd.getString("imgSerialNumList"), String.class);
        List<PageData> imgPropertyExList = (List<PageData>) baseDao.findForList("PhotoPropertyExMapper.queryByImgSerialNumList", imgSnList);

        return imgPropertyExList;
    }

    @Override
    public void deleteImgProperties(PageData pd) {

    }

    @Override
    public void deleteImgMain(PageData pd) {

    }

    @Override
    @Transactional
    public void insertImgMain(PageData pd) {


    }

    @Override
    public void insertImgProperties(List<PageData> pdList) {

    }

    @Override
    public void updateImgMain(PageData pd) {

    }

    @Override
    public void updateImgProperty(PageData pd) {

    }
}
