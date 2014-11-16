package com.nalyutin.textdisplaywidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
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

import java.util.Calendar;


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link TextWidgetConfigureActivity TextWidgetConfigureActivity}
 */
public class TextWidget extends AppWidgetProvider {

    public static String ACTION_WIDGET_CONFIGURE = "ConfigureWidget";
    public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";

    public static final String ACTION_AUTO_UPDATE = "AUTO_UPDATE";

    private PendingIntent service = null;

    @InjectView(R.id.textClock) TextClock textClock;
    @InjectView(R.id.textView) TextView textView;

    @Override
    public void onEnabled(Context context) {
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        final Calendar TIME = Calendar.getInstance();
        TIME.set(Calendar.MINUTE, 0);
        TIME.set(Calendar.SECOND, 0);
        TIME.set(Calendar.MILLISECOND, 0);

        final Intent intent = new Intent(context, TextWidgetService.class);

        if (service == null)
        {
            service = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        int refreshRate = TextWidgetConfigureActivity.loadIntPref(context, R.id.refresh_interval);

        alarmManager.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 1000 * refreshRate, service);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            TextWidgetConfigureActivity.deleteAllPrefs(context, appWidgetId, appWidgetIds.length);
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (service != null)
        {
            m.cancel(service);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.text_widget);

        boolean displayClock = TextWidgetConfigureActivity.loadBooleanPref(context, appWidgetId, R.id.display_clock);
        int foreColor = TextWidgetConfigureActivity.loadIntPref(context, appWidgetId, R.id.foreground_picker);
        int backColor = TextWidgetConfigureActivity.loadIntPref(context, appWidgetId, R.id.background_picker);

        if (displayClock) {
            views.setInt(R.id.textClock, "setVisibility", View.VISIBLE);
        } else {
            views.setInt(R.id.textClock, "setVisibility", View.GONE);
        }

        views.setInt(R.id.textClock, "setTextColor", foreColor);
        views.setInt(R.id.textClock, "setBackgroundColor", backColor);
        views.setInt(R.id.textView, "setTextColor", foreColor);
        views.setInt(R.id.textView, "setBackgroundColor", backColor);

        String fileName = TextWidgetConfigureActivity.loadStringPref(context, appWidgetId, R.id.file_name);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @OnClick( R.id.textView ) void configureWidget(View view) {
       Context context = view.getContext();
       Intent intent = new Intent(context, TextWidgetConfigureActivity.class);
       context.startActivity(intent);
    }

}


