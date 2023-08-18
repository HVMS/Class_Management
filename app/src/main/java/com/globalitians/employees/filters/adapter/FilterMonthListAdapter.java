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
import com.globalitians.employees.filters.models.FilterModelMonths;

import java.util.ArrayList;

public class FilterMonthListAdapter extends BaseAdapter {

    private ArrayList<FilterModelMonths> mArrListMonthList;
    private Activity mContext;
    private LayoutInflater mInflater;
    private ViewHolder holder;
    private OnMonthListListener mMonthListClickListener;
    private String strFrom = "";

    public FilterMonthListAdapter(Activity context, ArrayList<FilterModelMonths> arrListMonths, OnMonthListListener mMonthListClickListener) {
        mContext = context;
        mArrListMonthList = arrListMonths;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mMonthListClickListener = mMonthListClickListener;
        this.strFrom = strFrom;
    }

    @Override
    public int getCount() {
        return mArrListMonthList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrListMonthList.get(position);
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
            holder.mTvFiterMonth = view.findViewById(R.id.tv_filter_month);
            holder.mRelMonth = view.findViewById(R.id.rel_month);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        setData(position, holder, mArrListMonthList.get(position));
        return view;
    }


    private void setData(final int position, final ViewHolder holder, final FilterModelMonths data) {
        holder.mTvFiterMonth.setText("" + data.getStrMonth() + ", " + data.getStrYear());
        if (data.isSelected() == true) {
            holder.mTvFiterMonth.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
            holder.mRelMonth.setBackgroundColor(mContext.getResources().getColor(R.color.colorThemeBlackTransperent));
        } else {
            holder.mTvFiterMonth.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
            holder.mRelMonth.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
        }
        holder.mRelMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.isSelected()==true)
                {
                    mMonthListClickListener.onMonthRawClick(position, false);
                }else{
                    mMonthListClickListener.onMonthRawClick(position, true);
                }

            }
        });
    }

    static class ViewHolder {
        private TextView mTvFiterMonth;
        private RelativeLayout mRelMonth;
    }

    public interface OnMonthListListener {
        public void onMonthRawClick(int position, boolean isSelected);
    }
}
