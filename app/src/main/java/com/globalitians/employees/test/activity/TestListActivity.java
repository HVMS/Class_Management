package com.globalitians.employees.test.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.customviews.CustomButton;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.employee.activities.EmployeeLoginActivity;
import com.globalitians.employees.other.activities.AppSettingsActivity;
import com.globalitians.employees.test.adapter.AddMarksAdapter;
import com.globalitians.employees.test.adapter.StudentMarksAdapter;
import com.globalitians.employees.test.adapter.TestListAdapter;
import com.globalitians.employees.test.model.ModelTestList;
import com.globalitians.employees.test.model.TestDetailsModel;
import com.globalitians.employees.test.model.TestStudentsModel;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.LogUtil;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_ADD_MARKS_FOR_TEST;
import static com.globalitians.employees.utility.Constants.CODE_TEST_DETAILS;
import static com.globalitians.employees.utility.Constants.CODE_TEST_LIST;
import static com.globalitians.employees.utility.Constants.CODE_TEST_STUDENTS;
import static com.globalitians.employees.utility.Constants.KEY_LOGGEDIN_USER_ID;
import static com.globalitians.employees.utility.Constants.LOGIN_PREFRENCES;

public class TestListActivity extends AppCompatActivity implements OkHttpInterface, View.OnClickListener, TestListAdapter.TestViewListener, AddMarksAdapter.AddMarksListener {

    private SwipeRefreshLayout mRefreshTestList;
    private RecyclerView rvTestList;
    private ArrayList<ModelTestList.Test> mAlTestList = null;
    private ArrayList<TestStudentsModel.TestDetail.TestStudent> mAlTestStudents = null;
    private TestListAdapter testListAdapter;

    private BottomSheetDialog mDialogAddMarks;
    private BottomSheetDialog mDialogViewEditMarks;
    private RecyclerView mRvAddStudentMarks;
    private CustomTextView tvTestName;
    private CustomTextView tvBatchName;
    private CustomTextView tvTotalMarks;
    private LinearLayout llDatetimeContainer;
    private CustomTextView tvTestDate;
    private CustomTextView tvTestTime;
    private String mStrSelectedTestId;

    private CustomTextView viewEdit_tvTestName;
    private CustomTextView viewEdit_tvBatchName;
    private CustomTextView viewEdit_tvTotalMarks;
    private LinearLayout viewEdit_llDatetimeContainer;
    private CustomTextView viewEdit_tvTestDate;
    private CustomTextView viewEdit_tvTestTime;

    //view edit marks
    private CustomTextView tvNoMarksAdded;
    private RecyclerView rvStudentsMarks;

    private ArrayList<TestDetailsModel.TestDetail.StudentsMark> mAlStudentMarks = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(TestListActivity.this);
        setContentView(R.layout.activity_test_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        findViews();
        initializeAddMarksBottomSheet();
        initializeViewMarksBottomSheet();
        callApiToLoadTestList();
    }

