package com.tigerobo.distinguishtime.format2standard;

import com.tigerobo.distinguishtime.ChangeTextToNum;
import com.tigerobo.distinguishtime.ConstantPattern;
import com.tigerobo.distinguishtime.model.TimeRange;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * @author : jiancongchen on 2019-09-03
 **/
public class QuarterStandard {

    /**
     * 语句中包含季度，对月份进行修饰
     * @param timeRange
     * @param quarter
     * @param statement
     * @return
     */
    public static TimeRange getQuarter(TimeRange timeRange, String quarter, String statement){
        // 季度的归一化
        // 季度归一成月份
        ArrayList<Integer> quarterNums = new ArrayList<>();
        if (!quarter.equals("")) {
            quarter = quarter.replaceAll("^\\|", "");
            String[] quarterS = quarter.split("\\|");
            for (int i = 0; i < quarterS.length; i++) {
                int quarterNumb = LocalDate.now().getMonthValue();
                int quarterNum = (quarterNumb - 1) / 3 + 1;
                if (ConstantPattern.getPattern(ConstantPattern.NN).matcher(quarterS[i]).lookingAt()) {
                    if (quarterS[i].length() > 1) {
                        if (quarterS[i].substring(0, 1).equals("近") || quarterS[i].substring(0, 1).equals("去")||quarterS[i].substring(0, 1).equals("前")) {
                            quarterNum = quarterNumb - 3 * ChangeTextToNum.str2int(quarterS[i].substring(1));
                            quarterNums.add(quarterNum);
                            quarterNums.add(quarterNumb);
                        }
                        else{
                            Integer a = 0;
                            a = ChangeTextToNum.str2int(quarterS[0].substring(0,1))>0?1:ChangeTextToNum.str2int(quarterS[0].substring(0,1))<0?-1:0;
                            quarterNumb = quarterNum + a*ChangeTextToNum.str2int(quarterS[i].substring(1));
                            if (a>0) {
                                quarterNums.add(3*(quarterNum+a-1)+1);
                                quarterNums.add(quarterNumb * 3);
                            }
                            else{
                                quarterNums.add(3*(quarterNumb-1)+1);
                                quarterNums.add(3*(quarterNum+a-1)+1);
                            }
                        }
                        continue;
                    } else {
                        if (quarterS[i].equals("近")) {
                            quarterNum = quarterNumb + (-3)*ChangeTextToNum.str2int("近");
                            quarterNums.add(quarterNum);
                            quarterNums.add(quarterNumb);
                        }
                        else {
                            quarterNum = quarterNum + ChangeTextToNum.str2int(quarterS[i]);
                            quarterNums.add((quarterNum-1)*3+1);
                            quarterNums.add((quarterNum)*3);
                        }
                    }
                } else if (!quarterS[i].equals("")) {
                    // 三季度前
                    if (statement.indexOf("季") + 2 == statement.indexOf("前")) {
                        quarterNum = quarterNumb - 3*ChangeTextToNum.str2int(quarterS[i]);
                        quarterNums.add(quarterNum);
                        quarterNums.add(quarterNum);
                    } else {
                        quarterNum = ChangeTextToNum.str2int(quarterS[i]);
                        quarterNums.add((quarterNum-1)*3+1);
                        quarterNums.add(quarterNum*3);
                    }

                }
            }
            if (quarterNums.size() < 2) {
                quarterNums.add(quarterNums.get(0));
            }
        }
        // 季度修改
        if (quarterNums.size() == 2) {
            timeRange.setStartMonth(quarterNums.get(0));
            timeRange.setEndMonth(quarterNums.get(1));
            if(!timeRange.isDayFlag()) {
                timeRange.setStartDay(1);
                timeRange.setEndDay(31);
            }
        }
        return timeRange;
    }

}
