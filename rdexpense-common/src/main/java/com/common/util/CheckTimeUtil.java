package com.common.util;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

import static com.common.util.ConstantMsgUtil.WAN_DATE_FORMAT;

/**
 * @Auther: maomoming
 * @Date: 2020/10/9 16:05
 * @Description:   开始和结束日期的校验并加上时分秒
 */
public class CheckTimeUtil {
    private static String REGEX = "[^(0-9)]";

    public static void checkAndFormatDate(PageData pd) {

        String startDate = pd.getString("startTime");
        String endDate = pd.getString("endTime");
        try {
            if (StringUtils.isNotEmpty(startDate)) {
                CheckParameter.checkDate(pd, startDate, "开始日期", "startTime");
                StringBuilder stringBuilderStart = new StringBuilder();
                stringBuilderStart.append(startDate);
                stringBuilderStart.append(" 00:00:00");
                String resultStartTime = stringBuilderStart.toString();
                pd.put("startTime", resultStartTime);
            }
            if (StringUtils.isNotBlank(endDate)) {
                CheckParameter.checkDate(pd, endDate, "结束日期", "endTime");
                StringBuilder stringBuilderEnd = new StringBuilder();
                stringBuilderEnd.append(endDate);
                stringBuilderEnd.append(" 23:59:59");
                String resultEndTime = stringBuilderEnd.toString();
                pd.put("endTime", resultEndTime);
            }
        } catch (Exception e) {
            throw new MyException(WAN_DATE_FORMAT.desc(), e);
        }

        if(StringUtils.isNotEmpty(pd.getString("startTime"))&&StringUtils.isNotEmpty(pd.getString("endTime"))){
            String startDate1 = Pattern.compile(REGEX).matcher(pd.getString("startTime")).replaceAll("").trim();
            String endDate1 = Pattern.compile(REGEX).matcher(pd.getString("endTime")).replaceAll("").trim();
            if(Long.valueOf(startDate1)>Long.valueOf(endDate1)){
                throw  new MyException("开始时间不能大于结束时间");
            }
        }

    }
}