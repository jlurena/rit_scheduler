package me.jlurena.ritscheduler.models;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Month;
import org.threeten.bp.temporal.TemporalAdjusters;

import java.util.Locale;

public class Term {

    public static String toTermCode(LocalDate date) {
        return toTermCode(date.getYear(), isSpringTerm(date), isFallTerm(date));
    }

    public static String toTermCode(int year, boolean isSpring, boolean isFall) {
        int millenia = year / 1000;
        int decade = year % 100;
        int term;
        if (isFall) {
            term = 1;
        } else {
            decade--;
            term = isSpring ? 5 : 8;
        }

        return String.format(Locale.getDefault(), "%d%d%d", millenia, decade, term);
    }

    public static boolean isFallTerm(LocalDate date) {
        // Before 2nd Monday of December but after last Monday of August
        return (date.compareTo(date.withMonth(Month.DECEMBER.getValue())
                .with(TemporalAdjusters.dayOfWeekInMonth(2, DayOfWeek.MONDAY))) <= 0)
                && (date.compareTo(date.withMonth(Month.AUGUST.getValue())
                .with(TemporalAdjusters.lastInMonth(DayOfWeek.MONDAY))) >= 0);
    }

    public static boolean isSpringTerm(LocalDate date) {
        return date.compareTo(date.withMonth(Month.APRIL.getValue()).with(TemporalAdjusters.lastInMonth(DayOfWeek.MONDAY))) <= 0;
    }

    /**
     * Calculates term code for the following term.
     * @return String code for the next term.
     */
    public static String nextTerm() {
        LocalDate today = LocalDate.now();
        if (isFallTerm(today)) {
            // Today would be at least August of current year school year.
            return toTermCode(today.getYear(), true, false); // Next term is  Spring
        } else if (isSpringTerm(today)) {
            // Today would be January of next year, but of the past school year. Subtract 1 year
            return toTermCode(today.getYear() - 1, false, false); // Next term is Summer
        } else {
            // Next term is part of next year. Today would be May of the past school year. Keep the same
            return toTermCode(today.getYear(), false, true); // Next term is Fall + the following year
        }
    }
}
