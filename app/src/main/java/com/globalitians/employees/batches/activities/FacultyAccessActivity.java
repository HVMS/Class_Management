package com.globalitians.employees.batches.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.batches.adapter.FacultyAssignAdapter;
import com.globalitians.employees.batches.model.BatchDetailsModel;
import com.globalitians.employees.dashboard.activities.DashboardEmployeeActivity;
import com.globalitians.employees.faculty.model.FacultyListModel;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_ASSIGN_FACULTIES;
import static com.globalitians.employees.utility.Constants.CODE_FACULTY_LIST;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;

public class FacultyAccessActivity extends AppCompatActivity
        implements OkHttpInterface,
        FacultyAssignAdapter.OnSwitchClickEvent, View.OnClickListener {

    private RecyclerView mRvfacultylist;
    private Button btnSubmit;

    private ArrayList<String> mAlfacultySelectedIds;
    private String mStrSelectedFacultyIds="";

    private FacultyAssignAdapter facultyAssignAdapter;
    private FacultyListModel facultyModel;
    private ArrayList<FacultyListModel.Faculty> mAlfacultylist;
    private ArrayList<BatchDetailsModel.BatchDetail.Faculty> mArrlistSelectedFaculties;

    private String mStrbatchid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(FacultyAccessActivity.this);
        setContentView(R.layout.activity_faculty_access);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViews();
        init();
        getArrayListFromBatchDetailsOfSelectedStudents();
        callApiToLoadFacultyList();
    }

    private void getArrayListFromBatchDetailsOfSelectedStudents() {
        Intent intent = getIntent();
        if(intent!=null){
            mStrbatchid = intent.getStringExtra("batch_id_same");
            mArrlistSelectedFaculties = intent.getParcelableArrayListExtra("modelArraylist_same");
        }
    }

    private void callApiToLoadFacultyList() {

        if(!CommonUtil.isInternetAvailable(FacultyAccessActivity.this)){
            return;
        }

        new OkHttpRequest(FacultyAccessActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_FACULTY_LIST,
                RequestParam.getFacultyList(""+CommonUtil.getSharedPrefrencesInstance(FacultyAccessActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,"")),
                RequestParam.getNull(),
                CODE_FACULTY_LIST,
                true,this);
    }

    private void init() {
        mAlfacultylist = new ArrayList<>();
        mAlfacultySelectedIds = new ArrayList<>();
    }

    private void findViews() {
        mRvfacultylist = findViewById(R.id.rv_faculty_list);
        btnSubmit = findViewById(R.id.ASSIGNBTN);

        btnSubmit.setOnClickListener(this);
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
            case CODE_FACULTY_LIST:
                Log.e("faculties",""+response);
                final Gson gson = new Gson();
                try{
                    facultyModel = gson.fromJson(response,FacultyListModel.class);
                    if(facultyModel.getFaculties()!=null &&
                        facultyModel.getFaculties().size()>0){
                        mAlfacultylist = facultyModel.getFaculties();
                        setFacultyAdapter(mAlfacultylist);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case CODE_ASSIGN_FACULTIES:
                Log.e(">>>",""+response);
                try{
                    JSONObject jObjResponse = new JSONObject(response);
                    if (jObjResponse.has("status")) {
                        if (jObjResponse.getString("status").equals("success")) {
                            startActivity(new Intent(FacultyAccessActivity.this, DashboardEmployeeActivity.class));
                        }
                    }
                    Toast.makeText(this, "" + jObjResponse.getString("message"), Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setFacultyAdapter(ArrayList<FacultyListModel.Faculty> mAlfacultylist) {
        if(facultyAssignAdapter!=null){
            facultyAssignAdapter.notifyDataSetChanged();
        }else{

            for(int i = 0 ; i < mArrlistSelectedFaculties.size() ; i++){
                for(int j = 0 ; j < mAlfacultylist.size() ; j++){
                    if (mArrlistSelectedFaculties.get(i).getId().equals(mAlfacultylist.get(j).getId())) {
                        Log.e("name", "" + mAlfacultylist.get(j).getName());
                        mAlfacultylist.remove(j);
                    }
                }
            }

            facultyAssignAdapter = new FacultyAssignAdapter(FacultyAccessActivity.this,
                    mAlfacultylist,this);
            LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
            mRvfacultylist.setLayoutManager(manager);
            mRvfacultylist.setAdapter(facultyAssignAdapter);
            facultyAssignAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onClickSwitch(int position, boolean value) {
        mAlfacultylist.get(position).setSelected(value);
        facultyAssignAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ASSIGNBTN:
                for(int i = 0 ; i < mAlfacultylist.size() ; i++){
                    if(mAlfacultylist.get(i).isSelected()==true){
                        mAlfacultySelectedIds.add(""+mAlfacultylist.get(i).getId());
                    }else if(mAlfacultylist.get(i).isSelected()==false){
                        mAlfacultySelectedIds.remove(""+mAlfacultylist.get(i).getId());
                    }
                }
                callApiToAssignFaculties();
                break;
        }
    }

    @SuppressLint("NewApi")
    private void callApiToAssignFaculties() {

        if(!CommonUtil.isInternetAvailable(FacultyAccessActivity.this)){
            return;
        }

        mStrSelectedFacultyIds = String.join(",",mAlfacultySelectedIds);
        if(mStrSelectedFacultyIds.endsWith(",")){
            mStrSelectedFacultyIds = mStrSelectedFacultyIds.substring(0,mStrSelectedFacultyIds.length()-1);
        }

        new OkHttpRequest(FacultyAccessActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_ASSIGN_FACULTIES,
                RequestParam.assignFaculties(""+mStrbatchid,
                        ""+mStrSelectedFacultyIds),
                RequestParam.getNull(),
                CODE_ASSIGN_FACULTIES,
                true,this);

    }
}
