package com.solutions.medadhere.medadheresolutionsapp;

/**
 * Created by Yeshy on 8/19/2016.
 */
public class Medication {

    public String name;
    public String frequency;
    public String daysSupply;
    public String id;

    public Medication() {
        //default constructor because why not
    }

    public Medication(String name, String frequency, String daysSupply, String id) {
        this.name = name;
        this.frequency = frequency;
        this.daysSupply = daysSupply;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDaysSupply() {
        return daysSupply;
    }

    public void setDaysSupply(String daysSupply) {
        this.daysSupply = daysSupply;
    }

    public String getID() { return id; }

    public void setID(String id) { this.id = id; }


}
