package com.nalyutin.textdisplaywidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.InjectView;

/**
 * The configuration screen for the {@link TextWidget TextWidget} AppWidget.
 */
public class TextWidgetConfigureActivity extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    @InjectView(R.id.fileNameEdit) EditText mFileNameText;
    @InjectView(R.id.refreshIntervalEdit) EditText mRefreshIntervalText;
    @InjectView(R.id.displayClockcheckBox) CheckBox mDisplayClockCheckbox;

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
    }

    @OnClick(R.id.add_button) void addWidget()
    {
        final Context context = TextWidgetConfigureActivity.this;

        int refreshInterval = Integer.valueOf(mRefreshIntervalText.getText().toString());

        saveAllPrefs(context,   mAppWidgetId,
                                mFileNameText.getText().toString(),
                                refreshInterval,
                                mDisplayClockCheckbox.isChecked());

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        TextWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    static void saveAllPrefs(Context context, int appWidgetId, String fileName, int refreshInterval, Boolean displayClock) {
        saveStringPref(context, appWidgetId, R.id.fileNameEdit, fileName);
        saveIntPref(context, appWidgetId, R.id.refreshIntervalEdit, refreshInterval);
        saveBooleanPref(context, appWidgetId, R.id.displayClockcheckBox, displayClock);
    }

    static int loadIntPref(Context context, int appWidgetId, int prefId) {
        int prefValue;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        prefValue = prefs.getInt(PREF_PREFIX_KEY + appWidgetId + prefId, 1);
        return prefValue;
    }

    static void saveIntPref(Context context, int appWidgetId, int prefId, int prefValue) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId + prefId, prefValue);
        prefs.commit();
    }

    static Boolean loadBooleanPref(Context context, int appWidgetId, int prefId) {
        Boolean prefValue;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        prefValue = prefs.getBoolean(PREF_PREFIX_KEY + appWidgetId + prefId, false);
        return prefValue;
    }

    static void saveBooleanPref(Context context, int appWidgetId, int prefId, Boolean prefValue) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putBoolean(PREF_PREFIX_KEY + appWidgetId + prefId, prefValue);
        prefs.commit();
    }

    static String loadStringPref(Context context, int appWidgetId, int prefId) {
        String prefValue;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        prefValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId + prefId, "");
        return prefValue;
    }

    static void saveStringPref(Context context, int appWidgetId, int prefId, String prefValue) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + prefId, prefValue);
        prefs.commit();
    }

    static void deletePref(Context context, int appWidgetId, int prefId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + prefId);
        prefs.commit();
    }

    static void deleteAllPrefs(Context context, int appWidgetId) {
        deletePref(context, appWidgetId, R.id.fileNameEdit);
        deletePref(context, appWidgetId, R.id.refreshIntervalEdit);
    }

}



