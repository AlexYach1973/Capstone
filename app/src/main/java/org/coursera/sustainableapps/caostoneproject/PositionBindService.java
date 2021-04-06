package org.coursera.sustainableapps.caostoneproject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;

public class PositionBindService extends Service {

    /**
     * A Messenger that encapsulates the PositionRequestHandler used to handle
     * request Messages sent from the Observe.
     */
    private Messenger mReqMessenger = null;

    /**
     * Hook method called when the Service is created.
     */
    public void onCreate(){
        // Call to super class.
        super.onCreate();

        // The Messenger encapsulates the PositionRequestHandler used to
        // handle request Messages sent from Observe.
        /**
         * A PositionRequestHandler that processes Messages from the PositionBindService
         */
        PositionRequestHandler mPositionRequestHandler = new PositionRequestHandler(this);
        mReqMessenger = new Messenger(mPositionRequestHandler);
    }

    /**
     * Factory method that returns the underlying IBinder associated
     * with the RequestMessenger.
     */
    @Override
    public IBinder onBind(Intent intent) {

        return mReqMessenger.getBinder();
    }

    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * Factory method to make the  Intent.
     */
    public static Intent makeObserveIntent(Context context) {
        // Create the Intent that's associated to the PositionBindService class
        return new Intent(context, PositionBindService.class);
    }

}