package org.coursera.sustainableapps.caostoneproject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class Position extends AppCompatActivity {

    /**
     *  В этом классе используем внутренний класс - LocalBroadcastReceiver,
     *  в котором получаем текущие координаты
     *  In this class, we use the inner class - LocalBroadcastReceiver,
     *  in which we get the current coordinates
     */

    /**
     * logical field.
     * true - calling this class to insert a new position.
     * False -  calling this class to update positions
     */
    Boolean insertUpdate;

    // data Intent from DataBase
    Bundle dataIntent;

    /**
     * Action used by the LocalBroadcastManger
     */
    private static final String ACTION_POSITION_RECEIVER = "ActionPositionReceiver";

    /**
     * An instance of a local broadcast receiver implementation that receives a broadcast intent
     */
    private BroadcastReceiver mPositionReceiver; // = new PositionReceiver();


    /**
     * GUI fields
     * поля графического интерфейса
     */
    Spinner spDanger;
    EditText editTextDescr;
    TextView textLan, textLong;
    Button btnCurrentMap, btnOk;
    ImageView imageIcon;

    // позиции геолокации
    // geolocation positions
    double geoLan, geoLong;

    // строка адресс местоположния
    // string location address
    String goMap;

    // содержимое спинера
    // contents spinner
//    String[] spData = getResources().getStringArray(R.array.spinner_danger); // не работает. does not work
     String[] spData = {"Radiation", "Biodefense", "Chemical danger", "Laser danger",
                    "Electromagnetic", "Radio wave"};

    // текущая позиция спинера
    // current spinner position
    int positionDanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

        // initialize
        spDanger = findViewById(R.id.spinnerDanger);
        editTextDescr = findViewById(R.id.editTextDescription);
        textLan = findViewById(R.id.textLatitude);
        textLong = findViewById(R.id.textLongitude);
        btnCurrentMap = findViewById(R.id.buttonCurrentMap);
        btnOk = findViewById(R.id.buttonOk);
        imageIcon = findViewById(R.id.imageIcon);

        // assign a listener
        btnCurrentMap.setOnClickListener(viewClickListener);
        btnOk.setOnClickListener(viewClickListener);

        // setting button Map and Ok is not Enabled
        btnCurrentMap.setEnabled(false);
        btnOk.setEnabled(false);

        /**
         * extracting data from DataBase.class to define insert/update
         */
        dataIntent = getIntent().getExtras();
        // set boolean field insertUpdate
        insertUpdate = dataIntent.getString(DataBase.DESCRIPTION).equals("");

        Log.d("myLogs", "Boolean update: " + insertUpdate);

//        Log.d("myLogs", "Latitude update: " +
//                dataIntent.getDouble(DataBase.LATITUDE));

/**
 * initialize and register BroadcastReceiver
 * Start CurrentLocation
 */
        // when inserting a new item starts
        if (insertUpdate) {

             // Initialize and register BroadcastReceiver
            mPositionReceiver = new PositionReceiver(this);
            registerPositionReceiver();

            // Start CurrentLocation Activity
            CurrentLocation mCurrentLocation = new CurrentLocation(this);
            mCurrentLocation.currentPosition();

        }


        /**
         * set Spinner configuration
         */
        spinnerConfiguration();
    }

    protected void onResume() {

        super.onResume();

        // information output when updating
        if (!insertUpdate) {

            // setting button Map and Ok is Enabled
            btnCurrentMap.setEnabled(true);
            btnOk.setEnabled(true);

            // Output the Descriptions
            editTextDescr.setText(dataIntent.getString(DataBase.DESCRIPTION));

            // Output the Image
            imageIcon.setImageResource(dataIntent.getInt(DataBase.POSITION_DANGER));

            // Строки для вывода
            // Output lines
            String pos1 = "Latitude= " + Math.round(
                   dataIntent.getDouble(DataBase.LATITUDE) * 1000)/1000.0;
           String pos2 = "Longitude= " + Math.round(
                   dataIntent.getDouble(DataBase.LONGITUDE) * 1000)/1000.0;
           // Output text
           textLan.setText(pos1);
           textLong.setText(pos2);

            // Строка для карты
            // String for the MapView
            goMap = "http://www.google.com/maps/@" +
                    dataIntent.getDouble(DataBase.LATITUDE) + "," +
                    dataIntent.getDouble(DataBase.LONGITUDE) + "," + 15 + "z";

            // output spinner
            spDanger.setSelection(Utils.imageDamageForItemSpinnerUpdate(
                    dataIntent.getInt(DataBase.POSITION_DANGER)));

        }
    }

        /**
         * Spinner
         */
        private void spinnerConfiguration(){
        // адаптер для спиннера. spinner adapter
            // устанавливаем свое отображения списка
            // set our display of the list (R.layout.spinner_item)
            ArrayAdapter<String> spAdapter = new ArrayAdapter<>(this,
                    R.layout.spinner_item, R.id.spinnerTextDanger, spData);

        // привязываем спиннер к адаптеру
        // attach the spinner to the adapter
        spDanger.setAdapter(spAdapter);

        // Title
        spDanger.setPrompt("DANGER"); // не работает. does not work.
        // select the element
            if (insertUpdate)
        spDanger.setSelection(0,true);

        // устанавливаем обработчик нажатия
        // set the click handler
        spDanger.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                positionDanger = position;
                // Choice an image using the Utils class method helper method
                // Выбор изображения с помощью вспомогательного метода метода класса Utils
                imageIcon.setImageResource(Utils.imageDamageForItemSpinner(positionDanger));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

   /**
     * Display Latitude and Longitude
     */
   public void displayLatLong(double geoLan, double geoLong) {

       // setting button Map and Ok is Enabled
       btnCurrentMap.setEnabled(true);
       btnOk.setEnabled(true);

       Log.d("myLogs", "LAT and LONG from BroadcastReceiver: " + geoLan +", "+geoLong);

       // Строки для вывода
       // Output lines
       if (insertUpdate) {
           String pos1 = "Latitude= " + Math.round(geoLan * 1000)/1000.0;
           String pos2 = "Longitude= " + Math.round(geoLong * 1000)/1000.0;
           // Вывод
           textLan.setText(pos1);
           textLong.setText(pos2);

           // Строка для карты
           // String for the MapView
           goMap = "http://www.google.com/maps/@" + geoLan +
                   "," + geoLong + "," + 15 + "z";

       }
   }

    // обработка нажатий кнопок
    // handling button clicks
    @SuppressLint("NonConstantResourceId")
    View.OnClickListener viewClickListener = v -> {
      switch (v.getId()) {

          case R.id.buttonCurrentMap:

              // Используем HaMeR для запуска карты текущего расположения
              // Use HaMeR to launch a map of the current location
                  displayCurrentGoogleMap(goMap);

              break;

          case R.id.buttonOk:

              // Return to DataBase
              Intent intentResult = makeIntentResult();
              setResult(RESULT_OK, intentResult);
              finish();
              break;
      }
    };

    /**
     * метод, который загружает в фоновом потоке гугл карту текущего расположения
     * используя фреймворк HaMeR
     * a method that loads a Google map of the current location in the background thread
     * using the HaMeR framework
     */
    private void displayCurrentGoogleMap(String address) {

        final Runnable downLoadGoogleMap = () -> {
            //  Запускаем отдельное окно с Гуугл картой
            // Launch a separate window with a google map
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));

            startActivity(intent);
        };
        new Thread(downLoadGoogleMap).start();
    }

    //
    private Intent makeIntentResult() {
        Intent intent = new Intent();
        intent.putExtra(DataBase.LATITUDE, geoLan); // double
        intent.putExtra(DataBase.LONGITUDE, geoLong); // double
        intent.putExtra(DataBase.DESCRIPTION, checkTextDescription()); // String
        intent.putExtra(DataBase.POSITION_DANGER, positionDanger); // int

        return intent;
    }

    // проверка введенного текста в окно Description, если null, выводим Lat, Long
    // checking the entered text in the Description window? if null, output Lat, Long
    private String checkTextDescription() {
        String strText;
        if (!editTextDescr.getText().toString().equals(""))
            strText = editTextDescr.getText().toString();
        else
            strText = "Lat= " + Math.round(geoLan *100)/100.0 + ", " +
                    "Long= " + Math.round(geoLong *100)/100.0;

        return strText;
    }

    /**
     * Hook method called when activity is about to be destroyed.
     * Release resources that may cause a memory leak
     */
    protected void onDestroy() {
        super.onDestroy();

        // Unregister the broadcast receiver.
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mPositionReceiver);

//        Log.d("myLogs", "Position.class is destroy");
    }

    /**
     * Registers a broadcast receiver instance
     */
    private void registerPositionReceiver() {
        //Create a new broadcast intent filter that will filter and
        // receive ACTION_POSITION_RECEIVER intents
        IntentFilter intentFilter =
                new IntentFilter(Position.ACTION_POSITION_RECEIVER);

        // Call the Activity class helper method to register this local receiver instance
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mPositionReceiver,
                        intentFilter);
    }

    /**
     * BroadcastReceiver
     */
    private class PositionReceiver extends BroadcastReceiver {

        private final Position mPositionActivity;

        /**
         * Constructor for PositionReceiver
         */
        public PositionReceiver(Position activity) {
            mPositionActivity = activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract the Latitude and Longitude
            geoLan = intent.getDoubleExtra("geoLan", 0.0);
            geoLong = intent.getDoubleExtra("geoLong", 0.0);

            if (geoLan !=0) {
                // Calling method
                mPositionActivity.displayLatLong(geoLan, geoLong);
            }

        }
    }

}