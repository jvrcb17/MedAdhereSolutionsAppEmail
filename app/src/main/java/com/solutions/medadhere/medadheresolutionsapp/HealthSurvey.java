package com.solutions.medadhere.medadheresolutionsapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

//import android.icu.text.SimpleDateFormat;

/**
 * Created by lindsayherron on 9/14/16.
 */
public class HealthSurvey extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Button btnTag;
    int qnum;
    String questionParse;
    public static final String HSURVEY_FILENAME = "hsurvey_file";
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    final ArrayList<Integer> arrOpenParen = new ArrayList<>();
    final ArrayList<Integer> arrCloseParen = new ArrayList<>();
    final ArrayList<String> questionFilled = new ArrayList<>();
    final ArrayList<String> answersFilled = new ArrayList<>();

    public static final String PREFS_NAME = "MyPrefsFile";
    Context ctx;
    final ArrayList<String> questions = new ArrayList<>();
    String[] questionArray = new String[questions.size()];
    String buttonID;
    Toolbar toolbar;
    TextView amountOfQuestions;
    ProgressBar progressBar;
    final static int GET_RESULT_TEXT = 0;
    ArrayList<String[]> questionchoices = new ArrayList<>();
    ArrayList<String[]> key = new ArrayList<>();
    int [] naArray;
    int [] answers;
    int [] rightorwrong;
    float textsize = 20;
    int selected =0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_survey);
        //setTheme(R.style.myDialog);
        //showLocationDialog();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        toolbar.setSubtitle("Literacy Survey");
        LinearLayout layout_main = (LinearLayout) findViewById(R.id.main_ll);
        //get questions from xml file
        String[] mArray = getResources().getStringArray(R.array.LiteracySurvey);
        final ArrayList<String> questions = new ArrayList<>();

        for (String full : mArray) {
            String[] split = full.split("_");
            questions.add(split[0]);
            questionchoices.add(Arrays.copyOfRange(split, 1, split.length));
        }

        System.out.println(mArray.toString());
        questionArray = new String[questions.size()];
        questionArray = questions.toArray(questionArray);

        //get key from xml file
        final String[] keyArray = getResources().getStringArray(R.array.LiteracySurveyAnswers);
        for (String full : keyArray) {
            String[] split = full.split("_");
            key.add(Arrays.copyOfRange(split, 0, split.length));
        }

        //get notApplicable list from xml file
        String[] naArray1 = getResources().getStringArray(R.array.LiteracySurveyAnswersNA);
        naArray = new int[naArray1.length];
        int counter = 0;
        for (String full : naArray1) {
            naArray[counter] = Integer.parseInt(full);
            counter++;
        }

        //initialize results arrays
        answers = new int[mArray.length];
        Arrays.fill(answers, -1); //default for unanswered
        rightorwrong = new int[mArray.length];
        Arrays.fill(rightorwrong, 1); //default for wrong

        final HorizontalScrollView horScrollView = (HorizontalScrollView) findViewById(R.id.horizontal_scrollview);
        final TextView questionsView = (TextView) layout_main.findViewById(R.id.questionView);
        //questionsView.setText("Click an above button to start the survey.");
        final Button button = (Button) findViewById(R.id.submitButton);
        final Button buttonNext = (Button) findViewById(R.id.nextButton);
        final Button buttonPrev = (Button) findViewById(R.id.prevButton);
        LinearLayout layout = (LinearLayout) findViewById(R.id.lv);

        for (int i = 0; i < 36; i++) {
            final Button btnTag = new Button(this);
            btnTag.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            btnTag.setText("Question " + (i + 1));
            btnTag.setId(Integer.parseInt(Integer.toString(i)));
            layout.addView(btnTag);
            btnTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    qnum = btnTag.getId();
                    horScrollView.scrollTo(qnum*22*horScrollView.getMaxScrollAmount()/36,0);
                    if (qnum<35){
                        if (qnum==0){
                            buttonPrev.setVisibility(View.INVISIBLE);
                        }
                        buttonNext.setVisibility(View.VISIBLE);
                    }
                    if (qnum>0){
                        if (qnum==35){
                            buttonNext.setVisibility(View.INVISIBLE);
                        }
                        buttonPrev.setVisibility(View.VISIBLE);
                    }

                    ////TODO: Parse string here to change question into choices that have been made


                    questionsView.setText(Integer.toString(qnum+1)+".  "+parseQuestion(qnum));

                    final RadioButton opt1 = (RadioButton) findViewById(R.id.opt1);
                    opt1.setText(questionchoices.get(qnum)[0]);
                    opt1.setVisibility(View.VISIBLE);
                    final RadioButton opt2 = (RadioButton) findViewById(R.id.opt2);
                    opt2.setText(questionchoices.get(qnum)[1]);
                    opt2.setVisibility(View.VISIBLE);
                    final RadioButton opt3 = (RadioButton) findViewById(R.id.opt3);
                    opt3.setText(questionchoices.get(qnum)[2]);
                    opt3.setVisibility(View.VISIBLE);
                    final RadioButton opt4 = (RadioButton) findViewById(R.id.opt4);
                    opt4.setText(questionchoices.get(qnum)[3]);
                    opt4.setVisibility(View.VISIBLE);
                            if (answers[qnum] == 1) {
                                opt1.setChecked(true);
                                opt2.setChecked(false);
                                opt3.setChecked(false);
                                opt4.setChecked(false);
                            }
                            else if (answers[qnum] == 2){
                                opt1.setChecked(false);
                                opt2.setChecked(true);
                                opt3.setChecked(false);
                                opt4.setChecked(false);
                            }
                            else if (answers[qnum] == 3){
                                opt1.setChecked(false);
                                opt2.setChecked(false);
                                opt3.setChecked(true);
                                opt4.setChecked(false);
                            }
                            else if (answers[qnum] == 4) {
                                opt1.setChecked(false);
                                opt2.setChecked(false);
                                opt3.setChecked(false);
                                opt4.setChecked(true);
                            }
                    else{
                                opt1.setChecked(false);
                                opt2.setChecked(false);
                                opt3.setChecked(false);
                                opt4.setChecked(false);
                            }


                    opt1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            // TODO Auto-generated method stub
                            if (isChecked) {
                                opt2.setChecked(false);
                                opt3.setChecked(false);
                                opt4.setChecked(false);
                                if (btnTag.getId()==qnum){
                                    //btnTag.setBackgroundResource(R.drawable.button_bg);
                                    btnTag.setBackgroundColor(Color.TRANSPARENT);
                                    answers[qnum] = 1;
                                }
                                //TextView t;
                                //t.setText("The option, Option 1, has been checked below.");

                            }
                            int selected = 0;
                            for (int x : answers) {
                                if (x != -1) {
                                    selected++;
                                }
                                if (selected>35) {
                                    button.setVisibility(View.VISIBLE);
                                }
                            }
                            questionsView.setText(Integer.toString(qnum+1)+".  "+parseQuestion(qnum));
                        }
                    });

                    opt2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            // TODO Auto-generated method stub
                            if (isChecked) {
                                answers[qnum] = 2;
                                opt1.setChecked(false);
                                opt3.setChecked(false);
                                opt4.setChecked(false);
                                if (btnTag.getId()==qnum){
                                    //btnTag.setBackgroundResource(R.drawable.button_bg);
                                    btnTag.setBackgroundColor(Color.TRANSPARENT);
                                    answers[qnum] = 2;
                                }
                                //TextView t;
                                //t.setText("The option, Option 2, has been checked below.");
                            }
                            int selected = 0;
                            for (int x : answers) {
                                if (x != -1) {
                                    selected++;
                                }
                                if (selected>35) {
                                    button.setVisibility(View.VISIBLE);
                                }
                            }
                            questionsView.setText(Integer.toString(qnum+1)+".  "+parseQuestion(qnum));
                        }
                    });

                    opt3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            // TODO Auto-generated method stub
                            if (isChecked) {
                                answers[qnum] = 3;
                                opt1.setChecked(false);
                                opt2.setChecked(false);
                                opt4.setChecked(false);
                                if (btnTag.getId()==qnum){
                                    //btnTag.setBackgroundResource(R.drawable.horizontal_buttons_scroll);
                                    btnTag.setBackgroundColor(Color.TRANSPARENT);
                                    answers[qnum] = 3;
                                }
                                //TextView t;
                                //t.setText("The option, Option 3, has been checked below.");
                            }
                            int selected = 0;
                            for (int x : answers) {
                                if (x != -1) {
                                    selected++;
                                }
                                if (selected>35) {
                                    button.setVisibility(View.VISIBLE);
                                }
                            }
                            questionsView.setText(Integer.toString(qnum+1)+".  "+parseQuestion(qnum));
                        }
                    });
                    opt4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            // TODO Auto-generated method stub
                            if (isChecked) {

                                answers[qnum] = 4;
                                opt1.setChecked(false);
                                opt2.setChecked(false);
                                opt3.setChecked(false);
                                if (btnTag.getId()==qnum){
                                    //btnTag.setBackgroundResource(R.drawable.button_bg);
                                    btnTag.setBackgroundColor(Color.TRANSPARENT);
                                    answers[qnum] = 4;
                                }
                                //TextView t;
                                //t.setText("The option, Option 3, has been checked below.");
                            }
                            int selected = 0;
                            for (int x : answers) {
                                if (x != -1) {
                                    selected++;
                                }
                                if (selected>35) {
                                    button.setVisibility(View.VISIBLE);
                                }
                            }
                            questionsView.setText(Integer.toString(qnum+1)+".  "+parseQuestion(qnum));
                        }
                    });

                }
            });

        }

        buttonNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               btnTag = (Button) findViewById(qnum+1);
                btnTag.performClick();
            }
        });

        buttonPrev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnTag = (Button) findViewById(qnum-1);
                btnTag.performClick();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selected=0;
                int nonselected = 0;
                for (int x : answers) {
                    if (x != -1) {
                        selected++;
                    }
                    else{
                        nonselected++;
                    }
                }
                if (selected>35) {
                    String UID = ((MyApplication) HealthSurvey.this.getApplication()).getUID();

                    Snackbar.make(v, "Literacy Survey Successfully Completed", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    System.out.println(dateFormat.format(cal.getTime()));
                    final String currentDate = dateFormat.format(cal.getTime());
                    //***********************************************************************************************

//*********************************************************************************************

                    mDatabase.child("app").child("users").child(UID).child("literacysurveyanswersRW").child(currentDate) .setValue(Arrays.toString(answers));

                    ((MyApplication) HealthSurvey.this.getApplication()).setLiteracySurveyAnswers(answers);
                    ((MyApplication) HealthSurvey.this.getApplication()).setLiteracyDate(currentDate);
                    Intent i = new Intent(HealthSurvey.this, SurveySelectionActivity.class);
                    startActivity(i);
                    finish();
                }
                else{
                    Snackbar.make(v, "Please complete all 36 questions. "+nonselected+" questions remain. Please scroll through the buttons above.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }


        });
    }


    public String parseQuestion(int z) {

        arrOpenParen.clear();
        arrCloseParen.clear();
        questionFilled.clear();
        answersFilled.clear();

        int difference=0;
        int posOfQuestion = 0;
        String questionParse = questionArray[z];
        String newQuestion="";
        int closeParen = 0;
        int openParen = 0;
        closeParen = questionParse.indexOf(")");
        openParen = questionParse.indexOf("(");

        while (openParen!=-1){
            arrOpenParen.add(openParen);
            arrCloseParen.add(closeParen);
            closeParen = questionParse.indexOf(")",closeParen+1);
            openParen = questionParse.indexOf("(",openParen+1);
        }
        int total = arrOpenParen.size();
        for (int i=0;i<arrCloseParen.size();i++){
            if (arrOpenParen.get(i)==(arrCloseParen.get(i)-2)){
                posOfQuestion = i;
            }
        }

        for(int i=0;i<total;i++){
            if (i==0) {
                questionFilled.add(questionParse.substring(0, arrOpenParen.get(i)));
            }
            else{
                questionFilled.add(questionParse.substring(arrCloseParen.get(i-1)+1, arrOpenParen.get(i)));
            }
        }
        questionFilled.add(questionParse.substring(arrCloseParen.get(total-1)+1,questionParse.length()));
        for (int i=0;i<total;i++){
            difference = posOfQuestion-i;
            if (difference==0){
                if (answers[z] != -1){
                    answersFilled.add(i,questionchoices.get(z)[answers[z]-1] );
                }
                else{
                    answersFilled.add(i,"(?)");
                }
            }
            else{
                if (answers[z-difference] != -1){
                    answersFilled.add(i,questionchoices.get(z-difference)[answers[z-difference]-1] );
                }
                else{
                    answersFilled.add(i,"()");
                }
            }
        }
        for (int i=0;i<answersFilled.size();i++) {
            if (answersFilled.get(i).indexOf("(") == -1){
                newQuestion = newQuestion.concat(questionFilled.get(i)).concat("(").concat(answersFilled.get(i)).concat(")");
            }
            else {
                newQuestion = newQuestion.concat(questionFilled.get(i)).concat(answersFilled.get(i));
            }
        }

        newQuestion = newQuestion.concat(questionFilled.get(answersFilled.size()));
        return newQuestion;
    }







    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {

        String tel = ((MyApplication) this.getApplication()).getPharmaPhone();
        Log.e("Tel",tel);
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id  == R.id.nav_home){
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
        else if (id == R.id.nav_bloodpressure) {
            Intent i = new Intent(this, BPCalendarActivity.class);
            startActivity(i);
            finish();
        }else if(id == R.id.nav_weight){
            Intent i = new Intent(this, WeightCalendarActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_medication) {
            Intent i = new Intent(this, MedicationActivity.class);
            startActivity(i);
            finish();
        }else if (id == R.id.nav_surveys) {
            Intent i = new Intent(this, SurveySelectionActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_callmypharmacist) {
            Intent i = new Intent(Intent.ACTION_DIAL);
            i.setData(Uri.parse("tel:"+tel));
            startActivity(i);
            finish();
        } else if (id == R.id.nav_logout) {

            //Log.e("logout", UIDstored);
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        else if (id == R.id.nav_study_contact) {
            Intent i = new Intent(this, StudyContactsActivity.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}