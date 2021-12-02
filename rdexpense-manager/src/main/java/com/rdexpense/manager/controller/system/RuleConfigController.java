package com.rdexpense.manager.controller.system;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.PropertyUtil;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.system.ruleConfig.RuleConfigItemDto;
import com.rdexpense.manager.dto.system.ruleConfig.RuleConfigSaveDto;
import com.rdexpense.manager.service.system.RuleConfigService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author luxiangbao
 * @date 2020/8/19 11:44
 * @description 规则配置
 */
@RestController
@RequestMapping("/rule")
@Api(value = "规则配置", tags = "规则配置")
public class RuleConfigController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(RuleConfigController.class);

    @Autowired
    private LogUtil logUtil;

    @Autowired
    private RuleConfigService ruleConfigService;


    @ApiOperation(value = "保存/编辑")
    @PostMapping(value = "/saveRuleConfig", consumes = "application/json")
    public ResponseEntity saveRuleConfig(RuleConfigSaveDto ruleConfigSaveDto) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        ResponseEntity result = null;
        try {
            //校验取出参数
            String ruleList = pd.getString("ruleList");
            if (StringUtils.isBlank(ruleList)) {
                throw new MyException("规则项不能为空");
            }
            ruleConfigService.saveRuleConfig(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("编辑规则配置失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "规则配置", pd);
        }
    }


    @ApiOperation(value = "查询")
    @GetMapping(value = "/getRuleConfig")
    public ResponseEntity<List<RuleConfigItemDto>> getRuleConfig() {
        PageData pd = this.getParams();
        try {
            List<PageData> list = ruleConfigService.getRuleConfig(pd);
            return ResponseEntity.success(PropertyUtil.covertListModel(list, RuleConfigItemDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());

        } catch (Exception e) {
            logger.error("规则配置查询失败,request=[{}]", pd);
            throw new MyException(ConstantMsgUtil.ERR_QUERY_FAIL.desc(), e);
        }
    }


}
