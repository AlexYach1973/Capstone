package org.coursera.sustainableapps.caostoneproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class DataBase extends AppCompatActivity {

    MainActivity mMainActivity;

    /**
     * fields for determining the current location.
     * поля для определение текущей локации.
     */
    public static String LATITUDE = "latitude";
    public static String LONGITUDE = "longitude";
    public static String DESCRIPTION = "description";
    public static String POSITION_DANGER = "positionDanger";

    // Request code for Position
    private static final int REQUEST_POSITION = 1;

    /**
     * ListView to display the database
     *для отображения базы данных
     */
    ListView listDanger;
    ImageView imageView;
    ListView lvData;

    // Context menu
    private static final int DELETE_ID = 1;

    // формируем столбцы сопоставления
    // form matching columns
    String[] from = new String[]{DBContract.FeedEntry.COLUMN_DANGER,
            DBContract.FeedEntry.COLUMN_DESCRIPTION};

    int[] to = new int[]{R.id.imageViewList, R.id.textList};


    DangerProvider mProvider = new DangerProvider();

    /**
     * Buttons "Refresh", "Add" and "delete"
     */
    Button mButtonRefresh, mButtonAdd, mButtonDelete;

    //    private ContentResolver mContentResolver;

    // атрибуты для Map
    // attributes for MAP
   /* public static final String ATTRIBUTE_DANGER_ROUND = "round";
    public static final String ATTRIBUTE_DANGER_BIO = "bio";
    public static final String ATTRIBUTE_DANGER_CHEM = "chem";
    public static final String ATTRIBUTE_DANGER_RADIO = "radio";
    public static final String ATTRIBUTE_DANGER_LASER = "laser";
    public static final String ATTRIBUTE_DANGER_MAGNETICS = "magnetics";*/

    /**
     * Constructor initializes the fields.
     */
    public DataBase() {
//        mContentResolver = getApplicationContext().getContentResolver();
    }

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base);

        // initialize ListView
        imageView = findViewById(R.id.imageViewList);
        listDanger = findViewById(R.id.listDanger);
        lvData = (ListView) findViewById(R.id.listDanger);

        // initialize Buttons
        mButtonRefresh = findViewById(R.id.butRefresh);
        mButtonAdd = findViewById(R.id.butAdd);
        mButtonDelete = findViewById(R.id.butDelete);
        // assign a listener
        mButtonRefresh.setOnClickListener(viewClickListener);
        mButtonAdd.setOnClickListener(viewClickListener);
        mButtonDelete.setOnClickListener(viewClickListener);

        // добавляем контекстное меню к списку
        // add a context menu to the list
        registerForContextMenu(lvData);



