package com.tigerobo.distinguishtime;


import com.tigerobo.distinguishtime.model.TimeInformation;
import com.tigerobo.distinguishtime.model.TimeRange;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author : jiancongchen on 2019-08-31
 **/
public abstract class AbstractDistinguishTime implements DistinguishTime{

    public String statement;

    public static final int YEAR_2000 = 2000;
    public static final int YEAR_1900 = 1900;

    @Override
    public List<TimeRange> getStandardTime(TimeInformation timeInformation) {
        TimeRange timeRange = new TimeRange();
        timeRange = getYearNums(timeRange, timeInformation.getYear(), statement);
        timeRange = getMonthNums(timeRange, timeInformation.getMonth(), statement);
        timeRange = getDayNums(timeRange, timeInformation.getDay(), statement);
        timeRange = changeYearsNum(timeRange);
        timeRange = getHalfYear(timeRange,timeInformation.getHalfYear());
        timeRange = getQuarter(timeRange, timeInformation.getQurater(), statement);
        timeRange = getModify(timeRange,timeInformation.getModify(), statement);
        timeRange = autoComplete(timeRange);
        timeRange = TimeRange.getFormatDate(timeRange);
        timeRange = getWeekNums(timeRange, timeInformation.getWeek(), statement);
        LinkedList<TimeRange> timeRanges = new LinkedList<>();
        timeRanges.add(timeRange);
        return timeRanges;
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
                        if (yearNum==0){
                            Matcher m = ConstantPattern.getPattern(ConstantPattern.NUMBER_3).matcher(yearS[i]);
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
                e1.printStackTrace();
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
                        int a = 0;
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

    /**
     * 语句中如果有周的修饰，对时间进行补充修改
     * @param timeRange
     * @param week
     * @param statement
     * @return
     */
    public TimeRange getWeekNums(TimeRange timeRange,String week,String statement){
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
                            weekNum = 0 - 7 * ChangeTextToNum.str2int(weekS[i].substring(1, weekS[i].length())) + 1;
                            weekNums.add(weekNum);
                            weekNums.add(0);
                        } else {
                            // 下三周
                            Integer a = 0;
                            a = ChangeTextToNum.str2int(weekS[0].substring(0,1))>0?1:ChangeTextToNum.str2int(weekS[0].substring(0,1))<0?-1:0;
                            if(a>0) {
                                weekNum = 1 - weekNum + 7*a*(ChangeTextToNum.str2int(weekS[i].substring(1, weekS[i].length()))+1);
                                weekNums.add(1-LocalDate.now().getDayOfWeek().getValue()+ a*7);
                                weekNums.add(weekNum-1);
                            }else if (a<0){
                                weekNum = 1 - weekNum + 7*a*ChangeTextToNum.str2int(weekS[i].substring(1, weekS[i].length()));
                                weekNums.add(weekNum);
                                weekNums.add(1-LocalDate.now().getDayOfWeek().getValue()-1);
                            }else{
                                weekNums.add(1-LocalDate.now().getDayOfWeek().getValue());
                                weekNum = 1 - weekNum + 7*ChangeTextToNum.str2int(weekS[i].substring(1, weekS[i].length()))-1;
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

    /**
     * 语句中包含季度，对月份进行修饰
     * @param timeRange
     * @param quarter
     * @param statement
     * @return
     */
    public TimeRange getQuarter(TimeRange timeRange, String quarter, String statement){
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
                            quarterNum = quarterNumb - 3 * ChangeTextToNum.str2int(quarterS[i].substring(1, quarterS[i].length()));
                            quarterNums.add(quarterNum);
                            quarterNums.add(quarterNumb);
                        }
                        else{
                            Integer a = 0;
                            a = ChangeTextToNum.str2int(quarterS[0].substring(0,1))>0?1:ChangeTextToNum.str2int(quarterS[0].substring(0,1))<0?-1:0;
                            quarterNumb = quarterNum + a*ChangeTextToNum.str2int(quarterS[i].substring(1, quarterS[i].length()));
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

    /**
     * H 表示半年度
     * @param timeRange
     * @param halfYear
     * @return
     */
    public TimeRange getHalfYear(TimeRange timeRange,String halfYear){
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
     *
     * 对于月末之类对修饰词进行处理
     * @param timeRange
     * @param mod
     * @param statement
     * @return
     */
    public TimeRange getModify(TimeRange timeRange,String mod, String statement){
        mod = mod.replaceAll("^\\|", "");
        String[] mods = mod.split("\\|");
        for(int jj=0;jj<mods.length;jj++){
            try {
                if (ConstantPattern.isMod(mods[jj])) {
                    int monthR;
                    int dayR;
                    int monthRend ;
                    int dayRend;
                    String modName = "";
                    if (mods[jj].length() == 1) {
                        int modIndex = statement.indexOf(mods[jj]);
                        modName = statement.substring(modIndex - 1, modIndex + 1);
                    } else {
                        modName = mods[jj];
                    }
                    Integer[] modRange = ChangeTextToNum.RANG_NUM.get(modName);
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
            }
            catch (Exception e){
                System.out.print("修饰词异常");
                e.printStackTrace();
            }
        }
        return timeRange;
    }


    /**
     * 根据一定的规则改变年份
     * @param timeRange
     */
    public TimeRange changeYearsNum(TimeRange timeRange){
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

    /**
     * 如果语句中没有获取到月份或者日到信息，
     * 自动补充
     * @param timeRange
     * @return
     */
    public static TimeRange autoComplete(TimeRange timeRange){
        if(!timeRange.isMonthFlag()){
            timeRange.setStartMonth(1);
            timeRange.setEndMonth(12);
        }
        if(!timeRange.isDayFlag()){
            timeRange.setStartDay(1);
            timeRange.setEndDay(31);
        }
        return timeRange;
    }
}
