package com.rdexpense.manager.service.system.impl;

import com.alibaba.fastjson.JSONArray;
import com.common.base.dao.mysql.BaseDao;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.ConstantMsgUtil;
import com.rdexpense.manager.service.system.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author rdexpense
 * @date 2020/5/26 14:59
 * @describe
 */
@Service("MenuService")
public class MenuServiceImpl implements MenuService {

    @Autowired
    @Resource(name = "baseDao")
    private BaseDao dao;

    public static Map<String, String> comMap = new LinkedHashMap<>();// 组合按钮

    static {
        comMap.put("a10002", "create");
        comMap.put("a10003", "edit");
        comMap.put("a10001", "query");
        comMap.put("a10004", "approve");
    }
    /**
     * 显示菜单树
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryTree(PageData pd) {

        List<PageData> result = new LinkedList<>();
        List<PageData> menulist = (List<PageData>) dao.findForList("MenuMapper.queryAllMenu", pd);

        List<PageData> menuButtonList = (List<PageData>) dao.findForList("MenuMapper.queryAllMenuButton", pd);

        if(menulist != null && menulist.size() > 0){
            for(PageData pageData : menulist){
                String parentCode = pageData.getString("parentCode");
                if(parentCode.equals("1")){
                    pageData.put("children","");
                    result.add(pageData);
                }
            }

            List<PageData> list = recursiveTreeMenuList("0",menulist,menuButtonList);
            result.addAll(list);
        }

        return result;


    }


    /**
     * 查询菜单节点
     * @param pd
     * @return
     */
    @Override
    public PageData queryNode(PageData pd) {
        List<PageData> list = new ArrayList<>();
        String menuCode = pd.getString("menuCode");

        List<PageData> menulist = (List<PageData>) dao.findForList("MenuMapper.queryAllMenu", pd);

        List<PageData> menuButtonList = (List<PageData>) dao.findForList("MenuMapper.queryAllMenuButton", pd);

        PageData data = (PageData)dao.findForObject("MenuMapper.queryByMenuCode", pd);
        if(menulist != null && menulist.size() > 0){
            list = recursiveTreeMenuList(menuCode,menulist,menuButtonList);

        }

        if(data != null && list != null && list.size() > 0){
            data.put("children",list);
        }
        return data;
    }


