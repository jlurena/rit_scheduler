package me.jlurena.ritscheduler.homescreen;

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

    static int width = Util.dp2px(110);

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        Intent intent = new Intent(context, WidgetService.class);
        views.setRemoteAdapter(R.id.widget_calendar_container, intent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_calendar_container);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        WidgetProvider.width = Util.dp2px(newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH));
        updateAppWidget(context, appWidgetManager, appWidgetId);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
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

