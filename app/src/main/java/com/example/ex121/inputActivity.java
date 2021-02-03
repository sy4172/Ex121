package com.example.ex121;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class inputActivity extends AppCompatActivity {

    EditText name, address, tel, homeTel, firstParent, firstTel, secParent, secTel;

    SQLiteDatabase db;
    HelperDB hlp;
    Cursor crsr;
    ContentValues cv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        tel = findViewById(R.id.tel);
        homeTel = findViewById(R.id.homeTel);
        firstParent = findViewById(R.id.firstParent);
        firstTel = findViewById(R.id.firstTel);
        secParent = findViewById(R.id.secParent);
        secTel = findViewById(R.id.secTel);

        hlp = new HelperDB(this);
    }

    public void saveData(View view) {
        if (checkAll() && checkCouples()) {
            cv = new ContentValues();
            cv.put(Students.ACTIVE, 1);
            cv.put(Students.STUDENT_NAME, name.getText().toString());
            cv.put(Students.ADDRESS, address.getText().toString());
            cv.put(Students.TEL, Integer.valueOf(tel.getText().toString()));
            cv.put(Students.HOME_TEL, Integer.valueOf(homeTel.getText().toString()));
            cv.put(Students.PARENT1, firstParent.getText().toString());
            if (firstTel.getText().toString().isEmpty())
                cv.put(Students.PHONE1, -99);
            else
                cv.put(Students.PHONE1, Integer.valueOf(firstTel.getText().toString()));
            cv.put(Students.PARENT2, secParent.getText().toString());
            if (secTel.getText().toString().isEmpty())
                cv.put(Students.PHONE2, -99);
            else
                cv.put(Students.PHONE2, Integer.valueOf(secTel.getText().toString()));
            db = hlp.getWritableDatabase();
            db.insert(Students.TABLE_STUDENT, null, cv);
            db.close();
            cv.clear();
            name.setText("");
            address.setText("");
            tel.setText("");
            homeTel.setText("");
            firstParent.setText("");
            firstTel.setText("");
            secParent.setText("");
            secTel.setText("");
            Toast.makeText(this, "The details saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Make sure have typed currently"+'\n'+"and not typing the same data", Toast.LENGTH_LONG).show();
        }

    }

    private boolean checkCouples() {
        boolean sameName, sameTel;
        sameName = sameTel = false;

        String [] columns = {Students.STUDENT_NAME, Students.TEL};
        db = hlp.getWritableDatabase();
        crsr = db.query(Students.TABLE_STUDENT, columns, null, null, null, null, null, null);
        crsr.moveToFirst();
        if (crsr != null) {
            while (!crsr.isAfterLast() && !sameName && !sameTel) {
                sameName = name.getText().toString().equals(crsr.getString(crsr.getColumnIndex(Students.STUDENT_NAME)));
                sameTel = tel.getText().toString().equals(crsr.getString(crsr.getColumnIndex(Students.TEL)));
                crsr.moveToNext();
            }
            crsr.close();
        }
        db.close();

        return (!sameName && !sameTel);
    }

    private boolean checkAll() {
        boolean flag = true;
        if (name.getText().toString().isEmpty() || address.getText().toString().isEmpty() || tel.getText().toString().isEmpty() || homeTel.getText().toString().isEmpty())
            flag = false;
        if (tel.getText().toString().length() != 10 || homeTel.getText().toString().length() != 9) flag = false;
        if (name.getText().toString().endsWith(" ")) flag = false;
        if (name.getText().toString().contains("[0-9]+") || address.getText().toString().startsWith("[0-9]+")) flag = false;
        if (firstTel.getText().toString().length() > 0 || secTel.getText().toString().length() > 0){
            if (firstTel.getText().toString().contains("[a-zA-Z]+") || secTel.getText().toString().contains("[a-zA-Z]+") ||firstParent.getText().toString().contains("[0-9]+") || secParent.getText().toString().contains("[0-9]+")){
                flag = false;
            }
        }
        return flag;
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
        else if (id == R.id.input_grades){
            si = new Intent(this, gradesActivity.class);
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
