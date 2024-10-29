package com.example.adminjava;

public class ClassModel {
    private String id; // ID của lớp học
    private String courseId; // ID của khóa học
    private String teacherName; // Tên giáo viên
    private String date; // Ngày của lớp học
    private String comments; // Nhận xét về lớp học

    // Constructor không tham số
    public ClassModel(long aLong, long cursorLong, String string, String cursorString, String comments) { }

    // Constructor có tham số
    public ClassModel(long id, String courseId, String teacherName, String date, String comments) {
        this.id = String.valueOf(id);
        this.courseId = courseId;
        this.teacherName = teacherName;
        this.date = date;
        this.comments = comments;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
