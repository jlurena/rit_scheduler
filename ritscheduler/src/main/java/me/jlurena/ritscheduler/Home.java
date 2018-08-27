package me.jlurena.ritscheduler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.VolleyError;
import com.couchbase.lite.CouchbaseLiteException;
import com.kobakei.ratethisapp.RateThisApp;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.OnBoomListenerAdapter;
import com.nightonke.boommenu.Util;
import com.qhutch.elevationimageview.ElevationImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;
import me.jlurena.revolvingweekview.WeekView;
import me.jlurena.revolvingweekview.WeekViewEvent;
import me.jlurena.ritscheduler.database.DataManager;
import me.jlurena.ritscheduler.fragments.CourseCardFragment;
import me.jlurena.ritscheduler.fragments.SettingsFragment;
import me.jlurena.ritscheduler.models.Course;
import me.jlurena.ritscheduler.models.Settings;
import me.jlurena.ritscheduler.models.Term;
import me.jlurena.ritscheduler.networking.NetworkManager;
import me.jlurena.ritscheduler.networking.ResponseListener;
import me.jlurena.ritscheduler.utils.SettingsManager;
import me.jlurena.ritscheduler.utils.Utils;


public class Home extends Activity implements CourseCardFragment.ButtonsListeners {

    private BoomMenuButton mBoomMenuButton;
    private WeekView mWeekView;
    private ViewGroup mHomeMainContainer;
    private Spinner mTermSpinner;
    private AutoCompleteTextView mSearchCourse;
    private Term selectedTerm;
    private NetworkManager networkManager;
    private boolean isFragmentInflated = false;
    private DataManager dataManager;
    private HashSet<Course> courses;
    private SettingsFragment settingsFragment;
    private ConstraintLayout mFragmentOuterContainer;
    private List<Course> queryResult;
    private ElevationImageView mNext;
    private ElevationImageView mPrev;
    private int currentCoursePosition;
    private boolean isDimmed = false;
    private ArrayAdapter<String> autoCompleteAdapter;

