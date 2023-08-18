package com.globalitians.employees.test.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.test.model.TestDetailsModel;
import com.globalitians.employees.test.model.TestStudentsModel;
import com.globalitians.employees.utility.LogUtil;

import java.util.ArrayList;

public class AddMarksAdapter extends RecyclerView.Adapter<AddMarksAdapter.MyViewHolder> {

    private ArrayList<TestStudentsModel.TestDetail.TestStudent> mArrListTestStudents;
    private Activity mActivity;
    private TestStudentsModel testStudentsModel;
    private AddMarksListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStudentName;
        private TextView tvTotalMarks;
        private EditText edtObtainedMarks;
        private Switch mSwitchStatus;//Present - Absent

        public MyViewHolder(View view) {
            super(view);
            tvStudentName = view.findViewById(R.id.tv_student_name);
            tvTotalMarks = view.findViewById(R.id.tv_total_marks);
            edtObtainedMarks = view.findViewById(R.id.edt_obtained_marks);
            mSwitchStatus = view.findViewById(R.id.switch_status);
        }
    }

    public AddMarksAdapter(Activity activity,
                           TestStudentsModel testStudentsModel,
                           ArrayList<TestStudentsModel.TestDetail.TestStudent> mAlTestStudents,
                           AddMarksListener listener) {
        this.mActivity = activity;
        this.testStudentsModel = testStudentsModel;
        this.mArrListTestStudents = mAlTestStudents;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_add_marks_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        try {
            holder.tvStudentName.setText("" + mArrListTestStudents.get(position).getFname()
                    + " " + mArrListTestStudents.get(position).getLname());
            holder.tvTotalMarks.setText("" + testStudentsModel.getTestDetail().getTotalmarks());

            LogUtil.e("STATUS >>>>",""+mArrListTestStudents.get(position).getStatus_present_absent());

            if (mArrListTestStudents.get(position).getStatus_present_absent().equalsIgnoreCase("P")) {
                holder.mSwitchStatus.setChecked(true);
            } else {
                holder.mSwitchStatus.setChecked(false);
            }

            holder.mSwitchStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    listener.onStatusChange(position, b);
                }
            });

            holder.edtObtainedMarks.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    listener.onMarksAdd(position, "" + holder.edtObtainedMarks.getText().toString().trim());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mArrListTestStudents.size();
    }

    public interface AddMarksListener {
        void onStatusChange(int position, boolean status);

        void onMarksAdd(int position, String marks);
    }
}
