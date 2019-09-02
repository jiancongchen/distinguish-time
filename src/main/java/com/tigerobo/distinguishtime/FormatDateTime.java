package com.tigerobo.distinguishtime;

import com.tigerobo.distinguishtime.model.TimeInformation;

import java.sql.Time;
import java.util.regex.Matcher;

/**
 * @author : jiancongchen on 2019-09-02
 *
 * 带有格式的时间，比如：2019-08-31 或者 2019.08.31
 **/
public class FormatDateTime extends AbstractDistinguishTime{

    public FormatDateTime(String statement){
        this.statement = statement;
    }

    @Override
    public TimeInformation fomatLanguageTime() {
        Matcher matcher = ConstantPattern.getPattern(ConstantPattern.DATE_TIME_FORMAT_2).matcher(statement);
        TimeInformation timeInformation = new TimeInformation();
        if (matcher.find()) {
            String[] timeString = matcher.group().split("/|-|\\.");
            if (ChangeTextToNum.str2int(timeString[1]) > 12) {
                // 月份超过12，有问题
            } else {
                // 两个字段的只有前向时间
                if (timeString.length == 2) {
                    timeInformation.setYear(timeInformation.getYear() + "|" + timeString[0]);
                    timeInformation.setMonth(timeInformation.getMonth() + "|" + timeString[1]);
                } else {
                    String yearTmp = timeString[0];
                    String monthTmp = timeString[1];
                    String dayTmp = timeString[2];
                    if (yearTmp.length() < 3 && dayTmp.length() == 4) {
                        String tmp = dayTmp;
                        dayTmp = yearTmp;
                        yearTmp = tmp;
                    }
                    timeInformation.setYear(timeInformation.getYear() + "|" + yearTmp);
                    timeInformation.setMonth(timeInformation.getMonth() + "|" + monthTmp);
                    timeInformation.setDay(timeInformation.getDay() + "|" + dayTmp);
                }
            }
        }
        return timeInformation;
    }
}
