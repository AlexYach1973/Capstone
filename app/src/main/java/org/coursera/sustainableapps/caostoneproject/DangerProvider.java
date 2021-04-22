package org.coursera.sustainableapps.caostoneproject;

import android.content.ContentProvider;
import android.content.ContentUris; // специальный класс для работы с uri
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import static org.coursera.sustainableapps.caostoneproject.DBContract.*;

public class DangerProvider extends ContentProvider {

    // For testing.
//    public final static String AUTHORITY = "edu.vanderbilt.mooc.atom_reader.provider";

    /**
     * Use DatabaseHelper to manage database creation and version
     * management.
     */
    private DataBaseHelper mDataBaseHelper;

    /**
     * Context for the Content Provider.
     */
    private Context mContext;

    /**
     * The code that is returned when a URI for more than 1 items is
     * matched against the given components.  Must be positive.
     */
    public static final int CHARACTERS = 100;

    /**
     * The code that is returned when a URI for exactly 1 item is
     * matched against the given components.  Must be positive.
     */
    public static final int CHARACTER = 101;

    /**
     * The URI Matcher used by this content provider.
     */
    private static final UriMatcher sUriMatcher =
            buildUriMatcher();

    @Override
    public boolean onCreate() {
        mContext = getContext();
        // Create the DatabaseHelper.
        mDataBaseHelper = new DataBaseHelper(mContext);
        return true;
    }


    @Override
    public int delete(Uri uri,
                      String selection,
                      String[] selectionArgs) {

        int returnCount;

        // Logs
        Log.d("myLogs", "Удаление DangerProvider");
//        Log.d("myLogs", "Selection :" + selection);
//        Log.d("myLogs", "SelectionArgs :" + selectionArgs[0]);

        switch (sUriMatcher.match(uri)) {
            case CHARACTERS:
                returnCount = mDataBaseHelper.getWritableDatabase().delete
                        (FeedEntry.TABLE_NAME,
                                addSelectionArgs(selection, selectionArgs),
                                selectionArgs);

                Log.d("myLogs", "Delete CHARACTERS: " + CHARACTERS);
                break;

            case CHARACTER:
                // Expand the selection if necessary.
                selection = addSelectionArgs(selection, selectionArgs);

                returnCount =  mDataBaseHelper.getWritableDatabase().delete
                        (FeedEntry.TABLE_NAME,
                                addKeyIdCheckToWhereStatement(selection,
                                        ContentUris.parseId(uri)),
                                selectionArgs);

                Log.d("myLogs", "Delete CHARACTER: " + CHARACTER);
                break;

            default: throw new UnsupportedOperationException("???");
        }

        if (selection == null
                || returnCount > 0)
            // Notifies registered observers that row(s) were deleted.
            // Уведомляет зарегистрированных наблюдателей об удалении строки (строк).
            mContext.getContentResolver().notifyChange(uri,null);

        return returnCount;
    }

    /**
     * Return a selection string that concatenates all the
     * selectionArgs for a given @a selection using the given @a operation.
     * Возвращает строку выбора, которая объединяет все selectionArgs для данного выбора @a
     * с использованием данной операции
     */
    private String addSelectionArgs(String selection,
                                    String[] selectionArgs) {
        // Handle the "null" case.
        if (selection == null
                || selectionArgs == null)
            return null;
        else {
            StringBuilder selectionResult = new StringBuilder();

            // Properly add the selection args to the selectionResult.
            // Правильно добавьте аргументы выбора в selectionResult
            for (int i = 0;
                 i < selectionArgs.length - 1;
                 ++i)
                selectionResult.append(selection).append(" = ? ").append(" OR").append(" ");

            // Handle the final selection case.
            // Обработка случая окончательного выбора
            selectionResult.append(selection).append(" = ?");

            Log.d("myLogs", "DangerProvider addSelectionArgs: " + selectionResult);

            return selectionResult.toString();
        }
    }

    /**
     * Helper method that appends a given key id to the end of the
     * WHERE statement parameter.
     * Вспомогательный метод, который добавляет заданный идентификатор ключа
     * в конец параметра инструкции WHERE
     */
    private static String addKeyIdCheckToWhereStatement(String whereStatement,
                                                        long id) {
        String newWhereStatement;
        if (TextUtils.isEmpty(whereStatement))
            newWhereStatement = "";
        else
            newWhereStatement = whereStatement + " AND ";

        // Append the key id to the end of the WHERE statement.
        return newWhereStatement
                + FeedEntry._ID
                + " = '"
                + id
                + "'";
    }

