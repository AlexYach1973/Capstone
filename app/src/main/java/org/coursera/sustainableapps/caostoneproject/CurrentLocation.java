package org.coursera.sustainableapps.caostoneproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class CurrentLocation {

    /**
     * Action used by the LocalBroadcastManger
     */
    private static final String ACTION_POSITION_RECEIVER = "ActionPositionReceiver";

    // позиции геолокации
    // geolocation positions
    double geoLan, geoLong;
    Context mContext;

    // Constructor
    public CurrentLocation(Context context) {
        mContext = context;
    }


    public void currentPosition(){

        /**
         * LocationManager, LocationListener
         */
        // подключение к сервису. connection to the service
        // Сервис определения географического расположения
        // Geolocation service
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);

        // create a LocationListener
        // Слушатель. Listener
        LocationListener locationListener = new myLocationListener();

        // Проверка разрешений
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission
                .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // команда, которая срабатывает при корректировки данных (0 с или 0 м)
        // command that is triggered when data is corrected (0 s or 0 m)
        locationManager.requestLocationUpdates
                (LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }

    /**
     * обязательные методы для LocationListener
     * required methods for LocationListener
     */
    private class myLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(@NonNull Location location) {

            // coordinates
            geoLan = location.getLatitude();
            geoLong = location.getLongitude();

            Intent intent = new Intent(ACTION_POSITION_RECEIVER);
            intent.putExtra("geoLan", geoLan);
            intent.putExtra("geoLong", geoLong);

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

            // Calling method
//            mPositionActivity.displayLatLong(geoLan, geoLong);
        }
    }
}
