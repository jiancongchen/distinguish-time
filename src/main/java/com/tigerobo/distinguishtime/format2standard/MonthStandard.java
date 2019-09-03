package com.tigerobo.distinguishtime.format2standard;

import com.tigerobo.distinguishtime.ChangeTextToNum;
import com.tigerobo.distinguishtime.ConstantPattern;
import com.tigerobo.distinguishtime.model.TimeRange;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

/**
 * @author : jiancongchen on 2019-09-03
 **/
public class MonthStandard {

    /**
     * 把语句汇总的月份标准化
     * @param timeRange
     * @param month
     * @param statement
     * @return
     */
    public static TimeRange getMonthNums(TimeRange timeRange, String month, String statement){
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
                        monthNum = monthNum - 1 * ChangeTextToNum.str2int(monthS[i].substring(1));
                        timeRange.setStartMonth(monthNum);
                    } else {
                        Integer a = 0;
                        a = ChangeTextToNum.str2int(monthS[0].substring(0,1))>0?1:ChangeTextToNum.str2int(monthS[0].substring(0,1))<0?-1:0;
                        monthNum = monthNum + a * ChangeTextToNum.str2int(monthS[i].substring(1));
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
}