    @Override
    public void addCourseButton(Course course) {
        try {

            if (course.getMeetings().isOnline() || course.getMeetings().isTimeTBA()) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.action_not_supported)
                        .setMessage("Only in person lectures and classes with determined meeting times are supported.")
                        .setNeutralButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                        .show();
                removeFragment(course.getModelId());
                return;
            }
            dataManager.addModel(course);
            courses.add(course);
            mWeekView.notifyDatasetChanged();
            removeFragment(course.getModelId());
        } catch (CouchbaseLiteException e) {
            Utils.alertDialogFactory(this, R.string.error, getString(R.string.save_error)).show();
        } finally {
            enableBackground();
            queryResult.clear();
        }
    }

    @Override
    public void deleteCourseButton(Course course) {
        try {
            dataManager.deleteModel(course);
            courses.remove(course);
            mWeekView.notifyDatasetChanged();
            removeFragment(course.getModelId());
        } catch (CouchbaseLiteException e) {
            Utils.alertDialogFactory(this, R.string.error, getString(R.string.delete_course_error)).show();
        } finally {
            enableBackground();
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
                .addView(R.layout.view_search_dialogue)
                .normalColorRes(R.color.dark_gray)
                .highlightedColorRes(R.color.color_accent)
                .buttonHeight(Util.dp2px(110)));

        mBoomMenuButton.setOnBoomListener(new OnBoomListenerAdapter() {

            @Override
            public void onBoomDidHide() {
                mSearchCourse.getText().clear();
                mSearchCourse.setError(null);

                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(mSearchCourse.getWindowToken(), 0);
                }

                if (queryResult != null && !queryResult.isEmpty()) {
                    showCourseCard(queryResult.get(0), false);
                    if (queryResult.size() > 1) {
                        mNext.setVisibility(View.VISIBLE);
                    }
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
                final ImageView image = boomButton.getImageView();
                final Animation rotateAnimation = AnimationUtils.loadAnimation(Home.this, R.anim.rotate);
                String query = mSearchCourse.getText().toString();
                final Animation swinging = AnimationUtils.loadAnimation(Home.this, R.anim.swing_wiggle);

                if (query.isEmpty()) {
                    image.startAnimation(swinging);
                    mSearchCourse.setError("Search field cannot be empty");
                } else {
                    // Start animation
                    image.startAnimation(rotateAnimation);
                    // Start call
                    networkManager.queryCourses(query, selectedTerm.getTermCode(), new ResponseListener<List<Course>>() {

                        @Override
                        public void getResult(List<Course> courses, int errorCode, VolleyError error) {
                            if (errorCode == 200) {
                                // Display error if size is not 1
                                if (courses.isEmpty()) {
                                    Utils.alertDialogFactory(Home.this, R.string.error, null).setMessage(R.string.no_results_error).show();
                                    queryResult.clear();
                                } else {
                                    queryResult = courses;
                                    currentCoursePosition = 0;
                                    isFragmentInflated = true;
                                    mBoomMenuButton.reboom();
                                }
                            } else {
                                AlertDialog.Builder dialog = Utils.alertDialogFactory(Home.this, R.string.error, null);

                                if (error != null) {
                                    dialog.setMessage(error.getMessage()).show();
                                } else {
                                    dialog.setMessage(R.string.generic_error).show();
                                }
                                queryResult.clear();
                            }
                            image.clearAnimation();
                        }

                        @Override
                        public void onRequestFinished() {
                            image.clearAnimation();
                        }
                    });
                }
            }
        });
    }

    private void initCalendar() {
        this.mWeekView = findViewById(R.id.weekView);
        initCalendarSettings();

        try {
            dataManager.getModels(Course.TYPE, Course.class, (DataManager.DocumentParser<List<Course>>) model -> courses.addAll(model));

        } catch (CouchbaseLiteException e) {
            Utils.alertDialogFactory(this, R.string.error, getString(R.string.error_retrieving_saved_courses));
        }

        this.mWeekView.setWeekViewLoader(() -> {
            List<WeekViewEvent> events = new ArrayList<>();

            if (courses != null && !courses.isEmpty()) {
                for (Course course : courses) {
                    events.addAll(course.toWeekViewEvents());
                }
            }

            return events;
        });

        this.mWeekView.setOnEventClickListener((event, eventRect) -> {
            for (Course course : courses) {
                if (course.getCourseId().equals(event.getIdentifier())) {
                    showCourseCard(course, true);
                }
            }
        });

        this.mWeekView.setEmptyViewLongPressListener(time -> {
            isFragmentInflated = true;
            showSettings();
        });
    }

    private void initCalendarSettings() {
        Settings settings = SettingsManager.getInstance(this).getSettings();
        int firstVisibleDay = settings.getFirstVisibleDay();
        if (firstVisibleDay == 0) {
            this.mWeekView.goToToday();
        } else {
            this.mWeekView.setFirstDayOfWeek(firstVisibleDay);
            this.mWeekView.goToDay(firstVisibleDay);
        }

        this.mWeekView.setNumberOfVisibleDays(settings.getNumberOfVisibleDays());
        this.mWeekView.setLimitTime(settings.getMinHour(), settings.getMaxHour() + 1); // Let max hour be visible
        this.mWeekView.setAutoLimitTime(settings.isAutoLimitTime());

    }

    private void initNextPrevButtons() {
        this.mNext.setOnClickListener(v -> {
            if (isFragmentInflated && queryResult != null && !queryResult.isEmpty()) {
                if (queryResult.size() - 1 > currentCoursePosition) {
                    showCourseCard(queryResult.get(++currentCoursePosition), false);
                    mPrev.setVisibility(View.VISIBLE);
                }
                if (currentCoursePosition >= queryResult.size() - 1) {
                    mNext.setVisibility(View.GONE);
                    mPrev.setVisibility(View.VISIBLE);
                }
            }
        });

        this.mPrev.setOnClickListener(v -> {
            if (isFragmentInflated && queryResult != null && !queryResult.isEmpty()) {
                if (currentCoursePosition > 0) {
                    showCourseCard(queryResult.get(--currentCoursePosition), false);
                    mNext.setVisibility(View.VISIBLE);
                }
                if (currentCoursePosition == 0) {
                    mPrev.setVisibility(View.GONE);
                    mNext.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initOnFirstLaunch() {

        // Last
        final MaterialIntroView.Builder searchButtonIntro = new MaterialIntroView.Builder(this)
                .setFocusType(Focus.ALL)
                .setDelayMillis(100)
                .enableIcon(false)
                .setInfoText("Finally, click here to search!")
                .setShape(ShapeType.CIRCLE)
                .setUsageId("search_button")
                .enableFadeAnimation(true)
                .setIdempotent(true)
                .setListener(s -> mBoomMenuButton.reboom());

        // Fourth
        final MaterialIntroView.Builder termDropdownIntro = new MaterialIntroView.Builder(this)
                .setFocusType(Focus.ALL)
                .setDelayMillis(100)
                .setInfoText("Select the semester related to the search here.")
                .enableIcon(false)
                .setShape(ShapeType.RECTANGLE)
                .enableFadeAnimation(true)
                .setUsageId("term_dropdown")
                .setIdempotent(true)
                .setListener(s -> searchButtonIntro.show());

        // Third
        final MaterialIntroView.Builder searchIntro = new MaterialIntroView.Builder(this)
                .setFocusType(Focus.ALL)
                .setDelayMillis(100)
                .setInfoText("Enter a search term here. Please note, you'll only see the top 10 results, so be as precise as possible.")
                .enableIcon(false)
                .setShape(ShapeType.RECTANGLE)
                .enableFadeAnimation(true)
                .setUsageId("search_input")
                .setIdempotent(true)
                .setListener(s -> termDropdownIntro.show());

        // Second
        final MaterialIntroView.Builder bmbIntro = new MaterialIntroView.Builder(this)
                .setFocusType(Focus.ALL)
                .setDelayMillis(100)
                .setInfoText("Click here to open the Search box.")
                .setTarget(this.mBoomMenuButton)
                .enableIcon(false)
                .setShape(ShapeType.CIRCLE)
                .enableFadeAnimation(true)
                .setUsageId("bmb")
                .setIdempotent(true)
                .setListener(s -> {
                    mBoomMenuButton.boom();
                    final Handler handler = new Handler();
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (mBoomMenuButton.isBoomed()) {
                                // Set targets after boom
                                searchIntro.setTarget(mSearchCourse);
                                termDropdownIntro.setTarget(mTermSpinner);
                                searchButtonIntro.setTarget(mBoomMenuButton.getBoomButton(0).getImageView());
                                searchIntro.show();
                            } else {
                                handler.postDelayed(this, 500);
                            }
                        }
                    };
                    handler.postDelayed(runnable, 500);
                });

        // First
        new MaterialIntroView.Builder(this)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(500)
                .setInfoText("Long press on any empty area to open up the Settings menu.")
                .setTarget(this.mWeekView)
                .enableIcon(false)
                .enableFadeAnimation(true)
                .setShape(ShapeType.CIRCLE)
                .setUsageId("settings")
                .setIdempotent(true)
                .setListener(s -> bmbIntro.show())
                .show();

    }

    private void initSearchCourseEditText() {
        this.mSearchCourse = findViewById(R.id.search_course);
        this.mSearchCourse.setImeOptions(EditorInfo.IME_ACTION_DONE);
        this.mSearchCourse.setRawInputType(InputType.TYPE_CLASS_TEXT);
        this.mSearchCourse.setAdapter(autoCompleteAdapter);
        this.mSearchCourse.setOnClickListener(view -> mSearchCourse.setError(null));
        this.mSearchCourse.setOnEditorActionListener((v, id, e) -> {
            if (id == EditorInfo.IME_ACTION_DONE) {
                mBoomMenuButton.onButtonClick(0, mBoomMenuButton.getBoomButton(0));
                return true;
            } else {
                return false;
            }
        });
        this.mSearchCourse.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 4) {
                    networkManager.queryAutoComplete(s.toString(), new ResponseListener<List<String>>() {
                        @Override
                        public void getResult(List<String> terms, int errorCode, VolleyError error) {
                            autoCompleteAdapter.clear();
                            autoCompleteAdapter.addAll(terms);
                            mSearchCourse.setThreshold(4);
                            autoCompleteAdapter.getFilter().filter(s.toString(), null);
                        }

                        @Override
                        public void onRequestFinished() {
                        }
                    });
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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

        RateThisApp.Config rateConfig = new RateThisApp.Config();
        rateConfig.setTitle(R.string.rate_app_title);
        rateConfig.setMessage(R.string.rate_app_message);
        RateThisApp.init(rateConfig);
        RateThisApp.onCreate(this);
        RateThisApp.showRateDialogIfNeeded(this);

        this.courses = new HashSet<>();
        this.networkManager = NetworkManager.getInstance(this);
        this.dataManager = DataManager.getInstance(this);
        this.autoCompleteAdapter = new ArrayAdapter<>(this, R.layout.auto_complete_dropdown_item);

        this.mPrev = findViewById(R.id.previous);
        this.mNext = findViewById(R.id.next);
        this.mFragmentOuterContainer = findViewById(R.id.fragment_outer_container);
        this.mHomeMainContainer = findViewById(R.id.home_main_container);
        this.settingsFragment = new SettingsFragment();

        initBoomButton();
        initCalendar();
        initNextPrevButtons();
        initOnFirstLaunch();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isFragmentInflated) {
            Fragment fragment = getFragmentManager().findFragmentById(R.id.course_fragment_container);
            removeFragment(fragment.getTag());
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Distinguish between long press for Setting Preference Screen that appears on LongPress
        long eventDuration = event.getEventTime() - event.getDownTime();
        if (event.getAction() == MotionEvent.ACTION_UP && eventDuration < ViewConfiguration.getLongPressTimeout()) {

            if (isFragmentInflated) {
                Rect rect = new Rect(0, 0, 0, 0);

                this.mFragmentOuterContainer.getHitRect(rect);

                boolean intersects = rect.contains((int) event.getX(), (int) event.getY());

                if (!intersects) {
                    Fragment fragment = getFragmentManager().findFragmentById(R.id.course_fragment_container);
                    removeFragment(fragment.getTag());
                    return true;
                }
            }
        }

        return super.onTouchEvent(event);
    }

    private void removeFragment(String fragmentTag) {
        if (isFragmentInflated) {
            FragmentManager fm = getFragmentManager();
            Fragment fragment = fm.findFragmentByTag(fragmentTag);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (fragmentTag.equals(SettingsFragment.TAG)) {
                initCalendarSettings();
                ft.remove(settingsFragment);
            } else {
                if (fragment != null) {
                    ft.remove(fragment);
                    mNext.setVisibility(View.GONE);
                    mPrev.setVisibility(View.GONE);
                }
            }
            ft.commit();
            enableBackground();
            isFragmentInflated = false;
            isDimmed = false;
        }
    }

    private void showCourseCard(Course course, boolean isSavedCourse) {
        CourseCardFragment courseFrag = CourseCardFragment.newInstance(this, course, isSavedCourse);
        courseFrag.setButtonsListeners(this);
        if (!isDimmed) {
            disableBackground();
            isDimmed = true;
        }
        final FragmentManager fm = getFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.course_fragment_container, courseFrag, course.getModelId()).commit();
        isFragmentInflated = true;
    }

    private void showSettings() {
        if (isFragmentInflated) {
            disableBackground();
            final FragmentManager fm = getFragmentManager();
            final FragmentTransaction ft = fm.beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.replace(R.id.course_fragment_container, settingsFragment, SettingsFragment.TAG).commit();
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
            removeFragment(course.getModelId());
        }
    }
}
