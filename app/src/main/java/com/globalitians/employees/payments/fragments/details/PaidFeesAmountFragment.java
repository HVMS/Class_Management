package com.globalitians.employees.payments.fragments.details;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.payments.activities.FeesHistoryListActivity;
import com.globalitians.employees.payments.adapters.StudentFeesPaymentAdapter;
import com.globalitians.employees.payments.model.ModelFeesList;
import com.globalitians.employees.utility.network.OkHttpInterface;

public class PaidFeesAmountFragment extends Fragment implements OkHttpInterface,FeesHistoryListActivity.PaidFeesAmountDataReceivedListener {

    Activity mActivity;
    private StudentFeesPaymentAdapter mStudentFeesPaymentListAdapter;

    private RecyclerView mRvStudentFeesList;
    private TextView mTvPaidAmount;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        View rootView = inflater.inflate(R.layout.fragment_paid_fees_amount, container, false);

        mActivity = (FeesHistoryListActivity) getActivity();
        ((FeesHistoryListActivity) mActivity).setStudentFeesReceivedDataListener(this);

        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        mRvStudentFeesList=rootView.findViewById(R.id.rv_student_fees_list);
        mTvPaidAmount=rootView.findViewById(R.id.tvPaidAmount);
    }


    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {

    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }


    private void setFeesListAdapter(ModelFeesList model) {
        if(model.getPayments()!=null && model.getPayments().size()>0){
            mRvStudentFeesList.setVisibility(View.VISIBLE);
            getActivity().setTitle(model.getStudentDetail().get(0).getStudentName());
            mStudentFeesPaymentListAdapter = new StudentFeesPaymentAdapter(getActivity(), model);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            mRvStudentFeesList.setLayoutManager(manager);
            mRvStudentFeesList.setAdapter(mStudentFeesPaymentListAdapter);
            mTvPaidAmount.setVisibility(View.GONE);
        }else{
            mTvPaidAmount.setVisibility(View.VISIBLE);
            mTvPaidAmount.setText("Rs."+model.getStudentDetail().get(0).getPaidAmounnt()+"/-");
            mRvStudentFeesList.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPaidStudentFeesDataReceived(ModelFeesList model) {
        setFeesListAdapter(model);
    }
}
