package org.coursera.sustainableapps.caostoneproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity {

//    Field ContentResolver
    static ContentResolver mContentResolver;

    /**
     * fields for determining the current location.
     * Used in DataBase.class
     * поля для определение текущей локации.
     * Используется в DataBase.class
     */
    public LocationManager locationManager;
    List<String> providers;

    // Buttons
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

        // get LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
    }

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

                break;
        }

    };
}