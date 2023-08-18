package com.globalitians.employees.inquiry.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.inquiry.model.ModelInquiry;
import com.globalitians.employees.utility.CommonUtil;

import java.util.ArrayList;

public class InquiryListAdapter extends BaseAdapter {

    private ArrayList<ModelInquiry.Inquiry> mArrListInquiryList;
    private Activity mContext;
    private LayoutInflater mInflater;
    private ViewHolder holder;
    private OnInquiryListListener mInquiryListClickListener;

    public InquiryListAdapter(Activity context, ArrayList<ModelInquiry.Inquiry> arrListNewsFeedData, OnInquiryListListener onInquiryListListener) {
        mContext = context;
        mArrListInquiryList = arrListNewsFeedData;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInquiryListClickListener = onInquiryListListener;
    }

    @Override
    public int getCount() {
        return mArrListInquiryList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrListInquiryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = mInflater.inflate(R.layout.view_inquiry_list, null);
            holder = new ViewHolder();
            holder.tvCourseInitial = view.findViewById(R.id.tvCourseInitial);
            holder.tvInquiryCourse = view.findViewById(R.id.tvInquiryCourse);
            holder.tvInquiryDate = view.findViewById(R.id.tvInquiryDate);
            holder.tvInquiryPersonName = view.findViewById(R.id.tvInquiryPersonName);
            holder.tvInquiryPersonContact = view.findViewById(R.id.tvInquiryPersonContact);
            holder.ivCall = view.findViewById(R.id.iv_call);
            holder.ivDeleteInquiry = view.findViewById(R.id.iv_delete_inquiry);
            holder.ivDeleteInquiry.setVisibility(View.GONE);

            holder.ivCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInquiryListClickListener.onCallClick(position);
                }
            });

            holder.ivDeleteInquiry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInquiryListClickListener.onDeleteInquiry(position);
                }
            });
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInquiryListClickListener.onInquiryRawClick(position);
            }
        });

        setCourseListData(position, holder, mArrListInquiryList.get(position));
        return view;
    }


    private void setCourseListData(final int position, ViewHolder holder, ModelInquiry.Inquiry data) {
        holder.tvInquiryCourse.setText("" );

        for(int i=0;i<data.getCourses().size();i++){

            if(CommonUtil.isNullString(holder.tvInquiryCourse.getText().toString())){
                holder.tvInquiryCourse.setText("" + data.getCourses().get(i).getName());
            }else{
                holder.tvInquiryCourse.setText(holder.tvInquiryCourse.getText().toString()+", "+data.getCourses().get(i).getName().toString());
            }
        }

        holder.tvInquiryDate.setText("" + data.getInquiryDate());
        holder.tvInquiryPersonName.setText("" + data.getFname() + " " + data.getLname());
        holder.tvInquiryPersonContact.setText("" + data.getContact());

        char initial=data.getCourses().get(0).getName().charAt(0);
        holder.tvCourseInitial.setText(""+initial);

    }

    static class ViewHolder {
        private TextView tvInquiryCourse;
        private TextView tvInquiryDate;
        private TextView tvCourseInitial;
        private TextView tvInquiryPersonName;
        private TextView tvInquiryPersonContact;
        private ImageView ivCall;
        private ImageView ivDeleteInquiry;
    }

    public interface OnInquiryListListener {
        void onInquiryRawClick(int position);

        void onCallClick(int position);

        void onDeleteInquiry(int position);
    }
}
