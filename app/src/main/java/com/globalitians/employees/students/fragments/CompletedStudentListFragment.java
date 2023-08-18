package com.globalitians.employees.students.fragments;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.students.activities.StudentDetailsActivity;
import com.globalitians.employees.students.activities.StudentListTabbedActivity;
import com.globalitians.employees.students.adapters.StudentListRecylcerAdapter;
import com.globalitians.employees.students.models.ModelStudent;
import com.globalitians.employees.utility.LogUtil;
import com.globalitians.employees.utility.network.OkHttpInterface;

import java.util.ArrayList;

import static com.globalitians.employees.utility.CommonUtil.playSoundForAttendance;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_STUDENT_ID;
import static com.globalitians.employees.utility.Constants.REQUEST_PHONE_CALL;

public class CompletedStudentListFragment extends Fragment implements OkHttpInterface,StudentListTabbedActivity.UnPaidStudentListDataReceivedListener, StudentListRecylcerAdapter.OnStudentListListener {

    private Activity mActivity;
    private ArrayList<ModelStudent.Student> aListCompletedStudents;
    private StudentListRecylcerAdapter mStudentListAdapter;

    private RecyclerView mRvPaidStudentList;
    private int callPosition=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        View rootView = inflater.inflate(R.layout.fragment_unpaid_student_list, container, false);

        mActivity = (StudentListTabbedActivity) getActivity();
        ((StudentListTabbedActivity) mActivity).setUnpaidFeesReceivedDataListener(this);

        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        mRvPaidStudentList =rootView.findViewById(R.id.rv_unpaid_student_list);
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
        int temp=0;
        aListCompletedStudents =new ArrayList<>();
        for(int i=0;i<model.getStudents().size();i++){
            if(model.getStudents().get(i).getCourses().size()>0)
            {
                for(int j=0;j<model.getStudents().get(i).getCourses().size();j++)
                {
                    if(model.getStudents().get(i).getCourses().get(j).getCourseStatus().equalsIgnoreCase("Running")){
                        temp=1;
                        break;
                    }
                }
                if(temp==0)
                {
                    aListCompletedStudents.add(model.getStudents().get(i));
                }
            }
        }

        mStudentListAdapter = new StudentListRecylcerAdapter(getActivity(), aListCompletedStudents,this);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mRvPaidStudentList.setLayoutManager(manager);
        mRvPaidStudentList.setAdapter(mStudentListAdapter);
    }


    @Override
    public void onUnPaidStudentListDataReceived(ModelStudent model,String strFrom) {
        LogUtil.e(">>>>>","onUnPaidStudentListDataReceived");
        setUnPaidStudentListAdapter(model,"");
    }



    @Override
    public void onStudentRawClick(int position) {
        startActivity(new Intent(getActivity(), StudentDetailsActivity.class)
                .putExtra(KEY_INTENT_STUDENT_ID, "" + aListCompletedStudents.get(position).getId()));
    }

    @Override
    public void onMoreOptionClick(int adapterPosition,ImageView ivMoreOptions) {
        openPopupMenuOptions(adapterPosition,ivMoreOptions);
    }
/*
    @Override
    public void onCallStudent(int position) {
        callPosition=position;
        checkAndRequestCallPermission(position);
    }
*/

    private void checkAndRequestCallPermission(int position) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        } else {
            callNow("" + aListCompletedStudents.get(position).getContact());
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
                callNow("" + aListCompletedStudents.get(callPosition).getContact());
            } else {
                playSoundForAttendance("Call Permission not granted.",getActivity());
                Toast.makeText(getActivity(), "Call Permission not granted.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openPopupMenuOptions(final int adapterPosition, ImageView ivMoreOptions)
    {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(getActivity(), ivMoreOptions);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.student_option_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                String str=""+item.getTitle();
                switch(str){
                    case "Call":
                        callNow(""+aListCompletedStudents.get(adapterPosition).getContact());
                        break;
                    case "Notifications":
                        Toast.makeText(mActivity, "In Progress", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }
}
