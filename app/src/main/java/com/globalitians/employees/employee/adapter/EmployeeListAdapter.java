package com.globalitians.employees.employee.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.courses.model.ModelCourseCategoriesList;
import com.globalitians.employees.employee.model.ModelClassForEmployeeList;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;

import java.util.ArrayList;

public class EmployeeListAdapter extends RecyclerView.Adapter<EmployeeListAdapter.MyViewHolder> {

    private ArrayList<ModelClassForEmployeeList.Employee> mAlemployeelist;
    private Activity mActivity;
    private EmployeeClick employeeClick;
    private ModelClassForEmployeeList.Employee modelClassforemployee;

    public EmployeeListAdapter(Activity mActivity,ArrayList<ModelClassForEmployeeList.Employee> mAlemployeelist, EmployeeClick employeeClick) {
        this.mActivity = mActivity;
        this.mAlemployeelist = mAlemployeelist;
        this.employeeClick = employeeClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_employee_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try{
            modelClassforemployee = mAlemployeelist.get(position);
            setEmployeelistTolistview(position,holder,modelClassforemployee);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setEmployeelistTolistview(int position, MyViewHolder holder, ModelClassForEmployeeList.Employee modelClassforemployee) {
        if(!CommonUtil.isNullString(""+modelClassforemployee.getName())){
            holder.tvEmployeeName.setText(""+modelClassforemployee.getName());
        }else{
            holder.tvEmployeeName.setText("By Default");
        }

        if(!CommonUtil.isNullString(""+modelClassforemployee.getImage())){
            CommonUtil.setCircularImageForUser(mActivity,holder.ivEmployeeProfile,""+modelClassforemployee.getImage());
        }else{
            holder.ivEmployeeProfile.setImageResource(R.drawable.ic_user_round);
        }
    }


    @Override
    public int getItemCount() {
        return mAlemployeelist.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvEmployeeName;
        private CircularImageView ivEmployeeProfile;
        private LinearLayout linMainContainer;
        private ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            linMainContainer = itemView.findViewById(R.id.linMainContainer);
            tvEmployeeName = itemView.findViewById(R.id.tv_employee_name);
            ivEmployeeProfile = itemView.findViewById(R.id.iv_employee);
            imageView = itemView.findViewById(R.id.iv_more_options);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    employeeClick.onMoreOptionsClick(getAdapterPosition(),imageView);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    employeeClick.onEmployeeListClick(getAdapterPosition());
                }
            });

        }
    }

    public interface EmployeeClick{
        void onEmployeeListClick(int position);
        void onMoreOptionsClick(int position, ImageView mIvmoreoptions);
    }
}
