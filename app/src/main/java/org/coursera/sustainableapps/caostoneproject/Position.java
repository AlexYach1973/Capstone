package org.coursera.sustainableapps.caostoneproject;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class Position extends AppCompatActivity {

    /**
     *  В этом классе используем внутренний класс - LocalBroadcastReceiver,
     *  в котором получаем текущие координаты
     *  In this class, we use the inner class - LocalBroadcastReceiver,
     *  in which we get the current coordinates
     */
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
    ImageView imageMap;

    // позиции геолокации
    // geolocation positions
    double geoLan, geoLong;

    // строка адресс местоположния
    // string location address
    String goMap;

    // содержимое спинера
    // contents spinner
//    String[] spData = getResources().getStringArray(R.array.spinner_danger); // не работает
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
        imageMap = findViewById(R.id.imageMap);

        // assign a listener
        btnCurrentMap.setOnClickListener(viewClickListener);
        btnOk.setOnClickListener(viewClickListener);

        /**
         * initialize and register BroadcastReceiver
         */
        mPositionReceiver = new PositionReceiver(this);
        registerPositionReceiver();
        /**
         * Start BroadcastReceiver with ACTION_
         */
        LocalBroadcastManager.getInstance(Position.this)
                .sendBroadcast(new Intent(ACTION_POSITION_RECEIVER));

        /**
         * set Spinner configuration
         */
        spinnerConfiguration();

    }

        /**
         * Spinner
         */
        private void spinnerConfiguration(){
        // адаптер для спиннера
        // spinner adapter
      /*  ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spData);

        // указываем какой layout использовать для прорисовки пунктов выпадающего списка.
        // specify which layout to use for drawing the items of the drop-down list.
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

            // NEW SpinnerItem
            ArrayAdapter<String> spAdapter = new ArrayAdapter<>(this,
                    R.layout.spinner_item, R.id.spinnerTextDanger, spData);

        // привязываем спиннер к адаптеру
        // attach the spinner to the adapter
        spDanger.setAdapter(spAdapter);

        // Title
        spDanger.setPrompt("DANGER");
        // select the element
        spDanger.setSelection(0,true);

        // устанавливаем обработчик нажатия
        // set the click handler
        spDanger.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                positionDanger = position;
                // Choice image
                switch (positionDanger) {
                    case 0:
                        // "Radiation"
                        imageMap.setImageResource(R.mipmap.ic_launcher_round_foreground);
                        break;

                    case 1:
                        // "Biodefense"
                        imageMap.setImageResource(R.mipmap.ic_launcher_bio_foreground);
                        break;

                    case 2:
                        // "Chemical danger"
                        imageMap.setImageResource(R.mipmap.ic_launcher_chem_foreground);
                        break;

                    case 3:
                        // "Laser danger"
                        imageMap.setImageResource(R.mipmap.ic_launcher_laser_foreground);
                        break;

                    case 4:
                        // "Electromagnetic"
                        imageMap.setImageResource(R.mipmap.ic_launcher_magnetics_foreground);
                        break;

                    case 5:
                        // "Radio wave"
                        imageMap.setImageResource(R.mipmap.ic_launcher_radio_foreground);
                        break;
                }
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

       // Строки для вывода
       String pos1 = "Latitude= " + Math.round(geoLan * 1000)/1000.0;
       String pos2 = "Longitude= " + Math.round(geoLong * 1000)/1000.0;
       // Вывод
       textLan.setText(pos1);
       textLong.setText(pos2);

       // Строка для карты
       goMap = "http://www.google.com/maps/@" + geoLan +
               "," + geoLong + "," + 15 + "z";
   }

    // обработка нажатий кнопок
    // handling button clicks
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

       /* // Проверка расчета расстояния по долготе и широте
       // Мой дом, Оболонсий пр-т 16А, Киев
        Location locA = new Location("");
        locA.setLatitude(50.4708);
        locA.setLongitude(30.5075);
        // Крещатик, 14, Киев
        Location locB = new Location("");
        locB.setLatitude(50.4514);
        locB.setLongitude(30.5252);
        double dist = locA.distanceTo(locB);
        return String.valueOf(dist);*/

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
     *
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

            /**
             * LocationManager, LocationListener
             */
        // подключение к сервису. connection to the service
        // Сервис определения географического расположения
        // Geolocation service
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // create a LocationListener
        // Слушатель. Listener
        LocationListener locationListener = new myLocationListener();

        // Проверка разрешений
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // команда, которая срабатывает при корректировки данных (5 с или 10 м)
        // command that is triggered when data is corrected (5 s or 10 m)
        locationManager.requestLocationUpdates
                (LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }
        /**
         * обязательные методы для LocationListener
         * required methods for LocationListener
         */
        private class myLocationListener implements LocationListener {

            @Override
            public void onLocationChanged(@NonNull Location location) {

                // координаты
                geoLan = location.getLatitude();
                geoLong = location.getLongitude();

                // Calling method
                mPositionActivity.displayLatLong(geoLan, geoLong);
            }
        }

    }


}