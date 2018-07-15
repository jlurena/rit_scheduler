package me.jlurena.ritscheduler.models;

import android.support.annotation.ColorInt;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * models.Course.java
 * Represents a course.
 */

public class Course extends Model {

    @JsonIgnore
    private static final String TYPE = "course";
    /**
     * Color assigned by user for UI.
     */
    private @ColorInt int color;
    private String subject;
    private String enrollStatus;
    private String classType;
    private String component;
    private String instructorMode;
    private String campus;
    private String startingTerm;
    private String academicGroupShort;
    private String academicCareer;
    private String[] attributeValues;
    private String courseTitleLong;
    private String catalogNumber;
    private String[] attributeDescriptions;
    private int waitCap;
    private String classNumber;
    private String sessionCode;
    private String[] attributes;
    private int waitTotal;
    private int enrollmentCap;
    private String ppSearchId;
    private String academicTitle;
    private String location;
    private int enrollmentTotal;
    private String section;
    private String courseDescription;
    private String academicGroup;
    private int unitsMaximum;
    private int unitsMinimum;
    private String gradingBasis;
    private String associatedClassNumber;
    private String autoEnrollSect1;
    private String autoEnrollSect2;
    @JsonAlias("model_id")
    private String courseId;
    private String printTopic;
    private String courseTopicId;
    private String courseTopic;
    private String[] associatedComponents;
    private RelatedAttributes relatedAttributes;
    private Meeting meetings;
    private String[] preReqDescriptions;
    private String[] preReqDescrsShort;
    private String[] preReqDescrsLong;
    private String notes;
    private String[] associatedClasses;
    private ReservedSeat[] reservedCap;
    private String comboSection;

    public Course() {
        super(TYPE);
    }

    public String getComboSection() {
        return comboSection;
    }

    public void setComboSection(String comboSection) {
        this.comboSection = comboSection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Course course = (Course) o;

        return this.courseId.equals(course.courseId);
    }

