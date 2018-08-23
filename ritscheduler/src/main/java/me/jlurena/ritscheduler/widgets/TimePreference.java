package me.jlurena.ritscheduler.widgets;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v14.preference.PreferenceDialogFragment;
import android.support.v7.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import me.jlurena.ritscheduler.R;
import me.jlurena.ritscheduler.utils.Utils;

public class TimePreference extends DialogPreference {

    /**
     * Time start in minutes after midnight.
     */
    private int timeStart;
    /**
     * Time end in minutes after midnight.
     */
    private int timeEnd;
    private String timeRange;

    public TimePreference(Context context) {
        this(context, null);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.preferenceStyle);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @SuppressWarnings("SameReturnValue")
    @Override
    public int getDialogLayoutResource() {
        return R.layout.preference_hour_range_dialog;
    }

    /**
     * Get hour end.
     *
     * @return Hour of day.
     */
    public int getEndHour() {
        return timeEnd / 60;
    }

    /**
     * Get minute end.
     *
     * @return Minutes after midnight.
     */
    public int getEndMinute() {
        return timeEnd % 60;
    }

    /**
     * Get start hour.
     *
     * @return Hour of day.
     */
    public int getStartHour() {
        return timeStart / 60;
    }

    /**
     * Get start minute.
     *
     * @return Minute after midnight.
     */
    public int getStartMinute() {
        return timeStart % 60;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        String[] split = timeRange.split("-");
        this.timeStart = Integer.parseInt(split[0]);
        this.timeEnd = Integer.parseInt(split[1]);
        this.timeRange = timeRange;
        persistString(timeRange);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setTimeRange(restorePersistedValue ? getPersistedString(timeRange) : (String) defaultValue);
    }

    public void setTimeRange(int timeStart, int timeEnd) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.timeRange = String.valueOf(timeStart) + '-' + timeEnd;
        persistString(timeRange);
    }

    public static class TimePreferenceDialogFragment extends PreferenceDialogFragment {

        private TimePicker mTimePicker;
        private TabLayout mTabLayout;
        private boolean isStartTimePickerSelected = true;
        private int startHour = 0;
        private int startMinute = 0;
        private int endHour = 23;
        private int endMinute = 0;
        private TimePreference timePreference;

        public static TimePreferenceDialogFragment newInstance(String key) {
            final TimePreferenceDialogFragment
                    fragment = new TimePreferenceDialogFragment();
            final Bundle b = new Bundle(1);
            b.putString(ARG_KEY, key);
            fragment.setArguments(b);

            return fragment;
        }

        private void initTabLayout() {
            this.mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override public void onTabReselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    int hour, minute;
                    if (tab.getPosition() == 0) {
                        isStartTimePickerSelected = true;
                        hour = timePreference.getStartHour();
                        minute = timePreference.getStartMinute();
                    } else {
                        isStartTimePickerSelected = false;
                        hour = timePreference.getEndHour();
                        minute = timePreference.getEndMinute();
                    }

                    if (Build.VERSION.SDK_INT > 22) {
                        mTimePicker.setHour(hour);
                        mTimePicker.setMinute(minute);
                    } else {
                        mTimePicker.setCurrentHour(hour);
                        mTimePicker.setCurrentMinute(minute);
                    }

                }

                @Override public void onTabUnselected(TabLayout.Tab tab) {

                }
            });
        }

        @Override
        protected void onBindDialogView(View view) {
            super.onBindDialogView(view);

            this.mTimePicker = view.findViewById(R.id.timepicker);
            this.mTimePicker.setOnTimeChangedListener((view1, hourOfDay, minute) -> {
                if (isStartTimePickerSelected) {
                    startHour = hourOfDay;
                    startMinute = minute;
                } else {
                    endHour = hourOfDay;
                    endMinute = minute;
                }
            });

            this.mTabLayout = view.findViewById(R.id.tabs);
            initTabLayout();

            DialogPreference preference = getPreference();
            if (preference instanceof TimePreference) {
                timePreference = ((TimePreference) preference);
                int startHour = timePreference.getStartHour();
                int startMinute = timePreference.getStartMinute();
                int endHour = timePreference.getEndHour();
                int endMinute = timePreference.getEndMinute();
                boolean is24hour = DateFormat.is24HourFormat(getActivity());

                mTimePicker.setIs24HourView(is24hour);
                if (Build.VERSION.SDK_INT > 22) {
                    mTimePicker.setHour(startHour);
                    mTimePicker.setMinute(startMinute);
                } else {
                    mTimePicker.setCurrentHour(endHour);
                    mTimePicker.setCurrentMinute(endMinute);
                }
            }
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                if (startHour >= endHour) {
                    Utils.alertDialogFactory(getActivity(), R.string.error, getString(R.string.time_range_error)).show();
                    return;
                }
            }
            super.onClick(dialog, which);
        }

        @Override
        public void onDialogClosed(boolean positiveResult) {
            if (positiveResult) {
                // Get the current values from the TimePicker
                int startTime = (startHour * 60) + startMinute;
                int endTime = (endHour * 60) + endMinute;
                String timeRange = String.valueOf(startTime) + "-" + endTime;

                // Save the value
                DialogPreference preference = getPreference();
                if (preference instanceof TimePreference) {
                    TimePreference timePreference = ((TimePreference) preference);
                    if (timePreference.callChangeListener(timeRange)) {
                        timePreference.setTimeRange(timeRange);
                    }
                }
            }
        }
    }
}
