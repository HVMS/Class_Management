package com.globalitians.employees.students.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.students.models.InstallmentModel;

import java.util.ArrayList;

public class InstallmentListAdapter extends RecyclerView.Adapter<InstallmentListAdapter.MyViewHolder> {

    private ArrayList<InstallmentModel> mArrListInstallmentDate;
    private Activity mContext;
    private LayoutInflater mInflater;
    private InstallmentDateListener listener;

    public InstallmentListAdapter(Activity activity, ArrayList<InstallmentModel> mArrListInstallments, InstallmentDateListener listener) {
        this.mContext = activity;
        this.mArrListInstallmentDate = mArrListInstallments;
        this.listener=listener;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvInstallmentDate;
        private EditText edtInstallmentDate;
        private ImageView ivDelete;

        public MyViewHolder(View view) {
            super(view);
            tvInstallmentDate=view.findViewById(R.id.tv_installment_date);
            edtInstallmentDate=view.findViewById(R.id.edt_installment_date);
            ivDelete=view.findViewById(R.id.tv_delete_installment_date);
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_add_more_installment, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        try {
            InstallmentModel modelStudentList = mArrListInstallmentDate.get(position);

            holder.tvInstallmentDate.setText(modelStudentList.getStrDate());
            holder.edtInstallmentDate.setText(modelStudentList.getStrRs());

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onInstallmentRemove(position);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mArrListInstallmentDate.size();
    }

    public interface InstallmentDateListener {
        void onInstallmentRemove(int position);
    }
}
