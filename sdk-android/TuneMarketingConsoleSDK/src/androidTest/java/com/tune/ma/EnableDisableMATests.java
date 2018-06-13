package com.tune.ma;

import android.support.test.runner.AndroidJUnit4;

import com.tune.TuneConstants;
import com.tune.TuneTestWrapper;
import com.tune.TuneUnitTest;
import com.tune.ma.eventbus.TuneEventBus;
import com.tune.ma.eventbus.event.TuneAppForegrounded;
import com.tune.ma.playlist.model.TunePlaylist;
import com.tune.ma.powerhooks.model.TunePowerHookValue;
import com.tune.ma.utils.TuneSharedPrefsDelegate;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by charlesgilliam on 2/18/16.
 */
@RunWith(AndroidJUnit4.class)
public class EnableDisableMATests extends TuneUnitTest {

    @After
    public void tearDown() throws Exception {
        super.tearDown();

        new TuneSharedPrefsDelegate(getContext(), TuneConstants.PREFS_TUNE).clearSharedPreferences();
    }

    @Test
    public void testCorrectModulesAreInstantiated() throws Exception {
        initWith(Arrays.asList("configuration_disabled.json"));

        assertNotNull(TuneManager.getInstance());
        assertNotNull(TuneManager.getInstance().getAnalyticsManager());
        assertNotNull(TuneManager.getInstance().getApi());
        assertNotNull(TuneManager.getInstance().getConfigurationManager());
        assertNotNull(TuneManager.getInstance().getConnectedModeManager());
        assertNotNull(TuneManager.getInstance().getDeepActionManager());
        assertNotNull(TuneManager.getInstance().getExperimentManager());
        assertNotNull(TuneManager.getInstance().getFileManager());
        assertNotNull(TuneManager.getInstance().getPlaylistManager());
        assertNotNull(TuneManager.getInstance().getPowerHookManager());
        assertNotNull(TuneManager.getInstance().getProfileManager());
        assertNotNull(TuneManager.getInstance().getPushManager());
        assertNotNull(TuneManager.getInstance().getSessionManager());
        assertTrue(TuneEventBus.isEnabled());

        TuneManager.getInstance().getConfigurationManager().getConfigurationIfDisabled();
        TuneManager.getInstance().getConfigurationManager().onEvent(new TuneAppForegrounded("not used", 1L));

        // Disabling us doesn't immediately shut us down
        assertNotNull(TuneManager.getInstance());
        assertNotNull(TuneManager.getInstance().getAnalyticsManager());
        assertNotNull(TuneManager.getInstance().getApi());
        assertNotNull(TuneManager.getInstance().getConfigurationManager());
        assertNotNull(TuneManager.getInstance().getConnectedModeManager());
        assertNotNull(TuneManager.getInstance().getDeepActionManager());
        assertNotNull(TuneManager.getInstance().getExperimentManager());
        assertNotNull(TuneManager.getInstance().getFileManager());
        assertNotNull(TuneManager.getInstance().getPlaylistManager());
        assertNotNull(TuneManager.getInstance().getPowerHookManager());
        assertNotNull(TuneManager.getInstance().getProfileManager());
        assertNotNull(TuneManager.getInstance().getPushManager());
        assertNotNull(TuneManager.getInstance().getSessionManager());
        assertTrue(TuneEventBus.isEnabled());

        // Simulate a fresh restart
        initWith(Arrays.asList("configuration_disabled.json"));

        assertNotNull(TuneManager.getInstance());
        assertNull(TuneManager.getInstance().getAnalyticsManager());
        assertNotNull(TuneManager.getInstance().getApi());
        assertNotNull(TuneManager.getInstance().getConfigurationManager());
        assertNull(TuneManager.getInstance().getConnectedModeManager());
        assertNull(TuneManager.getInstance().getDeepActionManager());
        assertNotNull(TuneManager.getInstance().getExperimentManager());
        assertNotNull(TuneManager.getInstance().getFileManager());
        assertNotNull(TuneManager.getInstance().getPlaylistManager());
        assertNotNull(TuneManager.getInstance().getPowerHookManager());
        assertNotNull(TuneManager.getInstance().getProfileManager());
        assertNull(TuneManager.getInstance().getPushManager());
        assertNull(TuneManager.getInstance().getSessionManager());
        assertFalse(TuneEventBus.isEnabled());

        // Simulate a fresh restart
        initWith(Arrays.asList("configuration_enabled.json"));

        TuneManager.getInstance().getConfigurationManager().getConfigurationIfDisabled();
        TuneManager.getInstance().getConfigurationManager().onEvent(new TuneAppForegrounded("not used", 1L));

        assertNotNull(TuneManager.getInstance());
        assertNull(TuneManager.getInstance().getAnalyticsManager());
        assertNotNull(TuneManager.getInstance().getApi());
        assertNotNull(TuneManager.getInstance().getConfigurationManager());
        assertNull(TuneManager.getInstance().getConnectedModeManager());
        assertNull(TuneManager.getInstance().getDeepActionManager());
        assertNotNull(TuneManager.getInstance().getExperimentManager());
        assertNotNull(TuneManager.getInstance().getFileManager());
        assertNotNull(TuneManager.getInstance().getPlaylistManager());
        assertNotNull(TuneManager.getInstance().getPowerHookManager());
        assertNotNull(TuneManager.getInstance().getProfileManager());
        assertNull(TuneManager.getInstance().getPushManager());
        assertNull(TuneManager.getInstance().getSessionManager());
        assertFalse(TuneEventBus.isEnabled());

        // Simulate a fresh restart
        initWith(Arrays.asList("configuration_enabled.json"));

        // The session after we get the enabled flag everything is started up again
        assertNotNull(TuneManager.getInstance());
        assertNotNull(TuneManager.getInstance().getAnalyticsManager());
        assertNotNull(TuneManager.getInstance().getApi());
        assertNotNull(TuneManager.getInstance().getConfigurationManager());
        assertNotNull(TuneManager.getInstance().getConnectedModeManager());
        assertNotNull(TuneManager.getInstance().getDeepActionManager());
        assertNotNull(TuneManager.getInstance().getExperimentManager());
        assertNotNull(TuneManager.getInstance().getFileManager());
        assertNotNull(TuneManager.getInstance().getPlaylistManager());
        assertNotNull(TuneManager.getInstance().getPowerHookManager());
        assertNotNull(TuneManager.getInstance().getProfileManager());
        assertNotNull(TuneManager.getInstance().getPushManager());
        assertNotNull(TuneManager.getInstance().getSessionManager());
        assertTrue(TuneEventBus.isEnabled());
    }

