package com.rdexpense.manager.service.system;


import com.common.entity.PageData;
import com.rdexpense.manager.dto.system.menu.FunctionMenuButtonDTO;

import java.util.List;


public interface FunctionMenuService {

    List<PageData> queryMenuTree(String relateFlow);


    List<FunctionMenuButtonDTO> queryRoutingMenuTree(String token);

}
