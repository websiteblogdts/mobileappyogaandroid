package com.example.adminjava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private List<ClassModel> classList;
    private OnClassClickListener listener;

    public SearchAdapter(List<ClassModel> classList, OnClassClickListener listener) {
        this.classList = classList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        ClassModel classModel = classList.get(position);
        holder.bind(classModel, listener);
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public void updateData(List<ClassModel> newClassList) {
        this.classList = newClassList;
        notifyDataSetChanged();
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {

        TextView txtClassName;
        Button btnEdit;
        Button btnDelete;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            txtClassName = itemView.findViewById(R.id.txtClassName);
            btnEdit = itemView.findViewById(R.id.btnUpdateClass);
            btnDelete = itemView.findViewById(R.id.btnDeleteClass);
        }

        public void bind(ClassModel classModel, OnClassClickListener listener) {
            txtClassName.setText(classModel.getTeacherName());
            btnEdit.setOnClickListener(v -> listener.onClassEdit(classModel));
            btnDelete.setOnClickListener(v -> listener.onClassDelete(classModel));
        }
    }

    public interface OnClassClickListener {
        void onClassEdit(ClassModel classModel);
        void onClassDelete(ClassModel classModel);
    }
}
