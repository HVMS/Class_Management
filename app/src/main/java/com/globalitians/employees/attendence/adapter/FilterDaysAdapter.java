package com.globalitians.employees.attendence.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.attendence.model.FilterDaysModel;
import com.globalitians.employees.attendence.model.ModelAttendanceList;
import com.globalitians.employees.utility.CommonUtil;

import java.util.ArrayList;

public class FilterDaysAdapter extends RecyclerView.Adapter<FilterDaysAdapter.MyViewHolder> {
    private ArrayList<FilterDaysModel> mAlFilterDays;
    private Activity mActivity;
    private FitlterDayListener mFilterDayListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvWeekName;
        private TextView tvDayOfDate;


        public MyViewHolder(View view) {
            super(view);
            tvWeekName = view.findViewById(R.id.tv_weekname);
           // tvDayOfDate= view.findViewById(R.id.tvDay);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFilterDayListener.onFilterDayClick(getAdapterPosition());
                }
            });
        }
    }

    public FilterDaysAdapter(Activity activity,
                             ArrayList<FilterDaysModel> listFilterDays,
                             FitlterDayListener filterDayListener) {
        this.mActivity = activity;
        this.mAlFilterDays = listFilterDays;
        this.mFilterDayListener = filterDayListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_filter_day, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            FilterDaysModel modelFilterDays = mAlFilterDays.get(position);

            //holder.tv.setText(""+modelStudentAttendance.getDate());



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return 7;
    }

    public interface FitlterDayListener {
        void onFilterDayClick(int position);
    }
}
