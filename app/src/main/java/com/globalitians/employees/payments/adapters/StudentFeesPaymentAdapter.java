package com.globalitians.employees.payments.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.payments.model.ModelFeesList;

import java.util.ArrayList;

public class StudentFeesPaymentAdapter extends RecyclerView.Adapter<StudentFeesPaymentAdapter.MyViewHolder> {

        private ArrayList<ModelFeesList.Payment> mArrListFees;
        private ArrayList<ModelFeesList.StudentDetail> mArrListStudentDetails;
        private Activity mActivity;
        StudentFeesPaymentAdapter.ClickListener mFeesClickListener;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tvFeesAmount,tvFeesPaidDate,tvPaymentMode;
            private RelativeLayout linMainContainer;

            public MyViewHolder(View view) {
                super(view);
                linMainContainer = view.findViewById(R.id.linMainContainer);
                tvFeesAmount=view.findViewById(R.id.tvFeesAmount);
                tvFeesPaidDate=view.findViewById(R.id.tvFeesPaidDate);
                tvPaymentMode=view.findViewById(R.id.tv_payment_mode);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        mFeesClickListener.onFeesRowClick(getAdapterPosition());
                        Toast.makeText(mActivity, ""+mArrListStudentDetails.get(0).getStudentName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        public StudentFeesPaymentAdapter(Activity activity, ModelFeesList modelFeesList) {
            this.mActivity = activity;
            this.mArrListFees = modelFeesList.getPayments();
            this.mArrListStudentDetails=modelFeesList.getStudentDetail();
        }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_student_fees_payments_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            ModelFeesList.Payment modelFeesList = mArrListFees.get(position);

            holder.tvFeesAmount.setText("Rs."+modelFeesList.getPayment()+"/-");
            holder.tvFeesPaidDate.setText(""+modelFeesList.getPaymentDate());
            holder.tvPaymentMode.setText(""+modelFeesList.getPaymentType()+" "+modelFeesList.getCheckNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
        public int getItemCount() {
            return mArrListFees.size();
        }

        public interface ClickListener {
            void onFeesRowClick(int position);
        }
}
