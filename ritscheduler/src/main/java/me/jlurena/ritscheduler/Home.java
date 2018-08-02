package me.jlurena.ritscheduler;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.VolleyError;
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
import me.jlurena.ritscheduler.networking.NetworkManager;
import me.jlurena.ritscheduler.networking.ResponseListener;


public class Home extends Activity {

    private BoomMenuButton mBoomMenuButton;
    private WeekView mWeekView;
    private Spinner mTermSpinner;
    private EditText mSearchCourse;
    private Term selectedTerm;
    private NetworkManager networkManager;


    private static final String courseRegex = "^[A-Za-z]{4}\\s\\d{3}-\\d{2}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        networkManager = NetworkManager.getInstance(getApplicationContext());

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
        selectedTerm = term;

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
            public void onClicked(int index, final BoomButton boomButton) {
                RotateAnimation rotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(3000);
                rotateAnimation.setInterpolator(new LinearInterpolator());

                String query = mSearchCourse.getText().toString();
                final Animation swinging = AnimationUtils.loadAnimation(Home.this, R.anim.swinging);

                if (query.isEmpty()) {
                    boomButton.getImageView().startAnimation(swinging);
                    mSearchCourse.setError("Search field cannot be empty");
                } else {
                    if (query.matches(courseRegex)) {
                        // Start animation
                        boomButton.getImageView().setAnimation(rotateAnimation);
                        // Start call
                        networkManager.queryCourses(query, selectedTerm.getTermCode(),
                                new ResponseListener<List<Course>>() {

                                    @Override
                                    public void getResult(List<Course> courses, int errorCode, VolleyError error) {
                                        if (errorCode == 200) {
                                            if (courses.size() > 1) {
                                                // TODO some "search term too ambigous" error
                                            } else if (courses.size() < 1) {
                                                // TODO not found error
                                            } else {
                                                mBoomMenuButton.reboom();
                                                CourseCardFragment courseCardFragment = CourseCardFragment
                                                        .newInstance(courses.get(0));
                                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                                ft.replace(R.id.course_fragment_container, courseCardFragment).commit();


                                            }
                                        } else {

                                            // TODO some error dialog for bad response
                                        }
                                    }

                                    @Override
                                    public void onRequestFinished() {
                                        boomButton.getImageView().clearAnimation();
                                    }
                                });
                    } else {
                        boomButton.getImageView().startAnimation(swinging);
                        mSearchCourse.setError("Must match format \"CSCI 250-01\"");
                    }
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
