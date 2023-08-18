package com.globalitians.employees.inquiry.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.inquiry.model.ModelInquiryDetailsResponse;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import static com.globalitians.employees.utility.CommonUtil.getSharedPrefrencesInstance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_INQUIRY_DETAILS;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_INQUIRY_ID;

public class InquiryDetailsActivity extends AppCompatActivity implements OkHttpInterface {
    private EditText edtName;
    private EditText edtMobileNo;
    private EditText edtFeedback;
    private EditText edtReference;
    private EditText edtBranch;
    private TextView tvSave;
    
    private String strIntentInquiryId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(InquiryDetailsActivity.this);
        setContentView(R.layout.activity_inquiry_details);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getStudentIdFromIntent();
        findViews();
        callApiToLoadInquiryDetails();
    }

    private void getStudentIdFromIntent() {
        Intent intentInquiryId = getIntent();
        if (intentInquiryId != null) {
            strIntentInquiryId  = "" + intentInquiryId.getStringExtra(KEY_INTENT_INQUIRY_ID);
        }
    }

    private void callApiToLoadInquiryDetails() {
        if (!CommonUtil.isInternetAvailable(InquiryDetailsActivity.this)) {
            return;
        }

        new OkHttpRequest(InquiryDetailsActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_INQUIRY_DETAILS,
                RequestParam.inquiryDetails(strIntentInquiryId,
                        ""+getSharedPrefrencesInstance(InquiryDetailsActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,"")),
                RequestParam.getNull(),
                CODE_INQUIRY_DETAILS,
                true, this);
    }

    private void findViews() {
        edtName = (EditText) findViewById(R.id.edtName);
        edtMobileNo = (EditText) findViewById(R.id.edtMobileNo);
        edtBranch = (EditText) findViewById(R.id.edtBranch);
        edtReference = (EditText) findViewById(R.id.edtReference);
        tvSave=(TextView)findViewById(R.id.tv_save);
    }


    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        Log.e(">>inqiory response >> ",""+response);
        if (response == null) {
            response = "null";
        }

        switch (requestId) {
            case CODE_INQUIRY_DETAILS:
                final Gson gson = new Gson();
                try {
                    ModelInquiryDetailsResponse modelInquiryDetails = gson
                            .fromJson(response, ModelInquiryDetailsResponse.class);
                    if (modelInquiryDetails.getStatus().equals(Constants.SUCCESS_CODE)) {
                        setInquiryDetails(modelInquiryDetails);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setInquiryDetails(ModelInquiryDetailsResponse modelInquiryDetailsResponse) {
        edtName.setText("" + modelInquiryDetailsResponse.getInquiryDetail().getFname() + " " +
                modelInquiryDetailsResponse.getInquiryDetail().getLname());
        edtMobileNo.setText("" + modelInquiryDetailsResponse.getInquiryDetail().getMobileno());
        edtBranch.setText(""+modelInquiryDetailsResponse.getInquiryDetail().getBranch().getName());
        edtReference.setText(""+modelInquiryDetailsResponse.getInquiryDetail().getReference());

    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable
            error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }
}
