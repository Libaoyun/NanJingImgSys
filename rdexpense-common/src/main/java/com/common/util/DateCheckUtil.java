package com.common.util;


import com.common.base.exception.MyException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @auther rdexpense
 * @date 2020/10/21 10:28
 * @describe 日期判断工具类
 */
public class DateCheckUtil {

    public final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


    //判断选择的日期是否是本周
    public static boolean isThisWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(new Date(time));
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        if (paramWeek == currentWeek) {
            return true;
        }
        return false;
    }

    //判断选择的日期是否是今天
    public static boolean isToday(long time) {
        return isThisTime(time, "yyyy-MM-dd");
    }

    //判断选择的日期是否是本月
    public static boolean isThisMonth(long time) {
        return isThisTime(time, "yyyy-MM");
    }

    //判断选择的日期是否是上月
    public static boolean isLastMonth(String dataTime) {
        Calendar cal = Calendar.getInstance();
        cal.add(cal.MONTH, -1);
        Date currentTime=cal.getTime();//当前时间的上个月时间
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");
        String curTime=sdf.format(currentTime);

        if(dataTime.startsWith(curTime)){
            return true;
        }else{
            return false;
        }
    }

    //获取上个月的第一天的时间
    public static String getLastMonthFirstDay(){
        Calendar cal=Calendar.getInstance();//获取当前日期
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        String firstDay = format.format(cal.getTime()) + " 00:00:00";

        return firstDay;


    }



    //当前时间还未到某个时间，计算两个时间相差，几年几月几天几时几分几秒
    public static String getUnreachedTime(Date currentTime,Date firstTime){
        long diff = firstTime.getTime() - currentTime.getTime();//这样得到的差值是微秒级别
        Calendar  currentTimes =dataToCalendar(currentTime);//当前系统时间转Calendar类型
        Calendar  firstTimes =dataToCalendar(firstTime);//查询的数据时间转Calendar类型
        int year = firstTimes.get(Calendar.YEAR) - currentTimes.get(Calendar.YEAR);//获取年
        int month = firstTimes.get(Calendar.MONTH) - currentTimes.get(Calendar.MONTH);
        int day = firstTimes.get(Calendar.DAY_OF_MONTH) - currentTimes.get(Calendar.DAY_OF_MONTH);
        if (day < 0) {
            month -= 1;
            currentTimes.add(Calendar.MONTH, -1);
            day = day + currentTimes.getActualMaximum(Calendar.DAY_OF_MONTH);//获取日
        }
        if (month < 0) {
            month = (month + 12) % 12;//获取月
            year--;
        }
        long days = diff / (1000 * 60 * 60 * 24);
        long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60); //获取时
        long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);  //获取分钟
        long s=(diff/1000-days*24*60*60-hours*60*60-minutes*60);//获取秒
        String CountTime=year+"年"+month+"月"+day+"天"+hours+"小时"+minutes+"分"+s+"秒";
        return CountTime;
    }



    //当前时间超过某个时间，计算两个时间相差，几年几月几天几时几分几秒
    public static String getReachedTime(Date currentTime,Date firstTime){
        long diff = currentTime.getTime() - firstTime.getTime();//这样得到的差值是微秒级别
        Calendar  currentTimes =dataToCalendar(currentTime);//当前系统时间转Calendar类型
        Calendar  firstTimes =dataToCalendar(firstTime);//查询的数据时间转Calendar类型
        int year = currentTimes.get(Calendar.YEAR) - firstTimes.get(Calendar.YEAR);//获取年
        int month = currentTimes.get(Calendar.MONTH) - firstTimes.get(Calendar.MONTH);
        int day = currentTimes.get(Calendar.DAY_OF_MONTH) - firstTimes.get(Calendar.DAY_OF_MONTH);
        if (day < 0) {
            month -= 1;
            currentTimes.add(Calendar.MONTH, -1);
            day = day + currentTimes.getActualMaximum(Calendar.DAY_OF_MONTH);//获取日
        }
        if (month < 0) {
            month = (month + 12) % 12;//获取月
            year--;
        }
        long days = diff / (1000 * 60 * 60 * 24);
        long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60); //获取时
        long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);  //获取分钟
        long s=(diff/1000-days*24*60*60-hours*60*60-minutes*60);//获取秒
        String CountTime=year+"年"+month+"月"+day+"天"+hours+"小时"+minutes+"分"+s+"秒";
        return CountTime;
    }


    //当前时间还未到某个时间，计算两个时间相差，几天几时几分几秒 flag = flase
    //当前时间超过某个时间，计算两个时间相差，几天几时几分几秒 flag = true
    public static String getTimeDiff(Date date,Boolean flag) {

        StringBuilder sb = new StringBuilder();
        Date now = new Date();
        // 这样得到的差值是微秒级别
        long diff = 0;
        if(flag == true){
            diff =  now.getTime() - date.getTime();
        }else {
            diff =  date.getTime() - now.getTime();
        }


        // 只能精确到日 无法具细到年 月 不能确定一个月具体多少天 不能确定一年具体多少天
        // 获取日
        long day = diff / (1000 * 60 * 60 * 24);
        diff = diff % (1000 * 60 * 60 * 24);
        if (day >= 0) {
            sb.append(day).append("天 ");
        }
        // 获取时
        long hour = diff / (1000 * 60 * 60);
        diff = diff % (1000 * 60 * 60);
        if (hour >= 0) {
            sb.append(String.format("%2d", hour).replace(" ", "0")).append(":");
        }
        // 获取分
        long min = diff / (1000 * 60);
        diff = diff % (1000 * 60);
        if (min >= 0) {
            sb.append(String.format("%2d", min).replace(" ", "0")).append(":");
        }
        // 获取秒
        long sec = diff / 1000;
        if (sec >= 0) {
            sb.append(String.format("%2d", sec).replace(" ", "0"));
        }

        return sb.toString();

    }





    //Date类型转Calendar类型
    public static Calendar dataToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }





    private static boolean isThisTime(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        if (param.equals(now)) {
            return true;
        }
        return false;
    }


    /**
     * 判断两个日期的前后顺序
     * @param date1
     * @param date2
     * @return
     */
    public static String checkOrder(String date1,String date2) {

        String result = "";
        try {
            Date formatDate1 = format.parse(date1);
            Date formatDate2 = format.parse(date2);

            //比较两个日期
            int index = formatDate2.compareTo(formatDate1);

            //如果日期相等返回0
            if (index == 0) {
                result = "equal";
            } else if (index < 0) {
                //小于0，参数date1就是在date2之后,date1大于date2
                result = "than";
            } else {
                //大于0，参数date1就是在date2之前,date1小于date2
                result = "less";
            }

        } catch (ParseException e) {
            throw new MyException("时间格式错误");

        }

        return result;

    }

}
