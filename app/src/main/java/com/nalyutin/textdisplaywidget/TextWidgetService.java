package com.nalyutin.textdisplaywidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.widget.RemoteViews;

import com.nalyutin.util.TextFileReader;
import com.nalyutin.util.URLReader;

import java.util.Date;

/**
 * Created by pine on 14/11/14.
 */
public class TextWidgetService extends Service {
    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        String lastUpdated = DateFormat.format("hh:mm:ss", new Date()).toString();

        RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.text_widget);
        ComponentName thisWidget = new ComponentName(this, TextWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);

        for (int appWidgetId : manager.getAppWidgetIds(thisWidget)) {
            Context context = getApplicationContext();

            String fileName = TextWidgetConfigureActivity.loadStringPref(context, appWidgetId, R.id.file_name);

            String data = fileName;

            if (!fileName.equals("")) {
                if (fileName.matches("^https?://.*$")) {
                    data = URLReader.getData(fileName);
                } else {
                    data = TextFileReader.getData(fileName);
                }
            }

            data = lastUpdated  .concat(": ")
                                .concat(data);

            remoteView.setTextViewText(R.id.textView, data);
            manager.updateAppWidget(appWidgetId, remoteView);
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
