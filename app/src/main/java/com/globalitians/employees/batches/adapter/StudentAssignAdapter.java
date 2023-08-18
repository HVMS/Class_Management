package com.globalitians.employees.batches.adapter;

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
import com.globalitians.employees.students.models.ModelStudent;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;

import java.util.ArrayList;

public class StudentAssignAdapter extends RecyclerView.Adapter<StudentAssignAdapter.MyViewHolder> {

    private Activity activity;
    private ArrayList<BatchDetailsModel.BatchDetail.Student> mAlstudentlist;
    private BatchDetailsModel.BatchDetail.Student modelStudent;
    private OnCheckedStudentListner onCheckedStudentListner;

    public StudentAssignAdapter(Activity activity, ArrayList<BatchDetailsModel.BatchDetail.Student> mAlstudentlist, OnCheckedStudentListner onCheckedStudentListner) {
        this.activity = activity;
        this.mAlstudentlist = mAlstudentlist;
        this.onCheckedStudentListner = onCheckedStudentListner;
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
            setStudentDataToItem(position,holder,modelStudent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setStudentDataToItem(final int position, final MyViewHolder holder, final BatchDetailsModel.BatchDetail.Student modelStudent) {
        holder.mTxtstudentname.setText(""+modelStudent.getFirstName()+" "+modelStudent.getLastName());

        if(!CommonUtil.isNullString(""+modelStudent.getImage())){
            CommonUtil.setCircularImageForUser(activity,holder.mIvstudent,""+modelStudent.getImage());
        }else{
            holder.mIvstudent.setImageResource(R.drawable.ic_user_round);
        }

        holder.itemView.setSelected(mAlstudentlist.get(position).isSelected());

        /*holder.itemView.setTag(position);*/

        if(mAlstudentlist.get(position).isSelected()==false){
            holder.mIvstudentchecked.setVisibility(View.GONE);
            holder.itemView.setEnabled(true);
            holder.itemView.setAlpha((float) 1);
            mAlstudentlist.get(position).setSelected(false);
        }else if(mAlstudentlist.get(position).isSelected()==true){
            holder.mIvstudentchecked.setVisibility(View.VISIBLE);
            holder.itemView.setEnabled(false);
            holder.itemView.setAlpha((float) 0.50);
            mAlstudentlist.get(position).setSelected(true);
        }
    }

    @Override
    public int getItemCount() {
        return mAlstudentlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CustomTextView mTxtstudentname;
        private CircularImageView mIvstudent;
        private CheckBox chkAssign;
        private CircularImageView mIvstudentchecked;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mTxtstudentname = itemView.findViewById(R.id.tv_student_name);
            mIvstudent = itemView.findViewById(R.id.iv_student);
            chkAssign = itemView.findViewById(R.id.chkAssign);
            mIvstudentchecked = itemView.findViewById(R.id.iv_checked);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCheckedStudentListner.addStudent(getAdapterPosition(),true);
                }
            });

        }
    }

    public interface OnCheckedStudentListner{
        void addStudent(int position, boolean value);
    }
}
