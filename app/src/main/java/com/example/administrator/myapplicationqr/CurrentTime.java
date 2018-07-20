package com.example.administrator.myapplicationqr;

import java.util.Calendar;
import java.util.TimeZone;

public class CurrentTime {

    int day,month,year,hour,min;
    Calendar calendar;

    public CurrentTime(){
        this.calendar = Calendar.getInstance(TimeZone.getDefault());
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.month = calendar.get(Calendar.MONTH);
        this.year = calendar.get(Calendar.YEAR);
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.min = calendar.get(Calendar.MINUTE);
    }

    public int getDay(){
        return day;
    }
    public int getMonth(){
        return month;
    }
    public int getYear(){
        return year;
    }
    public int getHour(){
        return hour;
    }
    public int getMin(){
        return min;
    }

}
