package me.jlurena.ritscheduler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.android.volley.VolleyError;
import com.couchbase.lite.CouchbaseLiteException;
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
import me.jlurena.ritscheduler.database.DataManager;
import me.jlurena.ritscheduler.models.Course;
import me.jlurena.ritscheduler.models.Term;
import me.jlurena.ritscheduler.networking.NetworkManager;
import me.jlurena.ritscheduler.networking.ResponseListener;


public class Home extends Activity implements CourseCardFragment.OnAddCourseClickListener {

    private static final String COURSE_FRAG_TAG = "CourseFrag";
    private static final String courseRegex = "^[A-Za-z]{4}\\s\\d{3}([A-Za-z]{1})?-\\d{2}$";
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

    @Override
    public void addCourseListener(Course course) {
        try {
            dataManager.addModel(course);
        } catch (CouchbaseLiteException e) {
            new AlertDialog.Builder(Home.this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.save_error)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
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
                if (isCourseCardVisible) {
                    disableBackground();
                    final FragmentManager fm = getFragmentManager();
                    final FragmentTransaction ft = fm.beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.course_fragment_container, courseCardFragment,
                            COURSE_FRAG_TAG).commit();
                }
            }

            @Override
            public void onBoomDidShow() {
                // Setup View inside of boom
                initTermSpinner();
                initSearchCourseEditText();

            }

            @Override
            public void onClicked(int index, final BoomButton boomButton) {
                RotateAnimation rotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
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
                        networkManager.queryCourses(query, selectedTerm.getTermCode(), new ResponseListener<List<Course>>() {

                            @Override
                            public void getResult(List<Course> courses, int errorCode, VolleyError error) {
                                if (errorCode == 200) {

                                    // Display error if size is not 1
                                    if (courses.size() != 1) {
                                        AlertDialog.Builder dialog = Utils.errorDialogFactory(Home.this, null);

                                        if (courses.size() > 1) {
                                            dialog.setMessage(R.string.course_term_ambigious_error_msg);
                                        } else {
                                            dialog.setMessage(R.string.no_results_error);
                                        }

                                        dialog.show();

                                    } else {
                                        courseCardFragment = CourseCardFragment.newInstance(Home.this, courses.get(0));
                                        courseCardFragment.setOnAddCourseClickListener(Home.this);
                                        isCourseCardVisible = true;
                                        mBoomMenuButton.reboom();
                                    }
                                } else {
                                    AlertDialog.Builder dialog = Utils.errorDialogFactory(Home.this, null);

                                    if (error != null) {
                                        dialog.setMessage(error.getMessage()).show();
                                    } else {
                                        dialog.setMessage(R.string.generic_error).show();
                                    }
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
        });
    }

    private void initCalendar() {
        this.mWeekView = findViewById(R.id.weekView);

        mWeekView.setWeekViewLoader(new WeekView.WeekViewLoader() {

            @Override
            public List<? extends WeekViewEvent> onWeekViewLoad() {
                // TODO load saved events in db here
                return new ArrayList<>();
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
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    ft.remove(courseCardFragment).commit();
                    enableBackground();
                    isCourseCardVisible = false;
                    return true;
                }
            }
        }

        return super.onTouchEvent(event);
    }
}
