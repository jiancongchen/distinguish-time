package com.tigerobo.distinguishtime;

import java.util.HashMap;
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
    public static final String DATE_TIME_FORMAT_2 = "[0-9一两〇二三四五六七八九十零]{1,4}(-|/)[0-9一两〇二三四五六七八九十零]{1,2}((-|/)[0-9一两〇二三四五六七八九十零]{1,2})?" +
            "|[0-9一两〇二三四五六七八九十零]{1,4}\\.[0-9一两〇二三四五六七八九十零]{1,2}\\.[0-9一两〇二三四五六七八九十零]{1,2}";

    /**
     * 匹配时间格式为：20190831（纯数字）
     * 且后续不是百万等单位的数字，避免匹配到表示大小的纯数字
     */
    public static final String DATE_TIME_FORMAT_3 = "[0-9一两〇二三四五六七八九十零]{4,8}(?!(百万|亿|千|百|万|十))";


    /**
     * 数字格式
     */
    public static final String NUMBER_1 = "[0-9一两〇二三四五六七八九十百千万亿零去上前昨今近明后这本当下]+";


    public static final String NUMBER_2 = "[0-9零一二三四五六七八九十]";

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

}
