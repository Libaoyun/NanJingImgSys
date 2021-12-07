package com.rdexpense.manager.enums;

/**
 * @author dengteng
 * @title: ApprovalstatusEnum
 * @projectName rdexpense-back
 * @description: TODO
 * @date 2021/11/27
 */
public enum ApprovalstatusEnum {


    NOT_SUBMITTED("未提交","DICT10171001"),
    UNDER_APPROVAL("审批中","DICT10171002"),
    REPEALED("已废除","DICT10171003"),
    CALLED_BACK("被打回","DICT10171004"),
    PASSED("已通过","DICT10171005");


    private String desc;//文字描述
    private String code; //对应的代码

    /**
     * 私有构造,防止被外部调用
     * @param desc
     */
    private ApprovalstatusEnum(String desc,String code){
        this.desc=desc;
        this.code=code;
    }
    /**
     * 定义方法,返回描述
     */
    public String getDesc(){
        return desc;
    }

    /**
     * 定义方法,返回代码
     * @return
     */
    public String getCode(){
        return code;
    }

}
