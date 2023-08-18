package com.globalitians.employees.filters.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.filters.models.FilterModelStudentTypes;

import java.util.ArrayList;

public class FilterStudentTypeListAdapter extends BaseAdapter {

    private ArrayList<FilterModelStudentTypes> mArrListStudentTypes;
    private Activity mContext;
    private LayoutInflater mInflater;
    private ViewHolder holder;
    private OnStudentTypeClickListener mStudentTypeClickListener;

    public FilterStudentTypeListAdapter(Activity context, ArrayList<FilterModelStudentTypes> arrListMonths, OnStudentTypeClickListener mStudentTypeClickListener) {
        mContext = context;
        mArrListStudentTypes = arrListMonths;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mStudentTypeClickListener = mStudentTypeClickListener;
    }

    @Override
    public int getCount() {
        return mArrListStudentTypes.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrListStudentTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = mInflater.inflate(R.layout.view_filter_month_list, null);

            holder = new ViewHolder();
            holder.mTvFiterTypes = view.findViewById(R.id.tv_filter_month);
            holder.mRelTypes = view.findViewById(R.id.rel_month);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        setData(position, holder, mArrListStudentTypes.get(position));
        return view;
    }


    private void setData(final int position, final ViewHolder holder, final FilterModelStudentTypes data) {
        holder.mTvFiterTypes.setText("" + data.getStrStudentType());
        if (data.isSelected() == true) {
            holder.mTvFiterTypes.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
            holder.mRelTypes.setBackgroundColor(mContext.getResources().getColor(R.color.colorThemeBlackTransperent));
        } else {
            holder.mTvFiterTypes.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
            holder.mRelTypes.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
        }

        holder.mRelTypes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.isSelected()==true)
                {
                    mStudentTypeClickListener.onStudentTypesRawClick(position, false);
                }else{
                    mStudentTypeClickListener.onStudentTypesRawClick(position, true);
                }

            }
        });
    }

    static class ViewHolder {
        private TextView mTvFiterTypes;
        private RelativeLayout mRelTypes;
    }

    public interface OnStudentTypeClickListener {
        void onStudentTypesRawClick(int position, boolean isSelected);
    }
}
