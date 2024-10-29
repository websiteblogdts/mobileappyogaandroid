package com.example.adminjava;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchFragment extends Fragment {

    private EditText etSearchTeacher;
    private Button btnSearch;
    private TextView tvEmptyMessage; // Thêm TextView để hiển thị thông báo "Trống"

    private RecyclerView rvClassList;

    private SearchAdapter searchAdapter;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_name, container, false);

        // Khởi tạo các thành phần giao diện
        etSearchTeacher = view.findViewById(R.id.et_search_teacher);
        btnSearch = view.findViewById(R.id.btn_search);
        rvClassList = view.findViewById(R.id.rv_class_list);
        tvEmptyMessage = view.findViewById(R.id.tv_empty_message); // Khởi tạo TextView

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(getContext());

        // Thiết lập RecyclerView
        rvClassList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo SearchAdapter
        // Khởi tạo SearchAdapter với danh sách rỗng
        searchAdapter = new SearchAdapter(new ArrayList<>(), new SearchAdapter.OnClassClickListener() {
            @Override
            public void onClassEdit(ClassModel classModel) {
                // Xử lý chỉnh sửa lớp học
                Toast.makeText(getContext(), "Edit class: " + classModel.getTeacherName(), Toast.LENGTH_SHORT).show();
            }

            public void onClassDelete(ClassModel classModel) {
                // Handle course delete
                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete Course")
                        .setMessage("Are you sure you want to delete this course?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            dbHelper.deleteClass(Long.parseLong(classModel.getId()));
                            searchClasses();
                            Toast.makeText(getActivity(), "Course deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        rvClassList.setAdapter(searchAdapter); // Đặt SearchAdapter cho RecyclerView

        // Xử lý sự kiện khi nhấn nút "Search"
        btnSearch.setOnClickListener(v -> searchClasses());

        return view;
    }

    // Hàm để thực hiện tìm kiếm
    private void searchClasses() {
        String teacherName = etSearchTeacher.getText().toString().trim();

        // Kiểm tra nếu tên giáo viên chưa được nhập
        if (TextUtils.isEmpty(teacherName)) {
            Toast.makeText(getContext(), "Please enter teacher name", Toast.LENGTH_SHORT).show();
            return;
        }
        // Thực hiện tìm kiếm trong cơ sở dữ liệu
        List<ClassModel> classList = dbHelper.searchClassesByTeacherName(teacherName);

        // Kiểm tra nếu không có lớp nào được tìm thấy
        if (classList.isEmpty()) {
            Toast.makeText(getContext(), "No classes found for teacher: " + teacherName, Toast.LENGTH_SHORT).show();
            rvClassList.setVisibility(View.GONE); // Ẩn RecyclerView
            tvEmptyMessage.setVisibility(View.VISIBLE); // Hiện thông báo "Trống"
        } else {
            // Cập nhật danh sách lớp trong RecyclerView
            searchAdapter.updateData(classList); // Cập nhật dữ liệu mới vào SearchAdapter
            rvClassList.setVisibility(View.VISIBLE); // Hiện RecyclerView
            tvEmptyMessage.setVisibility(View.GONE); // Ẩn thông báo "Trống"
        }
    }
}
