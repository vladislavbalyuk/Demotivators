package com.status.demotivators;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class ConfigActivity extends AppCompatActivity {

    int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    Intent resultValue;
    int time;

    public final static String WIDGET_TIME = "widget_time";
    public final static String WIDGET_PREF = "widget_pref";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Config");

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if(widgetID == AppWidgetManager.INVALID_APPWIDGET_ID){
            finish();
        }

        resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);

        setResult(RESULT_CANCELED, resultValue);

        setContentView(R.layout.activity_config);
    }

    public void onClick(View v) {
        EditText editText = (EditText) findViewById(R.id.edit_text);

        if (!editText.getText().toString().trim().isEmpty()) {
            time = Integer.parseInt(editText.getText().toString());
            if(time > 0){



        SharedPreferences sp = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putInt(WIDGET_TIME, Integer.parseInt(editText.getText().toString()));
//        editor.commit();

                if (DemotivatorWidget.alarmManager == null){
                    DemotivatorWidget.alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                }
                Intent intent = new Intent(getApplicationContext(), AlarmManagerBroadcastReceiver.class);
                intent.putExtra("widgetID",widgetID);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), widgetID, intent, 0);
                DemotivatorWidget.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), time * 60 * 100, pendingIntent);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                DemotivatorWidget.updateAppWidget(this, appWidgetManager, sp, widgetID);

                setResult(RESULT_OK,resultValue);
            }
        }

        finish();
    }
}
