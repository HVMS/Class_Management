package com.globalitians.employees.Homework.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.batches.model.BatchListModel;

import java.util.ArrayList;

public class HomeWorkBatchListAdapter extends RecyclerView.Adapter<HomeWorkBatchListAdapter.BatchHolder> {

    private Activity activity;
    private ArrayList<BatchListModel.Batch> mAlbatches;
    private OnHomeWorkBatchClick onHomeWorkBatchClick;
    private BatchListModel.Batch batchListModel;
    private int row_index=0;

    public HomeWorkBatchListAdapter(Activity activity, ArrayList<BatchListModel.Batch> mAlbatches, OnHomeWorkBatchClick onHomeWorkBatchClick) {
        this.activity = activity;
        this.mAlbatches = mAlbatches;
        this.onHomeWorkBatchClick = onHomeWorkBatchClick;
    }

    @NonNull
    @Override
    public BatchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(activity).inflate(R.layout.students_batch_list_item,parent,false);
        return new BatchHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull BatchHolder holder, int position) {
        try{
            batchListModel = mAlbatches.get(position);
            setBatchListData(position,holder,batchListModel);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setBatchListData(final int position, final BatchHolder holder, BatchListModel.Batch batchListModel) {
        holder.mTxtbatchname.setText(""+batchListModel.getName());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = position;
                holder.mTxtbatchname.setTextColor(activity.getResources().getColor(R.color.mypurple));
                holder.relativeLayout.setBackground(activity.getResources().getDrawable(R.drawable.hw_batch_item_selected_bk));
                notifyDataSetChanged();
            }
        });

        if(row_index==position){
            holder.mTxtbatchname.setTextColor(activity.getResources().getColor(R.color.mypurple));
            holder.relativeLayout.setBackground(activity.getResources().getDrawable(R.drawable.hw_batch_item_selected_bk));
            onHomeWorkBatchClick.onBatchClick(position);
        }else{
            holder.mTxtbatchname.setTextColor(activity.getResources().getColor(R.color.colorWhite));
            holder.relativeLayout.setBackground(activity.getResources().getDrawable(R.drawable.hw_batch_item_bk));
        }
    }

    @Override
    public int getItemCount() {
        return mAlbatches.size();
    }

    public class BatchHolder extends RecyclerView.ViewHolder{

        private TextView mTxtbatchname;
        private RelativeLayout relativeLayout;

        public BatchHolder(@NonNull View itemView) {
            super(itemView);
            mTxtbatchname = itemView.findViewById(R.id.tv_batch_name);
            relativeLayout = itemView.findViewById(R.id.rel_batch_item);
        }
    }

    public interface OnHomeWorkBatchClick{
        void onBatchClick(int position);
    }
}
