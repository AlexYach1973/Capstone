package org.coursera.sustainableapps.caostoneproject;

/**
 * Этот класс взаимодействует с связанным сервисом и получает от него широту и долготу
 * текущего положения. Расчитывает расстояния между текущеи положением и точками опасности
 * из базы данных.
 * ***********************************************************************************
 * This class interacts with the bound service and gets the latitude and longitude
 * of the current position from it. Calculates the distance between the current location
 * and the danger points from the database.
 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class Observe extends AppCompatActivity {

    /**
     * Used for debugging.
     */
    private final String TAG = "myLogs";

    //    Field ContentResolver
    private static ContentResolver mContentResolver;

    // Button
    Button mBtnStart, mBtn2;

    /**
     * ListView to display the database
     *для отображения базы данных
     */
    ListView listObserve;

    // формируем столбцы сопоставления
    // form matching columns "from" and "to"
    String[] from = new String[]{DBContract.FeedEntry.COLUMN_DANGER,
            DBContract.FeedEntry.COLUMN_DESCRIPTION};
    int[] to = new int[]{R.id.imageListObserved, R.id.textViewDescrObserved};

    /**
     * Reference to the request messenger that's implemented in the PositionBindService
     * Ссылка на мессенджер запросов, реализованный в PositionBindService
     */
    private Messenger mReqMessengerRef = null;

    /**
     * The ReplyMessenger whose reference is passed to the PositionBindService
     * and used to process replies from the service.
     * ReplyMessenger - ссылка на который передается в PositionBindService
     * и используется для обработки ответов от службы
     */
    private Messenger mReplyMessenger;

    /**
     * This ServiceConnection is used to receive a Messenger reference after
     * binding to the PositionBindService using bindService().
     *
     * Этот ServiceConnection используется для получения ссылки на Messenger
     * после привязки к PositionBindService с помощью bindService ()
     */

    private final ServiceConnection mSvcConn = new ServiceConnection() {
        /**
         * Called after the PositionBindService is connected to
         * convey the result returned from onBind().
         * Вызывается после подключения PositionBindService для передачи результата,
         * возвращенного onBind()
         */
        public void onServiceConnected(
                ComponentName className,
                IBinder binder) {
            Log.d(TAG, "ComponentName: " + className);
            Log.d(TAG, "Получил ответочку!");

            // Create a new Messenger that encapsulates the
            // returned IBinder object and store it for later use
            // in mReqMessengerRef.
            mReqMessengerRef = new Messenger(binder);
        }

        /**
         * Called if the Service crashes and is no longer
         * available.  The ServiceConnection will remain bound,
         * but the Service will not respond to any requests.
         */
        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, "Service has been disconnected");

            mReqMessengerRef = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observe);

        mContentResolver = getContentResolver();

        // initialize ListView
        listObserve = findViewById(R.id.listObserve);

        // initialize Buttons
        mBtnStart = findViewById(R.id.btnObserveStart);
        mBtn2 = findViewById(R.id.button2Observe);
        // assign a listener
        mBtnStart.setOnClickListener(viewClickListener);
        mBtn2.setOnClickListener(viewClickListener);

        // Initialize the reply messenger.
        mReplyMessenger = new Messenger(new ReplyHandler(this));

        // отобразить базу данных без расстояний
        // display database without distances
        displayDbWithoutDist();

    }

    /**
     * Hook method called by Android when this activity becomes visible.
     */
    @Override
    protected void onStart() {
        // Call to super class.
        super.onStart();

        Log.d(TAG, "calling bindService(): Привет Service!");
        if (mReqMessengerRef == null) {

            // Bind to the PositionBindService associated with this Intent.
            bindService(PositionBindService.makeObserveIntent(this),
                    mSvcConn,
                    Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * Hook method called after onStart(), just before the activity (re)gains focus
     */
    protected void onResume() {
        super.onResume();

    }

    /**
     * Hook method called when activity is about to lose
     * focus. Release resources that may cause a memory leak.
     */
    protected void onPause() {
        super.onPause();

    }

    /**
     * Hook method called by Android when this activity becomes invisible.
     */
    @Override
    protected void onStop() {
        // Unbind from the Service.
        unbindService(mSvcConn);

        // Call to super class.
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "Observe DESTROYED !!!");
    }
    /**
     *В этом этом методе выводятся точки опасностей из базы данных без расчета расстояний до них.
     *  This method retrieves hazard points from the database without calculating distances to them.
     */
    private void displayDbWithoutDist() {

        // считываем данные из базы данных
        // read data from the database
        Cursor mCursor = mContentResolver.query(DBContract.FeedEntry.CONTENT_URI,
                DBContract.FeedEntry.sColumnsToDisplay,
                null, null, null);

        if (mCursor.getCount() == 0) {
            // Inform the user if there's nothing to display.
            Toast.makeText(this,
                    "No items to display",
                    Toast.LENGTH_SHORT).show();

        } else {

            // Display the results of the query.
            // создааем адаптер и настраиваем список и отображаем
            // create an adapter and set up a list and display
            SimpleCursorAdapter scAdapter = new SimpleCursorAdapter
                    (this, R.layout.item_list_observed, mCursor, from, to, 0);
            listObserve.setAdapter(scAdapter);

        }
    }

    /**
     * Этот метод получает текущие координаты из  и подсчитывает расстояния до точек опасности,
     * которые записаны в базе данных. И выводит их.
     * This method gets the current coordinates from and calculates the distances
     * to the danger points that are recorded in the database. And takes them out
     */
    public void calculateDistance(String str){

        Log.d(TAG, str + " !!!");

    }

    /**
     * Обработка нажатий на кнопки
     * mBtnStart - запускает BindService.
     * Handling button clicks
     * mBtnStart - Launches BindService
     */
    View.OnClickListener viewClickListener = v -> {
        switch (v.getId()) {

            case R.id.btnObserveStart:

                // Start PositionBindService
                try {
                    startPositionBindService();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.button2Observe:

                break;
        }
    };

    /**
     * Calls the service to get the current coordinates
     * Вызывает сервис для получения текущих координат0
     */
    private void startPositionBindService() throws RemoteException {
        // Create a request message that indicates the
        // UniqueIdGenService should send the reply back to
        // ReplyHandler encapsulated by the mReplyMessenger.
        // Создаем сообщение запроса, указывающее, что PositionBindService
        // должен отправить ответ обратно в ReplyHandler, инкапсулированный mReplyMessenger
        Message request = Message.obtain();
        request.replyTo = mReplyMessenger;

        if (mReqMessengerRef != null) {
            Log.d(TAG, "sending message to RequestHandler");

            mReqMessengerRef.send(request);

        } else {
            Log.d(TAG, "Сервис в настоящее время не работает");
        }

    }

    /**
     * Receives the reply from the PositionBindService containing the
     * Latitude and Longitude  and displays it to the user.
     * Получает ответ от PositionBindService, содержащий широту и долготу,
     * и отображает его пользователю.
     */
    static class ReplyHandler extends Handler {

        /**
         * Logging tag.
         */
        private static final String TAG = "myLogs";

        /**
         * Reference back to the enclosing activity.
         */
        private Observe observe;

        /**
         * Constructor initializes the fields.
         */
        public ReplyHandler(Observe observe) {
            this.observe = observe;
        }

        /**
         * Callback to handle the reply from the PositionBindService.
         */
        public void handleMessage(Message reply) {

            Log.d(TAG, "Observe handleMessage: получили reply от RequestHandler");

            double currentLat = reply.getData().getDouble("LAT");
            double currentLong = reply.getData().getDouble("LONG");

            Log.d(TAG, "широта: " + currentLat + ", " + "долгота: " + currentLong);


        }

    }


}