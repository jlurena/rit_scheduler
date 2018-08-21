package me.jlurena.ritscheduler.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v14.preference.PreferenceDialogFragment;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

import com.github.stephenvinouze.materialnumberpickercore.MaterialNumberPicker;

import me.jlurena.ritscheduler.R;

public class NumberPreference extends DialogPreference {
    private int number;

    public NumberPreference(Context context) {
        this(context, null);
    }

    public NumberPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.preferenceStyle);
    }

    public NumberPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public NumberPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.number = 3;
    }

    @Override public int getDialogLayoutResource() {
        return R.layout.preference_number_picker;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
        persistInt(number);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setNumber(restorePersistedValue ? getPersistedInt(number) : (int) defaultValue);
    }

    public static class NumberPreferenceDialogFragment extends PreferenceDialogFragment {
        private MaterialNumberPicker numberPicker;
        private NumberPreference numberPreference;
        private int number;

        public static NumberPreference.NumberPreferenceDialogFragment newInstance(String key) {
            final NumberPreference.NumberPreferenceDialogFragment
                    fragment = new NumberPreference.NumberPreferenceDialogFragment();
            final Bundle b = new Bundle(1);
            b.putString(ARG_KEY, key);
            fragment.setArguments(b);

            return fragment;
        }

        protected void onBindDialogView(View view) {
            super.onBindDialogView(view);

            numberPicker = view.findViewById(R.id.number_picker);
            numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    number = newVal;
                }
            });

            DialogPreference preference = getPreference();
            if (preference instanceof NumberPreference) {
                this.numberPreference = (NumberPreference) preference;
                numberPicker.setValue(numberPreference.getNumber());
            }
        }

        @Override
        public void onDialogClosed(boolean positiveResult) {
            if (positiveResult) {
                DialogPreference preference = getPreference();
                if (preference instanceof NumberPreference) {
                    NumberPreference numberPreference = ((NumberPreference) preference);
                    if (numberPreference.callChangeListener(number)) {
                        numberPreference.setNumber(number);
                    }
                }
            }
        }
    }
}
