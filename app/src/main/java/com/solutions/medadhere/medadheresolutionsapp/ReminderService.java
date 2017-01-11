package com.solutions.medadhere.medadheresolutionsapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ReminderService extends IntentService
{

    public static final String MED_FILENAME = "med_file";
    public static final String FREQ_FILENAME = "freq_file";
    public static final String PREFS_UID = "MyPrefsFile";
    ArrayList<String> medicationList = new ArrayList<>();
    ArrayList<String> freqList = new ArrayList<>();
    Context ctx;
    String USER_FILENAME = "user_file";
    String printedMeds;
    String tenAmMeds="";
    String twoPmMeds="";
    String eightPmMeds="";

    public ReminderService() {
        super("ReminderService");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */


    //@Override
    protected void onHandleIntent(Intent intent) {
        try {
            FileInputStream fin = openFileInput(MED_FILENAME);
            int c;
            String temp="";
            while( (c = fin.read()) != -1){
                temp = temp + Character.toString((char)c);
            }
            //Log.e("Login Attempt", temp);
            if (temp.length()>1){
                //Log.e("temp",temp);
                while(temp.indexOf("\n")!=-1){
                    medicationList.add(temp.substring(0,temp.indexOf("\n")));
                    temp = temp.substring(temp.indexOf("\n")+1,temp.length());
                }
                medicationList.add(temp.substring(0,temp.length()));
                //Log.e("index",Integer.toString(temp.indexOf("\n")));
                //Log.e("medList",medicationList.toString());
            }
            fin.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileInputStream finfreq = openFileInput(FREQ_FILENAME);
            int c;
            String temp2="";
            while( (c = finfreq.read()) != -1){
                temp2 = temp2 + Character.toString((char)c);
            }
            //Log.e("Login Attempt", temp);
            if (temp2.length()>1){
                //Log.e("temp2",temp2);
                while(temp2.indexOf("\n")!=-1){
                    freqList.add(temp2.substring(0,temp2.indexOf("\n")));
                    temp2 = temp2.substring(temp2.indexOf("\n")+1,temp2.length());
                }
                freqList.add(temp2.substring(0,temp2.length()));
                //Log.e("index",Integer.toString(temp2.indexOf("\n")));
                //Log.e("freqList",freqList.toString());
            }
            finfreq.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.e("medArrayFinal",medicationList.toString());

        //Log.e("medFreqFinal",freqList.toString());

        //first notification at 10 AM next day
        Calendar cur_cal = new GregorianCalendar();
        cur_cal.setTimeInMillis(System.currentTimeMillis());//set the current time and date for this calendar

        for (int i=0;i<freqList.size();i++){
            if(freqList.get(i).equals("Daily")){
                if(tenAmMeds=="") {
                    tenAmMeds = tenAmMeds.concat(medicationList.get(i));
                }
                else{
                    tenAmMeds = tenAmMeds.concat(", ").concat(medicationList.get(i));
                }
            }
            else if(freqList.get(i).equals("Twice daily")){
                if(tenAmMeds=="") {
                    tenAmMeds = tenAmMeds.concat(medicationList.get(i));
                }
                else{
                    tenAmMeds = tenAmMeds.concat(", ").concat(medicationList.get(i));
                }
                if(eightPmMeds=="") {
                    eightPmMeds = eightPmMeds.concat(medicationList.get(i));
                }
                else{
                    eightPmMeds = eightPmMeds.concat(", ").concat(medicationList.get(i));
                }
            }
            else{
                if(tenAmMeds=="") {
                    tenAmMeds = tenAmMeds.concat(medicationList.get(i));
                }
                else{
                    tenAmMeds = tenAmMeds.concat(", ").concat(medicationList.get(i));
                }
                if(eightPmMeds=="") {
                    eightPmMeds = eightPmMeds.concat(medicationList.get(i));
                }
                else{
                    eightPmMeds = eightPmMeds.concat(", ").concat(medicationList.get(i));
                }
                if(twoPmMeds=="") {
                    twoPmMeds = twoPmMeds.concat(medicationList.get(i));
                }
                else{
                    twoPmMeds = twoPmMeds.concat(", ").concat(medicationList.get(i));
                }
            }
        }
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());




        Intent alarmIntent = new Intent(this, MyAlarmReceiver.class);
        alarmIntent.putExtra("type", 1);
        final int _id = (int) cal.getTimeInMillis();
        Log.e("Alarm10","Set");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, _id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1691882881, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        cal.set(Calendar.HOUR_OF_DAY, 10); //18:32
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        //Set repeating every 15 minutes
        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),1000*60*10, pendingIntent);


        if(twoPmMeds!="") {
            //second notification at 2 PM next day
            cal.set(Calendar.HOUR_OF_DAY, 14); //18:32
            cal.set(Calendar.MINUTE, 0);
            alarmIntent = new Intent(this, MyAlarmReceiver.class);
            alarmIntent.putExtra("type", 2);
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            final int _id1 = (int) cal.getTimeInMillis();
            pendingIntent = PendingIntent.getBroadcast(this, _id1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
            //Set repeating every 10 minutes
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),1000*60*10, pendingIntent);
            Log.e("Alarm2","Set");
        }
        if (eightPmMeds!="") {
            //third notification at 8 PM next day
            cal.set(Calendar.HOUR_OF_DAY, 20); //18:32
            cal.set(Calendar.MINUTE, 0);
            alarmIntent = new Intent(this, MyAlarmReceiver.class);
            alarmIntent.putExtra("type", 3);
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            final int _id2 = (int) cal.getTimeInMillis();
            pendingIntent = PendingIntent.getBroadcast(this, _id2, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
            //Set repeating every 10 minutes
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),1000*60*10, pendingIntent3);
            Log.e("Alarm8","Set");
        }

        int typeOfNotification =  intent.getIntExtra("type", 0);



        SharedPreferences settings = getSharedPreferences(PREFS_UID, 0);
        //there should be a second settings for the medications list
        String UIDstored = settings.getString("UID", "Default");
        //Log. d("UID", UIDstored);
        if(typeOfNotification==1){
            Log.e("Print","10AM");
            printedMeds = tenAmMeds;
        }
        else if(typeOfNotification==2){
            Log.e("Print","2PM");
            printedMeds = twoPmMeds;
        }
        else if (typeOfNotification==3){
            Log.e("Print","8PM");
            printedMeds = eightPmMeds;
        }
        else if(typeOfNotification==4){
            Log.e("canceled","alarm4");
            alarmManager.cancel(pendingIntent);
        }
        else if(typeOfNotification==5){
            Log.e("canceled","alarm5");
            alarmManager.cancel(pendingIntent);
        }
        else if(typeOfNotification==6){
            Log.e("canceled","alarm6");
            alarmManager.cancel(pendingIntent);
        }




        if (typeOfNotification==1|typeOfNotification==2|typeOfNotification==3) {

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.health_logo))//
                    .setSmallIcon(R.drawable.ic_menu_send)
                    .setContentTitle("AMC-n-ME")
                    .setContentText("Please take your medication(s): " + printedMeds);

            Intent resultIntent = new Intent(this, LoginActivity.class);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);
            int mNotificationId = typeOfNotification+4;
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }
        stopService(intent);

    }
}