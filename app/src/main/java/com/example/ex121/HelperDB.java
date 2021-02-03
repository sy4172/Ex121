package com.example.ex121;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.example.ex121.Grades.GRADE;
import static com.example.ex121.Grades.IS_ACTIVE;
import static com.example.ex121.Grades.SEMESTER;
import static com.example.ex121.Grades.SUBJECT;
import static com.example.ex121.Grades.TABLE_GRADES;
import static com.example.ex121.Students.ACTIVE;
import static com.example.ex121.Students.ADDRESS;
import static com.example.ex121.Students.HOME_TEL;
import static com.example.ex121.Students.KEY_ID;
import static com.example.ex121.Students.PARENT1;
import static com.example.ex121.Students.PARENT2;
import static com.example.ex121.Students.PHONE1;
import static com.example.ex121.Students.PHONE2;
import static com.example.ex121.Students.TABLE_STUDENT;
import static com.example.ex121.Students.STUDENT_NAME;
import static com.example.ex121.Students.TEL;
// להוסיף את הטבלה השנייה
public class HelperDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StudentInfo.db";
    private static final int DATABASE_VERSION = 3;
    String strCreate, strDelete;

    public HelperDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        strCreate = "CREATE TABLE "+ TABLE_STUDENT;
        strCreate +=" ("+KEY_ID+" INTEGER PRIMARY KEY,";
        strCreate += " "+STUDENT_NAME+" TEXT,";
        strCreate += " "+TEL+" INTEGER,";
        strCreate += " "+HOME_TEL+ " INTEGER,";
        strCreate += " "+ADDRESS+ " TEXT,";
        strCreate += " "+PARENT1+ " TEXT,";
        strCreate += " "+PHONE1+" INTEGER,";
        strCreate += ""+PARENT2+ " TEXT,";
        strCreate += " "+PHONE2+ " INTEGER,";
        strCreate += " "+ ACTIVE+" INTEGER";
        strCreate += ");";
        db.execSQL(strCreate);

        strCreate="CREATE TABLE "+TABLE_GRADES;
        strCreate +=" ("+KEY_ID+" INTEGER PRIMARY KEY,";
        strCreate +=" "+ STUDENT_NAME+" TEXT,";
        strCreate +=" "+SUBJECT+" TEXT,";
        strCreate +=" "+SEMESTER+" INTEGER,";
        strCreate +=" "+GRADE+" INTEGER,";
        strCreate +=" "+IS_ACTIVE+" INTEGER";
        strCreate +=");";
        db.execSQL(strCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        strDelete="DROP TABLE IF EXISTS "+TABLE_STUDENT;
        db.execSQL(strDelete);

        strDelete="DROP TABLE IF EXISTS "+TABLE_GRADES;
        db.execSQL(strDelete);

        onCreate(db);
    }
}
