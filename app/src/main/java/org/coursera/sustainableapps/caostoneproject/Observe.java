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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static org.coursera.sustainableapps.caostoneproject.DBContract.FeedEntry;

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

    // Spinner
    Spinner spObserve;
    // содержимое спинера. contents spinner
    private final Integer[] spDataObserve = {
            R.mipmap.respirator_foreground,
            R.mipmap.ic_launcher_round_foreground,
            R.mipmap.ic_launcher_bio_foreground,
            R.mipmap.ic_launcher_chem_foreground,
            R.mipmap.ic_launcher_laser_foreground,
            R.mipmap.ic_launcher_magnetics_foreground,
            R.mipmap.ic_launcher_radio_foreground};

    // sorting the database (query) display using a spinner
    private String[] strSelectionArgs = null;
    private String strSelection = null;

    // TextViewю Пока не используем
//    TextView textViewSort;

    /**
     * RecyclerView;
     */
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    // ArrayList
    private ArrayList<RecyclerObserveItem> recyclerObserveItems;

    /**
     * Boolean field.
     * Specifies the long or short representation of the data on the screen.
     * This field is passed to RecyclerViewAdapter()
     *  if bool = true - long text
     * if bool = false - short text
     */
    private Boolean long_short_text = true;

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

        // initialize ImageButtons
        imageButtonEye = findViewById(R.id.buttonImageEye);
        imageButtonEyeEye = findViewById(R.id.buttonImageEyeEye);

        // initialize TextView
//        textViewSort = findViewById(R.id.textViewSort);

        /**
         * initialize Spinner
         */
        spObserve = findViewById(R.id.spinnerObserve);
        // Spinner configuration
        spinnerConfigurationObserve();

        // assign a listener
        imageButtonEye.setOnClickListener(viewClickListener);
//        textViewSort.setOnClickListener(viewClickListener);

        // Initialize the reply messenger.
        mReplyMessenger = new Messenger(new ReplyHandler(this));

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
     * Spinner configuration
     */
    private void spinnerConfigurationObserve(){
        // адаптер для спиннера. spinner adapter
        // устанавливаем свое отображения списка
        // set our display of the list (R.layout.spinner_item)
        MyIconAdapter spAdapter = new MyIconAdapter(this,
                R.layout.spinner_item_observe, spDataObserve);

        // привязываем спиннер к адаптеру
        // attach the spinner to the adapter
        spObserve.setAdapter(spAdapter);

        // set "all elements"
        spObserve.setSelection(0);

        // устанавливаем обработчик нажатия. set the click handler
        spObserve.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // convert the spinner's position to the ID of the hazard icon
                // using the Urils.class helper method

                int positionToIdIcon = 0;
                if (position == 0) {
                    strSelection = null;
                    strSelectionArgs = null;

                } else {
                     positionToIdIcon = Utils.imageDamageForItemSpinner(position - 1);
                    strSelection = FeedEntry.COLUMN_DANGER;
                    strSelectionArgs = new String[]{String.valueOf(positionToIdIcon)};
                }
                // отобразить базу данных без расстояний
                // display database without distances
                displayDbWithoutDist();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_observe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_observe_short:
                long_short_text = false;
                displayDbWithoutDist();

                Log.d(TAG,"menu short");
                return true;

            case R.id.menu_obserev_long:
                long_short_text = true;
                displayDbWithoutDist();

                Log.d(TAG,"menu long");
                return true;
        }

        return super.onOptionsItemSelected(item);
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
        Cursor mCursor = mContentResolver.query(FeedEntry.CONTENT_URI,
                FeedEntry.sColumnsToDisplay,
                strSelection, strSelectionArgs, null);

        if (mCursor.getCount() == 0) {
            // Inform the user if there's nothing to display.
            Toast.makeText(this,
                    "No items to display",
                    Toast.LENGTH_SHORT).show();

        } else {

            // pass the cursor and fetch data from the databaseand Display
            // передаем курсор и извлекаем данные из базы данных и отображаем
            extractDataFromCursor(mCursor);
            mCursor.close();

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
        mCursor.close();

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
        recyclerAdapter = new RecyclerViewAdapter(recyclerObserveItems,this,
                false, long_short_text);
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

        switch (v.getId()) {

            case R.id.buttonImageEye:
                // Show ImageButtonEye
                Utils.showImage(imageButtonEyeEye);

                try {
                    startPositionBindService();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
//            case R.id.textViewSort:
//
//
//                Log.d(TAG, "selected sorting DataBase");
//                break;
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
    private void calculateDistanceAndDisplay(double currentLat, double currentLong) {

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
        Cursor mCursor = mContentResolver.query(FeedEntry.CONTENT_URI,
                FeedEntry.sColumnsToDisplay,
                strSelection, strSelectionArgs, null);

        // recycleItems without distsnce
        ArrayList<RecyclerObserveItem> recycleItems = Utils.fillArrayListFromCursor(mCursor);
        mCursor.close();

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
                    String.valueOf(Math.round(distance))));

        }

        // intialize RecyclerAdapter
        recyclerAdapter = new RecyclerViewAdapter(recyclerObserveItems,this,
                false, long_short_text);
//        layoutManager = new LinearLayoutManager(this);

        // Dysplay
        recyclerView.setAdapter(recyclerAdapter);
//        recyclerView.setLayoutManager(layoutManager);

    }

    /**
     * creat MyIconAdapter for spinnerObserve
     */
    public class MyIconAdapter extends ArrayAdapter {

        // Constructor
        public MyIconAdapter(Context context, int resource, Object[] objects) {
            super(context, resource, objects);
        }
        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {

            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_item_observe, parent, false);

            ImageView icon = (ImageView) row.findViewById(R.id.spinnerImageObserve);
            icon.setImageResource(spDataObserve[position]);

            return row;
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
                observe.calculateDistanceAndDisplay(currentLat, currentLong);
            } else {
                Toast.makeText(observe.getApplicationContext(),
                        "wait 5 sec and try again",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

}