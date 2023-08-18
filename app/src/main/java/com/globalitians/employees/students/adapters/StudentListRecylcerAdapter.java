package com.globalitians.employees.students.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.students.models.ModelStudent;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;

import java.util.ArrayList;

public class StudentListRecylcerAdapter extends RecyclerView.Adapter<StudentListRecylcerAdapter.MyViewHolder> {

    private ArrayList<ModelStudent.Student> mArrListStudentList;
    private Activity mContext;
    private LayoutInflater mInflater;
    private StudentListRecylcerAdapter.OnStudentListListener mStudentListClickListener;

    public StudentListRecylcerAdapter(Activity activity,ArrayList<ModelStudent.Student> mArrListStudentList ,OnStudentListListener onStudentListListener) {
        this.mContext = activity;
        this.mArrListStudentList = mArrListStudentList;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mStudentListClickListener = onStudentListListener;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircularImageView ivUser;
        private TextView tvStudentName;
        private TextView tvCourseName;
        private ImageView ivMoreOptions;

        public MyViewHolder(View view) {
            super(view);
            ivUser = (CircularImageView)view.findViewById( R.id.iv_user );
            tvStudentName = (TextView)view.findViewById( R.id.tv_student_name );
            tvCourseName = (TextView)view.findViewById( R.id.tv_course_name );
            ivMoreOptions=(ImageView) view.findViewById(R.id.iv_more_options);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStudentListClickListener.onStudentRawClick(getAdapterPosition());
                    //Toast.makeText(mContext, "clicked", Toast.LENGTH_SHORT).show();
                }
            });
            ivMoreOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStudentListClickListener.onMoreOptionClick(getAdapterPosition(),ivMoreOptions);
                }
            });
        }
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_student_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        try {
            ModelStudent.Student modelStudentList = mArrListStudentList.get(position);

            holder.tvStudentName.setText("" + modelStudentList.getFname()+" "+modelStudentList.getLname());
            holder.tvCourseName.setText(modelStudentList.getCourses().get(0).getName());

            CommonUtil.setCircularImageForUser(mContext,holder.ivUser,""+modelStudentList.getImage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mArrListStudentList.size();
    }

    public interface OnStudentListListener {
        void onStudentRawClick(int position);
        void onMoreOptionClick(int adapterPosition,ImageView ivMoreOptions);
    }
}
