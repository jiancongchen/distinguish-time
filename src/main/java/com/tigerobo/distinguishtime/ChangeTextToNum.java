package com.tigerobo.distinguishtime;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author : jiancongchen on 2019-08-31
 *
 * 把中文的文本数字转换为数字类型
 **/
public class ChangeTextToNum {

    public static Map<String,Integer> char2num = new HashMap<>();
    public static Map<String,Integer> CN_NUM = new HashMap<>();
    public static Map<String,Integer> CN_UNIT = new HashMap<>();
    public static Map<String,Integer[]> HC = new HashMap<>();
    public static Map<String,Integer[]> RANG_NUM = new HashMap<>();
    private static final String TEN = "十";

    /**
     * 字符转数字类型
     * @param text
     * @return
     */
    public static int str2int(String text){
        try{
            return Integer.parseInt(text);
        }
        catch (NumberFormatException e) {
            return textToInt(text);
        }
    }

    public static int textToInt(String text){
        int result = 0;
        switch (text.length()){
            case 1:result = char2num.get(text);break;
            case 2:result = length2ToInt(text);break;
            case 3:result = text.indexOf(TEN) > -1 ? char2num.get(text.substring(0, 1)) * 10 + char2num.get(text.substring(2, 3)) * 1 : result;break;
            case 4:result = char2num.get(text.substring(0, 1))*1000+char2num.get(text.substring(1, 2))*100+char2num.get(text.substring(2, 3))*10+char2num.get(text.substring(3, 4))*1;break;
            default:chineseToInt(text);
        }
        return result;
    }

    public static int length2ToInt(String text){
        if (TEN.equals(text.substring(0,1))){
            return char2num.get(text.substring(1, 2))+10;
        }
        if(!TEN.equals(text.substring(1,2))){
            return char2num.get(text.substring(0, 1))*10 + char2num.get(text.substring(1, 2));
        }
        return char2num.get(text.substring(0, 1))*10;
    }


    /**
     * 中文的大写数字转数字类型
     * @param query
     * @return
     */
    public static int chineseToInt(String query){
        LinkedList<String> a = new LinkedList<>();
        for(int i=0;i<query.length();i++){
            a.addFirst(query.substring(i,i+1));
        }
        int unit =0;
        LinkedList<Object> tt = new LinkedList<>();

        while(a.size()>0){
            String last = a.pop();
            // 先判断单位
            if (CN_UNIT.containsKey(last)){
                unit = CN_UNIT.get(last);
                if (unit==10000){
                    tt.addFirst("w");
                    unit = 1;
                }
                else if(unit == 100000000){
                    tt.addFirst("y");
                    unit = 1;
                }
                continue;
            }
            else{
                Integer nn = CN_NUM.get(last);
                if (unit > 0) {
                    nn = nn*unit;
                    unit = 0;
                }
                tt.addFirst(nn);

            }
        }
        // 处理10-19的数字
        if(unit ==10){
            tt.addFirst(10);
        }
        int tmp = 0;
        int ret = 0;
        while(tt.size()>0){
            Object x = tt.pop();
            if(x != null){
                if ("w".equals(x.toString())){
                    tmp *= 10000;
                    ret +=tmp;
                    tmp = 0;

                }else if("y".equals(x.toString())){
                    tmp *= 100000000;
                    ret +=tmp;
                    tmp = 0;
                }else{
                    tmp += (int)x;
                }
            }
        }
        ret +=tmp;
        return ret;
    }


    static{
        char2num.put("一",1);
        char2num.put("二",2);
        char2num.put("两",2);
        char2num.put("三",3);
        char2num.put("四",4);
        char2num.put("五",5);
        char2num.put("六",6);
        char2num.put("七",7);
        char2num.put("八",8);
        char2num.put("九",9);
        char2num.put("十",10);
        char2num.put("零",0);
        char2num.put("〇",0);
        char2num.put("去",-1);
        char2num.put("上",-1);
        char2num.put("前",-2);
        char2num.put("这",0);
        char2num.put("本",0);
        char2num.put("当",0);
        char2num.put("下",1);
        char2num.put("昨",-1);
        char2num.put("明",1);
        char2num.put("后",2);
        char2num.put("今",0);
        char2num.put("近",-3);
        char2num.put("百",100);
        char2num.put("十",10);
        char2num.put("千",1000);
        char2num.put("万",10000);

        CN_NUM.put("一",1);
        CN_NUM.put("二",2);
        CN_NUM.put("两",2);
        CN_NUM.put("三",3);
        CN_NUM.put("四",4);
        CN_NUM.put("五",5);
        CN_NUM.put("六",6);
        CN_NUM.put("七",7);
        CN_NUM.put("八",8);
        CN_NUM.put("九",9);
        CN_NUM.put("十",10);
        CN_NUM.put("零",0);
        CN_NUM.put("〇",0);
        CN_NUM.put("壹",1);
        CN_NUM.put("貳",2);
        CN_NUM.put("叁",3);
        CN_NUM.put("肆",4);
        CN_NUM.put("伍",5);
        CN_NUM.put("陆",6);
        CN_NUM.put("柒",7);
        CN_NUM.put("捌",8);
        CN_NUM.put("玖",9);

        CN_UNIT.put("十",10);
        CN_UNIT.put("拾",10);
        CN_UNIT.put("百",100);
        CN_UNIT.put("佰",100);
        CN_UNIT.put("千",1000);
        CN_UNIT.put("万",10000);
        CN_UNIT.put("萬",10000);
        CN_UNIT.put("亿",100000000);
        CN_UNIT.put("億",100000000);

        HC.put("上半年", new Integer[]{101, 630});
        HC.put("下半年", new Integer[]{701, 1231});

        RANG_NUM.put("上旬",new Integer[]{1,10});
        RANG_NUM.put("中旬",new Integer[]{11,20});
        RANG_NUM.put("下旬",new Integer[]{21,31});
        RANG_NUM.put("年底",new Integer[]{1221,1231});
        RANG_NUM.put("年末",new Integer[]{1221,1231});
        RANG_NUM.put("月底",new Integer[]{31,31});
        RANG_NUM.put("年初",new Integer[]{101,110});
        RANG_NUM.put("月初",new Integer[]{1,1});
        RANG_NUM.put("月末",new Integer[]{31,31});
        RANG_NUM.put("月中",new Integer[]{15,15});
    }
}
