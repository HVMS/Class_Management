package com.globalitians.employees.faculty.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.employee.activities.AddEmployeeActivity;
import com.globalitians.employees.faculty.model.FacultyDetailsModel;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import de.hdodenhof.circleimageview.CircleImageView;
import okio.GzipSink;

import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_FACULTY_DETAILS;
import static com.globalitians.employees.utility.Constants.KEY_INQUIRY_DATE;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_FACULTY_ID;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_INQUIRY_ID;

public class FacultyDetailsActivity extends AppCompatActivity implements OkHttpInterface {

    private String strIntentFacultyId="";
    private FacultyDetailsModel facultyDetailsModel;
    private CustomTextView mTxtname;
    private CustomTextView mTxtmobieno;
    private CustomTextView mTxtbirthdate;
    private CustomTextView mTxtemail;
    private CustomTextView mTxtaddress;
    private CustomTextView mTxtjobtype;
    private CustomTextView mTxtdesgination;
    private CustomTextView mTxtjoiningdate;
    private CustomTextView mTxtsalary;
    private CustomTextView mTxtcourses;
    private CircularImageView mCirclefacultyImage;
    private CustomTextView mTxtStandards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(FacultyDetailsActivity.this);
        setContentView(R.layout.activity_faculty_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFacultyId();
        findViews();
    }

    private void findViews() {
        mTxtname = findViewById(R.id.tv_actual_name);
        mTxtmobieno = findViewById(R.id.tv_actual_mobile_no);
        mTxtbirthdate = findViewById(R.id.tv_actual_birthdate);
        mTxtemail = findViewById(R.id.tv_actual_email);
        mTxtaddress = findViewById(R.id.tv_actual_address);

        mTxtjobtype = findViewById(R.id.tv_actual_job_type);
        mTxtjoiningdate = findViewById(R.id.tv_actual_joining_date);
        mTxtsalary = findViewById(R.id.tv_actual_salary);
        mTxtdesgination = findViewById(R.id.tv_actual_deesignation);
        mCirclefacultyImage = findViewById(R.id.profile_of_faculty);
        mTxtcourses = findViewById(R.id.tv_actual_courses);
        mTxtStandards = findViewById(R.id.tv_actual_standards);
    }

    private void getFacultyId() {
        Intent intent = getIntent();
        if(intent!=null){
            strIntentFacultyId = intent.getStringExtra(KEY_INTENT_FACULTY_ID);
            if(!CommonUtil.isNullString(""+strIntentFacultyId)){
                callApiToLoadFacultyDetails();
            }
        }
    }

    private void callApiToLoadFacultyDetails() {

        if(!CommonUtil.isInternetAvailable(FacultyDetailsActivity.this)){
            return;
        }

        new OkHttpRequest(FacultyDetailsActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_FACULTY_DETAILS,
                RequestParam.getFacultyDetails(""+strIntentFacultyId),
                RequestParam.getNull(),
                CODE_FACULTY_DETAILS,
                true,this);
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_employee, menu);
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
            Intent yourIntent = new Intent(this, AddNewFacultyActivity.class);
            yourIntent.putExtra("modelFaculty", ""+strIntentFacultyId); //pass bundle to your intent
            startActivity(yourIntent);
            finish();
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
            case CODE_FACULTY_DETAILS:
                Log.e("Faculty Details",""+response);
                final Gson gson = new Gson();
                try{
                    facultyDetailsModel = gson.fromJson(response,FacultyDetailsModel.class);
                    if(facultyDetailsModel.getFacultyDetail()!=null){
                        setFacultyDetails(facultyDetailsModel);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setFacultyDetails(FacultyDetailsModel facultyDetailsModel) {

        getSupportActionBar().setTitle(""+facultyDetailsModel.getFacultyDetail().getName());

        if(!CommonUtil.isNullString(""+facultyDetailsModel.getFacultyDetail().getImage())){
            CommonUtil.setCircularImageForUser(this,mCirclefacultyImage,""+facultyDetailsModel.getFacultyDetail().getImage());
        }else{
            mCirclefacultyImage.setImageResource(R.drawable.ic_user_round);
        }

        mTxtname.setText(""+facultyDetailsModel.getFacultyDetail().getName());

        mTxtaddress.setText(""+facultyDetailsModel.getFacultyDetail().getAddress());
        mTxtbirthdate.setText(""+facultyDetailsModel.getFacultyDetail().getDob());
        mTxtdesgination.setText(""+facultyDetailsModel.getFacultyDetail().getDesignation());
        mTxtmobieno.setText(""+facultyDetailsModel.getFacultyDetail().getContactNo());
        mTxtjoiningdate.setText(""+facultyDetailsModel.getFacultyDetail().getJoiningDate());
        mTxtsalary.setText(""+facultyDetailsModel.getFacultyDetail().getSalary());
        mTxtjobtype.setText(""+facultyDetailsModel.getFacultyDetail().getJobType());
        mTxtemail.setText(""+facultyDetailsModel.getFacultyDetail().getEmail());

        StringBuilder stringBuilder = new StringBuilder("");
        String strCourses = "";

        for(int i = 0 ; i < facultyDetailsModel.getFacultyDetail().getCourses().size() ; i++){
            stringBuilder.append(""+facultyDetailsModel.getFacultyDetail().getCourses().get(i).getName()).append(", ");
        }

        strCourses = stringBuilder.substring(0,stringBuilder.length()-1);
        if(strCourses.endsWith(",")){
            strCourses = strCourses.substring(0,strCourses.length()-1);
        }

        mTxtcourses.setText(""+strCourses);

        StringBuilder stringBuilder1  = new StringBuilder("");
        String strStandards = "";

        for(int i = 0 ; i < facultyDetailsModel.getFacultyDetail().getStandards().size() ; i++){
            stringBuilder1.append(""+facultyDetailsModel.getFacultyDetail().getStandards().get(i).getName()).append(", ");
        }

        strStandards = stringBuilder1.substring(0,stringBuilder1.length()-1);
        if(strStandards.endsWith(",")){
            strStandards = strStandards.substring(0,strStandards.length()-1);
        }

        mTxtStandards.setText(""+strStandards);
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }
}
