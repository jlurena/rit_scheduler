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
import android.support.v4.content.LocalBroadcastManager;
import android.util.SparseIntArray;
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

import static me.jlurena.ritscheduler.homescreen.WidgetProvider.ACTION_NEXT;
import static me.jlurena.ritscheduler.homescreen.WidgetProvider.ACTION_PREVIOUS;
import static me.jlurena.ritscheduler.homescreen.WidgetProvider.ACTION_REFRESH;
import static me.jlurena.ritscheduler.homescreen.WidgetProvider.KEY_APP_WIDGET_ID;

public class WidgetRemoteViewsFactory extends BroadcastReceiver implements RemoteViewsService.RemoteViewsFactory {


    private final Context context;
    private final DataManager dataManager;
    private List<Course> courses;
    private WeekView weekView;
    private Calendar currentDay;
    private int id;
    private int width;
    // k = id, v = width
    static final SparseIntArray idMap = new SparseIntArray();
    private LocalBroadcastManager localBroadcastManager;

    WidgetRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        this.dataManager = DataManager.getInstance(context);
        this.currentDay = Calendar.getInstance();
        try {
            dataManager.getModels(Course.TYPE, Course.class, (DataManager.DocumentParser<List<Course>>) models -> courses = models);
        } catch (Exception e) {
            // Crash gracefully :)
        }
        this.id = intent.getIntExtra(KEY_APP_WIDGET_ID, 0);
        idMap.put(id, 0);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PREVIOUS);
        filter.addAction(ACTION_NEXT);
        filter.addAction(ACTION_REFRESH);
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.registerReceiver(this, filter);
        updateCourseList(null);
    }

    private void updateCourseList(@Nullable String action) {
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
                case ACTION_NEXT:
                    currentDay.add(Calendar.DAY_OF_YEAR, 1);
                    break;
                case ACTION_PREVIOUS:
                    currentDay.add(Calendar.DAY_OF_YEAR, -1);
                    break;
                case ACTION_REFRESH:
                    try {
                        dataManager.getModels(Course.TYPE, Course.class, (DataManager.DocumentParser<List<Course>>) models -> courses = models);
                        weekView.notifyDatasetChanged();
                    } catch (Exception e) {
                        // Crash gracefully :)
                    }
                    break;
                default:
                    this.currentDay = Calendar.getInstance();
                    break;
            }
        }
        SettingsManager settings = SettingsManager.getInstance(context);
        int height = (weekView.getMaxTime() - weekView.getMinTime()) * Util.dp2px(50);
        int day = currentDay.get(Calendar.DAY_OF_WEEK);
        day = day == 1 ? 7 : day - 1;
        weekView.goToDay(day);
        weekView.setLimitTime(settings.getMinHour(), settings.getMaxHour() + 1); // View lasthour

        if (width <= 500) {
            weekView.setNumberOfVisibleDays(1);
        } else if (width <= 800) {
            weekView.setNumberOfVisibleDays(2);
        } else {
            weekView.setNumberOfVisibleDays(3);
        }
        weekView.measure(width, height);
        weekView.layout(0, 0, width, height);
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
        return id;
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
        localBroadcastManager.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String action = intent.getAction();
        // Ignore broadcasts not meant for this receiver
        if (extras != null && extras.getInt(KEY_APP_WIDGET_ID, -1) == this.id) {

            if (extras.containsKey(WidgetProvider.KEY_SIZE_CHANGE)) {
                this.width = extras.getInt(WidgetProvider.KEY_SIZE_CHANGE);
                idMap.put(this.id, this.width);
                updateCourseList(null);
            } else if (action != null && (action.equals(ACTION_REFRESH) || action.equals(ACTION_NEXT) || action.equals(ACTION_PREVIOUS))) {
                updateCourseList(action);
            }
        }
    }
}
