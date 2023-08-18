package com.globalitians.employees.students.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.batches.model.BatchListModel;

import java.util.ArrayList;

public class FilterBatchListAdapter extends RecyclerView.Adapter<FilterBatchListAdapter.MyViewHolder> {

    private ArrayList<BatchListModel.Batch> mAlBatches;
    private Activity mActivity;
    private BatchClickListener mBatchListListener;

    public FilterBatchListAdapter(Activity activity, ArrayList<BatchListModel.Batch> mAlBatches, BatchClickListener listener) {
        this.mActivity = activity;
        this.mAlBatches = mAlBatches;
        this.mBatchListListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvBatchName, tvBatchAliasName;
        private RelativeLayout linMainContainer;
        private RelativeLayout mRelBackground;
        private ImageView mIvmoreoptions;

        public MyViewHolder(View view) {
            super(view);
            linMainContainer = view.findViewById(R.id.rel_container);
            tvBatchAliasName = view.findViewById(R.id.tv_batch_alias);
            tvBatchName = view.findViewById(R.id.tv_batch_name);
            mIvmoreoptions = view.findViewById(R.id.iv_more_options);
            mRelBackground= view.findViewById(R.id.rel_row_background);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_view_batch_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        try {
            final BatchListModel.Batch modelBatch = mAlBatches.get(position);
            if(mAlBatches.get(position).isSelected()==true)
            {
                holder.mRelBackground.setBackgroundColor(mActivity.getResources().getColor(R.color.colorThemeBlackTransperent));
                holder.tvBatchName.setTextColor(mActivity.getResources().getColor(R.color.colorWhite));
            }else{
                holder.mRelBackground.setBackgroundColor(mActivity.getResources().getColor(R.color.colorWhite));
                holder.tvBatchName.setTextColor(mActivity.getResources().getColor(R.color.colorBlack));
            }

            holder.tvBatchName.setText("" + modelBatch.getName());
            holder.tvBatchAliasName.setText("" + modelBatch.getAlias());
            holder.linMainContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(modelBatch.isSelected())
                    {
                        mBatchListListener.onBatchListClick(position,false);
                    }else{
                        mBatchListListener.onBatchListClick(position,true);
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mAlBatches.size();
    }

    public interface BatchClickListener {
        void onBatchListClick(int position,boolean isSelected);
    }
}
