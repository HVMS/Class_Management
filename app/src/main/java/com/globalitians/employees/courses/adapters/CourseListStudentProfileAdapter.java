package com.globalitians.employees.courses.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.students.models.ModelStudentDetailsResponse;

import java.util.List;

public class CourseListStudentProfileAdapter extends RecyclerView.Adapter<CourseListStudentProfileAdapter.MyViewHolder> {
 
    private List<ModelStudentDetailsResponse.StudentDetail.Course> alCourses;
 
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCourseName;
 
        public MyViewHolder(View view) {
            super(view);
            tvCourseName = (TextView) view.findViewById(R.id.tv_course_name);
        }
    }
 
 
    public CourseListStudentProfileAdapter(List<ModelStudentDetailsResponse.StudentDetail.Course> moviesList) {
        this.alCourses = moviesList;
    }
 
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_list_student_profile, parent, false);
 
        return new MyViewHolder(itemView);
    }
 
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvCourseName.setText(alCourses.get(position).getName());
    }
 
    @Override
    public int getItemCount() {
        return alCourses.size();
    }
}