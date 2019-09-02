package com.tigerobo.distinguishtime.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

/**
 * @author : jiancongchen on 2019-08-31
 **/
@Data
public class TimeRange {

    private Date startDate;

    private Date endDate;

    private int startYear;

    private int endYear;

    private int startMonth;

    private int endMonth;

    private int startDay;

    private int endDay;

    public TimeRange(){
        Date currDate = new Date();
        LocalDate localDate = LocalDate.now();
        this.startDate = currDate;
        this.endDate = currDate;
        this.startYear = localDate.getYear();
        this.endYear = localDate.getYear();
        this.startMonth = localDate.getMonthValue();
        this.endMonth = localDate.getMonthValue();
        this.startDay = localDate.getDayOfMonth();
        this.endDay = localDate.getDayOfMonth();
    }

    @Override
    public String toString() {
        return "TimeRange{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", startYear=" + startYear +
                ", endYear=" + endYear +
                ", startMonth=" + startMonth +
                ", endMonth=" + endMonth +
                ", startDay=" + startDay +
                ", endDay=" + endDay +
                '}';
    }
}
