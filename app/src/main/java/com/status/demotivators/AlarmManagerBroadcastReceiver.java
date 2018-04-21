package com.status.demotivators;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TAG");
        wakeLock.acquire();

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.demotivator_widget);
        ComponentName thisWidget = new ComponentName(context, DemotivatorWidget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        int widgetID = intent.getExtras().getInt("widgetID");
        Log.d("Log_Tag","" + widgetID);
//        for(int widgetID : appWidgetManager.getAppWidgetIds(thisWidget)){

            Intent updateIntent = new Intent(context, DemotivatorWidget.class);
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    new int[] { widgetID });
            PendingIntent updatePendingIntent = PendingIntent.getBroadcast(context, widgetID, updateIntent, 0);
            views.setOnClickPendingIntent(R.id.text_refresh, updatePendingIntent);

            new DemotivatorWidget.MyTask(context,views,appWidgetManager,widgetID).execute();
//        }


        wakeLock.release();
    }
}