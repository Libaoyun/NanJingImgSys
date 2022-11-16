package com.common.util;

/**
 * @author rdexpense
 * 工作流是否启用枚举类
 * @date 2020/4/14 23:02
 */
public enum FlowIsAbleEnum {
    ENABLE(0,"否"),
    DISABLE(1,"是"),

    ;

    private Integer val;

    private String desc;

    FlowIsAbleEnum(Integer val, String desc)
    {
        this.val = val;
        this.desc = desc;
    }

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static FlowIsAbleEnum getFlowIsAbleEnum(Integer val){
        if(val == null){
            return null;
        }
        for (FlowIsAbleEnum isAbleEnum : FlowIsAbleEnum.values()){
            if(val.equals(isAbleEnum.val)){
                return isAbleEnum;
            }
        }
        return null;
    }

    public static String getFlowIsAbleDesc(Integer val){
        if(val == null){
            return null;
        }
        for (FlowIsAbleEnum isAbleEnum : FlowIsAbleEnum.values()){
            if(val.equals(isAbleEnum.val)){
                return isAbleEnum.desc;
            }
        }
        return null;
    }
}
