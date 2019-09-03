package com.tigerobo.distinguishtime.format2standard;

import com.tigerobo.distinguishtime.ChangeTextToNum;
import com.tigerobo.distinguishtime.ConstantPattern;
import com.tigerobo.distinguishtime.model.TimeRange;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author : jiancongchen on 2019-09-03
 **/
public class WeekStandard {

    /**
     * 语句中如果有周的修饰，对时间进行补充修改
     * @param timeRange
     * @param week
     * @param statement
     * @return
     */
    public static TimeRange getWeekNums(TimeRange timeRange, String week, String statement){
        // 周归一化
        ArrayList<Integer> weekNums = new ArrayList<>();
        if (!"".equals(week)) {
            week = week.replaceAll("^\\|", "");
            String[] weekS = week.split("\\|");
            for (int i = 0; i < weekS.length; i++) {
                int weekNum = LocalDate.now().getDayOfWeek().getValue();
                if (ConstantPattern.getPattern(ConstantPattern.NN).matcher(weekS[i]).lookingAt()) {
                    if (weekS[i].length() > 1) {
                        if (weekS[i].substring(0, 1).equals("近") || weekS[i].substring(0, 1).equals("去")||weekS[i].substring(0, 1).equals("前")) {
                            weekNum = 0 - 7 * ChangeTextToNum.str2int(weekS[i].substring(1)) + 1;
                            weekNums.add(weekNum);
                            weekNums.add(0);
                        } else {
                            // 下三周
                            Integer a = 0;
                            a = ChangeTextToNum.str2int(weekS[0].substring(0,1))>0?1:ChangeTextToNum.str2int(weekS[0].substring(0,1))<0?-1:0;
                            if(a>0) {
                                weekNum = 1 - weekNum + 7*a*(ChangeTextToNum.str2int(weekS[i].substring(1))+1);
                                weekNums.add(1-LocalDate.now().getDayOfWeek().getValue()+ a*7);
                                weekNums.add(weekNum-1);
                            }else if (a<0){
                                weekNum = 1 - weekNum + 7*a*ChangeTextToNum.str2int(weekS[i].substring(1));
                                weekNums.add(weekNum);
                                weekNums.add(1-LocalDate.now().getDayOfWeek().getValue()-1);
                            }else{
                                weekNums.add(1-LocalDate.now().getDayOfWeek().getValue());
                                weekNum = 1 - weekNum + 7*ChangeTextToNum.str2int(weekS[i].substring(1))-1;
                                weekNums.add(weekNum);
                            }
                        }
                        continue;
                    } else {
                        if (weekS[i].equals("近")) {
                            weekNum = ChangeTextToNum.str2int("近") * 7 + 1;
                            weekNums.add(weekNum);
                            weekNums.add(0);
                        }else {
                            weekNum = -1 * weekNum + 7 * ChangeTextToNum.str2int(weekS[i]) + 1;
                            weekNums.add(weekNum);
                            if (weekS[i].equals("本") || weekS[i].equals("这")||weekS[i].equals("当")||weekS[i].equals("下")){
                                // 延伸到本周结束
                                weekNums.add(7*(ChangeTextToNum.str2int(weekS[i])+1)-LocalDate.now().getDayOfWeek().getValue());
                            } else {
                                weekNums.add(-1 * LocalDate.now().getDayOfWeek().getValue());
                            }
                        }
                    }
                } else if (!weekS[i].equals("")) {
                    if (statement.indexOf("周") + 1 == statement.indexOf("前") || statement.indexOf("星") + 2 == statement.indexOf("前")) {
                        weekNum = -7 * ChangeTextToNum.str2int(weekS[i]);
                    }
                }
                weekNums.add(weekNum);
            }
            if (weekNums.size() < 1) {
                weekNums.add(0);
                weekNums.add(0);
            }
            if (weekNums.size() < 2) {
                weekNums.add(weekNums.get(0));
            }
        }
        if (weekNums.size() > 1) {
            Calendar startDate = Calendar.getInstance();
            startDate.setTime(timeRange.getStartDate());
            startDate.add(Calendar.DATE, weekNums.get(0));
            timeRange.setStartDate(startDate.getTime());

            Calendar endDate = Calendar.getInstance();
            endDate.setTime(timeRange.getEndDate());
            endDate.add(Calendar.DATE, weekNums.get(1));
            timeRange.setEndDate(endDate.getTime());
        }
        return timeRange;
    }
}
