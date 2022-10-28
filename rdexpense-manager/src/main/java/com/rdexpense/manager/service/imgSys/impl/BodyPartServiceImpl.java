package com.rdexpense.manager.service.imgSys.impl;

import com.common.base.dao.mysql.BaseDao;
import com.common.entity.PageData;
import com.common.util.AwsUtil;
import com.common.util.ConstantValUtil;
import com.rdexpense.manager.service.imgSys.BodyPartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @description: 科室身体部位
 * @author: Libaoyun
 * @date: 2022-10-17 19:02
 **/

@Service
public class BodyPartServiceImpl implements BodyPartService {

    @Autowired
    private BaseDao baseDao;

    /**
     * 新增身体部位信息，但要注意id和序列号id默认为空，所以要手动获取最大值并进行填充
     * @param pd
     */
    @Override
    public void addBodyPart(PageData pd) {
        PageData maxIdData = (PageData) baseDao.findForObject("BodyPartMapper.selectMaxIdAndSerialNum", pd);
        if (pd.getString("standardPartFlag") == null){
            pd.put("standardPartFlag", "0");
        }
        Integer maxId = (maxIdData == null || maxIdData.getInt("id") == null) ? 0 : maxIdData.getInt("id") + 1;
        // FIXME 这里待讨论序列号初始值为多少，默认300000
        Integer maxSerialNum = (maxIdData == null || maxIdData.getInt("partSerialNum") == null) ? 300000
                : maxIdData.getInt("id") + 1;
        pd.put("id", maxId);
        pd.put("partSerialNum", maxSerialNum);
        baseDao.insert("BodyPartMapper.insertBodyPart", pd);
    }


    /**
     * 删除身体部位信息，但要注意删除时判断是否有子部位，若有则不能删除，必须向上删除
     * @param pd
     */
    @Override
    @Transactional
    public void deleteBodyPart(PageData pd) throws Exception {

        PageData hasChild = (PageData)baseDao.findForObject("BodyPartMapper.selectCountChild", pd);
        if (hasChild.getInt("countChild") > 0){
            throw new Exception("该部位下有子部位，请先删除子部位信息！");
        }
        baseDao.delete("BodyPartMapper.deleteBodyPart", pd);

        //FIXME 注意这里在删除时考虑是否删除关联信息，如科室内设备所能拍摄的部位，或者是表单上能显示的部位
    }


    /**
     * 修改部位信息
     * @param pd
     */
    @Override
    @Transactional
    public void updateBodyPart(PageData pd) {
        baseDao.update("BodyPartMapper.updateBodyPart", pd);
    }

    /**
     * 树状查询所有部位结果（以一个起始节点作为开始）
     * @param rootSerialNum
     * @return
     */
    @Override
    @Transactional
    public List<PageData> getAllBodyPart(String rootSerialNum) {
//        return (BodyPart) baseDao.findForObject("BodyPartMapper.getAllBodyPart", partSerialNum);
        List<PageData> result = new LinkedList<>();
        List<PageData> pd = (List<PageData>) baseDao.findForList("BodyPartMapper.getAllBodyPart");
        if(pd != null && pd.size() > 0){
            List<PageData> list = recursiveTreeBodyPartList(rootSerialNum,pd);
            result.addAll(list);
        }
        return result;
    }

    /**
     * 获取所有部位及其示意图，从云上获取
     * @return
     */
    @Override
    public List<PageData> getAvailableBodyPart() {
        List<PageData> list = (List<PageData>) baseDao.findForList("BodyPartMapper.getAvailableBodyPart");
        for (PageData pageData : list){
            if (pageData.getString("part_sketch_file")!=null && pageData.getString("part_sketch_file")!= ""){

                pageData.put("base64Str", AwsUtil.downloadBase64(pageData.getString("part_sketch_file"), ConstantValUtil.BODY_PART_IMAGE));
            }
        }
        return list;
    }

    /**
     * 为部位添加示意图，存储本地url
     * @param pageData
     */
    @Override
    public void addBodyPartImg(PageData pageData){
        baseDao.update("BodyPartMapper.addBodyPartImg", pageData);
    }

    public List<PageData> recursiveTreeBodyPartList(String parentId, List<PageData> pd) {
        List<PageData> childBodyPart = new ArrayList<>();
        for (PageData pageData : pd) {
            String partSerialNum = pageData.getString("partSerialNum");
            String pid = pageData.getString("parentSerialNum");
            if(parentId.equals(pid)){
                List<PageData> children = recursiveTreeBodyPartList(partSerialNum,pd);

                pageData.put("children",children);
                childBodyPart.add(pageData);
            }

        }
        return childBodyPart;
    }
}
