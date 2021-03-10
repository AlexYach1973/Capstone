package org.coursera.sustainableapps.caostoneproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Receiver обрабатывает сообщения от сервиса о изменении текущего положения
 * Receiver processes messages from the service about changing the current position
 */

public class PositionReceiver extends BroadcastReceiver {

    /**
     * фильтр на которые реагирует Receiver
     * filter to which Receiver responds
     */
    public final static String ACTION_CURRENT_POSITION = "org.coursera.CURRENT_POSITION";

    /**
     * поле, по которому определяется откуда пришел вызов
     * the field by which the call came from
     */
    public static String SOURCE;

    /**
     * ссылка на Observe, чтобы вызвать метод оттуда и передать данные
     * reference to Observe to call the method from there and pass the data
     */
    private final Observe mObserve;


    /**
     * Constructor sets the fields.
     */
    public PositionReceiver(Observe activity) {
        mObserve = activity;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");

        // Get SOURCE from Intent
        String sSource = intent.getStringExtra(SOURCE);

        switch (sSource) {
            case Observe.FROM_OBSERVE:

                Log.d("myLogs", "Запустили BroadcastReceiver из Observe");

                // Запускаем Сервис
                // Start Service
                startObserveService(context);

                break;

            case PositionService.FROM_SERVICE:

                Log.d("myLogs", "ОТветил IntentService в BroadcastReceiver");

                // Что-то делаем...
                // и отправляем назад в Observe, вызывая его метод
                mObserve.calculateDistance("Observe: Lat, Long");


                break;
        }

    }

    private void startObserveService(Context context) {
        context.startService(PositionService.makePositionReceiver(context));
    }

    /**
     * фабричный метод создания намерения, который посылается из Observe в PositionReceiver
     * Factory method for creating an intent that is sent from Observe to PositionReceiver
     */
    public static Intent makeObserveIntent(Context context, String source) {

        return new Intent(PositionReceiver.ACTION_CURRENT_POSITION)
                // Add extras.
                .putExtra(PositionReceiver.SOURCE, source)
                // Limit receivers to components in this app's package.
                .setPackage(context.getPackageName());
    }

    /**
     * фабричный метод создания намерения, который посылается из Service в PositionReceiver
     * Factory method for creating an intent that is sent from Service to PositionReceiver
     */
    public static Intent makeServiceIntent(Context context, String source) {

        return new Intent(PositionReceiver.ACTION_CURRENT_POSITION)
                // Add extras.
                .putExtra(PositionReceiver.SOURCE, source)
                // Limit receivers to components in this app's package.
                .setPackage(context.getPackageName());
    }

}