package me.jlurena.ritscheduler.models;

import org.junit.Test;
import org.threeten.bp.LocalDate;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class TermTest {

    @Test
    public void getTermCode_TermCode_IsValid() {
        String correctTermCode = "2181";
        Term term = Term.of(2018, 9, 1);
        assertEquals(correctTermCode, term.getTermCode());
        assertEquals(Term.Semester.Fall, term.getSemester());
    }

    @Test
    public void nextFourSemester_TermCode_IsValid() {
        String nextFiveSemesterCode = "2195";
        Term newTerm = Term.of(LocalDate.of(2018, 9, 1)).plusSemesters(4);
        assertEquals(nextFiveSemesterCode, newTerm.getTermCode());
        assertEquals(Term.Semester.Spring, newTerm.getSemester());
    }

    @Test
    public void nextSemester_TermCode_IsValid() {
        String nextSemesterCode = "2188";
        Term newTerm = Term.of(2019, 1, 20).nextSemester();
        assertEquals(nextSemesterCode, newTerm.getTermCode());
        assertEquals(Term.Semester.Summer, newTerm.getSemester());
    }

    @Test
    public void isFall_Returns_True() {
        assertTrue(Term.Semester.isFall(LocalDate.of(2018, 9, 1)));
    }

    @Test
    public void isSpring_Returns_True() {
        assertTrue(Term.Semester.isSpring(LocalDate.of(2018, 1, 1)));
    }

    @Test
    public void isSummer_Returns_True() {
        assertTrue(Term.Semester.isSummer(LocalDate.of(2018, 7, 1)));
    }
}
