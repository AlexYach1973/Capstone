package org.coursera.sustainableapps.caostoneproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
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
    private static final int REQUEST_UPDATE_POSITION = 2;

    /**
     * ListView to display the database
     *для отображения базы данных
     */
    ListView listDanger;
    ImageView imageView;
    ListView lvData;

    // Context menu
    private static final int DELETE_ID = 1;
    private static final int UPDATE_ID = 2;
    private long currentId ;

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

    //    Field ContentResolver
    static ContentResolver mContentResolver;

    /**
     * Constructor initializes the fields.
     */
    public DataBase() {
    }

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base);

        mContentResolver = getContentResolver();

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
        menu.add(0, UPDATE_ID, 0,"change ?");
    }

    // обработка нажатия на контекстное меню
    // handling clicking on the context menu
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case DELETE_ID:
                // получаем из пункта контекстного меню данные по пункту списка
                // get data on the list item from the context menu item
                AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo)
                        item.getMenuInfo();

                // извлекаем id записи и удаляем соответствующую запись в БД
                // retrieve the id of the record and delete the corresponding record in the database
                deleteForId(acmi.id);

                break;

            case UPDATE_ID:
                // вызываем Activity Position для редактирования выбранного пункта из списка listView
                // we call the activity Position to edit the selected item from the listView
                Intent intent = new Intent(this, Position.class);

                // получаем из пункта контекстного меню данные по пункту списка
                // get data on the list item from the context menu item
                AdapterView.AdapterContextMenuInfo acmiId = (AdapterView.AdapterContextMenuInfo)
                        item.getMenuInfo();

                // Сохраняем ID в переменной для использования в updateCurrentPosition
                // Store the ID in a variable for use in updateCurrentPosition
                currentId = acmiId.id;

                // Start
                startActivityForResult(intent, REQUEST_UPDATE_POSITION);

                break;
            default:
                Toast.makeText(this,
                        "Delete, update 0 position", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }

    private void deleteForId(long id) {
        //Logs
//        Log.d("myLogs", "delete ID= " + id);

        mContentResolver.delete(DBContract.FeedEntry.CONTENT_URI,
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
        Cursor mCursor = mContentResolver.query(DBContract.FeedEntry.CONTENT_URI,
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

        // получаем данные для текущей позиции
        // get data for the current position

        if (resultCode == Activity.RESULT_OK
                && requestCode == REQUEST_POSITION) {

            // We write the current position to the base SQLite
            // Записываем текущее положение в базу SQLite

            insertCurrentGeo(data);
//            displayCurrent();

            Toast.makeText(this,
                    "Insert 1 position", Toast.LENGTH_SHORT).show();


            // получаем данные для обновления позиции
            // get data to update the position
        }else if (resultCode == Activity.RESULT_OK
                && requestCode == REQUEST_UPDATE_POSITION) {

            // Обновляем текущую позицию
            //Updating the current position
            updateCurrentPosition(data);

        } else {
            Log.d("myLogs", "RESULT NOT OK ???");
            Toast.makeText(this,
                    "Insert, update 0 position", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * inserting the current position into the database
     * вставка текущей позиции в базу данных
     * @param intent
     */
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
        cvs.put(DBContract.FeedEntry.COLUMN_DANGER,
                insertIcon(intent.getIntExtra(POSITION_DANGER, 0)));

        // Inserting
        mContentResolver.insert(DBContract.FeedEntry.CONTENT_URI, cvs);

    }

    // Устанавливаем иконку выбранную в спиннере в Position.java
    // Set the icon selected in the spinner to Position.java
    private Integer insertIcon(int intSnipePosition) {

        int insIcon = 1;

        switch (intSnipePosition) {
            case 0:
                // "Radiation"
                insIcon = R.mipmap.ic_launcher_round_round;
                break;

            case 1:
                // "Biodefense"
                insIcon = R.mipmap.ic_launcher_bio_round;
                break;

            case 2:
                // "Chemical danger"
                insIcon = R.mipmap.ic_launcher_chem_round;
                break;

            case 3:
                // "Laser danger"
                insIcon = R.mipmap.ic_launcher_laser_round;
                break;

            case 4:
                // "Electromagnetic"
                insIcon = R.mipmap.ic_launcher_magnetics_round;
                break;

            case 5:
                // "Radio wave"
                insIcon = R.mipmap.ic_launcher_radio_round;
                break;
        }

        return insIcon;
    }


    /**
     * обновляем текущую позицию
     * update the current position
     */
    private void updateCurrentPosition(Intent intent) {

        ContentValues cvs = new ContentValues();

        // get the data and insert it into ContentValues
        // получаем данные и вставляем их в ContentValues

        // Icon
        cvs.put(DBContract.FeedEntry.COLUMN_DANGER,
                insertIcon(intent.getIntExtra(POSITION_DANGER, 0)));

        // Descriptions
        cvs.put(DBContract.FeedEntry.COLUMN_DESCRIPTION, intent.
                getStringExtra(DESCRIPTION));

//        Log.d("myLogs","ID= " + currentId);

        // обновляем выбранную строку
        // update the selected row
        mContentResolver.update(DBContract.FeedEntry.CONTENT_URI,
                cvs,
                DBContract.FeedEntry._ID,
                new String[]{String.valueOf(currentId)});

        // Сообщаем об обновлении
        // We inform about the update
        Toast.makeText(this,
                "Update _ID= "
                        + currentId,
                Toast.LENGTH_SHORT).show();


    }
    /**
     * This method is run when the user clicks the "Delete All" button
     */
    private void deleteAll() {

        int numDeleted = mContentResolver.delete(DBContract.FeedEntry.CONTENT_URI,
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
        cvs.put(DBContract.FeedEntry.COLUMN_LATITUDE, 50.4708);
        cvs.put(DBContract.FeedEntry.COLUMN_LONGITUDE, 30.305075);
        cvs.put(DBContract.FeedEntry.COLUMN_DESCRIPTION, "My House");
        mContentResolver.insert(DBContract.FeedEntry.CONTENT_URI, cvs);


        // we fill the database "Fukushima"
//        ContentValues cvs1 = new ContentValues();
        cvs.put(DBContract.FeedEntry.COLUMN_DANGER, R.mipmap.ic_launcher_round_round);
        cvs.put(DBContract.FeedEntry.COLUMN_LATITUDE, 37.760799);
        cvs.put(DBContract.FeedEntry.COLUMN_LONGITUDE, 140.474785);
        cvs.put(DBContract.FeedEntry.COLUMN_DESCRIPTION, "Fukushima");
        mContentResolver.insert(DBContract.FeedEntry.CONTENT_URI, cvs);
    }



}