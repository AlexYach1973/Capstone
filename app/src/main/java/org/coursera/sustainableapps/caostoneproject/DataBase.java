package org.coursera.sustainableapps.caostoneproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DataBase extends AppCompatActivity {

    /**
     * fields for determining the current location.
     * поля для определение текущей локации.
     */
    public static String LATITUDE = "latitude";
    public static String LONGITUDE = "longitude";
    public static String DESCRIPTION = "description";
    public static String POSITION_DANGER = "positionDanger";

    // Request code for Position
    private static final int REQUEST_INSERT_POSITION = 1;
    private static final int REQUEST_UPDATE_POSITION = 2;

    /**
     * ListView to display the database
     *для отображения базы данных
     */
    ListView lvData;

    // Context menu
    private static final int DELETE_ID = 1;
    private static final int UPDATE_ID = 2;
    private long currentId ;

    // формируем столбцы сопоставления
    // form matching columns
    private final String[] from = new String[]{DBContract.FeedEntry.COLUMN_DANGER,
            DBContract.FeedEntry.COLUMN_DESCRIPTION};

    private final int[] to = new int[]{R.id.imageViewList, R.id.textList};

    /**
     * Buttons "Refresh", "Add" and "delete"
     */
    Button mButtonRefresh, mButtonAdd, mButtonDelete;

    //    Field ContentResolver
    private static ContentResolver mContentResolver;

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
        lvData = findViewById(R.id.listDanger);

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

    }

    /**
     * Hook method called after onStart(), just before the activity
     * (re)gains focus
     */
    protected void onResume() {

        super.onResume();

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
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.context_menu_data_base, menu);
        menu.add(0, UPDATE_ID, 0,R.string.change_record);
        menu.add(0, DELETE_ID, 0, R.string.delete_record);
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

                // Refresh
                displayCurrent();

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
                currentId =  acmiId.id;
                Log.d("myLogs", "acmiId.id = " + currentId);

                // read information about the current position
                // читаем информацию про текущую позицию
               Cursor mCursorUpdate = mContentResolver.
                       query(DBContract.FeedEntry.buildUri(currentId),
                        null,null,null,null);

               // без этой херни почему-то неработает, хотя курсор содержит одну строку
               mCursorUpdate.moveToFirst();

                // adding information about the current position
                // добавление информации про текущую позицию
                intent.putExtra(DataBase.POSITION_DANGER, mCursorUpdate.getInt(mCursorUpdate.
                        getColumnIndex(DBContract.FeedEntry.COLUMN_DANGER)));

                intent.putExtra(DataBase.LATITUDE, mCursorUpdate.getDouble(mCursorUpdate.
                        getColumnIndex(DBContract.FeedEntry.COLUMN_LATITUDE)));

                intent.putExtra(DataBase.LONGITUDE, mCursorUpdate.getDouble(mCursorUpdate.
                        getColumnIndex(DBContract.FeedEntry.COLUMN_LONGITUDE)));

                intent.putExtra(DataBase.DESCRIPTION, mCursorUpdate.getString(mCursorUpdate.
                        getColumnIndex(DBContract.FeedEntry.COLUMN_DESCRIPTION)));

                // Start Activity Position
                startActivityForResult(intent, REQUEST_UPDATE_POSITION);

                // close Cursor
                mCursorUpdate.close();

                break;
            default:
                Toast.makeText(this,
                        "Delete, update 0 position", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }

    /**
     * Удаляем запись по ID
     * Delete the entry by ID
     */
    private void deleteForId(long id) {

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

        }
            // Display the results of the query.

            // создааем адаптер и настраиваем список
            // create an adapter and set up a list
            SimpleCursorAdapter scAdapter = new SimpleCursorAdapter
                    (this, R.layout.item_list_data_base, mCursor, from, to,0);
            lvData.setAdapter(scAdapter);

        // close Cursor
//        mCursor.close();

    }

    /**
     * handling button clicks "Refresh", "Add" and "delete"
     */
    @SuppressLint("NonConstantResourceId")
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
        intent.putExtra(DataBase.DESCRIPTION, "");
        intent.putExtra(DataBase.LATITUDE, 0);
        intent.putExtra(DataBase.LONGITUDE, 0);
        intent.putExtra(DataBase.POSITION_DANGER, 0);
        // Start Activity Position
        startActivityForResult(intent, REQUEST_INSERT_POSITION);
    }

    /**
     * getting data from activity Position with current geodata
     * получение данных из активности Position с текущими геоданными
     * @param requestCode REQUEST_POSITION
     * @param resultCode RESULT_OK
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
                && requestCode == REQUEST_INSERT_POSITION) {

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

        // insert the icon using the Utils class method helper method. вставляем иконку
        cvs.put(DBContract.FeedEntry.COLUMN_DANGER,
                Utils.imageDamageForItemSpinner
                        (intent.getIntExtra(POSITION_DANGER, 0)));

        // Inserting
        mContentResolver.insert(DBContract.FeedEntry.CONTENT_URI, cvs);

    }

    /**
     * обновляем текущую позицию (только иконку и описание)
     * update the current position (only icon and description)
     */
    private void updateCurrentPosition(Intent intent) {

        ContentValues cvs = new ContentValues();

        // get the data and insert it into ContentValues
        // получаем данные и вставляем их в ContentValues

        // insert the icon using the Utils class method helper method. вставляем иконку
        cvs.put(DBContract.FeedEntry.COLUMN_DANGER,
                Utils.imageDamageForItemSpinner
                        (intent.getIntExtra(POSITION_DANGER, 0)));

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

        displayCurrent();
    }

}