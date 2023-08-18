package com.globalitians.employees.other.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.globalitians.employees.R;

import java.util.ArrayList;

public class SingleRawAdapter extends BaseAdapter {

    private ArrayList<String> mArrString;
    private Activity mContext;
    private LayoutInflater mInflater;
    private ViewHolder holder;

    public SingleRawAdapter(Activity context, ArrayList<String> arrListString) {
        mContext = context;
        mArrString = arrListString;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mArrString.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrString.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = mInflater.inflate(R.layout.view_single_text, null);
            holder = new ViewHolder();
            holder.tvSingleRaw = (TextView) view.findViewById(R.id.tvSingleRaw);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        setSingleRawData(position, holder,mArrString.get(position));
        return view;
    }


    private void setSingleRawData(final int position, ViewHolder holder,String strData) {
        holder.tvSingleRaw.setText("" + strData);
    }

    static class ViewHolder {
        private TextView tvSingleRaw;
    }
}
