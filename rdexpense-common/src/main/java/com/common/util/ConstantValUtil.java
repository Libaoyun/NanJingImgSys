package com.common.util;




/**
 * 功能描述: 系统常用量值域
 * @Author: rdexpense
 * @Date: 2020/4/24 11:25
 */
public class ConstantValUtil {

    //附件上传目录
    public static final String BUCKET_PRIVATE = "private1";

    // 审批状态：10070001-未提交, 10070002-审批中, 10070003-已废除, 10070004-被打回 10070005-已通过
    public static final String[] APPROVAL_STATUS = {"DICT10070001", "DICT10070002", "DICT10070003", "DICT10070004", "DICT10070005"};


    // 审批处理结果：DICT10181001-发起, DICT10181002-同意, DICT10181003-回退上一个节点, DICT10181004-回退到发起人
    public static final String[] APPROVAL_RESULT = {"DICT10181001", "DICT10181002", "DICT10181003", "DICT10181004"};

    // 审批处理结果：DICT10181001-发起, DICT10181002-同意, DICT10181003-回退上一个节点, DICT10181004-回退到发起人
    public static final String[] APPROVAL_RESULT_NAME = {"发起", "同意", "回退上一个节点", "回退到发起人"};

}
