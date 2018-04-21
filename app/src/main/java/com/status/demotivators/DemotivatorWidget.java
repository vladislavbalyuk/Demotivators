package com.status.demotivators;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DemotivatorWidget extends AppWidgetProvider {

    private PendingIntent service = null;
    private static boolean alarmManadgerEnabled = false;
    public static AlarmManager alarmManager;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                SharedPreferences sp, int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.demotivator_widget);

        Intent updateIntent = new Intent(context, DemotivatorWidget.class);
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                new int[]{appWidgetId});
        PendingIntent pIntent = PendingIntent.getBroadcast(context, appWidgetId, updateIntent, 0);
        views.setOnClickPendingIntent(R.id.text_refresh, pIntent);

        new MyTask(context, views, appWidgetManager, appWidgetId).execute();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.d("LOG_TAG", "Updated");
        SharedPreferences sp = context.getSharedPreferences(ConfigActivity.WIDGET_PREF,
                Context.MODE_PRIVATE);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, sp, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {

        Log.d("LOG_TAG", "Enabled");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d("LOG_TAG", "Deleted");

//        SharedPreferences.Editor editor = context.getSharedPreferences(ConfigActivity.WIDGET_PREF,
//                Context.MODE_PRIVATE).edit();

        for (int widgetID : appWidgetIds) {
//            editor.remove(ConfigActivity.WIDGET_TIME);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widgetID, intent, 0);
            alarmManager.cancel(pendingIntent);
            alarmManadgerEnabled = false;
        }
//        editor.commit();

    }

    @Override
    public void onDisabled(Context context) {
        Log.d("LOG_TAG", "Disabled");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();
        String actionName = "show_details";

        if (actionName.equals(action)) {
            Intent intentDetail = new Intent(context, DetailActivity.class);
            intentDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle extras = intent.getExtras();
            if (extras != null) {
                intentDetail.putExtra("url", extras.getString("url"));
                context.startActivity(intentDetail);
            }
        }
    }

    public static class MyTask extends AsyncTask<Void, Void, Demotivator> {

        private Context context;
        final private RemoteViews views;
        final AppWidgetManager appWidgetManager;
        final int appWidgetId;

        public MyTask(Context context, RemoteViews views, AppWidgetManager appWidgetManager, int appWidgetId) {
            this.context = context;
            this.views = views;
            this.appWidgetManager = appWidgetManager;
            this.appWidgetId = appWidgetId;
        }

        @Override
        protected Demotivator doInBackground(Void... params) {

            BufferedReader reader = null;
            StringBuilder buf = new StringBuilder();
            try {
                URL url = new URL("https://demotivators.to/random/");
                HttpsURLConnection c = (HttpsURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setReadTimeout(10000);
                c.connect();
                reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    buf.append(line + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                    }
                }
            }

            String s = buf.toString();
            int start = s.indexOf("data-sharer-url");
            s = s.substring(start + 17);
            start = s.indexOf("\"");
            String urlPage = s.substring(0, start);
            start = s.indexOf("data-sharer-image");
            s = s.substring(start + 19);
            start = s.indexOf("\">");
            String urlImage = s.substring(0, start);
            Demotivator demotivator = new Demotivator(urlPage, urlImage);

            return demotivator;
        }

        protected void onPostExecute(final Demotivator demotivator) {
            super.onPostExecute(demotivator);
            try {
                final Target target = new Target() {

                    @Override
                    public void onPrepareLoad(Drawable arg0) {
                        return;
                    }

                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {

                        Intent intent = new Intent(context, DemotivatorWidget.class);

                        intent.setData(Uri.withAppendedPath(Uri.parse(demotivator.getUrlPage()), String.valueOf(appWidgetId)));
                        intent.setAction("show_details");

                        intent.putExtra("url", demotivator.getUrlPage());
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                                new int[]{appWidgetId});
                        PendingIntent pIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, 0);
                        views.setOnClickPendingIntent(R.id.appwidget_image, pIntent);

                        views.setImageViewBitmap(R.id.appwidget_image, bitmap);


                        appWidgetManager.updateAppWidget(appWidgetId, views);
                    }

                    @Override
                    public void onBitmapFailed(Drawable arg0) {
                        return;
                    }

                };

                Picasso.with(context)
                        .load(Uri.parse(demotivator.getUrlImage()))
                        .into(target);
            } catch (Exception e) {
            }
            ;

        }
    }

}

