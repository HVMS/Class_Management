package com.globalitians.employees.faculty.adapter;

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
import com.globalitians.employees.faculty.model.StandardListModel;

import java.util.ArrayList;

public class StandardListAdapter extends BaseAdapter {

    private Activity mContext;
    private ArrayList<StandardListModel.Standard> mAlstandardlist;
    private LayoutInflater mInflater;
    private ViewHolder holder;
    private OnStandardClickListner onStandardClickListner;
    private String strFrom="";

    public StandardListAdapter(Activity mContext, ArrayList<StandardListModel.Standard> mAlstandardlist, OnStandardClickListner onStandardClickListner, String strFrom) {
        this.mContext = mContext;
        this.mAlstandardlist = mAlstandardlist;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.onStandardClickListner = onStandardClickListner;
        this.strFrom = strFrom;
    }

    @Override
    public int getCount() {
        return mAlstandardlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mAlstandardlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(mContext,R.layout.layout_standard_item_with_checkbox,null);
            holder = new ViewHolder();
            holder.mTvStandardName=view.findViewById(R.id.tv_standard_name);
            holder.mRelStandard=view.findViewById(R.id.rv_standard);
            holder.chkStandard=view.findViewById(R.id.chkStandard);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        setStandardListData(position, holder, mAlstandardlist.get(position));
        return view;
    }

    private void setStandardListData(final int position, ViewHolder holder, StandardListModel.Standard standard) {
        holder.mTvStandardName.setText("" + standard.getName());
        holder.chkStandard.setChecked(false);

        holder.mRelStandard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStandardClickListner.onStandardRawClick(position);
            }
        });

        if(strFrom.equals("standard_list")){
            holder.chkStandard.setVisibility(View.GONE);
        }else{
            holder.chkStandard.setVisibility(View.VISIBLE);
            holder.chkStandard.setChecked(mAlstandardlist.get(position).isSelected());

            holder.chkStandard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onStandardClickListner.onStandardRawClick(position);
                }
            });

        }
    }

    static class ViewHolder {
        private TextView mTvStandardName;
        private RelativeLayout mRelStandard;
        private CheckBox chkStandard;
    }

    public interface OnStandardClickListner{
        void onStandardRawClick(int position);
        void onDeleteStandard(int position);
    }

}
