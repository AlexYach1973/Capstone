package org.coursera.sustainableapps.caostoneproject;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Этот класс взаимодействует с связанным сервисом и получает от него широту и долготу
 * текущего положения. Расчитывает расстояния между текущеи положением и точками опасности
 * из базы данных.
 * ***********************************************************************************
 * This class interacts with the bound service and gets the latitude and longitude
 * of the current position from it. Calculates the distance between the current location
 * and the danger points from the database.
 */

@SuppressWarnings("ALL")
public class Observe extends AppCompatActivity {

    //
    private static final String DISTANCE = "distance";

    /**
     * Used for debugging.
     */
    private final String TAG = "myLogs";

    //    Field ContentResolver
    private static ContentResolver mContentResolver;

    // Button
    private ImageButton imageButtonEye, imageButtonEyeEye;

    /**
     * ListView to display the database
     *для отображения базы данных
     */
    ListView listObserve;

    // List for Latitude and Longitude data
    ArrayList<Map<String, Object>> dataListLatLong = new ArrayList<>();

    /**
     * RecyclerView;
     */
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    // ArrayList
    private ArrayList<RecyclerObserveItem> recyclerObserveItems;

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
//            Log.d(TAG, "ComponentName: " + className);
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

        /** initialize RecyclerView */
        recyclerView = findViewById(R.id.recyclerViewObserve);

        // initialize Buttons
//        buttonUpdate = findViewById(R.id.btnObserveStart);
        imageButtonEye = findViewById(R.id.buttonImageEye);
        imageButtonEyeEye = findViewById(R.id.buttonImageEyeEye);

        // assign a listener
//        buttonUpdate.setOnClickListener(viewClickListener);
        imageButtonEye.setOnClickListener(viewClickListener);

        // Initialize the reply messenger.
        mReplyMessenger = new Messenger(new ReplyHandler(this));

        // отобразить базу данных без расстояний
        // display database without distances
        displayDbWithoutDist();

