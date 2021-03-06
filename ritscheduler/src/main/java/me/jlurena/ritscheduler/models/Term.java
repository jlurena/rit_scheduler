package me.jlurena.ritscheduler.models;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Month;
import org.threeten.bp.temporal.TemporalAdjusters;

import java.util.Locale;

import me.jlurena.ritscheduler.utils.Utils;

/**
 * Immutable class representing a school Term in RIT.
 */
public class Term {

    private final LocalDate currentTermDate;
    private final Semester semester;
    private final String termCode;

    private Term(LocalDate currentTermDate, Semester semester, String termCode) {
        this.currentTermDate = currentTermDate;
        this.semester = semester;
        this.termCode = termCode;
    }

    private Term(LocalDate date) {
        this.currentTermDate = date;
        this.termCode = toTermCode(this.currentTermDate);
        if (Semester.isFall(this.currentTermDate)) {
            this.semester = Semester.Fall;
        } else if (Semester.isSpring(this.currentTermDate)) {
            this.semester = Semester.Spring;
        } else {
            this.semester = Semester.Summer;
        }
    }

    private Term(String termCode) {
        this.termCode = termCode;
        int year = (termCode.charAt(0) - '0') * 1000 + Integer.parseInt(termCode.substring(1, 3));
        char semesterChar = termCode.charAt(3);
        switch (semesterChar) {
            case '1':
                this.semester = Semester.Fall;
                this.currentTermDate = LocalDate.of(year, 10, 1);
                break;
            case '5':
                this.semester = Semester.Spring;
                this.currentTermDate = LocalDate.of(year, 2, 1);
                break;
            case '8':
                this.semester = Semester.Summer;
                this.currentTermDate = LocalDate.of(year, 7, 1);
                break;
            default:
                throw new IllegalArgumentException("Invalid Term Code");
        }
    }

    /**
     * Cyphers a date into a Term code.
     *
     * @param date Date to cypher into a term code.
     * @return The term code.
     */
    private static String toTermCode(LocalDate date) {
        return toTermCode(date.getYear(), Semester.of(date));
    }

    /**
     * Cypher into a Term code.
     *
     * @param year Year of term.
     * @param semester If it falls in Spring term.
     * @return A term code.
     */
    private static String toTermCode(int year, Semester semester) {
        int millenia = year / 1000;
        int decade = year % 100;
        if (semester != Semester.Fall) {
            decade--;
        }

        return String.format(Locale.getDefault(), "%d%d%d", millenia, decade, semester.getSemesterValue());
    }

    /**
     * Get instance of current term.
     *
     * @return The current term.
     */
    public static Term currentTerm() {
        return new Term(Utils.now.toLocalDate());
    }

    /**
     * Get an instance of a Term.
     *
     * @param year Year of term.
     * @param month Month of term.
     * @param day Day of term.
     * @return A Term.
     */
    public static Term of(int year, int month, int day) {
        return Term.of(LocalDate.of(year, month, day));
    }

    /**
     * Create a Term with a given LocalDate.
     *
     * @param date Date.
     * @return A Term object.
     */
    public static Term of(LocalDate date) {
        return new Term(date);
    }

    /**
     * Get an instance of a term.
     *
     * @param termCode Term code.
     * @return A term based on term code.
     */
    public static Term of(String termCode) {
        return new Term(termCode);
    }

    public Semester getSemester() {
        return semester;
    }

    public String getTermCode() {
        return termCode;
    }

    public String longTermName() {
        return String.format(Locale.getDefault(), "%s '%d", this.semester.toString(), this.currentTermDate.getYear());
    }

    /**
     * Calculates term code for the following term. Changes this Term.
     *
     * @return String code for the next term.
     */
    public Term nextSemester() {
        LocalDate currentTermDate;
        Semester semester;
        String termCode;
        if (Semester.isFall(this.currentTermDate)) {
            // Next term is Spring
            currentTermDate = this.currentTermDate.plusYears(1).withMonth(Month.JANUARY.getValue()).with
                    (TemporalAdjusters
                            .dayOfWeekInMonth(2, DayOfWeek.MONDAY));
            semester = Semester.Spring;
            termCode = toTermCode(currentTermDate.getYear(), Semester.Spring);
        } else if (Semester.isSpring(this.currentTermDate)) {
            // Next semester is Summer
            currentTermDate = this.currentTermDate.withMonth(Month.JUNE.getValue()); // Any day in June is Summer term.
            semester = Semester.Summer;
            termCode = toTermCode(currentTermDate.getYear(), Semester.Summer); // Next term is Summer
        } else {
            // Next semester is Fall
            currentTermDate = this.currentTermDate.withMonth(Month.AUGUST.getValue()).with(TemporalAdjusters.lastInMonth
                    (DayOfWeek.MONDAY));
            semester = Semester.Fall;
            termCode = toTermCode(currentTermDate.getYear(), Semester.Fall);
        }
        return new Term(currentTermDate, semester, termCode);
    }

    /**
     * Get the future term given a number of terms to jump. Changes this Term.
     *
     * @param semestersToJump Terms to jump ahead.
     * @return A coded term.
     */
    public Term plusSemesters(int semestersToJump) {
        Term term = this;
        while (semestersToJump > 0) {
            term = term.nextSemester();
            semestersToJump--;
        }

        return term;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%s '%d", this.semester.toString(), this.currentTermDate.getYear() % 100);
    }

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

        /**
         * If date falls in Summer.
         *
         * @param date Date.
         * @return true if falls in Summer.
         */
        public static boolean isSummer(LocalDate date) {
            return !(isFall(date) || isSpring(date));
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
         * Get the semester value.
         *
         * @return Integer representing semester value. { Fall => 1, Spring => 5, Summer => 8}
         */
        public int getSemesterValue() {
            return semesterValue;
        }
    }
}
