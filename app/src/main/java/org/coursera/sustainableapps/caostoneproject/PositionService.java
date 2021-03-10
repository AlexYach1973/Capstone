package org.coursera.sustainableapps.caostoneproject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * Подкласс {@link IntentService} для обработки асинхронных запросов задач
 * в службе в отдельном потоке обработчика
 */

public class PositionService extends IntentService {

    public static final String FROM_SERVICE = "fromService";

    public PositionService() {
        super("PositionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("myLogs", "Запустили IntentService  из BroadcastReceiver");


        // Что-то делаем...
        // и отправляем назад в BroadcastReceiver...
        sendBroadcast(PositionReceiver.makeServiceIntent(this, FROM_SERVICE));

    }

/**
 * Заводской метод, который делает Intent для запуска PositionService из PositionReceiver
 * Factory method that makes the intent to start PositionService from PositionReceiver
 */
public static Intent makePositionReceiver(Context context){

    return new Intent(context, PositionService.class);
            // Add extras
//    .putExtra();
}

}