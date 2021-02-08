package com.example.ex121;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *  * @author		Shahar Yani
 *  * @version  	1.0
 *  * @since		20/01/2021
 *
 *  * This gradesActivity.class displays to the user several options of sorting the details
 *    that have been inserting to the SQLite DataBase.
 *    And there is a menu to move to the others activities.
 *  */
public class gradesActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner studentsSpin, semestersSpin, subjectsSpin;
    String[] semestersList = {"1", "2", "3", "4"};
    String selectedStudent, selectedSubject;
    ArrayList<String> studentsList, subjectsList;
    ArrayAdapter<String> adp, adp2, adp3;
    EditText inputtedGrade;
    int selectedSemester;

    SQLiteDatabase db;
    HelperDB hlp;
    Cursor crsr;
    ContentValues cv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);

        studentsSpin = findViewById(R.id.studentsSpin);
        semestersSpin = findViewById(R.id.semestersSpin);
        subjectsSpin = findViewById(R.id.subjectsSpin);
        inputtedGrade = findViewById(R.id.inputtedGrade);

        subjectsList = new ArrayList<>();
        studentsList = new ArrayList<>();
        studentsSpin.setOnItemSelectedListener(this);
        semestersSpin.setOnItemSelectedListener(this);
        subjectsSpin.setOnItemSelectedListener(this);

        studentsList.add("Students");

        String[] columns = {Students.STUDENT_NAME};
        String selection = Students.ACTIVE + "=?";
        String[] selectionArgs = {"1"};
        String orderBy = Students.STUDENT_NAME;
        hlp = new HelperDB(this);
        db = hlp.getWritableDatabase();
        crsr = db.query(Students.TABLE_STUDENT, columns, selection, selectionArgs, orderBy, null, null, null);
        crsr.moveToFirst();
        while (!crsr.isAfterLast()) {
            studentsList.add(crsr.getString(crsr.getColumnIndex(Students.STUDENT_NAME)));
            crsr.moveToNext();
        }
        crsr.close();
        db.close();

        // Reading from a SharedPreference file all the subjects that has been inserted
        SharedPreferences settings = getSharedPreferences("PREFS_SUBJECTS", MODE_PRIVATE);
        Set<String> set = settings.getStringSet("SubjectsSetString", null);
        if (set != null) {
            subjectsList.addAll(set);
        }

        adp = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, semestersList);
        semestersSpin.setAdapter(adp);
        adp2 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, subjectsList);
        subjectsSpin.setAdapter(adp2);
        adp3 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, studentsList);
        studentsSpin.setAdapter(adp3);
    }

    /**
     * The addSubject method acts when the Button was clicked,
     *  displays an AlertDialog to insert the new subject with comments
     *
     * @param view that was clicked
     */
    public void addSubject(View view) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("INSERT NEW SUBJECT");
        adb.setCancelable(false);
        final EditText newSubject = new EditText(this);
        adb.setView(newSubject);
        adb.setPositiveButton("INSERT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (newSubject.getText().toString().isEmpty())
                    Toast.makeText(gradesActivity.this, "No new subject was detected", Toast.LENGTH_SHORT).show();
                else {
                    String selectedSubject = newSubject.getText().toString();
                    if (subjectsList.contains(selectedSubject))
                        Toast.makeText(gradesActivity.this, "The subject is already in the list", Toast.LENGTH_SHORT).show();
                    else {
                        if (subjectsList.isEmpty()) subjectsList.add("Subjects");
                        subjectsList.add(selectedSubject);
                        adp2.notifyDataSetChanged();

                        // Writing to a SharedPreference file all the subjects that has been inserted
                        SharedPreferences settings = getSharedPreferences("PREFS_SUBJECTS", MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        Set<String> set = new HashSet<String>(subjectsList);
                        editor.putStringSet("SubjectsSetString", set);
                        editor.apply();
                        Toast.makeText(gradesActivity.this, "The new subject has been successfully inserted", Toast.LENGTH_SHORT).show();
                    }
                }
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

    /**
     * The insertGradesToSQL method acts when the Button was clicked,
     *  writing the selected student grade based on the selected parameters  with comments.
     *
     * @param view that was clicked
     */
    public void insertGradesToSQL(View view) {
        int selectedGrade;
        if (!inputtedGrade.getText().toString().isEmpty()) {
            selectedGrade = Integer.parseInt(inputtedGrade.getText().toString());
            if (!(selectedGrade > 100)) {
                if (checkIfTwice()) {
                    if (!selectedSubject.equals("Subjects") && !selectedStudent.equals("Students")) {
                        cv = new ContentValues();
                        cv.put(Grades.IS_ACTIVE, 1);
                        cv.put(Grades.STUDENT_NAME, selectedStudent);
                        cv.put(Grades.SUBJECT, selectedSubject);
                        cv.put(Grades.SEMESTER, selectedSemester);
                        cv.put(Grades.GRADE, selectedGrade);
                        db = hlp.getWritableDatabase();
                        db.insert(Grades.TABLE_GRADES, null, cv);
                        db.close();
                        cv.clear();
                        Toast.makeText(this, "The grade has successfully saved", Toast.LENGTH_SHORT).show();
                        inputtedGrade.setText("");
                    } else Toast.makeText(this, "Make sure that you haven't selected empty fields", Toast.LENGTH_LONG).show();
                }
                else Toast.makeText(this, "Make sure you don't insert the same details", Toast.LENGTH_LONG).show();
            } else Toast.makeText(this, "Enter a grade that in the range 0 - 100", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "No grade has detected", Toast.LENGTH_SHORT).show();
    }

    /**
     * The checkIfTwice method checks the selcetd parameters in the SQLite DataBase,
     * to prevent couples of the same data
     *
     * @return true if there is not twice else false
     */
    private boolean checkIfTwice() {
        boolean flag = true;
        String[] columns = {Grades.SUBJECT, Grades.SEMESTER};
        String selection = Grades.STUDENT_NAME + "=?";
        String[] selectionArgs = {selectedStudent};
        db = hlp.getWritableDatabase();
        crsr = db.query(Grades.TABLE_GRADES, columns, selection, selectionArgs, null, null, null, null);
        crsr.moveToFirst();
        if (crsr != null) {
            while ((! crsr.isAfterLast()) && flag){
                if (crsr.getInt(crsr.getColumnIndex(Grades.SEMESTER)) == selectedSemester && crsr.getString(crsr.getColumnIndex(Grades.SUBJECT)).equals(selectedSubject)) {
                    flag = false;
                }
                crsr.moveToNext();
            }
        }
        return flag;
    }

    /**
     * The onItemSelected method saved the selected parameters from the Spinners objects
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.semestersSpin) {
            selectedSemester = position + 1;
        } else if (parent.getId() ==  R.id.subjectsSpin) {
            selectedSubject = subjectsList.get(position);
        } else if (parent.getId() ==  R.id.studentsSpin) {
            selectedStudent = studentsList.get(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

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
        else if (id == R.id.Display_details){
            si = new Intent(this, displayActivity.class);
            startActivity(si);
        }
        else if (id == R.id.Sort_details){
            si = new Intent(this, sortOptActivity.class);
            startActivity(si);
        }
        return super.onOptionsItemSelected(item);
    }
}