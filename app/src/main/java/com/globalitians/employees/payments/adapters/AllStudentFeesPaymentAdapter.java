package com.globalitians.employees.payments.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.payments.model.ModelAllFeesPayments;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;

import java.util.ArrayList;
public class AllStudentFeesPaymentAdapter extends RecyclerView.Adapter<AllStudentFeesPaymentAdapter.MyViewHolder> {

        private ArrayList<ModelAllFeesPayments.Payment> mArrListFees;
        private Activity mActivity;
        AllStudentFeesPaymentAdapter.ClickListener mFeesClickListener;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private CircularImageView mCvStudentImage;
            private TextView tvFeesAmount,tvStudentName,tvFeesPaidDate,tvPaymentMode,tvChequeNumber;
            private RelativeLayout linMainContainer;

            public MyViewHolder(View view) {
                super(view);
                linMainContainer = view.findViewById(R.id.linMainContainer);
                mCvStudentImage=view.findViewById(R.id.iv_user);
                tvFeesAmount=view.findViewById(R.id.tvFeesAmount);
                tvStudentName=view.findViewById(R.id.tv_student_name);
                tvFeesPaidDate=view.findViewById(R.id.tvFeesPaidDate);
                tvPaymentMode=view.findViewById(R.id.tv_payment_mode);
                tvChequeNumber=view.findViewById(R.id.tvChequeNumber);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFeesClickListener.onFeesRowClick(getAdapterPosition());
                    }
                });
            }
        }

        public AllStudentFeesPaymentAdapter(Activity activity, ArrayList<ModelAllFeesPayments.Payment> listFees, AllStudentFeesPaymentAdapter.ClickListener clickListener) {
            this.mActivity = activity;
            this.mArrListFees = listFees;
            this.mFeesClickListener = clickListener;
        }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_all_fees_payments_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            ModelAllFeesPayments.Payment modelFeesList = mArrListFees.get(position);
            holder.tvFeesAmount.setText("Rs."+modelFeesList.getAmount()+"/-");
            holder.tvStudentName.setText(""+modelFeesList.getStudentName());
            holder.tvFeesPaidDate.setText("Paid On: "+modelFeesList.getPaymentDate());

            holder.tvPaymentMode.setVisibility(View.VISIBLE);
            holder.tvPaymentMode.setText(""+modelFeesList.getPayment_type());
            if(!CommonUtil.isNullString(modelFeesList.getCheck_no())){
                holder.tvChequeNumber.setVisibility(View.VISIBLE);
                holder.tvChequeNumber.setText(""+modelFeesList.getCheck_no());
            }else {
                holder.tvChequeNumber.setVisibility(View.GONE);
            }
            CommonUtil.setCircularImageToGlide(mActivity,holder.mCvStudentImage,""+modelFeesList.getStudentImage());

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
