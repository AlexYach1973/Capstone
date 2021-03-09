package org.coursera.sustainableapps.caostoneproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Observe extends AppCompatActivity {

    //    Field ContentResolver
    private static ContentResolver mContentResolver;

    // Button
    Button mBtnStart, mBtn2;

    /**
     * An instance of a broadcast receiver.
     */
    private PositionReceiver mPositionReceiver;

    /**
     * ListView to display the database
     *для отображения базы данных
     */
    ListView listObserve;

    // поле, в котором содержится дистанция между текущим положением и danger из базы данных
    // a field that contains the distance between the current position and the danger from the database
//    String  distanceList;// = "meter";

    // формируем столбцы сопоставления
    // form matching columns "from" and "to"
    String[] from = new String[]{DBContract.FeedEntry.COLUMN_DANGER,
            DBContract.FeedEntry.COLUMN_DESCRIPTION};
    int[] to = new int[]{R.id.imageListObserved, R.id.textViewDescrObserved};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observe);

        mContentResolver = getContentResolver();

        // initialize ListView
        listObserve = findViewById(R.id.listObserve);

        // initialize Buttons
        mBtnStart = findViewById(R.id.btnObserveStart);
        mBtn2 = findViewById(R.id.button2Observe);
        // assign a listener
        mBtnStart.setOnClickListener(viewClickListener);
        mBtn2.setOnClickListener(viewClickListener);

        /**
         * PositionReceiver
         */
        // Initialize the PingReceiver.
        mPositionReceiver = new PositionReceiver(this);

        // отобразить базу данных и расстояния
        // display database and distances