//        loadingDefault();
        displayCurrent();

    }

    /**
     * context menu
     */
    // оздаем контекстное меню
    // creating a context menu
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.delete_record);
    }

    // обработка нажатия на контекстное меню
    // handling clicking on the context menu
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == DELETE_ID) {

            // получаем из пункта контекстного меню данные по пункту списка
            // get data on the list item from the context menu item
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo)
                    item.getMenuInfo();

            // извлекаем id записи и удаляем соответствующую запись в БД
            // retrieve the id of the record and delete the corresponding record in the database
            deleteForId(acmi.id);
        }
        return true;
    }

    private void deleteForId(long id) {
        //Logs
        Log.d("myLogs", "delete ID= " + id);

        MainActivity.mContentResolver.delete(DBContract.FeedEntry.CONTENT_URI,
                DBContract.FeedEntry._ID, new String[] {String.valueOf(id)});

        // Сообщаем пользователю, какой ИД был удален
        // Telling the user which ID was deleted
        Toast.makeText(this,
                "Deleted _ID= "
                        + id,
                Toast.LENGTH_SHORT).show();
    }


    /**
     * Display the designated columns in the cursor as a List in
     * the ListView
     */
    private void displayCurrent() {

        // Query for all characters
        Cursor mCursor = MainActivity.mContentResolver.query(DBContract.FeedEntry.CONTENT_URI,
                DBContract.FeedEntry.sColumnsToDisplay,
                null, null, null);

// Inform the user if there's nothing to display.
        if (mCursor.getCount() == 0) {
            Toast.makeText(this,
                    "No items to display",
                    Toast.LENGTH_SHORT).show();

            // Logs
//            Log.d("myLogs", "mCursor = null");

        } else {
            // Display the results of the query.

            // создааем адаптер и настраиваем список
            // create an adapter and set up a list
            SimpleCursorAdapter scAdapter = new SimpleCursorAdapter
                    (this, R.layout.item_list, mCursor, from, to);
            lvData.setAdapter(scAdapter);

        }

    }

    /**
     * handling button clicks "Refresh", "Add" and "delete"
     */
    View.OnClickListener viewClickListener = v -> {
        switch (v.getId()) {
            case R.id.butRefresh:

                displayCurrent();
                break;

            case R.id.butAdd:

                addCurrentPosition();
                break;

            case R.id.butDelete:

                deleteAll();
                break;
        }

    };

    /**
     * Добавить текущую позицию с указанием типа опасности, долготы, широты и описанием
     * Add current position with danger type, longitude, latitude and description
     */
    private void addCurrentPosition() {

        // Call
        Intent intent = new Intent(this, Position.class);
        startActivityForResult(intent, REQUEST_POSITION);
    }

    /**
     * getting data from activity Position with current geodata
     * получение данных из активности Position с текущими геоданными
     * @param requestCode REQUEST_POSITION
     * @param resultCode RESULT_OK
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        // Check if the started Activity completed successfully and
        // the request code is what we're expecting.
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK
                && requestCode == REQUEST_POSITION) {

            // We write the current position to the base SQLite
            // Записываем текущее положение в базу SQLite

            insertCurrentGeo(data);
//            displayCurrent();

        } else {
            Log.d("myLogs", "RESULT NOT OK ???");
        }

    }

    private void insertCurrentGeo(Intent intent) {

        ContentValues cvs = new ContentValues();

        // get the data and insert it into ContentValues
        // получаем данные и вставляем их в ContentValues

        cvs.put(DBContract.FeedEntry.COLUMN_LATITUDE,
                intent.getDoubleExtra(LATITUDE, 0));

        cvs.put(DBContract.FeedEntry.COLUMN_LONGITUDE,
                intent.getDoubleExtra(LONGITUDE, 0));

        cvs.put(DBContract.FeedEntry.COLUMN_DESCRIPTION, intent.
                getStringExtra(DESCRIPTION));

        // insert the icon. вставляем иконку
        switch (intent.getIntExtra(POSITION_DANGER, 0)) {
            case 0:
                // "Radiation"
                cvs.put(DBContract.FeedEntry.COLUMN_DANGER, R.mipmap.ic_launcher_round_round);
                break;

            case 1:
                // "Biodefense"
                cvs.put(DBContract.FeedEntry.COLUMN_DANGER, R.mipmap.ic_launcher_bio_round);
                break;

            case 2:
                // "Chemical danger"
                cvs.put(DBContract.FeedEntry.COLUMN_DANGER, R.mipmap.ic_launcher_chem_round);
                break;

            case 3:
                // "Laser danger"
                cvs.put(DBContract.FeedEntry.COLUMN_DANGER, R.mipmap.ic_launcher_laser_round);
                break;

            case 4:
                // "Electromagnetic"
                cvs.put(DBContract.FeedEntry.COLUMN_DANGER, R.mipmap.ic_launcher_magnetics_round);
                break;

            case 5:
                // "Radio wave"
                cvs.put(DBContract.FeedEntry.COLUMN_DANGER, R.mipmap.ic_launcher_radio_round);
                break;
        }

        // Inserting
        MainActivity.mContentResolver.insert(DBContract.FeedEntry.CONTENT_URI, cvs);

    }

    /**
     * This method is run when the user clicks the "Delete All" button
     */
    private void deleteAll() {

        int numDeleted = MainActivity.mContentResolver.delete(DBContract.FeedEntry.CONTENT_URI,
                null, null);

        // Сообщаем пользователю, сколько символов было удалено.
        // Inform the user how many characters were deleted.
        Toast.makeText(this,
                "Deleted "
                        + numDeleted
                        + " position",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * loading danger of default
     */
    public void loadingDefault() {

        // loading danger of default
        // we fill the database "Chernobyl"
        ContentValues cvs = new ContentValues();
        cvs.put(DBContract.FeedEntry.COLUMN_DANGER, R.mipmap.ic_launcher_round_round);
        cvs.put(DBContract.FeedEntry.COLUMN_LATITUDE, 51.4045032);
        cvs.put(DBContract.FeedEntry.COLUMN_LONGITUDE, 30.0542331);
        cvs.put(DBContract.FeedEntry.COLUMN_DESCRIPTION, "Chernobyl");
        MainActivity.mContentResolver.insert(DBContract.FeedEntry.CONTENT_URI, cvs);


        // we fill the database "Fukushima"
//        ContentValues cvs1 = new ContentValues();
        cvs.put(DBContract.FeedEntry.COLUMN_DANGER, R.mipmap.ic_launcher_round_round);
        cvs.put(DBContract.FeedEntry.COLUMN_LATITUDE, 37.760799);
        cvs.put(DBContract.FeedEntry.COLUMN_LONGITUDE, 140.474785);
        cvs.put(DBContract.FeedEntry.COLUMN_DESCRIPTION, "Fukushima");
        MainActivity.mContentResolver.insert(DBContract.FeedEntry.CONTENT_URI, cvs);
    }



}