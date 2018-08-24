package me.jlurena.ritscheduler.models;

/**
 * A singleton class depicting the Settings of this application.
 */
public class Settings {

    private static Settings settings;
    private boolean autoLimitTime = false;
    private int numberOfVisibleDays = 3;
    private int minTime = 0;
    private int maxTime = 1380;
    private int firstVisibleDay = 0;

    /**
     * Get Settings instance.
     *
     * @return Settings instance.
     * @throws IllegalStateException if Settings instance has not yet been instantiated.
     */
    public static Settings getInstance() {
        if (settings == null) {
            settings = new Settings();
        }
        return settings;
    }

    public int getFirstVisibleDay() {
        return firstVisibleDay;
    }

    public Settings setFirstVisibleDay(int firstVisibleDay) {
        this.firstVisibleDay = firstVisibleDay;
        return this;
    }

    public int getMaxHour() {
        return maxTime / 60;
    }

    public Settings setMaxHour(int hour) {
        this.maxTime = hour * 60;
        return this;
    }

    /**
     * Get max time in form of minutes after midnight.
     *
     * @return Max time.
     */
    public int getMaxTime() {
        return maxTime;
    }

    /**
     * Set maximum time in form of minutes after midnight.
     *
     * @return Minimum time.
     */
    public Settings setMaxTime(int maxTime) {
        this.maxTime = maxTime;
        return this;
    }

    public int getMinHour() {
        return minTime / 60;
    }

    public Settings setMinHour(int hour) {
        this.minTime = hour * 60;
        return this;
    }

    /**
     * Get minimum time in form of minutes after midnight.
     *
     * @return Minimum time.
     */
    public int getMinTime() {
        return minTime;
    }

    /**
     * Set minimum time in form of minutes after midnight.
     *
     * @return Minimum time.
     */
    public Settings setMinTime(int minTime) {
        this.minTime = minTime;
        return this;
    }

    public int getNumberOfVisibleDays() {
        return numberOfVisibleDays;
    }

    public Settings setNumberOfVisibleDays(int numberOfVisibleDays) {
        this.numberOfVisibleDays = numberOfVisibleDays;
        return this;
    }

    public boolean isAutoLimitTime() {
        return autoLimitTime;
    }

    public Settings setAutoLimitTime(boolean autoLimitTime) {
        this.autoLimitTime = autoLimitTime;
        return this;
    }

    public Settings setTimeRange(String timeRange) {
        String[] split = timeRange.split("-");
        this.minTime = Integer.parseInt(split[0]);
        this.maxTime = Integer.parseInt(split[1]);
        return this;
    }
}
