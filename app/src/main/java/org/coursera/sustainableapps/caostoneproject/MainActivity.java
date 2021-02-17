package org.coursera.sustainableapps.caostoneproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

//    Field

    static ContentResolver mContentResolver;

    /**
     * Buttons
     */
    Button mButtonDanger, mButtonDatabase, mButtonMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialization();

        mContentResolver = getContentResolver();
    }

    private void initialization() {

        // Buttons
        mButtonDanger = findViewById(R.id.buttonDanger);
        mButtonDatabase = findViewById(R.id.buttonDatabase);
        mButtonMap = findViewById(R.id.buttonMap);

        // assign a listener
        mButtonDanger.setOnClickListener(viewClickListener);
        mButtonDatabase.setOnClickListener(viewClickListener);
        mButtonMap.setOnClickListener(viewClickListener);
    }

    View.OnClickListener viewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.buttonDanger:
                    Intent intent = new Intent(MainActivity.this, Danger.class);
                    startActivity(intent);
                    break;

                case R.id.buttonDatabase:

                    Intent intent1 = new Intent(MainActivity.this, DataBase.class);
                    startActivity(intent1);

                    break;
                case R.id.buttonMap:

                    break;
            }

        }
    };
}