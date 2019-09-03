package com.tigerobo.distinguishtime.format2standard;

import com.tigerobo.distinguishtime.ChangeTextToNum;
import com.tigerobo.distinguishtime.ConstantPattern;
import com.tigerobo.distinguishtime.model.TimeRange;

/**
 * @author : jiancongchen on 2019-09-03
 **/
public class ModifyStandard {

    /**
     *
     * 对于月末之类对修饰词进行处理
     * @param timeRange
     * @param mod
     * @param statement
     * @return
     */
    public static TimeRange getModify(TimeRange timeRange, String mod, String statement){
        mod = mod.replaceAll("^\\|", "");
        String[] mods = mod.split("\\|");
        for(int jj=0;jj<mods.length;jj++){
            try {
                if (ConstantPattern.isMod(mods[jj])) {
                    int monthR;
                    int dayR;
                    int monthRend ;
                    int dayRend;
                    String modName = "";
                    if (mods[jj].length() == 1) {
                        int modIndex = statement.indexOf(mods[jj]);
                        modName = statement.substring(modIndex - 1, modIndex + 1);
                    } else {
                        modName = mods[jj];
                    }
                    Integer[] modRange = ChangeTextToNum.RANG_NUM.get(modName);
                    monthR = modRange[0] / 100;
                    monthRend = modRange[1] / 100;
                    dayR = modRange[0] % 100;
                    dayRend = modRange[1] % 100;
                    if (monthR > 0) {
                        timeRange.setStartMonth(monthR);
                        timeRange.setEndMonth(monthRend);
                    }
                    if (dayR > 0) {
                        timeRange.setStartDay(dayR);
                        timeRange.setEndDay(dayRend);
                    }
                }
            }
            catch (Exception e){
                System.out.print("修饰词异常");
                e.printStackTrace();
            }
        }
        return timeRange;
    }
}
