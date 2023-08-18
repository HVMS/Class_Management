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

public class AssignedFacultyAdapter extends RecyclerView.Adapter<AssignedFacultyAdapter.MyViewHolder> {

    private Activity activity;
    private BatchDetailsModel.BatchDetail.Faculty facultyModel;
    private ArrayList<BatchDetailsModel.BatchDetail.Faculty> mArrlistfaculties;
    private OnFacultyCloseLsitner onFacultyCloseLsitner;

    public AssignedFacultyAdapter(Activity activity, ArrayList<BatchDetailsModel.BatchDetail.Faculty> mArrlistfaculties, OnFacultyCloseLsitner onFacultyCloseLsitner) {
        this.activity = activity;
        this.mArrlistfaculties = mArrlistfaculties;
        this.onFacultyCloseLsitner = onFacultyCloseLsitner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faculty_list_item, parent, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try{
            facultyModel = mArrlistfaculties.get(position);
            setDataToFacultyItems(position,holder,facultyModel);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setDataToFacultyItems(int position, MyViewHolder holder, BatchDetailsModel.BatchDetail.Faculty facultyModel) {
        holder.mTxtfacultyname.setText(""+facultyModel.getName());
        if(!CommonUtil.isNullString(""+facultyModel.getImage())){
            CommonUtil.setCircularImageForUser(activity,holder.mIvfacultyImage,""+facultyModel.getImage());
        }else{
            holder.mIvfacultyImage.setImageResource(R.drawable.ic_user_round);
        }
    }

    @Override
    public int getItemCount() {
        return mArrlistfaculties.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CircularImageView mIvfacultyImage;
        private CustomTextView mTxtfacultyname;
        private ImageView mIvclose;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mIvfacultyImage = itemView.findViewById(R.id.iv_faculty);
            mTxtfacultyname = itemView.findViewById(R.id.tv_actual_faculty_name);
            mIvclose = itemView.findViewById(R.id.iv_close);

            mIvclose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onFacultyCloseLsitner.onFacultyRemove(getAdapterPosition());
                }
            });

        }
    }

    public interface OnFacultyCloseLsitner{
        void onFacultyRemove(int position);
    }
}
