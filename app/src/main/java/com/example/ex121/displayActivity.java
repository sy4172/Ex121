package com.example.ex121;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class displayActivity extends AppCompatActivity implements  View.OnCreateContextMenuListener {

    ListView lv;
    CustomAdapter customadp;
    ArrayList<String> nameList;
    ArrayList<String> details;
    int pos;

    SQLiteDatabase db;
    HelperDB hlp;
    Cursor crsr;
    ContentValues cv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        lv = findViewById(R.id.lv);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setOnCreateContextMenuListener(this);

        nameList = new ArrayList<String>();
        details = new ArrayList<String>();
        hlp = new HelperDB(this);
        String[] columns = {Students.STUDENT_NAME, Students.TEL, Students.ADDRESS};
        String selection = Students.ACTIVE +"=?";
        String[] selectionArgs = {"1"};
        String orderBy = Students.STUDENT_NAME;
        db = hlp.getWritableDatabase();
        crsr = db.query(Students.TABLE_STUDENT, columns, selection, selectionArgs, null, null, orderBy,null);
        crsr.moveToFirst();
        if (crsr != null){
            while (! crsr.isAfterLast()) {
                nameList.add(crsr.getString(crsr.getColumnIndex(Students.STUDENT_NAME)));
                String temp = "TEL: "+ crsr.getInt(crsr.getColumnIndex(Students.TEL))+ ", ADDRESS: "+ crsr.getString(crsr.getColumnIndex(Students.ADDRESS));
                details.add(temp);
                crsr.moveToNext();
            }
            crsr.close();
            db.close();
        }
        customadp = new CustomAdapter(getApplicationContext(), nameList, details);
        lv.setAdapter(customadp);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        v.setOnCreateContextMenuListener(this);
        menu.setHeaderTitle("ACTION");
        menu.add("Delete Student");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo adpInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        pos = adpInfo.position;
        String action = item.getTitle().toString();
        if (action.equals("Delete Student")){
                deleteStudent(pos);
                deleteStudentGrades(pos);
                nameList.remove(pos);
                details.remove(pos);
                customadp.notifyDataSetChanged();
                lv.setAdapter(customadp);
                Toast.makeText(this, "The student has been deleted successfully", Toast.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
    }

    private void deleteStudent(int posToDelete) {
        String name, address, firstParent, secondParent;
        int tel, homeTel, firstPhone, secondPhone;

        String[] columns = {Students.STUDENT_NAME,Students.ADDRESS, Students.TEL, Students.HOME_TEL, Students.PARENT1, Students.PHONE1, Students.PARENT2, Students.PHONE2};
        String selection = Students.STUDENT_NAME +"=?";
        String[] selectionArgs = {nameList.get(posToDelete)};
        db = hlp.getWritableDatabase();
        crsr = db.query(Students.TABLE_STUDENT, columns, selection, selectionArgs, null, null, null, null);
        crsr.moveToFirst();
        name = crsr.getString(crsr.getColumnIndex(Students.STUDENT_NAME));
        tel = crsr.getInt(crsr.getColumnIndex(Students.TEL));
        homeTel = crsr.getInt(crsr.getColumnIndex(Students.HOME_TEL));
        address = crsr.getString(crsr.getColumnIndex(Students.ADDRESS));
        firstParent = crsr.getString(crsr.getColumnIndex(Students.PARENT1));
        firstPhone = crsr.getInt(crsr.getColumnIndex(Students.PHONE1));
        secondParent = crsr.getString(crsr.getColumnIndex(Students.PARENT2));
        secondPhone = crsr.getInt(crsr.getColumnIndex(Students.PHONE2));

        db.delete(Students.TABLE_STUDENT, Students.STUDENT_NAME+"=?", new String[]{nameList.get(posToDelete)});

        cv = new ContentValues();
        cv.put(Students.STUDENT_NAME, name);
        cv.put(Students.TEL, tel);
        cv.put(Students.HOME_TEL, homeTel);
        cv.put(Students.ADDRESS, address);
        cv.put(Students.PARENT1, firstParent);
        cv.put(Students.PHONE1, firstPhone);
        cv.put(Students.PARENT2, secondParent);
        cv.put(Students.PHONE2, secondPhone);
        cv.put(Students.ACTIVE,0);
        db.insert(Students.TABLE_STUDENT, null, cv);
        cv.clear();
        crsr.close();
        db.close();
    }

    private void deleteStudentGrades(int posToDelete) {
        String selection = Students.STUDENT_NAME +"=?";
        String[] selectionArgs = {nameList.get(posToDelete)};
        db = hlp.getWritableDatabase();
        crsr = db.query(Grades.TABLE_GRADES, null, selection, selectionArgs, null, null, null);
        crsr.moveToFirst();
        cv = new ContentValues();
        cv.put(Grades.IS_ACTIVE, 0);
        while (! crsr.isAfterLast()){
            db.update(Grades.TABLE_GRADES, cv, Grades.IS_ACTIVE+"=?", new String[]{Integer.toString(1)});
            crsr.moveToNext();
        }
        crsr.close();
        cv.clear();
        db.close();
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
        else if (id == R.id.Sort_details){
            si = new Intent(this, sortOptActivity.class);
            startActivity(si);
        }
        return super.onOptionsItemSelected(item);
    }
}