    /**
     * 更新菜单树形图
     * @param pd
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PageData> updateTree(PageData pd) {
        String data = pd.getString("data");
        List<PageData> dataList = JSONArray.parseArray(data,PageData.class);

        if(!CollectionUtils.isEmpty(dataList)){

            List<PageData> dataList1 = assemblePageData(dataList);
            List<PageData> buttonList = new LinkedList<>();

            if(!CollectionUtils.isEmpty(dataList1)){
                for(PageData pageData : dataList1){
                    if(pageData.getString("pathRouting").equals("1")){
                        PageData button = new PageData();
                        button.put("menuCode",pageData.getString("menuCode"));
                        button.put("parentCode",pageData.getString("parentCode"));
                        button.put("comButton",pageData.getString("comButton"));
                        button.put("detailButton",comMap.get(pageData.getString("comButton")));

                        buttonList.add(button);

                    }
                }
            }

            //重新排版父节点
            List<PageData> parentList = new LinkedList<>();
            if(!CollectionUtils.isEmpty(dataList1)) {
                for (PageData pageData : dataList1) {

                    String pathRouting = pageData.getString("pathRouting");
                    String parentCode = pageData.getString("parentCode");
                    if(pathRouting.equals("1")){
                        for(PageData parentData : dataList1){
                            String menuCode = parentData.getString("menuCode");
                            if(menuCode.equals(parentCode)){
                                pageData.put("parentCode",parentData.getString("parentCode"));
                            }
                        }
                    }

                    parentList.add(pageData);

                }
            }



            //删除整个菜单表
            dao.delete("MenuMapper.deleteAllMenu", pd);
            //删除整个菜单路由表
            dao.delete("MenuMapper.deleteAllMenuButton", pd);

            int count = 1;
            List<PageData> countList = (List<PageData>) dao.findForList("MenuMapper.queryAllMenuCount", pd);
            if(!CollectionUtils.isEmpty(countList)){
                count = countList.get(0).getInt("countSize") + 1;
            }
            //重新排版顺序
            List<PageData> newDataList = new LinkedList<>();
            if(!CollectionUtils.isEmpty(parentList)){
                for(int i=0;i<parentList.size();i++){
                    PageData pageData = parentList.get(i);
                    pageData.put("orderNumber",String.valueOf(i+count));

                    newDataList.add(pageData);
                }
            }


            //插入菜单表
            if(!CollectionUtils.isEmpty(newDataList)){
                for(PageData pageData : newDataList){
                    dao.insert("MenuMapper.insertBatchMenu", pageData);
                }

            }

            //插入按钮
            if(!CollectionUtils.isEmpty(buttonList)){
                dao.batchInsert("MenuMapper.insertBatchMenuButton", buttonList);
            }

        }

        //返回树形结构
        List<PageData> result = queryTree(pd);

        return result;
    }


    /**
     * 解析出整个树形结构
     * @param dataList
     * @return
     */
    public List<PageData> assemblePageData(List<PageData> dataList){
        List<PageData> result = new LinkedList<>();
        for(PageData pageData : dataList){
            result.add(pageData);
            int levelNumber = pageData.getInt("levelNumber");
            String menuCode = pageData.getString("menuCode");
            String children = pageData.getString("children");

            List<PageData> newChild = new ArrayList<>();
            if(!StringUtils.isEmpty(children)){
                List<PageData> childList = JSONArray.parseArray(children,PageData.class);
                if(!CollectionUtils.isEmpty(childList)) {
                    for (PageData childData : childList) {
                        int childLevel = childData.getInt("levelNumber");
                        if((childLevel - levelNumber) == 1){
                            newChild.add(childData);
                        }

                    }
                }


                if(!CollectionUtils.isEmpty(newChild)){
                    for(PageData childData : newChild){
                        List<PageData> list = new ArrayList<>();
                        childData.put("parentCode",menuCode);
                        list.add(childData);
                        //      result.add(childData);
                        List<PageData> child = assemblePageData(list);
                        result.addAll(child);
                    }
                }
            }
        }

        return result;

    }



    /**
     * 递归树
     * @param parentId
     * @param menuList
     * @return
     */
    public List<PageData> recursiveTreeMenuList(String parentId, List<PageData> menuList,List<PageData> menuButtonList) {
        List<PageData> childMenu = new ArrayList<>();
        for (PageData menu : menuList) {
            String menuId = menu.getString("menuCode");
            String pid = menu.getString("parentCode");
            String authCode = menu.getString("authMenuCode");
            String pathRouting = menu.getString("pathRouting");
            if((parentId.equals(pid) && StringUtils.isEmpty(authCode)) || parentId.equals(authCode)){
                List<PageData> children = recursiveTreeMenuList(menuId,menuList,menuButtonList);

                if(!CollectionUtils.isEmpty(menuButtonList)){
                    StringBuffer buffer = new StringBuffer();
                    for(PageData button : menuButtonList){
                        if(button.getString("menuCode").equals(menuId) && pathRouting.equals("0")){
                            buffer.append(button.getString("authorityButtonCode")+",");
                        }
                    }
                    if(buffer.length() > 0){
                        menu.put("comButton",buffer.toString());
                    }

                }

                menu.put("children",children);
                childMenu.add(menu);
            }

        }
        return childMenu;
    }


