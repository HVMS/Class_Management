package com.globalitians.employees.students.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.batches.model.BatchListModel;
import com.globalitians.employees.faculty.model.StandardListModel;
import com.globalitians.employees.filters.adapter.FilterMonthListAdapter;
import com.globalitians.employees.filters.adapter.FilterStudentTypeListAdapter;
import com.globalitians.employees.filters.models.FilterModelCourseList;
import com.globalitians.employees.filters.models.FilterModelMonths;
import com.globalitians.employees.filters.models.FilterModelStudentTypes;
import com.globalitians.employees.students.adapters.FilterBatchListAdapter;
import com.globalitians.employees.students.adapters.FilterCourseListAdapter;
import com.globalitians.employees.students.adapters.FilterStandardListAdapter;
import com.globalitians.employees.students.adapters.StudentFilterPagerAdapter;
import com.globalitians.employees.students.adapters.StudentListAdapter;
import com.globalitians.employees.students.models.ModelStudent;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.LogUtil;
import com.globalitians.employees.utility.PermissionManager;
import com.globalitians.employees.utility.Permissions;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import static com.globalitians.employees.utility.CommonUtil.getSharedPrefrencesInstance;
import static com.globalitians.employees.utility.CommonUtil.playSoundForAttendance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_BATCH_LIST;
import static com.globalitians.employees.utility.Constants.CODE_COURSE_LIST;
import static com.globalitians.employees.utility.Constants.CODE_FILTER_STUDENT;
import static com.globalitians.employees.utility.Constants.CODE_STANDARD_LIST;
import static com.globalitians.employees.utility.Constants.CODE_STUDENTS;
import static com.globalitians.employees.utility.Constants.CODE_STUDENT_SELECTION_FOR_ATTENDANCE;
import static com.globalitians.employees.utility.Constants.INTENT_KEY_COURSE_ID;
import static com.globalitians.employees.utility.Constants.INTENT_KEY_COURSE_NAME;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_STUDENT_COURSE;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_STUDENT_ID;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_STUDENT_IMAGE;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_STUDENT_INOUT;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_STUDENT_NAME;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_STUDENT_UNPAID_AMOUNT;
import static com.globalitians.employees.utility.Constants.KEY_STUDENT_SELECTION;
import static com.globalitians.employees.utility.Constants.KEY_STUDENT_SELECTION_FOR_UNPAID_STUDENT_FEES;
import static com.globalitians.employees.utility.Constants.WS_COURSE_LIST;
import static com.globalitians.employees.utility.Constants.WS_STANDARD_LIST;
import static com.globalitians.employees.utility.Constants.WS_STUDENT_FILTER;

public class StudentListActivity extends AppCompatActivity implements OkHttpInterface, View.OnClickListener, StudentListAdapter.OnStudentListListener, FilterCourseListAdapter.OnCourseListListener, FilterMonthListAdapter.OnMonthListListener, FilterStudentTypeListAdapter.OnStudentTypeClickListener, FilterBatchListAdapter.BatchClickListener, FilterStandardListAdapter.StandardClickListener {

    private ListView mLvStudents = null;
    private ListView mLVCourses = null;
    private ListView mLVMonths = null;
    private ListView mLVStudentTypes = null;
    private RecyclerView mRvBatches = null;
    private RecyclerView mRvStandards = null;

    private LinearLayout mLinCourses;
    private LinearLayout mLinMonths;
    private LinearLayout mLinStudentTypes;
    private LinearLayout mLinBatches;
    private LinearLayout mLinStandards;

    private SwipeRefreshLayout mSwipeToRefresh;
    private View mListFooter;

    private RelativeLayout mRelFilterLayout;

    private TextView mTvFilterCourses;
    private TextView mTvFilterMonths;
    private TextView mTvFilterStudentTypes;
    private TextView mTvFilterBatches;
    private TextView mTvFilterStandards;

    //For all data
    private ArrayList<ModelStudent.Student> mArrListStudents = null;

    private ArrayList<ModelStudent.Student> mArrListRunningStudents = new ArrayList<>();
    private ArrayList<ModelStudent.Student> mArrListCompletedStudents = new ArrayList<>();
    private ArrayList<ModelStudent.Student> mArrListSearchStudents = new ArrayList<>();

    private StudentListAdapter mAdapterStudentList = null;
    private boolean isFromManualAttendance = false;
    private boolean isFromAddFees = false;
    int backCount = 0;
    private boolean isDisplayMoreOptions = false;

    private Switch mSwitch_running_completed;

    //Here, Month Filter also contains Year along.
    private ArrayList<FilterModelMonths> mArrListMonths = new ArrayList<>();
    private ArrayList<FilterModelMonths> mArrListSelectedMonths = new ArrayList<>();

    private ArrayList<FilterModelCourseList.Course> mArrListCourses = new ArrayList<>();
    private ArrayList<Integer> mArrListSelectedCourseIds = new ArrayList<>();
    private ArrayList<Integer> mArrListAppliedCourseIds = new ArrayList<>();

    private ArrayList<FilterModelStudentTypes> mArrListStudentTypes = new ArrayList<>();
    private ArrayList<FilterModelStudentTypes> mArrListSelectedStudentTypes = new ArrayList<>();
    private ArrayList<FilterModelStudentTypes> mArrListAppliedStudentTypes = new ArrayList<>();

    private BatchListModel modelBatchList;
    private ArrayList<BatchListModel.Batch> mArrListBatches = new ArrayList<>();
    private ArrayList<BatchListModel.Batch> mArrListSelectedBatch = new ArrayList<>();

    private ArrayList<StandardListModel.Standard> mArrListStandards = new ArrayList<>();
    private ArrayList<StandardListModel.Standard> mArrListSelectedStandard = new ArrayList<>();

    private FilterCourseListAdapter mAdapterCourseList;
    private FilterMonthListAdapter mAdapterMonthList;
    private FilterStudentTypeListAdapter mAdapterStudentTypeList;
    private FilterBatchListAdapter mAdapterBatchList;
    private FilterStandardListAdapter mAdapterStandardList;

    private Button mBtnApplyCourseFilter;
    private Button mBtnApplyMonthFilter;
    private Button mBtnApplyStudentTypeFilter;
    private Button mBtnApplyBatchFilter;
    private Button mBtnApplyStandardFilter;

    private TextView mTvClearCourseFilter;
    private TextView mTvClearMonthFilter;
    private TextView mTvClearBatchFilter;
    private TextView mTvClearStudentTypesFilter;
    private TextView mTvClearStandardFilter;

    private TextView mTvTotalSelectedBatches;
    private TextView mTvTotalSelectedmonths;
    private TextView mTvTotalSelectedCourses;
    private TextView mTvTotalSelectedStudentTypes;
    private TextView mTvTotalSelectedStandard;

    private int mTotalSelectedMonth = 0;
    private SearchView mSearchView;
    private String mStrSearchValue = "";

    private TextView tv_apply_student_filter;
    private TabLayout tabLayoutStudentList;
    private ViewPager vpStudentList;

    private String mStrFilterMonths = "";
    private String mStrFilterCourseStatus = "";
    private String mStrFilterCourseid = "";
    private String mStrFilterBatchid = "";
    private String mStrFilterStandardId = "";

    private final PermissionManager.PermissionListener permissionListener = new PermissionManager.PermissionListener() {
        @Override
        public void onPermissionsGranted(List<String> perms) {
            if (perms.size() == Permissions.STORAGE_PERMISSIONS.length) {
                //updateImage();
                exportStudentDataToExcel();
            } else {
                LogUtil.i("Add Course Activity >>", "User denied some of required permissions! "
                        + "Even though we have following permissions now, "
                        + "task will still be aborted.\n" + CommonUtil.getStringFromList(perms));
            }
        }

        @Override
        public void onPermissionsDenied(List<String> perms) {
            Toast.makeText(StudentListActivity.this, "Please grant storage permissions.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionRequestRejected() {
        }

        @Override
        public void onPermissionNeverAsked(List<String> perms) {
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(StudentListActivity.this);
        setContentView(R.layout.activity_student_list);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        findViews();
        setListeners();
        getIntentData();
        callApiToLoadStudentList(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (backCount > 0) {
            callApiToLoadStudentList(true);
        }
    }

    private void getIntentData() {
        if (getIntent() != null && getIntent().hasExtra(KEY_STUDENT_SELECTION)) {
            isFromManualAttendance = true;
            isDisplayMoreOptions = true;
            setTitle("--Select Student--");
            mSwitch_running_completed.setVisibility(View.GONE);
        } else if (getIntent() != null && getIntent().hasExtra(KEY_STUDENT_SELECTION_FOR_UNPAID_STUDENT_FEES)) {
            isFromAddFees = true;
            isDisplayMoreOptions = true;
            setTitle("--Select Student--");
            mSwitch_running_completed.setVisibility(View.GONE);
        } else {
            setTitle("Students");
            //default filter call
            if (Constants.IS_COURSE_SUPPORT) {
                callApiToLoadCourseList();
            } else {
                callApiToLoadbatchlist();
                callApiToLoadStandardList();
            }
        }
    }

    private void init() {
        if (mArrListStudents != null && mArrListStudents.size() > 0) {
            mArrListStudents.clear();
        }
        mArrListStudents = new ArrayList<>();
        mArrListSearchStudents = new ArrayList<>();
        mArrListRunningStudents = new ArrayList<>();
        mArrListCompletedStudents = new ArrayList<>();

        mArrListCourses = new ArrayList<>();
        mArrListSelectedCourseIds = new ArrayList<>();
        mArrListAppliedCourseIds = new ArrayList<>();

        mArrListMonths = new ArrayList<>();
        mArrListSelectedMonths = new ArrayList<>();

        initMonthsValues();
        initStudentTypesValues();

        mListFooter = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_list_footer, null);
    }

    private void initStudentTypesValues() {

        mArrListStudentTypes = new ArrayList<>();
        mArrListSelectedStudentTypes = new ArrayList<>();
        mArrListAppliedStudentTypes = new ArrayList<>();

        /*FilterModelStudentTypes modelRunning = new FilterModelStudentTypes();
        modelRunning.setSelected(false);
        modelRunning.setStrStudentType("Running");*/
        FilterModelStudentTypes modelPending = new FilterModelStudentTypes();
        modelPending.setSelected(false);
        modelPending.setStrStudentType("Pending");
        /*FilterModelStudentTypes modelCompleted = new FilterModelStudentTypes();
        modelCompleted.setSelected(false);
        modelCompleted.setStrStudentType("Completed");*/
        FilterModelStudentTypes modelSuspended = new FilterModelStudentTypes();
        modelSuspended.setSelected(false);
        modelSuspended.setStrStudentType("Suspended");

        //mArrListStudentTypes.add(modelRunning);
        mArrListStudentTypes.add(modelPending);
        //mArrListStudentTypes.add(modelCompleted);
        mArrListStudentTypes.add(modelSuspended);

    }

    private void initMonthsValues() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);

        FilterModelMonths model1 = new FilterModelMonths();
        model1.setStrMonth("Jan");
        model1.setMonthNumber("01");
        model1.setStrYear("" + year);
        FilterModelMonths model2 = new FilterModelMonths();
        model2.setStrMonth("Feb");
        model2.setMonthNumber("02");
        model2.setStrYear("" + year);
        FilterModelMonths model3 = new FilterModelMonths();
        model3.setStrMonth("Mar");
        model3.setMonthNumber("03");
        model3.setStrYear("" + year);
        FilterModelMonths model4 = new FilterModelMonths();
        model4.setStrMonth("Apr");
        model4.setMonthNumber("04");
        model4.setStrYear("" + year);
        FilterModelMonths model5 = new FilterModelMonths();
        model5.setStrMonth("May");
        model5.setMonthNumber("05");
        model5.setStrYear("" + year);
        FilterModelMonths model6 = new FilterModelMonths();
        model6.setStrMonth("Jun");
        model6.setMonthNumber("06");
        model6.setStrYear("" + year);
        FilterModelMonths model7 = new FilterModelMonths();
        model7.setStrMonth("Jul");
        model7.setMonthNumber("07");
        model7.setStrYear("" + year);
        FilterModelMonths model8 = new FilterModelMonths();
        model8.setStrMonth("Aug");
        model8.setMonthNumber("08");
        model8.setStrYear("" + year);
        FilterModelMonths model9 = new FilterModelMonths();
        model9.setStrMonth("Sep");
        model9.setMonthNumber("09");
        model9.setStrYear("" + year);
        FilterModelMonths model10 = new FilterModelMonths();
        model10.setStrMonth("Oct");
        model10.setMonthNumber("10");
        model10.setStrYear("" + year);
        FilterModelMonths model11 = new FilterModelMonths();
        model11.setStrMonth("Nov");
        model11.setMonthNumber("11");
        model11.setStrYear("" + year);
        FilterModelMonths model12 = new FilterModelMonths();
        model12.setStrMonth("Dec");
        model12.setMonthNumber("12");
        model12.setStrYear("" + year);

        mArrListMonths.add(model1);
        mArrListMonths.add(model2);
        mArrListMonths.add(model3);
        mArrListMonths.add(model4);
        mArrListMonths.add(model5);
        mArrListMonths.add(model6);
        mArrListMonths.add(model7);
        mArrListMonths.add(model8);
        mArrListMonths.add(model9);
        mArrListMonths.add(model10);
        mArrListMonths.add(model11);
        mArrListMonths.add(model12);
    }

