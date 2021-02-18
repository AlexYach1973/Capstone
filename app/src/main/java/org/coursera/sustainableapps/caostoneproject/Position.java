package org.coursera.sustainableapps.caostoneproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Position extends AppCompatActivity {

    /**
     * GUI fields
     * поля графического интерфейса
     */
    ImageView imDanger;
    EditText editTextDescr;
    TextView textLan, textLong;
    Button btnCurrentMap, btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

        // initialize
        imDanger = findViewById(R.id.imageDanger);
        editTextDescr = findViewById(R.id.editTextDescription);
        textLan = findViewById(R.id.textLatitude);
        textLong = findViewById(R.id.textLongitude);
        btnCurrentMap = findViewById(R.id.buttonCurrentMap);
        btnOk = findViewById(R.id.buttonOk);

    }
}