//        displayObserved();

        // Register the PositionReceiver
        registeredPositionReceiver();

        // отобразить базу данных бе расстояний
        // display database without distances
        displayDbWithoutDist();

    }

    /**
     * Hook method called after onStart(), just before the activity
     * (re)gains focus
     */
    protected void onResume() {

        super.onResume();

    }

    protected void onDestroy() {

        super.onDestroy();

        Log.d("myLogs", "Observe DESTROYED !!!");
    }

    /**
     * Hook method called when activity is about to lose
     * focus. Release resources that may cause a memory leak.
     */
    protected void onPause() {

        super.onPause();

        // Unregister the PingReceiver.
        unregisterReceiver(mPositionReceiver);
    }

    /**
     * Register the PositionReceiver dynamically
     */
    private void registeredPositionReceiver(){
        // Create an intent filter for ACTION_VIEW_PING.
        IntentFilter intentFilter =
                new IntentFilter(PositionReceiver.ACTION_CURRENT_POSITION);

        // Register the receiver and the intent filter.
        registerReceiver(mPositionReceiver,
                intentFilter);

    }

    /**
     *В этом этом методе выводятся точки опасностей из базы данных без расчета расстояний до них.
     *  This method retrieves hazard points from the database without calculating distances to them.
     */
    private void displayDbWithoutDist() {

        // считываем данные из базы данных
        // read data from the database
        Cursor mCursor = mContentResolver.query(DBContract.FeedEntry.CONTENT_URI,
                DBContract.FeedEntry.sColumnsToDisplay,
                null, null, null);

        if (mCursor.getCount() == 0) {
            // Inform the user if there's nothing to display.
            Toast.makeText(this,
                    "No items to display",
                    Toast.LENGTH_SHORT).show();

        } else {

            // Display the results of the query.

            // создааем адаптер и настраиваем список и отображаем
            // create an adapter and set up a list and display
            SimpleCursorAdapter scAdapter = new SimpleCursorAdapter
                    (this, R.layout.item_list_observed, mCursor, from, to, 0);
            listObserve.setAdapter(scAdapter);

        }
    }

    /**
     * Обработка нажатий на кнопки
     * mBtnStart - запускает BroadcastReceiver
     * Handling button clicks
     * mBtnStart - Launches BroadcastReceiver
     */
    View.OnClickListener viewClickListener = v -> {
        switch (v.getId()) {

            case R.id.btnObserveStart:

                // StartPositionReceiver
                startPositionReceiver();

                break;

            case R.id.button2Observe:

                break;
        }
    };

    private void startPositionReceiver() {

    }


    /**
     * метод, который  отображает записанные в базе данных точки и расстояния до них
     * от текущей позиции
     * a method that displays the points recorded in the database and the distances
     * to them from the current position
     */
    private void displayObserved() {

        // считываем данные из базы данных
        // read data from the database
        Cursor mCursor = mContentResolver.query(DBContract.FeedEntry.CONTENT_URI,
                DBContract.FeedEntry.sColumnsToDisplay,
                null, null, null);

        // Inform the user if there's nothing to display.
        if (mCursor.getCount() == 0) {
            Toast.makeText(this,
                    "No items to display",
                    Toast.LENGTH_SHORT).show();

        } else {

            // Извлекаем  данные из курсора
            //Retrieving data from the cursor
            ArrayList<Map<String, Object>> dataList = extractDataFromCursor(mCursor);

            // Display the results of the query.

           /* // создааем адаптер и настраиваем список
            // create an adapter and set up a list
            SimpleCursorAdapter scAdapter = new SimpleCursorAdapter
                    (this, R.layout.item_list_observed, mCursor, from, to, 0);
            listObserve.setAdapter(scAdapter);*/

        }

    }

    /**
     * В этом методе извлекаем данные из базы данных и записываем их в коллекцию МАР объектов.
     * Извлеченные данные широты и долготы используем для определения расстояния между
     * текущей позиции и точками в базе данных. Полученные данные тоже записываем в коллекцию МАР.
     * Далее используем SimpleAdapter для отображения точек опасности из базы данных и
     * расстояний для них в активности Observe. Возвращаем МАР
     * In this method, we retrieve data from the database and write it to a collection of MAP objects.
     * We use the extracted latitude and longitude data to determine the distance between
     * the current position and points in the database. We also write the received data
     * into the MAP collection. Next, we use SimpleAdapter to display danger points
     * from the database and distances for them in the Observe Activity. Return Map object
     * @param mCursor extracted data from database
     */
    private ArrayList<Map<String, Object>> extractDataFromCursor(Cursor mCursor) {

        // List for Latitude and Longitude data
        ArrayList<Map<String, Object>> dataListLatLong = new ArrayList<>();

        // extract Latitude and Longitude
        Map<String, Object> dataMapLatLong;

            // First line
            mCursor.moveToFirst();

            // to the end of the table
            while (!mCursor.isAfterLast()) {
                dataMapLatLong = new HashMap<>();

                // Id для проверки
                dataMapLatLong.put(DBContract.FeedEntry._ID,
                        mCursor.getDouble(mCursor.getColumnIndex(
                                DBContract.FeedEntry._ID)));

                // широта (Latitude)
                dataMapLatLong.put(DBContract.FeedEntry.COLUMN_LATITUDE,
                        mCursor.getDouble(mCursor.getColumnIndex(
                                DBContract.FeedEntry.COLUMN_LATITUDE)));
                // долгота (Longitude)
                dataMapLatLong.put(DBContract.FeedEntry.COLUMN_LONGITUDE,
                        mCursor.getDouble(mCursor.getColumnIndex(
                                DBContract.FeedEntry.COLUMN_LONGITUDE)));
                //Add Map to List
                dataListLatLong.add(dataMapLatLong);

                //move to next line
                mCursor.moveToNext();
            }

        //Посылаем dataListLatLong в метод distanceCalculation() и получаем обновленный distanceList
        //  с расстояниями от точек опасности до текущей позиции
        // We send dataListLatLong to the method distanceCalculation() and get the updated
        // distanceList with the distances from the danger points to the current position
        ArrayList<Map<String, Object>> distanceList = distanceCalculation(dataListLatLong);

            return null; //dataListLatLong;
    }

    /**
     * в этом методе делаем расчет расстояния от текущего положения до точек опасности из базы данных,
     *  используя широту и долготу (метод distanceTo)
     *  in this method, we calculate the distance from the current position to danger points
     *  from the database using latitude and longitude (distanceTo method)
     * @param list- входные данные широты и долготы из базы данных
     * @param list- input latitude and longitude data from the database
     * @return return updated lis (eg listDistance) with distances
     */
    private ArrayList<Map<String, Object>>
    distanceCalculation(ArrayList<Map<String, Object>> list) {




        return null;
    }
}