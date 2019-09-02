package com.tigerobo.distinguishtime;

import com.tigerobo.distinguishtime.model.TimeRange;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;

/**
 * @author : jiancongchen on 2019-09-02
 *
 * 一个时间范围，比如：12到16日
 **/
public class OtherTime{

    public String statement;

    public OtherTime(String statement){
        this.statement = statement;
    }

    public TimeRange dateTimeFormat4() {
        Matcher matcher = ConstantPattern.getPattern(ConstantPattern.DATE_TIME_FORMAT_4).matcher(statement);
        matcher.find();
        TimeRange timeRange = new TimeRange();
        int dayStart = ChangeTextToNum.str2int(matcher.group(1));
        int dayEnd = ChangeTextToNum.str2int(matcher.group(3));
        timeRange.setStartDay(dayStart);
        timeRange.setEndDay(dayEnd);
        timeRange.setStartDate(getNormalTime(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), dayStart));
        timeRange.setEndDate(getNormalTime(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), dayEnd));
        return timeRange;
    }

    public TimeRange dateTimeFormat5(){
        Matcher matcher = ConstantPattern.getPattern(ConstantPattern.DATE_TIME_FORMAT_5).matcher(statement);
        matcher.find();
        TimeRange timeRange = new TimeRange();
        int year = matcher.group(2).equals(null) ? LocalDate.now().getYear() : ChangeTextToNum.str2int(matcher.group(2));
        int monthStart = ChangeTextToNum.str2int(matcher.group(3));
        int monthEnd = ChangeTextToNum.str2int(matcher.group(5));
        if (monthStart > 4 || monthEnd > 4) {
            return null;
        } else {
            timeRange.setStartDate(getNormalTime(year, (monthStart - 1) * 3 + 1, 1));
            timeRange.setEndDate(getNormalTime(year, monthEnd * 3, 31));
            return timeRange;

        }
    }

    public TimeRange dateTimeFormat6(){
        Matcher matcher = ConstantPattern.getPattern(ConstantPattern.DATE_TIME_FORMAT_6).matcher(statement);
        matcher.find();
        TimeRange timeRange = new TimeRange();
        Integer year = ChangeTextToNum.str2int(matcher.group(2));
        int monthStart = 0;
        int monthEnd = 0;
        if(year<=LocalDate.now().getYear()%100) {
            year = 2000 + year;
        }else if(year>1000){
            year = year;
        }else{
            // 异常
            return timeRange;
        }
        String hitS= matcher.group(1);
        String flagQH = "";
        int numQH =0;
        Matcher m = ConstantPattern.getPattern(ConstantPattern.QUARTER).matcher(hitS);
        if(m.find()){
            flagQH = m.group();
            numQH = ChangeTextToNum.str2int(hitS.substring(1-m.start(),2-m.start()));
        }else{
            return null;
        }
        if ("Q".equals(flagQH) || "q".equals(flagQH)) {
            monthStart = (numQH - 1) * 3 + 1;
            monthEnd = numQH* 3;
            if (monthStart >12 && monthEnd >12){
                // 异常
                return timeRange;
            }
        }
        if("H".equals(flagQH) || "h".equals(flagQH)){
            monthStart = (numQH - 1) * 6 + 1;
            monthEnd = numQH * 6;
            if (monthStart >12 && monthEnd >12){
                // 异常
                return timeRange;
            }
        }
        timeRange.setStartDate(getNormalTime(year, monthStart, 1));
        timeRange.setEndDate(getNormalTime(year, monthEnd, 31));
        return timeRange;
    }

    public TimeRange dateTimeFormate7(){
        Matcher matcher = ConstantPattern.getPattern(ConstantPattern.DATE_TIME_FORMAT_7).matcher(statement);
        matcher.find();
        TimeRange timeRange = new TimeRange();
        Integer year = ChangeTextToNum.str2int(matcher.group(1));
        int monthStart = 0;
        int monthEnd = 0;
        if(year<=LocalDate.now().getYear()%100) {
            year = 2000 + year;
        }else if(year>1000){
            year = year;
        }
        else{
            // 异常
            return timeRange;
        }
        String hitS= matcher.group(2);
        String flagQH = "";
        Integer numQH =0;
        Matcher m = ConstantPattern.getPattern(ConstantPattern.QUARTER).matcher(hitS);
        if(m.find()){
            flagQH = m.group();
            numQH = ChangeTextToNum.str2int(hitS.substring(1-m.start(),2-m.start()));
        }
        else{
            return timeRange;
        }

        if ("Q".equals(flagQH) || "q".equals(flagQH)) {
            monthStart = (numQH - 1) * 3 + 1;
            monthEnd = numQH  * 3;
            if (monthStart >12 && monthEnd >12){
                // 异常
                return timeRange;
            }
        }
        if("H".equals(flagQH) || "h".equals(flagQH)){
            monthStart = (numQH - 1) * 6 + 1;
            monthEnd = numQH * 6;
            if (monthStart >12 && monthEnd >12){
                // 异常
                return timeRange;
            }

        }
        timeRange.setStartDate(getNormalTime(year, monthStart, 1));
        timeRange.setEndDate(getNormalTime(year, monthEnd, 31));
        return timeRange;
    }

    /**
     * 根据年月日生成时间
     * @param year
     * @param month
     * @param day
     * @return
     */
    public  Date getNormalTime(int year, int month, int day){
        // 先生成标准日期格式：
        Calendar startDate = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        // 某年某月的最后一天
        if (day > cal.getActualMaximum(Calendar.DATE)) {
            day = cal.getActualMaximum(Calendar.DATE);
        }
        startDate.set(year, month - 1, day);
        return startDate.getTime();
    }

    /**
     * 返回0，则不匹配
     * @param statement
     * @return
     */
    public int isMatch(String statement){
        if(ConstantPattern.isMatch(statement,ConstantPattern.DATE_TIME_FORMAT_4)){
            return 4;
        }
        if(ConstantPattern.isMatch(statement,ConstantPattern.DATE_TIME_FORMAT_5)){
            return 5;
        }
        if(ConstantPattern.isMatch(statement,ConstantPattern.DATE_TIME_FORMAT_6)){
            return 6;
        }
        if(ConstantPattern.isMatch(statement,ConstantPattern.DATE_TIME_FORMAT_7)){
            return 7;
        }
        return 0;
    }

    public TimeRange getDateFormat(int match){
        TimeRange timeRange = new TimeRange();
        switch (match){
            case 4:timeRange = dateTimeFormat4();break;
            case 5:timeRange = dateTimeFormat5();break;
            case 6:timeRange = dateTimeFormat6();break;
            case 7:timeRange = dateTimeFormate7();break;
            default:break;
        }
        return timeRange;
    }
}
