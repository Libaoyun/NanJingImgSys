package com.common.util;




/**
 * 功能描述: 系统常用量值域
 * @Author: rdexpense
 * @Date: 2020/4/24 11:25
 */
public class ConstantValUtil {

    //附件上传目录
    public static final String BUCKET_PRIVATE = "private-camera";

    // 部位示意图路径
    public static final String BODY_PART_IMAGE = "body-part-img";

    //

    // 审批状态：10171001-未提交, 10171002-审批中, 10171003-已废除, 10171004-被打回 10171005-已通过
    public static final String[] APPROVAL_STATUS = {"DICT10171001", "DICT10171002", "DICT10171003", "DICT10171004", "DICT10171005"};


    // 审批处理结果：DICT10181001-发起, DICT10181002-同意, DICT10181003-回退上一个节点, DICT10181004-回退到发起人
    public static final String[] APPROVAL_RESULT = {"DICT10181001", "DICT10181002", "DICT10181003", "DICT10181004"};

    // 审批处理结果：DICT10181001-发起, DICT10181002-同意, DICT10181003-回退上一个节点, DICT10181004-回退到发起人
    public static final String[] APPROVAL_RESULT_NAME = {"发起", "同意", "回退上一个节点", "回退到发起人"};

}