        // Run BindService method
        Log.d(TAG, "calling bindService(): Привет Service!");
        if (mReqMessengerRef == null) {

            // Bind to the PositionBindService associated with this Intent.
            bindService(PositionBindService.makeObserveIntent(this),
                    mSvcConn,
                    Context.BIND_AUTO_CREATE);
        }

    }

    /**
     * Hook method called by Android when this activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
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
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
        // Unbind from the Service.
        unbindService(mSvcConn);

        Log.d(TAG, "Service отвязали");

        Log.d(TAG, "Observe DESTROYED !!!");
    }
    /**
     * В этом методе считываем данные из базы данных. Получаем курсор
     *  In this method, we read data from the database. Get the cursor
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

            // pass the cursor and fetch data from the databaseand Display
            // передаем курсор и извлекаем данные из базы данных и отображаем
            extractDataFromCursor(mCursor);

        }

    }

    /**
     * In this method, we extract data from the cursor and display
     * В этом методе извлекаем данные из курсора и выводим на экран
     */
    private void extractDataFromCursor(Cursor mCursor) {

        /**  initialize ArrayList */
        recyclerObserveItems = new ArrayList<>();

        // recycleItems without distance
        ArrayList<RecyclerObserveItem> recycleItems = Utils.fillArrayListFromCursor(mCursor);

        for (RecyclerObserveItem data : recycleItems) {
            // fill recyclerObserveItems
            recyclerObserveItems.add(new RecyclerObserveItem(data.getImage(),
                    data.getIdCurrent(),
                    data.getLat(),
                    data.getLng(),
                    data.getDescription(),
                     "-- "));

        }

        // intialize RecyclerAdapter
//        recyclerView.setHasFixedSize(true); // если размер не меняется
        recyclerAdapter = new RecyclerViewAdapter(recyclerObserveItems,this, false);
        layoutManager = new LinearLayoutManager(this);

        // Display
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     * Обработка нажатий на кнопки
     * mBtnStart - запускает BindService.
     * Handling button clicks
     * mBtnStart - Launches BindService
     */
    @SuppressLint("NonConstantResourceId")
    View.OnClickListener viewClickListener = v -> {

                // Show ImageButtonEye
                Utils.showImage(imageButtonEyeEye);

                try {
                    startPositionBindService();
                } catch (RemoteException e) {
                    e.printStackTrace();
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
     * Этот метод получает текущие координаты из и подсчитывает расстояния до точек опасности,
     * которые записаны в базе данных. И выводит их.
     * This method gets the current coordinates from and calculates the distances
     * to the danger points that are recorded in the database. And displays them
     */
    public void calculateDistance(double currentLat, double currentLong) {

        /**
         *  initialize AarrayList
         */
        recyclerObserveItems = new ArrayList<>();

        /**
         *  Calculates distance and add it to the recyclerObserveItem
         */
        // Current Location
        Location locCurrent = new Location("");
        locCurrent.setLatitude(currentLat);
        locCurrent.setLongitude(currentLong);

        // считываем данные из базы данных
        // read data from the database
        Cursor mCursor = mContentResolver.query(DBContract.FeedEntry.CONTENT_URI,
                DBContract.FeedEntry.sColumnsToDisplay,
                null, null, null);

        // recycleItems without distsnce
        ArrayList<RecyclerObserveItem> recycleItems = Utils.fillArrayListFromCursor(mCursor);

        // ArrayList recyclerObserveItems for Display
        for (RecyclerObserveItem data : recycleItems) {
            // Caluate distance
            // Location of danger
            Location locDataBase = new Location("");
            // Location of danger
            locDataBase.setLatitude(Double.valueOf(data.getLat()));
            locDataBase.setLongitude(Double.valueOf(data.getLng()));
            double distance = locDataBase.distanceTo(locCurrent);

            // set distance
            data.setMeters(String.valueOf(distance));

            // fill recyclerObserveItems
            recyclerObserveItems.add(new RecyclerObserveItem(data.getImage(),
                    data.getIdCurrent(),
                    data.getLat(),
                    data.getLng(),
                    data.getDescription(),
                    String.valueOf( Math.round(distance)) + " m"));

            //move to next line
            mCursor.moveToNext();
        }

        // intialize RecyclerAdapter
        recyclerAdapter = new RecyclerViewAdapter(recyclerObserveItems,this, false);
//        layoutManager = new LinearLayoutManager(this);

        // Dysplay
        recyclerView.setAdapter(recyclerAdapter);
//        recyclerView.setLayoutManager(layoutManager);

    }


//    private ArrayList fillArrayListFromCursor(Cursor mCursor) {
//
//        ArrayList<RecyclerObserveItem> recycleArrayList = new ArrayList<>();
//
//        // First line
//        mCursor.moveToFirst();
//        // to the end of the table
//        while (!mCursor.isAfterLast()) {
//
//            // the same as in extractDataFromCursor()
//            // Icon Danger
//            int presentDamage = mCursor.getInt(mCursor.getColumnIndex(
//                    DBContract.FeedEntry.COLUMN_DANGER));
//
//            // Use String.valueOf()
//            int presentId = mCursor.getInt(mCursor.getColumnIndex(
//                    DBContract.FeedEntry._ID));
//
//            // Double Lat and Long
//            Double presentLat = mCursor.getDouble(mCursor.getColumnIndex(
//                    DBContract.FeedEntry.COLUMN_LATITUDE));
//            Double presentLng = mCursor.getDouble(mCursor.getColumnIndex(
//                    DBContract.FeedEntry.COLUMN_LONGITUDE));
//
//            // Description
//            String presentDescription = mCursor.getString(mCursor.getColumnIndex(
//                    DBContract.FeedEntry.COLUMN_DESCRIPTION));
//
//            recycleArrayList.add(new RecyclerObserveItem(presentDamage,
//                    presentId,
//                    presentLat,
//                    presentLng,
//                    presentDescription,
//                    ""));
//
//            //move to next line
//            mCursor.moveToNext();
//
//        }
//        return recycleArrayList;
//    }

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
        private final Observe observe;

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

            if (currentLat != 0) {

                // calculate Distance
                observe.calculateDistance(currentLat, currentLong);
            } else {
                Toast.makeText(observe.getApplicationContext(),
                        "wait 5 sec and try again",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

}