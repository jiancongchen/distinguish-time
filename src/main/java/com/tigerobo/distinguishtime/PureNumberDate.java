package com.tigerobo.distinguishtime;

import com.tigerobo.distinguishtime.model.TimeInformation;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;

/**
 * @author : jiancongchen on 2019-09-02
 *
 * 匹配纯数字类型的时间格式
 *
 * 年份：2018
 * 年月：201805 或者 20185
 * 年月日：20180503
 * 如果一个语句中包含多个纯数字的时间格式，只匹配第一个
 **/
public class PureNumberDate  extends AbstractDistinguishTime{

    private static final int DATE_LENGTH_4 = 4;
    private static final int DATE_LENGTH_5 = 5;
    private static final int DATE_LENGTH_6 = 6;
    private static final int DATE_LENGTH_8 = 8;
    private static final int MONTH_1 = 1;
    private static final int MONTH_12 = 12;
    private static final int DAY_1 = 1;
    private static final int DAY_31 = 31;

    public PureNumberDate(String statement){
        this.statement = statement;
    }

    @Override
    public TimeInformation formatLanguageTime() {
        Matcher matcher = ConstantPattern.getPattern(ConstantPattern.DATE_TIME_FORMAT_3).matcher(statement);
        TimeInformation timeInformation = new TimeInformation();
        //表示数据是否有效
        boolean removeFlag = false;
        String yearTmp = null;
        String monthTmp = null;
        String dayTmp = null;
        if (matcher.find()) {
            if (matcher.group().length() == DATE_LENGTH_6 || matcher.group().length() == DATE_LENGTH_8 || matcher.group().length() == DATE_LENGTH_4 ||matcher.group().length() == DATE_LENGTH_5) {
                yearTmp = timeInformation.getYear() + "|" + matcher.group().substring(0, 4);
                // 先判断=年份是否合格
                if (!StringUtils.isEmpty(yearTmp)){
                    if (matcher.group().length() > DATE_LENGTH_4) {
                        try {
                            monthTmp = matcher.group().substring(4, 6);
                        } catch (Exception e) {
                            System.out.print("5位数字的时间");
                            monthTmp = matcher.group().substring(4, 5);
                        }
                        //月份不在 1 ～ 12之间
                        if (ChangeTextToNum.str2int(monthTmp) < MONTH_1 || ChangeTextToNum.str2int(monthTmp) > MONTH_12) {
                            removeFlag = true;
                            monthTmp = "";
                        } else {
                            monthTmp = timeInformation.getMonth() + "|" + monthTmp;
                        }
                    }
                    // 判断月份是否合格
                    if (!StringUtils.isEmpty(monthTmp)) {
                        if (matcher.group().length() > DATE_LENGTH_6) {
                            dayTmp = matcher.group().substring(6, 8);
                            //日不在，1～31天之内的
                            if (ChangeTextToNum.str2int(dayTmp) < DAY_1 || ChangeTextToNum.str2int(dayTmp) > DAY_31 || removeFlag) {
                                dayTmp = "";
                            } else {
                                dayTmp = timeInformation.getDay() + "|" + matcher.group().substring(6, 8);
                            }
                        }
                    }
                }
            }
            timeInformation.setYear(yearTmp);
            timeInformation.setMonth(monthTmp);
            timeInformation.setDay(dayTmp);
        }
        return timeInformation;
    }
}
