package com.common.util;

/**
 * @author rdexpense
 * 男女性别枚举类
 * @date 2020/4/2 14:02
 */
public enum GenderEnum {
    MALE(1,"男"),
    FEMALE(2,"女");

    ;

    private Integer val;

    private String desc;

    GenderEnum(Integer val, String desc)
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

    public static GenderEnum getGenderEnum(Integer val){
        if(val == null){
            return null;
        }
        for (GenderEnum genderEnum : GenderEnum.values()){
            if(val.equals(genderEnum.val)){
                return genderEnum;
            }
        }
        return null;
    }

    public static String getGenderDesc(Integer val){
        if(val == null){
            return null;
        }
        for (GenderEnum genderEnum : GenderEnum.values()){
            if(val.equals(genderEnum.val)){
                return genderEnum.desc;
            }
        }
        return null;
    }
}
