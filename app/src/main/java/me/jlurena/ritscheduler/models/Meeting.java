package me.jlurena.ritscheduler.models;

/**
 * models.Meeting.java
 * Represents meeting times of a course.
 */

public class Meeting {

    public static final String TYPE = "meeting";
    private String[] days;
    private String[] daysFull;
    private String[] locations;
    private String[] locationsShort;
    private String[] times;
    private String[] dates;
    private String[] dayTimes;

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
}
