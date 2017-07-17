package me.bonoj.moviebug.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtils {

    public static void setSortOrder(Context context, String sortOrder) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(context.getString(me.bonoj.moviebug.R.string.pref_sort_order_key), sortOrder);
        editor.apply();
    }

    public static String getSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortOrderKey = context.getString(me.bonoj.moviebug.R.string.pref_sort_order_key);
        String defaultSortOrder = QueryBuilderUtils.SORT_POPULARITY;
        return prefs.getString(sortOrderKey, defaultSortOrder);
    }

    public static void setStartupCheck(Context context, boolean isStartup) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(me.bonoj.moviebug.R.string.pref_startup_check_key), isStartup);
        editor.apply();
    }

    public static boolean getStartupCheck(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String startupCheckKey = context.getString(me.bonoj.moviebug.R.string.pref_startup_check_key);
        return prefs.getBoolean(startupCheckKey, true);
    }

    public static void setTabletPortraitBackPress(Context context, boolean isTabletPortraitBackPressed) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(me.bonoj.moviebug.R.string.pref_tablet_portrait_back_press_key), isTabletPortraitBackPressed);
        editor.apply();
    }

    public static boolean getTabletPortraitBackPress(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String tabletPortraitBackPressKey = context.getString(me.bonoj.moviebug.R.string.pref_tablet_portrait_back_press_key);
        return prefs.getBoolean(tabletPortraitBackPressKey, false);
    }
}
