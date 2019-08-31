package com.tigerobo.distinguishtime;


import com.tigerobo.distinguishtime.model.TimeInformation;
import com.tigerobo.distinguishtime.model.TimeRange;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author : jiancongchen on 2019-08-31
 **/
public abstract class AbstractDistinguishTime implements DistinguishTime{

    @Override
    public List<TimeRange> getStandardTime(TimeInformation timeInformation) {
        return null;
    }

    /**
     * 把语句中的年份标准化
     * @param year
     * @param statement
     * @return
     */
    public TimeRange getYearNums(TimeRange timeRange,String year,String statement){
        if(StringUtils.isEmpty(year) || StringUtils.isBlank(year)){
            return timeRange;
        }
        year = year.replaceAll("^\\|", "");
        String[] yearS = year.split("\\|");
        for (int i = 0; i < yearS.length; i++) {
            try {
                int yearNum = LocalDate.now().getYear();
                if (ConstantPattern.getPattern(ConstantPattern.NN).matcher(yearS[i]).lookingAt()) {
                    if (yearS[i].length() > 1) {
                        if (yearS[i].substring(0, 1).equals("近") || yearS[i].substring(0, 1).equals("去")||yearS[i].substring(0, 1).equals("前")) {
                            if (ChangeTextToNum.str2int(yearS[i].substring(1, yearS[i].length()))>0.001) {
                                yearNum = yearNum - 1 * ChangeTextToNum.str2int(yearS[i].substring(1, yearS[i].length()));
                                timeRange.setStartYear(yearNum);
                            }
                        } else {
                            // 需要考虑时间是否是未来的，或者过去的
                            Integer a = 0;
                            a = ChangeTextToNum.str2int(yearS[0].substring(0,1))>0?1:ChangeTextToNum.str2int(yearS[0].substring(0,1))<0?-1:0;
                            yearNum = yearNum + a * ChangeTextToNum.str2int(yearS[i].substring(1, yearS[i].length()));
                            if (a>0) {

                                timeRange.setStartYear(LocalDate.now().getYear() + a);
                                timeRange.setEndYear(yearNum);
                            }
                            else{
                                timeRange.setStartYear(yearNum);
                                timeRange.setEndYear(LocalDate.now().getYear() + a);

                            }
                        }
                        continue;
                    } else {
                        if (yearS[i].equals("近")) {
                            yearNum = yearNum + ChangeTextToNum.str2int("近");
                            timeRange.setStartYear(yearNum);
                            timeRange.setEndYear(LocalDate.now().getYear());

                        } else {
                            if(!statement.substring(statement.indexOf(yearS[i])+1, statement.indexOf(yearS[i])+2).equals("半"))
                            {
                                yearNum = yearNum + ChangeTextToNum.str2int(yearS[i]);
                                timeRange.setStartYear(yearNum);
                                timeRange.setEndYear(yearNum);

                            }
                        }
                    }
                } else if (!yearS[i].equals("")) {
                    // 3年前:
                    if (ChangeTextToNum.str2int(yearS[i]) < 1000 && statement.indexOf("年") + 1 == statement.indexOf("前")) {
                        yearNum = yearNum - ChangeTextToNum.str2int(yearS[i]);
                        timeRange.setStartYear(yearNum);
                        timeRange.setEndYear(yearNum);
                    } else {
                        yearNum = ChangeTextToNum.str2int(yearS[i]);
                        // 2015上半年 case
                        if (yearNum==0){
                            Matcher m = ConstantPattern.getPattern(ConstantPattern.NUMBER_2).matcher(yearS[i]);
                            if (m.find()){
                                timeRange.setStartYear(ChangeTextToNum.str2int(m.group()));
                                timeRange.setEndYear(ChangeTextToNum.str2int(m.group()));

                            }
                        }
                        else {
                            timeRange.setStartYear(yearNum);
                            timeRange.setEndYear(yearNum);
                        }
                    }
                }

            }
            catch(Exception e1){
                break;
            }
        }
        return timeRange;
    }

