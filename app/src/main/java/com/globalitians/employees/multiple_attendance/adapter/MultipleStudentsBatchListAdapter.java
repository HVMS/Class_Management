package com.globalitians.employees.multiple_attendance.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.multiple_attendance.model.MultipleStudentsBatchListModelClass;

import java.util.ArrayList;

public class MultipleStudentsBatchListAdapter extends RecyclerView.Adapter<MultipleStudentsBatchListAdapter.MyViewHolder> {

    private Activity activity;
    private ArrayList<MultipleStudentsBatchListModelClass.BatchDetail.Student> mAlbatchlistofstudents;
    private OnBatchClickEvent onBatchClickEvent;
    private MultipleStudentsBatchListModelClass.BatchDetail.Student studentsBatchListModelClass;

    private int row_index = 0;

    public MultipleStudentsBatchListAdapter(Activity activity, ArrayList<MultipleStudentsBatchListModelClass.BatchDetail.Student> mAlbatchlistofstudents, OnBatchClickEvent onBatchClickEvent) {
        this.activity = activity;
        this.mAlbatchlistofstudents = mAlbatchlistofstudents;
        this.onBatchClickEvent = onBatchClickEvent;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(activity).inflate(R.layout.multiple_students_batch_list_item,parent,false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        try{
            studentsBatchListModelClass = mAlbatchlistofstudents.get(position);
            setBatchName(position,holder,studentsBatchListModelClass);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setBatchName(final int position, final MyViewHolder holder, MultipleStudentsBatchListModelClass.BatchDetail.Student studentsBatchListModelClass) {
        holder.mTxtbatchname.setText(""+studentsBatchListModelClass.getName());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index = position;
                holder.mTxtbatchname.setTextColor(activity.getResources().getColor(R.color.colorWhite));
                holder.relativeLayout.setBackground(activity.getResources().getDrawable(R.drawable.multiple_batch_item_selected_bk));
                notifyDataSetChanged();
            }
        });

        if(row_index==position){
            holder.mTxtbatchname.setTextColor(activity.getResources().getColor(R.color.colorWhite));
            holder.relativeLayout.setBackground(activity.getResources().getDrawable(R.drawable.multiple_batch_item_selected_bk));
            onBatchClickEvent.onBatchClick(position);
        }else{
            holder.mTxtbatchname.setTextColor(activity.getResources().getColor(R.color.my_blue));
            holder.relativeLayout.setBackground(activity.getResources().getDrawable(R.drawable.multiple_batch_item_bk));
        }
    }

    @Override
    public int getItemCount() {
        return mAlbatchlistofstudents.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView mTxtbatchname;
        private RelativeLayout relativeLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTxtbatchname = itemView.findViewById(R.id.tv_batch_name);
            relativeLayout = itemView.findViewById(R.id.rel_batch_item);
        }
    }

    public interface OnBatchClickEvent{
        void onBatchClick(int position);
    }

}
