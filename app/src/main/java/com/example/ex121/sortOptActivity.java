package com.example.ex121;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

/**
 *  * @author		Shahar Yani
 *  * @version  	1.0
 *  * @since		20/01/2021
 *
 *  * This sortOptActivity.class displays to the user several options of sorting the details
 *    that have been inserting to the SQLite DataBase.
 *    And there is a menu to move to the others activities.
 *  */
public class sortOptActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    TextView titleOfOpt;
    ListView lv, optLv;
    String [] options;
    ArrayList<String> nameList, subjectsList;
    ArrayAdapter<String> adp;

    SQLiteDatabase db;
    HelperDB hlp;
    Cursor crsr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_opt);

        titleOfOpt = findViewById(R.id.titleOfOpt);
        optLv = findViewById(R.id.optLv);
        lv = findViewById(R.id.lv);

        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        hlp = new HelperDB(this);
        optLv.setOnItemClickListener(this);

        options = new String[]{"Options","Student's grades", "Grades in a certain subject", "All the students"};
        adp = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, options);
        optLv.setAdapter(adp);
        subjectsList = new ArrayList<String>();
        titleOfOpt.setText(R.string.select_option);

        // Getting all the 'active' students from the SQLite DataBase to an arrayList
        nameList = new ArrayList<String>();
        String[] columns = {Students.STUDENT_NAME};
        String selection = Students.ACTIVE +"=?";
        String[] selectionArgs = {"1"};
        String orderBy = Students.STUDENT_NAME;
        db = hlp.getWritableDatabase();
        crsr = db.query(Students.TABLE_STUDENT, columns, selection, selectionArgs, null, null, orderBy,null);
        crsr.moveToFirst();
        if (crsr != null){
            while (! crsr.isAfterLast()) {
                nameList.add(crsr.getString(crsr.getColumnIndex(Students.STUDENT_NAME)));
                crsr.moveToNext();
            }
            crsr.close();
            db.close();
        }

        Intent gi = getIntent();
        if (!Objects.requireNonNull(gi.getStringExtra("NameToGrades")).isEmpty()){
            lv.setAdapter(null);
            displayStudentGrades(gi.getStringExtra("NameToGrades"));
        }
    }

    /**
     * The onItemClick method acts when an option is selected from optLv object,
     * and call to other functions which display on lv object the result.
     * @param position for act the right option.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        titleOfOpt.setText(R.string.select_option);
        switch (position){
            case 0:{ // when the title "Option" was clicked nothing should be done
                lv.setAdapter(null);
            }
            break;

            case 1:{
                lv.setAdapter(null);
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                final EditText inputStudent = new EditText(this);
                adb.setTitle("INSERT STUDENT");
                adb.setIcon(R.drawable.iconlogo);
                adb.setView(inputStudent);
                adb.setPositiveButton("DISPLAY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = inputStudent.getText().toString();
                        displayStudentGrades(input);
                    }
                });

                adb.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog ad = adb.create();
                ad.show();
            }
            break;

            case 2:{
                lv.setAdapter(null);
                SharedPreferences settings = getSharedPreferences("PREFS_SUBJECTS", MODE_PRIVATE);
                Set<String> set = settings.getStringSet("SubjectsSetString", null);
                if (set != null) {
                    subjectsList.addAll(set);
                } else subjectsList.add("Subjects");

                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                final EditText inputSubject = new EditText(this);
                adb.setTitle("INSERT SUBJECT");
                adb.setIcon(R.drawable.iconlogo);
                adb.setView(inputSubject);
                adb.setPositiveButton("DISPLAY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = inputSubject.getText().toString();
                        displaySubjectsGrades(input);
                    }
                });

                adb.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog ad = adb.create();
                ad.show();
            }
            break;


            case 3:{
                lv.setAdapter(null);
                displayAllStudents();
            }
            break;
        }
    }

    /**
     * The displaySubjectsGrades method does the sorting based on the parameter of the subject
     *  and displays all the subject's grades by order on the ListView object view.
     *
     * @param input - the subject to sort with.
     */
    private void displaySubjectsGrades(String input) {
        String[] columns = {Grades.GRADE, Grades.STUDENT_NAME, Grades.SEMESTER};
        String selection = Grades.SUBJECT +"=?";
        String[] selectionArgs = {input};
        String orderBy = Grades.GRADE;
        ArrayList<String> gradesInSubject = new ArrayList<String>();
        db = hlp.getWritableDatabase();
        crsr = db.query(Grades.TABLE_GRADES, columns, selection, selectionArgs, null, null, orderBy,null);
        crsr.moveToFirst();
        if (subjectsList.contains(input) && crsr != null){
            titleOfOpt.setText("Grades of the subject: "+ input);
            while (! crsr.isAfterLast()) {
                String temp = "Name:"+ crsr.getString(crsr.getColumnIndex(Grades.STUDENT_NAME))+ " Sem:" + crsr.getInt(crsr.getColumnIndex(Grades.SEMESTER)) + " Grade:" + crsr.getInt(crsr.getColumnIndex(Grades.GRADE));
                gradesInSubject.add(temp);
                crsr.moveToNext();
            }
            crsr.close();
            db.close();

            adp = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, gradesInSubject);
            lv.setAdapter(adp);
        }
        else {
            Toast.makeText(sortOptActivity.this, "Invalid subject", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * The displayAllStudents method displays all the students based on the ABC's order on a ListView object.
     */
    private void displayAllStudents() {
        titleOfOpt.setText(R.string.wholeStudentText);
        adp = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, nameList);
        lv.setAdapter(adp);
    }

    /**
     * The displayStudentGrades method does the sorting based on the parameter of the student
     *  and displays all the student's grades by order on the ListView object view.
     *
     * @param input - the student to sort with.
     */
    private void displayStudentGrades(String input) {
        String[] columns = {Grades.GRADE, Grades.SUBJECT, Grades.SEMESTER};
        String selection = Grades.STUDENT_NAME +"=?";
        String[] selectionArgs = {input};
        String orderBy = Grades.GRADE;
        ArrayList<String> gradesDetails = new ArrayList<String>();
        db = hlp.getWritableDatabase();
        crsr = db.query(Grades.TABLE_GRADES, columns, selection, selectionArgs, null, null, orderBy,null);
        crsr.moveToFirst();
        if (crsr != null && nameList.contains(input)){
            titleOfOpt.setText("Grades of the student:");
            while (! crsr.isAfterLast()) {
                String temp = "Subject: "+ crsr.getString(crsr.getColumnIndex(Grades.SUBJECT))+ " Sem.: " + crsr.getInt(crsr.getColumnIndex(Grades.SEMESTER)) + " Grade: " + crsr.getInt(crsr.getColumnIndex(Grades.GRADE));
                gradesDetails.add(temp);
                crsr.moveToNext();
            }
            crsr.close();
            db.close();

            adp = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, gradesDetails);
            lv.setAdapter(adp);
            titleOfOpt.setText("Grades of the student:"+ input);
        }
        else {
            Toast.makeText(sortOptActivity.this, "Invalid student name", Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent si;
        if (id == R.id.Credits) {
            si = new Intent(this, CreditsActivity.class);
            startActivity(si);
        }
        else if (id == R.id.Input_details){
            si = new Intent(this, inputActivity.class);
            startActivity(si);
        }
        else if (id == R.id.input_grades){
            si = new Intent(this, gradesActivity.class);
            startActivity(si);
        }
        else if (id == R.id.Display_details){
            si = new Intent(this, displayActivity.class);
            startActivity(si);
        }
        return super.onOptionsItemSelected(item);
    }
}