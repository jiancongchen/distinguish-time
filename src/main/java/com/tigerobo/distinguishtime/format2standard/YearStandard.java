package com.tigerobo.distinguishtime.format2standard;

import com.tigerobo.distinguishtime.ChangeTextToNum;
import com.tigerobo.distinguishtime.ConstantPattern;
import com.tigerobo.distinguishtime.model.TimeRange;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.regex.Matcher;

/**
 * @author : jiancongchen on 2019-09-03
 **/
public class YearStandard {


    public static final int YEAR_2000 = 2000;
    public static final int YEAR_1900 = 1900;

    /**
     * 把语句中的年份标准化
     * @param year
     * @param statement
     * @return
     */
    public static TimeRange getYearNums(TimeRange timeRange, String year, String statement){
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
                            if (ChangeTextToNum.str2int(yearS[i].substring(1))>0.001) {
                                yearNum = yearNum - 1 * ChangeTextToNum.str2int(yearS[i].substring(1));
                                timeRange.setStartYear(yearNum);
                            }
                        } else {
                            // 需要考虑时间是否是未来的，或者过去的
                            int a ;
                            a = Integer.compare(ChangeTextToNum.str2int(yearS[0].substring(0, 1)), 0);
                            yearNum = yearNum + a * ChangeTextToNum.str2int(yearS[i].substring(1));
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
                            if(!statement.substring(statement.indexOf(yearS[i])+1, statement.indexOf(yearS[i])+2).equals("半")){
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
                        if (yearNum == 0){
                            Matcher m = ConstantPattern.getPattern(ConstantPattern.NUMBER_3).matcher(yearS[i]);
                            if (m.find()){
                                timeRange.setStartYear(ChangeTextToNum.str2int(m.group()));
                                timeRange.setEndYear(ChangeTextToNum.str2int(m.group()));
                            }
                        } else {
                            timeRange.setStartYear(yearNum);
                            timeRange.setEndYear(yearNum);
                        }
                    }
                }
            }
            catch(Exception e1){
                e1.printStackTrace();
                break;
            }
        }
        return timeRange;
    }

    /**
     * H 表示半年度
     * @param timeRange
     * @param halfYear
     * @return
     */
    public static TimeRange getHalfYear(TimeRange timeRange,String halfYear){
        // 半年度归一化
        if (!halfYear.equals("")) {
            halfYear = halfYear.replaceAll("^\\|", "");
            String[] halfYears = halfYear.split("\\|");
            if (halfYears.length==1){
                Integer Hnum = ChangeTextToNum.str2int(halfYears[0]);
                if (Hnum < 3) {
                    timeRange.setStartMonth((Hnum - 1) * 6 + 1);
                    timeRange.setEndMonth(Hnum * 6);
                }
            }
        }
        return timeRange;
    }

    /**
     * 根据一定的规则改变年份
     * @param timeRange
     */
    public static TimeRange changeYearsNum(TimeRange timeRange, String statement){
        // 判断是不是过去的时间，近三年，前年等
        if(!"".equals(ConstantPattern.getHistoryYear(statement))){
            timeRange.setStartYear(LocalDate.now().getYear()-3);
            timeRange.setEndYear(LocalDate.now().getYear());
            timeRange.setStartMonth(LocalDate.now().getMonthValue());
            timeRange.setEndMonth(LocalDate.now().getMonthValue());
            timeRange.setStartDay(LocalDate.now().getDayOfMonth());
            timeRange.setEndDay(LocalDate.now().getDayOfMonth());
        } else if (timeRange.getStartYear() < 100 && (timeRange.getStartYear() <= LocalDate.now().getYear() - 2000 + 3)) {
            //如果年份小于100，并且小于未来3年的数值，则认为是2000年后省略20的写法，例如：18年，22年
            timeRange.setStartYear(timeRange.getStartYear() + YEAR_2000);
            timeRange.setEndYear(timeRange.getEndYear() + YEAR_2000);
        } else if (timeRange.getStartYear() < 100) {
            //或者年份小于100，则可能是：99年，85年
            timeRange.setStartYear(timeRange.getStartYear() + YEAR_1900);
            timeRange.setEndYear(timeRange.getEndYear() + YEAR_1900);
        }
        //上半年，下半年强制设置月份，日
        if (!"".equals(ConstantPattern.getHC(statement))){
            int monthR;
            int dayR;
            int monthRend;
            int dayRend;
            String modName = ConstantPattern.getHC(statement);
            Integer[] modRange = ChangeTextToNum.HC.get(modName);
            monthR = modRange[0] / 100;
            monthRend = modRange[1] / 100;
            dayR = modRange[0] % 100;
            dayRend = modRange[1] % 100;
            if (monthR > 0) {
                timeRange.setStartMonth(monthR);
                timeRange.setEndMonth(monthRend);
            }
            if (dayR > 0) {
                timeRange.setStartDay(dayR);
                timeRange.setEndDay(dayRend);
            }
        }
        return timeRange;
    }

}
