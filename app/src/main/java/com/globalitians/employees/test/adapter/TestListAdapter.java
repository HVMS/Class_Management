package com.globalitians.employees.test.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.test.model.ModelTestList;

import java.util.ArrayList;

public class TestListAdapter extends RecyclerView.Adapter<TestListAdapter.MyViewHolder> {

    private ArrayList<ModelTestList.Test> mArrListTest;
    private Activity mActivity;
    private TestViewListener mTestClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;
        private TextView tvTime;
        private TextView tvTestName;
        private ImageView ivMarksAdded;

        private LinearLayout linMainContainer;

        public MyViewHolder(View view) {
            super(view);
            linMainContainer = view.findViewById(R.id.linMainContainer);
            tvDate = view.findViewById(R.id.tvTestDate);
            tvTime = view.findViewById(R.id.tvTestTime);
            tvTestName = view.findViewById(R.id.tvTestName);
            ivMarksAdded = view.findViewById(R.id.iv_marks_added_status);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  Toast.makeText(mActivity, ""+mArrListTest.get(getAdapterPosition()).getTestname(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public TestListAdapter(Activity activity, ArrayList<ModelTestList.Test> modelTestList, TestViewListener mTestClickListener) {
        this.mActivity = activity;
        this.mArrListTest = modelTestList;
        this.mTestClickListener = mTestClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_test_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        try {
            ModelTestList.Test modelTestList = mArrListTest.get(position);
            holder.tvTestName.setText("" + modelTestList.getName());
            holder.tvDate.setText("" + modelTestList.getDate());
            holder.tvTime.setText("" + modelTestList.getTime());

            if (modelTestList.isMarks_added()) {
                holder.ivMarksAdded.setBackgroundResource(R.drawable.circle_green);
            } else {
                holder.ivMarksAdded.setBackgroundResource(R.drawable.circle_red);
            }

            holder.linMainContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTestClickListener.onTestViewClick(position);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mArrListTest.size();
    }

    public interface TestViewListener {
        void onTestViewClick(int position);
    }
}
