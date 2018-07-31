package me.jlurena.ritscheduler;

import android.app.Fragment;
import android.os.Bundle;
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

import java.io.IOException;

import me.jlurena.ritscheduler.models.Course;
import me.jlurena.ritscheduler.models.Meeting;

public class CourseCardFragment extends Fragment {
    private static final String ARG_PARAM1 = "course";
    private static final String ARG_PARAM2 = "term";
    private static final ObjectMapper mapper = new ObjectMapper();
    private String term;
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
    private ImageView mProfessorIcon;
    private OnAddCourseClickListener onAddCourseClickListener;
    private TextView mProfessorsName;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CourseCardFragment.
     */
    public static CourseCardFragment newInstance(Course course, String term) {
        CourseCardFragment fragment = new CourseCardFragment();
        Bundle args = new Bundle();
        try {
            args.putString(ARG_PARAM1, mapper.writeValueAsString(course));
            args.putString(ARG_PARAM2, term);
            fragment.setArguments(args);
        } catch (JsonProcessingException e) {
            // TODO some error
        }
        return fragment;
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
        this.mCourseTerm.setText(this.term);
        initCourseCardDetails();

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
        textView.setTextSize(Util.sp2Px(12, getActivity()));

        return textView;
    }

    private void initCourseCardDetails() {
        Meeting meetings = this.course.getMeetings();
        String days[] = meetings.getDays();
        String times[] = meetings.getTimes();
        int length = meetings.getDates().length;
        // Set professor name
        String professors = meetings.isSameInstructor() ? meetings.getInstructors()[0] : TextUtils.join(", ", meetings.getInstructors());
        GridLayout.LayoutParams glParams = (GridLayout.LayoutParams) this.mProfessorIcon.getLayoutParams();
        glParams.rowSpec = GridLayout.spec(length+1);
        this.mProfessorIcon.setLayoutParams(glParams);

        glParams = (GridLayout.LayoutParams) this.mProfessorsName.getLayoutParams();
        glParams.rowSpec = GridLayout.spec(length+1);
        this.mProfessorsName.setLayoutParams(glParams);
        this.mProfessorsName.setText(professors);

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
                this.course = mapper.readValue(savedInstanceState.getString(ARG_PARAM1), Course.class);
                this.term = savedInstanceState.getString(ARG_PARAM2);
            } catch (IOException e) {
                // TODO some error handling
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_card, container);
        try {
            if (savedInstanceState != null) {
                this.course = mapper.readValue(savedInstanceState.getString(ARG_PARAM1), Course.class);
                this.term = savedInstanceState.getString(ARG_PARAM2);
            }
        } catch (IOException e) {
            // TODO some error handling
        }

        this.mAddCourse = view.findViewById(R.id.course_add_fab);
        this.mCourseName = view.findViewById(R.id.course_name_tv);
        this.mCourseSection = view.findViewById(R.id.course_section_tv);
        this.mCourseTerm = view.findViewById(R.id.course_term_tv);
        this.mCourseDetailsLayout = view.findViewById(R.id.course_details_gl);
        this.mProfessorIcon = view.findViewById(R.id.course_professor_icon);
        this.mProfessorsName = view.findViewById(R.id.course_professors_name);

        initCourseCard();
        return view;
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
