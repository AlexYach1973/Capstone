package org.coursera.sustainableapps.caostoneproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static org.coursera.sustainableapps.caostoneproject.DBContract.*;

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

    /** Static Button to call from a static method deleteForId() */
    @SuppressLint("StaticFieldLeak")
    static Button mButtonRefresh;

    /**
     * RecyclerView;
     */
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    // ArrayList
    private ArrayList<RecyclerObserveItem> recyclerObserveItems;

    //    Field ContentResolver
    private static ContentResolver mContentResolver;

    /**
     * Constructor initializes the fields.
     */
    public DataBase() {
    }

    // Context
    Context mContext;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base);

        mContext = getApplication();

        mContentResolver = getContentResolver();

        /** initialize RecyclerView */
        recyclerView = findViewById(R.id.recyclerViewDataBase);

        // initialize Buttons
        mButtonRefresh = findViewById(R.id.butRefresh);
        Button mButtonAdd = findViewById(R.id.butAdd);
        Button mButtonDelete = findViewById(R.id.butDelete);

        // assign a listener
        mButtonRefresh.setOnClickListener(viewClickListener);
        mButtonAdd.setOnClickListener(viewClickListener);
        mButtonDelete.setOnClickListener(viewClickListener);

    }

    /**
     * Hook method called after onStart(), just before the activity
     * (re)gains focus
     */
    protected void onResume() {

        super.onResume();

//        mButtonRefresh.setVisibility(View.GONE);
        mButtonRefresh.setEnabled(false);

        displayCurrent();
    }

    /**
     * Удаляем запись по ID
     * Delete the entry by ID
     * we make the method static, otherwise we will not take it out of the context menu,
     * which is written in the RecyclerViewAdapter
     */
    public static void deleteForId(long id,  Context context) {

        // // use withAppendedId method from ContentUris class to add "id" to end of uri string
        mContentResolver.delete(ContentUris.withAppendedId(FeedEntry.CONTENT_URI, id),
                FeedEntry._ID, new String[] {String.valueOf(id)});

        // Сообщаем пользователю, какой ИД был удален
        // Telling the user which ID was deleted
        Toast.makeText(context,
                "Deleted _ID= " + id + "\n" + "click REFRESH",
                Toast.LENGTH_LONG).show();

//        mButtonRefresh.setVisibility(View.VISIBLE);
        mButtonRefresh.setEnabled(true);

    }

    /**
     * Display the designated columns in the cursor as a List in
     * the ListView
     */
    private void displayCurrent() {

        // Query for all characters
        Cursor mCursor = mContentResolver.query(FeedEntry.CONTENT_URI,
                FeedEntry.sColumnsToDisplay,
                null, null, null);

// Inform the user if there's nothing to display.
        if (mCursor.getCount() == 0) {
            Toast.makeText(this,
                    "No items to display",
                    Toast.LENGTH_SHORT).show();

        }

        /**  initialize ArrayList */
        recyclerObserveItems = new ArrayList<>();

        // using a helper method from the UTIL.class
        ArrayList<RecyclerObserveItem> recycleItems = Utils.fillArrayListFromCursor(mCursor);
        mCursor.close();

        // recycleItems without distance
        for (RecyclerObserveItem data : recycleItems) {
            // fill recyclerObserveItems
            recyclerObserveItems.add(new RecyclerObserveItem(data.getImage(),
                    data.getIdCurrent(),
                    data.getLat(),
                    data.getLng(),
                    data.getDescription(),
                    ""));
        }

        // initialize RecyclerAdapter
//        recyclerView.setHasFixedSize(true); // если размер не меняется
        recyclerAdapter = new RecyclerViewAdapter(recyclerObserveItems,this, true);
        layoutManager = new LinearLayoutManager(this);

        // Display
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(layoutManager);

    }

    /**
     * handling button clicks "Refresh", "Add" and "delete"
     */
    @SuppressLint("NonConstantResourceId")
    View.OnClickListener viewClickListener = v -> {
        switch (v.getId()) {
            case R.id.butRefresh:

                displayCurrent();
//                mButtonRefresh.setVisibility(View.GONE);
                mButtonRefresh.setEnabled(false);
                break;

            case R.id.butAdd:

                addCurrentPosition();
                break;

            case R.id.butDelete:
                // Call Dialog
                showDialog(1);


                break;
        }

    };

    // Dialog deprecated
    protected Dialog onCreateDialog(int id) {

        if (id == 1) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);

            // Title
            adb.setTitle(R.string.title_dialog);
            // Message
            adb.setMessage(R.string.message_dialog);
            // icon
            adb.setIcon(R.mipmap.crossbones);

            // Positive
            adb.setPositiveButton(R.string.positive_dialog,
                    myClickListenerDialog);

            // Negative
            adb.setNegativeButton(R.string.negative_dialog,
                    myClickListenerDialog);

            // Create Dialog
            return adb.create();
        }
        return super.onCreateDialog(id);
    }

    // dialog listener
    DialogInterface.OnClickListener myClickListenerDialog = (dialog, which) -> {

        switch (which) {

            case Dialog.BUTTON_POSITIVE:
                deleteAll();
                break;

            case Dialog.BUTTON_NEGATIVE:
//                    finish();
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

        }else {
            Log.d("myLogs", "RESULT NOT OK ???");
            Toast.makeText(this,
                    "Insert, update 0 position", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * inserting the current position into the database
     * вставка текущей позиции в базу данных
     * ContentResolver при неудаче возвращает "null"
     */
    private void insertCurrentGeo(Intent intent) {

        ContentValues cvs = new ContentValues();

        // get the data and insert it into ContentValues
        // получаем данные и вставляем их в ContentValues

        cvs.put(FeedEntry.COLUMN_LATITUDE,
                intent.getDoubleExtra(LATITUDE, 0));

        cvs.put(FeedEntry.COLUMN_LONGITUDE,
                intent.getDoubleExtra(LONGITUDE, 0));

        cvs.put(FeedEntry.COLUMN_DESCRIPTION, intent.
                getStringExtra(DESCRIPTION));

        // insert the icon using the Utils class method helper method. вставляем иконку
        cvs.put(FeedEntry.COLUMN_DANGER,
                Utils.imageDamageForItemSpinner
                        (intent.getIntExtra(POSITION_DANGER, 0)));

        // Inserting
        mContentResolver.insert(FeedEntry.CONTENT_URI, cvs);

    }

    /**
     * обновляем текущую позицию (только иконку и описание)
     * update the current position (only icon and description)
     */
    public static void updateCurrentPosition(Intent intent, Context mContext) {

        ContentValues cvs = new ContentValues();

        // get the data and insert it into ContentValues
        // получаем данные и вставляем их в ContentValues

        // insert the icon using the Utils class method helper method. вставляем иконку
        cvs.put(FeedEntry.COLUMN_DANGER,
                Utils.imageDamageForItemSpinner
                        (intent.getIntExtra(POSITION_DANGER, 0)));

        // Descriptions
        cvs.put(FeedEntry.COLUMN_DESCRIPTION, intent.
                getStringExtra(DESCRIPTION));

//        Log.d("myLogs","ID= " + currentId);

        // обновляем выбранную строку
        // update the selected row
        int currentId = intent.getExtras().getInt("id");

        // use withAppendedId method from ContentUris class to add "id" to end of uri string
        mContentResolver.update(ContentUris.withAppendedId(FeedEntry.CONTENT_URI, currentId),
                cvs,
                FeedEntry._ID,
                new String[]{String.valueOf(currentId)});

        // Сообщаем об обновлении
        // We inform about the update

        Log.d("myLogs", "DATABASE: update currentId: " + currentId);

/**      for Toast it is necessary context. We passed it from Position.class */
                Toast.makeText(mContext,
                "Update _ID= "
                        + currentId,
                Toast.LENGTH_SHORT).show();

    }
    /**
     * This method is run when the user clicks the "Delete All" button
     */
    private void deleteAll() {

        int numDeleted = mContentResolver.delete(FeedEntry.CONTENT_URI,
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