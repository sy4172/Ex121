package com.example.ex121;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 *  * @author		Shahar Yani
 *  * @version  	1.0
 *  * @since		8/01/2021
 *
 *  * This Credits.class displays a credit activity
 *    and a menu move to the whole activities
 *  */
public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent si;
        if (id == R.id.Input_details){
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
        else if (id == R.id.Sort_details){
            si = new Intent(this, sortOptActivity.class);
            startActivity(si);
        }
        return super.onOptionsItemSelected(item);
    }
}