package com.solutions.medadhere.medadheresolutionsapp;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Yeshy on 3/11/2016.
 */
public class MyApplication extends Application{
    private DatabaseReference mDatabase;
    private String uid;
    private String email;
    private String bloodPressureGoal;
    private String clinicName;
    private String pharmaName;
    private String pharmaNumber;


    private String literacydate;
    private String lifestyledate;
    private String adherencedate;

    private int[] lifestyleSurveyAnswers = {0,0,0,0,0,0,0,0};
    private int[] adherenceSurveyAnswers = {0,0,0,0,0,0,0,0};
    private int[] literacySurveyAnswers = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};


    private ArrayList<String> freqList = new ArrayList<>();
    private ArrayList<String> medsList = new ArrayList<>();


    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }


    public void setBloodPressureGoal(String bloodPressureGoal) {
        this.bloodPressureGoal = bloodPressureGoal;
    }

    public String getBloodPressureGoal() {
        return bloodPressureGoal;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setPharmaName(String pharmaName) {
        this.pharmaName = pharmaName;
    }

    public String getPharmaName() {
        return pharmaName;
    }

    public void setPharmaPhone(String pharmaNumber) {
        this.pharmaNumber = pharmaNumber;
    }

    public String getPharmaPhone() {
        return pharmaNumber;
    }

    //*********************************

    public void setMeds(ArrayList<String> medicine){
        this.medsList = medicine;

    }

    public ArrayList<String> getMeds(){
        return this.medsList;

    }

    public void addFrequency(String freq){
        freqList.add(freq);
    }

    public ArrayList<String> getFreqList(){
        return this.freqList;
    }


    //************************************  SURVEYS  **************************************

    //*************************  Literacy Survey  **************************************

    public int[] getLiteracySurveyAnswers() {
        return this.literacySurveyAnswers;
    }

    public void setLiteracySurveyAnswers(int[] answers) {
        this.literacySurveyAnswers = answers;
    }

    public void setLiteracySurveyAnswersString(ArrayList<String> answersRW) {
        String lastEntry = answersRW.get(answersRW.size()-1);
        int ind  = lastEntry.indexOf(",");
        int count = 0;
        while(ind!=-1){
            if(ind==1) {
                this.literacySurveyAnswers[count] = Integer.parseInt(String.valueOf(lastEntry.charAt(1)));

            }
            else{
                this.literacySurveyAnswers[count] = Integer.parseInt(lastEntry.substring(1,ind));
                Log.e("non-one",lastEntry.substring(1,ind));
            }
            lastEntry = lastEntry.substring(ind+1);
            Log.e("latest",lastEntry);
            ind  = lastEntry.indexOf(",");
            count++;
        }
        if(lastEntry.length()>2){
            this.literacySurveyAnswers[count] = Integer.parseInt(lastEntry.substring(1,lastEntry.length()-1));
        }
        else {
            this.literacySurveyAnswers[count] = Integer.parseInt(String.valueOf(lastEntry.charAt(1)));
        }
        Log.e("Lit Answers", Arrays.toString(literacySurveyAnswers));
    }

    public void setLiteracyDate(String date){this.literacydate=date;}
    public String getLiteracyDate(){
        return this.literacydate;
    }



    //**************************  Lifestyle Survey  *************************************


    public int[] getLifestyleSurveyAnswers() {
        return  this.lifestyleSurveyAnswers;
    }

    public void setLifestyleSurveyAnswers(int[] answers) {
        this.lifestyleSurveyAnswers = answers;
    }

    public void setLifestyleSurveyAnswersString(ArrayList<String> answersRW) {
        String lastEntry = answersRW.get(answersRW.size()-1);
        int ind  = lastEntry.indexOf(",");
        int count = 0;
        while(ind!=-1){
            if(ind==1) {
                this.lifestyleSurveyAnswers[count] = Integer.parseInt(String.valueOf(lastEntry.charAt(1)));

            }
            else{
                this.lifestyleSurveyAnswers[count] = Integer.parseInt(lastEntry.substring(1,ind));
                Log.e("non-one",lastEntry.substring(1,ind));
            }
            lastEntry = lastEntry.substring(ind+1);
            Log.e("latest",lastEntry);
            ind  = lastEntry.indexOf(",");
            count++;
        }
        if(lastEntry.length()>2){
            this.lifestyleSurveyAnswers[count] = Integer.parseInt(lastEntry.substring(1,lastEntry.length()-1));
        }
        else {
            this.lifestyleSurveyAnswers[count] = Integer.parseInt(String.valueOf(lastEntry.charAt(1)));
        }
        //Log.e("Intake Life",answersRW.toString());
        //Log.e("OutTake", Arrays.toString(lifestyleSurveyAnswers));
    }

    public void setLifestyleDate(String date){this.lifestyledate=date;}
    public String getLifestyleDate(){
        return this.lifestyledate;
    }



    public String getUID() {
        return uid;
    }

    public void setUID(String uid) {
        this.uid = uid;
    }
}
