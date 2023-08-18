package com.globalitians.employees.multiple_attendance.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalitians.employees.R;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.multiple_attendance.model.MultipleDatewisestudentsattendancemodel;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;

import java.util.ArrayList;

public class MultipleDateWiseStudentListAdapter extends RecyclerView.Adapter<MultipleDateWiseStudentListAdapter.MyViewHolder> {

    private Activity activity;
    private ArrayList<MultipleDatewisestudentsattendancemodel.StudentAttendence> mAlstudentslist;
    private MultipleDatewisestudentsattendancemodel.StudentAttendence datewisestudentsattendancemodel;
    private OnDateWiseStudentClick onDateWiseStudentClick;

    public MultipleDateWiseStudentListAdapter(Activity activity, ArrayList<MultipleDatewisestudentsattendancemodel.StudentAttendence> mAlstudentslist, OnDateWiseStudentClick onDateWiseStudentClick) {
        this.activity = activity;
        this.mAlstudentslist = mAlstudentslist;
        this.onDateWiseStudentClick = onDateWiseStudentClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(activity).inflate(R.layout.multiple_date_wise_students_attendance_layout,parent,false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try{
            datewisestudentsattendancemodel = mAlstudentslist.get(position);
            setDataOfStudents(position,holder,datewisestudentsattendancemodel);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setDataOfStudents(int position, MyViewHolder holder, MultipleDatewisestudentsattendancemodel.StudentAttendence datewisestudentsattendancemodel) {
        if(!CommonUtil.isNullString(""+datewisestudentsattendancemodel.getStudentImage())){
            CommonUtil.setCircularImageToGlide(activity,holder.mIvstudentimage,""+datewisestudentsattendancemodel.getStudentImage());
        }else{
            holder.mIvstudentimage.setImageResource(R.drawable.ic_user_round);
        }

        holder.mTxtstudentname.setText(""+datewisestudentsattendancemodel.getStudentName());
        holder.mTxtstudentbatchname.setText(""+datewisestudentsattendancemodel.getBatchName());

        if(!CommonUtil.isNullString(""+datewisestudentsattendancemodel.getStatus())  &&
            datewisestudentsattendancemodel.getStatus().equalsIgnoreCase("P")){
            holder.mTxtintimetitle.setText("IN");
            holder.mTxtintimetitle.setTextColor(activity.getResources().getColor(R.color.my_green));
            holder.mTxtstudentIntime.setText(""+datewisestudentsattendancemodel.getIn());
        }else{
            holder.mTxtintimetitle.setText("Absent");
            holder.mTxtintimetitle.setTextColor(activity.getResources().getColor(R.color.my_absent));
            holder.mTxtstudentIntime.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return mAlstudentslist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CircularImageView mIvstudentimage;
        private CustomTextView mTxtstudentname;
        private CustomTextView mTxtintimetitle;
        private CustomTextView mTxtstudentIntime;
        private CustomTextView mTxtstudentbatchname;
        private CustomTextView mTxtpresentabsentstatus;
        private View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mIvstudentimage = itemView.findViewById(R.id.iv_student);
            mTxtstudentname = itemView.findViewById(R.id.tv_student_name);
            mTxtintimetitle = itemView.findViewById(R.id.tv_in);
            mTxtstudentbatchname = itemView.findViewById(R.id.tv_batch_alias_actual_name);
            mTxtstudentIntime = itemView.findViewById(R.id.tv_student_in_time);
            mTxtpresentabsentstatus = itemView.findViewById(R.id.tv_student_attendance_status);
            view = itemView.findViewById(R.id.line);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDateWiseStudentClick.onStudentClick(getAdapterPosition());
                }
            });

        }
    }

    public interface OnDateWiseStudentClick{
        void onStudentClick(int position);
    }

}
