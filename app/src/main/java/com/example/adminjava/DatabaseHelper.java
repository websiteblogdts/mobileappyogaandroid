package com.example.adminjava;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "yoga_class_db_test";
    private static final int DATABASE_VERSION = 2; // Increment version due to added courses table

    // Table names
    private static final String TABLE_CLASSES = "classes";
    private static final String TABLE_COURSES = "courses";
    private static final String TABLE_USERS = "users";

// Columns for classes table
private static final String COLUMN_CLASS_ID = "id";
    private static final String COLUMN_CLASS_COURSE_ID = "course_id";
    private static final String COLUMN_CLASS_TEACHER_NAME = "teacher_name";
    private static final String COLUMN_CLASS_DATE = "date";
    private static final String COLUMN_CLASS_COMMENTS = "comments";

    // Columns for courses table
    private static final String COLUMN_COURSE_ID = "id";
    private static final String COLUMN_COURSE_NAME = "name";
    private static final String COLUMN_COURSE_DAY_OF_WEEK = "day_of_week";
    private static final String COLUMN_COURSE_TIME = "time";
    private static final String COLUMN_COURSE_CAPACITY = "capacity";
    private static final String COLUMN_COURSE_DURATION = "duration";
    private static final String COLUMN_COURSE_PRICE = "price";
    private static final String COLUMN_COURSE_TYPE = "type";
    private static final String COLUMN_COURSE_DESCRIPTION = "description";

    // Columns for users table
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";

    // SQL statements to create tables
    private static final String CREATE_TABLE_COURSES = "CREATE TABLE "
            + TABLE_COURSES + "("
            + COLUMN_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_COURSE_NAME + " TEXT NOT NULL, "
            + COLUMN_COURSE_DAY_OF_WEEK + " TEXT NOT NULL, "
            + COLUMN_COURSE_TIME + " TEXT NOT NULL, "
            + COLUMN_COURSE_CAPACITY + " INTEGER NOT NULL, "
            + COLUMN_COURSE_DURATION + " INTEGER NOT NULL, "
            + COLUMN_COURSE_PRICE + " REAL NOT NULL, "
            + COLUMN_COURSE_TYPE + " TEXT NOT NULL, "
            + COLUMN_COURSE_DESCRIPTION + " TEXT"
            + ")";

private static final String CREATE_TABLE_CLASSES = "CREATE TABLE "
        + TABLE_CLASSES + "("
        + COLUMN_CLASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + COLUMN_CLASS_COURSE_ID + " INTEGER, "
        + COLUMN_CLASS_TEACHER_NAME + " TEXT NOT NULL, "
        + COLUMN_CLASS_DATE + " TEXT NOT NULL, "
        + COLUMN_CLASS_COMMENTS + " TEXT, "
        + "FOREIGN KEY(" + COLUMN_CLASS_COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(" + COLUMN_CLASS_ID + ")"
        + ")";
    private static final String CREATE_TABLE_USERS = "CREATE TABLE "
            + TABLE_USERS + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USER_NAME + " TEXT NOT NULL, "
            + COLUMN_USER_EMAIL + " TEXT NOT NULL UNIQUE, "
            + COLUMN_USER_PASSWORD + " TEXT NOT NULL"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_COURSES);
        db.execSQL(CREATE_TABLE_CLASSES);
        db.execSQL(CREATE_TABLE_USERS);
        Log.d("DatabaseHelper", "Tables created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop old tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        // Create tables again
        onCreate(db);
    }

    // Add a course
    public long addCourse(String name, String dayOfWeek, String time, int capacity, int duration, double price, String type, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COURSE_NAME, name);
        values.put(COLUMN_COURSE_DAY_OF_WEEK, dayOfWeek);
        values.put(COLUMN_COURSE_TIME, time);
        values.put(COLUMN_COURSE_CAPACITY, capacity);
        values.put(COLUMN_COURSE_DURATION, duration);
        values.put(COLUMN_COURSE_PRICE, price);
        values.put(COLUMN_COURSE_TYPE, type);
        values.put(COLUMN_COURSE_DESCRIPTION, description);

        long id = db.insert(TABLE_COURSES, null, values);
        syncCourseToFirebase(id, name, dayOfWeek, time, capacity, duration, price, type, description);

        return id;
    }

    // Sync course to Firebase
    private void syncCourseToFirebase(long courseId, String name, String dayOfWeek, String time, int capacity, int duration, double price, String type, String description) {
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference("courses").child(String.valueOf(courseId));
        CourseModel course = new CourseModel(courseId, name, dayOfWeek, time, capacity, duration, price, type, description);
        courseRef.setValue(course);
    }

    // Get all courses
    public List<CourseModel> getAllCourses() {
        List<CourseModel> courseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_COURSES, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") CourseModel course = new CourseModel(
                        cursor.getLong(cursor.getColumnIndex(COLUMN_COURSE_ID)), // ID
                        cursor.getString(cursor.getColumnIndex(COLUMN_COURSE_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_COURSE_DAY_OF_WEEK)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_COURSE_TIME)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_COURSE_CAPACITY)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_COURSE_DURATION)),
                        cursor.getDouble(cursor.getColumnIndex(COLUMN_COURSE_PRICE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_COURSE_TYPE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_COURSE_DESCRIPTION))
                );
                courseList.add(course);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return courseList;
    }


    public void updateCourse(CourseModel course) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_COURSE_NAME, course.getName());
        contentValues.put(COLUMN_COURSE_DAY_OF_WEEK, course.getDayOfWeek());
        contentValues.put(COLUMN_COURSE_TIME, course.getTime());
        contentValues.put(COLUMN_COURSE_CAPACITY, course.getCapacity());
        contentValues.put(COLUMN_COURSE_DURATION, course.getDuration());
        contentValues.put(COLUMN_COURSE_PRICE, course.getPrice());
        contentValues.put(COLUMN_COURSE_TYPE, course.getType());
        contentValues.put(COLUMN_COURSE_DESCRIPTION, course.getDescription());

        // Assuming you have a method to get the course ID
        db.update(TABLE_COURSES, contentValues, COLUMN_COURSE_ID + " = ?", new String[]{String.valueOf(course.getId())});
        // Cập nhật vào Firebase
        syncCourseToFirebase(Long.parseLong(course.getId()), course.getName(), course.getDayOfWeek(), course.getTime(),
                course.getCapacity(), course.getDuration(), course.getPrice(), course.getType(),
                course.getDescription());

    }

    // Delete a course
    public void deleteCourse(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COURSES, COLUMN_COURSE_ID + "=?", new String[]{String.valueOf(id)});

        // Delete data from Firebase
        deleteCourseFromFirebase(id);
        // Xóa lớp học liên quan
        List<ClassModel> classes = getClassesByCourseId(id);
        for (ClassModel classModel : classes) {
            deleteClass(Long.parseLong(classModel.getId()));
        }
    }

    // Delete course from Firebase
    private void deleteCourseFromFirebase(long courseId) {
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference("courses").child(String.valueOf(courseId));
        courseRef.removeValue();
    }


