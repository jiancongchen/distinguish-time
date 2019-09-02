package com.tigerobo.distinguishtime.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

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

    private String halfYear;

    public TimeInformation() {
        this.year = "";
        this.month = "";
        this.day = "";
        this.week = "";
        this.qurater = "";
        this.modify = "";
        this.halfYear = "";
    }

    /**
     * 判断是否没有获取任何的时间信息
     * @param timeInformation
     * @return
     */
    public static boolean isEmpty(TimeInformation timeInformation){
        if(timeInformation == null){
            return true;
        }
        boolean flag = StringUtils.isBlank(timeInformation.getYear()) && StringUtils.isBlank(timeInformation.getMonth())
                && StringUtils.isBlank(timeInformation.getDay()) && StringUtils.isBlank(timeInformation.getWeek()) &&
                StringUtils.isBlank(timeInformation.getWeek()) && StringUtils.isBlank(timeInformation.getModify()) &&
                StringUtils.isBlank(timeInformation.getHalfYear());
        if(flag){
            return true;
        }
        return false;
    }
}
