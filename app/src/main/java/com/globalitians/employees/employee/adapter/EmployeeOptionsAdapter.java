package com.globalitians.employees.employee.adapter;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.employee.model.EmployeeOptionsModel;

import java.util.ArrayList;

public class EmployeeOptionsAdapter extends RecyclerView.Adapter<EmployeeOptionsAdapter.MyViewHolder> {
    private ArrayList<EmployeeOptionsModel> mArrListStudentOptions;
    private Activity mActivity;
    private EmployeeOptionClickListener mStudentOptionClickListener;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvOptionTitle;
        public ImageView ivOption,ivOptionCenter;
        public RelativeLayout relContainer;

        public MyViewHolder(View view) {
            super(view);
            tvOptionTitle = (TextView) view.findViewById(R.id.tv_student_option);
            ivOption = (ImageView) view.findViewById(R.id.iv_student_option);
            ivOptionCenter = (ImageView) view.findViewById(R.id.iv_student_option_center);
            relContainer=view.findViewById(R.id.rel_container);
        }
    }

    public EmployeeOptionsAdapter(Activity activity, ArrayList<EmployeeOptionsModel> listStudentOptions, EmployeeOptionClickListener studentOptionClickListener) {
        this.mActivity=activity;
        this.mArrListStudentOptions = listStudentOptions;
        this.mStudentOptionClickListener=studentOptionClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_employee_options, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try{
            EmployeeOptionsModel modelStudentOption = mArrListStudentOptions.get(position);

           /* switch(mArrListStudentOptions.get(position).getStrOptionTitle()){
                case "My Attendance":
                    holder.ivOption.setImageResource(modelStudentOption.getOptionImageId());
                    break;
            }*/
            holder.tvOptionTitle.setText(modelStudentOption.getStrOptionTitle());
            //holder.ivOption.setImageResource(modelStudentOption.getOptionImageId());
            //holder.relContainer.setBackgroundColor(Color.parseColor(modelStudentOption.getStrColor()));
            //holder.relContainer.setBackgroundColor(Color.parseColor(modelStudentOption.getStrColor()));
            //holder.relContainer.setBackgroundResource(modelStudentOption.getBackgroundImageId());
            holder.ivOption.setBackgroundResource(modelStudentOption.getBackgroundImageId());
            holder.ivOptionCenter.setBackgroundResource(modelStudentOption.getOptionImageId());
            holder.relContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStudentOptionClickListener.onEmployeeOptionClick(position);
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mArrListStudentOptions.size();
    }

    public interface EmployeeOptionClickListener {
        public void onEmployeeOptionClick(int position);
    }
}
