package me.jlurena.ritscheduler.homescreen;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.nightonke.boommenu.Util;

import me.jlurena.ritscheduler.R;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetProvider extends AppWidgetProvider {

    static final String ACTION_REFRESH = "me.jlurena.ritscheduler.action.ACTION_REFRESH";
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

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        Intent intent = new Intent(context, WidgetService.class);
        views.setRemoteAdapter(R.id.widget_calendar_container, intent);

        views.setOnClickPendingIntent(R.id.widget_refresh, getPendingSelfIntent(context, ACTION_REFRESH, appWidgetId));
        views.setOnClickPendingIntent(R.id.widget_next, getPendingSelfIntent(context, ACTION_NEXT, appWidgetId));
        views.setOnClickPendingIntent(R.id.widget_previous, getPendingSelfIntent(context, ACTION_PREVIOUS, appWidgetId));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_calendar_container);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        int width = Util.dp2px(newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH));
        width -= (width / 10);
        Intent broadcastIntent = new Intent(ACTION_REFRESH);
        broadcastIntent.putExtra(KEY_SIZE_CHANGE, width);
        context.sendBroadcast(broadcastIntent);
        updateAppWidget(context, appWidgetManager, appWidgetId);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action != null) {
            if (action.equals(ACTION_REFRESH) || action.equals(ACTION_NEXT) || action.equals(ACTION_PREVIOUS)) {
                context.sendBroadcast(new Intent(action));
            }
        }

        Bundle extra = intent.getExtras();
        if (extra != null && extra.containsKey(KEY_APP_WIDGET_ID)) {
            updateAppWidget(context, AppWidgetManager.getInstance(context), extra.getInt(KEY_APP_WIDGET_ID));
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}

