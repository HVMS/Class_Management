package com.globalitians.employees.multiple_attendance.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalitians.employees.R;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.multiple_attendance.model.MultipleAttendanceDetailsModelClass;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;

import java.util.ArrayList;

public class MultipleStudentAttendanceAdapter extends RecyclerView.Adapter<MultipleStudentAttendanceAdapter.MyViewHolder> {

    private Activity activity;
    private ArrayList<MultipleAttendanceDetailsModelClass.BatchDetail.Student> mAlstudentlist;
    private OnOffSwitchEvent onOffSwitchEvent;
    private MultipleAttendanceDetailsModelClass.BatchDetail.Student modelStudent;

    public MultipleStudentAttendanceAdapter(Activity activity, ArrayList<MultipleAttendanceDetailsModelClass.BatchDetail.Student> mAlstudentlist, OnOffSwitchEvent onOffSwitchEvent) {
        this.activity = activity;
        this.mAlstudentlist = mAlstudentlist;
        this.onOffSwitchEvent = onOffSwitchEvent;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.take_student_attendance_layout, parent, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try{
            modelStudent = mAlstudentlist.get(position);
            setStudentData(position,holder,modelStudent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setStudentData(final int position, final MyViewHolder holder, MultipleAttendanceDetailsModelClass.BatchDetail.Student modelStudent) {
        holder.mTxtStudentName.setText(""+modelStudent.getFirstName()+" "+modelStudent.getLastName());

        if(!CommonUtil.isNullString(""+modelStudent.getImage())){
            CommonUtil.setCircularImageForUser(activity,holder.circleImageView,""+modelStudent.getImage());
        }else{
            holder.circleImageView.setImageResource(R.drawable.ic_user_round);
        }

        holder.switchCompat.setChecked(mAlstudentlist.get(position).isSelected());
        holder.switchCompat.setTag(position);
        holder.switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer pos = (Integer) holder.switchCompat.getTag();

                /*Toast.makeText(activity, "Position clicked is : "+mAlstudentlist.get(pos).getFirstName(), Toast.LENGTH_SHORT).show();*/

                if(mAlstudentlist.get(pos).isSelected()){
                    mAlstudentlist.get(pos).setSelected(false);
                    holder.switchCompat.setTextOff("A");
                }else{
                    mAlstudentlist.get(pos).setSelected(true);
                    holder.switchCompat.setTextOn("P");
                }

                onOffSwitchEvent.onSwitchClickEvent(position,mAlstudentlist.get(position).isSelected());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mAlstudentlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CustomTextView mTxtStudentName;
        private CircularImageView circleImageView;
        private SwitchCompat switchCompat;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mTxtStudentName = itemView.findViewById(R.id.tv_student_name);
            circleImageView = itemView.findViewById(R.id.iv_student);
            switchCompat = itemView.findViewById(R.id.onoffassign);
            /*switchCompat.setTextOff("A");*/
        }
    }

    public interface OnOffSwitchEvent{
        void onSwitchClickEvent(int position, boolean value);
    }

}
