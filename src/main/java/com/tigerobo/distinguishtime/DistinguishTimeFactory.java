package com.tigerobo.distinguishtime;

import com.tigerobo.distinguishtime.model.TimeInformation;
import com.tigerobo.distinguishtime.model.TimeRange;

/**
 * @author : jiancongchen on 2019-09-02
 **/
public class DistinguishTimeFactory {

    private static final int IS_OTHER = 4;

    public static TimeRange distinguishTime(String statement){

        OtherTime otherTime = new OtherTime(statement);
        int matchResult = otherTime.isMatch(statement);
        if( matchResult >= IS_OTHER){
            return otherTime.getDateFormat(matchResult);
        }

        DateContainsUnit dateContainsUnit = new DateContainsUnit(statement);
        TimeInformation timeInformation = dateContainsUnit.formatLanguageTime();
        if(!TimeInformation.isEmpty(timeInformation)){
            return dateContainsUnit.getStandardTime(timeInformation);
        }

        FormatDateTime formatDateTime = new FormatDateTime(statement);
        timeInformation = formatDateTime.formatLanguageTime();
        if(!TimeInformation.isEmpty(timeInformation)){
            return formatDateTime.getStandardTime(timeInformation);
        }

        PureNumberDate pureNumberDate = new PureNumberDate(statement);
        timeInformation = pureNumberDate.formatLanguageTime();
        if(!TimeInformation.isEmpty(timeInformation)){
            return pureNumberDate.getStandardTime(timeInformation);
        }
        return null;
    }
}
