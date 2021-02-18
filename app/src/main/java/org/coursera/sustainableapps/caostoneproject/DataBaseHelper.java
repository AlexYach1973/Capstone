 package org.coursera.sustainableapps.caostoneproject;

 import android.content.ContentValues;
 import android.content.Context;
 import android.database.sqlite.SQLiteDatabase;
 import android.database.sqlite.SQLiteOpenHelper;

 import java.io.File;

/**
 * The database helper used by the DangerProvider to create
 * and manage its underlying SQLite database.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    /**
     * Database name.
     */
    private static final String DATABASE_NAME =
            "org_coursera_dangerprovider";

    /**
     * Database version number, which is updated with each schema
     * change.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * SQL create table statements.
     * SQL statement used to create the database table.
     */

    final String SQL_CREATE_TABLE =
            "create table "
            + DBContract.FeedEntry.TABLE_NAME + " ("
            + DBContract.FeedEntry._ID + " integer primary key autoincrement, "
            + DBContract.FeedEntry.COLUMN_DANGER + " INTEGER , "
            + DBContract.FeedEntry.COLUMN_LATITUDE + " INTEGER , "
            + DBContract.FeedEntry.COLUMN_LONGITUDE + " INTEGER , "
            + DBContract.FeedEntry.COLUMN_DESCRIPTION + " TEXT "
            + " );";

    /**
     * Constructor - initialize database name and version, but don't
     * actually construct the database (which is done in the
     * onCreate() hook method). It places the database in the
     * application's cache directory, which will be automatically
     * cleaned up by Android if the device runs low on storage space.
     *
     * @param context Any context
     */
    public DataBaseHelper(Context context) {

        super(context,
                context.getCacheDir()
                        + File.separator
                        + DATABASE_NAME,
                null,
                DATABASE_VERSION);
    }

    /**
     * Hook method called when the database is created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table.
        db.execSQL(SQL_CREATE_TABLE);

        /*// loading danger of default
        // we fill the database "Chernobyl"
        ContentValues cvs = new ContentValues();
        cvs.put(DBContract.FeedEntry.COLUMN_DANGER, R.mipmap.ic_launcher_round_round);
        cvs.put(DBContract.FeedEntry.COLUMN_LATITUDE, 51.4045032);
        cvs.put(DBContract.FeedEntry.COLUMN_LONGITUDE, 30.0542331);
        cvs.put(DBContract.FeedEntry.COLUMN_DESCRIPTION, "Chernobyl");
        db.insert(DBContract.FeedEntry.TABLE_NAME, null, cvs);

        // we fill the database "Fukushima"
//        ContentValues cvs1 = new ContentValues();
        cvs.put(DBContract.FeedEntry.COLUMN_DANGER, R.mipmap.ic_launcher_round_round);
        cvs.put(DBContract.FeedEntry.COLUMN_LATITUDE, 37.760799);
        cvs.put(DBContract.FeedEntry.COLUMN_LONGITUDE, 140.474785);
        cvs.put(DBContract.FeedEntry.COLUMN_DESCRIPTION, "Fukushima");
        db.insert(DBContract.FeedEntry.TABLE_NAME, null, cvs);*/
    }

    /**
     * Hook method called when the database is upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Delete the existing tables.
        db.execSQL("DROP TABLE IF EXISTS "
                + DBContract.FeedEntry.TABLE_NAME);
        // Create the new tables.
        onCreate(db);
    }
}