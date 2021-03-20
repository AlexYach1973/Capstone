package org.coursera.sustainableapps.caostoneproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

//import java.util.logging.Handler;

/**
 * This class gets the current latitude and longitude and sends back to PositionBindService
 * via a reply messenger contained  in the request message
 * Этот класс получает текущую широту и долготу и отправляет обратно в PositionBindService
 * через ответный мессенджер, содержащийся в сообщении запроса.
 */
class PositionRequestHandler extends Handler {

    Context mContext;

    public double lat, lon;

    /**
     * Initialize PositionRequestHandler to generate IDs concurrently.
     */
    public PositionRequestHandler(Context context) {
        mContext = context;

    }

/**
 * Hook method called back when a request message arrives from the
 * PositionBindService.  The message it receives contains
 * the messenger used to reply to the activity.
 * Метод перехвата вызывается, когда сообщение с запросом приходит от PositionBindService.
 * Полученное сообщение содержит мессенджер, используемый для ответа на действие
 */
public void handleMessage(Message request) {
    // Store the reply messenger
    Messenger replyMessenger = request.replyTo;


//      Used for debugging.
    String TAG = "myLogs";
    Log.d(TAG,"RequestHandler: работает handleMessage");

/**     LocationManager, LocationListener
     подключение к сервису. connection to the service
     Сервис определения географического расположения.  Geolocation service
 */
    LocationManager locationManager = (LocationManager)
            mContext.getSystemService(Context.LOCATION_SERVICE);

    // create a LocationListener
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


    // Creates a Message
    Message reply = Message.obtain();

     // Add data
     Bundle dataLatLong = new Bundle();
     dataLatLong.putDouble("LAT", lat);
     dataLatLong.putDouble("LONG", lon);

     reply.setData(dataLatLong);

      // Send the reply back to the Observe
    try {
        replyMessenger.send(reply);
    } catch (RemoteException e) {
        e.printStackTrace();
    }

}
private class myLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(@NonNull Location location) {

        lat = location.getLatitude();
        lon = location.getLongitude();

    }
}

}
