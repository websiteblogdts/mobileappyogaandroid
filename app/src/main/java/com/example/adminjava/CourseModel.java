package com.example.adminjava;

public class CourseModel {
    private String id; // ID của khóa học
    private String name; // Tên khóa học
    private String dayOfWeek; // Ngày trong tuần
    private String time; // Thời gian
    private int capacity; // Số lượng tối đa của khóa học
    private int duration; // Thời gian diễn ra khóa học
    private double price; // Giá khóa học
    private String type; // Loại khóa học
    private String description; // Mô tả khóa học

    // Constructor không tham số
    public CourseModel() { }

    // Constructor có tham số
    public CourseModel(long id, String name, String dayOfWeek, String time, int capacity, int duration, double price, String type, String description) {
        this.id = String.valueOf(id);
        this.name = name;
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.type = type;
        this.description = description;
    }

    // Getter và Setter cho ID
// Getter cho ID
    public String getId() {
        return String.valueOf(id);
    }
    public void setId(String id) { this.id = id; }

    // Getter và Setter cho name
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Getter và Setter cho dayOfWeek
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    // Getter và Setter cho time
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    // Getter và Setter cho capacity
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    // Getter và Setter cho duration
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    // Getter và Setter cho price
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    // Getter và Setter cho type
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    // Getter và Setter cho description
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
