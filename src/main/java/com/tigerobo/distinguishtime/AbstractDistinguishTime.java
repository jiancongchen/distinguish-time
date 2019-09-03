package com.tigerobo.distinguishtime;


import com.tigerobo.distinguishtime.format2standard.*;
import com.tigerobo.distinguishtime.model.TimeInformation;
import com.tigerobo.distinguishtime.model.TimeRange;

import java.time.LocalDate;

/**
 * @author : jiancongchen on 2019-08-31
 **/
public abstract class AbstractDistinguishTime implements DistinguishTime{

    public String statement;

    @Override
    public TimeRange getStandardTime(TimeInformation timeInformation) {
        TimeRange timeRange = new TimeRange();
        timeRange = YearStandard.getYearNums(timeRange, timeInformation.getYear(), statement);
        timeRange = MonthStandard.getMonthNums(timeRange, timeInformation.getMonth(), statement);
        timeRange = DayStandard.getDayNums(timeRange, timeInformation.getDay(), statement);
        timeRange = YearStandard.changeYearsNum(timeRange, statement);
        timeRange = YearStandard.getHalfYear(timeRange,timeInformation.getHalfYear());
        timeRange = QuarterStandard.getQuarter(timeRange, timeInformation.getQurater(), statement);
        timeRange = ModifyStandard.getModify(timeRange,timeInformation.getModify(), statement);
        timeRange = autoComplete(timeRange);
        timeRange = TimeRange.getFormatDate(timeRange);
        timeRange = WeekStandard.getWeekNums(timeRange, timeInformation.getWeek(), statement);
        return timeRange;
    }

    /**
     * 如果语句中没有获取到月份或者日到信息，
     * 自动补充
     * @param timeRange
     * @return
     */
    public static TimeRange autoComplete(TimeRange timeRange){
        boolean completeMonthAndDay = timeRange.getEndYear() < LocalDate.now().getYear()
                && !timeRange.isMonthFlag() && !timeRange.isDayFlag();
        if(completeMonthAndDay){
            timeRange.setStartMonth(1);
            timeRange.setEndMonth(12);
            timeRange.setStartDay(1);
            timeRange.setEndDay(31);
            return timeRange;
        }
        if(timeRange.isMonthFlag() && !timeRange.isDayFlag()){
            timeRange.setStartDay(1);
            timeRange.setEndDay(31);
            return timeRange;
        }
        return timeRange;
    }
}
