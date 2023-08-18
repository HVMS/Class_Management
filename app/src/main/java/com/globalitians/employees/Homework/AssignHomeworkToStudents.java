package com.globalitians.employees.Homework;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.globalitians.employees.Homework.activity.AddHomeWorkActivity;
import com.globalitians.employees.Homework.adapter.AssignHomwWorkStudentsAdapter;
import com.globalitians.employees.R;
import com.globalitians.employees.batches.model.BatchDetailsModel;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_BATCH_DETAILS;

public class AssignHomeworkToStudents extends AppCompatActivity
        implements OkHttpInterface,
        AssignHomwWorkStudentsAdapter.OnAssignedHomeworkListner, View.OnClickListener {

    private RecyclerView mRvstudents;
    private AssignHomwWorkStudentsAdapter assignHomwWorkStudentsAdapter;

    private String mStrbatchid="";
    private Button mBtnassignhw;
    private ArrayList<String> mAlselectedStudentsIds;
    private CheckBox chkAll;

    private ArrayList<BatchDetailsModel.BatchDetail.Student> mAlstudentsbatchwise;
    private BatchDetailsModel batchDetailsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(AssignHomeworkToStudents.this);
        setContentView(R.layout.activity_assign_homework_to_students);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Assign students");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlackLight));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorBlackLight), PorterDuff.Mode.SRC_ATOP);

        findViews();
        init();
        getIdFromSpinner();
    }

    private void init() {
        mAlstudentsbatchwise = new ArrayList<>();
        mAlselectedStudentsIds = new ArrayList<>();
    }

    private void getIdFromSpinner() {
        Intent intent = getIntent();
        if (intent != null){
            mStrbatchid = ""+intent.getStringExtra("key_batch_id");
            Toast.makeText(this, "batch id is : "+mStrbatchid, Toast.LENGTH_SHORT).show();
            if(!CommonUtil.isNullString(""+mStrbatchid)){
                callApiToLoadbatchWiseStudents();
            }
        }
    }

    private void callApiToLoadbatchWiseStudents() {
        if(!CommonUtil.isInternetAvailable(AssignHomeworkToStudents.this)){
            return;
        }

        new OkHttpRequest(AssignHomeworkToStudents.this,
                OkHttpRequest.Method.POST,
                Constants.WS_BATCH_DETAIL,
                RequestParam.batchDetails(""+mStrbatchid),
                RequestParam.getNull(),
                CODE_BATCH_DETAILS,
                true,
                this);
    }

    private void findViews() {
        mRvstudents = findViewById(R.id.assign_students_homework);
        mBtnassignhw = findViewById(R.id.ASSIGNBTN);
        chkAll = findViewById(R.id.chkAll);

        mBtnassignhw.setOnClickListener(this);
    }

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        if(response==null){
            return;
        }

        switch (requestId){
            case CODE_BATCH_DETAILS:
                Log.e("students",""+response);
                final Gson studentsGson = new Gson();
                try{
                    batchDetailsModel = studentsGson.fromJson(response,BatchDetailsModel.class);
                    if(batchDetailsModel.getStatus().equals(Constants.SUCCESS_CODE)){
                        if(batchDetailsModel.getBatchDetail().getStudents()!=null &&
                          batchDetailsModel.getBatchDetail().getStudents().size()>0){
                            mAlstudentsbatchwise = batchDetailsModel.getBatchDetail().getStudents();
                            setStudentwiseAdapter(mAlstudentsbatchwise);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }

    }

    private void setStudentwiseAdapter(ArrayList<BatchDetailsModel.BatchDetail.Student> mAlstudentsbatchwise) {
        assignHomwWorkStudentsAdapter =
                new AssignHomwWorkStudentsAdapter(AssignHomeworkToStudents.this,mAlstudentsbatchwise,this);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        mRvstudents.setLayoutManager(manager);
        mRvstudents.setAdapter(assignHomwWorkStudentsAdapter);
        assignHomwWorkStudentsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void AddStudent(int position, boolean value) {
        mAlstudentsbatchwise.get(position).setSelected(value);
        assignHomwWorkStudentsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ASSIGNBTN:

                for(int i = 0 ; i < mAlstudentsbatchwise.size() ; i++){
                    if(mAlstudentsbatchwise.get(i).isSelected()==true){
                        mAlselectedStudentsIds.add(""+mAlstudentsbatchwise.get(i).getId());
                    }else if(mAlstudentsbatchwise.get(i).isSelected()==false){
                        mAlselectedStudentsIds.remove(""+mAlstudentsbatchwise.get(i).getId());
                    }
                }

                Intent intent = new Intent(AssignHomeworkToStudents.this,AddHomeWorkActivity.class);
                intent.putStringArrayListExtra("array_list_ids",mAlselectedStudentsIds);
                startActivity(intent);
                break;
        }
    }
}
