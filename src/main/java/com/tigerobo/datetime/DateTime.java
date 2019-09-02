package com.tigerobo.datetime;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTime {


    private static Logger logger = LoggerFactory.getLogger(DateTime.class);

    String number = "[0-9一两〇二三四五六七八九十百千万亿零去上前昨今近明后这本当下]+";
    String nn = "[去上前昨今近明后这本当下]";
    String unit = "(年|月|日|天|号|周|星期|季|H|h)";
    String modification = "(上旬|中旬|下旬|底|初|末|中)";
    public Map<String,Integer> char2num = new HashMap<String,Integer>(){
        {
            put("一",1);
            put("二",2);
            put("两",2);
            put("三",3);
            put("四",4);
            put("五",5);
            put("六",6);
            put("七",7);
            put("八",8);
            put("九",9);
            put("十",10);
            put("零",0);
            put("〇",0);
            put("去",-1);
            put("上",-1);
            put("前",-2);
            put("这",0);
            put("本",0);
            put("当",0);
            put("下",1);

            put("昨",-1);
            put("明",1);
            put("后",2);
            put("今",0);
            put("近",-3);
            put("百",100);
            put("十",10);
            put("千",1000);
            put("万",10000);
        }
    };

    public Integer chinese2Int(String query)
    {
        Map<String,Integer> CN_NUM = new HashMap<String,Integer>(){
            {
                put("一",1);
                put("二",2);
                put("两",2);
                put("三",3);
                put("四",4);
                put("五",5);
                put("六",6);
                put("七",7);
                put("八",8);
                put("九",9);
                put("十",10);
                put("零",0);
                put("〇",0);

                put("壹",1);
                put("貳",2);
                put("叁",3);
                put("肆",4);
                put("伍",5);
                put("陆",6);
                put("柒",7);
                put("捌",8);
                put("玖",9);
            }
        };
        Map<String,Integer> CN_UNIT = new HashMap<String,Integer>(){
            {
                put("十",10);
                put("拾",10);
                put("百",100);
                put("佰",100);
                put("千",1000);
                put("万",10000);
                put("萬",10000);
                put("亿",100000000);
                put("億",100000000);

            }
        };
        LinkedList<String> a = new LinkedList<>();
        for(int i=0;i<query.length();i++){
            a.addFirst(query.substring(i,i+1));
        }
        Integer unit =0;
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
        Integer tmp = 0;
        Integer ret = 0;
        while(tt.size()>0){
            Object x = tt.pop();
            if (x.toString().equals("w")){
                tmp *= 10000;
                ret +=tmp;
                tmp = 0;

            }
            else if(x.toString().equals("y")){
                tmp *= 100000000;
                ret +=tmp;
                tmp = 0;
            }
            else{
                tmp += (int)x;
            }
        }
        ret +=tmp;
        return ret;

    }




    List<String> nextString = new ArrayList<>(Arrays.asList("明","后","下"));
    List<String> lastString = new ArrayList<>(Arrays.asList("去","上","前","昨",
            "近"));
    Map<String,Integer[]> rangeNum = new HashMap<String,Integer[]>(){
        {
            put("上旬",new Integer[]{1,10});
            put("中旬",new Integer[]{11,20});
            put("下旬",new Integer[]{21,31});
            put("年底",new Integer[]{1221,1231});
            put("年末",new Integer[]{1221,1231});
            put("月底",new Integer[]{31,31});
            put("年初",new Integer[]{101,110});
            put("月初",new Integer[]{1,1});
            put("月末",new Integer[]{31,31});
            put("月中",new Integer[]{15,15});
        }
    };
    // hard code
    String hcString = "上半年|下半年";
    Map<String,Integer[]> hc = new HashMap<String,Integer[]>(){
        {
            put("上半年", new Integer[]{101, 630});
            put("下半年", new Integer[]{701, 1231});
        }
    };

    String getHC(String query){
        Matcher m = Pattern.compile(hcString).matcher(query);
        if(m.find()){
            return m.group();

        }
        else{
            return "";
        }
    }



    public String getHistoryYear(String query){

        final String regex1 = "(近|历|过去|前)(?![0-9一两二三四五六七八九十]).{0,2}年|历史|回顾|以往|过往|最近(?![0-9一两〇二三四五六七八九十]+)|近期|以前";

        if (regex1==null || regex1.equals("")){
            return "";
        }
        Pattern yearPt1 = Pattern.compile(regex1);

        Matcher m1 = yearPt1.matcher(query);

        if (m1.find()){
            String s = m1.group();
            if(s.equals("前年")){
                return "";
            }
            else {
                return m1.group();
            }
        }
        return "";
    }

    public Integer str2int(String text){
        try{
            return Integer.parseInt(text);
        }
        catch (NumberFormatException e) {
            try {
                if (text.length() == 1) {
                    return char2num.get(text);
                } else if (text.length() == 2) {
                    if (text.substring(0,1).equals("十")){
                        return char2num.get(text.substring(1, 2))+10;
                    }
                    else if(!text.substring(1,2).equals("十")){
                        return char2num.get(text.substring(0, 1))*10 + char2num.get(text.substring(1, 2));
                    }
                    else {
                        return char2num.get(text.substring(0, 1))*10;
                    }

                } else if (text.length() == 3) {
                    if (text.indexOf("十") > -1) {
                        return char2num.get(text.substring(0, 1)) * 10 + char2num.get(text.substring(2, 3)) * 1;
                    }
                    else{
                        return 0;
                    }
                }
                else if (text.length()==4){
                    return  char2num.get(text.substring(0, 1))*1000+char2num.get(text.substring(1, 2))*100+
                            char2num.get(text.substring(2, 3))*10+char2num.get(text.substring(3, 4))*1;
                }
                else
                {
                    return chinese2Int(text);
                }
            }
            catch (Exception e1){
                return 0;
            }
        }
//        return 0;
    }

    public  boolean isNumber( String q ){
        Pattern p = Pattern.compile(number);
        if(p.matcher(q).matches()){
            return true;
        }
        else{
            return false;
        }
    }

    public  boolean isDigital( String q ){
        Pattern p = Pattern.compile("[0-9零一二三四五六七八九十]");
        if(p.matcher(q).matches()){
            return true;
        }
        else{
            return false;
        }
    }
    public  boolean isUnit( String q ){
        Pattern p = Pattern.compile(unit);
        if(p.matcher(q).matches()){

            return true;
        }
        else{
            return false;
        }
    }
    public  boolean isMod( String q ){
        Pattern p = Pattern.compile(modification);
        if(p.matcher(q).matches()){
            return true;
        }
        else{
            return false;
        }
    }

    public  String getNormalTime(Integer year, Integer month, Integer day){

        // 先生成标准日期格式：
        Calendar startDate = Calendar.getInstance();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        // 某年某月的最后一天
        if (day > cal.getActualMaximum(Calendar.DATE)) {
            day = cal.getActualMaximum(Calendar.DATE);
        }
        startDate.set(year, month - 1, day);
        Date startTime = startDate.getTime();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        return ft.format(startTime);

    }

    public  MarketDate getDateTime(String subQuery) {
        try {
            //近 n [年/月/日/天]
            // [n|去|上一] 年 [n] 月
            //[n|上个] 月 [n] 日/号
            //[n|去|上个] 年/月底
            //[n|上个] 月 上中下旬
            boolean yearFlag = false;
            boolean monthFlag = false;
            String pattern = String.format("%s|%s|%s", number, unit, modification);
            String patternFormat = "[0-9一两〇二三四五六七八九十零]{1,4}(-|/)[0-9一两〇二三四五六七八九十零]{1,2}((-|/)[0-9一两〇二三四五六七八九十零]{1,2})?" +
                    "|[0-9一两〇二三四五六七八九十零]{1,4}\\.[0-9一两〇二三四五六七八九十零]{1,2}\\.[0-9一两〇二三四五六七八九十零]{1,2}";
            String patternFormat1 = "[0-9一两〇二三四五六七八九十零]{4,8}(?!(百万|亿|千|百|万|十))";

            Pattern p1 = Pattern.compile(pattern);
            Pattern p2 = Pattern.compile(patternFormat);
            Pattern p3 = Pattern.compile(patternFormat1);


            Matcher m1 = p1.matcher(subQuery);
            Matcher m2 = p2.matcher(subQuery);
            Matcher m3 = p3.matcher(subQuery);

            ArrayList<String> findx = new ArrayList<>();
            ArrayList<Integer> findIndex = new ArrayList<>();
            while (m1.find()) {
//                System.out.println(m1.group());
                findx.add(m1.group());
                findIndex.add(m1.start());

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
                if (isNumber(findx.get(i)) && i+1<findx.size() && findx.get(i + 1).equals("年")) {
                    year = year + "|" + findx.get(i);
                    //找到年的索引位置
                    markStr.put(i,findx.get(i)+findx.get(i + 1));
                    continue;

                }
                if (isNumber(findx.get(i)) && i+1<findx.size() && findx.get(i + 1).equals("月")) {
                    month = month + "|" + findx.get(i);
                    Integer startIndex = findIndex.get(i+1);
                    // 5月3日,如果长度够, 5月3
                    if (startIndex+2<subQuery.length()) {

                        String next2 =  subQuery.substring(startIndex+1,startIndex+2);
                        String next3 =  subQuery.substring(startIndex+2,startIndex+3);
                        if (isDigital(next2) && !next3.matches("日|天|号")) {
                            day = day + "|" + findx.get(i + 2);
                            markStr.put(i, findx.get(i) + findx.get(i + 1) + findx.get(i + 2));
                        }
                        else {
                            markStr.put(i, findx.get(i) + findx.get(i + 1));
                        }
                    }
                    // 句子长度不够
                    else if (startIndex + 1 < subQuery.length() && isDigital(subQuery.substring(startIndex+1,startIndex+2)))
                    {
                        day = day + "|" + findx.get(i + 2);
                        markStr.put(i, findx.get(i) + findx.get(i + 1) + findx.get(i + 2));
                    }
                    else{
                        markStr.put(i, findx.get(i) + findx.get(i + 1));

                    }
                    continue;
                }
                if (isNumber(findx.get(i)) && i+1<findx.size() && (findx.get(i + 1).equals("日") || findx.get(i + 1).equals("天") || findx.get(i + 1).equals("号"))) {
                    day = day + "|" + findx.get(i);
                    markStr.put(i,findx.get(i)+findx.get(i + 1));
                    continue;
                }
//                if(isNumber(findx.get(i)) && i-1>=0 && i+1==findx.size() && findx.get(i-1).equals("月") ){
//                    day = day + "|" + findx.get(i);
//                    markStr.put(i,findx.get(i));
//                    continue;
//                }

                if (isNumber(findx.get(i)) && i+1<findx.size() && (findx.get(i + 1).equals("周") || findx.get(i + 1).equals("星期"))) {
                    week = week + "|" + findx.get(i);
                    markStr.put(i,findx.get(i)+findx.get(i + 1));
                    continue;
                }

                String lastString="";
                if (findIndex.get(i)>0) {

                    lastString = subQuery.substring(findIndex.get(i) - 1, findIndex.get(i) );
                }



                if (isNumber(findx.get(i)) && ((i+1<findx.size() && findx.get(i + 1).equals("季"))||lastString.toUpperCase().equals("Q"))) {
                    quarter = quarter + "|" + findx.get(i);
                    markStr.put(i,findx.get(i));
                    continue;
                }

                if (isMod(findx.get(i))) {
                    mod = mod + "|" + findx.get(i);
                    markStr.put(i,findx.get(i));
                }
                if(findx.get(i).equals("上")||findx.get(i).equals("下")){
                    //没有去判断是否超过边界
                    try {
                        if (subQuery.substring(findIndex.get(i) + 1, findIndex.get(i) + 1 + 1).equals("旬")) {
                            mod = mod + "|" + subQuery.substring(findIndex.get(i),findIndex.get(i)+2);
                            markStr.put(i, subQuery.substring(findIndex.get(i),findIndex.get(i)+2));

                        }
                    }
                    catch (Exception e){
                        logger.error("上旬查找超过边界");
                    }
                }

//                if (i+1<findx.size() && isMod(findx.get(i + 1))) {
//                    mod = mod + "|" + findx.get(i + 1);
//                    markStr.put(i+1,findx.get(i+1));
//                }

                if(isNumber(findx.get(i)) && i+1<findx.size() && findx.get(i + 1).toUpperCase().equals("H")){
                    hhhh = hhhh+"|"+findx.get(i);
                    markStr.put(i,findx.get(i)+findx.get(i+1));
                }
                if(findx.get(i).toUpperCase().equals("H") && i+1<findx.size() && isNumber(findx.get(i+1))){
                    hhhh = hhhh+"|"+findx.get(i+1);
                    markStr.put(i,findx.get(i)+findx.get(i+1));
                }
            }






            String dateStr="";
            List<Map.Entry<Integer,String>> list = new ArrayList<Map.Entry<Integer,String>>(markStr.entrySet());
            list.sort((Map.Entry<Integer,String> h1, Map.Entry<Integer,String> h2) -> h1.getKey().compareTo(h2.getKey()));
            for(Map.Entry<Integer,String> x:list){
                dateStr = dateStr+x.getValue();
            }

            if(year.equals("") && month.equals("") && day.equals("") && week.equals("")) {
                if (m2.find()) {
                    String[] timeString = m2.group().split("/|-|\\.");
                    if (dateStr.equals("")) {
                        dateStr = m2.group();
                    }
                    if (str2int(timeString[1])>12){
                        // 月份有问题
                    }
                    else {
                        // 两个字段的只有前向时间
                        if (timeString.length==2) {
                            year = year + "|" + timeString[0];
                            month = month + "|" + timeString[1];
                        }
                        else{
                            String yearTmp = timeString[0];
                            String monthTmp = timeString[1];
                            String dayTmp = timeString[2];
                            if (yearTmp.length()<3 && dayTmp.length()==4){
                                String tmp = dayTmp;
                                dayTmp = yearTmp;
                                yearTmp = tmp;
                            }
                            year = year + "|" + yearTmp;
                            month = month + "|" + monthTmp;
                            day = day + "|" + dayTmp;

                        }
                    }
                }
                boolean removeFlag = false;
                String monthTmp = month;
                String yearTmp = year;
                String dayTmp = day;
                if (m3.find()) {
                    if (dateStr.equals("")) {
                        dateStr = m3.group();
                    }
                    if (m3.group().length() == 6 || m3.group().length() == 8 || m3.group().length() == 4||m3.group().length() == 5) {

                        yearTmp = m3.group().substring(0, 4);

//                    year = year + "|" + m3.group().substring(0, 4);
                        // 对year进行过滤
                        if ((str2int(yearTmp) > LocalDate.now().getYear() + 3 || (str2int(yearTmp) < 1980))){ //&& m3.group().length() == 4) {
                            removeFlag = true;
                            yearTmp = "";

                        } else {
                            yearTmp = year + "|" + m3.group().substring(0, 4);
                        }
                        // 先判断=年份是否合格
                        if (!StringUtils.isEmpty(yearTmp))
                        {
                            if (m3.group().length() > 4) {

                                try {
                                    monthTmp = m3.group().substring(4, 6);

                                } catch (Exception e) {
                                    logger.info("5位数字的时间");
                                    monthTmp = m3.group().substring(4, 5);
                                }
                                if (str2int(monthTmp) < 1 || str2int(monthTmp) > 12 || removeFlag) {
                                    removeFlag = true;
                                    yearTmp = year;
                                    monthTmp = "";
                                } else {
                                    monthTmp = month + "|" + monthTmp;
                                }
                            }
                            // 判断月份是否合格
                            if (!StringUtils.isEmpty(monthTmp)) {
                                if (m3.group().length() > 6) {
                                    dayTmp = m3.group().substring(6, 8);
                                    if (str2int(dayTmp) < 1 || str2int(dayTmp) > 31 || removeFlag) {
                                        removeFlag = true;
                                        yearTmp = year;
                                        monthTmp = month;
                                        dayTmp = "";
                                    } else {
                                        dayTmp = day + "|" + m3.group().substring(6, 8);
                                    }
                                }
                            }
                        }
                    }
                }
                year = yearTmp;
                month = monthTmp;
                day = dayTmp;
                if (dateStr.equals("")) {
                    dateStr = year + month + day;
                }


            }

            // 年份归一化
            ArrayList<Integer> yearNums = new ArrayList<>();
            ArrayList<Integer> monthNums = new ArrayList<>();
            ArrayList<Integer> dayNums = new ArrayList<>();

            if (!year.equals("")) {
                year = year.replaceAll("^\\|", "");
                String[] yearS = year.split("\\|");
                for (int i = 0; i < yearS.length; i++) {
                    try {
                        int yearNum = LocalDate.now().getYear();
                        if (Pattern.compile(nn).matcher(yearS[i]).lookingAt()) {
                            if (yearS[i].length() > 1) {
                                if (yearS[i].substring(0, 1).equals("近") || yearS[i].substring(0, 1).equals("去")||yearS[i].substring(0, 1).equals("前")) {
                                    if (str2int(yearS[i].substring(1, yearS[i].length()))>0.001) {
                                        yearNum = yearNum - 1 * str2int(yearS[i].substring(1, yearS[i].length()));
                                        yearNums.add(yearNum);
                                        yearNums.add(LocalDate.now().getYear());
                                        monthNums.add(LocalDate.now().getMonthValue());
                                        monthNums.add(LocalDate.now().getMonthValue());
                                        dayNums.add(LocalDate.now().getDayOfMonth());
                                        dayNums.add(LocalDate.now().getDayOfMonth());


                                    }
                                } else {
                                    // 需要考虑时间是否是未来的，或者过去的
                                    Integer a = 0;
                                    a = str2int(yearS[0].substring(0,1))>0?1:str2int(yearS[0].substring(0,1))<0?-1:0;
                                    yearNum = yearNum + a * str2int(yearS[i].substring(1, yearS[i].length()));
                                    if (a>0) {

                                        yearNums.add(LocalDate.now().getYear() + a);
                                        yearNums.add(yearNum);
                                    }
                                    else{
                                        yearNums.add(yearNum);
                                        yearNums.add(LocalDate.now().getYear() + a);

                                    }
//                                        monthNums.add(LocalDate.now().getMonthValue());
//                                        monthNums.add(LocalDate.now().getMonthValue());
//                                        dayNums.add(LocalDate.now().getDayOfMonth());
//                                        dayNums.add(LocalDate.now().getDayOfMonth());


                                }
                                continue;
                            } else {

//                                if (yearS[i].equals("前")  && subQuery.indexOf("前")!=subQuery.indexOf("年")-1 ){
//                                    continue;
//                                }
//                                if (yearS[i].equals("去")  && subQuery.indexOf("去")!=subQuery.indexOf("年")-1){
//                                    continue;
//                                }
//                                if (yearS[i].equals("前")){
//                                    yearNums.add(LocalDate.now().getYear()-2);
//                                    yearNums.add(LocalDate.now().getYear()-2);
//                                }
                                if (yearS[i].equals("近")) {
                                    yearNum = yearNum + str2int("近");
                                    yearNums.add(yearNum);
                                    yearNums.add(LocalDate.now().getYear());
                                    monthNums.add(LocalDate.now().getMonthValue());
                                    monthNums.add(LocalDate.now().getMonthValue());
                                    dayNums.add(LocalDate.now().getDayOfMonth());
                                    dayNums.add(LocalDate.now().getDayOfMonth());

                                } else {
                                    if(!subQuery.substring(subQuery.indexOf(yearS[i])+1, subQuery.indexOf(yearS[i])+2).equals("半"))
                                    {
                                        yearNum = yearNum + str2int(yearS[i]);
                                        yearNums.add(yearNum);
                                    }
                                }
                            }
                        } else if (!yearS[i].equals("")) {
                            // 3年前:
                            if (str2int(yearS[i]) < 1000 && subQuery.indexOf("年") + 1 == subQuery.indexOf("前")) {
                                yearNum = yearNum - str2int(yearS[i]);
                                yearNums.add(yearNum);
                                yearNums.add(yearNum);
                                monthNums.add(LocalDate.now().getMonthValue());
                                monthNums.add(LocalDate.now().getMonthValue());
                                dayNums.add(LocalDate.now().getDayOfMonth());
                                dayNums.add(LocalDate.now().getDayOfMonth());
                            } else {
                                yearNum = str2int(yearS[i]);
                                // 2015上半年 case
                                if (yearNum==0){
                                    Matcher m = Pattern.compile("[0-9零一二三四五六七八九十]+").matcher(yearS[i]);
                                    if (m.find()){
                                        yearNums.add(str2int(m.group()));
                                    }
                                }
                                else {
                                    yearNums.add(yearNum);
                                }
                            }
                        }

                    }
                    catch(Exception e1){
                        break;
                    }
                }

                if (yearNums.size() < 1) {
                    yearNums.add(LocalDate.now().getYear());
                    yearNums.add(LocalDate.now().getYear());
                }
                if (yearNums.size() < 2) {
                    yearNums.add(yearNums.get(0));
                }
            }

            // 月份归一化
            if (!month.equals("")) {
                month = month.replaceAll("^\\|", "");
                String[] monthS = month.split("\\|");
                for (int i = 0; i < monthS.length; i++) {
                    int monthNum = LocalDate.now().getMonthValue();
                    if (Pattern.compile(nn).matcher(monthS[i]).lookingAt()) {
                        yearFlag = true;

                        if (monthS[i].length() > 1) {
                            if (monthS[i].substring(0, 1).equals("近")|| monthS[i].substring(0, 1).equals("去")||monthS[i].substring(0, 1).equals("前")) {
                                monthNum = monthNum - 1 * str2int(monthS[i].substring(1, monthS[i].length()));
                                monthNums.add(monthNum);
                                monthNums.add(LocalDate.now().getMonthValue());
                                dayNums.add(LocalDate.now().getDayOfMonth());
                                dayNums.add(LocalDate.now().getDayOfMonth());

                            } else {
                                Integer a = 0;
                                a = str2int(monthS[0].substring(0,1))>0?1:str2int(monthS[0].substring(0,1))<0?-1:0;
                                monthNum = monthNum + a * str2int(monthS[i].substring(1, monthS[i].length()));
                                if (a>0){
                                    monthNums.add(LocalDate.now().getMonthValue()+a);
                                    monthNums.add(monthNum);
                                }
                                else
                                {
                                    monthNums.add(monthNum);
                                    monthNums.add(LocalDate.now().getMonthValue()+a);
                                }

                            }
                            continue;
                        } else {
                            // 近月
                            if (monthS[i].equals("近")) {
                                monthNum = monthNum + str2int("近");
                                monthNums.add(monthNum);
                                monthNums.add(LocalDate.now().getMonthValue());
                                dayNums.add(LocalDate.now().getDayOfMonth());
                                dayNums.add(LocalDate.now().getDayOfMonth());

                            } else {
                                monthNum = monthNum + str2int(monthS[i]);
                                monthNums.add(monthNum);
                            }
                        }
                    } else if (!monthS[i].equals("")) {
                        // 三个月前
                        if (subQuery.indexOf("月") + 1 == subQuery.indexOf("前")) {
                            yearFlag = true;

                            monthNum = monthNum - str2int(monthS[i]);
                            monthNums.add(monthNum);
                            monthNums.add(monthNum);
                            dayNums.add(LocalDate.now().getDayOfMonth());
                            dayNums.add(LocalDate.now().getDayOfMonth());


                        } else {
                            monthNum = str2int(monthS[i]);
                            monthNums.add(monthNum);
                        }

                    }

                }
                if (monthNums.size() < 1) {
                    monthNums.add(LocalDate.now().getMonthValue());
                    monthNums.add(LocalDate.now().getMonthValue());
                }
                if (monthNums.size() < 2) {
                    monthNums.add(monthNums.get(0));
                }
            }
            // 半年度归一化
            if (!hhhh.equals("")) {
                hhhh = hhhh.replaceAll("^\\|", "");
                String[] hhhhS = hhhh.split("\\|");
                if (hhhhS.length==1){
                    Integer Hnum = str2int(hhhhS[0]);
                    if (Hnum<3) {
                        monthNums.add((Hnum - 1) * 6 + 1);
                        monthNums.add(Hnum * 6);
                    }

                }


            }

            // 日归一化

            if (!day.equals("")) {
                day = day.replaceAll("^\\|", "");
                String[] dayS = day.split("\\|");
                for (int i = 0; i < dayS.length; i++) {
                    int dayNum = LocalDate.now().getDayOfMonth();
                    if (Pattern.compile(nn).matcher(dayS[i]).lookingAt()) {
                        yearFlag = true;
                        monthFlag = true;
                        if (dayS[i].length() > 1) {
                            if (dayS[i].substring(0, 1).equals("近")||dayS[i].substring(0, 1).equals("去")||dayS[i].substring(0, 1).equals("前")) {
                                dayNum = dayNum - 1 * str2int(dayS[i].substring(1, dayS[i].length())) + 1;
                                dayNums.add(dayNum);
                                dayNums.add(LocalDate.now().getDayOfMonth());
                            } else {
                                Integer a = 0;
                                a = str2int(dayS[0].substring(0,1))>0?1:str2int(dayS[0].substring(0,1))<0?-1:0;
                                dayNum = dayNum + a* str2int(dayS[i].substring(1, dayS[i].length()));
                                if (a>0) {

                                    dayNums.add(LocalDate.now().getDayOfMonth()+a);
                                    dayNums.add(dayNum);

                                }
                                else{
                                    dayNums.add(dayNum);
                                    dayNums.add(LocalDate.now().getDayOfMonth()+a);

                                }
                            }
                            continue;
                        } else {
                            if (dayS[i].equals("近")) {
                                dayNum = dayNum + str2int("近") + 1;
                                dayNums.add(dayNum);
                                dayNums.add(LocalDate.now().getDayOfMonth());
                            } else {
                                dayNum = dayNum + str2int(dayS[i]);
                            }

                        }
                    } else if (!dayS[i].equals("")) {
                        if (subQuery.indexOf("日") + 1 == subQuery.indexOf("前") || subQuery.indexOf("天") + 1 == subQuery.indexOf("前")) {
                            dayNum = dayNum - str2int(dayS[i]);
                            yearFlag = true;
                            monthFlag = true;
                        } else {
                            dayNum = str2int(dayS[i]);
                        }
                    }
                    dayNums.add(dayNum);
                }
                if (dayNums.size() < 1) {
                    dayNums.add(LocalDate.now().getDayOfMonth());
                    dayNums.add(LocalDate.now().getDayOfMonth());
                }
                if (dayNums.size() < 2) {
                    dayNums.add(dayNums.get(0));
                }
            }

            // 周归一化
            ArrayList<Integer> weekNums = new ArrayList<>();
            if (!week.equals("")) {

                week = week.replaceAll("^\\|", "");
                String[] weekS = week.split("\\|");
                for (int i = 0; i < weekS.length; i++) {
                    int weekNum = LocalDate.now().getDayOfWeek().getValue();

                    if (Pattern.compile(nn).matcher(weekS[i]).lookingAt()) {
                        yearFlag = true;
                        monthFlag = true;
                        if (weekS[i].length() > 1) {
                            if (weekS[i].substring(0, 1).equals("近") || weekS[i].substring(0, 1).equals("去")||weekS[i].substring(0, 1).equals("前")) {
                                weekNum = 0 - 7 * str2int(weekS[i].substring(1, weekS[i].length())) + 1;
                                weekNums.add(weekNum);
                                weekNums.add(0);
                            } else {
                                // 下三周
                                Integer a = 0;
                                a = str2int(weekS[0].substring(0,1))>0?1:str2int(weekS[0].substring(0,1))<0?-1:0;

                                //weekNum = -1 * weekNum - 7 * str2int(weekS[i].substring(1, weekS[i].length())) + 1;
//                                weekNum = 1 - weekNum + 7*a*(str2int(weekS[i].substring(1, weekS[i].length()))+1);
                                if(a>0) {
                                    weekNum = 1 - weekNum + 7*a*(str2int(weekS[i].substring(1, weekS[i].length()))+1);
                                    weekNums.add(1-LocalDate.now().getDayOfWeek().getValue()+ a*7);
                                    weekNums.add(weekNum-1);


                                }
                                else if (a<0){
                                    weekNum = 1 - weekNum + 7*a*str2int(weekS[i].substring(1, weekS[i].length()));
                                    weekNums.add(weekNum);
                                    weekNums.add(1-LocalDate.now().getDayOfWeek().getValue()-1);
                                }
                                else{
                                    weekNums.add(1-LocalDate.now().getDayOfWeek().getValue());
                                    weekNum = 1 - weekNum + 7*str2int(weekS[i].substring(1, weekS[i].length()))-1;
                                    weekNums.add(weekNum);


                                }

//                                weekNums.add(0);
                            }
                            continue;
                        } else {
                            if (weekS[i].equals("近")) {

                                weekNum = str2int("近") * 7 + 1;
                                weekNums.add(weekNum);
                                weekNums.add(0);
                            }
                            else {
                                weekNum = -1 * weekNum + 7 * str2int(weekS[i]) + 1;
                                weekNums.add(weekNum);
                                if (weekS[i].equals("本") || weekS[i].equals("这")||weekS[i].equals("当")||weekS[i].equals("下")){
                                    // 延伸到本周结束
                                    //weekNums.add(7);
                                    weekNums.add(7*(str2int(weekS[i])+1)-LocalDate.now().getDayOfWeek().getValue());

                                }
                                else {
                                    weekNums.add(-1 * LocalDate.now().getDayOfWeek().getValue());
                                }
                            }

                        }
                    } else if (!weekS[i].equals("")) {
                        if (subQuery.indexOf("周") + 1 == subQuery.indexOf("前") || subQuery.indexOf("星") + 2 == subQuery.indexOf("前")) {
                            weekNum = -7 * str2int(weekS[i]);
                            yearFlag = true;
                            monthFlag = true;
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

            // 季度的归一化
            // 季度归一成月份
            ArrayList<Integer> quarterNums = new ArrayList<>();
            if (!quarter.equals("")) {
                quarter = quarter.replaceAll("^\\|", "");
                String[] quarterS = quarter.split("\\|");
                for (int i = 0; i < quarterS.length; i++) {
                    int quarterNumb = LocalDate.now().getMonthValue();
                    int quarterNum = (quarterNumb - 1) / 3 + 1;
                    if (Pattern.compile(nn).matcher(quarterS[i]).lookingAt()) {
                        if (quarterS[i].length() > 1) {
                            if (quarterS[i].substring(0, 1).equals("近") || quarterS[i].substring(0, 1).equals("去")||quarterS[i].substring(0, 1).equals("前")) {

                                quarterNum = quarterNumb - 3 * str2int(quarterS[i].substring(1, quarterS[i].length()));
                                quarterNums.add(quarterNum);
                                quarterNums.add(quarterNumb);
                                dayNums.add(LocalDate.now().getDayOfMonth());
                                dayNums.add(LocalDate.now().getDayOfMonth());
                            }
                            else{
                                Integer a = 0;
                                a = str2int(quarterS[0].substring(0,1))>0?1:str2int(quarterS[0].substring(0,1))<0?-1:0;
                                quarterNumb = quarterNum + a*str2int(quarterS[i].substring(1, quarterS[i].length()));
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
                                quarterNum = quarterNumb + (-3)*str2int("近");
                                quarterNums.add(quarterNum);
                                quarterNums.add(quarterNumb);
                                dayNums.add(LocalDate.now().getDayOfMonth());
                                dayNums.add(LocalDate.now().getDayOfMonth());

                            }
                            else {

                                quarterNum = quarterNum + str2int(quarterS[i]);
                                quarterNums.add((quarterNum-1)*3+1);
                                quarterNums.add((quarterNum)*3);


                            }
                        }
                    } else if (!quarterS[i].equals("")) {
                        // 三季度前
                        if (subQuery.indexOf("季") + 2 == subQuery.indexOf("前")) {
                            quarterNum = quarterNumb - 3*str2int(quarterS[i]);
                            quarterNums.add(quarterNum);
                            quarterNums.add(quarterNum);
                            dayNums.add(LocalDate.now().getDayOfMonth());

                            dayNums.add(LocalDate.now().getDayOfMonth());

                        } else {
                            quarterNum = str2int(quarterS[i]);
                            quarterNums.add((quarterNum-1)*3+1);
                            quarterNums.add(quarterNum*3);

                        }

                    }
                    //quarterNums.add(quarterNum);
                }
                if (monthNums.size() < 1) {
//                    quarterNums.add(0);
//                    quarterNums.add(0);
                }
                if (quarterNums.size() < 2) {
                    quarterNums.add(quarterNums.get(0));
                }
            }

//        ArrayList<Integer> modNums = new ArrayList<>();
//        if (!mod.equals("")) {
//            mod = mod.replaceAll("^\\|", "");
//            String[] modS = mod.split("\\|");
//            if (modS.length==1) {
//                if (isMod(modS[0])) {
//                    Integer[] range = rangeNum.get(modS[0]);
//                    // 从范围里面确定月份和天数
//                    Integer rStartMonth = range[0] / 100;
//                    Integer rStartDay = range[0] % 100;
//                    Integer rEndMonth = range[1] / 100;
//                    Integer rEndDay = range[1] % 100;
//
//                }
//            }
//
//        }
            Integer yearStart = 0;
            Integer yearEnd = 0;
            Integer monthStart = -9999;
            Integer monthEnd = -9999;
            Integer dayStart = -9999;
            Integer dayEnd = -9999;
            System.out.println(String.format("year:%s---month:%s---day:%s---week:%s----mod:%s---quarter:%s", year, month, day, week, mod, quarter));

            // 强制改变年份
            if(!getHistoryYear(subQuery).equals("")){
                // 是不是历史年份
                dateStr = getHistoryYear(subQuery);
                yearStart = LocalDate.now().getYear()-3;
                yearEnd = LocalDate.now().getYear();
                monthStart = LocalDate.now().getMonthValue();
                monthEnd = LocalDate.now().getMonthValue();
                dayStart = LocalDate.now().getDayOfMonth();
                dayEnd = LocalDate.now().getDayOfMonth();
            }
            else if (yearNums.size() > 0) {
                yearFlag = true;
//                monthFlag = true;
                yearStart = yearNums.get(0);
                yearEnd = yearNums.get(1);
                if ((yearStart < 100) && (yearStart <= LocalDate.now().getYear() - 2000 + 3)) {
                    yearStart = yearStart + 2000;
                    yearEnd = yearEnd + 2000;
                } else if (yearStart < 100) {
                    yearStart = yearStart + 1900;
                    yearEnd = yearEnd + 1900;
                }
                //上半年，下半年强制设置月份，日
                if (!getHC(subQuery).equals("")){
                    Integer monthR = 0;
                    Integer dayR = 0;
                    Integer monthRend = 0;
                    Integer dayRend = 0;
                    String modName = getHC(subQuery);
                    Integer[] modRange = hc.get(modName);
                    monthR = modRange[0] / 100;
                    monthRend = modRange[1] / 100;
                    dayR = modRange[0] % 100;
                    dayRend = modRange[1] % 100;
                    if (monthR > 0) {
                        monthStart = monthR;
                        monthEnd = monthRend;

                    }
                    if (dayR > 0) {
                        dayStart = dayR;
                        dayEnd = dayRend;
                    }
                }

            }

            // 季度修改
            if (quarterNums.size() == 2) {
                int starQuarter = quarterNums.get(0);
                int endQuarter = quarterNums.get(1);
//                monthStart = (starQuarter - 1) * 3 + 1;
//                monthEnd = endQuarter * 3;
//                dayStart = 1;
//                dayEnd = 31;

                monthStart = starQuarter;
                monthEnd = endQuarter;
                if(dayNums.size()!=2) {
                    dayStart = 1;
                    dayEnd = 31;
                }

            } else if (monthNums.size() > 0) {
                monthFlag = true;
                monthStart = monthNums.get(0);
                monthEnd = monthNums.get(1);
            }

            if (dayNums.size() > 0) {
                dayStart = dayNums.get(0);
                dayEnd = dayNums.get(1);
            }

            //  修饰语
            mod = mod.replaceAll("^\\|", "");
            String[] mods = mod.split("\\|");
            for(int jj=0;jj<mods.length;jj++){
                try {
                    if (isMod(mods[jj])) {
                        Integer monthR = 0;
                        Integer dayR = 0;
                        Integer monthRend = 0;
                        Integer dayRend = 0;
                        String modName = "";
                        if (mods[jj].length() == 1) {
                            int modIndex = subQuery.indexOf(mods[jj]);
                            modName = subQuery.substring(modIndex - 1, modIndex + 1);
                        } else {
                            modName = mods[jj];
                        }
                        Integer[] modRange = rangeNum.get(modName);
                        monthR = modRange[0] / 100;
                        monthRend = modRange[1] / 100;
                        dayR = modRange[0] % 100;
                        dayRend = modRange[1] % 100;
                        if (monthR > 0) {
                            monthStart = monthR;
                            monthEnd = monthRend;

                        }
                        if (dayR > 0) {
                            dayStart = dayR;
                            dayEnd = dayRend;
                        }
                    }
                }
                catch (Exception e){
                    logger.info("修饰词有问题");
                }

            }
            // 自动填充
            if (yearStart < 1 && monthStart < -9998 && dayStart < -9998 && weekNums.size()<1 ) {
                return new MarketDate();
            } else {
                if (yearStart < 1) {
                    yearStart = LocalDate.now().getYear();
                    yearEnd = LocalDate.now().getYear();
                } else if (yearStart > 0 && monthStart < -9998 && dayStart < -9998) {
                    monthStart = 1;
                    monthEnd = 12;
                    dayStart = 1;
                    dayEnd = 31;
                }


                if (monthStart < 1 && yearEnd < LocalDate.now().getYear() && dayStart < -9998) {
                    monthStart = 1;
                    monthEnd = 12;
                    dayStart = 1;
                    dayEnd = 31;
                } else if (monthStart < -9998) {
//                if (dayEnd>LocalDate.now().getDayOfMonth()) {
//                    monthStart = LocalDate.now().getMonthValue()-1;
//                    monthEnd = LocalDate.now().getMonthValue()-1;
//                }
//                else{
//                    monthStart = LocalDate.now().getMonthValue();
//                    monthEnd = LocalDate.now().getMonthValue();
//                }
                    monthStart = LocalDate.now().getMonthValue();
                    monthEnd = LocalDate.now().getMonthValue();
                } else if (monthStart > -9999 && monthStart < 1) {
//                monthStart = 12-Math.abs(monthStart)%12;
//                yearStart = yearStart-Math.abs(monthStart)/12-1;
                }
//                if (dayStart < -9998 && (monthEnd < LocalDate.now().getMonthValue() || yearEnd < LocalDate.now().getYear())) {
                if (dayStart < -9998 && weekNums.size()<1) {
                    dayStart = 1;
                    dayEnd = 31;
                } else if (dayStart < -9998) {
                    dayStart = LocalDate.now().getDayOfMonth();
                    dayEnd = LocalDate.now().getDayOfMonth();
                } else if (dayStart > -9999 && dayStart < 1) {
//                dayStart = 31-Math.abs(dayStart)%31;
//                monthStart = monthStart-Math.abs(monthStart)/31-1;
                }



                // 先生成标准日期格式：
                Calendar startDate = Calendar.getInstance();

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, yearStart);
                cal.set(Calendar.MONTH, monthStart - 1);
                // 某年某月的最后一天
                if (dayStart > cal.getActualMaximum(Calendar.DATE)) {
                    dayStart = cal.getActualMaximum(Calendar.DATE);
                }
                startDate.set(yearStart, monthStart - 1, dayStart);
                cal.set(Calendar.YEAR, yearEnd);
                cal.set(Calendar.MONTH, monthEnd - 1);
                if (dayEnd > cal.getActualMaximum(Calendar.DATE)) {
                    dayEnd = cal.getActualMaximum(Calendar.DATE);
                }
                Calendar endDate = Calendar.getInstance();
                endDate.set(yearEnd, monthEnd - 1, dayEnd);

                // 几周前的修饰
                if (weekNums.size() > 1) {
                    Integer deltaStartday = weekNums.get(0);
                    Integer deltaendDay = weekNums.get(1);

                    startDate.add(startDate.DATE, deltaStartday);
                    endDate.add(endDate.DATE, deltaendDay);

                }

                Date startTime = startDate.getTime();
                Date endTime = endDate.getTime();

//      System.out.println(String.format("yearStart:%d---yearEnd:%d", yearStart,yearEnd));
//      System.out.println(String.format("monthStart:%d---monthEnd:%d",  monthStart, monthEnd));
//      System.out.println(String.format("dayStart:%d---dayEnd:%d",dayStart, dayEnd));
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
//            System.out.println(ft.format(startTime) + " " + ft.format(endTime));

                //return ft.format(startTime) + " " + ft.format(endTime)+" "+dateStr+"\t"+yearFlag+"__"+monthFlag;
                return new MarketDate(ft.format(startTime),ft.format(endTime),dateStr,yearFlag,monthFlag);
            }
        }
        catch (Exception e1) {
//                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
//                return ft.format(LocalDate.now()) + " " + ft.format(LocalDate.now());
            return new MarketDate();
        }
    }

    public  List<MarketDate> getHardTime(String query){

        List<MarketDate> result = new ArrayList<>();

        try {

            String patternFormat0 = "([0-9一两〇二三四五六七八九十零]{1,4})(至|到)([0-9一两〇二三四五六七八九十零]{1,4})(日|号)";
            String patternFormat1 = "(([0-9一两〇二三四五六七八九十零]{2,4})年?).?([0-9一两〇二三四五六七八九十零])(至|到).?([0-9一两〇二三四五六七八九十零])季";
            String patternFormat2 = "([1-4qhHQ]{2})\\s?([0-9]{2,4})";
//            String patternFormat3 = "([0-9]{2,4})年?\\s?(Q|q|H|h)([1-4])";
            String patternFormat3 = "([0-9]{2,4})年?\\s?([1-4qhHQ]{2})";
            Pattern p0 = Pattern.compile(patternFormat0);
            Pattern p1 = Pattern.compile(patternFormat1);
            Pattern p2 = Pattern.compile(patternFormat2);
            Pattern p3 = Pattern.compile(patternFormat3);
            Matcher m0 = p0.matcher(query);
            Matcher m1 = p1.matcher(query);
            Matcher m2 = p2.matcher(query);
            Matcher m3 = p3.matcher(query);
            if (m0.find()) {
                Integer dayStart = str2int(m0.group(1));
                Integer dayEnd = str2int(m0.group(3));
                String dateStr1 = m0.group(1);
                String dateStr2 = m0.group(3);
                String startTime = getNormalTime(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), dayStart);
                String endTime = getNormalTime(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), dayEnd);
                MarketDate md = new MarketDate(startTime, startTime, dateStr1, false, false);
                MarketDate md1 = new MarketDate(endTime, endTime, dateStr2, false, false);
                result.add(md);
                result.add(md1);
                return result;

            } else if (m1.find()) {
                Integer year = m1.group(2).equals(null) ? LocalDate.now().getYear() : str2int(m1.group(2));
                Integer monthStart = str2int(m1.group(3));
                Integer monthEnd = str2int(m1.group(5));
                String monthStr1 = m1.group(3);
                String monthStr2 = m1.group(5);
                if (monthStart > 4 || monthEnd > 4) {
                    return result;
                } else {
                    String startTime = getNormalTime(year, (monthStart - 1) * 3 + 1, 1);
                    String endTime = getNormalTime(year, monthEnd * 3, 31);
                    MarketDate md = new MarketDate(startTime, startTime, monthStr1, false, false);
                    MarketDate md1 = new MarketDate(endTime, endTime, monthStr2, false, false);
                    result.add(md);
                    result.add(md1);
                    return result;

                }

            }
            else if(m2.find()){
                Integer year = str2int(m2.group(2));
                Integer monthStart=0;
                Integer monthEnd=0;
                if(year<=LocalDate.now().getYear()%100) {
                    year = 2000 + year;
                }
                else if(year>1000){
                    year = year;
                }
                else{
                    // 异常
                    return result;
                }
                String hitS= m2.group(1);
                String flagQH = "";
                Integer numQH =0;
                Matcher m = Pattern.compile("[qQHh]").matcher(hitS);
                if(m.find()){
                    flagQH = m.group();
                    numQH = str2int(hitS.substring(1-m.start(),2-m.start()));
                }
                else{
                    return result;
                }
                if (flagQH.equals("Q") || flagQH.equals("q")) {
                    monthStart = (numQH - 1) * 3 + 1;
                    monthEnd = numQH* 3;
                    if (monthStart >12 && monthEnd >12){
                        // 异常
                        return result;
                    }
                }

                if(flagQH.equals("H") || flagQH.equals("h")){
                    monthStart = (numQH - 1) * 6 + 1;
                    monthEnd = numQH * 6;
                    if (monthStart >12 && monthEnd >12){
                        // 异常
                        return result;
                    }

                }
                String dateStr1 = m2.group();
                String dateStr2 = dateStr1;
                String startTime = getNormalTime(year, monthStart, 1);
                String endTime = getNormalTime(year, monthEnd, 31);
                MarketDate md = new MarketDate(startTime, startTime, dateStr1, false, false);
                MarketDate md1 = new MarketDate(endTime, endTime, "", false, false);
                result.add(md);
                result.add(md1);
                return result;
            }else if(m3.find()){

                Integer year = str2int(m3.group(1));
                Integer monthStart=0;
                Integer monthEnd=0;
                if(year<=LocalDate.now().getYear()%100) {
                    year = 2000 + year;
                }
                else if(year>1000){
                    year = year;
                }
                else{
                    // 异常
                    return result;
                }
                String hitS= m3.group(2);
                String flagQH = "";
                Integer numQH =0;
                Matcher m = Pattern.compile("[qQHh]").matcher(hitS);
                if(m.find()){
                    flagQH = m.group();
                    numQH = str2int(hitS.substring(1-m.start(),2-m.start()));
                }
                else{
                    return result;
                }

                if (flagQH.equals("Q") || flagQH.equals("q")) {
                    monthStart = (numQH - 1) * 3 + 1;
                    monthEnd = numQH  * 3;
                    if (monthStart >12 && monthEnd >12){
                        // 异常
                        return result;
                    }
                }

                if(flagQH.equals("H") || flagQH.equals("h")){
                    monthStart = (numQH - 1) * 6 + 1;
                    monthEnd = numQH * 6;
                    if (monthStart >12 && monthEnd >12){
                        // 异常
                        return result;
                    }

                }
                String dateStr1 = m3.group();
                String dateStr2 = dateStr1;
                String startTime = getNormalTime(year, monthStart, 1);
                String endTime = getNormalTime(year, monthEnd, 31);
                MarketDate md = new MarketDate(startTime, startTime, dateStr1, false, false);
                MarketDate md1 = new MarketDate(endTime, endTime, "", false, false);
                result.add(md);
                result.add(md1);
                return result;

            }
            else {
                return result;
            }
        }
        catch ( Exception  e){
            logger.error("hard time error!");
            return result;

        }

    }

    public  List<MarketDate> updateTime(String query){
        List<MarketDate> result = new ArrayList<MarketDate>();
        // 优先走hard code逻辑
        List<MarketDate> hardRes = getHardTime(query);
        if (hardRes.size()>0){
            return hardRes;
        }
        else {
            try {
                String[] querys = query.split("到|至");
                MarketDate date1 = new MarketDate();
                MarketDate date2 = new MarketDate();
                // 只有一个时间
                if (querys.length == 1) {
                    date1 = getDateTime(querys[0]);
                }
                // 两个时间
                else if (querys.length > 1) {
                    date1 = getDateTime(querys[0]);
                    date2 = getDateTime(querys[1]);
                    if (!date1.isNull() && !date2.isNull()) {
                        // 前一个时间有年份，后一个时间没有年份
//                    if (date1.split("\t")[1].equals("true") && date2.split("\t")[1].equals("false")) {
//                        String newYear = date1.substring(0, 4);
//                        date2 = newYear + date2.substring(4, date2.length());
//                        date2 = date2.substring(0, 11) + newYear + date2.substring(15, date2.length());
//                    }
                        if (date1.isYearFlag() && !date2.isYearFlag()) {
                            String newYear = date1.getStartDate().substring(0, 4);
                            String date2S = date2.getStartDate();
                            String date2E = date2.getEndDate();
                            date2S = newYear + date2S.substring(4);
                            date2E = newYear + date2E.substring(4);
                            date2.setStartDate(date2S);
                            date2.setEndDate(date2E);
                        }
                        if (date1.isMonthFlag() && !date2.isMonthFlag()) {
                            String newMonth = date1.getStartDate().substring(5, 7);
                            String date2S = date2.getStartDate();
                            String date2E = date2.getEndDate();
                            date2S = date2S.substring(0, 5) + newMonth + date2S.substring(7);
                            date2E = date2E.substring(0, 5) + newMonth + date2E.substring(7);
                            date2.setStartDate(date2S);
                            date2.setEndDate(date2E);
                        }

                    }
                }

                if (date2.isNull()) {
                    result.add(date1);
                } else if (!date1.isNull() && !date2.isNull()) {
                    result.add(date1);
                    result.add(date2);
                } else {

                    result.add(date1);
                }

                return result;

            } catch (Exception e) {
                logger.error("抽取时间异常" + e);
                return result;
            }
        }

    }

}