package org.coursera.sustainableapps.caostoneproject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

//import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * This class gets the current latitude and longitude and sends back to PositionBindService
 * via a reply messenger contained  in the request message
 * Этот класс получает текущую широту и долготу и отправляет обратно в PositionBindService
 * через ответный мессенджер, содержащийся в сообщении запроса.
 */
class PositionRequestHandler extends Handler {

    /**
     * Used for debugging.
     */
    private final String TAG = "myLogs";

    /**
     * Initialize PositionRequestHandler to generate IDs concurrently.
     */
    public PositionRequestHandler(Context context) {

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

    Log.d(TAG,"RequestHandler: работает handleMessage");


    // где-то получаем широту и долготу
    double currentLat = 0.1;
    double currentLong = 1.2;

     Message reply = Message.obtain();

     Bundle dataLatLong = new Bundle();
     dataLatLong.putDouble("LAT", currentLat);
     dataLatLong.putDouble("LONG", currentLong);

     reply.setData(dataLatLong);

      // Send the reply back to the Observe
    try {
        replyMessenger.send(reply);
    } catch (RemoteException e) {
        e.printStackTrace();
    }

}

}