    @Test
    public void testGetHookValueById() throws Exception {
        initWith(Arrays.asList("configuration_disabled.json"));

        tune.registerPowerHook("name", "friendly name", "foobar");

        assertEquals("foobar", tune.getValueForHookById("name"));

        JSONObject phookJson1 = new JSONObject();
        phookJson1.put(TunePowerHookValue.VALUE, "bingbang");
        postPlaylistChangedWithPhookJson("name", phookJson1);

        assertEquals("bingbang", tune.getValueForHookById("name"));

        TuneManager.getInstance().getConfigurationManager().onEvent(new TuneAppForegrounded("not used", 1L));

        // Disabling us doesn't immediately revert powerhook values to the default value
        assertEquals("bingbang", tune.getValueForHookById("name"));

        // Simulate a fresh restart
        initWith(Arrays.asList("configuration_disabled.json"));

        tune.registerPowerHook("name", "friendly name", "foobar");

        // We should still be able to get the default value
        assertEquals("foobar", tune.getValueForHookById("name"));

        // Simulate a fresh restart
        initWith(Arrays.asList("configuration_enabled.json"));
        tune.registerPowerHook("name", "friendly name", "foobar");

        // When we go live, we don't go live immediately
        assertEquals("foobar", tune.getValueForHookById("name"));

        // Simulate a fresh restart
        initWith(Arrays.asList("configuration_enabled.json"));
        tune.registerPowerHook("name", "friendly name", "foobar");

        postPlaylistChangedWithPhookJson("name", phookJson1);

        assertEquals("bingbang", tune.getValueForHookById("name"));
    }

    private void initWith(List<String> configs) {
        TuneManager.destroy();
        // The eventbus always starts out enabled then becomes disabled
        TuneEventBus.enable();
        TuneManager.init(getContext(), TuneTestWrapper.getTestingConfig(configs));
    }

    private void postPlaylistChangedWithPhookJson(String name, JSONObject phookJson) throws JSONException {
        JSONObject phooksJson = new JSONObject();
        phooksJson.put(name, phookJson);

        JSONObject playlistJson = new JSONObject();
        playlistJson.put(TunePlaylist.POWER_HOOKS_KEY, phooksJson);

        TunePlaylist playlist = new TunePlaylist(playlistJson);

        TuneManager.getInstance().getPowerHookManager().updatePowerHooksFromPlaylist(playlist);
    }
}
