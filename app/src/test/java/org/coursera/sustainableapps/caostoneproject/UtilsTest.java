package org.coursera.sustainableapps.caostoneproject;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;

public class UtilsTest {

   /* private Utils utils;

    @Before
    public  void setUp() {
        utils = new Utils();
    }*/

    @Test
    public void aValidImageForItemSpinner() {

        assertEquals(R.mipmap.ic_launcher_round_foreground,
                Utils.imageDamageForItemSpinner(0));

        assertEquals(R.mipmap.ic_launcher_bio_foreground,
                Utils.imageDamageForItemSpinner(1));

        assertEquals(R.mipmap.ic_launcher_chem_foreground,
                Utils.imageDamageForItemSpinner(2));

        assertEquals(R.mipmap.ic_launcher_laser_foreground,
                Utils.imageDamageForItemSpinner(3));

        assertEquals(R.mipmap.ic_launcher_magnetics_foreground,
                Utils.imageDamageForItemSpinner(4));

        assertEquals(R.mipmap.ic_launcher_radio_foreground,
                Utils.imageDamageForItemSpinner(5));

        assertEquals(R.mipmap.ic_launcher_round_foreground,
                Utils.imageDamageForItemSpinner(anyInt()));

    }

    @Test
    public void aValidPositionSpinnerForIdImage() {

        assertEquals(0,Utils.imageDamageForItemSpinnerUpdate(
                R.mipmap.ic_launcher_round_foreground));

        assertEquals(1,Utils.imageDamageForItemSpinnerUpdate(
                R.mipmap.ic_launcher_bio_foreground));

        assertEquals(2,Utils.imageDamageForItemSpinnerUpdate(
                R.mipmap.ic_launcher_chem_foreground));

        assertEquals(3,Utils.imageDamageForItemSpinnerUpdate(
                R.mipmap.ic_launcher_laser_foreground));

        assertEquals(4,Utils.imageDamageForItemSpinnerUpdate(
                R.mipmap.ic_launcher_magnetics_foreground));

        assertEquals(5,Utils.imageDamageForItemSpinnerUpdate(
                R.mipmap.ic_launcher_radio_foreground));

        assertEquals(0,Utils.imageDamageForItemSpinnerUpdate(anyInt()));
    }

}
