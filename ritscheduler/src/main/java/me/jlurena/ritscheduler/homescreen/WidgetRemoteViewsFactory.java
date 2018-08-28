package me.jlurena.ritscheduler.homescreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.couchbase.lite.CouchbaseLiteException;
import com.nightonke.boommenu.Util;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.format.TextStyle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.jlurena.revolvingweekview.WeekView;
import me.jlurena.revolvingweekview.WeekViewEvent;
import me.jlurena.ritscheduler.R;
import me.jlurena.ritscheduler.database.DataManager;
import me.jlurena.ritscheduler.models.Course;
import me.jlurena.ritscheduler.utils.SettingsManager;

public class WidgetRemoteViewsFactory extends BroadcastReceiver implements RemoteViewsService.RemoteViewsFactory {


    private final Context context;
    private final DataManager dataManager;
    private final SettingsManager settings;
    private List<Course> courses;
    private WeekView weekView;
    private Calendar currentDay;
    private int width;

    WidgetRemoteViewsFactory(Context context) {
        this.context = context;
        this.dataManager = DataManager.getInstance(context);
        this.currentDay = Calendar.getInstance();
        this.width = Util.dp2px(110);

        IntentFilter filter = new IntentFilter();
        filter.addAction(WidgetProvider.ACTION_PREVIOUS);
        filter.addAction(WidgetProvider.ACTION_NEXT);
        filter.addAction(WidgetProvider.ACTION_REFRESH);
        context.registerReceiver(this, filter);
        this.settings = SettingsManager.getInstance(context);
        updateCourseList(null);
    }

    private void updateCourseList(@Nullable String action) {
        try {
            dataManager.getModels(Course.TYPE, Course.class, (DataManager.DocumentParser<List<Course>>) models -> courses = models);

        } catch (CouchbaseLiteException e) {
            // Can't really do anything but crash gracefully
        }
        // Only initialize once. Gets called many times.
        if (this.weekView == null) {
            this.weekView = new WeekView(context);

            weekView.setWeekViewLoader(() -> {
                List<WeekViewEvent> events = new ArrayList<>();

                if (!courses.isEmpty()) {
                    for (Course course : courses) {
                        events.addAll(course.toWeekViewEvents());
                    }
                }

                return events;
            });
            Resources resources = context.getResources();
            weekView.setDayTimeInterpreter(new WeekView.DayTimeInterpreter() {
                @Override
                public String interpretDay(int dayValue) {
                    DayOfWeek day = DayOfWeek.of(dayValue);
                    return day.getDisplayName(TextStyle.SHORT, Locale.getDefault());
                }

                @Override
                public String interpretTime(int hour, int minutes) {
                    if (hour == 0) {
                        return "12AM";
                    } else if (hour < 12) {
                        return hour + "AM";
                    } else if (hour == 12) {
                        return "12PM";
                    } else {
                        return hour - 12 + "PM";
                    }
                }
            });
            weekView.setNumberOfVisibleDays(1);
            weekView.setDayBackgroundColor(resources.getColor(R.color.calendar_day_background_color));
            weekView.setEventTextColor(resources.getColor(android.R.color.white));
            weekView.setHeaderRowBackgroundColor(resources.getColor(R.color.color_accent));
            weekView.setHeaderColumnBackgroundColor(resources.getColor(R.color.color_accent));
            weekView.setTodayBackgroundColor(resources.getColor(R.color.calendar_today_background_color));
            weekView.setDrawingCacheEnabled(true);
        }

        if (action != null) {
            switch (action) {
                case WidgetProvider.ACTION_NEXT:
                    currentDay.add(Calendar.DAY_OF_YEAR, 1);
                    break;
                case WidgetProvider.ACTION_PREVIOUS:
                    currentDay.add(Calendar.DAY_OF_YEAR, -1);
                    break;
                case WidgetProvider.ACTION_REFRESH:
                default:
                    this.currentDay = Calendar.getInstance();
                    break;
            }
        }

        int height = (weekView.getMaxTime() - weekView.getMinTime()) * Util.dp2px(50);
        int day = currentDay.get(Calendar.DAY_OF_WEEK);
        day = day == 1 ? 7 : day - 1;
        weekView.goToDay(day);
        weekView.notifyDatasetChanged();
        weekView.setLimitTime(settings.getMinHour(), settings.getMaxHour() + 1); // View lasthour

        if (width <= 500) {
            weekView.setNumberOfVisibleDays(1);
        } else if (width <= 800) {
            weekView.setNumberOfVisibleDays(2);
        } else {
            weekView.setNumberOfVisibleDays(3);
        }
        weekView.measure(this.width, height);
        weekView.layout(0, 0, this.width, height);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION) {
            return null;
        }

        Bitmap bitmap = this.weekView.getDrawingCache();
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.calendar_imageview);
        views.setImageViewBitmap(R.id.widget_calendar, bitmap);

        views.setOnClickFillInIntent(R.id.widget_calendar, new Intent());

        return views;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();
        updateCourseList(null);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        context.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String action = intent.getAction();
        if (extras != null && extras.containsKey(WidgetProvider.KEY_SIZE_CHANGE)) {
            this.width = extras.getInt(WidgetProvider.KEY_SIZE_CHANGE);
            updateCourseList(null);
        } else if (action != null &&
                (action.equals(WidgetProvider.ACTION_REFRESH)
                        || action.equals(WidgetProvider.ACTION_NEXT)
                        || action.equals(WidgetProvider.ACTION_PREVIOUS)
                )) {
            updateCourseList(action);
        }
    }
}
