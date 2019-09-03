package com.tigerobo.distinguishtime;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : jiancongchen on 2019-08-30
 **/
public class ConstantPattern {

    /**
     * 匹配时间格式为：2019年8月31日
     */
    public static final String DATE_TIME_FORMAT_1 = "[0-9一两〇二三四五六七八九十百千万亿零去上前昨今近明后这本当下]+|(年|月|日|天|号|周|星期|季|H|h)|(上旬|中旬|下旬|底|初|末|中)";

    /**
     * 匹配时间格式为：2019-08-31 或者 2019.08.31
     */
    public static final String DATE_TIME_FORMAT_2 = "[0-9一两〇二三四五六七八九十零]{1,4}(-|/)[0-9一两〇二三四五六七八九十零]{1,2}((-|/)[0-9一两〇二三四五六七八九十零]{1,4})?" +
            "|[0-9一两〇二三四五六七八九十零]{1,4}\\.[0-9一两〇二三四五六七八九十零]{1,2}\\.[0-9一两〇二三四五六七八九十零]{1,4}";

    /**
     * 匹配时间格式为：20190831（纯数字）
     * 且后续不是百万等单位的数字，避免匹配到表示大小的纯数字
     */
    public static final String DATE_TIME_FORMAT_3 = "[0-9一两〇二三四五六七八九十零]{4,8}(?!(百万|亿|千|百|万|十))";

    /**
     * 其他的时间格式
     */
    public static final String DATE_TIME_FORMAT_4 = "([0-9一两〇二三四五六七八九十零]{1,4})(至|到)([0-9一两〇二三四五六七八九十零]{1,4})(日|号)";

    public static final String DATE_TIME_FORMAT_5 = "(([0-9一两〇二三四五六七八九十零]{2,4})年?).?([0-9一两〇二三四五六七八九十零])(至|到).?([0-9一两〇二三四五六七八九十零])季";

    public static final String DATE_TIME_FORMAT_6 = "([1-4qhHQ]{2})\\s?([0-9]{2,4})";

    public static final String DATE_TIME_FORMAT_7 = "([0-9]{2,4})年?\\s?([1-4qhHQ]{2})";

    /**
     * 表示过去的时间格式
     */
    public static final String HISTORY_DATE = "(近|历|过去|前)(?![0-9一两二三四五六七八九十]).{0,2}年|历史|回顾|以往|过往|最近(?![0-9一两〇二三四五六七八九十]+)|近期|以前";

    /**
     * 数字格式
     */
    public static final String NUMBER_1 = "[0-9一两〇二三四五六七八九十百千万亿零去上前昨今近明后这本当下]+";

    public static final String NUMBER_2 = "[0-9零一二三四五六七八九十]";

    public static final String NUMBER_3 = "[0-9零一二三四五六七八九十]+";

    /**
     * 表示时间前后的语义
     */
    public static final String NN = "[去上前昨今近明后这本当下]";

    /**
     * 时间单位
     */
    public static final String UNIT = "(年|月|日|天|号|周|星期|季|H|h)";

    /**
     * 用于月份的特殊表达
     */
    public static final String MODIFICATION = "(上旬|中旬|下旬|底|初|末|中)";

    /**
     * 半年的表达方式
     */
    public static final String HALF_YEAR = "上半年|下半年";

    public static final String QUARTER = "[qQHh]";

    /**
     * 存放编译后的正则表达式
     */
    public static final HashMap<String, Pattern> patternFactory = new HashMap<>();

    /**
     * 享元模式，共享编译后的正则表达式
     * @param expression
     * @return
     */
    public static Pattern getPattern(String expression){
        Pattern pattern = patternFactory.get(expression);
        if(pattern == null){
            pattern = Pattern.compile(expression);
            //防止恶意使用，导致内存泄露
            if(patternFactory.size() < 64){
                patternFactory.put(expression,pattern);
            }
        }
        return pattern;
    }

    /**
     * 给定的字符串，是否符合正则规则
     * @param text
     * @param expression
     * @return
     */
    public static boolean isMatch(String text, String expression){
        Pattern pattern = getPattern(expression);
        return pattern.matcher(text).matches();
    }

    public static boolean isNumber(String text){
        return isMatch(text, NUMBER_1);
    }

    public static boolean isDigital(String text){
        return isMatch(text, NUMBER_2);
    }

    public static boolean isUnit(String text){
        return isMatch(text, UNIT);
    }

    public static boolean isMod(String text){
        return isMatch(text, MODIFICATION);
    }


    public static String getHistoryYear(String statement){
        Matcher matcher = getPattern(HISTORY_DATE).matcher(statement);
        if (matcher.find()){
            String s = matcher.group();
            return "前年".equals(s) ? "" : matcher.group();
        }
        return "";
    }

    public static String getHC(String query){
        Matcher matcher = getPattern(HALF_YEAR).matcher(query);
        return matcher.find() ? matcher.group() : "";
    }

}

