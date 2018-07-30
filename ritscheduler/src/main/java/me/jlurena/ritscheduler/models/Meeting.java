package me.jlurena.ritscheduler.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.jlurena.revolvingweekview.DayTime;
import me.jlurena.revolvingweekview.WeekView;
import me.jlurena.revolvingweekview.WeekViewEvent;

/**
 * models.Meeting.java
 * Represents meeting times of a course.
 */

@SuppressWarnings("ConstantConditions")
public class Meeting {

    public static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:ma");
    private String[] days;
    /**
     * Days of meeting in an Array represented as full humanized day names.
     */
    private String[] daysFull;
    private String[] locations;
    private String[] locationsShort;
    /**
     * Times in an array represented as 10:00AM - 10:50AM
     */
    private String[] times;
    private String[] dates;
    @JsonProperty("daytimes")
    private String[] dayTimes;
    private String[] instructorEmails;
    private String[] instructors;

    public String[] getInstructorEmails() {
        return instructorEmails;
    }

    public void setInstructorEmails(String[] instructorEmails) {
        this.instructorEmails = instructorEmails;
    }

    public String[] getInstructors() {
        return instructors;
    }

    public void setInstructors(String[] instructors) {
        this.instructors = instructors;
    }

    public String[] getDays() {
        return days;
    }

    public void setDays(String[] days) {
        this.days = days;
    }

    public String[] getDaysFull() {
        return daysFull;
    }

    public void setDaysFull(String[] daysFull) {
        this.daysFull = daysFull;
    }

    public String[] getLocations() {
        return locations;
    }

    public void setLocations(String[] locations) {
        this.locations = locations;
    }

    public String[] getLocationsShort() {
        return locationsShort;
    }

    public void setLocationsShort(String[] locationsShort) {
        this.locationsShort = locationsShort;
    }

    public String[] getTimes() {
        return times;
    }

    public void setTimes(String[] times) {
        this.times = times;
    }

    public String[] getDates() {
        return dates;
    }

    public void setDates(String[] dates) {
        this.dates = dates;
    }

    public String[] getDayTimes() {
        return dayTimes;
    }

    public void setDayTimes(String[] dayTimes) {
        this.dayTimes = dayTimes;
    }

    /**
     * Creates a List of WeekViewEvents with start time and end times pertaining to this Meeting.
     * @return A list of WeekViewEvents <b>ONLY</b> with locations, start time and end times properties.
     */
    public List<WeekViewEvent> toWeekViewEvents() {


        int length = this.days.length;
        // Days length will always equal the number of times meeting takes place
        ArrayList<WeekViewEvent> events = new ArrayList<>();

        String[] splitTime;
        DayOfWeek dayOfWeek;
        LocalTime startTime, endTime;
        DayTime start, end;
        WeekViewEvent event;
        for (int i = 0; i < length; i++) {
            String[] days = this.daysFull[i].split(" ");
            event = new WeekViewEvent();

            // Parse hours
            splitTime = this.times[i].split(" - ");
            startTime = LocalTime.parse(splitTime[0], dtf);
            endTime = LocalTime.parse(splitTime[1], dtf);

            // Set Location
            event.setLocation(this.locationsShort[i]);
            for (String day: days) {
                dayOfWeek = DayOfWeek.valueOf(day);

                start = new DayTime(dayOfWeek, startTime);
                end = new DayTime(dayOfWeek, endTime);

                event.setStartTime(start);
                event.setEndTime(end);

                events.add(event);
            }
        }

        return events;
    }

    /**
     * Check if the same instructor is used.
     * @return true if same instructor is used.
     */
    public boolean isSameInstructor() {
        return Collections.frequency(Arrays.asList(instructors), instructors[0]) == instructors.length;
    }
}
