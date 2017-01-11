package com.solutions.medadhere.medadheresolutionsapp;

import android.app.Application;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

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



    private int[] demographicsSurveyAnswers = {0,0,0,0,0,0,0};
    private int[] lifestyleSurveyAnswers = {0,0,0,0,0,0,0,0};
    private int[] lifestyleSurveyAnswersRW = {0,0,0,0,0,0,0,0};
    private int[] adherenceSurveyAnswers = {0,0,0,0,0,0,0,0};
    private int[] adherenceSurveyAnswersRW = {0,0,0,0,0,0,0,0};
    private int[] literacySurveyAnswers = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private int[] literacySurveyAnswersRW = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    private ArrayList<Medication> medicationList = new ArrayList<>();




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

    public void addMedication(Medication medication) {
        medicationList.add(medication);
    }

    public void removeMedication() {
        if(medicationList.size() != 0) {
            medicationList.remove(medicationList.size() - 1);
        }
    }
    public void addMedicationList(ArrayList<Medication> medicationList) {
        this.medicationList = medicationList;
    }

    public ArrayList<Medication> getMedicationList() {
        return medicationList;
    }

    public void resetMedicationList() {
        medicationList = new ArrayList<>();
    }

    public String[] getMedicationNames() {
        String [] medicationNames = new String[medicationList.size()];
        int counter = 0;
        for(Medication m: medicationList) {
            medicationNames[counter] = m.getName();
            System.out.println(m.getName());
            counter++;
        }
        return medicationNames;
    }
    //********************************************************************************************
    public void setDemographicsSurveyAnswers(int[] answers) {
        if(answers.length == demographicsSurveyAnswers.length)
            demographicsSurveyAnswers = answers;
    }

    public int[] getDemographicsSurveyAnswers() {
        return demographicsSurveyAnswers;
    }
    //********************************************************************************************
    public void setLiteracySurveyAnswers(int[] answers) {
        if(answers.length == literacySurveyAnswers.length)
            literacySurveyAnswers = answers;
    }

    public void setLiteracySurveyAnswersRW(int[] answersRW) {
        if(answersRW.length == literacySurveyAnswersRW.length)
            literacySurveyAnswersRW = answersRW;
    }

    public int[] getLiteracySurveyAnswers() {
        return literacySurveyAnswers;
    }

    public int[] getLiteracySurveyAnswersRW() {
        return literacySurveyAnswersRW;
    }
    //********************************************************************************************
    public void setAdherenceSurveyAnswers(int[] answers) {
        if(answers.length == adherenceSurveyAnswers.length)
            adherenceSurveyAnswers = answers;
    }

    public void setAdherenceSurveyAnswersRW(int[] answersRW) {
        if(answersRW.length == adherenceSurveyAnswersRW.length)
            adherenceSurveyAnswersRW = answersRW;
    }

    public int[] getAdherenceSurveyAnswers() {
        return adherenceSurveyAnswers;
    }

    public int[] getAdherenceSurveyAnswersRW() {
        return adherenceSurveyAnswersRW;
    }
    //********************************************************************************************
    public void setLifestyleSurveyAnswers(int[] answers) {
        if(answers.length == lifestyleSurveyAnswers.length)
            lifestyleSurveyAnswers = answers;
    }

    public void setLifestyleSurveyAnswersRW(int[] answersRW) {
        if(answersRW.length == lifestyleSurveyAnswersRW.length)
            lifestyleSurveyAnswersRW = answersRW;
    }

    public int[] getLifestyleSurveyAnswers() {
        return lifestyleSurveyAnswers;
    }

    public int[] getLifestyleSurveyAnswersRW() {
        return lifestyleSurveyAnswersRW;
    }
    //********************************************************************************************

    public String getUID() {
        return uid;
    }

    public void setUID(String uid) {
        this.uid = uid;
    }
}
