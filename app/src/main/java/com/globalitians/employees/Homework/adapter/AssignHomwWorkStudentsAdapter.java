package com.globalitians.employees.Homework.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.globalitians.employees.R;
import com.globalitians.employees.batches.model.BatchDetailsModel;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;

import java.util.ArrayList;

public class AssignHomwWorkStudentsAdapter extends RecyclerView.Adapter<AssignHomwWorkStudentsAdapter.MyViewHolder> {

    private Activity activity;
    private ArrayList<BatchDetailsModel.BatchDetail.Student> mAlstudentlist;
    private BatchDetailsModel.BatchDetail.Student modelStudent;
    private OnAssignedHomeworkListner onAssignedHomeworkListner;

    public AssignHomwWorkStudentsAdapter(Activity activity, ArrayList<BatchDetailsModel.BatchDetail.Student> mAlstudentlist, OnAssignedHomeworkListner onAssignedHomeworkListner) {
        this.activity = activity;
        this.mAlstudentlist = mAlstudentlist;
        this.onAssignedHomeworkListner = onAssignedHomeworkListner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assign_student_items, parent, false);;
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try{
            modelStudent = mAlstudentlist.get(position);
            setStudentDetails(position,holder,modelStudent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setStudentDetails(final int position, final MyViewHolder holder, BatchDetailsModel.BatchDetail.Student modelStudent) {
        holder.mTxtstudentname.setText(""+modelStudent.getFirstName()+" "+modelStudent.getLastName());

        if(!CommonUtil.isNullString(""+modelStudent.getImage())){
            CommonUtil.setCircularImageForUser(activity,holder.mIvstudent,""+modelStudent.getImage());
        }else{
            holder.mIvstudent.setImageResource(R.drawable.ic_user_round);
        }

        holder.chkAssign.setChecked(mAlstudentlist.get(position).isSelected());
        holder.chkAssign.setTag(position);
        holder.chkAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer pos = (Integer) holder.chkAssign.getTag();

                if(mAlstudentlist.get(pos).isSelected()){
                    mAlstudentlist.get(pos).setSelected(false);
                }else{
                    mAlstudentlist.get(pos).setSelected(true);
                }

                onAssignedHomeworkListner.AddStudent(position,mAlstudentlist.get(position).isSelected());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAlstudentlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CustomTextView mTxtstudentname;
        private CircularImageView mIvstudent;
        private CheckBox chkAssign;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mTxtstudentname = itemView.findViewById(R.id.tv_student_name);
            mIvstudent = itemView.findViewById(R.id.iv_student);
            chkAssign = itemView.findViewById(R.id.chkAssign);

            chkAssign.setVisibility(View.VISIBLE);
        }
    }

    public interface OnAssignedHomeworkListner{
        void AddStudent(int position, boolean value);
    }

}
