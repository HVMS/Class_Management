package com.globalitians.employees.batches.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.batches.adapter.AssignedStudentAdapter;
import com.globalitians.employees.batches.adapter.BottomSheetBatchListAdpater;
import com.globalitians.employees.batches.adapter.StudentAssignAdapter;
import com.globalitians.employees.batches.model.BatchDetailsModel;
import com.globalitians.employees.batches.model.BatchListModel;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.dashboard.activities.DashboardEmployeeActivity;
import com.globalitians.employees.multiple_attendance.model.MultipleStudentsBatchListModelClass;
import com.globalitians.employees.students.models.ModelStudent;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_ASSIGN_STUDENTS;
import static com.globalitians.employees.utility.Constants.CODE_BATCH_DETAILS;
import static com.globalitians.employees.utility.Constants.CODE_BATCH_LIST;
import static com.globalitians.employees.utility.Constants.CODE_REMOVE_STUDENT;
import static com.globalitians.employees.utility.Constants.CODE_STUDENTS_BATCHES;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;

public class AssignStudentActivity extends AppCompatActivity
        implements OkHttpInterface,
        AssignedStudentAdapter.OnCloseImageClickListner,
        StudentAssignAdapter.OnCheckedStudentListner, View.OnClickListener,
        BottomSheetBatchListAdpater.OnBatchCLickListner{

    private RecyclerView mRvallstudents;
    private RecyclerView mRvselectedStudents;
    private Button mBtnassign;
    private CustomTextView mTxtStudentNameTitle;

    private ArrayList<BatchDetailsModel.BatchDetail.Student> mArraySelectedStudents;

    private ModelStudent modelStudent;
    private SwipeRefreshLayout mSwipeToRefresh;
    private StudentAssignAdapter studentAssignAdapter;
    private AssignedStudentAdapter assignedStudentAdapter;

    private ArrayList<String> mAlselectedStudentsIds;
    private String mStrbatchId="";
    private String mStrSelectedStudentsIds="";
    private String mStrRemovedIds="";

    /*bottom sheet bar for batch filter*/
    private BottomSheetDialog mDialogFilterBatch;
    private FloatingActionButton mFabfilterbatch;

    private RecyclerView mRvbatchlist;
    private BatchListModel batchModel;
    private ArrayList<BatchListModel.Batch> mAlbatchlist;
    private BottomSheetBatchListAdpater bottomSheetBatchListAdpater;
    private String mStrSelectedBatchId="";

    private ArrayList<BatchDetailsModel.BatchDetail.Student> mAlStudentOfBatches;
    private BatchDetailsModel batchDetailsModel;

    private ImageView mIvSelectBatchImage;
    private TextView mTxtSelectBatchTitle;

    private MultipleStudentsBatchListModelClass studentsBatchListModelClass;
    private ArrayList<MultipleStudentsBatchListModelClass.BatchDetail.Student> mAlbatchlistofstudents = new ArrayList<>();
    private int positionRemoved=0;

    private int positionAdd=0;
    private boolean removedStudentCalled = false;
    private boolean addStudentCalled = false;
    private String mStrSelectedBatchName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(AssignStudentActivity.this);
        setContentView(R.layout.activity_assign_student);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViews();
        init();
        getArrayListFromBatchDetailsOfSelectedStudents();
    }

    private void initBatchListBottomSheetBar() {
        final View mViewFilterBatch = getLayoutInflater().inflate(R.layout.bottom_sheet_filter_batches, null);
        mDialogFilterBatch = new BottomSheetDialog(AssignStudentActivity.this);
        mDialogFilterBatch.setContentView(mViewFilterBatch);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            CommonUtil.setWhiteNavigationBar(mDialogFilterBatch);
        }

        mRvbatchlist = mDialogFilterBatch.findViewById(R.id.rv_batch_list);

        callApiToLoadBatchList();

        CustomTextView tvCancel = mViewFilterBatch.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogFilterBatch.isShowing() == true) {
                    mDialogFilterBatch.dismiss();
                }
            }
        });

        mDialogFilterBatch.show();
    }

    private void callApiToLoadBatchList() {
        if(!CommonUtil.isInternetAvailable(AssignStudentActivity.this)){
            return;
        }

        new OkHttpRequest(AssignStudentActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_BATCH_LIST,
                RequestParam.batchlist(""+CommonUtil.getSharedPrefrencesInstance(AssignStudentActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,"")),
                RequestParam.getNull(),
                CODE_BATCH_LIST,
                false,this);
    }

    private void getArrayListFromBatchDetailsOfSelectedStudents() {
        Intent intent = getIntent();
        if (intent != null) {
            mArraySelectedStudents = intent.getParcelableArrayListExtra("modelArraylist");
            mStrbatchId = ""+intent.getStringExtra("batch_id");
            setSelectedStudents(mArraySelectedStudents);
        }
    }

    private void setSelectedStudents(ArrayList<BatchDetailsModel.BatchDetail.Student> mArraySelectedStudents) {
        assignedStudentAdapter = new AssignedStudentAdapter(AssignStudentActivity.this, mArraySelectedStudents, this);
        GridLayoutManager manager1 = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
        mRvselectedStudents.setLayoutManager(manager1);
        mRvselectedStudents.setAdapter(assignedStudentAdapter);
        assignedStudentAdapter.notifyDataSetChanged();
    }

    private void init() {
        mAlselectedStudentsIds = new ArrayList<>();
        mAlbatchlist = new ArrayList<>();
        mAlStudentOfBatches = new ArrayList<>();
        mAlbatchlistofstudents = new ArrayList<>();
    }

    private void findViews() {
        mTxtSelectBatchTitle = findViewById(R.id.tv_show_text);
        mTxtStudentNameTitle = findViewById(R.id.tv_student_name);
        mIvSelectBatchImage = findViewById(R.id.iv_extra);
        mRvallstudents = findViewById(R.id.rv_all_student_list);
        mRvselectedStudents = findViewById(R.id.rv_selected_student_list);
        mBtnassign = findViewById(R.id.btn_save);
        mSwipeToRefresh = findViewById(R.id.swipeToRefreshBatch);

        mFabfilterbatch = findViewById(R.id.fabFilterBatch);

        mBtnassign.setOnClickListener(this);
        mFabfilterbatch.setOnClickListener(this);

        mSwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setSelectedStudents(mArraySelectedStudents);
                if(mSwipeToRefresh!=null && mSwipeToRefresh.isRefreshing()){
                    mSwipeToRefresh.setRefreshing(false);
                }
            }
        });
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

        if (response == null) {
            return;
        }

        switch (requestId) {
            case CODE_ASSIGN_STUDENTS:
                Log.e("assigned students",""+response);
                try{
                    JSONObject jObjResponse = new JSONObject(response);
                    if (jObjResponse.has("status")) {
                        if (jObjResponse.getString("status").equals("success")) {
                            startActivity(new Intent(AssignStudentActivity.this, DashboardEmployeeActivity.class));
                        }
                    }
                    Toast.makeText(this, "" + jObjResponse.getString("message"), Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case CODE_REMOVE_STUDENT:
                Log.e("removed",""+response);
                try{
                    JSONObject jObjResponse = new JSONObject(response);
                    if (jObjResponse.has("status")) {
                        if (jObjResponse.getString("status").equals("success")) {
                            Toast.makeText(this, "" + jObjResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CODE_BATCH_LIST:
                Log.e("batches",""+response);
                final Gson batches = new Gson();
                try{
                    batchModel = batches.fromJson(response,BatchListModel.class);
                    if(batchModel.getStatus().equalsIgnoreCase("success")){
                        if(batchModel.getBatches()!=null && batchModel.getBatches().size()>0){

                            mAlbatchlist = new ArrayList<>();

                            /* now take only completed batches and store that in to array list */

                            for(int i = 0 ; i < batchModel.getBatches().size() ; i++){
                                if(batchModel.getBatches().get(i).getStatus().equalsIgnoreCase("Running")){
                                    mAlbatchlist.add(batchModel.getBatches().get(i));
                                }
                            }

                            /* now set the array list to the bottom sheet recycler view */

                            setBottomSheetRecyclerView(mAlbatchlist);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case CODE_BATCH_DETAILS:
                Log.e("batches students",""+response);
                final Gson batchDetailsGson = new Gson();
                try{
                    batchDetailsModel = batchDetailsGson.fromJson(response,BatchDetailsModel.class);
                    if(batchDetailsModel.getStatus().equals(Constants.SUCCESS_CODE)){

                        if(batchDetailsModel.getBatchDetail().getStudents()!=null &&
                          batchDetailsModel.getBatchDetail().getStudents().size()>0){

                            mTxtSelectBatchTitle.setVisibility(View.GONE);
                            mIvSelectBatchImage.setVisibility(View.GONE);

                            /* check batch id of selected batch from filter is matching with
                            * assigned batch id or not according to apply logic*/

                            if(mAlStudentOfBatches.size()>0 && mAlStudentOfBatches!=null){
                                mAlStudentOfBatches.clear();
                            }

                            /* now check whether the by default students which are shown above
                               * they are present in filtered students or not if
                               * they are then remove otherwise add it */

                            mAlStudentOfBatches = new ArrayList<>();
                            mAlStudentOfBatches = batchDetailsModel.getBatchDetail().getStudents();


                             if(mAlStudentOfBatches==null || mAlStudentOfBatches.size()==0){
                                 mTxtStudentNameTitle.setVisibility(View.GONE);
                                 Toast.makeText(this, "No Data Found Filter More", Toast.LENGTH_SHORT).show();
                             }else{
                                 mSwipeToRefresh.setVisibility(View.VISIBLE);
                                 mRvallstudents.setVisibility(View.VISIBLE);
                                 mBtnassign.setVisibility(View.VISIBLE);
                                 setAllStudentAdapter(mAlStudentOfBatches);
                             }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case CODE_STUDENTS_BATCHES:
                Log.e("student batch list",""+response);
                final Gson gson = new Gson();
                try{
                    studentsBatchListModelClass = gson.fromJson(response,MultipleStudentsBatchListModelClass.class);

                    if(studentsBatchListModelClass.getBatchDetail().getStudents()!=null
                        && studentsBatchListModelClass.getBatchDetail().getStudents().size()>0){

                        if(removedStudentCalled==true){

                            mAlbatchlistofstudents = studentsBatchListModelClass.getBatchDetail().getStudents();

                            StringBuilder stringBuilder = new StringBuilder("");

                            for(int i = 0 ; i < mAlbatchlistofstudents.size() ; i++){

                                BatchDetailsModel.BatchDetail.Student student = new BatchDetailsModel.BatchDetail.Student();
                                student.setId(mArraySelectedStudents.get(positionRemoved).getId());
                                student.setFirstName(mArraySelectedStudents.get(positionRemoved).getFirstName());
                                student.setLastName(mArraySelectedStudents.get(positionRemoved).getLastName());
                                student.setImage(mArraySelectedStudents.get(positionRemoved).getImage());

                                if(mArraySelectedStudents.get(positionRemoved).getId().equals(mAlbatchlistofstudents.get(i).getId())){
                                    mAlStudentOfBatches.add(i,student);
                                    mAlStudentOfBatches.get(i).setSelected(false);
                                }

                                assignedStudentAdapter.notifyDataSetChanged();
                                studentAssignAdapter.notifyDataSetChanged();

                            }

                            stringBuilder.append(mArraySelectedStudents.get(positionRemoved).getId()).append(",");
                            /*Toast.makeText(this, ""+stringBuilder.toString(), Toast.LENGTH_SHORT).show();*/

                            assignedStudentAdapter.notifyDataSetChanged();
                            studentAssignAdapter.notifyDataSetChanged();
                            mArraySelectedStudents.remove(positionRemoved);

                            callApiToLoadBatchStudents(mStrSelectedBatchId);

                            mStrRemovedIds = stringBuilder.toString();
                            if(mStrRemovedIds.endsWith(",")){
                                mStrRemovedIds = mStrRemovedIds.substring(0,mStrRemovedIds.length()-1);
                            }

                            /*callApiToRemoveStudents(mStrRemovedIds);*/

                        }
                        /*else if(addStudentCalled == true){

                            if(mAlbatchlistofstudents!=null && mAlbatchlistofstudents.size()>0){
                                mAlbatchlistofstudents.clear();
                            }

                            mAlbatchlistofstudents = new ArrayList<>();

                            mAlbatchlistofstudents = studentsBatchListModelClass.getBatchDetail().getStudents();

                            for(int i = 0 ; i < mAlbatchlistofstudents.size() ; i++){

                                BatchDetailsModel.BatchDetail.Student student = new BatchDetailsModel.BatchDetail.Student();
                                *//*student.setId(mAlStudentOfBatches.get(positionAdd).getId());*//*
                                student.setImage(mAlStudentOfBatches.get(positionAdd).getImage());
                                student.setFirstName(mAlStudentOfBatches.get(positionAdd).getFirstName());
                                student.setLastName(mAlStudentOfBatches.get(positionAdd).getLastName());

                                if(mAlStudentOfBatches.get(positionAdd).getId().equals(mAlbatchlistofstudents.get(i).getId())){
                                    mArraySelectedStudents.add(i,student);
                                }

                                mArraySelectedStudents.add(0,student);

                                assignedStudentAdapter.notifyDataSetChanged();
                                studentAssignAdapter.notifyDataSetChanged();

                            }

                        }*/
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
        }

    private void setBottomSheetRecyclerView(ArrayList<BatchListModel.Batch> mAlbatchlist) {

        if(bottomSheetBatchListAdpater!=null){
            bottomSheetBatchListAdpater.notifyDataSetChanged();
        }

        bottomSheetBatchListAdpater = new
                BottomSheetBatchListAdpater(AssignStudentActivity.this,mAlbatchlist,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRvbatchlist.setLayoutManager(linearLayoutManager);
        mRvbatchlist.setAdapter(bottomSheetBatchListAdpater);
        bottomSheetBatchListAdpater.notifyDataSetChanged();

    }

    private void setAllStudentAdapter(ArrayList<BatchDetailsModel.BatchDetail.Student> mAlStudentOfBatches) {
        mTxtStudentNameTitle.setVisibility(View.VISIBLE);
        mTxtStudentNameTitle.setText("Selected Batch Name : "+mStrSelectedBatchName);

        studentAssignAdapter = new StudentAssignAdapter(AssignStudentActivity.this, mAlStudentOfBatches,this);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        mRvallstudents.setLayoutManager(manager);
        mRvallstudents.setAdapter(studentAssignAdapter);
        studentAssignAdapter.notifyDataSetChanged();

        for(int i = 0 ; i < mArraySelectedStudents.size(); i++){
            for(int j = 0 ; j < mAlStudentOfBatches.size() ; j++){
                if(mArraySelectedStudents.get(i).getId().equals(mAlStudentOfBatches.get(j).getId())){
                    Log.e("removed",""+mAlStudentOfBatches.get(j).getFirstName());
                    /*mAlStudentOfBatches.remove(j);*/
                    mAlStudentOfBatches.get(j).setSelected(true);
                    studentAssignAdapter.notifyDataSetChanged();
                }else if(mArraySelectedStudents.get(i).getId().equals(mAlStudentOfBatches.get(j).getId())){
                    Log.e("not removed",""+mAlStudentOfBatches.get(j).getFirstName());
                    mAlStudentOfBatches.get(j).setSelected(false);
                    studentAssignAdapter.notifyDataSetChanged();
                }
            }
        }

    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onRemoveStudent(int position) {
        /*Toast.makeText(this, "removing is in progress", Toast.LENGTH_SHORT).show();*/

        positionRemoved = position;
        addStudentCalled = false;
        removedStudentCalled = true;

        /* now check the removed students in how many batches according to that we have to add to particular model class*/

        callApiToCheckStudentsBatches(""+mArraySelectedStudents.get(position).getId());

    }

    private void callApiToCheckStudentsBatches(String studentId) {
        if(!CommonUtil.isInternetAvailable(AssignStudentActivity.this)){
            return;
        }

        new OkHttpRequest(AssignStudentActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_STUDENTS_BATCHES,
                RequestParam.multiplegetStudentsBatches(""+studentId),
                RequestParam.getNull(),
                CODE_STUDENTS_BATCHES,
                false,this);
    }

    @Override
    public void addStudent(int position,boolean value) {
        mAlStudentOfBatches.get(position).setSelected(value);
        studentAssignAdapter.notifyDataSetChanged();

        positionAdd = position;
        addStudentCalled = true;
        removedStudentCalled = false;

        BatchDetailsModel.BatchDetail.Student student = new BatchDetailsModel.BatchDetail.Student();
        student.setId(mAlStudentOfBatches.get(position).getId());
        student.setImage(mAlStudentOfBatches.get(position).getImage());
        student.setFirstName(mAlStudentOfBatches.get(position).getFirstName());
        student.setLastName(mAlStudentOfBatches.get(position).getLastName());

        mArraySelectedStudents.add(0,student);
        callApiToLoadBatchStudents(mStrSelectedBatchId);

        assignedStudentAdapter.notifyDataSetChanged();
        studentAssignAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_save:
                /*Toast.makeText(this, "assigning is in progress", Toast.LENGTH_SHORT).show();*/

                StringBuilder stringBuilder = new StringBuilder("");

                for(int i = 0 ; i < mArraySelectedStudents.size(); i++){
                    stringBuilder.append(""+mArraySelectedStudents.get(i).getId()).append(",");
                    Log.e("names",""+mArraySelectedStudents.get(i).getFirstName());
                }

                mStrSelectedStudentsIds = stringBuilder.toString();
                if(mStrSelectedStudentsIds.endsWith(",")){
                    mStrSelectedStudentsIds = mStrSelectedStudentsIds.substring(0,mStrSelectedStudentsIds.length()-1);
                }
                Log.e("ids",""+mStrSelectedStudentsIds);
                CallApiToAssignStudents(mStrSelectedStudentsIds);
                break;
            case R.id.fabFilterBatch:
                initBatchListBottomSheetBar();
                break;
        }
    }

    @SuppressLint("NewApi")
    private void CallApiToAssignStudents(String mStrSelectedStudentsIds) {

        if(!CommonUtil.isInternetAvailable(AssignStudentActivity.this)){
            return;
        }

        new OkHttpRequest(AssignStudentActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_ASSIGN_STUDENTS,
                RequestParam.assignStudents(""+mStrbatchId,
                        ""+mStrSelectedStudentsIds),
                RequestParam.getNull(),
                CODE_ASSIGN_STUDENTS,
                false,this);
    }

    @Override
    public void onClickBatchItem(int position) {

        if(mDialogFilterBatch!=null && mDialogFilterBatch.isShowing()){
            mDialogFilterBatch.dismiss();
        }

        mStrSelectedBatchId = ""+mAlbatchlist.get(position).getId();
        mStrSelectedBatchName = ""+mAlbatchlist.get(position).getName();
        callApiToLoadBatchStudents(mStrSelectedBatchId);
    }

    private void callApiToLoadBatchStudents(String mStrSelectedBatchId) {
        if(!CommonUtil.isInternetAvailable(AssignStudentActivity.this)){
            return;
        }

        new OkHttpRequest(AssignStudentActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_BATCH_DETAIL,
                RequestParam.batchDetails(""+mStrSelectedBatchId),
                RequestParam.getNull(),
                CODE_BATCH_DETAILS,
                false,this);

    }
}
