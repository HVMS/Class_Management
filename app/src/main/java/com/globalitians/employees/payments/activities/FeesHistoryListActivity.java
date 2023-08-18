package com.globalitians.employees.payments.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.payments.adapters.AllStudentFeesPaymentAdapter;
import com.globalitians.employees.payments.adapters.StudentFeesPaymentPagerAdapter;
import com.globalitians.employees.payments.model.ModelAllFeesPayments;
import com.globalitians.employees.payments.model.ModelFeesList;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_GET_STUDENT_PAYMENT_LIST;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_STUDENT_ID;

public class FeesHistoryListActivity extends AppCompatActivity implements View.OnClickListener, OkHttpInterface {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String strStudentId = "";

    private ArrayList<ModelAllFeesPayments.Payment> mAlAllPayments;
    private AllStudentFeesPaymentAdapter mAllFeesPaymentListAdapter = null;


    private TextView tvNoData;

    //When clicking on single raw of student
    private String singalStudentId = null;
    private LinearLayout linViewPagerLayout;
    private TabLayout tabLayoutFeesPayments;
    private ViewPager viewPagerFeesPayments;
    private StudentFeesPaymentPagerAdapter feesPaymentPagerAdapter;

    private CircularImageView ivUserImage;
    private PaidFeesAmountDataReceivedListener mPaidFeesAmountDataListener;
    private UnPaidFeesAmountDataReceivedListener mUnPaidFeesAmountDataListener;
    private TotalFeesAmountDataReceivedListener mTotalPaidFeesAmountDataListener;

    public interface PaidFeesAmountDataReceivedListener {
        void onPaidStudentFeesDataReceived(ModelFeesList model);
    }

    public interface UnPaidFeesAmountDataReceivedListener {
        void onUnPaidStudentFeesDataReceived(ModelFeesList model);
    }

    public interface TotalFeesAmountDataReceivedListener {
        void onTotalStudentFeesDataReceived(ModelFeesList model);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(FeesHistoryListActivity.this);
        setContentView(R.layout.activity_fees_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null && getIntent().hasExtra(KEY_INTENT_STUDENT_ID)) {
            strStudentId = getIntent().getStringExtra(KEY_INTENT_STUDENT_ID);
        }

        findViews();
        //defaultCallForAttendanceList(singalStudentId);
        callApiToGetStudentPaymentDetails("" + strStudentId);

    }

    public void setStudentFeesReceivedDataListener(PaidFeesAmountDataReceivedListener listener) {
        this.mPaidFeesAmountDataListener = listener;
    }

    public void setUnpaidFeesReceivedDataListener(UnPaidFeesAmountDataReceivedListener listener) {
        this.mUnPaidFeesAmountDataListener = listener;
    }

    public void setTotalFeesReceivedDataListener(TotalFeesAmountDataReceivedListener listener) {
        this.mTotalPaidFeesAmountDataListener = listener;
    }

    private void findViews() {

        tvNoData = findViewById(R.id.tvNoData);

        ivUserImage = findViewById(R.id.iv_user);
        //tvFeesAmount=findViewById(R.id.tvFeesAmount);
        //tvStudentName=findViewById(R.id.tv_student_name);

        linViewPagerLayout = (LinearLayout) findViewById(R.id.linStudentFeesPaymentDetails);
        viewPagerFeesPayments = (ViewPager) findViewById(R.id.viewPagerFeesPayment);
        feesPaymentPagerAdapter = new StudentFeesPaymentPagerAdapter(getSupportFragmentManager());
        viewPagerFeesPayments.setAdapter(feesPaymentPagerAdapter);
        viewPagerFeesPayments.setOffscreenPageLimit(3);

        tabLayoutFeesPayments = (TabLayout) findViewById(R.id.tabsFeesPayment);
        tabLayoutFeesPayments.setupWithViewPager(viewPagerFeesPayments);


    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle("Payment Reports");
    }

    private void callApiToGetStudentPaymentDetails(String student_id) {
        if (!CommonUtil.isInternetAvailable(FeesHistoryListActivity.this)) {
            //finish();
            return;
        }

        if (CommonUtil.isNullString(student_id)) {
            setTitle("Payment Reports");
        }

        new OkHttpRequest(FeesHistoryListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_FEES_LIST,
                RequestParam.fees_list(
                        "" + student_id),
                RequestParam.getNull(),
                CODE_GET_STUDENT_PAYMENT_LIST,
                true, this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        try {
            Log.e(">>> attendace : ", "" + response);
            if (response == null) {
                Toast.makeText(this, "Error in Getting response", Toast.LENGTH_SHORT).show();
                return;
            }

            switch (requestId) {
                case CODE_GET_STUDENT_PAYMENT_LIST:
                    final Gson gsonAttendanceStudent = new Gson();
                    try {
                        ModelFeesList modelStudentPaymentList =
                                gsonAttendanceStudent.fromJson(response, ModelFeesList.class);

                        if (modelStudentPaymentList.getStatus().equals(Constants.SUCCESS_CODE)) {
                            mPaidFeesAmountDataListener.onPaidStudentFeesDataReceived(modelStudentPaymentList);
                            mUnPaidFeesAmountDataListener.onUnPaidStudentFeesDataReceived(modelStudentPaymentList);
                            mTotalPaidFeesAmountDataListener.onTotalStudentFeesDataReceived(modelStudentPaymentList);
                            CommonUtil.setCircularImageForUser(FeesHistoryListActivity.this, ivUserImage, modelStudentPaymentList.getStudentDetail().get(0).getStudent_image().replace("pic", "student"));
                        } else{
                            Toast.makeText(FeesHistoryListActivity.this, "Something going wrong. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }
}
