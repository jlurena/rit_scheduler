package me.jlurena.ritscheduler.fragments;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v14.preference.PreferenceDialogFragment;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.Preference;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nightonke.boommenu.Util;

import me.jlurena.ritscheduler.R;
import me.jlurena.ritscheduler.models.Settings;
import me.jlurena.ritscheduler.utils.Utils;
import me.jlurena.ritscheduler.widgets.NumberPreference;
import me.jlurena.ritscheduler.widgets.TimePreference;

/**
 * Settings fragment used to create settings_preference View.
 */
public class SettingsFragment extends PreferenceFragment {
    public static final String TAG = "PREFERENCE_FRAGMENT";
    public static final String ARG_1 = "context";

    public static Settings updateSettings(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean autoLimitTime = prefs.getBoolean(context.getString(R.string.auto_limit_time_key), false);
        int numVisibleDays = prefs.getInt(context.getString(R.string.num_visible_days_key), 3);
        String timeRange = prefs.getString(context.getString(R.string.hour_range_key), "0-1380");
        boolean hasFirstVisibleDay = prefs.getBoolean(context.getString(R.string.set_first_visible_day_flag_key), false);
        int firstVisibleDay = Integer.parseInt(prefs.getString(context.getString(R.string.first_visible_day_key), "0")); // Doesn't matter

        return Settings.getInstance().setAutoLimitTime(autoLimitTime)
                .setNumberOfVisibleDays(numVisibleDays)
                .setTimeRange(timeRange)
                .setFirstVisibleDayFlag(hasFirstVisibleDay)
                .setFirstVisibleDay(firstVisibleDay);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        updateSettings(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                updateSettings(getActivity());
            }
        });

    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            layoutParams.height = Util.dp2px(400);
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.CENTER;
            view.setLayoutParams(layoutParams);
        }
        return view;
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {

        PreferenceDialogFragment dialogFragment = null;
        String key = preference.getKey();
        if (preference instanceof TimePreference) {
            dialogFragment = TimePreference.TimePreferenceDialogFragment.newInstance(key);
        } else if (preference instanceof NumberPreference) {
            dialogFragment = NumberPreference.NumberPreferenceDialogFragment.newInstance(preference.getKey());
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getFragmentManager(), TAG);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }

    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();

        if (key.equals(getString(R.string.technical_support_key))) {
            Intent intent = Utils.emailIntent(new String[]{"eljean@live.com"}, "Technical Support for RITScheduler");
            startActivity(intent);
            return true;
        } else if (key.equals(getString(R.string.rate_RITScheduler_key))) {
            final Intent goToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName()));
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity()
                        .getPackageName())));
            }
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }
}
