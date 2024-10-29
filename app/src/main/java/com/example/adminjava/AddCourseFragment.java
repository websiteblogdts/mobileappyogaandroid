package com.example.adminjava;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AddCourseFragment extends Fragment {

    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private Button btnAddCourse;
//    private ClassAdapter classAdapter;

    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_course, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCourses);
        btnAddCourse = view.findViewById(R.id.btnAddCourse);

        databaseHelper = new DatabaseHelper(getActivity());

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadCourses();

        // Set up Add Course Button to show dialog
        btnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCourseDialog();
            }
        });

        return view;
    }

    private void loadCourses() {
        List<CourseModel> courseList = databaseHelper.getAllCourses();
        courseAdapter = new CourseAdapter(courseList, new CourseAdapter.OnCourseClickListener() {
            @Override
            public void onCourseEdit(CourseModel course) {
                // Handle course edit
                showEditCourseDialog(course);
            }

            @Override
            public void onCourseDelete(CourseModel course) {
                // Handle course delete
                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete Course")
                        .setMessage("Are you sure you want to delete this course?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            databaseHelper.deleteCourse(Long.parseLong(course.getId()));
                            loadCourses(); // Reload the list after deleting
                            Toast.makeText(getActivity(), "Course deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

        });

        recyclerView.setAdapter(courseAdapter);
    }

    // Khởi tạo dữ liệu ngày trong tuần
    String[] daysOfWeek = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật"};
    boolean[] selectedDays = new boolean[daysOfWeek.length]; // Lưu trạng thái chọn

    private void showAddCourseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_course, null);
        builder.setView(dialogView);

        EditText edtName = dialogView.findViewById(R.id.edtCourseName);
        EditText edtDayOfWeek = dialogView.findViewById(R.id.edtDayOfWeek);
        EditText edtTime = dialogView.findViewById(R.id.edtTime);
        EditText edtCapacity = dialogView.findViewById(R.id.edtCapacity);
        EditText edtPrice = dialogView.findViewById(R.id.edtPrice);
        EditText edtDescription = dialogView.findViewById(R.id.edtDescription); // Thêm trường Description

        RadioGroup edtType = dialogView.findViewById(R.id.edtType); // Lấy RadioGroup


        // 1. Chọn Ngày trong tuần với AlertDialog
        edtDayOfWeek.setOnClickListener(v -> {
            AlertDialog.Builder dayBuilder = new AlertDialog.Builder(getContext());
            dayBuilder.setTitle("Chọn ngày trong tuần");

            dayBuilder.setMultiChoiceItems(daysOfWeek, selectedDays, (dialog, which, isChecked) -> {
                selectedDays[which] = isChecked; // Lưu trạng thái chọn
            });

            dayBuilder.setPositiveButton("OK", (dialog, which) -> {
                StringBuilder selectedDaysString = new StringBuilder();
                for (int i = 0; i < selectedDays.length; i++) {
                    if (selectedDays[i]) {
                        if (selectedDaysString.length() > 0) {
                            selectedDaysString.append(", ");
                        }
                        selectedDaysString.append(daysOfWeek[i]);
                    }
                }
                edtDayOfWeek.setText(selectedDaysString.toString());
            });

            dayBuilder.setNegativeButton("Cancel", null);
            dayBuilder.show();
        });

        // 2. Chọn thời gian với TimePickerDialog
        edtTime.setOnClickListener(v -> {
            TimePickerDialog startTimePicker = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                @SuppressLint("DefaultLocale") String startTime = String.format("%02d:%02d", hourOfDay, minute);

                @SuppressLint("SetTextI18n") TimePickerDialog endTimePicker = new TimePickerDialog(getContext(), (view1, endHourOfDay, endMinute) -> {
                    @SuppressLint("DefaultLocale") String endTime = String.format("%02d:%02d", endHourOfDay, endMinute);
                    edtTime.setText(startTime + " - " + endTime);
                }, 0, 0, true);

                endTimePicker.show();
            }, 0, 0, true);

            startTimePicker.show();
        });

        // 3. Thêm "persons" vào trường Capacity với TextWatcher
        edtCapacity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString()) && !s.toString().endsWith(" persons")) {
                    edtCapacity.setText(s.toString() + " persons");
                    edtCapacity.setSelection(edtCapacity.getText().length() - " persons".length());
                }
            }
        });

        // 4. Thêm ký hiệu "$" cho trường Price với TextWatcher
        edtPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString()) && !s.toString().startsWith("$")) {
                    edtPrice.setText("$" + s.toString());
                    edtPrice.setSelection(edtPrice.getText().length());
                }
            }
        });


        // Xử lý sự kiện thêm course khi nhấn nút "Add"
        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = edtName.getText().toString().trim();
            String dayOfWeek = edtDayOfWeek.getText().toString().trim();
            String time = edtTime.getText().toString().trim();
            String capacityStr = edtCapacity.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