public long addClass(long courseId, String teacherName, String date, String comments) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_CLASS_COURSE_ID, courseId);
    values.put(COLUMN_CLASS_TEACHER_NAME, teacherName);
    values.put(COLUMN_CLASS_DATE, date);
    values.put(COLUMN_CLASS_COMMENTS, comments);

    long id = db.insert(TABLE_CLASSES, null, values);
    syncClassToFirebase(id, String.valueOf(courseId), teacherName, date, comments);

    return id;
}

    public int updateClass(long id, long courseId, String teacherName, String date, String comments) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASS_COURSE_ID, courseId);
        values.put(COLUMN_CLASS_TEACHER_NAME, teacherName);
        values.put(COLUMN_CLASS_DATE, date);
        values.put(COLUMN_CLASS_COMMENTS, comments);
        //        return db.update(TABLE_CLASSES, values, COLUMN_CLASS_ID + "=?", new String[]{String.valueOf(id)});
        int rowsAffected = db.update(TABLE_CLASSES, values, COLUMN_CLASS_ID + "=?", new String[]{String.valueOf(id)});
        syncClassToFirebase(id, String.valueOf(courseId), teacherName, date, comments);
        return rowsAffected;

    }

    // Delete a class
    public void deleteClass(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASSES, COLUMN_CLASS_ID + "=?", new String[]{String.valueOf(id)});
        // Delete class from Firebase
        deleteClassFromFirebase(id);
    }

    // Sync delete class from Firebase
    private void deleteClassFromFirebase(long classId) {
        DatabaseReference classRef = FirebaseDatabase.getInstance().getReference("classes").child(String.valueOf(classId));
        classRef.removeValue();
    }
    private void syncClassToFirebase(long classId, String courseId, String teacherName, String date, String comments) {
        DatabaseReference classRef = FirebaseDatabase.getInstance().getReference("classes").child(String.valueOf(classId));
        ClassModel classModel = new ClassModel(classId, courseId, teacherName, date, comments);
        classRef.setValue(classModel);
    }

    // Lấy tất cả các lớp học
    public List<ClassModel> getAllClasses() {
        List<ClassModel> classList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLASSES, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") ClassModel classModel = new ClassModel(
                        cursor.getLong(cursor.getColumnIndex(COLUMN_CLASS_ID)),
                        cursor.getLong(cursor.getColumnIndex(COLUMN_CLASS_COURSE_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_TEACHER_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_DATE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_COMMENTS))
                );
                classList.add(classModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return classList;
    }


public List<ClassModel> getClassesByCourseId(long courseId) {
    List<ClassModel> classList = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.query(TABLE_CLASSES, null, COLUMN_CLASS_COURSE_ID + "=?", new String[]{String.valueOf(courseId)}, null, null, null);

    if (cursor.moveToFirst()) {
        do {
            @SuppressLint("Range") ClassModel classModel = new ClassModel(
                    Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_ID))), // ID lớp học
                    cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_COURSE_ID)), // ID khóa học
                    cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_TEACHER_NAME)), // Tên giáo viên
                    cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_DATE)), // Ngày lớp học
                    cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_COMMENTS)) // Nhận xét (comments)
            );
            classList.add(classModel);
        } while (cursor.moveToNext());
    }
    cursor.close();
    return classList;
}

    // Search for classes by teacher (coach) name
    public List<ClassModel> searchClassesByTeacherName(String teacherName) {
        List<ClassModel> classList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Truy vấn để tìm lớp học dựa trên tên giáo viên
        Cursor cursor = db.query(TABLE_CLASSES, null, COLUMN_CLASS_TEACHER_NAME + " LIKE ?",
                new String[]{"%" + teacherName + "%"}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") ClassModel classModel = new ClassModel(
                        Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_ID))), // ID lớp học
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_COURSE_ID)), // ID khóa học
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_TEACHER_NAME)), // Tên giáo viên
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_DATE)), // Ngày lớp học
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_COMMENTS)) // Nhận xét (comments)
                );
                classList.add(classModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return classList;
    }



}