    private void callApiToLoadbatchlist() {
        if (!CommonUtil.isInternetAvailable(StudentListActivity.this)) {
            return;
        }

        new OkHttpRequest(StudentListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_BATCH_LIST,
                RequestParam.batchlist("" + CommonUtil.getSharedPrefrencesInstance(StudentListActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, "")),
                RequestParam.getNull(),
                CODE_BATCH_LIST,
                false,
                this);

    }

    private void callApiToLoadStudentList(boolean isShowDialog) {
        if (!CommonUtil.isInternetAvailable(StudentListActivity.this)) {
            return;
        }

        new OkHttpRequest(StudentListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_STUDENT_LIST,
                RequestParam.studentList(
                        "" + getSharedPrefrencesInstance(StudentListActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, "")),
                RequestParam.getNull(),
                CODE_STUDENTS,
                isShowDialog, this);
    }

    private void callApiToLoadStandardList() {

        if (!CommonUtil.isInternetAvailable(StudentListActivity.this)) {
            return;
        }

        new OkHttpRequest(StudentListActivity.this,
                OkHttpRequest.Method.GET,
                WS_STANDARD_LIST,
                RequestParam.loadGetRequestsData(),
                RequestParam.getNull(),
                CODE_STANDARD_LIST,
                true, this);
    }

