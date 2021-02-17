package org.coursera.sustainableapps.caostoneproject;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DataBase extends AppCompatActivity {

    /**
     * ListView
     */
    ListView listDanger;
    ImageView imageView;
    ListView lvData;

    DangerProvider mProvider = new DangerProvider();

    /**
     * Define the Proxy for accessing the HobbitProvider.
     */
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

        // initialize
        imageView = findViewById(R.id.imageViewList);
        listDanger = findViewById(R.id.listDanger);
        lvData = (ListView) findViewById(R.id.listDanger);

        displayCurrent();

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
            Log.d("myLogs", "mCursor = null");

            // Remove the display if there's nothing left to show.
//            mHobbitActivity.displayCursor(mCursor = null);

        } else {
            // Display the results of the query.

//            Log.d("myLogs", mCursor.toString());

            // формируем столбцы сопоставления
            String[] from = new String[] {DBContract.FeedEntry.COLUMN_DANGER,
            DBContract.FeedEntry.COLUMN_DESCRIPTION};

            int[] to = new int[] {R.id.imageViewList, R.id.textList};

            // создааем адаптер и настраиваем список
            SimpleCursorAdapter scAdapter = new SimpleCursorAdapter
                    (this,R.layout.item_list, mCursor,from,to);
            lvData.setAdapter(scAdapter);

        }

    }

}