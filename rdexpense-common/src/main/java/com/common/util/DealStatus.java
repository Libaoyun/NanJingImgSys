package com.common.util;

import lombok.Getter;

/**
 * 操作状态码
 */
@Getter
public enum DealStatus {
    ADD(1, "新增", "DICT10111003"),
    UPDATE(2, "编辑", "DICT10111004"),
    DELETE(3, "删除", "DICT10111005"),
    LOGININ(4, "登陆系统", "DICT10111001"),
    LOGINOUT(5, "退出系统", "DICT10111002"),
    EXPORT(6, "导出", "DICT10111006"),
    UPLOAD(7, "上传", "DICT10111007");



    private int code;
    private String name;
    private String type;

    DealStatus(int code, String name, String type) {
        this.code = code;
        this.name = name;
        this.type = type;
    }

    public static String getName(int code) {
        DealStatus[] values = values();
        for (DealStatus value : values) {
            if (value.code == code) {
                return value.name;
            }
        }
        return null;
    }


    public static String getType(int code) {
        DealStatus[] values = values();
        for (DealStatus value : values) {
            if (value.code == code) {
                return value.type;
            }
        }
        return null;
    }


}
