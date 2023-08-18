package com.globalitians.employees.attendence.adapter;

import  android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.attendence.model.ModelAttendanceList;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;

import java.util.ArrayList;

public class AttendanceListSpecificAdapter extends RecyclerView.Adapter<AttendanceListSpecificAdapter.MyViewHolder> {
    private ArrayList<ModelAttendanceList.Attendence> mArrListAttendance;
    private Activity mActivity;
    private AttendanceClickListener mAttendanceClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircularImageView ivUser;
        private TextView tvStudentName;
        private TextView tvDateTime;
        private LinearLayout linMainContainer;

        public MyViewHolder(View view) {
            super(view);
            linMainContainer=view.findViewById(R.id.linMainContainer);
            ivUser = (CircularImageView) view.findViewById(R.id.iv_user);
            tvStudentName = (TextView) view.findViewById(R.id.tv_student_name);
            tvDateTime= (TextView) view.findViewById(R.id.tv_datetime);
        }
    }

    public AttendanceListSpecificAdapter(Activity activity, ArrayList<ModelAttendanceList.Attendence> listStudentOptions, AttendanceClickListener studentAttendanceClickListener) {
        this.mActivity = activity;
        this.mArrListAttendance = listStudentOptions;
        this.mAttendanceClickListener = studentAttendanceClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_attendance_in_out, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            ModelAttendanceList.Attendence modelStudentAttendance = mArrListAttendance.get(position);
            Log.e(">>",""+mArrListAttendance.get(position).getStatus());
            if(mArrListAttendance.get(position).getStatus().equalsIgnoreCase("in")){
                holder.linMainContainer.setBackgroundColor(Color.parseColor("#aa00ff00"));
                holder.tvStudentName.setTextColor(Color.BLACK);
                holder.tvDateTime.setTextColor(Color.BLACK);
            }else{
                holder.linMainContainer.setBackgroundColor(Color.parseColor("#aaff0000"));
                holder.tvStudentName.setTextColor(Color.WHITE);
                holder.tvDateTime.setTextColor(Color.WHITE);
            }

            holder.tvDateTime.setText(""+modelStudentAttendance.getDate());
            holder.tvStudentName.setText(""+modelStudentAttendance.getStudentName());
            CommonUtil.setImageToGlide(mActivity,holder.ivUser,""+modelStudentAttendance.getStudentImage());
            holder.linMainContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAttendanceClickListener.onAttendanceClick(position);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mArrListAttendance.size();
    }

    public interface AttendanceClickListener {
        void onAttendanceClick(int position);
    }
}
