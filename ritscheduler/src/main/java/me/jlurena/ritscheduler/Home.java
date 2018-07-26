package me.jlurena.ritscheduler;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import me.jlurena.ritscheduler.models.Course;
import me.jlurena.ritscheduler.models.Term;


public class Home extends Activity {

    private BoomMenuButton mBoomMenuButton;
    private WeekView mWeekView;
    private Spinner mTermSpinner;
    private EditText mSearchCourse;
    private List<Course> courses;
    private Term selectedTerm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initBoomButton();
        initCalendar();
    }

    private void initSearchCourseEditText() {
        mSearchCourse = findViewById(R.id.search_course);
        mSearchCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchCourse.setError(null);
            }
        });
    }

    private void initTermSpinner() {
        mTermSpinner = findViewById(R.id.term_spinner);
        Term term = Term.currentTerm();
        // Generate current and next two terms
        Term[] terms = {term, term.nextSemester(), term.plusSemesters(2)};
        ArrayAdapter<Term> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, terms);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTermSpinner.setAdapter(spinnerAdapter);

        mTermSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedTerm = (Term) mTermSpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedTerm = (Term) mTermSpinner.getItemAtPosition(0);
            }
        });
    }

    private void initBoomButton() {
        mBoomMenuButton = findViewById(R.id.boom_menu_button);

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
                String query = mSearchCourse.getText().toString();
                if (query.isEmpty()) {
                    mSearchCourse.setError("Search field cannot be empty");
                } else {
                    // TODO launch new activity and perform search
                }
            }

            @Override
            public void onBoomDidHide() {
                mSearchCourse.getText().clear();
                mSearchCourse.setError(null);
            }

            @Override
            public void onBoomDidShow() {
                // Setup View inside of boom
                initTermSpinner();
                initSearchCourseEditText();

            }
        });
    }

    private void initCalendar() {
        mWeekView = findViewById(R.id.weekView);

        mWeekView.setWeekViewLoader(new WeekView.WeekViewLoader() {

            @Override
            public List<? extends WeekViewEvent> onWeekViewLoad() {
                // TODO load saved events in db here
                return new ArrayList<>();
            }
        });
    }
}
