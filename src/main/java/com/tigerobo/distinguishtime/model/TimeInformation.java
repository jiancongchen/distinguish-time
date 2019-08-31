package com.tigerobo.distinguishtime.model;

import lombok.Data;

/**
 * @author : jiancongchen on 2019-08-31
 **/
@Data
public class TimeInformation {

    private String year;

    private String month;

    private String day;

    private String week;

    /**
     * 季度
     */
    private String qurater;

    /**
     * 修饰词
     */
    private String modify;
}