    private void findViews() {
        rvTestList = findViewById(R.id.rv_tests);
        mRefreshTestList = findViewById(R.id.refresh_test);
        mRefreshTestList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callApiToLoadTestList();
            }
        });
    }

    private void init() {
        mAlTestList = new ArrayList<>();
    }

    private void initializeAddMarksBottomSheet() {
        final View mViewAddStudentMarks = getLayoutInflater().inflate(R.layout.bottom_sheet_add_marks, null);
        mDialogAddMarks = new BottomSheetDialog(TestListActivity.this);
        mDialogAddMarks.setContentView(mViewAddStudentMarks);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            CommonUtil.setWhiteNavigationBar(mDialogAddMarks);
        }
        mRvAddStudentMarks = mViewAddStudentMarks.findViewById(R.id.rv_add_marks);
        final EditText edtObtainedMarks = mViewAddStudentMarks.findViewById(R.id.edt_obtained_marks);
        final Switch switchStatus = mViewAddStudentMarks.findViewById(R.id.switch_status);

        tvTestName = (CustomTextView) mViewAddStudentMarks.findViewById(R.id.tv_test_name);
        tvBatchName = (CustomTextView) mViewAddStudentMarks.findViewById(R.id.tv_batch_name);
        tvTotalMarks = (CustomTextView) mViewAddStudentMarks.findViewById(R.id.tv_total_marks);
        llDatetimeContainer = (LinearLayout) mViewAddStudentMarks.findViewById(R.id.ll_datetime_container);
        tvTestDate = (CustomTextView) mViewAddStudentMarks.findViewById(R.id.tv_test_date);
        tvTestTime = (CustomTextView) mViewAddStudentMarks.findViewById(R.id.tv_test_time);

        final CustomTextView mTvCancel = mViewAddStudentMarks.findViewById(R.id.tv_cancel);
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialogAddMarks != null && mDialogAddMarks.isShowing()) {
                    mDialogAddMarks.dismiss();
                }
            }
        });
        final CustomButton mBtnSave = mViewAddStudentMarks.findViewById(R.id.btn_save);
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialogAddMarks != null && mDialogAddMarks.isShowing()) {
                    mDialogAddMarks.dismiss();
                }
                //marks_studentid_obtainedmarks_P
                final ArrayList<String> alStringMarks = new ArrayList<>();
                for (int i = 0; i < mAlTestStudents.size(); i++) {

                    String strObtainedMarks = "0";
                    if (mAlTestStudents.get(i).getObtained_marks() != null && mAlTestStudents.get(i).getObtained_marks().toString().length() > 0) {
                        strObtainedMarks = "" + mAlTestStudents.get(i).getObtained_marks();
                    } else {
                        strObtainedMarks = "0";
                    }
                    String marks = "marks"
                            + "_" + mAlTestStudents.get(i).getId()
                            + "_" + strObtainedMarks
                            + "_" + mAlTestStudents.get(i).getStatus_present_absent();

                    alStringMarks.add(marks);
                    LogUtil.e(">> marks", "" + marks);

                }
                SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_PREFRENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                final String editedBy = "" + sharedPreferences.getString(KEY_LOGGEDIN_USER_ID, "");

                AlertDialog.Builder builder = new AlertDialog.Builder(TestListActivity.this);
                builder.setMessage("Have You Verified Marks Entered? ");
                builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        callApiToAddMarks(alStringMarks, editedBy);
                    }
                }).setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //nothing
                        if (mDialogAddMarks != null && !mDialogAddMarks.isShowing()) {
                            mDialogAddMarks.show();
                        }
                    }
                    // Create the AlertDialog object and return it
                });

                builder.show();
            }
        });
    }

    private void initializeViewMarksBottomSheet() {
        final View mViewViewStudentMarks = getLayoutInflater().inflate(R.layout.bottom_sheet_test_details, null);
        mDialogViewEditMarks = new BottomSheetDialog(TestListActivity.this);
        mDialogViewEditMarks.setContentView(mViewViewStudentMarks);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            CommonUtil.setWhiteNavigationBar(mDialogViewEditMarks);
        }
        viewEdit_tvTestName = (CustomTextView) mViewViewStudentMarks.findViewById(R.id.tv_test_name);
        viewEdit_tvBatchName = (CustomTextView) mViewViewStudentMarks.findViewById(R.id.tv_batch_name);
        viewEdit_tvTotalMarks = (CustomTextView) mViewViewStudentMarks.findViewById(R.id.tv_total_marks);
        viewEdit_llDatetimeContainer = (LinearLayout) mViewViewStudentMarks.findViewById(R.id.ll_datetime_container);
        viewEdit_tvTestDate = (CustomTextView) mViewViewStudentMarks.findViewById(R.id.tv_test_date);
        viewEdit_tvTestTime = (CustomTextView) mViewViewStudentMarks.findViewById(R.id.tv_test_time);
        rvStudentsMarks = (RecyclerView) mViewViewStudentMarks.findViewById(R.id.rv_students_marks);

        tvNoMarksAdded = mViewViewStudentMarks.findViewById(R.id.tv_no_marks_added);

        final CustomTextView mTvCancel = mViewViewStudentMarks.findViewById(R.id.tv_cancel);
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialogViewEditMarks != null && mDialogViewEditMarks.isShowing()) {
                    mDialogViewEditMarks.dismiss();
                }
            }
        });
    }

    private void callApiToLoadTestList() {
        if (!CommonUtil.isInternetAvailable(TestListActivity.this)) {
            return;
        }

        new OkHttpRequest(TestListActivity.this,
                OkHttpRequest.Method.GET,
                Constants.WS_TEST_LIST,
                RequestParam.loadGetRequestsData(),
                RequestParam.getNull(),
                CODE_TEST_LIST,
                true, this);
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
            case CODE_TEST_LIST:
                final Gson testListGson = new Gson();
                try {
                    ModelTestList modelTestList = testListGson
                            .fromJson(response, ModelTestList.class);

                    if (modelTestList.getStatus().equals(Constants.SUCCESS_CODE)) {
                        mAlTestList = modelTestList.getTests();
                        if (mAlTestList != null && mAlTestList.size() > 0) {
                            setTestListAdapter();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (mRefreshTestList != null && mRefreshTestList.isRefreshing()) {
                    mRefreshTestList.setRefreshing(false);
                }
                break;
            case CODE_TEST_STUDENTS:
                final Gson testStudentsGson = new Gson();
                try {
                    TestStudentsModel modelTestStudents = testStudentsGson
                            .fromJson(response, TestStudentsModel.class);
                    mAlTestStudents = new ArrayList<>();
                    if (modelTestStudents.getStatus().equals(Constants.SUCCESS_CODE)) {
                        tvTestName.setText("" + modelTestStudents.getTestDetail().getTestname());
                        tvBatchName.setText("Batch: " + modelTestStudents.getTestDetail().getBatch_name());
                        tvTotalMarks.setText("Total Marks: " + modelTestStudents.getTestDetail().getTotalmarks());
                        tvTestDate.setText("Date: " + modelTestStudents.getTestDetail().getDate());
                        tvTestTime.setText("Time: " + modelTestStudents.getTestDetail().getTime());

                        if (modelTestStudents.getTestDetail().getTestStudents() != null
                                && modelTestStudents.getTestDetail().getTestStudents().size() > 0) {

                            for (int i = 0; i < modelTestStudents.getTestDetail().getTestStudents().size(); i++) {
                                modelTestStudents.getTestDetail().getTestStudents().get(i).setStatus_present_absent("P");
                            }

                            mAlTestStudents.addAll(modelTestStudents.getTestDetail().getTestStudents());
                            setAdapterForStudentMarks(modelTestStudents, mAlTestStudents);
                        }
                        if (mDialogAddMarks != null && !mDialogAddMarks.isShowing()) {
                            mDialogAddMarks.show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CODE_ADD_MARKS_FOR_TEST:
                try {
                    JSONObject jobjresponse = new JSONObject(response);
                    if (jobjresponse.has("message")) {
                        Toast.makeText(TestListActivity.this, "" + jobjresponse.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    callApiToLoadTestList();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case CODE_TEST_DETAILS:
                final Gson testDetailsGson = new Gson();
                try {
                    TestDetailsModel modelTestDetails = testDetailsGson
                            .fromJson(response, TestDetailsModel.class);

                    if (modelTestDetails.getStatus().equals(Constants.SUCCESS_CODE)) {
                        viewEdit_tvTestName.setText("" + modelTestDetails.getTestDetail().getTestname());
                        viewEdit_tvBatchName.setText("" + modelTestDetails.getTestDetail().getBatchName());
                        viewEdit_tvTotalMarks.setText("Total Marks: " + modelTestDetails.getTestDetail().getTotalmarks());
                        viewEdit_tvTestDate.setText("Date: " + modelTestDetails.getTestDetail().getDate());
                        viewEdit_tvTestTime.setText("Time: " + modelTestDetails.getTestDetail().getTime());

                        mAlStudentMarks = new ArrayList<>();
                        if (modelTestDetails.getTestDetail().getStudentsMarks() != null
                                &&
                                modelTestDetails.getTestDetail().getStudentsMarks().size() > 0) {
                            tvNoMarksAdded.setVisibility(View.GONE);
                            mAlStudentMarks.addAll(modelTestDetails.getTestDetail().getStudentsMarks());
                            setAdapterForStudentMarksDetails(modelTestDetails);
                        } else {
                            tvNoMarksAdded.setVisibility(View.VISIBLE);
                        }
                        if (mDialogViewEditMarks != null && !mDialogViewEditMarks.isShowing()) {
                            mDialogViewEditMarks.show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setAdapterForStudentMarks(TestStudentsModel testStudents, ArrayList<TestStudentsModel.TestDetail.TestStudent> mAlTestStudents) {
        AddMarksAdapter addMarksAdapter = new AddMarksAdapter(TestListActivity.this, testStudents, mAlTestStudents, this);
        LinearLayoutManager manager = new LinearLayoutManager(TestListActivity.this);
        mRvAddStudentMarks.setLayoutManager(manager);
        mRvAddStudentMarks.setAdapter(addMarksAdapter);
    }

    private void setAdapterForStudentMarksDetails(TestDetailsModel modelTestDetails) {
        StudentMarksAdapter marksListAdapter = new StudentMarksAdapter(TestListActivity.this, modelTestDetails.getTestDetail().getStudentsMarks(), "" + modelTestDetails.getTestDetail().getPassingMarks());
        LinearLayoutManager manager = new LinearLayoutManager(TestListActivity.this);
        rvStudentsMarks.setLayoutManager(manager);
        rvStudentsMarks.setAdapter(marksListAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
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

    private void setTestListAdapter() {
        testListAdapter = new TestListAdapter(TestListActivity.this, mAlTestList, this);
        LinearLayoutManager manager = new LinearLayoutManager(TestListActivity.this);
        rvTestList.setLayoutManager(manager);
        rvTestList.setAdapter(testListAdapter);
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_filter_month:
                Toast.makeText(this, "In Progress", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ll_filter_courses:
                Toast.makeText(this, "In Progresss", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ll_filter_student_types:
                Toast.makeText(this, "In Progressss", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onTestViewClick(int position) {
        mStrSelectedTestId = "" + mAlTestList.get(position).getId();
        if (mAlTestList.get(position).isMarks_added()) {
            callApiToLoadTestDetails("" + mAlTestList.get(position).getId());
        } else {
            callApiToLoadTestStudents("" + mAlTestList.get(position).getId());
        }
    }

    private void callApiToLoadTestDetails(String strTestId) {
        if (!CommonUtil.isInternetAvailable(TestListActivity.this)) {
            return;
        }

        new OkHttpRequest(TestListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_TEST_DETAILS,
                RequestParam.testDetails(strTestId),
                RequestParam.getNull(),
                CODE_TEST_DETAILS,
                true, this);
    }


    private void callApiToLoadTestStudents(String strTestId) {
        if (!CommonUtil.isInternetAvailable(TestListActivity.this)) {
            return;
        }

        new OkHttpRequest(TestListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_TEST_STUDENTS,
                RequestParam.testStudents(strTestId),
                RequestParam.getNull(),
                CODE_TEST_STUDENTS,
                true, this);
    }

    private void callApiToAddMarks(ArrayList<String> mAlMarks,
                                   String editedBy) {
        if (!CommonUtil.isInternetAvailable(TestListActivity.this)) {
            return;
        }

        new OkHttpRequest(TestListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_ADD_MARKS,
                RequestParam.addStudentMarks(mStrSelectedTestId,
                        mAlMarks,
                        editedBy),
                RequestParam.getNull(),
                CODE_ADD_MARKS_FOR_TEST,
                true, this);
    }

    @Override
    public void onStatusChange(int position, boolean status) {
        if (status) {
            mAlTestStudents.get(position).setStatus_present_absent("P");
        } else {
            mAlTestStudents.get(position).setStatus_present_absent("A");
        }
    }

    @Override
    public void onMarksAdd(int position, String marks) {
        mAlTestStudents.get(position).setObtained_marks(marks);
    }
}
