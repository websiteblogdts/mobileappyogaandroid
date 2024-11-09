package com.example.adminjava;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<CourseModel> courseList;

    private OnCourseClickListener onCourseClickListener;


    public CourseAdapter(List<CourseModel> courseList, OnCourseClickListener onCourseClickListener) {
        this.courseList = courseList;
        this.onCourseClickListener = onCourseClickListener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        CourseModel course = courseList.get(position);
        holder.txtCourseName.setText(course.getName());
        holder.txtCourseDay.setText(course.getDayOfWeek());
        holder.txtCourseTime.setText(course.getTime());

        // Set click listeners for Edit and Delete buttons
        holder.btnEditCourse.setOnClickListener(v -> onCourseClickListener.onCourseEdit(course));
        holder.btnDeleteCourse.setOnClickListener(v -> onCourseClickListener.onCourseDelete(course));

        holder.itemView.setOnClickListener(v -> {

                    ((MainActivity) holder.itemView.getContext()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ClassListFragment.newInstance(Long.parseLong(course.getId()))) // Nếu course.getId() trả về long
                .addToBackStack(null)
                .commit();
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {

        TextView txtCourseName, txtCourseDay, txtCourseTime;
        Button btnEditCourse, btnDeleteCourse;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCourseName = itemView.findViewById(R.id.txtCourseName);
            txtCourseDay = itemView.findViewById(R.id.txtCourseDay);
            txtCourseTime = itemView.findViewById(R.id.txtCourseTime);
            btnEditCourse = itemView.findViewById(R.id.btn_edit_course); // Ensure you have this ID in your XML layout
            btnDeleteCourse = itemView.findViewById(R.id.btn_delete_course); // Ensure you have this ID in your XML layout
        }
    }

    // Define interface for handling click events
    public interface OnCourseClickListener {
        void onCourseEdit(CourseModel courseModel);
        void onCourseDelete(CourseModel courseModel);

    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<CourseModel> newCourses) {
        this.courseList.clear();
        this.courseList.addAll(newCourses);
        notifyDataSetChanged(); // Cập nhật UI
    }

}
