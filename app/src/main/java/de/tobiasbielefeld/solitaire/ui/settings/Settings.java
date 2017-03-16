/*
 * Copyright (C) 2016  Tobias Bielefeld
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you want to contact me, send me an e-mail at tobias.bielefeld@gmail.com
 */

package de.tobiasbielefeld.solitaire.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;
import java.util.Locale;
import android.content.Intent;

import java.util.List;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.SharedData;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.games.FortyEight;
import de.tobiasbielefeld.solitaire.games.Pyramid;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/*
 *  Settings activity created with the "Create settings activity" tool from Android Studio.
 */

public class Settings extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    Toast toast;
    private Preference preferenceCards, preferenceCardsBackground, preferenceMenuBarPosition;
    private Preference preferenceMenuColumns;

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ViewGroup) getListView().getParent()).setPadding(0, 0, 0, 0);                             //remove huge padding in landscape

         /* set a nice back arrow in the actionBar */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //only item is the back arrow
        finish();
        return true;
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_card_drawables))) {
            Card.updateCardDrawableChoice();
            setPreferenceCardsSummary();

        } else if (key.equals(getString(R.string.pref_key_card_background))) {
            Card.updateCardBackgroundChoice();
            setPreferenceCardsBackgroundSummary();

        } else if (key.equals(getString(R.string.pref_key_hide_status_bar))) {
            showOrHideStatusBar();

        } else if (key.equals(getString(R.string.pref_key_orientation))) {
            setOrientation();

        } else if (key.equals(getString(R.string.pref_key_left_handed_mode))) {
            if (gameLogic != null)
                gameLogic.mirrorStacks();

        } else if (key.equals(getString(R.string.pref_key_klondike_draw))) {
            showToast(getString(R.string.settings_restart_klondike));

        } else if (key.equals(getString(R.string.pref_key_canfield_draw))) {
            showToast(getString(R.string.settings_restart_canfield));

        } else if (key.equals(getString(R.string.pref_key_spider_difficulty))) {
            showToast(getString(R.string.settings_restart_spider));

        } else if (key.equals(getString(R.string.pref_key_yukon_rules))) {
            showToast(getString(R.string.settings_restart_yukon));

        } else if (key.equals(getString(R.string.pref_key_menu_columns_portrait)) || key.equals(getString(R.string.pref_key_menu_columns_landscape))) {
            setPreferenceMenuColumns();

        } else if (key.equals(getString(R.string.pref_key_language))) {
            setLocale();

        } else if (key.equals(getString(R.string.pref_key_forty_eight_limited_redeals))) {
            if (currentGame instanceof FortyEight)
                gameLogic.toggleNumberOfRedeals();

        } else if (key.equals(getString(R.string.pref_key_pyramid_limited_redeals))) {
            if (currentGame instanceof Pyramid)
                gameLogic.toggleNumberOfRedeals();

        } else if (key.equals(getString(R.string.pref_key_icon_theme))) {
            if (gameLogic != null)
                gameLogic.updateIcons();

        } else if (key.equals(getString(R.string.pref_key_menu_bar_position_landscape)) || key.equals(getString(R.string.pref_key_menu_bar_position_portrait))) {
            setPreferenceMenuBarPosition();
            if (gameLogic != null)
                gameLogic.updateMenuBar();

        }
    }

    public void onResume() {
        super.onResume();

        if (savedSharedData==null) {
            savedSharedData = PreferenceManager.getDefaultSharedPreferences(this);
        }

        if (savedGameData==null) {
            savedGameData = getSharedPreferences(lg.getSharedPrefName(), MODE_PRIVATE);
        }

        savedSharedData.registerOnSharedPreferenceChangeListener(this);
        showOrHideStatusBar();
        setOrientation();
    }

    public void onPause() {
        super.onPause();
        savedSharedData.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setPreferenceCardsBackgroundSummary() {
        preferenceCardsBackground.setSummary(String.format(Locale.getDefault(), "%s %s",
                getString(R.string.settings_background), getSharedInt(CARD_BACKGROUND, 1)));

        switch (getSharedInt(CARD_BACKGROUND, 1)) {
            case 1:
                preferenceCardsBackground.setIcon(R.drawable.background_1);
                break;
            case 2:
                preferenceCardsBackground.setIcon(R.drawable.background_2);
                break;
            case 3:
                preferenceCardsBackground.setIcon(R.drawable.background_3);
                break;
            case 4:
                preferenceCardsBackground.setIcon(R.drawable.background_4);
                break;
            case 5:
                preferenceCardsBackground.setIcon(R.drawable.background_5);
                break;
            case 6:
                preferenceCardsBackground.setIcon(R.drawable.background_6);
                break;
            case 7:
                preferenceCardsBackground.setIcon(R.drawable.background_7);
                break;
            case 8:
                preferenceCardsBackground.setIcon(R.drawable.background_8);
                break;
            case 9:
                preferenceCardsBackground.setIcon(R.drawable.background_9);
                break;
            case 10:
                preferenceCardsBackground.setIcon(R.drawable.background_10);
                break;
            case 11:
                preferenceCardsBackground.setIcon(R.drawable.background_11);
                break;
            case 12:
                preferenceCardsBackground.setIcon(R.drawable.background_12);
                break;
        }
    }

    private void setPreferenceCardsSummary() {
        String text = "";

        switch (getSharedInt(CARD_DRAWABLES, 1)) {
            case 1:
                text = getString(R.string.settings_basic);
                preferenceCards.setIcon(R.drawable.basic_diamonds_13);
                break;
            case 2:
                text = getString(R.string.settings_classic);
                preferenceCards.setIcon(R.drawable.classic_diamonds_13);
                break;
            case 3:
                text = getString(R.string.settings_abstract);
                preferenceCards.setIcon(R.drawable.abstract_diamonds_13);
                break;
            case 4:
                text = getString(R.string.settings_simple);
                preferenceCards.setIcon(R.drawable.simple_diamonds_13);
                break;
            case 5:
                text = getString(R.string.settings_modern);
                preferenceCards.setIcon(R.drawable.modern_diamonds_13);
                break;
            case 6:
                text = getString(R.string.settings_dark);
                preferenceCards.setIcon(R.drawable.dark_diamonds_13);
                break;
        }

        preferenceCards.setSummary(text);
    }

    private void setPreferenceMenuColumns(){
        int portraitValue = Integer.parseInt(getSharedString(MENU_COLUMNS_PORTRAIT,DEFAULT_MENU_COLUMNS_PORTRAIT));
        int landscapeValue = Integer.parseInt(getSharedString(MENU_COLUMNS_LANDSCAPE,DEFAULT_MENU_COLUMNS_LANDSCAPE));

        String text = String.format(Locale.getDefault(),"%s: %d\n%s: %d",
                getString(R.string.portrait),portraitValue,getString(R.string.landscape),landscapeValue);

        preferenceMenuColumns.setSummary(text);
    }

    private void setPreferenceMenuBarPosition(){
        String portrait, landscape;
        if (sharedStringEquals(getString(R.string.pref_key_menu_bar_position_portrait),DEFAULT_MENU_BAR_POSITION_PORTRAIT)) {
            portrait = getString(R.string.settings_menu_bar_position_bottom);
        } else {
            portrait = getString(R.string.settings_menu_bar_position_top);
        }

        if (sharedStringEquals(getString(R.string.pref_key_menu_bar_position_landscape),DEFAULT_MENU_BAR_POSITION_LANDSCAPE)) {
            landscape = getString(R.string.settings_menu_bar_position_right);
        } else {
            landscape = getString(R.string.settings_menu_bar_position_left);
        }

        String text = String.format(Locale.getDefault(),"%s: %s\n%s: %s",
                getString(R.string.portrait),portrait,getString(R.string.landscape),landscape);

        preferenceMenuBarPosition.setSummary(text);
    }

    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || CustomizationPreferenceFragment.class.getName().equals(fragmentName)
                || OtherPreferenceFragment.class.getName().equals(fragmentName)
                || GamesPreferenceFragment.class.getName().equals(fragmentName)
                || MenuPreferenceFragment.class.getName().equals(fragmentName)
                || DoubleTapPreferenceFragment.class.getName().equals(fragmentName);
    }

    private void setOrientation() {
        switch (getSharedString(PREF_KEY_ORIENTATION, DEFAULT_ORIENTATION)) {
            case "1": //follow system settings
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                break;
            case "2": //portrait
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case "3": //landscape
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case "4": //landscape upside down
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
        }
    }

    private void showOrHideStatusBar() {
        if (getSharedBoolean(getString(R.string.pref_key_hide_status_bar), false))
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void showToast( String text) {
        if (toast == null) {
            toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        }
        else
            toast.setText(text);

        toast.show();
    }

    public static class CustomizationPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_customize);
            setHasOptionsMenu(true);

            Settings settings = (Settings) getActivity();

            settings.preferenceCards = findPreference(getString(R.string.pref_key_cards));
            settings.preferenceCardsBackground = findPreference(getString(R.string.pref_key_cards_background));
            settings.preferenceMenuBarPosition = findPreference(getString(R.string.pref_key_menu_bar_position));

            settings.setPreferenceCardsSummary();
            settings.setPreferenceCardsBackgroundSummary();
            settings.setPreferenceMenuBarPosition();
        }
    }

    public static class OtherPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_other);
            setHasOptionsMenu(true);
        }
    }

    public static class GamesPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_games);
            setHasOptionsMenu(true);
        }
    }

    public static class MenuPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_menu);
            setHasOptionsMenu(true);

            Settings settings = (Settings) getActivity();

            settings.preferenceMenuColumns = findPreference(getString(R.string.pref_key_menu_columns));
            settings.setPreferenceMenuColumns();
        }
    }

    public static class DoubleTapPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_double_tap);
            setHasOptionsMenu(true);
        }
    }

    private void setLocale() {
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}
