package com.globalitians.employees.courses.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.courses.model.ModelCourseList;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;

import java.util.ArrayList;

public class CourseListAdapter extends BaseAdapter {

    private ArrayList<ModelCourseList.Course> mArrListCourseList;
    private Activity mContext;
    private LayoutInflater mInflater;
    private ViewHolder holder;
    private OnCourseListListener mCourseListClickListener;
    private String strFrom="";

    public CourseListAdapter(Activity context, ArrayList<ModelCourseList.Course> arrListNewsFeedData, OnCourseListListener onCourseListListener,String strFrom) {
        mContext = context;
        mArrListCourseList = arrListNewsFeedData;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCourseListClickListener = onCourseListListener;
        this.strFrom=strFrom;
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
            if(strFrom.equalsIgnoreCase("course_list")){
                view = mInflater.inflate(R.layout.view_course_list, null);
            }else{
                view = mInflater.inflate(R.layout.view_course_list_inquiry, null);
            }

            holder = new ViewHolder();
            holder.mTvCourseName=view.findViewById(R.id.tv_course_name);
            holder.mRelCourse=view.findViewById(R.id.rel_course);
            holder.chkCourse=view.findViewById(R.id.chkCourse);
            holder.ivDeleteCourse =view.findViewById(R.id.iv_delete_course);
            holder.ivCourse=view.findViewById(R.id.iv_course);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        setCourseListData(position, holder, mArrListCourseList.get(position));
        return view;
    }


    private void setCourseListData(final int position, final ViewHolder holder, ModelCourseList.Course data) {
        holder.mTvCourseName.setText("" + data.getName());
        holder.chkCourse.setChecked(false);

        holder.mRelCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCourseListClickListener.onCourseRawClick(position);
            }
        });

        if(strFrom.equals("course_list")){
            CommonUtil.setCircularImageToGlide(mContext,holder.ivCourse,""+mArrListCourseList.get(position).getImage());
            holder.chkCourse.setVisibility(View.GONE);
            holder.ivDeleteCourse.setVisibility(View.VISIBLE);
            holder.ivDeleteCourse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCourseListClickListener.onDeleteCourse(position);
                }
            });
        }else{
            holder.chkCourse.setVisibility(View.VISIBLE);
            holder.ivDeleteCourse.setVisibility(View.GONE);
            holder.chkCourse.setChecked(mArrListCourseList.get(position).isSelected());

            holder.chkCourse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCourseListClickListener.onCourseRawClick(position);
                }
            });

        }
    }

    static class ViewHolder {
        private TextView mTvCourseName;
        private RelativeLayout mRelCourse;
        private CheckBox chkCourse;
        private ImageView ivDeleteCourse;
        private CircularImageView ivCourse;
    }

    public interface OnCourseListListener {
        public void onCourseRawClick(int position);
        public void onDeleteCourse(int position);
    }
}
