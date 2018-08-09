package me.jlurena.ritscheduler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.VolleyError;
import com.couchbase.lite.CouchbaseLiteException;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.OnBoomListenerAdapter;
import com.nightonke.boommenu.Util;

import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import me.jlurena.revolvingweekview.WeekView;
import me.jlurena.revolvingweekview.WeekViewEvent;
import me.jlurena.ritscheduler.database.DataManager;
import me.jlurena.ritscheduler.models.Course;
import me.jlurena.ritscheduler.models.Term;
import me.jlurena.ritscheduler.networking.NetworkManager;
import me.jlurena.ritscheduler.networking.ResponseListener;


public class Home extends Activity implements CourseCardFragment.ButtonsListeners {

    private static final String COURSE_FRAG_TAG = "CourseFrag";
    private static final String courseRegex = "^[A-Za-z]{4}\\s\\d{3}([A-Za-z])?-\\d{2}$";
    private BoomMenuButton mBoomMenuButton;
    private WeekView mWeekView;
    private ViewGroup mHomeMainContainer;
    private Spinner mTermSpinner;
    private EditText mSearchCourse;
    private CourseCardFragment courseCardFragment;
    private FrameLayout mFragmentContainer;
    private Term selectedTerm;
    private NetworkManager networkManager;
    private boolean isCourseCardVisible = false;
    private DataManager dataManager;
    private HashSet<Course> courses;

    private void refreshCalendar() {

    }

    @Override
    public void addCourseButton(Course course) {
        try {
            dataManager.addModel(course);
            courses.add(course);
            mWeekView.notifyDatasetChanged();
            if (course.getMeetings().getDays().length > 0 && course.getMeetings().getDayTimes().length > 0) {
                WeekViewEvent firstEvent = course.toWeekViewEvents().get(0);
                mWeekView.goToDate(firstEvent.getStartTime().getDay());
                mWeekView.goToHour(firstEvent.getStartTime().getHour());
            }
        } catch (CouchbaseLiteException e) {
            Utils.alertDialogFactory(this, R.string.error, getString(R.string.save_error)).show();
        } finally {
            removeCourseCardFragment();
        }
    }

    @Override
    public void deleteCourseButton(Course course) {
        try {
            dataManager.deleteModel(course);
            courses.remove(course);
            mWeekView.notifyDatasetChanged();
        } catch (CouchbaseLiteException e) {
            Utils.alertDialogFactory(this, R.string.error, getString(R.string.delete_course_error)).show();
        } finally {
            removeCourseCardFragment();
        }
    }

    @Override
    public void updateCourseButton(Course course) {
        try {
            dataManager.updateModel(course);
            courses.remove(course);
            courses.add(course);
            mWeekView.notifyDatasetChanged();
        } catch (CouchbaseLiteException e) {
            Utils.alertDialogFactory(this, R.string.error, getString(R.string.update_course_error)).show();
        } finally {
            removeCourseCardFragment();
        }
    }

    private void disableBackground() {
        // Dim and remove listeners from background
        mBoomMenuButton.setDraggable(false);
        mBoomMenuButton.setEnabled(false);
        Utils.applyDim(mHomeMainContainer, .5F);
    }

    private void enableBackground() {
        Utils.clearDim(mHomeMainContainer);
        mBoomMenuButton.setDraggable(true);
        mBoomMenuButton.setEnabled(true);
    }

