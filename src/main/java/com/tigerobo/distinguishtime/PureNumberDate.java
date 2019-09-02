package com.tigerobo.distinguishtime;

import com.tigerobo.distinguishtime.model.TimeInformation;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.regex.Matcher;

/**
 * @author : jiancongchen on 2019-09-02
 *
 * 匹配纯数字类型的时间格式
 **/
public class PureNumberDate  extends AbstractDistinguishTime{

    public PureNumberDate(String statement){
        this.statement = statement;
    }

    @Override
    public TimeInformation fomatLanguageTime() {
        Matcher matcher = ConstantPattern.getPattern(ConstantPattern.DATE_TIME_FORMAT_3).matcher(statement);
        TimeInformation timeInformation = new TimeInformation();
        boolean removeFlag = false;
        String yearTmp = null;
        String monthTmp = null;
        String dayTmp = null;
        if (matcher.find()) {
            if (matcher.group().length() == 6 || matcher.group().length() == 8 || matcher.group().length() == 4||matcher.group().length() == 5) {
                yearTmp = matcher.group().substring(0, 4);
                // 对year进行过滤
                if ((ChangeTextToNum.str2int(yearTmp) > LocalDate.now().getYear() + 3 || (ChangeTextToNum.str2int(yearTmp) < 1980))){
                    removeFlag = true;
                    yearTmp = "";
                } else {
                    yearTmp = timeInformation.getYear() + "|" + matcher.group().substring(0, 4);
                }
                // 先判断=年份是否合格
                if (!StringUtils.isEmpty(yearTmp)){
                    if (matcher.group().length() > 4) {
                        try {
                            monthTmp = matcher.group().substring(4, 6);
                        } catch (Exception e) {
                            System.out.print("5位数字的时间");
                            monthTmp = matcher.group().substring(4, 5);
                        }
                        if (ChangeTextToNum.str2int(monthTmp) < 1 || ChangeTextToNum.str2int(monthTmp) > 12 || removeFlag) {
                            removeFlag = true;
                            monthTmp = "";
                        } else {
                            monthTmp = timeInformation.getMonth() + "|" + monthTmp;
                        }
                    }
                    // 判断月份是否合格
                    if (!StringUtils.isEmpty(monthTmp)) {
                        if (matcher.group().length() > 6) {
                            dayTmp = matcher.group().substring(6, 8);
                            if (ChangeTextToNum.str2int(dayTmp) < 1 || ChangeTextToNum.str2int(dayTmp) > 31 || removeFlag) {
                                removeFlag = true;
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