    /**
     * 把语句汇总的月份标准化
     * @param timeRange
     * @param month
     * @param statement
     * @return
     */
    public TimeRange getMonthNums(TimeRange timeRange, String month, String statement){
        if(StringUtils.isEmpty(month) || StringUtils.isBlank(month)){
            return timeRange;
        }
        month = month.replaceAll("^\\|", "");
        String[] monthS = month.split("\\|");
        for (int i = 0; i < monthS.length; i++) {
            int monthNum = LocalDate.now().getMonthValue();
            if (ConstantPattern.getPattern(ConstantPattern.NN).matcher(monthS[i]).lookingAt()) {
                if (monthS[i].length() > 1) {
                    if (monthS[i].substring(0, 1).equals("近")|| monthS[i].substring(0, 1).equals("去")||monthS[i].substring(0, 1).equals("前")) {
                        monthNum = monthNum - 1 * ChangeTextToNum.str2int(monthS[i].substring(1, monthS[i].length()));
                        timeRange.setStartMonth(monthNum);
                    } else {
                        Integer a = 0;
                        a = ChangeTextToNum.str2int(monthS[0].substring(0,1))>0?1:ChangeTextToNum.str2int(monthS[0].substring(0,1))<0?-1:0;
                        monthNum = monthNum + a * ChangeTextToNum.str2int(monthS[i].substring(1, monthS[i].length()));
                        if (a>0){
                            timeRange.setStartMonth(LocalDate.now().getMonthValue()+a);
                            timeRange.setEndMonth(monthNum);
                        }else{
                            timeRange.setStartMonth(monthNum);
                            timeRange.setEndMonth(LocalDate.now().getMonthValue()+a);
                        }

                    }
                    continue;
                } else {
                    // 近月
                    if (monthS[i].equals("近")) {
                        monthNum = monthNum + ChangeTextToNum.str2int("近");
                        timeRange.setStartMonth(monthNum);
                        timeRange.setEndMonth(LocalDate.now().getMonthValue());
                    } else {
                        monthNum = monthNum + ChangeTextToNum.str2int(monthS[i]);
                        timeRange.setStartMonth(monthNum);
                        timeRange.setEndMonth(monthNum);
                    }
                }
            } else if (!monthS[i].equals("")) {
                // 三个月前
                if (statement.indexOf("月") + 1 == statement.indexOf("前")) {
                    monthNum = monthNum - ChangeTextToNum.str2int(monthS[i]);
                    timeRange.setStartMonth(monthNum);
                    timeRange.setEndMonth(monthNum);
                } else {
                    monthNum = ChangeTextToNum.str2int(monthS[i]);
                    timeRange.setStartMonth(monthNum);
                    timeRange.setEndMonth(monthNum);
                }
            }
        }
        return timeRange;
    }

    /**
     * 把语句中的天标准化
     * @param timeRange
     * @param day
     * @param statement
     * @return
     */
    public TimeRange getDayNums(TimeRange timeRange, String day, String statement){
        if(StringUtils.isEmpty(day) || StringUtils.isBlank(day)){
            return timeRange;
        }
        day = day.replaceAll("^\\|", "");
        String[] dayS = day.split("\\|");
        for (int i = 0; i < dayS.length; i++) {
            int dayNum = LocalDate.now().getDayOfMonth();
            if (ConstantPattern.getPattern(ConstantPattern.NN).matcher(dayS[i]).lookingAt()) {
                if (dayS[i].length() > 1) {
                    if (dayS[i].substring(0, 1).equals("近")||dayS[i].substring(0, 1).equals("去")||dayS[i].substring(0, 1).equals("前")) {
                        dayNum = dayNum - 1 * ChangeTextToNum.str2int(dayS[i].substring(1, dayS[i].length())) + 1;
                        timeRange.setStartDay(dayNum);
                    } else {
                        Integer a = 0;
                        a = ChangeTextToNum.str2int(dayS[0].substring(0,1))>0?1:ChangeTextToNum.str2int(dayS[0].substring(0,1))<0?-1:0;
                        dayNum = dayNum + a* ChangeTextToNum.str2int(dayS[i].substring(1, dayS[i].length()));
                        if (a>0) {
                            timeRange.setStartDay(LocalDate.now().getDayOfMonth()+a);
                            timeRange.setEndDay(dayNum);
                        }else{
                            timeRange.setStartDay(dayNum);
                            timeRange.setEndDay(LocalDate.now().getDayOfMonth()+a);

                        }
                    }
                    continue;
                } else {
                    if (dayS[i].equals("近")) {
                        dayNum = dayNum + ChangeTextToNum.str2int("近") + 1;
                        timeRange.setStartDay(dayNum);
                        timeRange.setEndDay(LocalDate.now().getDayOfMonth());
                    } else {
                        dayNum = dayNum + ChangeTextToNum.str2int(dayS[i]);
                    }

                }
            } else if (!dayS[i].equals("")) {
                if (statement.indexOf("日") + 1 == statement.indexOf("前") || statement.indexOf("天") + 1 == statement.indexOf("前")) {
                    dayNum = dayNum - ChangeTextToNum.str2int(dayS[i]);
                } else {
                    dayNum = ChangeTextToNum.str2int(dayS[i]);
                }
            }
            timeRange.setStartDay(dayNum);
            timeRange.setEndDay(dayNum);
        }
        return timeRange;
    }
}
