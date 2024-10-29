package com.example.adminjava;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class ClassListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ClassAdapter classAdapter;

    private DatabaseHelper databaseHelper;

    public static ClassListFragment newInstance(long courseId) {
        ClassListFragment fragment = new ClassListFragment();
        Bundle args = new Bundle();
        args.putLong("courseId", courseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewClasses);
        Button btnAddClass = view.findViewById(R.id.btnAddClass);

        databaseHelper = new DatabaseHelper(getActivity());

        // Load classes for the selected course
        long courseId = getArguments().getLong("courseId");
        loadClasses(courseId);

        btnAddClass.setOnClickListener(v -> {
            showAddClassDialog(courseId);
        });

        return view;
    }

    private void loadClasses(long courseId) {
        List<ClassModel> classList = databaseHelper.getClassesByCourseId(courseId);
        classAdapter = new ClassAdapter(classList, new ClassAdapter.OnClassClickListener() {
            @Override
            public void onClassEdit(ClassModel classModel) {
                showEditClassDialog(classModel);
            }

            @Override
            public void onClassDelete(ClassModel classModel) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete Class")
                        .setMessage("Are you sure you want to delete this Class?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            databaseHelper.deleteClass(Long.parseLong(classModel.getId()));
                            loadClasses(courseId);
                            Toast.makeText(getActivity(), "Class deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(classAdapter);
    }
    private void refreshClassList(long courseId) {
        List<ClassModel> classList = databaseHelper.getClassesByCourseId(courseId);
        if (!classList.isEmpty()) {
            if (classAdapter == null) {
                classAdapter = new ClassAdapter(classList, new ClassAdapter.OnClassClickListener() {
                    @Override
                    public void onClassEdit(ClassModel classModel) {
                        showEditClassDialog(classModel); // Gọi phương thức trong Fragment
                    }

                    @Override
                    public void onClassDelete(ClassModel classModel) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Xóa lớp học")
                                .setMessage("Bạn có chắc muốn xóa lớp học này không?")
                                .setPositiveButton("Xóa", (dialog, which) -> {
                                    databaseHelper.deleteClass(Long.parseLong(classModel.getId()));
                                    refreshClassList(Long.parseLong(classModel.getCourseId()));
                                    Toast.makeText(getActivity(), "Đã xóa lớp học", Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("Hủy", null)
                                .show();
                    }
                });
                recyclerView.setAdapter(classAdapter); // Gán adapter cho RecyclerView
            } else {
                classAdapter.updateData(classList); // Cập nhật dữ liệu trong adapter
            }
        }
    }

    public void showAddClassDialog(long courseId) {
        // Tạo một dialog để thêm lớp học
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_class, null);
        builder.setView(dialogView);

        // Tìm các view trong dialog
        EditText edtTeacherName = dialogView.findViewById(R.id.edtTeacherName);
        EditText edtDate = dialogView.findViewById(R.id.edtDate);
        EditText edtComments = dialogView.findViewById(R.id.edtComments);

        // Thêm logic cho nút "Add"
        builder.setPositiveButton("Add", (dialog, which) -> {
            // Thu thập thông tin nhập từ người dùng
            String teacherName = edtTeacherName.getText().toString();
            String date = edtDate.getText().toString();
            String comments = edtComments.getText().toString();

            // Chèn lớp học vào cơ sở dữ liệu
            long classId = databaseHelper.addClass(courseId, teacherName, date, comments);

            // Làm mới danh sách lớp học hoặc giao diện người dùng
            refreshClassList(courseId);

            Toast.makeText(getContext(), "Class added successfully!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void showEditClassDialog(final ClassModel classModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Chỉnh sửa lớp học");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_class, null);
        builder.setView(dialogView);

        // Khởi tạo các trường nhập liệu
        EditText edtTeacherName = dialogView.findViewById(R.id.edtTeacherName);
        EditText edtDate = dialogView.findViewById(R.id.edtDate);
        EditText edtComments = dialogView.findViewById(R.id.edtComments);

        // Điền sẵn thông tin vào các trường
        edtTeacherName.setText(classModel.getTeacherName());
        edtDate.setText(classModel.getDate());
        edtComments.setText(classModel.getComments());

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            // Thu thập thông tin nhập từ người dùng
            String teacherName = edtTeacherName.getText().toString().trim();
            String date = edtDate.getText().toString().trim();
            String comments = edtComments.getText().toString().trim();

            // Kiểm tra xem thông tin nhập có hợp lệ hay không
            if (teacherName.isEmpty() || date.isEmpty()) {
                Toast.makeText(getActivity(), "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return; // Thoát nếu thông tin không hợp lệ
            }

            // Cập nhật thông tin lớp học vào cơ sở dữ liệu
            int rowsAffected = databaseHelper.updateClass(
                    Long.parseLong(classModel.getId()),
                    Long.parseLong(classModel.getCourseId()),
                    teacherName, // Chỉ cập nhật tên giáo viên
                    date,
                    comments
            );

            // Làm mới danh sách lớp học và thông báo kết quả
            if (rowsAffected > 0) {
                Toast.makeText(getActivity(), "Lớp học đã được cập nhật!", Toast.LENGTH_SHORT).show();
                refreshClassList(Long.parseLong(classModel.getCourseId()));
            } else {
                Toast.makeText(getActivity(), "Không thể cập nhật lớp học!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
