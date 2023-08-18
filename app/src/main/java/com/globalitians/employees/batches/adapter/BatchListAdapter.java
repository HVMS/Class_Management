package com.globalitians.employees.batches.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.batches.activities.BatchListActivity;
import com.globalitians.employees.batches.model.BatchListModel;
import com.globalitians.employees.payments.model.ModelFeesList;

import java.util.ArrayList;

public class BatchListAdapter extends RecyclerView.Adapter<BatchListAdapter.MyViewHolder> {

    private ArrayList<BatchListModel.Batch> mAlBatches;
    private Activity mActivity;
    private BatchClickListener mBatchListListener;

    public BatchListAdapter(Activity activity, ArrayList<BatchListModel.Batch> mAlBatches, BatchClickListener listener) {
        this.mActivity = activity;
        this.mAlBatches = mAlBatches;
        this.mBatchListListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvBatchName, tvBatchAliasName;
        private RelativeLayout linMainContainer;
        private ImageView mIvmoreoptions;

        public MyViewHolder(View view) {
            super(view);
            linMainContainer = view.findViewById(R.id.rel_container);
            tvBatchAliasName = view.findViewById(R.id.tv_batch_alias);
            tvBatchName = view.findViewById(R.id.tv_batch_name);
            mIvmoreoptions = view.findViewById(R.id.iv_more_options);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_batch_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        try {
            BatchListModel.Batch modelBatch = mAlBatches.get(position);
            holder.tvBatchName.setText("" + modelBatch.getName());
            holder.tvBatchAliasName.setText("" + modelBatch.getAlias());
            holder.linMainContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBatchListListener.onBatchListClick(position);
                }
            });

            holder.mIvmoreoptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBatchListListener.onMoreOptionsClick(position,holder.mIvmoreoptions);
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
        void onBatchListClick(int position);
        void onMoreOptionsClick(int position, ImageView mivmoreoptions);
    }
}
