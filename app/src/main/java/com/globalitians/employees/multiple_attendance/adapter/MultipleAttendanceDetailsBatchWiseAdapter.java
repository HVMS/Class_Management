package com.globalitians.employees.multiple_attendance.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalitians.employees.R;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.multiple_attendance.model.MultipleAttendanceDetailsModelClass;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;

import java.util.ArrayList;

public class MultipleAttendanceDetailsBatchWiseAdapter extends RecyclerView.Adapter<MultipleAttendanceDetailsBatchWiseAdapter.MyViewHolder> {

    private Activity activity;
    private ArrayList<MultipleAttendanceDetailsModelClass.BatchDetail.Student> mAlstduentslist;
    private onStudentItemClickEvent onStudentItemClick;
    private MultipleAttendanceDetailsModelClass.BatchDetail.Student attendanceDetailsModelClass;

    public MultipleAttendanceDetailsBatchWiseAdapter(Activity activity, ArrayList<MultipleAttendanceDetailsModelClass.BatchDetail.Student> mAlstduentslist, onStudentItemClickEvent onStudentItemClick) {
        this.activity = activity;
        this.mAlstduentslist = mAlstduentslist;
        this.onStudentItemClick = onStudentItemClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.multiple_batch_wise_students_attendance_item_layout, parent, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try{
           attendanceDetailsModelClass = mAlstduentslist.get(position);
           setAttendanceDetails(position,holder,attendanceDetailsModelClass);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setAttendanceDetails(int position, MyViewHolder holder, MultipleAttendanceDetailsModelClass.BatchDetail.Student attendanceDetailsModelClass) {
        if(!CommonUtil.isNullString(""+attendanceDetailsModelClass.getImage())){
            CommonUtil.setCircularImageToGlide(activity,holder.mIvstudentimage,""+attendanceDetailsModelClass.getImage());
        }else{
            holder.mIvstudentimage.setImageResource(R.drawable.ic_user_round);
        }

        holder.mTxtstudentname.setText(""+attendanceDetailsModelClass.getFirstName()+" "+attendanceDetailsModelClass.getLastName());

        if(!CommonUtil.isNullString(""+attendanceDetailsModelClass.getAttStatus()) &&
           attendanceDetailsModelClass.getAttStatus().equalsIgnoreCase("P")){
            holder.mTxtpresentabsentstatus.setText("Present");
            holder.mTxtpresentabsentstatus.setTextColor(activity.getResources().getColor(R.color.my_blue));
            holder.mTxtintimetitle.setVisibility(View.VISIBLE);
            holder.mTxtstudentIntime.setVisibility(View.VISIBLE);
            holder.mTxtstudentIntime.setText(""+attendanceDetailsModelClass.getAttIntime());
        }else{
            holder.mTxtintimetitle.setVisibility(View.GONE);
            holder.mTxtstudentIntime.setVisibility(View.GONE);
            holder.mTxtpresentabsentstatus.setText("Absent");
            holder.mTxtpresentabsentstatus.setTextColor(activity.getResources().getColor(R.color.my_absent));
        }
    }

    @Override
    public int getItemCount() {
        return mAlstduentslist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CircularImageView mIvstudentimage;
        private CustomTextView mTxtstudentname;
        private CustomTextView mTxtintimetitle;
        private CustomTextView mTxtstudentIntime;
        private CustomTextView mTxtpresentabsentstatus;
        private View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mIvstudentimage = itemView.findViewById(R.id.iv_student);
            mTxtstudentname = itemView.findViewById(R.id.tv_student_name);
            mTxtintimetitle = itemView.findViewById(R.id.tv_in);
            mTxtstudentIntime = itemView.findViewById(R.id.tv_student_in_time);
            mTxtpresentabsentstatus = itemView.findViewById(R.id.tv_student_attendance_status);
            view = itemView.findViewById(R.id.line);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onStudentItemClick.onStudentClick(getAdapterPosition());
                }
            });
        }
    }

    public interface onStudentItemClickEvent{
        void onStudentClick(int position);
    }
}
