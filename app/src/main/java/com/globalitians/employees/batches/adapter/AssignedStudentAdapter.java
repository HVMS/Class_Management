package com.globalitians.employees.batches.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.globalitians.employees.R;
import com.globalitians.employees.batches.model.BatchDetailsModel;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;

import java.util.ArrayList;

public class AssignedStudentAdapter extends RecyclerView.Adapter<AssignedStudentAdapter.MyViewHolder> {

    private Activity activity;
    private ArrayList<BatchDetailsModel.BatchDetail.Student> mArrliststduents;
    private BatchDetailsModel.BatchDetail.Student studentsModel;
    private OnCloseImageClickListner onCloseImageClickListner;

    public AssignedStudentAdapter(Activity activity, ArrayList<BatchDetailsModel.BatchDetail.Student> mArrliststduents, OnCloseImageClickListner onCloseImageClickListner) {
        this.activity = activity;
        this.mArrliststduents = mArrliststduents;
        this.onCloseImageClickListner = onCloseImageClickListner;
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
        try {
            studentsModel = mArrliststduents.get(position);
            setStudentData(position,holder,studentsModel);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setStudentData(int position, MyViewHolder holder, BatchDetailsModel.BatchDetail.Student studentsModel) {
        holder.mTxtstudentName.setText(""+studentsModel.getFirstName()+" "+studentsModel.getLastName());
        if(!CommonUtil.isNullString(""+studentsModel.getImage())){
            CommonUtil.setCircularImageForUser(activity,holder.mIvstudentImage,""+studentsModel.getImage());
        }else{
            holder.mIvstudentImage.setImageResource(R.drawable.ic_user_round);
        }
        holder.mIvclose.setImageResource(R.drawable.cancel_24px);
    }

    @Override
    public int getItemCount() {
        return mArrliststduents.size();
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
                    onCloseImageClickListner.onRemoveStudent(getAdapterPosition());
                }
            });
        }
    }

    public interface OnCloseImageClickListner{
        void onRemoveStudent(int position);
    }

}
