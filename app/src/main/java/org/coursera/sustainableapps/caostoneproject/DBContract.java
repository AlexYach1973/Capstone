package org.coursera.sustainableapps.caostoneproject;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Contract containing labels that are used to access the table and
 * its entries of the location storage database.
 */
public final class DBContract {
    public DBContract(){}

    /**
     * This ContentProvider's unique identifier.
     */
    public static final String CONTENT_AUTHORITY = "org.coursera.dangerprovider";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which
     * apps will use to contact the content provider.
     */
    public static final Uri BASE_CONTENT_URI =
            Uri.parse("content://"
                    + CONTENT_AUTHORITY);

    public static final String PATH_CHARACTER =
            FeedEntry.TABLE_NAME;



    // Define table contents
    public static abstract class FeedEntry implements BaseColumns {

        /**
         * Use BASE_CONTENT_URI to create the unique URI for Character
         * Table that apps will use to contact the content provider.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon()
                        .appendPath(PATH_CHARACTER).build();

       /* public static final Uri CONTENT_URI =
                Uri.parse(BASE_CONTENT_URI + "/" + PATH_CHARACTER);*/

        /**
         * When the Cursor returned for a given URI by the
         * ContentProvider contains 0..x items.
         */
        public static final String CONTENT_ITEMS_TYPE =
                "vnd.android.cursor.dir/"
                        + CONTENT_AUTHORITY
                        + "/"
                        + PATH_CHARACTER;

        /**
         * When the Cursor returned for a given URI by the
         * ContentProvider contains 1 item.
         */
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/"
                        + CONTENT_AUTHORITY
                        + "/"
                        + PATH_CHARACTER;

        /**
         * Columns to display.
         */
        public static final String[] sColumnsToDisplay =
                new String[] {
                        FeedEntry._ID,
                        FeedEntry.COLUMN_DANGER,
                        FeedEntry.COLUMN_LATITUDE,
                        FeedEntry.COLUMN_LONGITUDE,
                        FeedEntry.COLUMN_DESCRIPTION
                };

        /**
         * Table where location entries are stored
         */
        public static final String TABLE_NAME = "data_table";

        /**
         * Species danger
         */
        public static final String COLUMN_DANGER = "danger";

        /**
         * Latitude that was logged
         */
        public static final String COLUMN_LATITUDE = "lat";

        /**
         * Longitude that was logged
         */
        public static final String COLUMN_LONGITUDE = "long";

        /**
         * Description of the Location
         */
        public static final String COLUMN_DESCRIPTION = "description";

        /**
         * Return a Uri that points to the row containing a given id.
         * возвращает Uri, который указывает на строку, содержащую данный идентификатор
         * @param id row id
         * @return Uri URI for the specified row id
         */
        public static Uri buildUri(Long id) {
            return ContentUris.withAppendedId(CONTENT_URI,
                    id);
        }

    }
}