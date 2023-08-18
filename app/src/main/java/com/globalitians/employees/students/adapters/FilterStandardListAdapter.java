package com.globalitians.employees.students.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.faculty.model.StandardListModel;

import java.util.ArrayList;

public class FilterStandardListAdapter extends RecyclerView.Adapter<FilterStandardListAdapter.MyViewHolder> {

    private ArrayList<StandardListModel.Standard> mAlBatches;
    private Activity mActivity;
    private StandardClickListener mStandardListListener;

    public FilterStandardListAdapter(Activity activity, ArrayList<StandardListModel.Standard> mAlStandard, StandardClickListener listener) {
        this.mActivity = activity;
        this.mAlBatches = mAlStandard;
        this.mStandardListListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStandardName, tvBatchAliasName;
        private RelativeLayout linMainContainer;
        private RelativeLayout mRelBackground;
        private ImageView mIvmoreoptions;

        public MyViewHolder(View view) {
            super(view);
            linMainContainer = view.findViewById(R.id.rel_container);
            tvBatchAliasName = view.findViewById(R.id.tv_batch_alias);
            tvStandardName = view.findViewById(R.id.tv_batch_name);
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
            final StandardListModel.Standard modelStandard = mAlBatches.get(position);
            if(mAlBatches.get(position).isSelected()==true)
            {
                holder.mRelBackground.setBackgroundColor(mActivity.getResources().getColor(R.color.colorThemeBlackTransperent));
                holder.tvStandardName.setTextColor(mActivity.getResources().getColor(R.color.colorWhite));
            }else{
                holder.mRelBackground.setBackgroundColor(mActivity.getResources().getColor(R.color.colorWhite));
                holder.tvStandardName.setTextColor(mActivity.getResources().getColor(R.color.colorBlack));
            }

            holder.tvStandardName.setText("" + modelStandard.getName());
            holder.linMainContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(modelStandard.isSelected())
                    {
                        mStandardListListener.onStandardClick(position,false);
                    }else{
                        mStandardListListener.onStandardClick(position,true);
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

    public interface StandardClickListener {
        void onStandardClick(int position, boolean isSelected);
    }
}
