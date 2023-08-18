package com.globalitians.employees.test.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.test.model.ModelTestList;
import com.globalitians.employees.test.model.TestDetailsModel;

import java.util.ArrayList;

public class StudentMarksAdapter extends RecyclerView.Adapter<StudentMarksAdapter.MyViewHolder> {

    private ArrayList<TestDetailsModel.TestDetail.StudentsMark> mArrListMarks;
    private Activity mActivity;
    private String mStrPassingMarks = "";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStudentName;
        private TextView tvMarks;
        private TextView tvStatus;

        public MyViewHolder(View view) {
            super(view);
            tvStudentName = view.findViewById(R.id.tv_student_name);
            tvMarks = view.findViewById(R.id.tv_obtained_marks);
            tvStatus = view.findViewById(R.id.tv_test_status);
        }
    }

    public StudentMarksAdapter(Activity activity,
                               ArrayList<TestDetailsModel.TestDetail.StudentsMark> mArrListMarks,
                               String strPassingMarks) {
        this.mActivity = activity;
        this.mArrListMarks = mArrListMarks;
        this.mStrPassingMarks = strPassingMarks;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_student_marks, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        try {
            TestDetailsModel.TestDetail.StudentsMark modelMarks = mArrListMarks.get(position);
            holder.tvStudentName.setText("" + modelMarks.getStudentFname() + " " + "" + modelMarks.getStudentLname());
            holder.tvMarks.setText("" + modelMarks.getObtainedmarks());
            if (Integer.parseInt(modelMarks.getObtainedmarks()) >= Integer.parseInt(mStrPassingMarks)) {
                holder.tvStatus.setText("Pass");
            } else {
                holder.tvStatus.setText("Fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mArrListMarks.size();
    }
}
