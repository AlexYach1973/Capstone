package org.coursera.sustainableapps.caostoneproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import static org.coursera.sustainableapps.caostoneproject.R.id.buttonDanger;

public class MainActivity extends AppCompatActivity {

    // Buttons
    Button mButtonDanger, mButtonDatabase, mButtonMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialization();

    }

    private void initialization() {

        // Buttons
        mButtonDanger = findViewById(buttonDanger);
        mButtonDatabase = findViewById(R.id.buttonDatabase);
        mButtonMap = findViewById(R.id.buttonMap);

        // assign a listener
        mButtonDanger.setOnClickListener(viewClickListener);
        mButtonDatabase.setOnClickListener(viewClickListener);
        mButtonMap.setOnClickListener(viewClickListener);

    }

    @SuppressLint("NonConstantResourceId")
    View.OnClickListener viewClickListener = v -> {

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
                Intent intent2 = new Intent(MainActivity.this, Observe.class);
                startActivity(intent2);
                break;
        }

    };

}