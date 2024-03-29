package com.rdexpense.manager.service.system.impl;


import com.alibaba.fastjson.JSON;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.ErrorCodeEnum;
import com.rdexpense.manager.dto.base.UserInfoDTO;
import com.rdexpense.manager.dto.system.menu.FunctionMenuButtonDTO;
import com.rdexpense.manager.dto.system.menu.Meta;
import com.rdexpense.manager.service.system.FunctionMenuService;

import com.rdexpense.manager.util.UseTokenInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class FunctionMenuServiceImpl implements FunctionMenuService {


    @Autowired
    private UseTokenInfo useTokenInfo;

    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;


    /**
     * 递归菜单树结构
     *
     * @param parentId 父级菜单
     * @param menuList 所有的菜单列表
     * @return List<FunctionMenu>
     */
    public List<PageData> treeMenuList(int parentId, List<PageData> menuList) {
        List<PageData> childMenu = new ArrayList<>();
        for (PageData menu : menuList) {
            int menuId = menu.getInt("menuCode");
            int pid = menu.getInt("parentCode");
            if (parentId == pid) {
                List<PageData> children = treeMenuList(menuId, menuList);
                menu.put("children", children);
                childMenu.add(menu);
            }
        }
        return childMenu;
    }


    public List<FunctionMenuButtonDTO> recursiveTreeMenuList(String parentId, List<FunctionMenuButtonDTO> menuList) {
        List<FunctionMenuButtonDTO> childMenu = new ArrayList<>();
        try {
            for (FunctionMenuButtonDTO menu : menuList) {

                String menuId = menu.getMenuCode().toString();
                String pid = menu.getParentCode().toString();
                if (parentId.equals(pid)) {
                    List<FunctionMenuButtonDTO> children = recursiveTreeMenuList(menuId, menuList);

                    List<FunctionMenuButtonDTO> resultList = children.stream().collect(
                            Collectors.collectingAndThen(
                                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FunctionMenuButtonDTO::getOrderNumber))), ArrayList::new));
                    menu.setChildren(resultList);
                    childMenu.add(menu);

                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return childMenu;
    }

    /**
     * 查询可以关联工作流的菜单树
     *
     * @param relateFlow 是否可以关联工作流 0-否 1-是 null-查询所有
     * @return ResponseEntity<List < FunctionMenu>>
     */
    @Override
    public List<PageData> queryMenuTree(String relateFlow) {
        List<PageData> menus = (List<PageData>) baseDao.findForList("RouteMenuMapper.queryMenuList", relateFlow);
        if (!CollectionUtils.isEmpty(menus)) {
            treeMenuList(0, menus);
        } else {
            menus = Collections.EMPTY_LIST;
        }
        return menus;
    }




    /**
     * 查询路由下按钮
     *
     * @return ResponseEntity<List < FunctionMenu>>
     */
    @Override
    public List<FunctionMenuButtonDTO> queryRoutingMenuTree(String token) {

        List<Map> allData = (List<Map>) baseDao.findForList("RouteMenuMapper.queryAllButtonRoutingList");
        ResponseEntity<UserInfoDTO> responseEntity = this.getUserInfo(token);
        if (responseEntity.getCode() != 0) {
            throw new MyException(responseEntity.getMsg());
        }
        UserInfoDTO userInfoVo = responseEntity.getData();
        List<String> userIdList = new ArrayList<>();
        userIdList.add(userInfoVo.getUserCode());
        String postStr = userInfoVo.getPostId();
        if(StringUtils.isNotBlank(postStr)){
            String[] arr = postStr.split(",");
            for(String s : arr){
                userIdList.add(s);
            }
        }

        //查询菜单权限,和授权按钮
        List<PageData> authorMenuList = (List<PageData>) baseDao.findForList("RouteMenuMapper.queryMenuButton", userIdList);
        List<PageData> list = new ArrayList<>();
        for (PageData menu : authorMenuList) {
            String button = menu.getString("authorityButtonCode");
            //授权的菜单按钮
            for (PageData menu1 : authorMenuList) {
                if (menu.getString("menuCode").equals(menu1.getString("menuCode"))) {
                    String button1 = menu1.getString("authorityButtonCode");
                    if (StringUtils.isNotBlank(button)) {
                        button = String.format("%s,%s", button, button1);
                    }
                }
            }
            menu.put("setButtonCode",button);
            list.add(menu);
        }

        List<PageData> menuList = list.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(e->e.getString("menuCode")))), ArrayList::new)
        );
        //所有的菜单
        List<PageData> allMenuInfoList1 = (List<PageData>) baseDao.findForList("RouteMenuMapper.selectMenuButton");
