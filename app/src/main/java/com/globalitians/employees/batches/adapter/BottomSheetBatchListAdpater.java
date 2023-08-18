package com.globalitians.employees.batches.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalitians.employees.R;
import com.globalitians.employees.batches.model.BatchListModel;
import com.globalitians.employees.customviews.CustomTextView;

import java.util.ArrayList;

public class BottomSheetBatchListAdpater extends RecyclerView.Adapter<BottomSheetBatchListAdpater.MyViewHolder> {

    private Activity activity;
    private ArrayList<BatchListModel.Batch> mAlbacthes;
    private OnBatchCLickListner onBatchCLickListner;
    private BatchListModel.Batch batchListModel;

    public BottomSheetBatchListAdpater(Activity activity, ArrayList<BatchListModel.Batch> mAlbacthes, OnBatchCLickListner onBatchCLickListner) {
        this.activity = activity;
        this.mAlbacthes = mAlbacthes;
        this.onBatchCLickListner = onBatchCLickListner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(activity).inflate(R.layout.batch_list_item_for_bottom_sheet,parent,false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try{
            batchListModel = mAlbacthes.get(position);
            setBatchListData(position,holder,batchListModel);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setBatchListData(int position, MyViewHolder holder, BatchListModel.Batch batchListModel) {
        holder.mTxtbatchname.setText(""+batchListModel.getName());
        holder.mTxtbatchalisname.setText(""+batchListModel.getAlias());
    }

    @Override
    public int getItemCount() {
        return mAlbacthes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CustomTextView mTxtbatchname;
        private CustomTextView mTxtbatchalisname;
        private View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mTxtbatchname = itemView.findViewById(R.id.tv_batch_name);
            mTxtbatchalisname = itemView.findViewById(R.id.tv_batch_alias_name);
            view = itemView.findViewById(R.id.view_line);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBatchCLickListner.onClickBatchItem(getAdapterPosition());
                }
            });

        }

    }

    public interface OnBatchCLickListner{
        void onClickBatchItem(int position);
    }
}
