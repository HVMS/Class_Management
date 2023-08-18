package com.globalitians.employees.Homework.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.globalitians.employees.Homework.model.HomeWorkDetailsModelClass;
import com.globalitians.employees.R;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;

import java.util.ArrayList;

public class AssignedHomeWorkStudentAdapter extends RecyclerView.Adapter<AssignedHomeWorkStudentAdapter.MyViewHolder>{

    private Activity activity;
    private HomeWorkDetailsModelClass.BatchDetail.Student homeWorkDetailsModelClass;
    private ArrayList<HomeWorkDetailsModelClass.BatchDetail.Student> mAlhwlist;
    private OnHomeWorkStudentRemove onHomeWorkStudentRemove;

    public AssignedHomeWorkStudentAdapter(Activity activity, ArrayList<HomeWorkDetailsModelClass.BatchDetail.Student> mAlhwlist, OnHomeWorkStudentRemove onHomeWorkStudentRemove) {
        this.activity = activity;
        this.mAlhwlist = mAlhwlist;
        this.onHomeWorkStudentRemove = onHomeWorkStudentRemove;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assigned_student_layout, parent, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try{
            homeWorkDetailsModelClass = mAlhwlist.get(position);
            setHomeWorkDetails(position,holder,homeWorkDetailsModelClass);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setHomeWorkDetails(int position, MyViewHolder holder, HomeWorkDetailsModelClass.BatchDetail.Student homeWorkDetailsModelClass) {
        holder.mTxtstudentName.setText(""+homeWorkDetailsModelClass.getFname()+" "+homeWorkDetailsModelClass.getLname());
        holder.mIvclose.setImageResource(R.drawable.cancel_24px);
        if(!CommonUtil.isNullString(""+homeWorkDetailsModelClass.getImage())){
            CommonUtil.setCircularImageForUser(activity,holder.mIvstudentImage,""+homeWorkDetailsModelClass.getImage());
        }else{
            holder.mIvstudentImage.setImageResource(R.drawable.ic_user_round);
        }
    }

    @Override
    public int getItemCount() {
        return mAlhwlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CircularImageView mIvstudentImage;
        private CustomTextView mTxtstudentName;
        private ImageView mIvclose;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mIvstudentImage = itemView.findViewById(R.id.iv_student);
            mTxtstudentName = itemView.findViewById(R.id.tv_actual_student_name);
            mIvclose = itemView.findViewById(R.id.iv_close);

            mIvclose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onHomeWorkStudentRemove.onRemoveStudent(getAdapterPosition());
                }
            });
        }
    }

    public interface OnHomeWorkStudentRemove{
        void onRemoveStudent(int position);
    }

}
