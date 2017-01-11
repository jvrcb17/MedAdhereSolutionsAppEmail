package com.solutions.medadhere.medadheresolutionsapp;

import java.util.Calendar;

/**
 * Created by Scott on 11/20/2016.
 */

public class TimeLog {

    public String TimeStamp;
    public String DateStamp;

    public TimeLog() {
        //default constructor because why not
    }

    //public TimeLog(String TimeStamp, String DateStamp) {
    //    this.TimeStamp = TimeStamp;
    //    this.DateStamp = DateStamp;
    //}


    public String getTimeStamp() {
        Calendar rightNow = Calendar.getInstance();


        int ihour = rightNow.get(Calendar.HOUR_OF_DAY);
        int iminute = rightNow.get(Calendar.MINUTE);
        int isecond = rightNow.get(Calendar.SECOND);
        String hour;
        if (ihour<10) {
            hour = "0" + Integer.toString(ihour);
        }else
        {
            hour = Integer.toString(ihour);
        }
        String minute;
        if (iminute<10) {
            minute = "0" + Integer.toString(iminute);
        }else
        {
            minute = Integer.toString(iminute);
        }

        String second;
        if (iminute<10) {
            second = "0" + Integer.toString(isecond);
        }else
        {
            second = Integer.toString(isecond);
        }
        TimeStamp = hour + ":" + minute +":"+second;

        return TimeStamp;

        /*
        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);

            if (Build.VERSION.SDK_INT >= 23 ) {
            time = timePicker.getHour() + ":";
            int minute = timePicker.getMinute();
            if (minute <10){
                time+="0"+minute;
            }
            else{
                time+=minute;
            }

        } else {
            time = timePicker.getCurrentHour() + ":";
            int minute = timePicker.getCurrentMinute();
            if (minute <10){
                time+="0"+minute;
            }
            else{
                time+=minute;
            }
        }
        */

    }

    public String getDateStamp() {
        Calendar rightNow = Calendar.getInstance();
        int iyear = rightNow.get(Calendar.YEAR);
        int imonth = rightNow.get(Calendar.MONTH) + 1;
        int iday = rightNow.get(Calendar.DAY_OF_MONTH);

        String year = Integer.toString(iyear);
        String month;
        if (imonth<10) {
            month = "0" + Integer.toString(imonth);
        }else
        {
            month = Integer.toString(imonth);
        }
        String day;
        if (iday<10) {
            day = "0" + Integer.toString(iday);
        }else
        {
            day = Integer.toString(iday);
        }
        DateStamp = year + "-" + month + "-" + day;

        return DateStamp;
    }

}
