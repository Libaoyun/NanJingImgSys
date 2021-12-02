package com.rdexpense.manager.service.system;

import com.common.entity.PageData;

import java.util.List;

/**
 * @author luxiangbao
 * @date 2020/8/19 11:47
 * @description
 */
public interface RuleConfigService {

    void saveRuleConfig(PageData pd);

    List<PageData> getRuleConfig(PageData pd);

}
