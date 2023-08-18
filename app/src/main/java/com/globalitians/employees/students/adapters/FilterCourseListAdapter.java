package com.globalitians.employees.students.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.filters.models.FilterModelCourseList;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;

import java.util.ArrayList;

public class FilterCourseListAdapter extends BaseAdapter {

    private ArrayList<FilterModelCourseList.Course> mArrListCourseList;
    private Activity mContext;
    private LayoutInflater mInflater;
    private ViewHolder holder;
    private OnCourseListListener mCourseListClickListener;

    public FilterCourseListAdapter(Activity context, ArrayList<FilterModelCourseList.Course> arrListNewsFeedData, OnCourseListListener onCourseListListener) {
        mContext = context;
        mArrListCourseList = arrListNewsFeedData;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCourseListClickListener = onCourseListListener;
    }

    @Override
    public int getCount() {
        return mArrListCourseList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrListCourseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = mInflater.inflate(R.layout.view_filter_course_list, null);
            holder = new ViewHolder();
            holder.mTvCourseName = view.findViewById(R.id.tv_course_name);
            holder.mRelCourse = view.findViewById(R.id.rel_course);
            holder.ivCourse = view.findViewById(R.id.iv_course);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        setCourseListData(position, holder, mArrListCourseList.get(position));
        return view;
    }


    private void setCourseListData(final int position, final ViewHolder holder, final FilterModelCourseList.Course data) {
        holder.mTvCourseName.setText("" + data.getName());
        if(mArrListCourseList.get(position).isSelected()==true)
        {
            holder.mRelCourse.setBackgroundColor(mContext.getResources().getColor(R.color.colorThemeBlackTransperent));
            holder.mTvCourseName.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
        }else{
            holder.mRelCourse.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
            holder.mTvCourseName.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
        }

        holder.mRelCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.isSelected()==false)
                {
                    mCourseListClickListener.onCourseSelected(position,true);
                    holder.mRelCourse.setBackgroundColor(mContext.getResources().getColor(R.color.colorThemeBlackTransperent));
                    holder.mTvCourseName.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                }else{
                    mCourseListClickListener.onCourseSelected(position,false);
                    holder.mRelCourse.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
                    holder.mTvCourseName.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
                }
            }
        });
        CommonUtil.setCircularImageToGlide(mContext, holder.ivCourse, "" + mArrListCourseList.get(position).getImage());

    }

    static class ViewHolder {
        private TextView mTvCourseName;
        private RelativeLayout mRelCourse;
        private CircularImageView ivCourse;
    }

    public interface OnCourseListListener {
        void onCourseSelected(int position, boolean b);
    }
}
