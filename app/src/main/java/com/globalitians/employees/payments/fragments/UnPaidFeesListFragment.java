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
import android.widget.TextView;
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

public class UnPaidFeesListFragment extends Fragment implements OkHttpInterface, StudentListRecylcerAdapter.OnStudentListListener, PaymentListTabbedActivity.UnPaidStudentListDataReceivedListener {

    private Activity mActivity;
    private ArrayList<ModelStudent.Student> aListUnPaidStudents;
    private StudentListRecylcerAdapter mStudentListAdapter;
    private TextView tvNoRecordFound;
    private RecyclerView mRvPaidStudentList;

    private int callPosition=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        View rootView = inflater.inflate(R.layout.fragment_unpaid_student_list, container, false);

        mActivity = (PaymentListTabbedActivity) getActivity();
        ((PaymentListTabbedActivity) mActivity).setUnpaidFeesReceivedDataListener(this);

        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        mRvPaidStudentList =rootView.findViewById(R.id.rv_unpaid_student_list);
        tvNoRecordFound=rootView.findViewById(R.id.tv_no_unpaid_fees_records);
        tvNoRecordFound.setVisibility(View.GONE);
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


    //from payment(paid,unpaid)
    //from student(completed, uncompleted)
    private void setUnPaidStudentListAdapter(ModelStudent model,String strFrom) {
        aListUnPaidStudents =new ArrayList<>();
        if(model.getStudents()!=null && model.getStudents().size()>0)
        {
            for(int i=0;i<model.getStudents().size();i++){
                if(model.getStudents().get(i).getAmount().equalsIgnoreCase("Unpaid"))
                {
                    aListUnPaidStudents.add(model.getStudents().get(i));
                }
            }

            if(aListUnPaidStudents.size()>0)
            {
                mStudentListAdapter = new StudentListRecylcerAdapter(getActivity(), aListUnPaidStudents,this);
                LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                mRvPaidStudentList.setLayoutManager(manager);
                mRvPaidStudentList.setAdapter(mStudentListAdapter);
                tvNoRecordFound.setVisibility(View.GONE);
            }else{
                tvNoRecordFound.setVisibility(View.VISIBLE);
                if(mStudentListAdapter!=null)
                {
                    mStudentListAdapter.notifyDataSetChanged();
                }

            }
        }
        else{
            if(aListUnPaidStudents!=null && aListUnPaidStudents.size()>0)
            {
                aListUnPaidStudents.clear();
            }
            if(mStudentListAdapter!=null)
            {
                mStudentListAdapter.notifyDataSetChanged();
            }
            tvNoRecordFound.setVisibility(View.VISIBLE);
            mRvPaidStudentList.setVisibility(View.GONE);
        }
    }


    @Override
    public void onUnPaidStudentListDataReceived(ModelStudent model,String strFrom) {
        LogUtil.e(">>>>>","onUnPaidStudentListDataReceived");
        setUnPaidStudentListAdapter(model,"");
    }

   /* @Override
    public void onDeleteStudent(int position) {

    }

    @Override
    public void onCallStudent(int position) {
        callPosition=position;
        checkAndRequestCallPermission(position);
    }*/

    @Override
    public void onStudentRawClick(int position) {
        startActivity(new Intent(getActivity(), FeesHistoryListActivity.class)
                .putExtra(KEY_INTENT_STUDENT_ID, "" + aListUnPaidStudents.get(position).getId()));
    }

    @Override
    public void onMoreOptionClick(int adapterPosition, ImageView ivMoreOptions) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PHONE_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callNow("" + aListUnPaidStudents.get(callPosition).getContact());
            } else {
                playSoundForAttendance("Call Permission not granted.",getActivity());
                Toast.makeText(getActivity(), "Call Permission not granted.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkAndRequestCallPermission(int position) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        } else {
            callNow("" + aListUnPaidStudents.get(position).getContact());
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
}
