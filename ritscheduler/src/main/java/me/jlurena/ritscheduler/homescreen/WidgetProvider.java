package me.jlurena.ritscheduler.homescreen;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.RemoteViews;

import com.nightonke.boommenu.Util;

import java.util.Calendar;

import me.jlurena.ritscheduler.Home;
import me.jlurena.ritscheduler.R;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetProvider extends AppWidgetProvider {

    public static final String ACTION_REFRESH = "me.jlurena.ritscheduler.action.ACTION_REFRESH";
    static final String ACTION_NEXT = "me.jlurena.ritscheduler.action.ACTION_NEXT";
    static final String ACTION_PREVIOUS = "me.jlurena.ritscheduler.action.ACTION_PREVIOUS";
    static final String KEY_SIZE_CHANGE = "size_change";
    static final String KEY_APP_WIDGET_ID = "app_widget_id";

    private static PendingIntent getPendingSelfIntent(Context context, String action, int appWidgetId) {
        Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction(action);
        intent.putExtra(KEY_APP_WIDGET_ID, appWidgetId);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static void scheduleNextUpdate(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction(ACTION_REFRESH);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 1);
        midnight.set(Calendar.MILLISECOND, 0);
        midnight.add(Calendar.DAY_OF_YEAR, 1);

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, midnight.getTimeInMillis(), pendingIntent);
        }
    }

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(KEY_APP_WIDGET_ID, appWidgetId);
        views.setRemoteAdapter(R.id.widget_calendar_container, intent);

        // Buttons on widget click handlers
        views.setOnClickPendingIntent(R.id.widget_refresh, getPendingSelfIntent(context, ACTION_REFRESH, appWidgetId));
        views.setOnClickPendingIntent(R.id.widget_next, getPendingSelfIntent(context, ACTION_NEXT, appWidgetId));
        views.setOnClickPendingIntent(R.id.widget_previous, getPendingSelfIntent(context, ACTION_PREVIOUS, appWidgetId));

        // Widget Container click handler
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        homeIntent.setComponent(new ComponentName(context.getPackageName(), Home.class.getName()));
        PendingIntent homePendingIntent = PendingIntent.getActivity(context, 0, homeIntent, 0);
        views.setPendingIntentTemplate(R.id.widget_calendar_container, homePendingIntent);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_calendar_container);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        int width = Util.dp2px(newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH));
        Intent broadcastIntent = new Intent(ACTION_REFRESH);
        broadcastIntent.putExtra(KEY_SIZE_CHANGE, width);
        broadcastIntent.putExtra(KEY_APP_WIDGET_ID, appWidgetId);
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
        updateAppWidget(context, appWidgetManager, appWidgetId);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action != null && (action.equals(ACTION_REFRESH) || action.equals(ACTION_NEXT) || action.equals(ACTION_PREVIOUS))) {
            Bundle extra = intent.getExtras();
            // Update the widget
            if (extra != null && extra.containsKey(KEY_APP_WIDGET_ID)) {
                Intent actionIntent = new Intent(action);
                int id = extra.getInt(KEY_APP_WIDGET_ID);
                actionIntent.putExtra(KEY_APP_WIDGET_ID, id);
                LocalBroadcastManager.getInstance(context).sendBroadcast(actionIntent);
                updateAppWidget(context, AppWidgetManager.getInstance(context), id);
            }

        }

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        scheduleNextUpdate(context);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}

