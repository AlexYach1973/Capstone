package org.coursera.sustainableapps.caostoneproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class DataBase extends AppCompatActivity {

    /**
     * ListView to display the database
     *для отображения базы данных
     */
    ListView listDanger;
    ImageView imageView;
    ListView lvData;

    DangerProvider mProvider = new DangerProvider();

    /**
     * fields required to determine the current position
     * поля, необходимые для определения текущего положения
     */
//    private LocationManager locationManager;

    /**
     * Buttons "Refresh", "Add" and "delete"
     */
    Button mButtonRefresh, mButtonAdd, mButtonDelete;

    //    private ContentResolver mContentResolver;

    // атрибуты для Map
    // attributes for MAP
   /* public static final String ATTRIBUTE_DANGER_ROUND = "round";
    public static final String ATTRIBUTE_DANGER_BIO = "bio";
    public static final String ATTRIBUTE_DANGER_CHEM = "chem";
    public static final String ATTRIBUTE_DANGER_RADIO = "radio";
    public static final String ATTRIBUTE_DANGER_LASER = "laser";
    public static final String ATTRIBUTE_DANGER_MAGNETICS = "magnetics";*/

    /**
     * Constructor initializes the fields.
     */
    public DataBase() {
//        mContentResolver = getApplicationContext().getContentResolver();
    }

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base);

        // initialize ListView
        imageView = findViewById(R.id.imageViewList);
        listDanger = findViewById(R.id.listDanger);
        lvData = (ListView) findViewById(R.id.listDanger);

        // initialize Buttons
        mButtonRefresh = findViewById(R.id.butRefresh);
        mButtonAdd = findViewById(R.id.butAdd);
        mButtonDelete = findViewById(R.id.butDelete);
        // assign a listener
        mButtonRefresh.setOnClickListener(viewClickListener);
        mButtonAdd.setOnClickListener(viewClickListener);
        mButtonDelete.setOnClickListener(viewClickListener);

        loadingDefault();
        displayCurrent();

    }

    /**
     * Display the designated columns in the cursor as a List in
     * the ListView
     */
    private void displayCurrent() {

        // Query for all characters
        Cursor mCursor = MainActivity.mContentResolver.query(DBContract.FeedEntry.CONTENT_URI,
                DBContract.FeedEntry.sColumnsToDisplay,
                null, null, null);

// Inform the user if there's nothing to display.
        if (mCursor.getCount() == 0) {
            Toast.makeText(this,
                    "No items to display",
                    Toast.LENGTH_SHORT).show();

            // Logs
            Log.d("myLogs", "mCursor = null");

            // Remove the display if there's nothing left to show.
//            mHobbitActivity.displayCursor(mCursor = null);

        } else {
            // Display the results of the query.

            // формируем столбцы сопоставления
            // form matching columns
            String[] from = new String[]{DBContract.FeedEntry.COLUMN_DANGER,
                    DBContract.FeedEntry.COLUMN_DESCRIPTION};

            int[] to = new int[]{R.id.imageViewList, R.id.textList};

            // создааем адаптер и настраиваем список
            // create an adapter and set up a list
            SimpleCursorAdapter scAdapter = new SimpleCursorAdapter
                    (this, R.layout.item_list, mCursor, from, to);
            lvData.setAdapter(scAdapter);

        }

    }

    /**
     * handling button clicks "Refresh", "Add" and "delete"
     */
    View.OnClickListener viewClickListener = v -> {
        switch (v.getId()) {
            case R.id.butRefresh:

                displayCurrent();
                break;

            case R.id.butAdd:

                addCurrentPosition();
                break;

            case R.id.butDelete:

                deleteAll();
                break;
        }

    };

    /**
     * Добавить текущую позицию с указанием типа опасности, лолготы, ширины и описанием
     * Add current position with danger type, length, width and description
     */
    private void addCurrentPosition() {

        /*// get LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);*/

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/
        /*@SuppressLint("MissingPermission") Location location = MainActivity.locationManager.getLastKnownLocation(String.valueOf(providers));

        // Current location Coordinates
        double lat = location.getLatitude();
        double lon = location.getLongitude();

        // logs
        Log.d("myLogs", "Широта: " + lat + "Долгота: " + lon);*/


        // Call
        Intent intent = new Intent();

        startActivityForResult(intent, 1);



    }

    /**
     * This method is run when the user clicks the "Delete All" button
     */
    private void deleteAll() {

        int numDeleted = MainActivity.mContentResolver.delete(DBContract.FeedEntry.CONTENT_URI,
                null, null);

        // Сообщаем пользователю, сколько символов было удалено.
        // Inform the user how many characters were deleted.
        Toast.makeText(this,
                "Deleted "
                        + numDeleted
                        + " position",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * loading danger of default
     */
    public void loadingDefault() {

        // loading danger of default
        // we fill the database "Chernobyl"
        ContentValues cvs = new ContentValues();
        cvs.put(DBContract.FeedEntry.COLUMN_DANGER, R.mipmap.ic_launcher_round_round);
        cvs.put(DBContract.FeedEntry.COLUMN_LATITUDE, 51.4045032);
        cvs.put(DBContract.FeedEntry.COLUMN_LONGITUDE, 30.0542331);
        cvs.put(DBContract.FeedEntry.COLUMN_DESCRIPTION, "Chernobyl");
        MainActivity.mContentResolver.insert(DBContract.FeedEntry.CONTENT_URI, cvs);


        // we fill the database "Fukushima"
//        ContentValues cvs1 = new ContentValues();
        cvs.put(DBContract.FeedEntry.COLUMN_DANGER, R.mipmap.ic_launcher_round_round);
        cvs.put(DBContract.FeedEntry.COLUMN_LATITUDE, 37.760799);
        cvs.put(DBContract.FeedEntry.COLUMN_LONGITUDE, 140.474785);
        cvs.put(DBContract.FeedEntry.COLUMN_DESCRIPTION, "Fukushima");
        MainActivity.mContentResolver.insert(DBContract.FeedEntry.CONTENT_URI, cvs);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        // Check if the started Activity completed successfully and
        // the request code is what we're expecting.
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK
                && requestCode == 1) {
            // DisplayCurrent

        }


    }

}