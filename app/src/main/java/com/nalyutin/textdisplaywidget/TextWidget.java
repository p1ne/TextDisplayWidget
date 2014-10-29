package com.nalyutin.textdisplaywidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.nalyutin.util.TextFileReader;


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link TextWidgetConfigureActivity TextWidgetConfigureActivity}
 */
public class TextWidget extends AppWidgetProvider {

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
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {


        //CharSequence widgetText = TextWidgetConfigureActivity.loadPref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.text_widget);
        String fileName = TextWidgetConfigureActivity.loadPref(context, appWidgetId, R.id.fileNameEdit);
        if (!fileName.equals("")) {
            views.setTextViewText(R.id.textView, TextFileReader.getFileContents(fileName));
        } else {
            views.setTextViewText(R.id.textView, "No file loaded yet");
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

}


