package me.jlurena.ritscheduler.models;

import android.support.annotation.ColorInt;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import me.jlurena.revolvingweekview.WeekViewEvent;

/**
 * models.Course.java
 * Represents a course.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Course extends Model {

    @JsonIgnore
    public static final String TYPE = "course";
    /**
     * Color assigned by user for UI.
     */
    private @ColorInt int color;
    private String subject;
    private String startingTerm;
    private String courseTitleLong;
    private String catalogNumber;
    private String section;
    @JsonAlias("model_id")
    private String courseId;
    private Meeting meetings;

    public Course() {
        super(TYPE);
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public void setCatalogNumber(String catalogNumber) {
        this.catalogNumber = catalogNumber;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
        super.setModelId(courseId);
    }

    public String getCourseTitleLong() {
        return courseTitleLong;
    }

    public void setCourseTitleLong(String courseTitleLong) {
        this.courseTitleLong = courseTitleLong;
    }

    public Meeting getMeetings() {
        return meetings;
    }

    public void setMeetings(Meeting meetings) {
        this.meetings = meetings;
    }

    /**
     * Get qualified name of Course.
     *
     * @return The qualified name of this Course in the format of [subject]-[section]. Eg: CSCI-250.
     */
    @JsonIgnore
    public String getQualifiedName() {
        return String.format(Locale.getDefault(), "%s %s-%s", this.subject, this.catalogNumber, this.section);
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getStartingTerm() {
        return startingTerm;
    }

    public void setStartingTerm(String startingTerm) {
        this.startingTerm = startingTerm;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Course course = (Course) o;

        return this.getModelId().equals(course.getModelId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getModelId());
    }

    @JsonIgnore
    @Override
    public Map<String, Object> toMap() {
        //noinspection unchecked
        return new ObjectMapper().convertValue(this, Map.class);
    }

    /**
     * Creates a List of WeekViewEvents based on this Course's properties.
     *
     * @return A list of WeekViewEvents.
     */
    public List<WeekViewEvent> toWeekViewEvents() {
        List<WeekViewEvent> events = meetings.toWeekViewEvents(); // Get WeekViewEvents from meetings.

        for (WeekViewEvent event : events) {
            event.setIdentifier(this.courseId);
            event.setColor(this.color);
            event.setName(getQualifiedName());
        }
        return events;
    }
}
