package com.globalitians.employees.branches.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.branches.model.ModelBranchList;

import java.util.ArrayList;
public class BranchListAdapter extends BaseAdapter {

    private ArrayList<ModelBranchList.Branch> mArrListBranchList;
    private Activity mContext;
    private LayoutInflater mInflater;
    private ViewHolder holder;
    private OnBranchListListener mInquiryListClickListener;

    public BranchListAdapter(Activity context, ArrayList<ModelBranchList.Branch> arrListNewsFeedData, OnBranchListListener onInquiryListListener) {
        mContext = context;
        mArrListBranchList = arrListNewsFeedData;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInquiryListClickListener = onInquiryListListener;
    }

    @Override
    public int getCount() {
        return mArrListBranchList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrListBranchList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = mInflater.inflate(R.layout.view_branch_list, null);
            holder = new ViewHolder();
            holder.tvBranchId = (TextView) view.findViewById(R.id.tvBranchID);
            holder.tvBranchName = (TextView) view.findViewById(R.id.tvBranchName);
            holder.tvBranchAddress = (TextView) view.findViewById(R.id.tvBranchAddress);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String map = "http://maps.google.co.in/maps?q=" + mArrListBranchList.get(position).getAddress();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                    mContext.startActivity(intent);
            }
        });

        setBranchListData(position, holder, mArrListBranchList.get(position));
        return view;
    }


    private void setBranchListData(final int position, ViewHolder holder, ModelBranchList.Branch data) {
        holder.tvBranchId.setText("" + data.getId());
        holder.tvBranchName.setText("" + data.getName());
        holder.tvBranchAddress.setText("" + data.getAddress());

        Log.e(">>> Address",""+data.getAddress());
    }

    static class ViewHolder {
        private TextView tvBranchId;
        private TextView tvBranchName;
        private TextView tvBranchAddress;
    }

    public interface OnBranchListListener {
    }
}
