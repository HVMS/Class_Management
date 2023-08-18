package com.globalitians.employees.students.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.students.models.ModelStudent;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;

import java.util.ArrayList;

public class StudentListAdapter extends BaseAdapter {

    private ArrayList<ModelStudent.Student> mArrListStudentList;
    private Activity mContext;
    private LayoutInflater mInflater;
    private ViewHolder holder;
    private OnStudentListListener mStudentListClickListener;
    private boolean isDisplayMoreOptions = false;

    public StudentListAdapter(Activity context, ArrayList<ModelStudent.Student> arrListStudentListData, OnStudentListListener onStudentListListener,
                              boolean isDisplayMoreOptions) {
        mContext = context;
        mArrListStudentList = arrListStudentListData;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mStudentListClickListener = onStudentListListener;
        this.isDisplayMoreOptions = isDisplayMoreOptions;
    }

    @Override
    public int getCount() {
        return mArrListStudentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrListStudentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = mInflater.inflate(R.layout.view_student_list, null);
            holder = new ViewHolder();
            holder.ivUser = (CircularImageView) view.findViewById(R.id.iv_user);
            holder.tvStudentName = (TextView) view.findViewById(R.id.tv_student_name);
            holder.tvCourseName = (TextView) view.findViewById(R.id.tv_course_name);
            holder.ivMoreOptions = (ImageView) view.findViewById(R.id.iv_more_options);
            holder.tvBatchName=(TextView)view.findViewById(R.id.tv_batch_name);

            if (isDisplayMoreOptions == false) {
                holder.ivMoreOptions.setVisibility(View.VISIBLE);
            } else {
                holder.ivMoreOptions.setVisibility(View.GONE);
            }
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStudentListClickListener.onStudentRawClick(position);
            }
        });

        holder.ivMoreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStudentListClickListener.onStudentMoreOptionsClick(position, holder.ivMoreOptions);
            }
        });

        setStudentListData(position, holder, mArrListStudentList.get(position));
        return view;
    }


    private void setStudentListData(final int position, ViewHolder holder, ModelStudent.Student data) {
        holder.tvStudentName.setText("" + data.getFname() + " " + data.getLname());
        if(Constants.IS_COURSE_SUPPORT)
        {
            holder.tvCourseName.setVisibility(View.VISIBLE);
            if(data!=null && data.getCourses()!=null)
            {
                for (int i = 0; i < data.getCourses().size(); i++) {
                    holder.tvCourseName.setText("" + data.getCourses().get(i).getName());
                }
            }else{
                holder.tvCourseName.setText("No Course Assigned");
            }
        }else{
            holder.tvCourseName.setVisibility(View.GONE);
        }
        holder.tvBatchName.setText("Batch: "+data.getBatch_name());
        CommonUtil.setImageToGlide(mContext, holder.ivUser, "" + data.getImage());
    }

    static class ViewHolder {
        public ImageView ivMoreOptions;
        private CircularImageView ivUser;
        private TextView tvStudentName;
        private TextView tvCourseName;
        private TextView tvBatchName;
     }

    public interface OnStudentListListener {
        public void onStudentRawClick(int position);
        public void onStudentMoreOptionsClick(int position, ImageView ivMoreOptions);
    }
}
