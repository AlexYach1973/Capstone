package org.coursera.sustainableapps.caostoneproject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

public class PositionBindService extends Service {

    /**
     * Used for debugging.
     */
    private final String TAG = "myLogs";

    /**
     * A PositionRequestHandler that processes Messages from the PositionBindService
     */
    private PositionRequestHandler mPositionRequestHandler = null;

    /**
     * A Messenger that encapsulates the PositionRequestHandler used to handle
     * request Messages sent from the Observe.
     */
    private Messenger mReqMessenger = null;

    /*public PositionBindService() {
    }*/

    /**
     * Hook method called when the Service is created.
     */
    public void onCreate(){
        // Call to super class.
        super.onCreate();

        // The Messenger encapsulates the PositionRequestHandler used to
        // handle request Messages sent from Observe.
        mPositionRequestHandler = new PositionRequestHandler(this);
        mReqMessenger = new Messenger(mPositionRequestHandler);
    }

    /**
     * Factory method that returns the underlying IBinder associated
     * with the RequestMessenger.
     */
    @Override
    public IBinder onBind(Intent intent) {

        // debug Log
        Log.d(TAG, "onBind: Привет Observe! Держи ответочку...");

        return mReqMessenger.getBinder();
    }

    /**
     * Called when the service is destroyed, which is the last call
     * the Service receives informing it to clean up any resources it holds.
     * Вызывается при уничтожении службы, что является последним вызовом,
     * который получает служба, информируя ее об очистке любых ресурсов, которые она хранит.
     */
    public void onDestroy() {
        // Call to super class.
        super.onDestroy();

        // Ensure threads used by the ThreadPoolExecutor complete and
        // are reclaimed by the system.
//        mPositionRequestHandler.shutdown();
    }


    /**
     * Factory method to make the  Intent.
     */
    public static Intent makeObserveIntent(Context context) {
        // Create the Intent that's associated to the PositionBindService class
        return new Intent(context, PositionBindService.class);
    }

}