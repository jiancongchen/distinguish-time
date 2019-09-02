package com.tigerobo.distinguishtime;


import com.tigerobo.distinguishtime.model.TimeInformation;
import com.tigerobo.distinguishtime.model.TimeRange;

import java.util.List;

public interface DistinguishTime {

    /**
     * 将自然语言处理成包含时间信息的形式
     *
     * 根据不同的语言习惯，进行不同的转换
     * 可以不断扩展，提高语句识别的能力
     * @return
     */
    TimeInformation fomatLanguageTime();

    /**
     * 将包含时间信息的类，格式化成标准的时间格式
     * @param timeInformation
     * @return
     */
    List<TimeRange> getStandardTime(TimeInformation timeInformation);
}