    /**
     * 新增节点
     * @param pd
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveNode(PageData pd) {
        //判断菜单编码和名称是否重复
        PageData data = (PageData) dao.findForObject("MenuMapper.searchByCodeOrTitle", pd);
        if(data == null || data.size() == 0){

            pd = setDefaultValue(pd);
            PageData parentData = (PageData) dao.findForObject("MenuMapper.searchByParentCode", pd);
            pd.put("levelNumber",parentData.getInt("levelNumber") + 1);
            String pathRouting = pd.getString("pathRouting");
            String menuCode = pd.getString("menuCode");
            String comButton = pd.getString("comButton");
            //新增列表
            if(pathRouting.equals("0")){

                //插入菜单表
                int count = dao.insert("MenuMapper.insertMenu", pd);
                //插入菜单按钮
                if(count > 0){

                    batchInsertMenuButton(comButton,menuCode);
                }


            }else{//新增非列表

                //插入菜单路由
                pd.put("detailButton",comMap.get(comButton));
                int count = dao.insert("MenuMapper.insertMenuButton",pd);


                //插入菜单
                //查询父节点的父节点

                if (parentData != null) {
                    pd.put("parentCode",parentData.getString("parentCode"));
                }
                if(count > 0){
                    dao.insert("MenuMapper.insertMenu", pd);
                }



            }

        }


    }


    //插入数据库中默认的值
    public PageData setDefaultValue(PageData pageData){

        PageData data = (PageData) dao.findForObject("MenuMapper.searchMaxOrderNumber");
        if(data != null){
            pageData.put("orderNumber",data.getString("orderNumber"));
        }else{
            pageData.put("orderNumber",data.getString("1"));
        }
 //       pageData.put("menuType","1");
        pageData.put("redirect","");
        pageData.put("isValid","1");
        pageData.put("isHome","0");
        pageData.put("isAble","0");

        return pageData;

    }



    /**
     * 更新菜单节点
     * @param pd
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNode(PageData pd) {
        String id = pd.getString("id");
        String comButton = pd.getString("comButton");
        String menuCode = pd.getString("menuCode");
        List<PageData> list = (List<PageData>) dao.findForList("MenuMapper.searchByCodeOrTitle", pd);
        if(CollectionUtils.isEmpty(list) || checkSelf(list,id)){
            String pathRouting = pd.getString("pathRouting");
            //修改列表
            //更新菜单
            int count = dao.update("MenuMapper.updateMenu", pd);
            if(pathRouting.equals("0")){
                //更新菜单按钮
                if(count > 0){
                    int deleteCount = dao.delete("MenuMapper.deleteButtonByMenuCode",pd);
                    if(deleteCount > 0){
                        batchInsertMenuButton(comButton,menuCode);
                    }
                }

            }else{//修改非列表

                if(count > 0){
                    pd.put("detailButton",comMap.get(comButton));
                    dao.update("MenuMapper.updateMenuButton",pd);
                }

            }
        }

    }


    //批量插入菜单按钮
    public void batchInsertMenuButton(String comButton,String menuCode){
        if(!StringUtils.isEmpty(comButton)){
            String[] array = comButton.split(",");
            if(array.length > 0){
                List<PageData> buttonList = new ArrayList<>();
                for(String arr : array){
                    PageData buttonData = new PageData();
                    buttonData.put("menuCode",menuCode);
                    buttonData.put("authorityButtonCode",arr);

                    buttonList.add(buttonData);
                }

                dao.batchInsert("MenuMapper.batchInsertMenuButton",buttonList);
            }

        }
    }


    /**
     * 删除菜单节点
     * @param pd
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNode(PageData pd) {
        List<PageData> menulist = (List<PageData>) dao.findForList("MenuMapper.queryAllMenu", pd);
        List<PageData> menuButtonList = (List<PageData>) dao.findForList("MenuMapper.queryAllMenuButton", pd);
        List<String> menuCodeList =  new ArrayList<>();
        String menuCode = pd.getString("menuCode");
        menuCodeList.add(menuCode);
        if(!CollectionUtils.isEmpty(menulist)){
            List<PageData> list = recursiveTreeMenuList(menuCode,menulist,menuButtonList);
            if(!CollectionUtils.isEmpty(list)){
                for(PageData pageData : list){
                    menuCodeList.add(pageData.getString("menuCode"));
                }
            }
        }


        //删除菜单表
        dao.delete("MenuMapper.deleteMenu", menuCodeList);
        //删除菜单按钮表
        dao.delete("MenuMapper.deleteBatchMenuButton", menuCodeList);
        //删除按钮路由表
        dao.delete("MenuMapper.deleteMenuButton", menuCodeList);
        //删除已授权菜单表
        dao.delete("MenuMapper.deleteAuthorityMenu", menuCodeList);


    }



    public boolean checkSelf(List<PageData> list, String id){
        for(PageData pageData : list){
            if(pageData.getString("id").equals(id)){
                return true;
            }
        }
        return false;
    }



}
