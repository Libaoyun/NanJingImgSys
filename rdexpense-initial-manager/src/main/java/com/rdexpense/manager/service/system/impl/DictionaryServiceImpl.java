package com.rdexpense.manager.service.system.impl;

import com.alibaba.fastjson.JSONArray;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.ErrorCodeEnum;
import com.rdexpense.manager.service.system.DictionaryService;
import com.rdexpense.manager.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * @Description:
 * @Author: rdexpense
 * @Date: 2020/12/30 09:47
 * @Version: 1.0
 */
@Service
public class DictionaryServiceImpl implements DictionaryService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());



    @Autowired
    private LogUtil logUtil;

    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;

    /**
     * 查询字典数据
     * @return
     */
    @Override
    public List<PageData> findDictionaryTreeInfo() {
        List<PageData> uqDictionaryList = (List<PageData>) baseDao.findForList("DictionaryMapper.selectDictionaryTreeList");
        return uqDictionaryList;

    }


    /**
     * 查询字典枚举值列表
     * @param pd
     * @return
     */
    @Override
    public List<PageData> findDictionariesList(PageData pd) {
        List<PageData> uqDictionaryList = (List<PageData>) baseDao.findForList("DictionaryMapper.queryDictionariesList", pd);
        return uqDictionaryList;

    }

    /**
     * 新增字典枚举值
     * @param pd
     */
    @Override
    @Transactional
    public void createDictionaryInfo(PageData pd) {

        //判断枚举值是否重复
        List<PageData> uqDictionaryList = (List<PageData>) baseDao.findForList("DictionaryMapper.queryDictionariesList", pd);
        if (!CollectionUtils.isEmpty(uqDictionaryList)) {
            for (PageData dic : uqDictionaryList) {
                if (dic.getString("dicEnumName").equals(pd.getString("dicEnumName"))) {
                    throw new MyException(ErrorCodeEnum.DATA_HAS_EXIST.desc());
                }
            }
        }

        //判断是否重复
        List<PageData> enumList = (List<PageData>) baseDao.findForList("DictionaryMapper.queryEnumList", pd);
        if (!CollectionUtils.isEmpty(enumList)) {
            throw new MyException(ErrorCodeEnum.DIC_ENUM_EXIST.desc());
        }

        pd.put("isShow", 1);
        baseDao.insert("DictionaryMapper.insertDictionary", pd);

    }

    /**
     * 编辑枚举值
     * @param pd
     */
    @Override
    @Transactional
    public void updateDictionaryInfo(PageData pd) {

        List<PageData> enumList = (List<PageData>) baseDao.findForList("DictionaryMapper.queryDictionariesByDicEnumName", pd);
        if(!CollectionUtils.isEmpty(enumList)){
            for(PageData dicEnum : enumList){
                if(dicEnum.getString("dicEnumName").equals(pd.getString("dicEnumName")) &&
                        dicEnum.getString("dicEnumId").equals(pd.getString("dicEnumId"))){
                    throw new MyException(ErrorCodeEnum.DIC_ENUM_EXIST.desc());
                }
            }
        }

        baseDao.update("DictionaryMapper.updateDictionaryInfoByEnumId",pd);

    }


    @Override
    @Transactional
    public void deleteDictionaryInfo(PageData pd) {
        List<String> list = JSONArray.parseArray(pd.getString("ids"), String.class);
        //删除风险数据表数据
        baseDao.batchDelete("DictionaryMapper.deleteDictionaryInfo", list);
    }



    @Override
    public List<PageData> findPullDownDictionariesList(PageData pd) {
        List<String> list = JSONArray.parseArray(pd.getString("dicTypeIds"), String.class);

        List<PageData> uqDictionaryList = (List<PageData>) baseDao.findForList("DictionaryMapper.selectPullDownDictionaryTreeList", list);

        return uqDictionaryList;

    }


    /**
     * 查询数据字典类型数据
     * @return
     */
    @Override
    public List<PageData> queryDictionaryType() {

        List<PageData> result = new LinkedList<>();
        List<PageData> dictTypeList = (List<PageData>) baseDao.findForList("DictionaryTypeMapper.selectList");

        if(!CollectionUtils.isEmpty(dictTypeList)){
            List<PageData> list = recursiveTreeList("0",dictTypeList);
            result.addAll(list);
        }

        return result;
    }

    /**
     * 查询数据字典类型数据,根据字典类型ID
     * @return
     */
    @Override
    public List<PageData> queryDictionaryTypeByPid(PageData pd) {
        List<PageData> dictTypeList = (List<PageData>) baseDao.findForList("DictionaryTypeMapper.selectListByParentId",pd);

        return dictTypeList;
    }

    /**
     * 新增字典类型
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void saveDictionType(PageData pd) {

        //先查询新增的数据在数据库中是否已经存在
        PageData data = (PageData) baseDao.findForObject("DictionaryTypeMapper.selectDictionaryTypeByTypeIdOrTypeName",pd);
        if(data != null){
            throw new MyException(ErrorCodeEnum.DATA_HAS_EXIST.desc());
        }

        baseDao.insert("DictionaryTypeMapper.insertDictionTypeData",pd);

    }

    /**
     * 修改字典类型数据
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void modifyDictionType(PageData pd) {
        //先查询新增的数据在数据库中是否已经存在

        List<PageData> dictTypeList = (List<PageData>) baseDao.findForList("DictionaryTypeMapper.selectListByDicTypeId",pd);
        if (!CollectionUtils.isEmpty(dictTypeList)){
            //类别的id 存在，那么提示数据已经存在
            throw new MyException(ErrorCodeEnum.DATA_HAS_EXIST.desc());
        }

        baseDao.update("DictionaryTypeMapper.updateDictionTypeData",pd);
    }


    /**
     * 删除字典类型数据
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void removeDictionType(PageData pd) {
        List<PageData> dictTypeList = (List<PageData>) baseDao.findForList("DictionaryTypeMapper.selectList");
        //筛选出字典类型的id
        List<String> codeList =  new ArrayList<>();
        String dicTypeId = pd.getString("dicTypeId");
        codeList.add(dicTypeId);
        if(dicTypeId != null){
            List<PageData> list = new ArrayList<>();
            recursiveTreeList1(list,dicTypeId,dictTypeList);
            if(!CollectionUtils.isEmpty(list)){
                for(PageData type : list){
                    codeList.add(type.getString("dicTypeId"));
                }
            }
        }


        if(!CollectionUtils.isEmpty(codeList)){
            //删除字典类型
            baseDao.batchDelete("DictionaryTypeMapper.deleteDictionaryType",codeList);

            //筛选对应的枚举值
            baseDao.batchDelete("DictionaryTypeMapper.deleteDictionary",codeList);
        }



    }


    /**
     * 递归树
     * @param parentId
     * @param dataList
     * @return
     */
    public List<PageData> recursiveTreeList(String parentId, List<PageData> dataList) {
        List<PageData> childList = new ArrayList<>();
        for (PageData type : dataList) {
            String dicTypeId = type.getString("dicTypeId");
            String dicTypeParentCode = type.getString("dicTypeParentId");
            if(dicTypeParentCode.equals(parentId)){
                List<PageData> childrenList = recursiveTreeList(dicTypeId,dataList);
                type.put("children",childrenList);
                childList.add(type);
            }

        }
        return childList;
    }


    public void recursiveTreeList1(List<PageData> list, String dicTypeId, List<PageData> dataList) {
        for (PageData type : dataList) {
            String dicTypeCode = type.getString("dicTypeId");
            String dicTypeParentCode = type.getString("dicTypeParentId");
            if(dicTypeParentCode.equals(dicTypeId)){
                list.add(type);
                recursiveTreeList1(list,dicTypeCode,dataList);

            }

        }

    }


}
