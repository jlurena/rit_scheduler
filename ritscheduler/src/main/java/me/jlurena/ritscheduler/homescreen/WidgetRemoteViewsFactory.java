package me.jlurena.ritscheduler.homescreen;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Binder;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.couchbase.lite.CouchbaseLiteException;
import com.nightonke.boommenu.Util;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.format.TextStyle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import me.jlurena.revolvingweekview.WeekView;
import me.jlurena.revolvingweekview.WeekViewEvent;
import me.jlurena.ritscheduler.R;
import me.jlurena.ritscheduler.database.DataManager;
import me.jlurena.ritscheduler.models.Course;
import me.jlurena.ritscheduler.models.Settings;

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {


    private final Context context;
    private final DataManager dataManager;
    private final HashSet<Course> courses;
    private WeekView weekView;

    WidgetRemoteViewsFactory(Context context) {
        this.context = context;
        this.dataManager = DataManager.getInstance(context);
        this.courses = new HashSet<>();
        updateCourseList();

    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
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

        return views;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
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
        updateCourseList();
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        WidgetProvider.width = Util.dp2px(110);
    }

    private void updateCourseList() {
        try {
            dataManager.getModels(Course.TYPE, Course.class, (DataManager.DocumentParser<List<Course>>) courses::addAll);

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
            weekView.setDateTimeInterpreter(new WeekView.DateTimeInterpreter() {
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
            weekView.setNumberOfVisibleDays(1);
            weekView.goToToday();
            weekView.setDayBackgroundColor(resources.getColor(R.color.calendar_day_background_color));
            weekView.setEventTextColor(resources.getColor(android.R.color.white));
            weekView.setHeaderRowBackgroundColor(resources.getColor(R.color.color_accent));
            weekView.setHeaderColumnBackgroundColor(resources.getColor(R.color.color_accent));
            weekView.setTodayBackgroundColor(resources.getColor(R.color.calendar_today_background_color));
            weekView.setDrawingCacheEnabled(true);
        }
        Settings settings = Settings.getInstance();
        weekView.setLimitTime(settings.getMinHour(), settings.getMaxHour());
        weekView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        weekView.layout(0, 0, WidgetProvider.width, (weekView.getmMaxTime() - weekView.getmMinTime()) * Util.dp2px(50));
    }
}