    @Override
    public String getType(Uri uri) {

        // at the given URI.
        switch (sUriMatcher.match(uri)) {
            case CHARACTERS:
                return FeedEntry.CONTENT_ITEMS_TYPE;
            case CHARACTER:
                return FeedEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: "
                        + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues cvs) {

        Uri returnUri;

        // Logs
//        Log.d("myLogs", "insert :" + uri.toString());

        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If there's a match insert a new row.
        //
        if (sUriMatcher.match(uri) == CHARACTERS) {
            final SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
            long id = db.insert(FeedEntry.TABLE_NAME,
                    null, cvs);

            // метод withAppendedId добавляет в конец строки полученный "/id"
            returnUri = ContentUris
                    .withAppendedId(FeedEntry.CONTENT_URI, id);
        } else {
            throw new UnsupportedOperationException("Unknown uri: "
                    + uri);
        }
        // Notifies registered observers that a row was inserted.
        mContext.getContentResolver().notifyChange(uri,null);

        Log.d("myLogs", "insertUri witch id: " + returnUri);

        return returnUri;
    }


    /**
     * Method called to handle query requests from client
     * applications.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        // Match the id returned by UriMatcher to query appropriate rows.
        // Сопоставьте идентификатор, возвращаемый UriMatcher,
        // для запроса соответствующих строк.
        switch (sUriMatcher.match(uri)) {
            case CHARACTERS:

//                Log.d("myLogs", "Зaпустили Provider метод Query");

                cursor = mDataBaseHelper.getReadableDatabase().query
                        (FeedEntry.TABLE_NAME,
                                null,
                                addSelectionArgs(selection, selectionArgs),
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                // Logs
                Log.d("myLogs", "Query CHARACTERS: " + CHARACTERS);

                break;

            case CHARACTER:
                cursor = mDataBaseHelper.getReadableDatabase().query
                        (FeedEntry.TABLE_NAME,
                                projection,
                                addKeyIdCheckToWhereStatement(selection,
                                        ContentUris.parseId(uri)), // parseId(uri)- преобразует последний сегмент после слеша в long
                                selectionArgs,
                                null,
                                null,
                                sortOrder);

                Log.d("myLogs", "Query CHARACTER: " + CHARACTER);
                break;

            default:
                // Logs
                Log.d("myLogs", "CASE - No CHARACTERS, No CHARACTER");
                throw new UnsupportedOperationException("QUERY ???");
        }

        // Register to watch a content URI for changes.
        cursor.setNotificationUri(mContext.getContentResolver(), uri);

        return cursor;
    }

    /**
     * Method called to handle update requests from client applications
     * Метод, вызываемый для обработки запросов на обновление от клиентских приложений.
     */
    @Override
    public int update(Uri uri, ContentValues cvs, String selection,
                      String[] selectionArgs) {

        int returnCount;
        // Logs
//        Log.d("myLogs", "update: "  + uri);

        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If there's a match update rows.
        switch (sUriMatcher.match(uri)) {
            case CHARACTERS:

                returnCount = mDataBaseHelper.getWritableDatabase().update
                        (FeedEntry.TABLE_NAME,
                                cvs,
                                addSelectionArgs(selection, selectionArgs),
                                selectionArgs);
                Log.d("myLogs", "Update CHARACTERS: " + CHARACTERS);
                break;

            case CHARACTER:
                // Expand the selection if necessary.
                selection = addSelectionArgs(selection, selectionArgs);

                returnCount = mDataBaseHelper.getWritableDatabase().update
                        (FeedEntry.TABLE_NAME,
                                cvs,
                                addKeyIdCheckToWhereStatement(selection,
                                        ContentUris.parseId(uri)),
                                selectionArgs);
                Log.d("myLogs", "Update CHARACTER: " + CHARACTER);
                break;

            default:
                throw new UnsupportedOperationException("UPDATE ???");
        }

        if (returnCount > 0)
            // Notifies registered observers that row(s) were
            // updated.
            mContext.getContentResolver().notifyChange(uri,
                    null);

        return returnCount;
    }

    /**
     * Helper method that matches each URI to the integer
     * constants defined above.
     * Вспомогательный метод, который сопоставляет каждый URI
     * с целочисленными константами, определенными выше
     *
     * @return UriMatcher
     */
    protected static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code
        // to return when a match is found.  The code passed into the
        // constructor represents the code to return for the rootURI.
        // It's common to use NO_MATCH as the code for this case.
        // Все пути, добавленные в UriMatcher, имеют соответствующий код,
        // который будет возвращаться при обнаружении совпадения.
        // Код, переданный в конструктор, представляет собой код, возвращаемый для rootURI.
        // Обычно в качестве кода для этого случая используется NO_MATCH.
        final UriMatcher matcher =
                new UriMatcher(UriMatcher.NO_MATCH);

        // For each type of URI that is added, a corresponding code is
        // created.
        matcher.addURI(CONTENT_AUTHORITY,
                PATH_CHARACTER,
                CHARACTERS);
        matcher.addURI(CONTENT_AUTHORITY,
                PATH_CHARACTER
                        + "/#",
                CHARACTER);
        return matcher;
    }

}