    private void initBoomButton() {
        this.mBoomMenuButton = findViewById(R.id.boom_menu_button);

        mBoomMenuButton.setButtonEnum(ButtonEnum.Ham);

        mBoomMenuButton.addBuilder(new HamButton.Builder()
                .normalImageRes(android.R.drawable.ic_menu_search)
                .addView(R.layout.search_dialogue)
                .normalColorRes(R.color.dark_gray)
                .highlightedColorRes(R.color.color_accent)
                .buttonHeight(Util.dp2px(80)));

        mBoomMenuButton.setOnBoomListener(new OnBoomListenerAdapter() {

            @Override
            public void onBoomDidHide() {
                mSearchCourse.getText().clear();
                mSearchCourse.setError(null);
                showCourseCard();
            }

            @Override
            public void onBoomDidShow() {
                // Setup View inside of boom
                initTermSpinner();
                initSearchCourseEditText();

            }

            @Override
            public void onClicked(int index, final BoomButton boomButton) {
                final ImageView image = boomButton.getImageView();
                final Animation rotateAnimation = AnimationUtils.loadAnimation(Home.this, R.anim.rotate);
                String query = mSearchCourse.getText().toString();
                final Animation swinging = AnimationUtils.loadAnimation(Home.this, R.anim.swinging);

                if (query.isEmpty()) {
                    image.startAnimation(swinging);
                    mSearchCourse.setError("Search field cannot be empty");
                } else {
                    if (query.matches(courseRegex)) {
                        // Start animation
                        image.startAnimation(rotateAnimation);
                        // Start call
                        networkManager.queryCourses(query, selectedTerm.getTermCode(), new ResponseListener<List<Course>>() {

                            @Override
                            public void getResult(List<Course> courses, int errorCode, VolleyError error) {
                                if (errorCode == 200) {

                                    // Display error if size is not 1
                                    if (courses.size() != 1) {
                                        AlertDialog.Builder dialog = Utils.alertDialogFactory(Home.this, R.string.error, null);

                                        if (courses.size() > 1) {
                                            dialog.setMessage(R.string.course_term_ambigious_error_msg);
                                        } else {
                                            dialog.setMessage(R.string.no_results_error);
                                        }

                                        dialog.show();

                                    } else {
                                        courseCardFragment = CourseCardFragment.newInstance(Home.this, courses.get(0), false);
                                        courseCardFragment.setButtonsListeners(Home.this);
                                        isCourseCardVisible = true;
                                        mBoomMenuButton.reboom();
                                    }
                                } else {
                                    AlertDialog.Builder dialog = Utils.alertDialogFactory(Home.this, R.string.error, null);

                                    if (error != null) {
                                        dialog.setMessage(error.getMessage()).show();
                                    } else {
                                        dialog.setMessage(R.string.generic_error).show();
                                    }
                                }
                            }

                            @Override
                            public void onRequestFinished() {
                                image.clearAnimation();
                            }
                        });
                    } else {
                        image.startAnimation(swinging);
                        mSearchCourse.setError("Must match format \"CSCI 250-01\"");
                    }
                }
            }
        });
    }

    private void initCalendar() {
        this.mWeekView = findViewById(R.id.weekView);
        this.mWeekView.goToToday();
        this.mWeekView.goToHour(LocalTime.now().getHour() - 2);

        try {
            dataManager.getModels(Course.TYPE, Course.class, new DataManager.DocumentParser<List<Course>>() {
                @Override
                public void toModelCallback(List<Course> model) {
                    courses.addAll(model);
                }
            });

        } catch (CouchbaseLiteException e) {
            Utils.alertDialogFactory(this, R.string.error, getString(R.string.error_retrieving_saved_courses));
        }

        mWeekView.setWeekViewLoader(new WeekView.WeekViewLoader() {

            @Override
            public List<? extends WeekViewEvent> onWeekViewLoad() {
                List<WeekViewEvent> events = new ArrayList<>();

                if (courses != null && !courses.isEmpty()) {
                    for (Course course : courses) {
                        events.addAll(course.toWeekViewEvents());
                    }
                }

                return events;
            }
        });

        this.mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {
                for (Course course : courses) {
                    if (course.getCourseId().equals(event.getIdentifier())) {
                        courseCardFragment = CourseCardFragment.newInstance(Home.this, course, true);
                        courseCardFragment.setButtonsListeners(Home.this);
                        isCourseCardVisible = true;
                        showCourseCard();
                    }
                }
            }
        });
    }

    private void initSearchCourseEditText() {
        this.mSearchCourse = findViewById(R.id.search_course);
        mSearchCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchCourse.setError(null);
            }
        });
    }

    private void initTermSpinner() {
        this.mTermSpinner = findViewById(R.id.term_spinner);
        Term term = Term.currentTerm();
        // Generate current and next two terms
        Term[] terms = {term, term.nextSemester(), term.plusSemesters(2)};
        ArrayAdapter<Term> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, terms);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.mTermSpinner.setAdapter(spinnerAdapter);
        this.selectedTerm = term;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.courses = new HashSet<>();
        this.networkManager = NetworkManager.getInstance(this);
        this.dataManager = DataManager.getInstance(this);
        this.mHomeMainContainer = findViewById(R.id.home_main_container);
        this.mFragmentContainer = findViewById(R.id.course_fragment_container);

        initBoomButton();
        initCalendar();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {

            if (isCourseCardVisible && courseCardFragment != null) {
                Rect rect = new Rect(0, 0, 0, 0);

                mFragmentContainer.getHitRect(rect);

                boolean intersects = rect.contains((int) event.getX(), (int) event.getY());

                if (!intersects) {
                    removeCourseCardFragment();
                    return true;
                }
            }
        }

        return super.onTouchEvent(event);
    }

    private void removeCourseCardFragment() {
        if (isCourseCardVisible && courseCardFragment != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.remove(courseCardFragment).commit();
            enableBackground();
            isCourseCardVisible = false;
        }
    }

    private void showCourseCard() {
        if (isCourseCardVisible) {
            disableBackground();
            final FragmentManager fm = getFragmentManager();
            final FragmentTransaction ft = fm.beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.replace(R.id.course_fragment_container, courseCardFragment,
                    COURSE_FRAG_TAG).commit();
        }
    }
}
