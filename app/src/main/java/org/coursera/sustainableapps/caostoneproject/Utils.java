package org.coursera.sustainableapps.caostoneproject;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.widget.ImageButton;

import java.util.ArrayList;

public class Utils {

    // Этот метод принимает позицию спинера и возвращает ИД картики oпаности
    // This method takes the position of the spinner and returns the ID of the picture panel.
    public static int imageDamageForItemSpinner(int position) {

        int imageDamage = 0;

        // Choice image
        switch (position) {
            case 0:
                // "Radiation"
                imageDamage = R.mipmap.ic_launcher_round_foreground;
                break;

            case 1:
                // "Biodefense"
                imageDamage = R.mipmap.ic_launcher_bio_foreground;
                break;

            case 2:
                // "Chemical danger"
                imageDamage = R.mipmap.ic_launcher_chem_foreground;
                break;

            case 3:
                // "Laser danger"
                imageDamage = R.mipmap.ic_launcher_laser_foreground;
                break;

            case 4:
                // "Electromagnetic"
                imageDamage = R.mipmap.ic_launcher_magnetics_foreground;
                break;

            case 5:
                // "Radio wave"
                imageDamage = R.mipmap.ic_launcher_radio_foreground;
                break;
        }
        return imageDamage;
    }

    // Этот метод принимает ИД картинки опасности и возвращает позицию спиннера
    // This method takes the ID of the hazard picture and returns the position of the spinner.
    @SuppressLint("NonConstantResourceId")
    public static int imageDamageForItemSpinnerUpdate(int idImageDamage) {

        int positionSpinner = 0;

        // Choice image
        switch (idImageDamage) {
            case R.mipmap.ic_launcher_round_foreground:
                // "Radiation"
                positionSpinner = 0;
                break;

            case R.mipmap.ic_launcher_bio_foreground:
                // "Biodefense"
                positionSpinner = 1;
                break;

            case R.mipmap.ic_launcher_chem_foreground:
                // "Chemical danger"
                positionSpinner = 2 ;
                break;

            case R.mipmap.ic_launcher_laser_foreground:
                // "Laser danger"
                positionSpinner = 3;
                break;

            case R.mipmap.ic_launcher_magnetics_foreground:
                // "Electromagnetic"
                positionSpinner = 4;
                break;

            case R.mipmap.ic_launcher_radio_foreground:
                // "Radio wave"
                positionSpinner = 5;
                break;
        }
        return positionSpinner;
    }

    // ImageButtonEye animation when you click on the FloatingButton
    public static void showImage(ImageButton imageId) {
        imageId.animate().rotation(imageId.getRotation() + 1800).setDuration(2000);
//        imageId.animate().scaleX(0).scaleY(0).setDuration(2000);
//        imageId.animate().scaleX(1).scaleY(1).setDuration(2000);

    }

    public static ArrayList<RecyclerObserveItem> fillArrayListFromCursor(Cursor mCursor) {

        ArrayList<RecyclerObserveItem> recycleArrayList = new ArrayList<>();

        // First line
        mCursor.moveToFirst();
        // to the end of the table
        while (!mCursor.isAfterLast()) {

            // the same as in extractDataFromCursor()
            // Icon Danger
            int presentDamage = mCursor.getInt(mCursor.getColumnIndex(
                    DBContract.FeedEntry.COLUMN_DANGER));

            // Use String.valueOf()
            int presentId = mCursor.getInt(mCursor.getColumnIndex(
                    DBContract.FeedEntry._ID));

            // Double Lat and Long
            double presentLat = mCursor.getDouble(mCursor.getColumnIndex(
                    DBContract.FeedEntry.COLUMN_LATITUDE));
            double presentLng = mCursor.getDouble(mCursor.getColumnIndex(
                    DBContract.FeedEntry.COLUMN_LONGITUDE));

            // Description
            String presentDescription = mCursor.getString(mCursor.getColumnIndex(
                    DBContract.FeedEntry.COLUMN_DESCRIPTION));

            recycleArrayList.add(new RecyclerObserveItem(presentDamage,
                    presentId,
                    presentLat,
                    presentLng,
                    presentDescription,
                    ""));

            //move to next line
            mCursor.moveToNext();

        }
        return recycleArrayList;
    }

}
