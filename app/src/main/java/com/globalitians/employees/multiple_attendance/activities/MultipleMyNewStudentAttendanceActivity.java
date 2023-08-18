package com.globalitians.employees.multiple_attendance.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.globalitians.employees.R;
import com.globalitians.employees.dashboard.activities.DashboardEmployeeActivity;
import com.globalitians.employees.multiple_attendance.adapter.MultipleStudentAttendanceAdapter;
import com.globalitians.employees.multiple_attendance.adapter.MultipleStudentsBatchListAdapter;
import com.globalitians.employees.multiple_attendance.model.MultipleAttendanceDetailsModelClass;
import com.globalitians.employees.multiple_attendance.model.MultipleAttendanceListModelClass;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_BATCH_WISE_ATTENDANCE_DETAILS;
import static com.globalitians.employees.utility.Constants.CODE_BATCH_WISE_ATTENDANCE_LIST;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.TAKE_ATTENDANCE_BATCHWISE;

public class MultipleMyNewStudentAttendanceActivity extends AppCompatActivity
        implements OkHttpInterface,
        MultipleStudentAttendanceAdapter.OnOffSwitchEvent, View.OnClickListener {

    private RecyclerView mRvbatchwiseStudents;
    private SwipeRefreshLayout mSwipeToRefresh;
    private Spinner mSpinnerbatch;
    private CardView cardView;
    private TextView mTxtextra;
    private ImageView mIvextra;
    private Button mBtntakeattendance;

    private MultipleAttendanceListModelClass attendanceListModelClass;

    private MultipleAttendanceDetailsModelClass attendanceDetailsModelClass;
    private ArrayList<MultipleAttendanceDetailsModelClass.BatchDetail.Student> mAlattendancetakenstudentslist;

    private ArrayList<MultipleAttendanceListModelClass.Batch> mArrListBatches;
    private ArrayList<String> mAlbatches;
    private MultipleStudentAttendanceAdapter multipleStudentAttendanceAdapter;

    private String mStrSelectedBatchId="";
    private String mStrSelectedBatchName="";
    private ArrayList<String> mAlids;
    private ArrayList<String> mAlabsentIds;
    private String mStrSelectedStudentIds="";
    private String mStrAbsentIds="";
    private int count=0;
    private String mStrAttendanceId="";
    private String date="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(MultipleMyNewStudentAttendanceActivity.this);
        setContentView(R.layout.activity_my_new_student_attendance);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Take Attendance");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlackLight));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.toolbarGreen), PorterDuff.Mode.SRC_ATOP);

        findViews();
        init();
        callApiToLoadBatchList();
    }

    private void callApiToLoadBatchList() {

        if(!CommonUtil.isInternetAvailable(MultipleMyNewStudentAttendanceActivity.this)){
            return;
        }

        new OkHttpRequest(MultipleMyNewStudentAttendanceActivity.this,
                OkHttpRequest.Method.GET,
                Constants.WS_BATCH_WISE_ATTENDANCE_LIST,
                RequestParam.multiplegetAttendanceListBatchWise(""+CommonUtil.getSharedPrefrencesInstance(
                        MultipleMyNewStudentAttendanceActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,""),
                            ""+date),
                RequestParam.getNull(),
                CODE_BATCH_WISE_ATTENDANCE_LIST,
                false,this);
    }

    private void init() {
        mArrListBatches = new ArrayList<>();
        mAlbatches = new ArrayList<>();
        mAlids = new ArrayList<>();
        mAlabsentIds = new ArrayList<>();
        mAlattendancetakenstudentslist = new ArrayList<>();
    }

    private void findViews() {
        mBtntakeattendance = findViewById(R.id.take_atte_btn);
        mIvextra = findViewById(R.id.iv_extra);
        mTxtextra = findViewById(R.id.tv_show_text);
        mRvbatchwiseStudents = findViewById(R.id.rv_batch_wise_students);
        cardView = findViewById(R.id.cardview_another);
        mSpinnerbatch = findViewById(R.id.spinner_batch);
        mSwipeToRefresh = findViewById(R.id.swipeToRefreshBatch);

        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        mBtntakeattendance.setOnClickListener(this);

        mSpinnerbatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if(position==0){
                    cardView.setVisibility(View.GONE);
                    mTxtextra.setVisibility(View.VISIBLE);
                    mIvextra.setVisibility(View.VISIBLE);
                    mBtntakeattendance.setVisibility(View.GONE);
                }

                if(position>0){
                    mIvextra.setVisibility(View.GONE);
                    mTxtextra.setVisibility(View.GONE);
                    cardView.setVisibility(View.VISIBLE);
                    mBtntakeattendance.setVisibility(View.VISIBLE);
                    mStrSelectedBatchId = ""+attendanceListModelClass.getBatches().get(position-1).getId();
                    mStrSelectedBatchName = ""+attendanceListModelClass.getBatches().get(position-1).getName();

                    if(attendanceListModelClass.getBatches().get(position-1).getIsAttandenceTaken()==false){
                        mStrAttendanceId = ""+attendanceListModelClass.getBatches().get(position-1).getId();
                        count = 0;
                        getSupportActionBar().setTitle("Take Attendance");
                        mBtntakeattendance.setText("Take Attendance");
                        callApiToLoadAttendanceDetails(""+mStrAttendanceId);
                    }else{
                        mStrAttendanceId = ""+attendanceListModelClass.getBatches().get(position-1).getId();
                        count = 1;
                        getSupportActionBar().setTitle("Edit Attendance");
                        mBtntakeattendance.setText("Save");
                        callApiToLoadAttendanceDetails(""+mStrAttendanceId);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });

        mSwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mSwipeToRefresh!=null && mSwipeToRefresh.isRefreshing()) {
                    mSwipeToRefresh.setRefreshing(false);
                }
            }
        });

    }

    private void callApiToLoadAttendanceDetails(String mStrattendanceId) {
        if(!CommonUtil.isInternetAvailable(MultipleMyNewStudentAttendanceActivity.this)){
            return;
        }

        new OkHttpRequest(MultipleMyNewStudentAttendanceActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_BATCH_WISE_ATTENDANCE_DETAILS,
                RequestParam.multiplegetAttendanceDetails(""+mStrattendanceId,
                        ""+date),
                RequestParam.getNull(),
                CODE_BATCH_WISE_ATTENDANCE_DETAILS,
                true,this);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            case CODE_BATCH_WISE_ATTENDANCE_LIST:
                Log.e("batch list",""+response);
                final Gson batchGson = new Gson();
                try{
                    attendanceListModelClass = batchGson
                            .fromJson(response, MultipleAttendanceListModelClass.class);

                    if (attendanceListModelClass.getStatus().equals(Constants.SUCCESS_CODE)) {
                        mArrListBatches=new ArrayList<>();
                        mArrListBatches = attendanceListModelClass.getBatches();
                        if (mArrListBatches != null && mArrListBatches.size() > 0) {
                            setadapterForBatchList();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            /*case CODE_BATCH_DETAILS:
                Log.e("students",""+response);
                final Gson studentGson = new Gson();
                try{
                    mAlstudentsbatchwise = new ArrayList<>();
                    batchDetailsModel = studentGson.fromJson(response,BatchDetailsModel.class);
                    if(batchDetailsModel.getStatus().equals(Constants.SUCCESS_CODE)) {
                        if (batchDetailsModel.getBatchDetail().getStudents() != null &&
                                batchDetailsModel.getBatchDetail().getStudents().size() > 0) {
                            mAlstudentsbatchwise = batchDetailsModel.getBatchDetail().getStudents();
                            setStudentwiseAdapter(mAlstudentsbatchwise);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;*/
            case TAKE_ATTENDANCE_BATCHWISE:
                Log.e("attendance response",""+response);
                try{
                    JSONObject jObjResponse = new JSONObject(response);
                    if (jObjResponse.has("status")) {
                        if (jObjResponse.getString("status").equals("success")) {
                            finish();
                        }
                    }
                    Toast.makeText(this, "" + jObjResponse.getString("message"), Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case CODE_BATCH_WISE_ATTENDANCE_DETAILS:
                Log.e("attendance details",""+response);
                final Gson attendanceGson = new Gson();
                try{
                    attendanceDetailsModelClass = attendanceGson.fromJson(response,MultipleAttendanceDetailsModelClass.class);
                    if(attendanceDetailsModelClass.getBatchDetail().getStudents()!=null
                     && attendanceDetailsModelClass.getBatchDetail().getStudents().size()>0){
                        mAlattendancetakenstudentslist = attendanceDetailsModelClass.getBatchDetail().getStudents();

                        setStudentwiseAdapter(mAlattendancetakenstudentslist);

                        if(count==0){
                            for(int i = 0 ; i < mAlattendancetakenstudentslist.size() ; i++){
                                mAlattendancetakenstudentslist.get(i).setSelected(false);
                                multipleStudentAttendanceAdapter.notifyDataSetChanged();
                            }
                        }else if(count==1){
                            for(int i = 0 ; i < mAlattendancetakenstudentslist.size() ; i++){
                                if(mAlattendancetakenstudentslist.get(i).getAttStatus().equalsIgnoreCase("P")){
                                    mAlattendancetakenstudentslist.get(i).setSelected(true);
                                    multipleStudentAttendanceAdapter.notifyDataSetChanged();
                                }else{
                                    mAlattendancetakenstudentslist.get(i).setSelected(false);
                                    multipleStudentAttendanceAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setStudentwiseAdapter(ArrayList<MultipleAttendanceDetailsModelClass.BatchDetail.Student> mAlstudentsbatchwise) {
        multipleStudentAttendanceAdapter =
                new MultipleStudentAttendanceAdapter(MultipleMyNewStudentAttendanceActivity.this,mAlstudentsbatchwise,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRvbatchwiseStudents.setLayoutManager(linearLayoutManager);
        mRvbatchwiseStudents.setAdapter(multipleStudentAttendanceAdapter);
        multipleStudentAttendanceAdapter.notifyDataSetChanged();
    }

    private void setadapterForBatchList() {
        mAlbatches = new ArrayList<>();

        if(mArrListBatches.size()>0 && mArrListBatches!=null){
            for(int i = 0 ; i < mArrListBatches.size() ; i++){
                mAlbatches.add(i,""+mArrListBatches.get(i).getName());
            }
        }

        mAlbatches.add(0,"Select Batch");
        ArrayAdapter<String> partnerAdapter = new ArrayAdapter<>(MultipleMyNewStudentAttendanceActivity.this,android.R.layout.simple_list_item_1,mAlbatches);
        mSpinnerbatch.setAdapter(partnerAdapter);
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onSwitchClickEvent(int position, boolean value) {
        mAlattendancetakenstudentslist.get(position).setSelected(value);
        multipleStudentAttendanceAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.take_atte_btn:
                takeAttendance();
                /*if(count==0){
                    multipletakeAttendance();
                }*/
                /*else{
                    Toast.makeText(this, "You can't take attendance multiple times", Toast.LENGTH_SHORT).show();
                    mAlids.clear();
                    mAlabsentIds.clear();
                }*/
                break;
        }
    }

    private void takeAttendance() {

        if(mAlids!=null && mAlids.size()>0){
            mAlids.clear();
        }

        if(mAlabsentIds.size()>0 && mAlabsentIds!=null){
            mAlabsentIds.clear();
        }

        for(int i = 0 ; i < mAlattendancetakenstudentslist.size() ; i++){
            if(mAlattendancetakenstudentslist.get(i).isSelected()==true){
                mAlids.add(""+mAlattendancetakenstudentslist.get(i).getId());
            }else{
                mAlids.remove(""+mAlattendancetakenstudentslist.get(i).getId());
                mAlabsentIds.add(""+mAlattendancetakenstudentslist.get(i).getId());
            }
        }

        StringBuilder stringBuilder = new StringBuilder("");
        for(int i = 0 ; i <  mAlids.size() ; i++){
            stringBuilder.append(mAlids.get(i)).append(",");
        }

        mStrSelectedStudentIds = stringBuilder.toString();
        if(mStrSelectedStudentIds.endsWith(",")){
            mStrSelectedStudentIds = mStrSelectedStudentIds.substring(0,mStrSelectedStudentIds.length()-1);
        }

        StringBuilder stringBuilder1 = new StringBuilder("");
        for(int i = 0 ; i < mAlabsentIds.size() ; i++){
            stringBuilder1.append(mAlabsentIds.get(i)).append(",");
        }

        mStrAbsentIds = stringBuilder1.toString();
        if(mStrAbsentIds.endsWith(",")){
            mStrAbsentIds = mStrAbsentIds.substring(0,mStrAbsentIds.length()-1);
        }

        /*Toast.makeText(this, "present students id : "+mStrSelectedStudentIds, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "absent students id : "+mStrAbsentIds, Toast.LENGTH_SHORT).show();*/

        callApiTakeAttendance(mStrSelectedStudentIds,mStrAbsentIds);
    }

    private void callApiTakeAttendance(String mStrSelectedStudentIds, String mStrAbsentIds) {

        if(!CommonUtil.isInternetAvailable(MultipleMyNewStudentAttendanceActivity.this)){
            return;
        }

        new OkHttpRequest(MultipleMyNewStudentAttendanceActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_TAKE_ATTENDANCE_BATCHWISE,
                RequestParam.multipletakeAttendance(""+mStrSelectedBatchId,
                        ""+mStrSelectedStudentIds,
                        ""+mStrAbsentIds),
                RequestParam.getNull(),
                TAKE_ATTENDANCE_BATCHWISE,
                true,this);
    }
}
