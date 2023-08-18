package com.globalitians.employees.employee.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.employee.model.ModelClassForEmployeeDetails;
import com.globalitians.employees.students.activities.AddStudentActivity;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import org.spongycastle.cert.ocsp.Req;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_EMPLOYEE_DETAILS;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_EMPLOYEE_ID;

public class EmployeeDetailsActivity extends AppCompatActivity
        implements OkHttpInterface {

    private String mStremployeeid = "";
    private ModelClassForEmployeeDetails modelClassForEmployeeDetails;
    private CustomTextView mTxtname;
    private CustomTextView mTxtmobieno;
    private CustomTextView mTxtbirthdate;
    private CustomTextView mTxtemail;
    private CustomTextView mTxtaddress;
    private CustomTextView mTxtjobtype;
    private CustomTextView mTxtdesgination;
    private CustomTextView mTxtjoiningdate;
    private CustomTextView mTxtsalary;
    private CircularImageView mCircleemployeeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(EmployeeDetailsActivity.this);
        setContentView(R.layout.activity_employee_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getEmployeeId();
        findViews();
        callApiToLoadEmployeeData();
    }

    private void callApiToLoadEmployeeData() {

        if (!CommonUtil.isInternetAvailable(EmployeeDetailsActivity.this)) {
            return;
        }

        new OkHttpRequest(EmployeeDetailsActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_EMPLOYEE_DETAILS,
                RequestParam.getFacultyDetails("" + mStremployeeid),
                RequestParam.getNull(),
                CODE_EMPLOYEE_DETAILS,
                true, this);
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
        mCircleemployeeImage = findViewById(R.id.profile_of_employee);
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
            Intent yourIntent = new Intent(this, AddEmployeeActivity.class);
            //Bundle b = new Bundle();
            //b.putSerializable("user",modelStudent);
            //modelStudent is static
            yourIntent.putExtra("modelEmployee", ""+mStremployeeid); //pass bundle to your intent
            startActivity(yourIntent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getEmployeeId() {
        Intent intent = getIntent();
        if (intent != null) {
            mStremployeeid = intent.getStringExtra(KEY_INTENT_EMPLOYEE_ID);
        }
    }

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        Log.e("<><>", "" + response);

        if (CommonUtil.isNullString(response)) {
            return;
        }

        switch (requestId) {
            case CODE_EMPLOYEE_DETAILS:
                Log.e("Employee details", "" + response);
                final Gson gson = new Gson();
                try {
                    modelClassForEmployeeDetails = gson.fromJson(response, ModelClassForEmployeeDetails.class);
                    if (modelClassForEmployeeDetails.getEmployeeDetail() != null) {
                        setEmployeeDetails(modelClassForEmployeeDetails);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    private void setEmployeeDetails(ModelClassForEmployeeDetails modelClassForEmployeeDetails) {

        getSupportActionBar().setTitle("" + modelClassForEmployeeDetails.getEmployeeDetail().getName());

        mTxtname.setText("" + modelClassForEmployeeDetails.getEmployeeDetail().getName());

        if(!CommonUtil.isNullString(""+modelClassForEmployeeDetails.getEmployeeDetail().getImage())){
            CommonUtil.setCircularImageToGlide(EmployeeDetailsActivity.this,
                    mCircleemployeeImage,""+modelClassForEmployeeDetails.getEmployeeDetail().getImage());
        }else{
            mCircleemployeeImage.setImageResource(R.drawable.ic_user_round);
        }

        mTxtaddress.setText(""+modelClassForEmployeeDetails.getEmployeeDetail().getAddress());
        mTxtbirthdate.setText(""+modelClassForEmployeeDetails.getEmployeeDetail().getDob());
        mTxtmobieno.setText(""+modelClassForEmployeeDetails.getEmployeeDetail().getContactNo());
        mTxtdesgination.setText(""+modelClassForEmployeeDetails.getEmployeeDetail().getDesignation());
        mTxtsalary.setText(""+modelClassForEmployeeDetails.getEmployeeDetail().getSalary());
        mTxtjobtype.setText(""+modelClassForEmployeeDetails.getEmployeeDetail().getJobType());
        mTxtjoiningdate.setText(""+modelClassForEmployeeDetails.getEmployeeDetail().getJoiningDate());
        mTxtemail.setText(""+modelClassForEmployeeDetails.getEmployeeDetail().getEmail());
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

}