//            String type = edtType.getText().toString().trim();           // Lấy giá trị từ trường Type
            String description = edtDescription.getText().toString().trim(); // Lấy giá trị từ trường Description


            // Lấy loại khóa học từ RadioGroup
            String type = "";
            int selectedTypeId = edtType.getCheckedRadioButtonId(); // Lấy ID của RadioButton đã chọn
            if (selectedTypeId == R.id.radioFlowYoga) {
                type = "Flow Yoga";
            } else if (selectedTypeId == R.id.radioFamilyYoga) {
                type = "Family Yoga";
            } else if (selectedTypeId == R.id.radioAerialYoga) {
                type = "Aerial Yoga";
            }
            // Kiểm tra xem có trường nào bị bỏ trống không
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(dayOfWeek) || TextUtils.isEmpty(time) ||
                    TextUtils.isEmpty(capacityStr) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(type)) {
                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            // Parse giá trị số
            int capacity = Integer.parseInt(capacityStr.replace(" persons", "").trim()); // Xóa "persons" trước khi parse
            double price = Double.parseDouble(priceStr.replace("$", "").trim()); // Xóa "$" trước khi parse

            // Gọi hàm thêm course vào database
            long courseId = databaseHelper.addCourse(name, dayOfWeek, time, capacity, 60, price, type, description); // Truyền type và description

            if (courseId != -1) {
                Toast.makeText(getActivity(), "Course added successfully!", Toast.LENGTH_SHORT).show();
                loadCourses(); // Tải lại danh sách courses
            } else {
                Toast.makeText(getActivity(), "Failed to add course", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);

        // Hiển thị dialog
        builder.create().show();
    }
    private void showEditCourseDialog(final CourseModel course) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit Course");

        // Inflate custom layout for editing course
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_course, null);
        builder.setView(view);

        EditText edtName = view.findViewById(R.id.edtCourseName);
        EditText edtDayOfWeek = view.findViewById(R.id.edtDayOfWeek);
        EditText edtTime = view.findViewById(R.id.edtTime);
        EditText edtCapacity = view.findViewById(R.id.edtCapacity);
        EditText edtDuration = view.findViewById(R.id.edtDuration);
        EditText edtPrice = view.findViewById(R.id.edtPrice);
        RadioGroup radioGroupType = view.findViewById(R.id.edtType); // Sửa đổi để lấy RadioGroup
        EditText edtDescription = view.findViewById(R.id.edtDescription);

        // Pre-fill fields with course data
        edtName.setText(course.getName());
        edtDayOfWeek.setText(course.getDayOfWeek());
        edtTime.setText(course.getTime());
        edtCapacity.setText(String.valueOf(course.getCapacity()));
        edtDuration.setText(String.valueOf(course.getDuration()));
        edtPrice.setText(String.valueOf(course.getPrice()));
        edtDescription.setText(course.getDescription());

        // Set selected RadioButton based on course type
        switch (course.getType()) {
            case "Flow Yoga":
                radioGroupType.check(R.id.radioFlowYoga);
                break;
            case "Family Yoga":
                radioGroupType.check(R.id.radioFamilyYoga);
                break;
            case "Aerial Yoga":
                radioGroupType.check(R.id.radioAerialYoga);
                break;
        }

        // Chọn Ngày trong tuần với AlertDialog
        edtDayOfWeek.setOnClickListener(v -> {
            AlertDialog.Builder dayBuilder = new AlertDialog.Builder(getContext());
            dayBuilder.setTitle("Chọn ngày trong tuần");

            dayBuilder.setMultiChoiceItems(daysOfWeek, selectedDays, (dialog, which, isChecked) -> {
                selectedDays[which] = isChecked; // Lưu trạng thái chọn
            });

            dayBuilder.setPositiveButton("OK", (dialog1, which) -> {
                StringBuilder selectedDaysString = new StringBuilder();
                for (int i = 0; i < selectedDays.length; i++) {
                    if (selectedDays[i]) {
                        if (selectedDaysString.length() > 0) {
                            selectedDaysString.append(", ");
                        }
                        selectedDaysString.append(daysOfWeek[i]);
                    }
                }
                edtDayOfWeek.setText(selectedDaysString.toString());
            });

            dayBuilder.setNegativeButton("Cancel", null);
            dayBuilder.show();
        });

        // Chọn thời gian với TimePickerDialog
        edtTime.setOnClickListener(v -> {
            TimePickerDialog startTimePicker = new TimePickerDialog(getContext(), (view1, hourOfDay, minute) -> {
                String startTime = String.format("%02d:%02d", hourOfDay, minute);

                TimePickerDialog endTimePicker = new TimePickerDialog(getContext(), (view2, endHourOfDay, endMinute) -> {
                    String endTime = String.format("%02d:%02d", endHourOfDay, endMinute);
                    edtTime.setText(startTime + " - " + endTime);
                }, 0, 0, true);

                endTimePicker.show();
            }, 0, 0, true);

            startTimePicker.show();
        });

        // Thêm "persons" vào trường Capacity với TextWatcher
        edtCapacity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString()) && !s.toString().endsWith(" persons")) {
                    edtCapacity.setText(s.toString() + " persons");
                    edtCapacity.setSelection(edtCapacity.getText().length() - " persons".length());
                }
            }
        });

        // Thêm ký hiệu "$" cho trường Price với TextWatcher
        edtPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString()) && !s.toString().startsWith("$")) {
                    edtPrice.setText("$" + s.toString());
                    edtPrice.setSelection(edtPrice.getText().length());
                }
            }
        });

        builder.setPositiveButton("Update", (dialog, which) -> {
            // Handle course update
            String name = edtName.getText().toString().trim();
            String dayOfWeek = edtDayOfWeek.getText().toString().trim();
            String time = edtTime.getText().toString().trim();
            String capacityStr = edtCapacity.getText().toString().trim();
            String durationStr = edtDuration.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();

            // Lấy loại khóa học từ RadioGroup
            int selectedTypeId = radioGroupType.getCheckedRadioButtonId();
            String type = "";
            if (selectedTypeId == R.id.radioFlowYoga) {
                type = "Flow Yoga";
            } else if (selectedTypeId == R.id.radioFamilyYoga) {
                type = "Family Yoga";
            } else if (selectedTypeId == R.id.radioAerialYoga) {
                type = "Aerial Yoga";
            }

            // Validate input
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(dayOfWeek) || TextUtils.isEmpty(time) ||
                    TextUtils.isEmpty(capacityStr) || TextUtils.isEmpty(durationStr) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(type)) {
                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Parse numeric fields
            int capacity = Integer.parseInt(capacityStr.replace(" persons", "").trim());
            int duration = Integer.parseInt(durationStr);
            double price = Double.parseDouble(priceStr.replace("$", "").trim());

            // Update course in database
            course.setName(name);
            course.setDayOfWeek(dayOfWeek);
            course.setTime(time);
            course.setCapacity(capacity);
            course.setDuration(duration);
            course.setPrice(price);
            course.setType(type);
            course.setDescription(description);
            databaseHelper.updateCourse(course);

            Toast.makeText(getActivity(), "Course updated successfully!", Toast.LENGTH_SHORT).show();
            loadCourses(); // Reload the list after updating the course
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }
}
