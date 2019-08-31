package com.tigerobo.datetime;

public class MarketDate {
    private String startDate;
    private String endDate;
    private String dateStr;
    private boolean yearFlag;
    private boolean monthFlag;

    public MarketDate(){
        startDate = "";
        endDate = "";
        dateStr = "";
        yearFlag = false;
        monthFlag = false;
    }
    public MarketDate(String startDate, String endDate, String dateStr, boolean yearFlag, boolean monthFlag){
        this.startDate = startDate;
        this.endDate = endDate;
        this.dateStr = dateStr;
        this.yearFlag = yearFlag;
        this.monthFlag = monthFlag;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public boolean isYearFlag() {
        return yearFlag;
    }

    public void setYearFlag(boolean yearFlag) {
        this.yearFlag = yearFlag;
    }

    public boolean isMonthFlag() {
        return monthFlag;
    }

    public void setMonthFlag(boolean monthFlag) {
        this.monthFlag = monthFlag;
    }

    public boolean isNull(){
        if(this.startDate.equals("")){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public String toString() {
        return "MarketDate{" +
                "startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", dateStr='" + dateStr + '\'' +
                ", yearFlag=" + yearFlag +
                ", monthFlag=" + monthFlag +
                '}';
    }
}