    private void findViews() {
        mRelFilterLayout = findViewById(R.id.rel_filter_layout);
        mRelFilterLayout.setVisibility(View.GONE);

        mLinMonths = findViewById(R.id.ll_filter_month);
        mLinCourses = findViewById(R.id.ll_filter_courses);
        mLinStudentTypes = findViewById(R.id.ll_filter_student_types);
        mLinBatches = findViewById(R.id.ll_filter_batch);
        mLinStandards = findViewById(R.id.ll_filter_standard);

        mTvFilterMonths = findViewById(R.id.tv_filter_by_month);
        mTvFilterCourses = findViewById(R.id.tv_filter_by_courses);
        mTvFilterStudentTypes = findViewById(R.id.tv_filter_by_student_types);
        mTvFilterBatches = findViewById(R.id.tv_filter_by_batch);
        mTvFilterStandards = findViewById(R.id.tv_filter_by_standard);

        mSwitch_running_completed = (Switch) findViewById(R.id.switch_running_completed);
        mSwitch_running_completed.setChecked(true);
        mSwitch_running_completed.setText("Running");
        mSwitch_running_completed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setStudentListAdapter(mArrListRunningStudents);
                    mSwitch_running_completed.setText("Running");
                } else {
                    setStudentListAdapter(mArrListCompletedStudents);
                    mSwitch_running_completed.setText("Completed");
                }
            }
        });

        mArrListStudents = new ArrayList<>();
        mArrListRunningStudents = new ArrayList<>();
        mArrListCompletedStudents = new ArrayList<>();
        mArrListSearchStudents = new ArrayList<>();
        mLvStudents = findViewById(R.id.lv_students);

        mSwipeToRefresh = findViewById(R.id.swipeToRefreshStudentList);
        mSwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshStudentListData();
            }
        });
    }

    private void refreshStudentListData() {

        if (mArrListStudents != null && mArrListStudents.size() > 0) {
            mArrListStudents.clear();
        }
        if (mArrListRunningStudents != null && mArrListRunningStudents.size() > 0) {
            mArrListRunningStudents.clear();
        }
        if (mArrListCompletedStudents != null && mArrListCompletedStudents.size() > 0) {
            mArrListCompletedStudents.clear();
        }
        if (mArrListSearchStudents != null && mArrListSearchStudents.size() > 0) {
            mArrListSearchStudents.clear();
        }

        if (mAdapterStudentList != null) {
            mAdapterStudentList.notifyDataSetChanged();
        }

        mArrListStudents = new ArrayList<>();
        mArrListSearchStudents = new ArrayList<>();
        mArrListRunningStudents = new ArrayList<>();
        mArrListCompletedStudents = new ArrayList<>();

        if (CommonUtil.isNullString(mStrSearchValue)) {
            //DEfault service call for student list
            callApiToLoadStudentList(false);
        } else {
            //search value call..
            callApiToFilterStudentList(mStrSearchValue);
        }
    }

    private void setadapterForBatchList(RecyclerView rvBatchList) {
        mAdapterBatchList = new FilterBatchListAdapter(StudentListActivity.this, mArrListBatches, this);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        rvBatchList.setLayoutManager(manager);
        rvBatchList.setAdapter(mAdapterBatchList);
    }

    private void setadapterForStandardList(RecyclerView mRvStandards) {
        mAdapterStandardList = new FilterStandardListAdapter(StudentListActivity.this, mArrListStandards, this);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        mRvStandards.setLayoutManager(manager);
        mRvStandards.setAdapter(mAdapterStandardList);
    }

    private void setListeners() {
        if (Constants.IS_COURSE_SUPPORT) {
            mLinCourses.setOnClickListener(this);
            mLinCourses.setVisibility(View.VISIBLE);

            mLinStudentTypes.setOnClickListener(this);
            mLinStudentTypes.setVisibility(View.VISIBLE);
        } else {
            mLinCourses.setVisibility(View.GONE);
            mLinStudentTypes.setVisibility(View.GONE);
        }
        mLinMonths.setOnClickListener(this);
        mLinBatches.setOnClickListener(this);
        mLinStandards.setOnClickListener(this);
    }


    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        /*
         *
         * {"status":"success","message":"Students Found",
         * "students":
         * [{"id":25,"fname":"Jaimin","lname":"Modi","contact":"9999999956",
         * "branch_name":"Vastral","image":"","parentname":"Dinesh",
         * "parentmobileno":"8888998877","address":"Amarjyot","email":"jaimin@yahoo.in",
         * "reference":"modi","joining_date":"10-06-2018","dob":"07-05-1996",
         * "courses":[{"id":"11","name":"CCC","fees":"2300","duration":"6 Month",
         * "course_status":"Running","book_material":"Yes","bag":"No","certificate":"Yes"}]
         * ,"in_out_status":"Out"},{"id":23,"fname":"Bharga","lname":"Modi","contact":"9999999956","branch_name":"Vastral","image":"http:\/\/globalitians.com\/student\/ZeUrqb9E.jpg","parentname":"Dinesh","parentmobileno":"8888998877","address":"Amarjyot","email":"bhargav@gmail.in","reference":"modi","joining_date":"10-06-2018","dob":"07-05-1996","courses":[{"id":"11","name":"CCC","fees":"2300","duration":"6 Month","course_status":"Running","book_material":"Yes","bag":"No","certificate":"Yes"}],"in_out_status":"Out"},{"id":21,"fname":"Nik","lname":"patel","contact":"9974583968","branch_name":"Odhav","image":"http:\/\/globalitians.com\/student\/J1N2CB73.jpg","parentname":"Nme","parentmobileno":"7895653215","address":"vastral","email":"vastrlbranch@gmail.com","reference":"-----------","joining_date":"10-06-1992","dob":"01-01-1992","courses":[{"id":"11","name":"CCC","fees":"2500","duration":"2 Month","course_status":"Running","book_material":"No","bag":"No","certificate":"Yes"},{"id":"12","name":"Corporate Tally","fees":"15000","duration":"6 Month","course_status":"Running","book_material":"No","bag":"No","certificate":"Yes"}],"in_out_status":"Out"},{"id":17,"fname":"Urvish","lname":"Desai","contact":"9978631800","branch_name":"Vastral","image":"","parentname":"Nasme","parentmobileno":"4567896778","address":"Address","email":"urvishdesai@gmail.com","reference":"FRIEND","joining_date":"01-05-2019","dob":"01-05-2000","courses":[{"id":"14","name":"C ","fees":"4000","duration":"2 Month","course_status":"Running","book_material":"No","bag":"No","certificate":"No"}],"in_out_status":"Out"},{"id":13,"fname":"Digvijaysinh","lname":"jadeja","contact":"9428808619","branch_name":"Vastral","image":"","parentname":"name ","parentmobileno":"3456789045","address":"Address","email":"niharikapatel1118@gmail.com","reference":"-","joining_date":"07-05-2019","dob":"14-05-2010","courses":[{"id":"7","name":"Xpert Tally","fees":"10000","duration":"4 Month","course_status":"Running","book_material":"Yes","bag":"Yes","certificate":"Yes"}],"in_out_status":"Out"},{"id":11,"fname":"Niharika","lname":"Patel","contact":"9974583968","branch_name":"Vastral","image":"","parentname":"Vishnubhai","parentmobileno":"9974583968","address":"Suryam Sky","email":"niharikapatel1192@gmail.com","reference":"-","joining_date":"10-05-2019","dob":"01-01-1992","courses":[{"id":"1","name":"PHP","fees":"15000","duration":"6 Month","course_status":"Running","book_material":"Yes","bag":"Yes","certificate":"Yes"},{"id":"4","name":"Web Designing","fees":"15000","duration":"1 Year","course_status":"Running","book_material":"No","bag":"No","certificate":"No"}],"in_out_status":"Out"}]}
         * */
        if (response == null) {
            response = "null";
        }

        switch (requestId) {
            case CODE_STUDENTS:
                Log.e("CODE STUDENTS >> ", "" + response);
                final Gson studentListGson = new Gson();
                try {
                    ModelStudent modelStudent = studentListGson
                            .fromJson(response, ModelStudent.class);

                    if (modelStudent.getStatus().equals(Constants.SUCCESS_CODE)) {
                        mArrListStudents = modelStudent.getStudents();
                        for (int i = 0; i < mArrListStudents.size(); i++) {
                            if (mArrListStudents.get(i).getId() == 86) {
                                Log.e(">>> 86", "" + mArrListStudents.get(i).getFname());
                            }
                            if (mArrListStudents.get(i).getId() == 87) {
                                Log.e(">>> 87    ", "" + mArrListStudents.get(i).getFname());
                            }
                        }
                        if (mArrListStudents != null && mArrListStudents.size() > 0) {
                            for (int i = 0; i < mArrListStudents.size(); i++) {
                                //display only unpaid amounts records
                                if (mArrListStudents.get(i).getCourse_status().equalsIgnoreCase("running")) {
                                    mArrListRunningStudents.add(mArrListStudents.get(i));
                                } else {
                                    mArrListCompletedStudents.add(mArrListStudents.get(i));
                                }
                            }
                            if (mSwitch_running_completed.isChecked()) {
                                setStudentListAdapter(mArrListRunningStudents);
                            } else {
                                setStudentListAdapter(mArrListCompletedStudents);
                            }
                        }
                    } else {
                        Toast.makeText(this, "" + modelStudent.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if (mSwipeToRefresh.isRefreshing()) {
                        mSwipeToRefresh.setRefreshing(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CODE_FILTER_STUDENT:
                Log.e("FILTER STUDENTS >> ", "" + response);
                if (mArrListSearchStudents != null && mArrListSearchStudents.size() > 0) {
                    mArrListSearchStudents.clear();
                    mArrListSearchStudents = new ArrayList<>();
                }
                if (mArrListRunningStudents != null && mArrListRunningStudents.size() > 0) {
                    mArrListRunningStudents.clear();
                    mArrListRunningStudents = new ArrayList<>();
                }
                if (mArrListCompletedStudents != null && mArrListCompletedStudents.size() > 0) {
                    mArrListCompletedStudents.clear();
                    mArrListCompletedStudents = new ArrayList<>();
                }
                if (mAdapterStudentList != null) {
                    mAdapterStudentList.notifyDataSetChanged();
                }
                final Gson studentListFilterGson = new Gson();
                try {
                    ModelStudent modelStudent = studentListFilterGson
                            .fromJson(response, ModelStudent.class);

                    if (modelStudent.getStatus().equals(Constants.SUCCESS_CODE)) {
                        mArrListSearchStudents = modelStudent.getStudents();
                        if (mArrListSearchStudents != null && mArrListSearchStudents.size() > 0) {
                            for (int i = 0; i < mArrListSearchStudents.size(); i++) {
                                //display only unpaid amounts records
                                /*if (mArrListSearchStudents.get(i).getCourse_status().equalsIgnoreCase("running")) {
                                    mArrListRunningStudents.add(mArrListSearchStudents.get(i));
                                } else {
                                    mArrListCompletedStudents.add(mArrListSearchStudents.get(i));
                                }*/
                                mArrListRunningStudents.add(mArrListSearchStudents.get(i));
                            }
                            if (mSwitch_running_completed.isChecked()) {
                                setStudentListAdapter(mArrListRunningStudents);
                            } else {
                                setStudentListAdapter(mArrListCompletedStudents);
                            }
                        }
                    } else {
                        Toast.makeText(this, "" + modelStudent.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if (mSwipeToRefresh.isRefreshing()) {
                        mSwipeToRefresh.setRefreshing(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CODE_COURSE_LIST:
                Log.e("COURSE_LIST >> ", "" + response);
                final Gson courseListGson = new Gson();
                try {
                    FilterModelCourseList modelCourseList = courseListGson
                            .fromJson(response, FilterModelCourseList.class);

                    if (modelCourseList.getStatus().equals(Constants.SUCCESS_CODE)) {
                        mArrListCourses = modelCourseList.getCourses();

                        if (mArrListCourses != null && mArrListCourses.size() > 0) {
                            //nothing to do here.. setting adapter in dialog.
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CODE_BATCH_LIST:
                Log.e("BATCH LIST >> ", "" + response);
                final Gson batchGson = new Gson();
                try {
                    modelBatchList = batchGson
                            .fromJson(response, BatchListModel.class);

                    if (modelBatchList.getStatus().equals(Constants.SUCCESS_CODE)) {
                        mArrListBatches = new ArrayList<>();
                        mArrListBatches = modelBatchList.getBatches();

                        if (mArrListBatches != null && mArrListBatches.size() > 0) {
                            //nothing to do here.. setting adapter in dialog.
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case CODE_STANDARD_LIST:
                Log.e("STANDARD LIST >> ", "" + response);
                final Gson standardListgson = new Gson();
                try {
                    StandardListModel standardListModel = standardListgson.fromJson(response, StandardListModel.class);
                    if (standardListModel.getStatus().equals(Constants.SUCCESS_CODE)) {
                        mArrListStandards = standardListModel.getStandards();
                        if (mArrListStandards != null && mArrListStandards.size() > 0) {
                            //nothing to do here.. setting adapter in dialog.
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            /*case CODE_DELETE_STUDENT:
                try {
                    JSONObject jsonDeleteInquiry = new JSONObject(response);
                    if (jsonDeleteInquiry.has("status")) {
                        if (jsonDeleteInquiry.getString("status").equalsIgnoreCase("success")) {
                            Toast.makeText(this, "" + jsonDeleteInquiry.getString("message"), Toast.LENGTH_SHORT).show();
                            playSoundForAttendance("" + jsonDeleteInquiry.getString("message"), StudentListActivity.this);
                            mArrListStudents.remove(deletePosition);
                            mAdapterStudentList.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;*/
        }

    }

    private void setFilterCourseListAdapter(ListView mLVCourses) {
        mAdapterCourseList = new FilterCourseListAdapter(StudentListActivity.this, mArrListCourses, this);
        mLVCourses.setAdapter(mAdapterCourseList);
        if (mLVCourses.getFooterViewsCount() == 0) {
            mLVCourses.addFooterView(mListFooter);
        }
        mListFooter.setVisibility(View.GONE);
    }

    private void setFilterMonthListAdapter(ListView mLVMonths) {
        mAdapterMonthList = new FilterMonthListAdapter(StudentListActivity.this, mArrListMonths, this);
        mLVMonths.setAdapter(mAdapterMonthList);
        if (mLVMonths.getFooterViewsCount() == 0) {
            mLVMonths.addFooterView(mListFooter);
        }
        mListFooter.setVisibility(View.GONE);
    }

    private void setFilterStudentTypesAdapter(ListView mLVStudentTypes) {
        mAdapterStudentTypeList = new FilterStudentTypeListAdapter(StudentListActivity.this, mArrListStudentTypes, this);
        mLVStudentTypes.setAdapter(mAdapterStudentTypeList);
        if (mLVStudentTypes.getFooterViewsCount() == 0) {
            mLVStudentTypes.addFooterView(mListFooter);
        }
        mListFooter.setVisibility(View.GONE);
    }

    private void setStudentListAdapter(ArrayList<ModelStudent.Student> mArrListStudents) {
        if (mAdapterStudentList != null) {
            mAdapterStudentList = null;
        }
        mAdapterStudentList = new StudentListAdapter(StudentListActivity.this, mArrListStudents, this, isDisplayMoreOptions);
        mLvStudents.setAdapter(mAdapterStudentList);
        if (mLvStudents.getFooterViewsCount() == 0) {
            mLvStudents.addFooterView(mListFooter);
        }
        mListFooter.setVisibility(View.GONE);
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
                openMonthFilterDialog();
                break;
            case R.id.ll_filter_courses:
                openCourseFilterDialog();
                break;
            case R.id.ll_filter_student_types:
                openStudentTypeFilterDialog();
                break;
            case R.id.ll_filter_batch:
                openBatchFilterDialog();
                break;
            case R.id.ll_filter_standard:
                openStandardFilterDialog();
                break;
        }
    }

    private void openMonthFilterDialog() {

        if (mArrListSelectedMonths != null && mArrListSelectedMonths.size() > 0) {

            for (int i = 0; i < mArrListMonths.size(); i++) {
                mArrListMonths.get(i).setSelected(false);
            }
            for (int i = 0; i < mArrListMonths.size(); i++) {
                for (int j = 0; j < mArrListSelectedMonths.size(); j++) {
                    if (mArrListMonths.get(i).getStrMonth().equalsIgnoreCase(mArrListSelectedMonths.get(j).getStrMonth())) {
                        mArrListMonths.get(i).setSelected(true);
                    } else {
                        //mArrListMonths.get(i).setSelected(false);
                    }
                }
            }
        }

        final Dialog dialogMonthFilter = new Dialog(StudentListActivity.this);
        dialogMonthFilter.setContentView(R.layout.dialog_filter_courses_months);
        dialogMonthFilter.setCancelable(false);

        mTvTotalSelectedmonths = dialogMonthFilter.findViewById(R.id.tv_total_selected_months);
        mTvClearMonthFilter = dialogMonthFilter.findViewById(R.id.tv_clear_course_student_type_filter);
        mBtnApplyMonthFilter = dialogMonthFilter.findViewById(R.id.btn_apply_course_month_filter);
        mLVMonths = (ListView) dialogMonthFilter.findViewById(R.id.lv_filter_course_months);
        ImageView iv_close = dialogMonthFilter.findViewById(R.id.iv_close_student_type_filter);

        setFilterMonthListAdapter(mLVMonths);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mArrListSelectedMonths != null && mArrListSelectedMonths.size() < 1) {
                    for (int i = 0; i < mArrListMonths.size(); i++) {
                        mArrListMonths.get(i).setSelected(false);
                    }
                }
                mAdapterMonthList.notifyDataSetChanged();

                if (dialogMonthFilter != null && dialogMonthFilter.isShowing()) {
                    dialogMonthFilter.dismiss();
                }
            }
        });

        manageFiltersForMonths();
        mTvClearMonthFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMonthListFilter();
                if (dialogMonthFilter != null && dialogMonthFilter.isShowing()) {
                    dialogMonthFilter.dismiss();
                }
            }
        });

        mBtnApplyMonthFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mArrListSelectedMonths != null && mArrListSelectedMonths.size() > 0) {
                    mArrListSelectedMonths.clear();
                    mArrListSelectedMonths = new ArrayList<>();
                }
                for (int i = 0; i < mArrListMonths.size(); i++) {
                    if (mArrListMonths.get(i).isSelected()) {
                        mArrListSelectedMonths.add(mArrListMonths.get(i));
                    }
                }

                if (mArrListSelectedMonths.size() > 0) {
                    dialogMonthFilter.dismiss();
                    mLinMonths.setBackgroundResource(R.drawable.rounded_filter_selected);
                    mTvFilterMonths.setTextColor(getResources().getColor(R.color.colorWhite));

                    callApiToFilterStudentList("" + mStrSearchValue);
                }

            }
        });
        if (dialogMonthFilter != null && !dialogMonthFilter.isShowing()) {
            dialogMonthFilter.show();
            //manageFiltersForMonths();
        }
    }

    private void openBatchFilterDialog() {
        if (mArrListSelectedBatch != null && mArrListSelectedBatch.size() > 0) {
            for (int i = 0; i < mArrListBatches.size(); i++) {
                mArrListBatches.get(i).setSelected(false);
            }
            for (int i = 0; i < mArrListBatches.size(); i++) {
                for (int j = 0; j < mArrListSelectedBatch.size(); j++) {
                    if (mArrListBatches.get(i).getName().equalsIgnoreCase(mArrListSelectedBatch.get(j).getName())) {
                        mArrListBatches.get(i).setSelected(true);
                    } else {
                        //mArrListMonths.get(i).setSelected(false);
                    }
                }
            }
        }

        final Dialog dialogBatchFilter = new Dialog(StudentListActivity.this);
        dialogBatchFilter.setContentView(R.layout.dialog_filter_batches);
        dialogBatchFilter.setCancelable(false);

        mTvTotalSelectedBatches = dialogBatchFilter.findViewById(R.id.tv_total_selected_batches);
        mTvClearBatchFilter = dialogBatchFilter.findViewById(R.id.tv_clear_batch_filter);
        mBtnApplyBatchFilter = dialogBatchFilter.findViewById(R.id.btn_apply_batch_filter);
        mRvBatches = (RecyclerView) dialogBatchFilter.findViewById(R.id.rv_filter_batches);
        ImageView iv_close = dialogBatchFilter.findViewById(R.id.iv_close_batch_filter);

        setadapterForBatchList(mRvBatches);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mArrListSelectedBatch != null && mArrListSelectedBatch.size() < 1) {
                    for (int i = 0; i < mArrListBatches.size(); i++) {
                        mArrListBatches.get(i).setSelected(false);
                    }
                }
                mAdapterBatchList.notifyDataSetChanged();

                if (dialogBatchFilter != null && dialogBatchFilter.isShowing()) {
                    dialogBatchFilter.dismiss();
                }
            }
        });

        manageFiltersForBatches();

        mTvClearBatchFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearBatchListFilter();
                if (dialogBatchFilter != null && dialogBatchFilter.isShowing()) {
                    dialogBatchFilter.dismiss();
                }
            }
        });

        mBtnApplyBatchFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mArrListSelectedBatch != null && mArrListSelectedBatch.size() > 0) {
                    mArrListSelectedBatch.clear();
                    mArrListSelectedBatch = new ArrayList<>();
                }
                for (int i = 0; i < mArrListBatches.size(); i++) {
                    if (mArrListBatches.get(i).isSelected()) {
                        mArrListSelectedBatch.add(mArrListBatches.get(i));
                    }
                }

                if (mArrListSelectedBatch.size() > 0) {
                    dialogBatchFilter.dismiss();
                    mLinBatches.setBackgroundResource(R.drawable.rounded_filter_selected);
                    mTvFilterBatches.setTextColor(getResources().getColor(R.color.colorWhite));

                    callApiToFilterStudentList("" + mStrSearchValue);
                }
            }
        });
        if (dialogBatchFilter != null && !dialogBatchFilter.isShowing()) {
            dialogBatchFilter.show();
        }
    }

    private void openStandardFilterDialog() {
        if (mArrListSelectedStandard != null && mArrListSelectedStandard.size() > 0) {
            for (int i = 0; i < mArrListStandards.size(); i++) {
                mArrListStandards.get(i).setSelected(false);
            }
            for (int i = 0; i < mArrListStandards.size(); i++) {
                for (int j = 0; j < mArrListSelectedStandard.size(); j++) {
                    if (mArrListStandards.get(i).getName().equalsIgnoreCase(mArrListSelectedStandard.get(j).getName())) {
                        mArrListStandards.get(i).setSelected(true);
                    } else {
                        //mArrListMonths.get(i).setSelected(false);
                    }
                }
            }
        }

        final Dialog dialogStandardFilter = new Dialog(StudentListActivity.this);
        dialogStandardFilter.setContentView(R.layout.dialog_filter_standards);
        dialogStandardFilter.setCancelable(false);

        mTvTotalSelectedStandard = dialogStandardFilter.findViewById(R.id.tv_total_selected_standards);
        mTvClearStandardFilter = dialogStandardFilter.findViewById(R.id.tv_clear_standard_filter);
        mBtnApplyStandardFilter = dialogStandardFilter.findViewById(R.id.btn_apply_standard_filter);
        mRvStandards = (RecyclerView) dialogStandardFilter.findViewById(R.id.rv_filter_standards);
        ImageView iv_close = dialogStandardFilter.findViewById(R.id.iv_close_standard_filter);

        setadapterForStandardList(mRvStandards);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mArrListSelectedStandard != null && mArrListSelectedStandard.size() < 1) {
                    for (int i = 0; i < mArrListStandards.size(); i++) {
                        mArrListStandards.get(i).setSelected(false);
                    }
                }
                mAdapterStandardList.notifyDataSetChanged();

                if (dialogStandardFilter != null && dialogStandardFilter.isShowing()) {
                    dialogStandardFilter.dismiss();
                }
            }
        });

        manageFiltersForStandards();

        mTvClearStandardFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearStandardListFilter();
                if (dialogStandardFilter != null && dialogStandardFilter.isShowing()) {
                    dialogStandardFilter.dismiss();
                }
            }
        });

        mBtnApplyStandardFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mArrListSelectedStandard != null && mArrListSelectedStandard.size() > 0) {
                    mArrListSelectedStandard.clear();
                    mArrListSelectedStandard = new ArrayList<>();
                }
                mArrListSelectedStandard=new ArrayList<>();
                for (int i = 0; i < mArrListStandards.size(); i++) {
                    if (mArrListStandards.get(i).isSelected()) {
                        mArrListSelectedStandard.add(mArrListStandards.get(i));
                    }
                }

                if (mArrListSelectedStandard.size() > 0) {
                    dialogStandardFilter.dismiss();
                    mLinStandards.setBackgroundResource(R.drawable.rounded_filter_selected);
                    mTvFilterStandards.setTextColor(getResources().getColor(R.color.colorWhite));

                    callApiToFilterStudentList("" + mStrSearchValue);
                }
            }
        });
        if (dialogStandardFilter != null && !dialogStandardFilter.isShowing()) {
            dialogStandardFilter.show();
        }
    }

    private void openCourseFilterDialog() {
        if (mArrListSelectedCourseIds != null && mArrListSelectedCourseIds.size() > 0) {

            for (int i = 0; i < mArrListCourses.size(); i++) {
                mArrListCourses.get(i).setSelected(false);
            }
            for (int i = 0; i < mArrListCourses.size(); i++) {
                for (int j = 0; j < mArrListSelectedCourseIds.size(); j++) {
                    if (mArrListCourses.get(i).getId() == mArrListSelectedCourseIds.get(j)) {
                        mArrListCourses.get(i).setSelected(true);
                    }
                }
            }
        }

        final Dialog dialogCourseFilter = new Dialog(StudentListActivity.this);
        dialogCourseFilter.setContentView(R.layout.dialog_filter_courses);
        dialogCourseFilter.setCancelable(false);

        mTvClearCourseFilter = dialogCourseFilter.findViewById(R.id.tv_clear_course_filter);
        mBtnApplyCourseFilter = dialogCourseFilter.findViewById(R.id.btn_apply_course_filter);
        mTvTotalSelectedCourses = dialogCourseFilter.findViewById(R.id.tv_total_selected_courses);
        mLVCourses = (ListView) dialogCourseFilter.findViewById(R.id.lv_filter_course_list);
        ImageView iv_close = dialogCourseFilter.findViewById(R.id.iv_close);

        setFilterCourseListAdapter(mLVCourses);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mArrListSelectedCourseIds != null && mArrListSelectedCourseIds.size() < 1) {
                    for (int i = 0; i < mArrListCourses.size(); i++) {
                        mArrListCourses.get(i).setSelected(false);
                    }
                }
                mAdapterCourseList.notifyDataSetChanged();

                if (dialogCourseFilter != null && dialogCourseFilter.isShowing()) {
                    dialogCourseFilter.dismiss();
                }
            }
        });
        manageFilterForCourses();
        mTvClearCourseFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCourseListFilter();

                if (dialogCourseFilter != null && dialogCourseFilter.isShowing()) {
                    dialogCourseFilter.dismiss();
                }
            }
        });

        mBtnApplyCourseFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mArrListSelectedCourseIds != null && mArrListSelectedCourseIds.size() > 0) {
                    mArrListSelectedCourseIds.clear();
                    mArrListSelectedCourseIds = new ArrayList<>();
                }

                for (int i = 0; i < mArrListCourses.size(); i++) {
                    if (mArrListCourses.get(i).isSelected()) {
                        mArrListSelectedCourseIds.add(mArrListCourses.get(i).getId());
                    }
                }

                if (mArrListSelectedCourseIds.size() > 0) {
                    dialogCourseFilter.dismiss();
                    mLinCourses.setBackgroundResource(R.drawable.rounded_filter_selected);
                    mTvFilterCourses.setTextColor(getResources().getColor(R.color.colorWhite));

                    callApiToFilterStudentList("" + mStrSearchValue);

                }
            }
        });
        if (dialogCourseFilter != null && !dialogCourseFilter.isShowing()) {
            dialogCourseFilter.show();
        }
    }

    private void clearMonthListFilter() {
        if (mArrListMonths != null && mArrListMonths.size() > 0) {
            for (int i = 0; i < mArrListMonths.size(); i++) {
                if (mArrListMonths.get(i).isSelected() == true) {
                    mArrListMonths.get(i).setSelected(false);
                }
            }
        }
        mAdapterMonthList.notifyDataSetChanged();

        mTvClearMonthFilter.setTextColor(getResources().getColor(R.color.colorGrey));
        mTvClearMonthFilter.setEnabled(false);

        mTvTotalSelectedmonths.setVisibility(View.GONE);
        mTvTotalSelectedmonths.setText("");
        mBtnApplyMonthFilter.setBackgroundColor(getResources().getColor(R.color.colorGrey));

        mLinMonths.setBackgroundResource(R.drawable.rounded_filter_white);
        mTvFilterMonths.setTextColor(getResources().getColor(R.color.colorBlack));

        if (mArrListSelectedMonths != null && mArrListSelectedMonths.size() > 0) {
            mArrListSelectedMonths.clear();
        }

        //To reflect changes...
        if ((mArrListSelectedBatch != null && mArrListSelectedBatch.size() > 0) ||
                (mArrListAppliedStudentTypes != null && mArrListAppliedStudentTypes.size() > 0) ||
                (mArrListSelectedStandard != null && mArrListSelectedStandard.size() > 0) ||
                (mArrListAppliedCourseIds != null && mArrListAppliedCourseIds.size() > 0)) {
            callApiToFilterStudentList("" + mStrSearchValue);
        } else {
            callApiToLoadStudentList(false);
        }
    }

    private void clearStandardListFilter() {
        if (mArrListStandards != null && mArrListStandards.size() > 0) {
            for (int i = 0; i < mArrListStandards.size(); i++) {
                if (mArrListStandards.get(i).isSelected() == true) {
                    mArrListStandards.get(i).setSelected(false);
                }
            }
        }
        mAdapterStandardList.notifyDataSetChanged();

        mTvClearStandardFilter.setTextColor(getResources().getColor(R.color.colorGrey));
        mTvClearStandardFilter.setEnabled(false);

        mTvTotalSelectedStandard.setVisibility(View.GONE);
        mTvTotalSelectedStandard.setText("");
        mBtnApplyStandardFilter.setBackgroundColor(getResources().getColor(R.color.colorGrey));

        mLinStandards.setBackgroundResource(R.drawable.rounded_filter_white);
        mTvFilterStandards.setTextColor(getResources().getColor(R.color.colorBlack));

        if (mArrListSelectedStandard != null && mArrListSelectedStandard.size() > 0) {
            mArrListSelectedStandard.clear();
        }

        //To reflect changes...
        if ((mArrListSelectedBatch != null && mArrListSelectedBatch.size() > 0) ||
                (mArrListAppliedStudentTypes != null && mArrListAppliedStudentTypes.size() > 0) ||
                (mArrListSelectedMonths != null && mArrListSelectedMonths.size() > 0) ||
                (mArrListAppliedCourseIds != null && mArrListAppliedCourseIds.size() > 0)) {
            callApiToFilterStudentList("" + mStrSearchValue);
        } else {
            callApiToLoadStudentList(false);
        }
    }

    private void clearCourseListFilter() {
        if (mArrListCourses != null && mArrListCourses.size() > 0) {
            for (int i = 0; i < mArrListCourses.size(); i++) {
                if (mArrListCourses.get(i).isSelected() == true) {
                    mArrListCourses.get(i).setSelected(false);
                }
            }
        }
        mAdapterCourseList.notifyDataSetChanged();

        mTvClearCourseFilter.setTextColor(getResources().getColor(R.color.colorGrey));
        mTvClearCourseFilter.setEnabled(false);

        mTvTotalSelectedCourses.setVisibility(View.GONE);
        mTvTotalSelectedCourses.setText("");
        mBtnApplyCourseFilter.setBackgroundColor(getResources().getColor(R.color.colorGrey));

        mLinCourses.setBackgroundResource(R.drawable.rounded_filter_white);
        mTvFilterCourses.setTextColor(getResources().getColor(R.color.colorBlack));

        if (mArrListSelectedCourseIds != null && mArrListSelectedCourseIds.size() > 0) {
            mArrListSelectedCourseIds.clear();
        }

        //To reflect changes...
        if ((mArrListSelectedBatch != null && mArrListSelectedBatch.size() > 0) ||
                (mArrListAppliedStudentTypes != null && mArrListAppliedStudentTypes.size() > 0) ||
                (mArrListSelectedStandard != null && mArrListSelectedStandard.size() > 0) ||
                (mArrListSelectedMonths != null && mArrListSelectedMonths.size() > 0)) {
            callApiToFilterStudentList("" + mStrSearchValue);
        } else {
            callApiToLoadStudentList(false);
        }
    }

    private void clearBatchListFilter() {
        if (mArrListBatches != null && mArrListBatches.size() > 0) {
            for (int i = 0; i < mArrListBatches.size(); i++) {
                if (mArrListBatches.get(i).isSelected() == true) {
                    mArrListBatches.get(i).setSelected(false);
                }
            }
        }

        mAdapterBatchList.notifyDataSetChanged();

        mTvClearBatchFilter.setTextColor(getResources().getColor(R.color.colorGrey));
        mTvClearBatchFilter.setEnabled(false);

        mTvTotalSelectedBatches.setVisibility(View.GONE);
        mTvTotalSelectedBatches.setText("");
        mBtnApplyBatchFilter.setBackgroundColor(getResources().getColor(R.color.colorGrey));

        mLinBatches.setBackgroundResource(R.drawable.rounded_filter_white);
        mTvFilterBatches.setTextColor(getResources().getColor(R.color.colorBlack));

        if (mArrListSelectedBatch != null && mArrListSelectedBatch.size() > 0) {
            mArrListSelectedBatch.clear();
        }

        //To reflect changes...
        if ((mArrListSelectedStudentTypes != null && mArrListSelectedStudentTypes.size() > 0) ||
                (mArrListSelectedCourseIds != null && mArrListSelectedCourseIds.size() > 0) ||
                (mArrListSelectedStandard != null && mArrListSelectedStandard.size() > 0) ||
                (mArrListSelectedMonths != null && mArrListSelectedMonths.size() > 0)) {

            LogUtil.e("studenrt types",""+mArrListSelectedStudentTypes.size());
            LogUtil.e("mArrListSelectedCourseIds ",""+mArrListSelectedCourseIds.size());
            LogUtil.e("mArrListSelectedStandard ",""+mArrListSelectedStandard.size());
            LogUtil.e("mArrListSelectedMonths",""+mArrListSelectedMonths.size());
            LogUtil.e(">>> CALLLED"," FILTER");

            callApiToFilterStudentList("" + mStrSearchValue);
        } else {
            LogUtil.e(">>> CALLLED"," STUDENT");
            callApiToLoadStudentList(false);
        }
    }


    private void clearStudentTypeFilter() {
        if (mArrListStudentTypes != null && mArrListStudentTypes.size() > 0) {
            for (int i = 0; i < mArrListStudentTypes.size(); i++) {
                if (mArrListStudentTypes.get(i).isSelected() == true) {
                    mArrListStudentTypes.get(i).setSelected(false);
                }
            }
        }

        mAdapterStudentTypeList.notifyDataSetChanged();

        mTvClearStudentTypesFilter.setTextColor(getResources().getColor(R.color.colorGrey));
        mTvClearStudentTypesFilter.setEnabled(false);

        mTvTotalSelectedStudentTypes.setVisibility(View.GONE);
        mTvTotalSelectedStudentTypes.setText("");
        mBtnApplyStudentTypeFilter.setBackgroundColor(getResources().getColor(R.color.colorGrey));

        mLinStudentTypes.setBackgroundResource(R.drawable.rounded_filter_white);
        mTvFilterStudentTypes.setTextColor(getResources().getColor(R.color.colorBlack));

        if (mArrListSelectedStudentTypes != null && mArrListSelectedStudentTypes.size() > 0) {
            mArrListSelectedStudentTypes.clear();
        }

        //To reflect changes...
        if ((mArrListSelectedBatch != null && mArrListSelectedBatch.size() > 0) ||
                (mArrListSelectedCourseIds != null && mArrListSelectedCourseIds.size() > 0) ||
                (mArrListSelectedStandard != null && mArrListSelectedStandard.size() > 0) ||
                (mArrListSelectedMonths != null && mArrListSelectedMonths.size() > 0)) {
            callApiToFilterStudentList("" + mStrSearchValue);
        } else {
            callApiToLoadStudentList(false);
        }
    }

    private void manageFilterForCourses() {
        int temp = 0;
        for (int i = 0; i < mArrListCourses.size(); i++) {
            if (mArrListCourses.get(i).isSelected()) {
                temp = 1;
                mTvClearCourseFilter.setEnabled(true);
                mBtnApplyCourseFilter.setEnabled(true);
                mBtnApplyCourseFilter.setBackgroundColor(getResources().getColor(R.color.colorThemeBlue));
                mTvClearCourseFilter.setTextColor(getResources().getColor(R.color.colorThemeBlue));
                return;
            }
        }
        if (temp == 0) {
            mTvTotalSelectedCourses.setVisibility(View.GONE);
            mTvTotalSelectedCourses.setText("");
            mTvClearCourseFilter.setEnabled(false);
            mBtnApplyCourseFilter.setBackgroundColor(getResources().getColor(R.color.colorGrey));
            mTvClearCourseFilter.setTextColor(getResources().getColor(R.color.colorGrey));
            mBtnApplyCourseFilter.setEnabled(false);

        }
    }

    private void openStudentTypeFilterDialog() {
        if (mArrListSelectedStudentTypes != null && mArrListSelectedStudentTypes.size() > 0) {
            for (int i = 0; i < mArrListStudentTypes.size(); i++) {
                mArrListStudentTypes.get(i).setSelected(false);
            }
            for (int i = 0; i < mArrListStudentTypes.size(); i++) {
                for (int j = 0; j < mArrListSelectedStudentTypes.size(); j++) {
                    if (mArrListStudentTypes.get(i).getStrStudentType().equalsIgnoreCase(mArrListSelectedStudentTypes.get(j).getStrStudentType())) {
                        mArrListStudentTypes.get(i).setSelected(true);
                    }
                }
            }
        }

        final Dialog dialogStudentTypesFilter = new Dialog(StudentListActivity.this);
        dialogStudentTypesFilter.setContentView(R.layout.dialog_filter_student_type);
        dialogStudentTypesFilter.setCancelable(false);

        mTvClearStudentTypesFilter = dialogStudentTypesFilter.findViewById(R.id.tv_clear_course_student_type_filter);
        mBtnApplyStudentTypeFilter = dialogStudentTypesFilter.findViewById(R.id.btn_apply_student_type_filter);
        mTvTotalSelectedStudentTypes = dialogStudentTypesFilter.findViewById(R.id.tv_total_selected_student_types);
        mLVStudentTypes = (ListView) dialogStudentTypesFilter.findViewById(R.id.lv_filter_student_types);
        ImageView iv_close = dialogStudentTypesFilter.findViewById(R.id.iv_close_student_type_filter);

        setFilterStudentTypesAdapter(mLVStudentTypes);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mArrListSelectedStudentTypes != null && mArrListSelectedStudentTypes.size() < 1) {
                    for (int i = 0; i < mArrListStudentTypes.size(); i++) {
                        mArrListStudentTypes.get(i).setSelected(false);
                    }
                }
                mAdapterStudentTypeList.notifyDataSetChanged();

                if (dialogStudentTypesFilter != null && dialogStudentTypesFilter.isShowing()) {
                    dialogStudentTypesFilter.dismiss();
                }
            }
        });
        manageFiltersForStudentTypes();
        mTvClearStudentTypesFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearStudentTypeFilter();
                mSwitch_running_completed.setVisibility(View.VISIBLE);
                if (dialogStudentTypesFilter != null && dialogStudentTypesFilter.isShowing()) {
                    dialogStudentTypesFilter.dismiss();
                }
            }
        });

        mBtnApplyStudentTypeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwitch_running_completed.setVisibility(View.GONE);
                if (mArrListSelectedStudentTypes != null && mArrListSelectedStudentTypes.size() > 0) {
                    mArrListSelectedStudentTypes.clear();
                    mArrListSelectedStudentTypes = new ArrayList<>();
                }

                for (int i = 0; i < mArrListStudentTypes.size(); i++) {
                    if (mArrListStudentTypes.get(i).isSelected()) {
                        mArrListSelectedStudentTypes.add(mArrListStudentTypes.get(i));
                    }
                }

                if (mArrListSelectedStudentTypes.size() > 0) {
                    dialogStudentTypesFilter.dismiss();
                    mLinStudentTypes.setBackgroundResource(R.drawable.rounded_filter_selected);
                    mTvFilterStudentTypes.setTextColor(getResources().getColor(R.color.colorWhite));
                    callApiToFilterStudentList("" + mStrSearchValue);
                }
            }
        });
        if (dialogStudentTypesFilter != null && !dialogStudentTypesFilter.isShowing()) {
            dialogStudentTypesFilter.show();
        }
    }

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
        if (id == R.id.action_add) {
            startActivity(new Intent(StudentListActivity.this, AddStudentActivity.class));
            return true;
        }
        if (id == R.id.action_filter) {
            if (mRelFilterLayout.getVisibility() == View.GONE) {
                mRelFilterLayout.setVisibility(View.VISIBLE);
            } else {
                mRelFilterLayout.setVisibility(View.GONE);
            }
            //openFilterStudentBottomSheetDialog();
        }
        if (id == R.id.action_pdf) {
            createPdfForStudents("In Progress");
            Toast.makeText(this, "In Progress", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_excel) {
            if (PermissionManager.hasPermissions(StudentListActivity.this, Permissions.STORAGE_PERMISSIONS)) {
                exportStudentDataToExcel();
            } else {
                PermissionManager.requestPermissions(StudentListActivity.this, Constants.CODE_RUNTIME_STORAGE_PERMISSION,
                        permissionListener, "", Permissions.STORAGE_PERMISSIONS);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void exportStudentDataToExcel() {
        File sd = Environment.getExternalStorageDirectory();
        String csvFile = "GlobalIT_Student_Report.xls";

        File directory = new File(sd.getAbsolutePath());
        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {
            //file path
            File file = new File(directory, csvFile);
            Log.e("Path >>", "" + file.getAbsolutePath());
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("userList", 0);
            sheet.setName("Student Report");
            // column and row
            sheet.addCell(new Label(0, 0, "ID"));
            sheet.addCell(new Label(1, 0, "Branch"));
            sheet.addCell(new Label(2, 0, "FirstName"));
            sheet.addCell(new Label(3, 0, "LastName"));
            sheet.addCell(new Label(4, 0, "Contact"));
            sheet.addCell(new Label(5, 0, "Email"));
            sheet.addCell(new Label(6, 0, "Address"));
            sheet.addCell(new Label(7, 0, "DateOfBirth"));
            sheet.addCell(new Label(8, 0, "In/Out"));
            sheet.addCell(new Label(9, 0, "UNPAID Amount"));
            sheet.addCell(new Label(10, 0, "Total Amount"));
            sheet.addCell(new Label(11, 0, "Status"));
            sheet.addCell(new Label(12, 0, "Courses"));
            sheet.addCell(new Label(13, 0, "JoinedOn"));
            sheet.addCell(new Label(14, 0, "ParentName"));
            sheet.addCell(new Label(15, 0, "ParentMobile"));
            sheet.addCell(new Label(16, 0, "ReferenceBy"));
            sheet.addCell(new Label(17, 0, "ImageUrl"));
            if (mSwitch_running_completed.isChecked()) {
                if (mArrListRunningStudents != null && mArrListRunningStudents.size() > 0) {
                    for (int i = 0; i < mArrListRunningStudents.size(); i++) {
                        sheet.addCell(new Label(0, i + 1, mArrListRunningStudents.get(i).getId().toString()));
                        sheet.addCell(new Label(1, i + 1, mArrListRunningStudents.get(i).getBranchName().toString()));
                        sheet.addCell(new Label(2, i + 1, mArrListRunningStudents.get(i).getFname().toString()));
                        sheet.addCell(new Label(3, i + 1, mArrListRunningStudents.get(i).getLname().toString()));
                        sheet.addCell(new Label(4, i + 1, mArrListRunningStudents.get(i).getContact().toString()));
                        sheet.addCell(new Label(5, i + 1, mArrListRunningStudents.get(i).getEmail().toString()));
                        sheet.addCell(new Label(6, i + 1, mArrListRunningStudents.get(i).getAddress().toString()));
                        sheet.addCell(new Label(7, i + 1, mArrListRunningStudents.get(i).getDob().toString()));
                        sheet.addCell(new Label(8, i + 1, mArrListRunningStudents.get(i).getIn_out_status().toString()));
                        sheet.addCell(new Label(9, i + 1, mArrListRunningStudents.get(i).getUnpaid_amount().toString()));
                        sheet.addCell(new Label(10, i + 1, mArrListRunningStudents.get(i).getAmount().toString()));
                        sheet.addCell(new Label(11, i + 1, mArrListRunningStudents.get(i).getCourse_status().toString()));
                        sheet.addCell(new Label(12, i + 1, mArrListRunningStudents.get(i).getCourses().get(0).getName().toString()));
                        sheet.addCell(new Label(13, i + 1, mArrListRunningStudents.get(i).getJoiningDate().toString()));
                        sheet.addCell(new Label(14, i + 1, mArrListRunningStudents.get(i).getParentname().toString()));
                        sheet.addCell(new Label(15, i + 1, mArrListRunningStudents.get(i).getParentmobileno().toString()));
                        sheet.addCell(new Label(16, i + 1, mArrListRunningStudents.get(i).getReference().toString()));
                        sheet.addCell(new Label(17, i + 1, mArrListRunningStudents.get(i).getImage().toString()));
                    }
                }
                workbook.write();
                workbook.close();
                openExcelFile(file);
            } else {
                if (mArrListCompletedStudents != null && mArrListCompletedStudents.size() > 0) {
                    for (int i = 0; i < mArrListCompletedStudents.size(); i++) {
                        sheet.addCell(new Label(0, i + 1, mArrListCompletedStudents.get(i).getId().toString()));
                        sheet.addCell(new Label(1, i + 1, mArrListCompletedStudents.get(i).getBranchName().toString()));
                        sheet.addCell(new Label(2, i + 1, mArrListCompletedStudents.get(i).getFname().toString()));
                        sheet.addCell(new Label(3, i + 1, mArrListCompletedStudents.get(i).getLname().toString()));
                        sheet.addCell(new Label(4, i + 1, mArrListCompletedStudents.get(i).getContact().toString()));
                        sheet.addCell(new Label(5, i + 1, mArrListCompletedStudents.get(i).getEmail().toString()));
                        sheet.addCell(new Label(6, i + 1, mArrListCompletedStudents.get(i).getAddress().toString()));
                        sheet.addCell(new Label(7, i + 1, mArrListCompletedStudents.get(i).getDob().toString()));
                        sheet.addCell(new Label(8, i + 1, mArrListCompletedStudents.get(i).getIn_out_status().toString()));
                        sheet.addCell(new Label(9, i + 1, mArrListCompletedStudents.get(i).getUnpaid_amount().toString()));
                        sheet.addCell(new Label(10, i + 1, mArrListCompletedStudents.get(i).getAmount().toString()));
                        sheet.addCell(new Label(11, i + 1, mArrListCompletedStudents.get(i).getCourse_status().toString()));
                        sheet.addCell(new Label(12, i + 1, mArrListCompletedStudents.get(i).getCourses().get(0).getName().toString()));
                        sheet.addCell(new Label(13, i + 1, mArrListCompletedStudents.get(i).getJoiningDate().toString()));
                        sheet.addCell(new Label(14, i + 1, mArrListCompletedStudents.get(i).getParentname().toString()));
                        sheet.addCell(new Label(15, i + 1, mArrListCompletedStudents.get(i).getParentmobileno().toString()));
                        sheet.addCell(new Label(16, i + 1, mArrListCompletedStudents.get(i).getReference().toString()));
                        sheet.addCell(new Label(17, i + 1, mArrListCompletedStudents.get(i).getImage().toString()));
                    }
                }
                workbook.write();
                workbook.close();
                openExcelFile(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openExcelFile(File path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File("" + path.toString())), "application/vnd.ms-excel");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(StudentListActivity.this, "No Application Available to View Excel", Toast.LENGTH_SHORT).show();
        }
    }

    private void createPdfForStudents(String sometext) {
        PDDocument doc = null;
        PDPage page = null;
        try {
            doc = new PDDocument();
            page = new PDPage();

            doc.addPage(page);
            PDFont font = PDType1Font.HELVETICA;
            PDPageContentStream content = new PDPageContentStream(doc, page);

            content.beginText();
            content.setFont(font, 12);
            content.moveTextPositionByAmount(100, 700);
            String text = "";
            for (int i = 0; i < mArrListStudents.size(); i++) {
                content.drawString("" + mArrListStudents.get(i).getFname() + " " + mArrListStudents.get(i).getLname());
                content.moveTextPositionByAmount(0, 20);
            }

            content.endText();
            content.close();
            doc.save("nameoffile.pdf");
            doc.close();
            System.out.print("Pages" + doc.getNumberOfPages());

        } catch (Exception e) {
            System.out.println(e);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard_employee, menu);

        MenuItem itemFilter = menu.findItem(R.id.action_filter);
        itemFilter.setVisible(true);

        final MenuItem itemSearch = menu.findItem(R.id.action_search);
        itemSearch.setVisible(true);

        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);

        MenuItem itemAddStudent = menu.findItem(R.id.action_add);
        itemAddStudent.setVisible(false);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));

        if (mSearchView != null) {
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.setIconifiedByDefault(false);

            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                public boolean onQueryTextChange(String newText) {

                    mStrSearchValue = newText;
                    if (newText.length() > 0) {
                        OkHttpRequest.cancelOkHttpRequest(Constants.WS_STUDENT_FILTER);
                        callApiToFilterStudentList(newText);
                    } else {
                        mStrSearchValue = "";
                        refreshStudentListData();
                    }
                    return true;
                }

                public boolean onQueryTextSubmit(String query) {
                    mStrSearchValue = query;
                    if (query.length() > 0) {
                        OkHttpRequest.cancelOkHttpRequest(Constants.WS_STUDENT_FILTER);
                        callApiToFilterStudentList(query);
                    } else {
                        mStrSearchValue = "";
                        refreshStudentListData();
                    }
                    return true;
                }
            };

            // Get the search close button image view
            ImageView closeButton = (ImageView) mSearchView.findViewById(R.id.search_close_btn);

            // Set on click listener
            closeButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    EditText et = (EditText) findViewById(R.id.search_src_text);

                    //Clear the text from EditText view
                    et.setText("");

                    //Clear query
                    mSearchView.setQuery("", false);
                    //Collapse the action view
                    mSearchView.onActionViewCollapsed();
                    //Collapse the search widget
                    itemSearch.collapseActionView();

                    mStrSearchValue = "";
                    mArrListSearchStudents.clear();
                    mArrListRunningStudents.clear();
                    mArrListCompletedStudents.clear();
                    mArrListStudents.clear();
                    mAdapterStudentList.notifyDataSetChanged();

                    callApiToLoadStudentList(false);
                }
            });

            mSearchView.setOnQueryTextListener(queryTextListener);
        }

        return true;
    }

    @Override
    public void onStudentRawClick(int position) {
        if (isFromManualAttendance || isFromAddFees) {
            if (isFromAddFees) {
                //From add fees
                Intent intent = new Intent()
                        .putExtra(KEY_INTENT_STUDENT_NAME, "" + mArrListRunningStudents.get(position).getFname()
                                + " " + mArrListRunningStudents.get(position).getLname())
                        .putExtra(INTENT_KEY_COURSE_ID,
                                "" + mArrListRunningStudents.get(position).getCourses().get(0).getId())
                        .putExtra(INTENT_KEY_COURSE_NAME,
                                "" + mArrListRunningStudents.get(position).getCourses().get(0).getName())
                        .putExtra(KEY_INTENT_STUDENT_IMAGE,
                                "" + mArrListRunningStudents.get(position).getImage())
                        .putExtra(KEY_INTENT_STUDENT_INOUT,
                                "" + mArrListRunningStudents.get(position).getIn_out_status())
                        .putExtra(KEY_INTENT_STUDENT_COURSE,
                                "" + mArrListRunningStudents.get(position).getCourses().get(0).getName())
                        .putExtra(KEY_INTENT_STUDENT_UNPAID_AMOUNT,
                                "" + mArrListRunningStudents.get(position).getUnpaid_amount())
                        .putExtra(KEY_INTENT_STUDENT_ID,
                                "" + mArrListRunningStudents.get(position).getId());
                setResult(CODE_STUDENT_SELECTION_FOR_ATTENDANCE, intent);
            } else {
                //From attendence..
                if (mSwitch_running_completed.isChecked()) {
                    Intent intent = new Intent()
                            .putExtra(KEY_INTENT_STUDENT_NAME, "" + mArrListRunningStudents.get(position).getFname()
                                    + " " + mArrListRunningStudents.get(position).getLname())
                            .putExtra(INTENT_KEY_COURSE_ID,
                                    "" + mArrListRunningStudents.get(position).getCourses().get(0).getId())
                            .putExtra(INTENT_KEY_COURSE_NAME,
                                    "" + mArrListRunningStudents.get(position).getCourses().get(0).getName())
                            .putExtra(KEY_INTENT_STUDENT_IMAGE,
                                    "" + mArrListRunningStudents.get(position).getImage())
                            .putExtra(KEY_INTENT_STUDENT_INOUT,
                                    "" + mArrListRunningStudents.get(position).getIn_out_status())
                            .putExtra(KEY_INTENT_STUDENT_COURSE,
                                    "" + mArrListRunningStudents.get(position).getCourses().get(0).getName())
                            .putExtra(KEY_INTENT_STUDENT_UNPAID_AMOUNT,
                                    "" + mArrListRunningStudents.get(position).getUnpaid_amount())
                            .putExtra(KEY_INTENT_STUDENT_ID,
                                    "" + mArrListRunningStudents.get(position).getId());

                    setResult(CODE_STUDENT_SELECTION_FOR_ATTENDANCE, intent);
                } else {
                    Intent intent = new Intent()
                            .putExtra(KEY_INTENT_STUDENT_NAME, "" + mArrListCompletedStudents.get(position).getFname()
                                    + " " + mArrListCompletedStudents.get(position).getLname())
                            .putExtra(INTENT_KEY_COURSE_ID,
                                    "" + mArrListCompletedStudents.get(position).getCourses().get(0).getId())
                            .putExtra(INTENT_KEY_COURSE_NAME,
                                    "" + mArrListCompletedStudents.get(position).getCourses().get(0).getName())
                            .putExtra(KEY_INTENT_STUDENT_IMAGE,
                                    "" + mArrListCompletedStudents.get(position).getImage())
                            .putExtra(KEY_INTENT_STUDENT_INOUT,
                                    "" + mArrListCompletedStudents.get(position).getIn_out_status())
                            .putExtra(KEY_INTENT_STUDENT_COURSE,
                                    "" + mArrListCompletedStudents.get(position).getCourses().get(0).getName())
                            .putExtra(KEY_INTENT_STUDENT_UNPAID_AMOUNT,
                                    "" + mArrListCompletedStudents.get(position).getUnpaid_amount())
                            .putExtra(KEY_INTENT_STUDENT_ID,
                                    "" + mArrListCompletedStudents.get(position).getId());

                    setResult(CODE_STUDENT_SELECTION_FOR_ATTENDANCE, intent);
                }

            }
            finish();

        } else {
            //Normal Navigation..
            if (mSwitch_running_completed.isChecked()) {
                startActivity(new Intent(StudentListActivity.this, StudentDetailsActivity.class)
                        .putExtra(KEY_INTENT_STUDENT_ID, "" + mArrListRunningStudents.get(position).getId()));
            } else {
                startActivity(new Intent(StudentListActivity.this, StudentDetailsActivity.class)
                        .putExtra(KEY_INTENT_STUDENT_ID, "" + mArrListCompletedStudents.get(position).getId()));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onStudentMoreOptionsClick(int position, ImageView ivMoreOptions) {
        openPopupMenuOptions(position, ivMoreOptions);
    }

    public void callNow(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "" + number));
        if (ActivityCompat.checkSelfPermission(StudentListActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            playSoundForAttendance("Please grant call permission from settings.", StudentListActivity.this);
            Toast.makeText(StudentListActivity.this, "Please grant call permission from settings.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            startActivity(intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void openPopupMenuOptions(final int adapterPosition, ImageView ivMoreOptions) {

        View view = mLvStudents.getChildAt(adapterPosition - mLvStudents.getFirstVisiblePosition());
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(StudentListActivity.this, view);
        popup.setGravity(Gravity.END);

        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.student_option_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                String str = "" + item.getTitle();
                switch (str) {
                    case "Call":
                        callNow("" + mArrListStudents.get(adapterPosition).getContact());
                        break;
                    case "SMS":
                        Toast.makeText(StudentListActivity.this, "In Progress", Toast.LENGTH_SHORT).show();
                        break;
                    case "Notification":
                        Toast.makeText(StudentListActivity.this, "In Progress", Toast.LENGTH_SHORT).show();
                        break;
                    case "Settings":
                        Toast.makeText(StudentListActivity.this, "In Progress", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }

    private void setSearchStudentListAdapter() {
        mAdapterStudentList = null;
        mAdapterStudentList = new StudentListAdapter(StudentListActivity.this, mArrListSearchStudents, this, isDisplayMoreOptions);
        mLvStudents.setAdapter(mAdapterStudentList);
        if (mLvStudents.getFooterViewsCount() == 0) {
            mLvStudents.addFooterView(mListFooter);
        }
        mListFooter.setVisibility(View.GONE);
    }

    private void callApiToLoadCourseList() {
        if (!CommonUtil.isInternetAvailable(StudentListActivity.this)) {
            return;
        }

        new OkHttpRequest(StudentListActivity.this,
                OkHttpRequest.Method.GET,
                WS_COURSE_LIST,
                RequestParam.loadGetRequestsData(),
                RequestParam.getNull(),
                CODE_COURSE_LIST,
                true, this);
    }

    private void callApiToFilterStudentList(String strSearch) {
        if (!CommonUtil.isInternetAvailable(StudentListActivity.this)) {
            return;
        }

        String strBranchId = "" + getSharedPrefrencesInstance(
                StudentListActivity.this)
                .getString(KEY_EMPLOYEE_BRANCH_ID,
                        "");

        if (mArrListSelectedMonths != null && mArrListSelectedMonths.size() > 0) {
            for (int i = 0; i < mArrListSelectedMonths.size(); i++) {
                if (i == 0) {
                    mStrFilterMonths =
                            "" + mArrListSelectedMonths.get(i).getMonthNumber() + "-" +
                                    "" + mArrListSelectedMonths.get(i).getStrYear();
                } else {
                    mStrFilterMonths = mStrFilterMonths + "," + mArrListSelectedMonths.get(i).getMonthNumber() + "-" +
                            "" + mArrListSelectedMonths.get(i).getStrYear();
                }
            }
        }

        if (Constants.IS_COURSE_SUPPORT) {
            /* For Course id selection Filter */
            if (mArrListSelectedCourseIds != null && mArrListSelectedCourseIds.size() > 0) {
                for (int i = 0; i < mArrListSelectedCourseIds.size(); i++) {
                    if (i == 0) {
                        mStrFilterCourseid = "" + mArrListSelectedCourseIds.get(i);
                    } else {
                        mStrFilterCourseid = mStrFilterCourseid + "," + mArrListSelectedCourseIds.get(i);
                    }
                }
            }

            /* For Course status selection Filter */
            if (mArrListSelectedStudentTypes != null && mArrListSelectedStudentTypes.size() > 0) {
                for (int i = 0; i < mArrListSelectedStudentTypes.size(); i++) {
                    if (i == 0) {
                        mStrFilterCourseStatus = "" + mArrListSelectedStudentTypes.get(i).getStrStudentType().toLowerCase();
                    } else {
                        mStrFilterCourseStatus = mStrFilterCourseStatus +
                                "," + mArrListSelectedStudentTypes.get(i).getStrStudentType().toLowerCase();
                    }
                }
            }
        }

        if (mArrListSelectedBatch != null && mArrListSelectedBatch.size() > 0) {
            for (int i = 0; i < mArrListSelectedBatch.size(); i++) {
                if (i == 0) {
                    mStrFilterBatchid = "" + mArrListSelectedBatch.get(i).getId();
                } else {
                    mStrFilterBatchid = mStrFilterBatchid + "," + mArrListSelectedBatch.get(i).getId();
                }
            }
        }

        if (mArrListSelectedStandard != null && mArrListSelectedStandard.size() > 0) {
            for (int i = 0; i < mArrListSelectedStandard.size(); i++) {
                if (i == 0) {
                    mStrFilterStandardId = "" + mArrListSelectedStandard.get(i).getId();
                } else {
                    mStrFilterStandardId = mStrFilterStandardId + "," + mArrListSelectedStandard.get(i).getId();
                }
            }
        }


        new OkHttpRequest(StudentListActivity.this,
                OkHttpRequest.Method.POST,
                WS_STUDENT_FILTER,
                RequestParam.filterStudent(
                        "" + mStrFilterCourseStatus,
                        "" + mStrFilterCourseid,
                        "" + mStrFilterMonths,
                        "" + strSearch,
                        "" + strBranchId,
                        "" + mStrFilterStandardId,
                        "" + mStrFilterBatchid),
                RequestParam.getNull(),
                CODE_FILTER_STUDENT,
                false, this);
    }


    @Override
    public void onCourseSelected(int position, boolean b) {
        try {
            mArrListCourses.get(position).setSelected(b);
            mAdapterCourseList.notifyDataSetChanged();
            manageFilterForCourses();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMonthRawClick(int position, boolean b) {
        try {
            mArrListMonths.get(position).setSelected(b);
            mAdapterMonthList.notifyDataSetChanged();
            manageFiltersForMonths();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void manageFiltersForMonths() {
        int temp = 0;
        for (int i = 0; i < mArrListMonths.size(); i++) {
            if (mArrListMonths.get(i).isSelected()) {
                temp = 1;
                mTvClearMonthFilter.setEnabled(true);
                mBtnApplyMonthFilter.setEnabled(true);
                mBtnApplyMonthFilter.setBackgroundColor(getResources().getColor(R.color.colorThemeBlue));
                mTvClearMonthFilter.setTextColor(getResources().getColor(R.color.colorThemeBlue));
                return;
            }
        }

        if (temp == 0) {
            mTvTotalSelectedmonths.setVisibility(View.GONE);
            mTvTotalSelectedmonths.setText("");
            mTvClearMonthFilter.setEnabled(false);
            mBtnApplyMonthFilter.setBackgroundColor(getResources().getColor(R.color.colorGrey));
            mTvClearMonthFilter.setTextColor(getResources().getColor(R.color.colorGrey));
            mBtnApplyMonthFilter.setEnabled(false);
        }
    }

    private void manageFiltersForBatches() {
        int temp = 0;
        for (int i = 0; i < mArrListBatches.size(); i++) {
            if (mArrListBatches.get(i).isSelected()) {
                temp = 1;
                mTvClearBatchFilter.setEnabled(true);
                mBtnApplyBatchFilter.setEnabled(true);
                mBtnApplyBatchFilter.setBackgroundColor(getResources().getColor(R.color.colorThemeBlue));
                mTvClearBatchFilter.setTextColor(getResources().getColor(R.color.colorThemeBlue));
                return;
            }
        }

        if (temp == 0) {
            mTvTotalSelectedBatches.setVisibility(View.GONE);
            mTvTotalSelectedBatches.setText("");
            mTvClearBatchFilter.setEnabled(false);
            mBtnApplyBatchFilter.setBackgroundColor(getResources().getColor(R.color.colorGrey));
            mTvClearBatchFilter.setTextColor(getResources().getColor(R.color.colorGrey));
            mBtnApplyBatchFilter.setEnabled(false);
        }
    }

    private void manageFiltersForStandards() {
        int temp = 0;
        for (int i = 0; i < mArrListStandards.size(); i++) {
            if (mArrListStandards.get(i).isSelected()) {
                temp = 1;
                mTvClearStandardFilter.setEnabled(true);
                mBtnApplyStandardFilter.setEnabled(true);
                mBtnApplyStandardFilter.setBackgroundColor(getResources().getColor(R.color.colorThemeBlue));
                mTvClearStandardFilter.setTextColor(getResources().getColor(R.color.colorThemeBlue));
                return;
            }
        }

        if (temp == 0) {
            mTvTotalSelectedStandard.setVisibility(View.GONE);
            mTvTotalSelectedStandard.setText("");
            mTvClearStandardFilter.setEnabled(false);
            mBtnApplyStandardFilter.setBackgroundColor(getResources().getColor(R.color.colorGrey));
            mTvClearStandardFilter.setTextColor(getResources().getColor(R.color.colorGrey));
            mBtnApplyStandardFilter.setEnabled(false);
        }
    }

    private void manageFiltersForStudentTypes() {
        int temp = 0;
        for (int i = 0; i < mArrListStudentTypes.size(); i++) {
            if (mArrListStudentTypes.get(i).isSelected()) {
                temp = 1;
                mTvClearStudentTypesFilter.setEnabled(true);
                mBtnApplyStudentTypeFilter.setEnabled(true);
                mBtnApplyStudentTypeFilter.setBackgroundColor(getResources().getColor(R.color.colorThemeBlue));
                mTvClearStudentTypesFilter.setTextColor(getResources().getColor(R.color.colorThemeBlue));
                return;
            }
        }
        if (temp == 0) {
            mTvTotalSelectedStudentTypes.setVisibility(View.GONE);
            mTvTotalSelectedStudentTypes.setText("");
            mTvClearStudentTypesFilter.setEnabled(false);
            mBtnApplyStudentTypeFilter.setBackgroundColor(getResources().getColor(R.color.colorGrey));
            mTvClearStudentTypesFilter.setTextColor(getResources().getColor(R.color.colorGrey));
            mBtnApplyStudentTypeFilter.setEnabled(false);
        }
    }

    @Override
    public void onStudentTypesRawClick(int position, boolean b) {
        try {
            mArrListStudentTypes.get(position).setSelected(b);
            mAdapterStudentTypeList.notifyDataSetChanged();
            manageFiltersForStudentTypes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFilterStudentBottomSheetDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.bottomsheet_filter_students, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);

        tv_apply_student_filter = dialog.findViewById(R.id.tv_apply_student_filter);
        tabLayoutStudentList = dialog.findViewById(R.id.tabsStudentFilter);
        vpStudentList = dialog.findViewById(R.id.vpStudentFilter);

        StudentFilterPagerAdapter studentListPagerAdapter = new StudentFilterPagerAdapter(getSupportFragmentManager());
        vpStudentList.setAdapter(studentListPagerAdapter);
        vpStudentList.setOffscreenPageLimit(3);

        tabLayoutStudentList.setupWithViewPager(vpStudentList);

        tv_apply_student_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StudentListActivity.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    @Override
    public void onBatchListClick(int position, boolean isSelected) {
        try {
            mArrListBatches.get(position).setSelected(isSelected);
            mAdapterBatchList.notifyDataSetChanged();
            manageFiltersForBatches();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStandardClick(int position, boolean isSelected) {
        try {
            mArrListStandards.get(position).setSelected(isSelected);
            mAdapterStandardList.notifyDataSetChanged();
            manageFiltersForStandards();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
/*
*



  course_status: Running,Pending,Completed,Running,Pending,Completed
  course_id: 31,31,31
  month: 01-2019,02-2019,03-2019,10-2019,11-2019,12-2019





* */