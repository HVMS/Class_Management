package com.globalitians.employees.payments.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.payments.activities.FeesHistoryListActivity;
import com.globalitians.employees.payments.activities.PaymentListTabbedActivity;
import com.globalitians.employees.students.adapters.StudentListRecylcerAdapter;
import com.globalitians.employees.students.models.ModelStudent;
import com.globalitians.employees.utility.LogUtil;
import com.globalitians.employees.utility.network.OkHttpInterface;

import java.util.ArrayList;

import static com.globalitians.employees.utility.CommonUtil.playSoundForAttendance;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_STUDENT_ID;
import static com.globalitians.employees.utility.Constants.REQUEST_PHONE_CALL;

public class PaidFeesListFragment extends Fragment implements OkHttpInterface, StudentListRecylcerAdapter.OnStudentListListener, PaymentListTabbedActivity.PaidStudentListDataReceivedListener {

    private Activity mActivity;
    private ArrayList<ModelStudent.Student> aListPaidStudents;
    private StudentListRecylcerAdapter mStudentListAdapter;

    private RecyclerView mRvPaidStudentList;
    private int callPosition=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        View rootView = inflater.inflate(R.layout.fragment_paid_student_list, container, false);

        mActivity = (PaymentListTabbedActivity) getActivity();
        ((PaymentListTabbedActivity) mActivity).setPaidStudentReceivedDataListener(this);

        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        mRvPaidStudentList =rootView.findViewById(R.id.rv_paid_student_list);
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


    private void setPaidStudentListAdapter(ModelStudent model) {
        aListPaidStudents=new ArrayList<>();
        if(model.getStudents()!=null)
        {
            for(int i=0;i<model.getStudents().size();i++){
                if(model.getStudents().get(i).getAmount().equalsIgnoreCase("Paid"))
                {
                    aListPaidStudents.add(model.getStudents().get(i));
                }
            }
        }

        if(aListPaidStudents!=null && aListPaidStudents.size()>0)
        {
            mStudentListAdapter = new StudentListRecylcerAdapter(getActivity(), aListPaidStudents,this);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            mRvPaidStudentList.setLayoutManager(manager);
            mRvPaidStudentList.setAdapter(mStudentListAdapter);
        }else{
            mRvPaidStudentList.setVisibility(View.GONE);
            if(mStudentListAdapter!=null)
            {
                mStudentListAdapter.notifyDataSetChanged();
            }

        }

    }

    @Override
    public void onPaidStudentListDataReceived(ModelStudent model,String strFrom) {
        LogUtil.e(">>>>>","onPaidStudentListDataReceived");
        setPaidStudentListAdapter(model);
    }


    /*@Override
    public void onCallStudent(int position) {
        callPosition=position;
        checkAndRequestCallPermission(position);
    }*/

    @Override
    public void onStudentRawClick(int position) {
        startActivity(new Intent(getActivity(), FeesHistoryListActivity.class)
                .putExtra(KEY_INTENT_STUDENT_ID, "" +aListPaidStudents.get(position).getId()));
    }

    @Override
    public void onMoreOptionClick(int adapterPosition, ImageView ivMoreOptions) {

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Payments");
    }

    private void checkAndRequestCallPermission(int position) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        } else {
            callNow("" + aListPaidStudents.get(position).getContact());
        }
    }

    public void callNow(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "" + number));
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            playSoundForAttendance("Please grant call permission from settings.",getActivity());
            Toast.makeText(getActivity(), "Please grant call permission from settings.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PHONE_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callNow("" + aListPaidStudents.get(callPosition).getContact());
            } else {
                playSoundForAttendance("Call Permission not granted.",getActivity());
                Toast.makeText(getActivity(), "Call Permission not granted.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
