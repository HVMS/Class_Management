package com.globalitians.employees.payments.fragments.details;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.payments.activities.FeesHistoryListActivity;
import com.globalitians.employees.payments.model.ModelFeesList;

public class UnpaidFeesAmountFragment extends Fragment implements FeesHistoryListActivity.UnPaidFeesAmountDataReceivedListener {
    private TextView tvUnpaidAmount;
    Activity mActivity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        View rootView = inflater.inflate(R.layout.fragment_unpaid_fees_amount, container, false);

        mActivity =  getActivity();
        ((FeesHistoryListActivity) mActivity).setUnpaidFeesReceivedDataListener(this);


        tvUnpaidAmount=rootView.findViewById(R.id.tvUnpaidAmount);
        return rootView;
    }


    @Override
    public void onUnPaidStudentFeesDataReceived(ModelFeesList model) {
        tvUnpaidAmount.setVisibility(View.VISIBLE);
        tvUnpaidAmount.setText("Rs. "+model.getStudentDetail().get(0).getUnpaidAmount()+"/-");
        getActivity().setTitle(model.getStudentDetail().get(0).getStudentName());
    }
}

