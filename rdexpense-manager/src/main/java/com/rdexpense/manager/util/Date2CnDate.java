package com.rdexpense.manager.util;

import com.common.base.exception.MyException;
import com.common.util.ConstantMsgUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Date2CnDate {
    public static String convertDate2CnDate(String date) throws ParseException {
        String cnDate = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt = sdf.parse(date);
            String strs[] = date.split("-");

            // 年
            for (int i = 0; i < strs[0].length(); i++) {
                cnDate += formatDigit(strs[0].charAt(i));
            }

            // 月
            char c1 = strs[1].charAt(0);
            char c2 = strs[1].charAt(1);
            String cnMonth  = c1 == '1' ? (c2 == '0' ?  "十" : "十" + formatDigit(c2)) : String.valueOf(formatDigit(c2));

            // 日
            char d1 = strs[2].charAt(0);
            char d2 = strs[2].charAt(1);
            String cnDay = "";
            if (d1 == '0') {//单位数天
                cnDay = String.valueOf(formatDigit(d2));
            } else if (d1 != '1' && d2 == '0') {//几十
                cnDay = String.valueOf(formatDigit(d1)) + "十";
            } else if (d1 != '1' && d2 != '0') {//几十几
                cnDay = formatDigit(d1) + "十" + formatDigit(d2);
            } else if (d1 == '1' && d2 != '0') {//十几
                cnDay = "十" + formatDigit(d2);
            } else {//10
                cnDay = "十";
            }

            cnDate += "年" + cnMonth + "月" + cnDay + "日";

            return cnDate;

        } catch (Exception e) {
            throw new MyException(ConstantMsgUtil.ERR_EXPORT_FAIL.desc(), e);
        }
    }

    public static char formatDigit(char sign) {
        if (sign == '0')
            sign = '〇';
        if (sign == '1')
            sign = '一';
        if (sign == '2')
            sign = '二';
        if (sign == '3')
            sign = '三';
        if (sign == '4')
            sign = '四';
        if (sign == '5')
            sign = '五';
        if (sign == '6')
            sign = '六';
        if (sign == '7')
            sign = '七';
        if (sign == '8')
            sign = '八';
        if (sign == '9')
            sign = '九';
        return sign;
    }

    public static long CnDigit2Digit(char sign) {
        long res = -1;
        if (sign == '〇' || sign == '零')
            res = 0;
        if (sign == '一' || sign == '壹')
            res = 1;
        if (sign == '二' || sign == '贰')
            res = 2;
        if (sign == '三' || sign == '叁')
            res = 3;
        if (sign == '四' || sign == '肆')
            res = 4;
        if (sign == '五' || sign == '伍')
            res = 5;
        if (sign == '六' || sign == '陆')
            res = 6;
        if (sign == '七' || sign == '柒')
            res = 7;
        if (sign == '八' || sign == '捌')
            res = 8;
        if (sign == '九' || sign == '玖')
            res = 9;
        if (sign == '十' || sign == '拾')
            res = 10;
        if (sign == '百' || sign == '佰')
            res = 100;
        if (sign == '千' || sign == '仟')
            res = 1000;
        if (sign == '万')
            res = 10000;
        if (sign == '亿')
            res = 100000000;
        return res;
    }
}

