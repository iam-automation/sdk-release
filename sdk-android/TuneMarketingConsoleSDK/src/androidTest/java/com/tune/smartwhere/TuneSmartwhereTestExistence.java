package com.tune.smartwhere;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class TuneSmartwhereTestExistence extends TuneSmartWhereTests {
    private static final String TUNE_SMARTWHERE_COM_PROXIMITY_LIBRARY_PROXIMITYCONTROL = "com.proximity.library.ProximityControl";

    @Test
    public void testExistence() throws Exception {
        // Note that we can't check existence via. the public API, because the unit test mocking layer
        // is pretending that the class does in fact exist.
        // In this case we are going to do the forName check ourselves.
        //
        Class clazz = null;
        try {
            clazz = Class.forName(TUNE_SMARTWHERE_COM_PROXIMITY_LIBRARY_PROXIMITYCONTROL);
        } catch (ClassNotFoundException e) {
            // This is expected.
        }

        assertNull(clazz);
    }
}