//        for (FunctionMenuButtonDTO pageData : allMenuInfoList) {
//            pageData.setM
//            pageData.put("menu", new PageData());
//        }

        List<FunctionMenuButtonDTO> allMenuInfoList = transforToList(allMenuInfoList1);


        //所有的按钮
        List<PageData> buttonList = (List<PageData>) baseDao.findForList("RouteMenuMapper.selectButton");

        FunctionMenuButtonDTO rootMenu = null;
        List<FunctionMenuButtonDTO> childerList = new ArrayList<>();
        List<FunctionMenuButtonDTO> newMenuButtonList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(allMenuInfoList)) {
            //所有的菜单
            for (FunctionMenuButtonDTO menu : allMenuInfoList) {

                //根据写在数据库的前端信息
                menu.getMeta().setTitle(menu.getTitle());
                Integer val = 1;
                if (val.equals(menu.getNoDropdownVal())) {
                    menu.setNoDropdown(true);
                }
                if (val.equals(menu.getKeepAliveVal())) {
                    menu.getMeta().setKeepAlive(true);
                }
                if (val.equals(menu.getHiddenVal())) {
                    menu.setHidden(true);
                }
                //根据菜单的menuCode判断是否跟菜单
                Integer rootCode = 0;
                if (rootMenu == null) {
                    if (rootCode.equals(menu.getMenuCode())) {
                        rootMenu = menu;
                        continue;
                    }
                }

                //根据菜单的menuCode判断是否欢迎页
                Integer comeCode = -2;
                if (comeCode.equals(menu.getMenuCode())) {
                    childerList.add(menu);
                    continue;
                }

                //根据菜单的menuCode判断是否欢迎页
                Integer waitCode = 22;
                if (waitCode.equals(menu.getMenuCode())) {
                    childerList.add(menu);
                    continue;
                }

                //授权的菜单、组合按钮、组合按钮详细
                if (!CollectionUtils.isEmpty(menuList)) {
                    for (PageData str : menuList) {
                        String button = str.getString("setButtonCode");
                        Set<String> buttonSet = new HashSet<>();
                        //授权的菜单按钮
                        if (StringUtils.isNotBlank(button)) {
                            buttonSet = new HashSet<>(Arrays.asList(button.split(",")));
                        }

                        List<String> buttonRoutingList = new ArrayList<>();
                        //根据授权信息获取按钮路由id
                        if (!CollectionUtils.isEmpty(buttonSet)) {
                            //遍历uq_button_routing所有信息
                            for (Map<String, String> mapEntity : allData) {
                                if (mapEntity.get("authMenuCode").equals(str.getString("menuCode"))
                                        && buttonSet.contains(mapEntity.get("comButton"))) {
                                    buttonRoutingList.add(mapEntity.get("menuCode"));
                                }
                            }

//                            log.info("buttonRoutingList === " + buttonRoutingList.toString());
                        }
                        //判断菜单是否授权===只针对菜单路由 查找对应的组合按钮详细
                        if (str.getString("menuCode").equals(menu.getMenuCode().toString())) {
                            //是否首页 ishome判断是否首页
                            Integer home = 1;
                            if (home.equals(menu.getIsHome())) {
                                childerList.add(menu);
                                //有首页权限的有以下的权限
                                for (FunctionMenuButtonDTO functionMenuButton : allMenuInfoList) {
                                    //消息提醒
                                    Integer code = -1;
                                    if (code.equals(functionMenuButton.getMenuCode())) {
                                        childerList.add(functionMenuButton);
                                        continue;
                                    }
                                    //我的代办
                                    Integer myTo = 22;
                                    if (myTo.equals(functionMenuButton.getMenuCode())) {
                                        childerList.add(functionMenuButton);
                                        continue;
                                    }
                                }
                                continue;
                            }

                            Set<PageData> set = new HashSet<>();
                            if (!CollectionUtils.isEmpty(buttonSet)) {
                                //授权的按钮
                                for (String authButton : buttonSet) {
                                    //所有的菜单按钮
                                    buttonList.forEach(allButton -> {
                                        if (authButton.equals(allButton.getString("authorityButtonCode"))) {
                                            set.add(allButton);
                                        }
                                    });
                                }
                            }
                            menu.getMeta().setBtnPermissions(new ArrayList(set));

                            newMenuButtonList.add(menu);
                        }
                        //按钮路由   0，路由菜单，1，按钮路由
                        Integer pathRouting = 1;
                        if ((pathRouting.equals(menu.getPathRouting()))) {

                            if (!buttonRoutingList.isEmpty()) {
                                buttonRoutingList.forEach(buttonRout -> {
                                    if (buttonRout.equals(String.valueOf(menu.getMenuCode()))) {
                                        menu.getMeta().setKeepAlive(true);
                                        menu.setHidden(true);
                                        newMenuButtonList.add(menu);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }

        if(rootMenu!=null){
            rootMenu.setChildren(childerList);
        }

        List<FunctionMenuButtonDTO> combinationMenu = new ArrayList<>();
        List<FunctionMenuButtonDTO> resultList = new ArrayList<>();

        List<FunctionMenuButtonDTO> unique = newMenuButtonList.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FunctionMenuButtonDTO::getMenuCode))), ArrayList::new)
        );
        //递归出其他节点
        List<FunctionMenuButtonDTO> otherMenuList = recursiveTreeMenuList("0", unique);

        if (rootMenu != null && rootMenu.getMenuCode() != null) {
            combinationMenu.add(rootMenu);
        }
        if (!CollectionUtils.isEmpty(otherMenuList)) {
            combinationMenu.addAll(otherMenuList);
        }
        resultList = combinationMenu.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FunctionMenuButtonDTO::getOrderNumber))), ArrayList::new)
        );
        return resultList;
    }

    private ResponseEntity<UserInfoDTO> getUserInfo(String userTokenKey) {
        // 从请求头中取出用户tokenKey
        if (StringUtils.isBlank(userTokenKey)) {
            log.error("获取请求头用户token为空：request = [{}]", JSON.toJSONString(userTokenKey));
            return ResponseEntity.failure(ErrorCodeEnum.BLANK_TOKEN.val(), ErrorCodeEnum.BLANK_TOKEN.desc());
        }
        UserInfoDTO userInfoVo = useTokenInfo.getUserInfoByToken(userTokenKey);
        if (null == userInfoVo) {
            log.warn("获取redis用户信息异常：request = [{}]", JSON.toJSONString(userTokenKey));
            return ResponseEntity.failure(ErrorCodeEnum.GET_USER_TOKEN_EXCEPTION.val(), ErrorCodeEnum.GET_USER_TOKEN_EXCEPTION.desc());
        }
        return ResponseEntity.success(userInfoVo);
    }



    private List<FunctionMenuButtonDTO> transforToList(List<PageData> list){
        List<FunctionMenuButtonDTO> allMenuInfoList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(list)){
            for(PageData data : list){
                FunctionMenuButtonDTO button = new FunctionMenuButtonDTO();
                button.setId(Long.parseLong(data.getString("id")));
                button.setMenuCode(data.getInt("menuCode"));
                button.setTitle(data.getString("title"));
                button.setPath(data.getString("path"));
                button.setParentCode(data.getInt("parentCode"));
                button.setMenuType(data.getString("menuType"));
                button.setOrderNumber(data.getInt("orderNumber"));
                button.setLevelNumber(data.getInt("levelNumber"));
                button.setIcon(data.getString("icon"));
                button.setName(data.getString("name"));
                button.setRedirect(data.getString("redirect"));
                button.setComponent(data.getString("component"));
                button.setIsValid(data.getString("isValid"));
                button.setRelateFlow(data.getString("relateFlow"));
                button.setNoDropdownVal(data.getInt("noDropdownVal"));
                button.setKeepAliveVal(data.getInt("keepAliveVal"));
                button.setHiddenVal(data.getInt("hiddenVal"));
                button.setIsHome(data.getInt("isHome"));
                button.setPathRouting(data.getInt("pathRouting"));
                button.setMeta(new Meta());

                allMenuInfoList.add(button);
            }
        }

        return allMenuInfoList;

    }

}
