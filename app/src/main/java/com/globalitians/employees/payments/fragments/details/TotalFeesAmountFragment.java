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

public class TotalFeesAmountFragment extends Fragment implements FeesHistoryListActivity.TotalFeesAmountDataReceivedListener {
    private TextView tvTotalAmount;
    private Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        View rootView = inflater.inflate(R.layout.fragment_total_fees_amount, container, false);

        mActivity =  getActivity();
        ((FeesHistoryListActivity) mActivity).setTotalFeesReceivedDataListener(this);

        tvTotalAmount=rootView.findViewById(R.id.tvTotalAmount);
        return rootView;
    }


    @Override
    public void onTotalStudentFeesDataReceived(ModelFeesList model) {
        tvTotalAmount.setVisibility(View.VISIBLE);
        tvTotalAmount.setText("Rs. "+model.getStudentDetail().get(0).getTotalAmount()+"/-");
        getActivity().setTitle(model.getStudentDetail().get(0).getStudentName());
    }
}

