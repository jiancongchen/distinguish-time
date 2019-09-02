package com.tigerobo.distinguishtime.model;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

/**
 * @author : jiancongchen on 2019-08-31
 **/
@Data
public class TimeRange {

    private Date startDate;

    private Date endDate;

    private int startYear;

    private int endYear;

    private int startMonth;

    private int endMonth;

    private int startDay;

    private int endDay;

    /**
     * 标志在语句中是否真正解析得到相关数据
     */
    private boolean yearFlag;

    private boolean monthFlag;

    private boolean dayFlag;

    public TimeRange(){
        LocalDate localDate = LocalDate.now();
        this.startYear = localDate.getYear();
        this.endYear = localDate.getYear();
        this.startMonth = localDate.getMonthValue();
        this.endMonth = localDate.getMonthValue();
        this.startDay = localDate.getDayOfMonth();
        this.endDay = localDate.getDayOfMonth();
        this.yearFlag = false;
        this.monthFlag = false;
        this.dayFlag = false;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
        this.yearFlag = true;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
        this.yearFlag = true;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
        this.monthFlag = true;
    }

    public void setEndMonth(int endMonth) {
        this.endMonth = endMonth;
        this.monthFlag = true;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
        this.dayFlag = true;
    }

    public void setEndDay(int endDay) {
        this.endDay = endDay;
        this.dayFlag = true;
    }

    @Override
    public String toString() {
        return "TimeRange{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", startYear=" + startYear +
                ", endYear=" + endYear +
                ", startMonth=" + startMonth +
                ", endMonth=" + endMonth +
                ", startDay=" + startDay +
                ", endDay=" + endDay +
                '}';
    }

    /**
     * 把解析后的日期转换成Date类型
     * @param timeRange
     * @return
     */
    public static TimeRange getFormatDate(TimeRange timeRange){
        Calendar startDate = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, timeRange.getStartYear());
        cal.set(Calendar.MONTH, timeRange.getStartMonth() - 1);
        // 某年某月的最后一天
        if (timeRange.getStartDay() > cal.getActualMaximum(Calendar.DATE)) {
            timeRange.setStartDay(cal.getActualMaximum(Calendar.DATE));
        }
        startDate.set(timeRange.getStartYear(), timeRange.getStartMonth() - 1, timeRange.getStartDay());

        cal.set(Calendar.YEAR, timeRange.getEndYear());
        cal.set(Calendar.MONTH, timeRange.getEndMonth() - 1);
        if (timeRange.getEndDay() > cal.getActualMaximum(Calendar.DATE)) {
            timeRange.setEndDay(cal.getActualMaximum(Calendar.DATE));
        }
        Calendar endDate = Calendar.getInstance();
        endDate.set(timeRange.getEndYear(), timeRange.getEndMonth() - 1, timeRange.getEndDay());

//        // 几周前的修饰
//        if (weekNums.size() > 1) {
//            Integer deltaStartday = weekNums.get(0);
//            Integer deltaendDay = weekNums.get(1);
//
//            startDate.add(startDate.DATE, deltaStartday);
//            endDate.add(endDate.DATE, deltaendDay);
//
//        }

        timeRange.setStartDate(startDate.getTime());
        timeRange.setEndDate(endDate.getTime());
        return timeRange;
    }

}
