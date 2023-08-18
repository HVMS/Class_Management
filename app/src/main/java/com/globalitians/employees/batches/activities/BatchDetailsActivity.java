package com.globalitians.employees.batches.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.batches.adapter.AssignedFacultyAdapter;
import com.globalitians.employees.batches.adapter.AssignedStudentAdapter;
import com.globalitians.employees.batches.model.BatchDetailsModel;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.dashboard.activities.DashboardEmployeeActivity;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_BATCH_DETAILS;
import static com.globalitians.employees.utility.Constants.CODE_REMOVE_FACULTY;
import static com.globalitians.employees.utility.Constants.CODE_REMOVE_STUDENT;

public class BatchDetailsActivity extends AppCompatActivity
        implements OkHttpInterface,
        View.OnClickListener,
        AssignedStudentAdapter.OnCloseImageClickListner,
        AssignedFacultyAdapter.OnFacultyCloseLsitner {

    private CustomTextView mTxtcreatedby;
    private CustomTextView mTxtcreatedDate;
    private CustomTextView mTxtfacultycount;
    private CustomTextView mTxtstudentscount;

    private RecyclerView rvFacultylist;
    private RecyclerView rvStudentlist;

    private ImageView mIveditfaculty;
    private ImageView mIveditstudents;

    private ArrayList<BatchDetailsModel.BatchDetail.Faculty> mArrlistfaculty;
    private ArrayList<BatchDetailsModel.BatchDetail.Student> mArrliststudents;

    private String mStrremovedFacultyIds="";

    private BatchDetailsModel batchDetailsModel;
    private String mStrBatchId="";

    private AssignedStudentAdapter assignedStudentAdapter;
    private AssignedFacultyAdapter assignedFacultyAdapter;
    private String mStrremovedStuedntIds="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(BatchDetailsActivity.this);
        setContentView(R.layout.activity_batch_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Batch Details");

        findViews();
        init();
        Intent intent=getIntent();
        if(intent!=null) {
            mStrBatchId=""+intent.getStringExtra("key_batch_id");
            callApiToGetBatchDetails();
        }
    }

    private void init() {
        mArrlistfaculty = new ArrayList<>();
        mArrliststudents = new ArrayList<>();
    }

    @SuppressLint("WrongViewCast")
    private void findViews() {

        mTxtcreatedby = findViewById(R.id.tv_title_created_actual);
        mTxtcreatedDate = findViewById(R.id.tv_actual_created_date);
        mTxtfacultycount = findViewById(R.id.tv_actual_no_of_faculties);
        mTxtstudentscount = findViewById(R.id.tv_actual_no_of_students);
        mIveditfaculty = findViewById(R.id.iv_edit_faculties);
        mIveditstudents = findViewById(R.id.iv_edit_students);

        rvFacultylist = findViewById(R.id.rv_faculty_list);
        rvStudentlist = findViewById(R.id.rv_student_list);

        mIveditfaculty.setOnClickListener(this);
        mIveditstudents.setOnClickListener(this);

    }

    private void callApiToGetBatchDetails() {

        if (!CommonUtil.isInternetAvailable(BatchDetailsActivity.this)) {
            return;
        }

        new OkHttpRequest(BatchDetailsActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_BATCH_DETAIL,
                RequestParam.batchDetails(""+mStrBatchId),
                RequestParam.getNull(),
                CODE_BATCH_DETAILS,
                false, this);
    }

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        if (response == null) {
            response = "null";
        }

        switch (requestId) {
            case CODE_BATCH_DETAILS:
                Log.e("batch details",""+response);
                final Gson batchDetailsGson = new Gson();
                try {
                    batchDetailsModel = batchDetailsGson
                            .fromJson(response, BatchDetailsModel.class);
                    if(batchDetailsModel.getStatus().equals(Constants.SUCCESS_CODE)){
                        setBatchDetails(batchDetailsModel);
                        if(batchDetailsModel.getBatchDetail().getStudents()!=null
                        && batchDetailsModel.getBatchDetail().getStudents().size()>0){
                            mArrliststudents = batchDetailsModel.getBatchDetail().getStudents();
                            setAssignedStudentsToAdapter(mArrliststudents);
                        }else{
                            Toast.makeText(this, "No Students Assigned Yet", Toast.LENGTH_SHORT).show();
                        }
                        if(batchDetailsModel.getBatchDetail().getFaculties()!=null
                        && batchDetailsModel.getBatchDetail().getFaculties().size()>0){
                            mArrlistfaculty = batchDetailsModel.getBatchDetail().getFaculties();
                            setAssignedFacultiesAdapter(mArrlistfaculty);
                        }else{
                            Toast.makeText(this, "No Faculties Assigned Yet", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CODE_REMOVE_FACULTY:
                Log.e("removed faculty",""+response);
                try{
                    JSONObject jObjResponse = new JSONObject(response);
                    if (jObjResponse.has("status")) {
                        if (jObjResponse.getString("status").equals("success")) {
                            Toast.makeText(this, "" + jObjResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case CODE_REMOVE_STUDENT:
                Log.e("removed student",""+response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("status")){
                       if(jsonObject.getString("status").equals("success")){
                           Toast.makeText(this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                       }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setAssignedFacultiesAdapter(ArrayList<BatchDetailsModel.BatchDetail.Faculty> mArrlistfaculty) {
        mTxtfacultycount.setText(""+mArrlistfaculty.size());

        assignedFacultyAdapter = new AssignedFacultyAdapter(BatchDetailsActivity.this,mArrlistfaculty,this);
        GridLayoutManager manager = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
        rvFacultylist.setLayoutManager(manager);
        rvFacultylist.setAdapter(assignedFacultyAdapter);
        assignedFacultyAdapter.notifyDataSetChanged();
    }

    private void setAssignedStudentsToAdapter(ArrayList<BatchDetailsModel.BatchDetail.Student> mArrliststudents) {
        mTxtstudentscount.setText(""+mArrliststudents.size());

        assignedStudentAdapter = new AssignedStudentAdapter(BatchDetailsActivity.this,mArrliststudents,this);
        GridLayoutManager manager1 = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        rvStudentlist.setLayoutManager(manager1);
        rvStudentlist.setAdapter(assignedStudentAdapter);
        assignedStudentAdapter.notifyDataSetChanged();
    }

    private void setBatchDetails(BatchDetailsModel batchDetailsModel) {
        getSupportActionBar().setTitle(""+batchDetailsModel.getBatchDetail().getName());

        mTxtcreatedDate.setText(""+batchDetailsModel.getBatchDetail().getCreatedAt());
        mTxtcreatedby.setText(""+batchDetailsModel.getBatchDetail().getEditedUserName());
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_employee, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);

        MenuItem itemadd = menu.findItem(R.id.action_add);
        itemadd.setVisible(false);

        MenuItem item1 = menu.findItem(R.id.action_settings);
        item1.setVisible(false);

        MenuItem itemEdit = menu.findItem(R.id.action_edit);
        itemEdit.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (id == R.id.action_edit) {
            Intent yourIntent = new Intent(this, AddNewBatchActivity.class);
            //Bundle b = new Bundle();
            //b.putSerializable("user",modelStudent);
            //modelStudent is static
            yourIntent.putExtra("modelBatch", ""+mStrBatchId); //pass bundle to your intent
            startActivity(yourIntent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_edit_faculties:
                Intent intentFaculty = new Intent(BatchDetailsActivity.this,FacultyAccessActivity.class);
                intentFaculty.putParcelableArrayListExtra("modelArraylist_same",mArrlistfaculty);
                intentFaculty.putExtra("batch_id_same",""+mStrBatchId);
                startActivity(intentFaculty);
                break;
            case R.id.iv_edit_students:
                Intent intent = new Intent(BatchDetailsActivity.this,AssignStudentActivity.class);
                intent.putParcelableArrayListExtra("modelArraylist",mArrliststudents);
                intent.putExtra("batch_id",""+mStrBatchId);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRemoveStudent(int position) {
        mStrremovedStuedntIds = ""+mArrliststudents.get(position).getId();
        mArrliststudents.remove(position);
        setAssignedStudentsToAdapter(mArrliststudents);
        callApiToRemoveStudents(mStrremovedStuedntIds);
    }

    private void callApiToRemoveStudents(String mStrremovedStuedntIds) {
        if(!CommonUtil.isInternetAvailable(BatchDetailsActivity.this)){
            return;
        }

        new OkHttpRequest(BatchDetailsActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_ASSIGN_STUDENTS,
                RequestParam.removeStudent(""+mStrBatchId,
                        ""+ mStrremovedStuedntIds),
                RequestParam.getNull(),
                CODE_REMOVE_STUDENT,
                false,this);
    }

    @Override
    public void onFacultyRemove(int position) {
        mStrremovedFacultyIds = ""+mArrlistfaculty.get(position).getId();
        mArrlistfaculty.remove(position);
        setAssignedFacultiesAdapter(mArrlistfaculty);
        callApiToRemoveFaculty();
    }

    private void callApiToRemoveFaculty() {
        if(!CommonUtil.isInternetAvailable(BatchDetailsActivity.this)){
            return;
        }

        new OkHttpRequest(BatchDetailsActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_ASSIGN_FACULTIES,
                RequestParam.removeFaculty(""+mStrBatchId,
                        ""+mStrremovedFacultyIds),
                RequestParam.getNull(),
                CODE_REMOVE_FACULTY,
                false,this);
    }
}


