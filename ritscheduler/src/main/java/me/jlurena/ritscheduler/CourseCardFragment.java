package me.jlurena.ritscheduler;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nightonke.boommenu.Util;
import com.rey.material.widget.FloatingActionButton;
import com.rey.material.widget.TextView;
import com.rtugeek.android.colorseekbar.ColorSeekBar;

import java.io.IOException;

import me.jlurena.ritscheduler.models.Course;
import me.jlurena.ritscheduler.models.Meeting;

public class CourseCardFragment extends Fragment {
    private static final String ARG_PARAM1 = "course";
    private static final ObjectMapper mapper = new ObjectMapper();
    private final int DAYS_COL = 1;
    private final int TIMES_COL = 2;
    /**
     * Keep track of the current row of Day and Time textview
     */
    private int currentDayTimesRow = 0;
    private Course course;
    private FloatingActionButton mAddCourse;
    private TextView mCourseSection;
    private TextView mCourseName;
    private TextView mCourseTerm;
    private GridLayout mCourseDetailsLayout;
    private ConstraintLayout mCourseHeader;
    private ImageView mProfessorIcon;
    private ImageView mCalendarIcon;
    private ImageView mLocationIcon;
    private OnAddCourseClickListener onAddCourseClickListener;
    private TextView mProfessorsName;
    private ColorSeekBar mColorSlider;
    private Integer currentColor;

    /**
     * Factory method to create an instance of CardFragment.
     *
     * @param course The course object.
     * @return A new instance of fragment CourseCardFragment.
     */
    public static CourseCardFragment newInstance(Course course) {
        CourseCardFragment fragment = new CourseCardFragment();
        Bundle args = new Bundle();
        try {
            args.putString(ARG_PARAM1, mapper.writeValueAsString(course));
            fragment.setArguments(args);
        } catch (JsonProcessingException e) {
            // TODO some error
        }
        return fragment;
    }

    private void initColorSeekbar() {
        final Drawable headerDrawable = DrawableCompat.wrap(this.mCourseHeader.getBackground());
        this.mColorSlider.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                currentColor = color;
                DrawableCompat.setTint(headerDrawable, color);
                mCourseHeader.setBackground(headerDrawable);
            }
        });

        this.mColorSlider.setOnInitDoneListener(new ColorSeekBar.OnInitDoneListener() {
            @Override
            public void done() {
                if (currentColor != null) {
                    DrawableCompat.setTint(headerDrawable, currentColor);
                    mCourseHeader.setBackground(headerDrawable);
                } else {
                    mColorSlider.setColorBarPosition(mColorSlider.getColorIndexPosition(getActivity().getResources().getColor(R.color.color_primary)));
                }


            }
        });

    }

    private void initCourseCard() {
        this.mAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onAddCourseClickListener != null) {
                    onAddCourseClickListener.onAddCourse();
                    mAddCourse.setLineMorphingState((mAddCourse.getLineMorphingState() + 1) % 2, true);
                }
            }
        });

        this.mCourseName.setText(this.course.getCourseTitleLong());
        this.mCourseSection.setText(this.course.getQualifiedName());
        this.mCourseTerm.setText(this.course.getStartingTerm());
        initCourseCardDetails();
        initColorSeekbar();

    }

    private TextView createDayTimeTextView(String text, int row, int col) {
        TextView textView = new TextView(getActivity());
        GridLayout.LayoutParams gllp = new GridLayout.LayoutParams();

        gllp.rowSpec = GridLayout.spec(row);
        gllp.columnSpec = GridLayout.spec(col);
        gllp.setMarginStart(Util.dp2px(20));
        textView.setLayoutParams(gllp);

        textView.setText(text);
        textView.setTextColor(getResources().getColor(R.color.dark_gray));
        textView.setTextSize(12);

        return textView;
    }

    private void initCourseCardDetails() {
        Meeting meetings = this.course.getMeetings();
        String days[] = meetings.getDays();
        String times[] = meetings.getTimes();
        int length = meetings.getDates().length;
        // Set professor name, set icon position
        String professors = meetings.isSameInstructor() ? meetings.getInstructors()[0] : TextUtils.join(", ", meetings.getInstructors());
        GridLayout.LayoutParams glParams = (GridLayout.LayoutParams) this.mProfessorIcon.getLayoutParams();
        glParams.rowSpec = GridLayout.spec(length+1);
        this.mProfessorIcon.setLayoutParams(glParams);

        glParams = (GridLayout.LayoutParams) this.mProfessorsName.getLayoutParams();
        glParams.rowSpec = GridLayout.spec(length+1);
        this.mProfessorsName.setLayoutParams(glParams);
        this.mProfessorsName.setText(professors);

        // Set icon span of course meetings icons
        glParams = (GridLayout.LayoutParams) this.mCalendarIcon.getLayoutParams();
        glParams.rowSpec = GridLayout.spec(0, length);
        this.mCalendarIcon.setLayoutParams(glParams);

        // Set icon span of course location icon
        glParams = (GridLayout.LayoutParams) this.mLocationIcon.getLayoutParams();
        glParams.rowSpec = GridLayout.spec(2 + length, 2 + length);
        this.mLocationIcon.setLayoutParams(glParams);


        // Set days and times
        for (int i = 0; i < length; i++) {
            this.mCourseDetailsLayout.addView(createDayTimeTextView(days[i], i, DAYS_COL));
            this.mCourseDetailsLayout.addView(createDayTimeTextView(times[i], i, TIMES_COL));
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                this.course = mapper.readValue(getArguments().getString(ARG_PARAM1), Course.class);
            } catch (IOException e) {
                // TODO some error handling
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_card, container, false);
        try {
            if (savedInstanceState != null) {
                this.course = mapper.readValue(savedInstanceState.getString(ARG_PARAM1), Course.class);
            }
        } catch (IOException e) {
            // TODO some error handling
        }

        this.mAddCourse = view.findViewById(R.id.course_add_fab);
        this.mCourseName = view.findViewById(R.id.course_name_tv);
        this.mCourseSection = view.findViewById(R.id.course_section_tv);
        this.mCourseTerm = view.findViewById(R.id.course_term_tv);
        this.mCourseDetailsLayout = view.findViewById(R.id.course_details_gl);
        this.mCourseHeader = view.findViewById(R.id.course_header_container);
        this.mProfessorIcon = view.findViewById(R.id.course_professor_icon);
        this.mProfessorsName = view.findViewById(R.id.course_professors_name);
        this.mCalendarIcon = view.findViewById(R.id.course_calendar_icon);
        this.mLocationIcon = view.findViewById(R.id.course_location_icon);
        this.mColorSlider = view.findViewById(R.id.colorSlider);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initCourseCard();
    }

    /**
     * Sets onAddCourseClickListener for Add button.
     * @param onAddCourseClickListener The Add click listener.
     */
    public void setOnAddCourseClickListener(OnAddCourseClickListener onAddCourseClickListener) {
        this.onAddCourseClickListener = onAddCourseClickListener;
    }

    public interface OnAddCourseClickListener {
        void onAddCourse();
    }
}
