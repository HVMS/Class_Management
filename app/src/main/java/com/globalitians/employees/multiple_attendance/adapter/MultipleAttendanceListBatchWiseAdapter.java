package com.globalitians.employees.multiple_attendance.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalitians.employees.R;
import com.globalitians.employees.multiple_attendance.model.MultipleAttendanceListModelClass;
import com.globalitians.employees.customviews.CustomTextView;

import java.util.ArrayList;

public class MultipleAttendanceListBatchWiseAdapter extends RecyclerView.Adapter<MultipleAttendanceListBatchWiseAdapter.MyViewHolder> {

    private Activity activity;
    private ArrayList<MultipleAttendanceListModelClass.Batch> mAlattendanceList;
    private OnAttendanceListClickListner onAttendanceListClickListner;
    private MultipleAttendanceListModelClass.Batch attendanceListModelClass;

    public MultipleAttendanceListBatchWiseAdapter(Activity activity, ArrayList<MultipleAttendanceListModelClass.Batch> mAlattendanceList, OnAttendanceListClickListner onAttendanceListClickListner) {
        this.activity = activity;
        this.mAlattendanceList = mAlattendanceList;
        this.onAttendanceListClickListner = onAttendanceListClickListner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.batch_wise_attendance_list_items_layout,parent,false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try{
            attendanceListModelClass = mAlattendanceList.get(position);
            setAttendanceDetails(position,holder,attendanceListModelClass);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setAttendanceDetails(int position, MyViewHolder holder, MultipleAttendanceListModelClass.Batch attendanceListModelClass) {
        holder.mTxtbatchname.setText(""+attendanceListModelClass.getName());
        holder.mTxtaliasname.setText(""+attendanceListModelClass.getAlias());
        /*holder.mTxtcreateddate.setText(""+attendanceListModelClass.getCreatedAt());*/
        if(attendanceListModelClass.getIsAttandenceTaken()==true){
            holder.mTxtattendancestatus.setText("Taken");
            holder.mTxtattendancestatus.setTextColor(activity.getResources().getColor(R.color.my_green));
        }else{
            holder.mTxtattendancestatus.setText("Not Taken");
            holder.mTxtattendancestatus.setTextColor(activity.getResources().getColor(R.color.my_orange));
        }
    }

    @Override
    public int getItemCount() {
        return mAlattendanceList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CustomTextView mTxtbatchname;
        /*private CustomTextView mTxtaliastitle;*/
        private CustomTextView mTxtaliasname;
        private CustomTextView mTxtattendancestatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mTxtbatchname = itemView.findViewById(R.id.tv_batch_name);
            /*mTxtaliastitle = itemView.findViewById(R.id.tv_batch_alias);*/
            mTxtaliasname = itemView.findViewById(R.id.tv_batch_alias_actual_name);
            mTxtattendancestatus = itemView.findViewById(R.id.tv_attendance_status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onAttendanceListClickListner.onAttendanceItenClick(getAdapterPosition());
                }
            });

        }
    }

    public interface OnAttendanceListClickListner{
        void onAttendanceItenClick(int position);
    }

}
