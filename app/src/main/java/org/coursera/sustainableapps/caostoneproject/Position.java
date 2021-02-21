package org.coursera.sustainableapps.caostoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class Position extends AppCompatActivity {

    /**
     * GUI fields
     * поля графического интерфейса
     */
    Spinner spDanger;
    EditText editTextDescr;
    TextView textLan, textLong;
    Button btnCurrentMap, btnOk;
    WebView webView;

    // позиции геолокации
    // geolocation positions
    double geoLan, geoLong;

    // строка адресс местоположния
    // string location address
    String goMap;

    // содержимое спинера
    // contents spinner
    String[] spData = {"Radiation", "Biodefense", "Chemical danger", "Laser danger",
                    "Electromagnetic", "Radio wave"};
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
        webView = findViewById(R.id.webViewAdd);

        // assign a listener
        btnCurrentMap.setOnClickListener(viewClickListener);
        btnOk.setOnClickListener(viewClickListener);

        /**
         * Spinner
         */
        // адаптер для спиннера
        // spinner adapter
        ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spData);

        // указываем какой layout использовать для прорисовки пунктов выпадающего списка.
        // specify which layout to use for drawing the items of the drop-down list.
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // привязываем спиннер к адаптеру
        // attach the spinner to the adapter
        spDanger.setAdapter(spAdapter);

        // Title
//        spDanger.setPrompt("Danger choosing");
        // select the element
        spDanger.setSelection(0);

        // устанавливаем обработчик нажатия
        // set the click handler
        spDanger.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                positionDanger = position;
                // Logs
//                Log.d("myLogs", "" + positionDanger);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /**
         * WebView
         */
        // включаем поддержку JavaScript
        // turn on support JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // команда, которая срабатывает при корректировки данных (5 с или 10 м)
        // command that is triggered when data is corrected (5 s or 10 m)
        locationManager.requestLocationUpdates
                (LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }
    }

    /**
     * обязательные методы для LocationListener
     * required methods for LocationListener
     */
    private class myLocationListener implements LocationListener {

        String pos1, pos2;

        @Override
        public void onLocationChanged(@NonNull Location location) {

            if (location != null){
                // координаты
                geoLan = location.getLatitude();
                geoLong = location.getLongitude();
                // Строки для вывода
                pos1 = "Широта= " + Math.round(geoLan *10000)/10000.0;
                pos2 = "Долгота= " + geoLong;
                // Вывод
                textLan.setText(pos1);
                textLong.setText(pos2);
                // Строка для карты
                goMap = "http://www.google.com/maps/@" + geoLan +
                        "," + geoLong + "," + 15 + "z";

                // Потом разберусь...
//                webView.loadUrl(goMap);

            } else {
                textLan.setText("?");
                textLong.setText("?");
            }

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }
    }

    // обработка нажатий кнопок
    // handling button clicks
    View.OnClickListener viewClickListener = v -> {
      switch (v.getId()) {

          case R.id.buttonCurrentMap:

              //  Запускаем отдельное окно с Гуугл картой
              // Launch a separate window with a google map
              Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(goMap));
              startActivity(intent);
              break;

          case R.id.buttonOk:

              // Return to DataBase
              Intent intentResult = makeIntentResult();
              finish();
              break;
      }
    };

    private Intent makeIntentResult() {
        Intent intent = new Intent();
        intent.putExtra(DataBase.LATITUDE, geoLan); // double
        intent.putExtra(DataBase.LONGITUDE, geoLong); // double
        intent.putExtra(DataBase.DESCRIPTION, editTextDescr.getText().toString()); // String
        intent.putExtra(DataBase.POSITION_DANGER, positionDanger); // int
        setResult(RESULT_OK, intent);

        return intent;
    }


}