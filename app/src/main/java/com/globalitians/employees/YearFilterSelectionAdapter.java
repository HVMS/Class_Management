package com.globalitians.employees;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.globalitians.employees.customviews.CustomCheckBox;

import java.util.ArrayList;

public class YearFilterSelectionAdapter extends RecyclerView.Adapter<YearFilterSelectionAdapter.MyViewHolder> {

    private ArrayList<YearModel> mArrListYears;
    private Activity mContext;
    private LayoutInflater mInflater;
    private YearClickListener mYearClickListener;

    public YearFilterSelectionAdapter(Activity activity,
                                      ArrayList<YearModel> mArrListYears,
                                      YearClickListener monthClickListener) {
        this.mContext = activity;
        this.mArrListYears = mArrListYears;

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mYearClickListener = monthClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CustomCheckBox mCbYearFilter;
        private TextView mTvYearName;

        public MyViewHolder(View view) {
            super(view);
            mCbYearFilter = view.findViewById(R.id.cb_month_filter);
            mCbYearFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mYearClickListener.onYearClick(getAdapterPosition(), isChecked);
                }
            });
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_filter_selection, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.mCbYearFilter.setChecked(mArrListYears.get(position).isSelected);
        holder.mCbYearFilter.setText("" + mArrListYears.get(position).strYearName);
    }

    @Override
    public int getItemCount() {
        return mArrListYears.size();
    }

    public interface YearClickListener {
        void onYearClick(int position, boolean isChecked);
    }
}
