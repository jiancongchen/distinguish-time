package com.tigerobo;

import com.tigerobo.datetime.DateTime;
import com.tigerobo.distinguishtime.DistinguishTimeFactory;
import com.tigerobo.distinguishtime.model.TimeRange;

/**
 * @author : jiancongchen on 2019-09-02
 **/
public class TestTime {

    public static void main(String[] args) {
        String statement = "2018年10月10至19日";
        DateTime dateTime = new DateTime();
        System.out.println(dateTime.getDateTime(statement));
        System.out.println();
        System.out.println(dateTime.getHardTime(statement));
        System.out.println();
        for (TimeRange timeRange : DistinguishTimeFactory.distinguishTime(statement)){
            System.out.println(timeRange);
        }
    }
}
