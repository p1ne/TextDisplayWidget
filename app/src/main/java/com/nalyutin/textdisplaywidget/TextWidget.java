package com.nalyutin.textdisplaywidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import android.widget.TextClock;
import android.widget.TextView;

import com.nalyutin.util.TextFileReader;


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link TextWidgetConfigureActivity TextWidgetConfigureActivity}
 */
public class TextWidget extends AppWidgetProvider {

    public static final String ACTION_AUTO_UPDATE = "AUTO_UPDATE";

    @InjectView(R.id.textClock) TextClock textClock;
    @InjectView(R.id.textView) TextView textView;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            TextWidgetConfigureActivity.deleteAllPrefs(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // start alarm
        TextWidgetAlarm appWidgetAlarm = new TextWidgetAlarm(context.getApplicationContext());
        appWidgetAlarm.startAlarm();
    }

    @Override
    public void onDisabled(Context context) {
        // stop alarm
        TextWidgetAlarm appWidgetAlarm = new TextWidgetAlarm(context.getApplicationContext());
        appWidgetAlarm.stopAlarm();
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.text_widget);

        String fileName = TextWidgetConfigureActivity.loadStringPref(context, appWidgetId, R.id.fileNameEdit);
        int refreshRate = TextWidgetConfigureActivity.loadIntPref(context, appWidgetId, R.id.refreshIntervalEdit);
        Boolean displayClock = TextWidgetConfigureActivity.loadBooleanPref(context, appWidgetId, R.id.displayClockcheckBox);

        if (displayClock) {
            views.setInt(R.id.textClock, "setVisibility", View.VISIBLE);
        } else {
            views.setInt(R.id.textClock, "setVisibility", View.GONE);
        }

        if (!fileName.equals("")) {
            views.setTextViewText(R.id.textView, TextFileReader.getFileContents(fileName));
        } else {
            views.setTextViewText(R.id.textView, "No file loaded yet");
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @OnClick( { R.id.textView, R.id.textClock, R.layout.text_widget} ) void configureWidget(View view) {
       Context context = view.getContext();
       Intent intent = new Intent(context, TextWidgetConfigureActivity.class);
       context.startActivity(intent);
    }

}


