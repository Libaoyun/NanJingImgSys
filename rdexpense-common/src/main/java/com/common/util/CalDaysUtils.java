package com.common.util;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @auther rdexpense
 * @date 2020/7/23 16:58
 * @describe 计算两个时间相差几小时，获取当前日期或指定日期的前N天的日期集合等
 */
public class CalDaysUtils {


    static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static final DecimalFormat df = new DecimalFormat("0.0");

    /**
     * 判断两个时间哪个在前，哪个在后
     * @return false time1在time2前
     * @return true time1在time2后
     */
    public static  Boolean periodOnDate(String time1,String time2){
        Date date1 = null;
        Date date2 = null;
        Boolean flag;
        try {
            date1 = simpleDateFormat.parse(time1);
            date2 = simpleDateFormat.parse(time2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (date1.after(date2)) {
            flag = true;
        }else{
            flag = false;
        }
        return flag;
    }


    /**
     * 计算两个时间相差几小时
     * @param startTime
     * @param endTime
     * @return
     */
    public static double calTimeRange(String startTime,String endTime){
        Date startDate = null;
        Date endDate = null;
        double result = 0.0;
        try {
            startDate = simpleDateFormat.parse(startTime);
            endDate = simpleDateFormat.parse(endTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(startDate != null && endDate != null){
            long diff = endDate.getTime() - startDate.getTime();

            long nd = 1000 * 24 * 60 * 60;
            long nh = 1000 * 60 * 60;
            long nm = 1000 * 60;

            // 计算差多少小时
            long hour = diff % nd / nh;
            // 计算差多少分钟
            long min = diff % nd % nh / nm;

            //分钟换算成小时
            result = hour + calHalfValue(Double.parseDouble(df.format(min/(double)60)));


        }

        return result;
    }

    /**
     * 计算是否为半天 后者半小时
     * @param calValue
     * @return
     */
    public static double calHalfValue(double calValue){
        if(calValue < 0.5){
            calValue = 0.5;
        }else if(calValue > 0.5){
            calValue = 1;
        }else{
            calValue = 0.5;
        }

        return calValue;
    }


    /**
     * 获取当前日期或指定日期的前N天的日期集合
     * @param startTime
     * @param endTime
     * @param nday
     * @return
     */
    public static List<String> getNDaysList(String startTime, String endTime, int nday) {
        int ndaycurrent = nday - 1;
        // 返回的日期集合
        List<String> days = new ArrayList<String>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -ndaycurrent);
            Date start = calendar.getTime();
            Date end = new Date();
            if (StringUtils.isNotBlank(startTime) && StringUtils.isBlank(endTime)) {
                //如果用户只选择了startTime,endTime为null,startTime + 10的日期
                start = dateFormat.parse(startTime);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(start);
                calendar1.add(Calendar.DATE, ndaycurrent);
                end = calendar1.getTime();
            } else if (StringUtils.isBlank(startTime) && StringUtils.isNotBlank(endTime)) {
                //如果用户只选择了endTime,startTime为null,endTime - 10的日期
                end = dateFormat.parse(endTime);
                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(end);
                calendar2.add(Calendar.DATE, -ndaycurrent);
                start = calendar2.getTime();
            } else if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
                //如果用户选择了startTime和endTime，判断endTime - startTime两个日期是否超过了ndaycurrent，超过返回最近nday天记录
                Date start1 = dateFormat.parse(startTime);
                Date end1 = dateFormat.parse(endTime);
                int a = (int) ((end1.getTime() - start1.getTime()) / (1000 * 3600 * 24));
                if (a <= ndaycurrent) {
                    //如果小于等于n天
                    start = dateFormat.parse(startTime);
                    end = dateFormat.parse(endTime);
                }
            }
            //如果超过了ndaycurrent天,就是默认的start和end
            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)

            while (tempStart.before(tempEnd)) {
                days.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }


    /**
     * 判断两天是否是连续的两天
     * @param nowTime
     * @param lastTime
     */
    public static int checkDays(String nowTime,String lastTime){
        SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd");
        int count = 1;
        try {
            Date nowDate = sdf.parse(nowTime);
            Date lastDate = sdf.parse(lastTime);

            count = (Math.abs((int) nowDate.getTime() - (int) lastDate.getTime()) / 3600000) / 24;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return count;
    }

}
