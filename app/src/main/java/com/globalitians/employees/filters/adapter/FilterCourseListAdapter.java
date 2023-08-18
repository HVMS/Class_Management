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
import com.globalitians.employees.courses.model.ModelCourseList;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;

import java.util.ArrayList;

public class FilterCourseListAdapter extends BaseAdapter {

    private ArrayList<ModelCourseList.Course> mArrListCourseList;
    private Activity mContext;
    private LayoutInflater mInflater;
    private ViewHolder holder;
    private OnCourseListListener mCourseListClickListener;
    private String strFrom = "";

    public FilterCourseListAdapter(Activity context, ArrayList<ModelCourseList.Course> arrListNewsFeedData, OnCourseListListener onCourseListListener, String strFrom) {
        mContext = context;
        mArrListCourseList = arrListNewsFeedData;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCourseListClickListener = onCourseListListener;
        this.strFrom = strFrom;
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
            if (strFrom.equalsIgnoreCase("course_list")) {
                view = mInflater.inflate(R.layout.view_course_list, null);
            } else {
                view = mInflater.inflate(R.layout.view_course_list_inquiry, null);
            }

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


    private void setCourseListData(final int position, final ViewHolder holder, ModelCourseList.Course data) {
        holder.mTvCourseName.setText("" + data.getName());
        holder.mRelCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCourseListClickListener.onCourseRawClick(position);
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
        public void onCourseRawClick(int position);
    }
}
