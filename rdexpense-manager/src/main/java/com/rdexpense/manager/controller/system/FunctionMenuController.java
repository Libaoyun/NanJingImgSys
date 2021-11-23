package com.rdexpense.manager.controller.system;

import com.common.entity.PageData;
import com.common.util.ConstantMsgUtil;
import com.common.util.PropertyUtil;
import com.rdexpense.manager.dto.system.menu.FunctionMenuButtonDTO;
import com.rdexpense.manager.dto.system.menu.RouteMenu;
import com.rdexpense.manager.service.system.FunctionMenuService;
import com.rdexpense.manager.controller.base.BaseController;
import com.common.entity.ResponseEntity;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menu")
@Api(value = "授权功能菜单", tags = {"授权功能菜单"})
public class FunctionMenuController extends BaseController {


    @Autowired
    private FunctionMenuService functionMenuService;


    @GetMapping(value = "/queryFlowMenuTree")
    @ApiOperation(value = "查询流程配置的菜单", notes = "查询流程配置的菜单")
    public ResponseEntity<List<RouteMenu>> queryFlowMenuTree() {
        try {
            List<PageData> result = functionMenuService.queryMenuTree("1");
            //这里不是根目录
            return ResponseEntity.success(PropertyUtil.covertListModel(result,RouteMenu.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }

    }





    @GetMapping(value = "/queryRoutingMenuTree")
    @ApiOperation(value = "查询菜单", notes = "查询菜单")
    public ResponseEntity<List<FunctionMenuButtonDTO>> queryRoutingMenuTree() {

        try {
            List<FunctionMenuButtonDTO> result = functionMenuService.queryRoutingMenuTree(this.getUserToken("token"));
            return ResponseEntity.success(PropertyUtil.covertListModel(result, FunctionMenuButtonDTO.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());

        }
    }



}
