package me.jlurena.ritscheduler.models;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Month;
import org.threeten.bp.temporal.TemporalAdjusters;

import java.util.Locale;

public class Term {

    /**
     * Enum representing Semesters in a Term.
     */
    public enum Semester {
        Fall(1),
        Spring(5),
        Summer(8);

        private final int semesterValue;

        Semester(int semesterValue) {
            this.semesterValue = semesterValue;
        }

        /**
         * Get the semester value.
         *
         * @return Integer representing semester value. { Fall => 1, Spring => 5, Summer => 8}
         */
        public int getSemesterValue() {
            return semesterValue;
        }

        /**
         * Get Semester of a date.
         *
         * @param date The date.
         * @return The Semester corresponding to the date.
         */
        public static Semester of(LocalDate date) {
            if (isFall(date)) {
                return Semester.Fall;
            } else if (isSpring(date)) {
                return Semester.Spring;
            } else {
                return Semester.Summer;
            }
        }

        /**
         * If date falls in the Fall term.
         *
         * @param date Date to check.
         * @return true if falls in Fall term.
         */
        public static boolean isFall(LocalDate date) {
            // Before 2nd Monday of December but after last Monday of August
            return (date.compareTo(date.withMonth(Month.DECEMBER.getValue())
                    .with(TemporalAdjusters.dayOfWeekInMonth(2, DayOfWeek.MONDAY))) <= 0)
                    && (date.compareTo(date.withMonth(Month.AUGUST.getValue())
                    .with(TemporalAdjusters.lastInMonth(DayOfWeek.MONDAY))) >= 0);
        }

        /**
         * If Date falls in Spring term.
         *
         * @param date Date to check.
         * @return true if falls in Spring term.
         */
        public static boolean isSpring(LocalDate date) {
            return date.compareTo(date.withMonth(Month.APRIL.getValue()).with(TemporalAdjusters.lastInMonth(DayOfWeek
                    .MONDAY))) <= 0;
        }
    }

    private LocalDate currentTermDate = LocalDate.now();
    private Semester semester;
    private String termCode;

    public Semester getSemester() {
        return semester;
    }

    public String getTermCode() {
        return termCode;
    }

    /**
     * Cyphers a date into a Term code.
     *
     * @param date Date to cypher into a term code.
     * @return The term code.
     */
    public static String toTermCode(LocalDate date) {
        return toTermCode(date.getYear(), Semester.of(date));
    }


    /**
     * Cypher into a Term code.
     *
     * @param year Year of term.
     * @param semester If it falls in Spring term.
     * @return A term code.
     */
    public static String toTermCode(int year, Semester semester) {
        int millenia = year / 1000;
        int decade = year % 100;
        if (semester != Semester.Fall) {
            decade--;
        }

        return String.format(Locale.getDefault(), "%d%d%d", millenia, decade, semester.getSemesterValue());
    }

    /**
     * Get the future term given a number of terms to jump. Changes this Term.
     *
     * @param semestersToJump Terms to jump ahead.
     * @return A coded term.
     */
    public String goToSemester(int semestersToJump) {
        if (semestersToJump > 0) {
            nextSemester();
            goToSemester(--semestersToJump);
        }

        return currentTerm();
    }

    public String currentTerm() {
        return termCode == null ? toTermCode(currentTermDate) : termCode;
    }

    /**
     * Calculates term code for the following term. Changes this Term.
     *
     * @return String code for the next term.
     */
    public String nextSemester() {
        if (Semester.isFall(currentTermDate)) {
            // Next term is Spring
            currentTermDate = currentTermDate.plusYears(1).withMonth(Month.JANUARY.getValue()).with(TemporalAdjusters
                    .dayOfWeekInMonth(2, DayOfWeek.MONDAY));
            semester = Semester.Spring;
            termCode = toTermCode(currentTermDate.getYear(), Semester.Spring);
        } else if (Semester.isSpring(currentTermDate)) {
            // Next semester is Summer
            currentTermDate = currentTermDate.withMonth(Month.JUNE.getValue()); // Any day in June is Summer term.
            semester = Semester.Summer;
            termCode = toTermCode(currentTermDate.getYear(), Semester.Summer); // Next term is Summer
        } else {
            // Next semester is Fall
            currentTermDate = currentTermDate.withMonth(Month.AUGUST.getValue()).with(TemporalAdjusters.lastInMonth
                    (DayOfWeek.MONDAY));
            semester = Semester.Fall;
            termCode = toTermCode(currentTermDate.getYear(), Semester.Fall);
        }
        return termCode;
    }
}
