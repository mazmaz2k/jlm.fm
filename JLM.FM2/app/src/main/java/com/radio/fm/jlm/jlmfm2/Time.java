package com.radio.fm.jlm.jlmfm2;

import java.util.Calendar;

/**
 * Created by Omri on 06/06/2017.
 */

public class Time
{
    public int getMinutes() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }
    public int getHours(){
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }
    public int getSecond(){
        return Calendar.getInstance().get(Calendar.SECOND);
    }
}
