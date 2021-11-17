package com.rdexpense.manager.controller.system;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.PropertyUtil;
import com.rdexpense.manager.service.system.MenuService;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.system.menu.AddMenuNodeDto;
import com.rdexpense.manager.dto.system.menu.MenuNodeDto;
import com.rdexpense.manager.dto.system.menu.MenuTreeDto;
import com.rdexpense.manager.util.LogUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.common.util.ConstantMsgUtil.*;

/**
 * @author rdexpense
 * @date 2020/5/26 14:52
 * @describe 菜单管理
 */
@Api(value = "菜单管理接口", tags = "菜单管理")
@RestController
@RequestMapping(value = "/menuMangement")
public class MenuController extends BaseController {


    @Autowired
    private MenuService menuService;

    @Autowired
    private LogUtil logUtil;

    @ApiOperation(value="显示菜单树形图")
    @PostMapping(value = "/queryTree", consumes = "application/json")
    @ApiImplicitParam(name = "menuCode", value = "菜单编码", required = true, dataType = "string")
    ResponseEntity<List<MenuTreeDto>> queryTree(){
        PageData pd = this.getParams();
        CheckParameter.checkPositiveInt(pd.getString("menuCode"), "菜单编码");
        try{

            List<PageData> dataList = menuService.queryTree(pd);
            ResponseEntity result = ResponseEntity.success(PropertyUtil.covertListModel(dataList, MenuTreeDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            throw new MyException(ERR_QUERY_FAIL.desc(), e);
        }


    }

    @ApiOperation(value = "更新菜单树形图")
    @PostMapping(value = "/updateTree", consumes = "application/json")
    ResponseEntity<List<MenuTreeDto>> updateTree(MenuTreeDto menuTreeDto) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        ResponseEntity result = null;
        try {
            List<PageData> dataList = menuService.updateTree(pd);
            result = ResponseEntity.success(PropertyUtil.covertListModel(dataList, MenuTreeDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            throw new MyException(ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "菜单树形图", pd);
        }
    }

    @ApiOperation(value = "查询节点")
    @PostMapping(value = "/queryNode", consumes = "application/json")
    @ApiImplicitParam(name = "menuCode", value = "菜单编码", required = true, dataType = "string")
    ResponseEntity<MenuTreeDto> queryNode() {
        PageData pd = this.getParams();
        CheckParameter.checkPositiveInt(pd.getString("menuCode"), "菜单编码");
        try {
            PageData resultData = menuService.queryNode(pd);
            ResponseEntity result = ResponseEntity.success(PropertyUtil.pushData(resultData, MenuTreeDto.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc()));
            return result;
        } catch (Exception e) {
            throw new MyException(ERR_QUERY_FAIL.desc(), e);
        }

    }


    @ApiOperation(value = "新增节点")
    @PostMapping(value = "/saveNode", consumes = "application/json")
    public ResponseEntity saveNode(AddMenuNodeDto addMenuNodeDto){
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        ResponseEntity result = null;
        checkSave(pd);
        try {
            menuService.saveNode(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "菜单节点", pd);
        }
    }

    @ApiOperation(value = "更新节点")
    @PostMapping(value = "/updateNode", consumes = "application/json")
    public ResponseEntity updateNode(AddMenuNodeDto addMenuNodeDto){
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        ResponseEntity result = null;
        checkSave(pd);
        try {
            menuService.updateNode(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            throw new MyException(ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "菜单节点", pd);
        }
    }


    @ApiOperation(value = "删除节点")
    @PostMapping(value = "/deleteNode", consumes = "application/json")
    public ResponseEntity deleteNode(MenuNodeDto menuNodeDto) {
        PageData pd = this.getParams();
        String menuCode = pd.getString("menuCode");
        CheckParameter.checkDefaultParams(pd);
        ResponseEntity result = null;

        if(menuCode.equals("-10") || menuCode.equals("3000")){
            throw new MyException("该路由不允许删除");
        }
        try {
            menuService.deleteNode(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            throw new MyException(ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "菜单节点", pd);
        }
    }



    private void checkSave(PageData pd) {
        String menuCode = pd.getString("menuCode");
        CheckParameter.stringLengthAndEmpty(menuCode, "菜单编码", 8);

        String title = pd.getString("title");
        CheckParameter.stringLengthAndEmpty(title, "菜单名称", 50);

        String name = pd.getString("name");
        CheckParameter.stringLengthAndEmpty(name, "路由名称", 64);

        String path = pd.getString("path");
        CheckParameter.stringLengthAndEmpty(path, "路由路径", 256);

        String component = pd.getString("component");
        CheckParameter.stringLengthAndEmpty(component, "路由组件", 256);

//        String icon = pd.getString("icon");
//        CheckParameter.stringLengthAndEmpty(icon, "图标", 128);

        String hidden = pd.getString("hidden");
        CheckParameter.stringLengthAndEmpty(hidden, "是否隐藏", 1);

        String keepAlive = pd.getString("keepAlive");
        CheckParameter.stringLengthAndEmpty(keepAlive, "是否缓存", 1);

        String noDropDown = pd.getString("noDropDown");
        CheckParameter.stringLengthAndEmpty(noDropDown, "子级菜单", 1);

        String pathRouting = pd.getString("pathRouting");
        CheckParameter.stringLengthAndEmpty(pathRouting, "菜单类型", 1);

//        String comButton = pd.getString("comButton");
//        CheckParameter.stringLengthAndEmpty(comButton, "组合按钮", 64);

        String parentCode = pd.getString("parentCode");
        CheckParameter.stringLengthAndEmpty(parentCode, "父级编码", 8);

        String isApprove = pd.getString("isApprove");
        CheckParameter.stringLengthAndEmpty(isApprove, "是否为审批页面", 1);

        String relateFlow = pd.getString("relateFlow");
        CheckParameter.stringLengthAndEmpty(relateFlow, "是否关联工作流", 1);

    }

}
