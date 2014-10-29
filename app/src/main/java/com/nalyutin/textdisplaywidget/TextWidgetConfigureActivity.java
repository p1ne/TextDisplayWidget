package com.nalyutin.textdisplaywidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


/**
 * The configuration screen for the {@link TextWidget TextWidget} AppWidget.
 */
public class TextWidgetConfigureActivity extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mFileNameText;
    EditText mRefreshIntervalText;
    private static final String PREFS_NAME = "com.nalyutin.textdisplaywidget.TextWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    public TextWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.text_widget_configure);
        mFileNameText = (EditText)findViewById(R.id.fileNameEdit);
        mRefreshIntervalText = (EditText)findViewById(R.id.refreshIntervalEdit);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

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

        // FIXME
        mFileNameText.setText(loadPref(TextWidgetConfigureActivity.this, mAppWidgetId, R.id.fileNameEdit));
        mRefreshIntervalText.setText(loadPref(TextWidgetConfigureActivity.this, mAppWidgetId, R.id.refreshIntervalEdit));
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = TextWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            saveAllPrefs(context,mAppWidgetId,mFileNameText.getText().toString(),mRefreshIntervalText.getText().toString());

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            TextWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    // Write the prefix to the SharedPreferences object for this widget
    static void saveAllPrefs(Context context, int appWidgetId, String fileName, String refreshInterval) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + R.id.fileNameEdit, fileName);
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + R.id.refreshIntervalEdit, refreshInterval);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadPref(Context context, int appWidgetId, int prefId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String prefValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId + prefId, null);
        if (prefValue == null) {
            return "";
        } else {
            return prefValue;
        }
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



