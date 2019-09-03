package com.tigerobo.distinguishtime.format2standard;

import com.tigerobo.distinguishtime.ChangeTextToNum;
import com.tigerobo.distinguishtime.ConstantPattern;
import com.tigerobo.distinguishtime.model.TimeRange;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

/**
 * @author : jiancongchen on 2019-09-03
 **/
public class DayStandard {

    /**
     * 把语句中的天标准化
     * @param timeRange
     * @param day
     * @param statement
     * @return
     */
    public static TimeRange getDayNums(TimeRange timeRange, String day, String statement){
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
                        dayNum = dayNum - 1 * ChangeTextToNum.str2int(dayS[i].substring(1)) + 1;
                        timeRange.setStartDay(dayNum);
                    } else {
                        int a = 0;
                        a = ChangeTextToNum.str2int(dayS[0].substring(0,1))>0?1:ChangeTextToNum.str2int(dayS[0].substring(0,1))<0?-1:0;
                        dayNum = dayNum + a* ChangeTextToNum.str2int(dayS[i].substring(1));
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
