package com.tigerobo;

import com.tigerobo.datetime.DateTime;
import com.tigerobo.distinguishtime.DistinguishTimeFactory;

/**
 * @author : jiancongchen on 2019-09-02
 **/
public class TestTime {

    public static void main(String[] args) {
        String statement = "我想要2018年至2019年以及2020年的华北地区的螺纹钢产量";
        DateTime dateTime = new DateTime();
        System.out.println(dateTime.getDateTime(statement));
        System.out.println();
        System.out.println(dateTime.getHardTime(statement));
        System.out.println();
        System.out.println(DistinguishTimeFactory.distinguishTime(statement));

    }
}
