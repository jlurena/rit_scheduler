package me.jlurena.ritscheduler.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.jlurena.revolvingweekview.DayTime;
import me.jlurena.revolvingweekview.WeekViewEvent;
import me.jlurena.ritscheduler.utils.Utils;

/**
 * models.Meeting.java
 * Represents meeting times of a course.
 */

@SuppressWarnings("ConstantConditions")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Meeting {

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

    /**
     * Get instructor name wrapped around an anchor mailto tag linking to their email.
     *
     * @return Comma seperated anchor tags with linked email with professors name.
     */
    @JsonIgnore
    public String getInstructorsNameWithEmail() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < instructors.length; i++) {
            builder.append("<a href=\"mailto:");
            if (i < instructorEmails.length) {
                builder.append(instructorEmails[i]);
            }
            builder.append("\">");
            builder.append(instructors[i]).append("</a>");
            if (i != instructors.length - 1) {
                builder.append(", ");
            }
        }

        return builder.toString();
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

    /**
     * Helper method that ensures a 1-1 relation with day time meetings and locations in cases
     * where all locations share the same day time meetings. In essence, makes sure that locations.length = dayTime.length.
     *
     * @return An array of locations of size equal to dayTimes.
     */
    @JsonIgnore
    public String[] getLocationsShortForEachDayTime() {
        // Sometimes locations < meetings, in this case duplicate this list
        int l = this.dayTimes.length;
        if (this.locationsShort.length != l) {
            String[] locations = new String[l];
            Arrays.fill(locations, this.locationsShort[0]);
            return locations;
        } else {
            return this.locationsShort;
        }
    }

    public String[] getTimes() {
        return times;
    }

    public void setTimes(String[] times) {
        this.times = times;
    }

    /**
     * Whether this class is an online class.
     *
     * @return true if online else false.
     */
    @JsonIgnore
    public boolean isOnline() {
        for (String location : locations) {
            if (location.equals("Online Class")) {
                return true;
            }
        }
        return locations.length == 0;
    }

    /**
     * Check if the same instructor is used.
     *
     * @return true if same instructor is used.
     */
    @JsonIgnore
    public boolean isSameInstructor() {
        return Collections.frequency(Arrays.asList(instructors), instructors[0]) == instructors.length;
    }

    /**
     * Whether meeting time is to be announced.
     *
     * @return true if it is to be announced.
     */
    @JsonIgnore
    public boolean isTimeTBA() {
        for (String day : this.daysFull) {
            if (day.equals("To Be Announced")) {
                return true;
            }
        }
        return this.daysFull.length == 0;
    }

    /**
     * Creates a List of WeekViewEvents with start time and end times pertaining to this Meeting.
     *
     * @return A list of WeekViewEvents <b>ONLY</b> with locations, start time and end times properties.
     */
    public List<WeekViewEvent> toWeekViewEvents() {


        int daysLen = this.daysFull.length;
        int timesLen = this.times.length;
        // Days length will always equal the number of times meeting takes place
        ArrayList<WeekViewEvent> events = new ArrayList<>();

        String[] splitTime;
        DayOfWeek dayOfWeek;
        LocalTime startTime, endTime;
        DayTime start, end;
        WeekViewEvent event;
        String[] locations = getLocationsShortForEachDayTime();
        for (int i = 0, j = 0; i < daysLen; i++, j++) {
            String[] days = this.daysFull[i].split(" ");
            if (j >= timesLen) {
                j--;
            }
            // Parse hours
            splitTime = this.times[j].split(" - ");
            startTime = LocalTime.parse(splitTime[0], Utils.STANDARD_TIME_FORMAT);
            endTime = LocalTime.parse(splitTime[1], Utils.STANDARD_TIME_FORMAT);

            // Set Location
            for (String day : days) {
                event = new WeekViewEvent();
                event.setLocation(locations[i]);
                dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());

                start = new DayTime(dayOfWeek, startTime);
                end = new DayTime(dayOfWeek, endTime);

                event.setStartTime(start);
                event.setEndTime(end);

                events.add(event);
            }
        }

        return events;
    }
}