    @Override
    public int hashCode() {
        return notes.hashCode();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getEnrollStatus() {
        return enrollStatus;
    }

    public void setEnrollStatus(String enrollStatus) {
        this.enrollStatus = enrollStatus;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getInstructorMode() {
        return instructorMode;
    }

    public void setInstructorMode(String instructorMode) {
        this.instructorMode = instructorMode;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getStartingTerm() {
        return startingTerm;
    }

    public void setStartingTerm(String startingTerm) {
        this.startingTerm = startingTerm;
    }

    public String getAcademicGroupShort() {
        return academicGroupShort;
    }

    public void setAcademicGroupShort(String academicGroupShort) {
        this.academicGroupShort = academicGroupShort;
    }

    public String getAcademicCareer() {
        return academicCareer;
    }

    public void setAcademicCareer(String academicCareer) {
        this.academicCareer = academicCareer;
    }

    public String[] getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(String[] attributeValues) {
        this.attributeValues = attributeValues;
    }

    public String getCourseTitleLong() {
        return courseTitleLong;
    }

    public void setCourseTitleLong(String courseTitleLong) {
        this.courseTitleLong = courseTitleLong;
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public void setCatalogNumber(String catalogNumber) {
        this.catalogNumber = catalogNumber;
    }

    public String[] getAttributeDescriptions() {
        return attributeDescriptions;
    }

    public void setAttributeDescriptions(String[] attributeDescriptions) {
        this.attributeDescriptions = attributeDescriptions;
    }

    public int getWaitCap() {
        return waitCap;
    }

    public void setWaitCap(int waitCap) {
        this.waitCap = waitCap;
    }

    public String getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public String[] getAttributes() {
        return attributes;
    }

    public void setAttributes(String[] attributes) {
        this.attributes = attributes;
    }

    public int getWaitTotal() {
        return waitTotal;
    }

    public void setWaitTotal(int waitTotal) {
        this.waitTotal = waitTotal;
    }

    public int getEnrollmentCap() {
        return enrollmentCap;
    }

    public void setEnrollmentCap(int enrollmentCap) {
        this.enrollmentCap = enrollmentCap;
    }

    public String getPpSearchId() {
        return ppSearchId;
    }

    public void setPpSearchId(String ppSearchId) {
        this.ppSearchId = ppSearchId;
    }

    public String getAcademicTitle() {
        return academicTitle;
    }

    public void setAcademicTitle(String academicTitle) {
        this.academicTitle = academicTitle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getEnrollmentTotal() {
        return enrollmentTotal;
    }

    public void setEnrollmentTotal(int enrollmentTotal) {
        this.enrollmentTotal = enrollmentTotal;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public String getAcademicGroup() {
        return academicGroup;
    }

    public void setAcademicGroup(String academicGroup) {
        this.academicGroup = academicGroup;
    }

    public int getUnitsMaximum() {
        return unitsMaximum;
    }

    public void setUnitsMaximum(int unitsMaximum) {
        this.unitsMaximum = unitsMaximum;
    }

    public int getUnitsMinimum() {
        return unitsMinimum;
    }

    public void setUnitsMinimum(int unitsMinimum) {
        this.unitsMinimum = unitsMinimum;
    }

    public String getGradingBasis() {
        return gradingBasis;
    }

    public void setGradingBasis(String gradingBasis) {
        this.gradingBasis = gradingBasis;
    }

    public String getAssociatedClassNumber() {
        return associatedClassNumber;
    }

    public void setAssociatedClassNumber(String associatedClassNumber) {
        this.associatedClassNumber = associatedClassNumber;
    }

    public String getAutoEnrollSect1() {
        return autoEnrollSect1;
    }

    public void setAutoEnrollSect1(String autoEnrollSect1) {
        this.autoEnrollSect1 = autoEnrollSect1;
    }

    public String getAutoEnrollSect2() {
        return autoEnrollSect2;
    }

    public void setAutoEnrollSect2(String autoEnrollSect2) {
        this.autoEnrollSect2 = autoEnrollSect2;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
        super.setModelId(courseId);
    }

    public String getPrintTopic() {
        return printTopic;
    }

    public void setPrintTopic(String printTopic) {
        this.printTopic = printTopic;
    }

    public String getCourseTopicId() {
        return courseTopicId;
    }

    public void setCourseTopicId(String courseTopicId) {
        this.courseTopicId = courseTopicId;
    }

    public String getCourseTopic() {
        return courseTopic;
    }

    public void setCourseTopic(String courseTopic) {
        this.courseTopic = courseTopic;
    }

    public String[] getAssociatedComponents() {
        return associatedComponents;
    }

    public void setAssociatedComponents(String[] associatedComponents) {
        this.associatedComponents = associatedComponents;
    }

    public RelatedAttributes getRelatedAttributes() {
        return relatedAttributes;
    }

    public void setRelatedAttributes(RelatedAttributes relatedAttributes) {
        this.relatedAttributes = relatedAttributes;
    }

    public Meeting getMeetings() {
        return meetings;
    }

    public void setMeetings(Meeting meetings) {
        this.meetings = meetings;
    }

    public String[] getPreReqDescriptions() {
        return preReqDescriptions;
    }

    public void setPreReqDescriptions(String[] preReqDescriptions) {
        this.preReqDescriptions = preReqDescriptions;
    }

    public String[] getPreReqDescrsShort() {
        return preReqDescrsShort;
    }

    public void setPreReqDescrsShort(String[] preReqDescrsShort) {
        this.preReqDescrsShort = preReqDescrsShort;
    }

    public String[] getPreReqDescrsLong() {
        return preReqDescrsLong;
    }

    public void setPreReqDescrsLong(String[] preReqDescrsLong) {
        this.preReqDescrsLong = preReqDescrsLong;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String[] getAssociatedClasses() {
        return associatedClasses;
    }

    public void setAssociatedClasses(String[] associatedClasses) {
        this.associatedClasses = associatedClasses;
    }

    public ReservedSeat[] getReservedCap() {
        return reservedCap;
    }

    public void setReservedCap(ReservedSeat[] reservedCap) {
        this.reservedCap = reservedCap;
    }

    @Override
    public Map<String, Object> toMap() {
        //noinspection unchecked
        return new ObjectMapper().convertValue(this, Map.class);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}