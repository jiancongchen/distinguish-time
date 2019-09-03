package com.tigerobo.distinguishtime;

import com.tigerobo.distinguishtime.model.TimeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;

/**
 * @author : jiancongchen on 2019-08-31
 *
 * 包含时间单位，年月日等的时间格式，
 * 对应:ConstantPattern.DATE_TIME_FORMAT_1
 **/
public class DateContainsUnit extends AbstractDistinguishTime{

    private static Logger logger = LoggerFactory.getLogger(DateContainsUnit.class);

    public DateContainsUnit(String statement){
        this.statement = statement;
    }

    @Override
    public TimeInformation formatLanguageTime() {
        Matcher matcher = ConstantPattern.getPattern(ConstantPattern.DATE_TIME_FORMAT_1).matcher(statement);
        ArrayList<String> findx = new ArrayList<>();
        ArrayList<Integer> findIndex = new ArrayList<>();
        while (matcher.find()) {
            findx.add(matcher.group());
            findIndex.add(matcher.start());
        }
        TimeInformation timeInformation = new TimeInformation();
        Map<Integer,String> markStr = new TreeMap<>();
        for (int i = 0; i < findx.size(); i++) {
            //如果结尾是q或者Q表示季度
            String lastString = findIndex.get(i) > 0 ? statement.substring(findIndex.get(i) - 1, findIndex.get(i) ) : "";
            if(lastString.toUpperCase().equals("Q")){
                timeInformation.setQurater(timeInformation.getQurater() + "|" + findx.get(i));
                markStr.put(i,findx.get(i));
                continue;
            }
            if(findx.get(i).equals("上")||findx.get(i).equals("下")){
                //没有去判断是否超过边界
                try {
                    if (statement.substring(findIndex.get(i) + 1, findIndex.get(i) + 1 + 1).equals("旬")) {
                        timeInformation.setModify(timeInformation.getModify()+ "|" + statement.substring(findIndex.get(i),findIndex.get(i)+2));
                        markStr.put(i, statement.substring(findIndex.get(i),findIndex.get(i)+2));
                    }
                }
                catch (Exception e){
                    logger.error("上旬查找超过边界");
                }
            }
            if(findx.get(i).toUpperCase().equals("H") && i+1<findx.size() && ConstantPattern.isNumber(findx.get(i+1))){
                timeInformation.setHalfYear(timeInformation.getHalfYear() +"|"+findx.get(i+1));
                markStr.put(i,findx.get(i)+findx.get(i+1));
            }
            if (ConstantPattern.isMod(findx.get(i))) {
                timeInformation.setModify(timeInformation.getModify() + "|" + findx.get(i));
                markStr.put(i,findx.get(i));
            }

            //以下的判断必须基于数字的前提
            if(!ConstantPattern.isNumber(findx.get(i))){
                continue;
            }
            if (i+1 < findx.size() && findx.get(i + 1).equals("年")) {
                timeInformation.setYear(timeInformation.getYear() + "|" + findx.get(i));
                //找到年的索引位置
                markStr.put(i,findx.get(i)+findx.get(i + 1));
                continue;
            }
            if (i+1<findx.size() && findx.get(i + 1).equals("月")) {
                timeInformation.setMonth(timeInformation.getMonth() + "|" + findx.get(i));
                Integer startIndex = findIndex.get(i+1);
                // 5月3日,如果长度够, 5月3
                if (startIndex+2<statement.length()) {
                    String next2 =  statement.substring(startIndex+1,startIndex+2);
                    String next3 =  statement.substring(startIndex+2,startIndex+3);
                    if (ConstantPattern.isDigital(next2) && !next3.matches("日|天|号")) {
                        timeInformation.setDay(timeInformation.getDay() + "|" + findx.get(i + 2));
                        markStr.put(i, findx.get(i) + findx.get(i + 1) + findx.get(i + 2));
                    }else {
                        markStr.put(i, findx.get(i) + findx.get(i + 1));
                    }
                }else if (startIndex + 1 < statement.length() && ConstantPattern.isDigital(statement.substring(startIndex+1,startIndex+2))){
                    timeInformation.setDay(timeInformation.getDay() + "|" + findx.get(i + 2));
                    markStr.put(i, findx.get(i) + findx.get(i + 1) + findx.get(i + 2));
                }else{
                    markStr.put(i, findx.get(i) + findx.get(i + 1));
                }
                continue;
            }
            if (i+1<findx.size() && (findx.get(i + 1).equals("日") || findx.get(i + 1).equals("天") || findx.get(i + 1).equals("号"))) {
                timeInformation.setDay(timeInformation.getDay() + "|" + findx.get(i));
                markStr.put(i,findx.get(i)+findx.get(i + 1));
                continue;
            }
            if (i+1<findx.size() && (findx.get(i + 1).equals("周") || findx.get(i + 1).equals("星期"))) {
                timeInformation.setWeek(timeInformation.getWeek() + "|" + findx.get(i));
                markStr.put(i,findx.get(i)+findx.get(i + 1));
                continue;
            }
            if (((i+1<findx.size() && findx.get(i + 1).equals("季")))) {
                timeInformation.setQurater(timeInformation.getQurater()+ "|" + findx.get(i));
                markStr.put(i,findx.get(i));
                continue;
            }
            if(i+1<findx.size() && findx.get(i + 1).toUpperCase().equals("H")){
                timeInformation.setHalfYear(timeInformation.getHalfYear() +"|"+findx.get(i));
                markStr.put(i,findx.get(i)+findx.get(i+1));
            }
        }
        return timeInformation;
    }
}
