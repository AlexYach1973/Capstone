package org.coursera.sustainableapps.caostoneproject;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
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
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
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
//    FloatingActionButton buttonUpdate;
    ImageButton imageButtonEye;

    /**
     * ListView to display the database
     *для отображения базы данных
     */
    ListView listObserve;

    // List for Latitude and Longitude data
    ArrayList<Map<String, Object>> dataListLatLong = new ArrayList<>();


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

        // initialize ListView
        listObserve = findViewById(R.id.listObserve);

        // initialize Buttons
//        buttonUpdate = findViewById(R.id.btnObserveStart);
        imageButtonEye = findViewById(R.id.buttonImageEye);
        // assign a listener
//        buttonUpdate.setOnClickListener(viewClickListener);
        imageButtonEye.setOnClickListener(viewClickListener);

        // Initialize the reply messenger.
        mReplyMessenger = new Messenger(new ReplyHandler(this));

        // отобразить базу данных без расстояний
        // display database without distances
        displayDbWithoutDist();

        // processing of clicking a list item
        listObserve.setOnItemClickListener((parent, view, position, id) -> {
            // call the google map display method
            displaySelectedItem(position);

            Log.d(TAG, "id_selected: " + id + "; " + "position: " + position);

        });

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
     * В этом этом методе выводятся точки опасностей из базы данных без расчета расстояний до них.
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

//        Retrieving data from the cursor for further use
//        Извлекаем данные из курсора для дальнейшего использования
        extractDataFromCursor(mCursor);

    }

    /**
     * In this method, we extract the latitude and longitude from the cursor and
     * add them to Map<String, Object> to calculate the distance of the danger points from the current location
     * we also add the name and description of the danger to the Map<String, Object>,
     * for display using SimpleAdapter
     * В этом методе извлекаем широту и долготу из курсора и добавляем их в Map<String, Object>
     * для оасчета расстояния точек опасности от текущего местоположения
     * также добавляем в Map<String, Object> наименование и описание опасности,
     * для вывода на экран с помощью SimpleAdapter
     */
    private void extractDataFromCursor(Cursor mCursor) {

        // extract Latitude and Longitude
        Map<String, Object> dataMapLatLong;

        // First line
        mCursor.moveToFirst();

        // to the end of the table
        while (!mCursor.isAfterLast()) {
            dataMapLatLong = new HashMap<>();

            // Id для проверки
            dataMapLatLong.put(DBContract.FeedEntry._ID,
                    mCursor.getInt(mCursor.getColumnIndex(
                            DBContract.FeedEntry._ID)));

            // Danger
            dataMapLatLong.put(DBContract.FeedEntry.COLUMN_DANGER,
                    mCursor.getInt(mCursor.getColumnIndex(
                            DBContract.FeedEntry.COLUMN_DANGER)));

            // широта (Latitude)
            dataMapLatLong.put(DBContract.FeedEntry.COLUMN_LATITUDE,
                    mCursor.getDouble(mCursor.getColumnIndex(
                            DBContract.FeedEntry.COLUMN_LATITUDE)));
            // долгота (Longitude)
            dataMapLatLong.put(DBContract.FeedEntry.COLUMN_LONGITUDE,
                    mCursor.getDouble(mCursor.getColumnIndex(
                            DBContract.FeedEntry.COLUMN_LONGITUDE)));

            // Description
            dataMapLatLong.put(DBContract.FeedEntry.COLUMN_DESCRIPTION,
                    mCursor.getString(mCursor.getColumnIndex(
                            DBContract.FeedEntry.COLUMN_DESCRIPTION)));

            //Add Map to List
            dataListLatLong.add(dataMapLatLong);

            //move to next line
            mCursor.moveToNext();
        }

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
                Utils.showImage(imageButtonEye);

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
     * Этот метод получает текущие координаты из  и подсчитывает расстояния до точек опасности,
     * которые записаны в базе данных. И выводит их.
     * This method gets the current coordinates from and calculates the distances
     * to the danger points that are recorded in the database. And takes them out
     */
    public void calculateDistance(double currentLat, double currentLong){

        // Current Location
        Location locCurrent = new Location("");
        locCurrent.setLatitude(currentLat);
        locCurrent.setLongitude(currentLong);

        // new ArrayList
        ArrayList<Map<String, Object>> dataDistance =
                new ArrayList<>(dataListLatLong.size());

        // New Map with distances
        Map<String, Object> dataMapDist;

        // Location of danger
        Location locDataBase = new Location("");
        // go through the whole dataListLatLong
        for (Map<String, Object> data : dataListLatLong){

            // Initialize dataMapDist
            dataMapDist = new HashMap<>();

            double lat = (double) data.get(DBContract.FeedEntry.COLUMN_LATITUDE);
            double lon = (double) data.get(DBContract.FeedEntry.COLUMN_LONGITUDE);

            // Location of danger
            locDataBase.setLatitude(lat);
            locDataBase.setLongitude(lon);
            double distance = locDataBase.distanceTo(locCurrent);

            // Add to Map
            dataMapDist.put(DISTANCE, (int) distance);

            // Add to ArrayList
            dataDistance.add(dataMapDist);

//            Log.d(TAG, "" + lat + ", " + lon);

        }

        // объединяем два ArrayLists
        // combine two ArrayLists: dataListLatLong + dataDistance
        for (int i = 0; i < dataListLatLong.size(); i++) {

            // extract Map from dataListLatLong
            Map<String, Object> returnMapLatLong = dataListLatLong.get(i);
            // extract Map from dataDistance
            Map<String, Object> returnMapDistance = dataDistance.get(i);

//            insert distance from returnMapDistance into returnMapLatLong
            returnMapLatLong.put(DISTANCE, returnMapDistance.get(DISTANCE));
            // Logs
//            Log.d(TAG, " " + dataListLatLong.get(i));

            // insert returnMapLatLong into ArrayLists
            dataListLatLong.set(i, returnMapLatLong);

        }

        // Create Adapter
        SimpleAdapter sAdapter = new SimpleAdapter(this,
                dataListLatLong,
                R.layout.item_list_observed, new String[]{DBContract.FeedEntry.COLUMN_DANGER,
                DBContract.FeedEntry.COLUMN_DESCRIPTION, DISTANCE},
                new int[] {R.id.imageListObserved, R.id.textViewDescrObserved,
                        R.id.textViewDistanceObserved});

        // assign adapter
        listObserve.setAdapter(sAdapter);



    }

    private void displaySelectedItem(int position) {

        // extract selected Latitude and Longitude
        Map<String, Object> selectedMapLatLong = dataListLatLong.get(position);
        double selectedLat = (double) selectedMapLatLong.get(DBContract.FeedEntry.COLUMN_LATITUDE);
        double selectedLon = (double) selectedMapLatLong.get(DBContract.FeedEntry.COLUMN_LONGITUDE);

//        Log.d(TAG, "LatitudeSelected: " + selectedLat);

        // Строка для карты
        // String for the MapView
        String addressSelected = "http://www.google.com/maps/@" + selectedLat +
                "," + selectedLon + "," + 15 + "z";


        //  Запускаем отдельное окно с Гуугл картой
        // Launch a separate window with a google map
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(addressSelected));

        startActivity(intent);

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

//            Log.d(TAG, "широта: " + currentLat + ", " + "долгота: " + currentLong);

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