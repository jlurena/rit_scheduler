package me.jlurena.ritscheduler;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.OnBoomListenerAdapter;
import com.nightonke.boommenu.Util;

import java.util.ArrayList;
import java.util.List;

import me.jlurena.revolvingweekview.WeekView;
import me.jlurena.revolvingweekview.WeekViewEvent;
import me.jlurena.ritscheduler.models.Term;


public class Home extends Activity {

    private BoomMenuButton mBoomMenuButton;
    private WeekView mWeekView;
    private Spinner mTermSpinner;
    private EditText mSearchCourse;
    private Term term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initBoomButton();
        initCalendar();


    }

    private void initSearchCourse() {
    }

    private void initTermSpinner() {
        mTermSpinner = findViewById(R.id.term_spinner);
        term = Term.currentTerm();
        // Generate current and next two terms
        Term[] terms = { term, term.nextSemester(), term.plusSemesters(2)};
        ArrayAdapter<Term> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, terms);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTermSpinner.setAdapter(spinnerAdapter);
    }

    private void initBoomButton() {
        mBoomMenuButton = findViewById(R.id.boom_menu_button);
        final Context context = this.getBaseContext();

        mBoomMenuButton.setButtonEnum(ButtonEnum.Ham);

        mBoomMenuButton.addBuilder(new HamButton.Builder()
                .normalImageRes(android.R.drawable.ic_menu_search)
                .addView(R.layout.search_dialogue)
                .normalColorRes(R.color.dark_gray)
                .highlightedColorRes(R.color.color_accent)
                .buttonHeight(Util.dp2px(80)));
        mBoomMenuButton.setOnBoomListener(new OnBoomListenerAdapter() {

            @Override
            public void onClicked(int index, BoomButton boomButton) {
                // When clicked
            }

            @Override
            public void onBoomDidHide() {
                // When finished hiding
            }

            @Override
            public void onBoomDidShow() {
                // Setup View inside of boom
                initTermSpinner();

            }
        });
    }

    private void initCalendar() {
        mWeekView = findViewById(R.id.weekView);

        mWeekView.setWeekViewLoader(new WeekView.WeekViewLoader() {

            @Override
            public List<? extends WeekViewEvent> onWeekViewLoad() {
                return new ArrayList<>();
            }
        });
    }
}
