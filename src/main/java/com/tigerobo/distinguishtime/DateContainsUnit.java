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

    @Override
    public TimeInformation fomatLanguageTime(String statement) {
        Matcher matcher = ConstantPattern.getPattern(ConstantPattern.DATE_TIME_FORMAT_1).matcher(statement);
        ArrayList<String> findx = new ArrayList<>();
        ArrayList<Integer> findIndex = new ArrayList<>();
        while (matcher.find()) {
            findx.add(matcher.group());
            findIndex.add(matcher.start());
        }
        String year = "";
        String month = "";
        String day = "";
        String week = "";
        String mod = "";
        String quarter = "";
        String hhhh = "";
        Map<Integer,String> markStr = new TreeMap<>();


        for (int i = 0; i < findx.size(); i++) {
            //如果结尾是q或者Q表示季度
            String lastString = findIndex.get(i) > 0 ? statement.substring(findIndex.get(i) - 1, findIndex.get(i) ) : "";
            if(lastString.toUpperCase().equals("Q")){
                quarter = quarter + "|" + findx.get(i);
                markStr.put(i,findx.get(i));
                continue;
            }
            if(findx.get(i).equals("上")||findx.get(i).equals("下")){
                //没有去判断是否超过边界
                try {
                    if (statement.substring(findIndex.get(i) + 1, findIndex.get(i) + 1 + 1).equals("旬")) {
                        mod = mod + "|" + statement.substring(findIndex.get(i),findIndex.get(i)+2);
                        markStr.put(i, statement.substring(findIndex.get(i),findIndex.get(i)+2));

                    }
                }
                catch (Exception e){
                    logger.error("上旬查找超过边界");
                }
            }
            if(findx.get(i).toUpperCase().equals("H") && i+1<findx.size() && ConstantPattern.isNumber(findx.get(i+1))){
                hhhh = hhhh+"|"+findx.get(i+1);
                markStr.put(i,findx.get(i)+findx.get(i+1));
            }
            if (ConstantPattern.isMod(findx.get(i))) {
                mod = mod + "|" + findx.get(i);
                markStr.put(i,findx.get(i));
            }

            //以下的判断必须基于数字的前提
            if(!ConstantPattern.isNumber(findx.get(i))){
                continue;
            }
            if (i+1 < findx.size() && findx.get(i + 1).equals("年")) {
                year = year + "|" + findx.get(i);
                //找到年的索引位置
                markStr.put(i,findx.get(i)+findx.get(i + 1));
                continue;
            }
            if (i+1<findx.size() && findx.get(i + 1).equals("月")) {
                month = month + "|" + findx.get(i);
                Integer startIndex = findIndex.get(i+1);
                // 5月3日,如果长度够, 5月3
                if (startIndex+2<statement.length()) {
                    String next2 =  statement.substring(startIndex+1,startIndex+2);
                    String next3 =  statement.substring(startIndex+2,startIndex+3);
                    if (ConstantPattern.isDigital(next2) && !next3.matches("日|天|号")) {
                        day = day + "|" + findx.get(i + 2);
                        markStr.put(i, findx.get(i) + findx.get(i + 1) + findx.get(i + 2));
                    }else {
                        markStr.put(i, findx.get(i) + findx.get(i + 1));
                    }
                }else if (startIndex + 1 < statement.length() && ConstantPattern.isDigital(statement.substring(startIndex+1,startIndex+2))){
                    day = day + "|" + findx.get(i + 2);
                    markStr.put(i, findx.get(i) + findx.get(i + 1) + findx.get(i + 2));
                }else{
                    markStr.put(i, findx.get(i) + findx.get(i + 1));

                }
                continue;
            }
            if (i+1<findx.size() && (findx.get(i + 1).equals("日") || findx.get(i + 1).equals("天") || findx.get(i + 1).equals("号"))) {
                day = day + "|" + findx.get(i);
                markStr.put(i,findx.get(i)+findx.get(i + 1));
                continue;
            }

            if (i+1<findx.size() && (findx.get(i + 1).equals("周") || findx.get(i + 1).equals("星期"))) {
                week = week + "|" + findx.get(i);
                markStr.put(i,findx.get(i)+findx.get(i + 1));
                continue;
            }
            if (((i+1<findx.size() && findx.get(i + 1).equals("季")))) {
                quarter = quarter + "|" + findx.get(i);
                markStr.put(i,findx.get(i));
                continue;
            }
            if(i+1<findx.size() && findx.get(i + 1).toUpperCase().equals("H")){
                hhhh = hhhh+"|"+findx.get(i);
                markStr.put(i,findx.get(i)+findx.get(i+1));
            }

        }

        return null;
    }

    public static void main(String[] args) {
        DateContainsUnit dateContainsUnit = new DateContainsUnit();
        System.out.print(dateContainsUnit.fomatLanguageTime("2019年8月31日"));
    }
}
