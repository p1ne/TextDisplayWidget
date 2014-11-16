package com.nalyutin.textdisplaywidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.InjectView;

/**
 * The configuration screen for the {@link TextWidget TextWidget} AppWidget.
 */
public class TextWidgetConfigureActivity extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    @InjectView(R.id.file_name) EditText mFileNameText;
    @InjectView(R.id.refresh_interval) EditText mRefreshIntervalText;
    @InjectView(R.id.display_clock) CheckBox mDisplayClockCheckbox;

    @InjectView(R.id.foreground_picker) ColorPicker mForegroundColorPicker;
    @InjectView(R.id.foreground_valuebar) SVBar mForegroundSVBar;
    @InjectView(R.id.foreground_opacitybar) OpacityBar mForegroundOpacityBar;

    @InjectView(R.id.background_picker) ColorPicker mBackgroundColorPicker;
    @InjectView(R.id.background_valuebar) SVBar mBackgroundSVBar;
    @InjectView(R.id.background_opacitybar) OpacityBar mBackgroundOpacityBar;

    private static final String PREFS_NAME = "com.nalyutin.textdisplaywidget.TextWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    public TextWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setResult(RESULT_CANCELED);

        setContentView(R.layout.text_widget_configure);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        ButterKnife.inject(this);

        mForegroundColorPicker.addSVBar(mForegroundSVBar);
        mForegroundColorPicker.addOpacityBar(mForegroundOpacityBar);

        mBackgroundColorPicker.addSVBar(mBackgroundSVBar);
        mBackgroundColorPicker.addOpacityBar(mBackgroundOpacityBar);

        mForegroundColorPicker.setColor(Color.WHITE);
        mForegroundColorPicker.setNewCenterColor(Color.WHITE);
        mForegroundColorPicker.setOldCenterColor(Color.WHITE);

        mBackgroundColorPicker.setColor(Color.TRANSPARENT);
        mBackgroundColorPicker.setNewCenterColor(Color.TRANSPARENT);
        mBackgroundColorPicker.setOldCenterColor(Color.TRANSPARENT);

    }

    @OnClick(R.id.add_button) void addWidget()
    {
        final Context context = TextWidgetConfigureActivity.this;

        int refreshInterval = 60;

        try {
            refreshInterval = Integer.parseInt(mRefreshIntervalText.getText().toString());
        } catch (NumberFormatException e) {
            refreshInterval = 60; // default value
        }

        saveAllPrefs(context,   mAppWidgetId,
                                mFileNameText.getText().toString(),
                                refreshInterval,
                                mDisplayClockCheckbox.isChecked(),
                                mForegroundColorPicker.getColor(),
                                mBackgroundColorPicker.getColor());

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        TextWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    static void saveAllPrefs(Context context, int appWidgetId,
                                String fileName,
                                int refreshInterval,
                                boolean displayClock,
                                int foreColor,
                                int backColor) {
        saveStringPref(context, appWidgetId, R.id.file_name, fileName);
        saveIntPref(context, R.id.refresh_interval, refreshInterval);
        saveBooleanPref(context, appWidgetId, R.id.display_clock, displayClock);
        saveIntPref(context, appWidgetId, R.id.foreground_picker, foreColor);
        saveIntPref(context, appWidgetId, R.id.background_picker, backColor);
    }

    static String prefId(int a) {
        return PREF_PREFIX_KEY.concat(String.valueOf(a));
    }

    static String prefId(int a, int b) {
        return PREF_PREFIX_KEY.concat(String.valueOf(a)).concat(String.valueOf(b));
    }

    static int loadIntPref(Context context, int prefName) {
        int prefValue;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        prefValue = prefs.getInt(prefId(prefName), 1);
        return prefValue;
    }

    static void saveIntPref(Context context, int prefName, int prefValue) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(prefId(prefName), prefValue);
        prefs.commit();
    }

    static int loadIntPref(Context context, int appWidgetId, int prefName) {
        int prefValue;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        prefValue = prefs.getInt(prefId(appWidgetId, prefName), 1);
        return prefValue;
    }

    static void saveIntPref(Context context, int appWidgetId, int prefName, int prefValue) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(prefId(appWidgetId, prefName), prefValue);
        prefs.commit();
    }

    static boolean loadBooleanPref(Context context, int appWidgetId, int prefName) {
        boolean prefValue;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        prefValue = prefs.getBoolean(prefId(appWidgetId, prefName), false);
        return prefValue;
    }

    static void saveBooleanPref(Context context, int appWidgetId, int prefName, boolean prefValue) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putBoolean(prefId(appWidgetId, prefName), prefValue);
        prefs.commit();
    }

    static String loadStringPref(Context context, int appWidgetId, int prefName) {
        String prefValue;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        prefValue = prefs.getString(prefId(appWidgetId, prefName), "");
        return prefValue;
    }

    static void saveStringPref(Context context, int appWidgetId, int prefName, String prefValue) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(prefId(appWidgetId, prefName), prefValue);
        prefs.commit();
    }

    static void deletePref(Context context, int appWidgetId, int prefName) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(prefId(appWidgetId, prefName));
        prefs.commit();
    }

    static void deletePref(Context context, int prefName) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(prefId(prefName));
        prefs.commit();
    }

    static void deleteAllPrefs(Context context, int appWidgetId, int numWidgets) {
        deletePref(context, appWidgetId, R.id.file_name);
        deletePref(context, appWidgetId, R.id.refresh_interval);
        deletePref(context, appWidgetId, R.id.display_clock);
        deletePref(context, appWidgetId, R.id.foreground_picker);
        deletePref(context, appWidgetId, R.id.background_picker);
        if (numWidgets == 1) {
            deletePref(context, R.id.refresh_interval);
        }
    }

}