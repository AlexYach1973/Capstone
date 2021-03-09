package org.coursera.sustainableapps.caostoneproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
    private String SOURCE;

    /**
     * ссылка на Observe, чтобы вызвать метод оттуда и передать данные
     * reference to Observe to call the method from there and pass the data
     */
    private Observe mObserve;


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
        throw new UnsupportedOperationException("Not yet implemented");
    }
}