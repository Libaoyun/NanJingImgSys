package com.rdexpense.manager.service.system;


import com.common.entity.PageData;

import java.util.List;


public interface FunctionMenuService {

    List<PageData> queryMenuTree(String relateFlow);


    List<PageData> queryRoutingMenuTree(String token);

}
