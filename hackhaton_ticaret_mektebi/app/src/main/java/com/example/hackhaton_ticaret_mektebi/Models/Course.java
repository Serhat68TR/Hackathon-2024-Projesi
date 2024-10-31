package com.example.hackhaton_ticaret_mektebi.Models;

public class Course {
    private String courseID;
    private String courseName;
    private String teacherID;
    private String courseDate;
    private String courseStartTime;
    private String courseEndTime;
    private String courseDepartment;
    public Course() {
        // Default constructor
    }

    public Course(String courseID, String courseName, String teacherID, String courseDate, String courseStartTime, String courseEndTime, String courseDepartment) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.teacherID = teacherID;
        this.courseDate = courseDate;
        this.courseStartTime = courseStartTime;
        this.courseEndTime = courseEndTime;
        this.courseDepartment = courseDepartment;
    }

    public String getCourseDepartment() {
        return courseDepartment;
    }

    public void setCourseDepartment(String courseDepartment) {
        this.courseDepartment = courseDepartment;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(String courseDate) {
        this.courseDate = courseDate;
    }

    public String getCourseStartTime() {
        return courseStartTime;
    }

    public void setCourseStartTime(String courseStartTime) {
        this.courseStartTime = courseStartTime;
    }

    public String getCourseEndTime() {
        return courseEndTime;
    }

    public void setCourseEndTime(String courseEndTime) {
        this.courseEndTime = courseEndTime;
    }
}
