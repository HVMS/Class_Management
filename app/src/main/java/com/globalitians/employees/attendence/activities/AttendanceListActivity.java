package com.globalitians.employees.attendence.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.attendence.adapter.AttendanceListAdapter;
import com.globalitians.employees.attendence.adapter.AttendanceListSpecificAdapter;
import com.globalitians.employees.attendence.model.ModelAttendanceList;
import com.globalitians.employees.other.adapter.SingleRawAdapter;
import com.globalitians.employees.students.activities.AddStudentActivity;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.LogUtil;
import com.globalitians.employees.utility.PermissionManager;
import com.globalitians.employees.utility.Permissions;
import com.globalitians.employees.utility.network.HTTPUtils;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import static com.globalitians.employees.utility.Constants.CODE_GET_ATTENDANCE_LIST;
import static com.globalitians.employees.utility.Constants.CODE_MAKE_OUT_ENTRY;
import static com.globalitians.employees.utility.Constants.CODE_SEARCH_STUDENT;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.KEY_FILTER_BY;
import static com.globalitians.employees.utility.Constants.KEY_FILTER_END_DATE;
import static com.globalitians.employees.utility.Constants.KEY_FILTER_MONTH;
import static com.globalitians.employees.utility.Constants.KEY_FILTER_MONTH_YEAR;
import static com.globalitians.employees.utility.Constants.KEY_FILTER_SELECTED_DATE;
import static com.globalitians.employees.utility.Constants.KEY_FILTER_START_DATE;
import static com.globalitians.employees.utility.Constants.KEY_FILTER_YEAR;
import static com.globalitians.employees.utility.Constants.KEY_OUT_REQUIRED;
import static com.globalitians.employees.utility.Constants.LOGIN_PREFRENCES;
import static com.globalitians.employees.utility.Constants.WS_STUDENT_FILTER;

public class AttendanceListActivity extends AppCompatActivity implements OkHttpInterface, AttendanceListAdapter.AttendanceClickListener, View.OnClickListener {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //private Dialog mDialogFilterAttendance;
    private Dialog mDialogFilterByDate;
    private Dialog mDialogFilterByDateRange;
    private Dialog mDialogFilterByMonth;
    private Dialog mDialogFilterByYear;

    private FloatingActionButton mFabFilterAttendance;

    private RecyclerView mRvAttendanceList;

    private DatePickerDialog datePickerDialog_dateFilter = null;
    private DatePickerDialog datePickerDialog_startDate = null;
    private DatePickerDialog datePickerDialog_endDate = null;

    private ArrayList<ModelAttendanceList.Attendence> mALAttendaceList;
    private ArrayList<ModelAttendanceList.Attendence> mALSearchAttendaceList;
    private AttendanceListAdapter mAttendanceListAdapter;
    private AttendanceListSpecificAdapter mAttendanceListAdapterOut;
    private AttendanceListSpecificAdapter mAttendanceListAdapterIn;
    private String strCurrentDate = "";
    private boolean fabExpanded = false;

    //taken
    private Animation animSlideUp;
    private Animation animSlideDown;
    private RelativeLayout layoutFabSubMenu;
    private LinearLayout layoutContainerFabs;
    private LinearLayout layoutFabFilterOutRequired;
    private LinearLayout layoutFabFilterByDate;
    private LinearLayout layoutFabFilterBetweenDates;
    private LinearLayout layoutFabFilterByMonth;
    private LinearLayout layoutFabFilterByYear;
    //taken

    private RelativeLayout relSingalStudent;//layout for particular student raw clicked
    private RelativeLayout relFilterCriteria;
    private TextView tvFilterAppliedValue;
    private TextView tvFilterAppliedValueStudent;
    private ImageView ivClearFilter;
    private ImageView ivClearFilterStudent;
    private ImageView ivStudentFilterBack;
    private String strRecentApply = "";

    /////
    private TextView tvStartDate;
    private TextView tvEndDate;
    private TextView tvSelectDate;
    private Spinner spinnerMonth;
    private Spinner spinnerYearWithMonth;
    private Spinner spinnerYear;
    private TextView tvNoData;
    private SwipeRefreshLayout srAttendanceList;
    private int makeOutPosition = -1;

    //When clicking on single raw of student
    private String singalStudentId = null;

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
            Toast.makeText(AttendanceListActivity.this, "Please grant storage permissions.", Toast.LENGTH_SHORT).show();
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
        setFullScreenMode(AttendanceListActivity.this);
        setContentView(R.layout.activity_attendance_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViews();

        getSharedPrefrences();

        animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slideup);
        animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slidedown);

        defaultCallForAttendanceList(singalStudentId);
    }

    private void exportStudentDataToExcel() {
        File sd = Environment.getExternalStorageDirectory();
        String csvFile = "GlobalIT_Attendance_Report.xls";

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
            WritableSheet sheet = workbook.createSheet("attendanceList", 0);
            sheet.setName("Attendance Report");
            // column and row
            sheet.addCell(new Label(0, 0, "ID"));
            sheet.addCell(new Label(1, 0, "Name"));
            sheet.addCell(new Label(2, 0, "DATE"));
            sheet.addCell(new Label(3, 0, "INTime"));
            sheet.addCell(new Label(4, 0, "OUTTime"));
            sheet.addCell(new Label(5, 0, "Status"));
            sheet.addCell(new Label(6, 0, "StudentImage"));
            if (mALAttendaceList != null && mALAttendaceList.size() > 0) {
                for (int i = 0; i < mALAttendaceList.size(); i++) {
                    sheet.addCell(new Label(0, i + 1, mALAttendaceList.get(i).getAttendence_id().toString()));
                    sheet.addCell(new Label(1, i + 1, mALAttendaceList.get(i).getStudentName().toString()));
                    sheet.addCell(new Label(2, i + 1, mALAttendaceList.get(i).getDate().toString()));
                    sheet.addCell(new Label(3, i + 1, mALAttendaceList.get(i).getIn_time().toString()));
                    sheet.addCell(new Label(4, i + 1, mALAttendaceList.get(i).getOut_time().toString()));
                    sheet.addCell(new Label(5, i + 1, mALAttendaceList.get(i).getStatus().toString()));
                    sheet.addCell(new Label(6, i + 1, mALAttendaceList.get(i).getStudentImage().toString()));
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
            Toast.makeText(AttendanceListActivity.this, "No Application Available to View Excel", Toast.LENGTH_SHORT).show();
        }
    }


    private void defaultCallForAttendanceList(String singalStudentId) {
        if (mALAttendaceList != null && mALAttendaceList.size() > 0) {
            mALAttendaceList.clear();
            if (mAttendanceListAdapter != null) {
                mAttendanceListAdapter.notifyDataSetChanged();
            }
        }
        mALAttendaceList = new ArrayList<>();

        if (sharedPreferences != null && sharedPreferences.contains(KEY_FILTER_BY)) {
            //If Filter is applied and user not coming first time..
            //loading attendance data with filtered values
            checkForTheStoredPrefrenceValuesAndDoChanges(singalStudentId);
        } else {
            //default first time call when user comes to this screen
            //loading attendance data with default current date
            relFilterCriteria.setVisibility(View.GONE);
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            Date currentDate = Calendar.getInstance().getTime();
            strCurrentDate = df.format(currentDate);

            //current day,month and year
            callApiToGetAttendaceList("" + strCurrentDate,
                    "", "", "" + singalStudentId, "", "");
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_employee, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        //searchView.setMinimumWidth(Integer.MAX_VALUE);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(true);
        MenuItem itemedit = menu.findItem(R.id.action_edit);
        itemedit.setVisible(false);
        MenuItem itemadd = menu.findItem(R.id.action_add);
        itemadd.setVisible(false);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //some operation
                setAttendanceListAdapter(mALAttendaceList);
                return true;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //some operation
                if (mALSearchAttendaceList != null && mALSearchAttendaceList.size() > 0) {
                    mALSearchAttendaceList.clear();
                }
                mALSearchAttendaceList = new ArrayList<>();
            }
        });
        EditText searchPlate = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchPlate.setHint("Search Student");
        searchPlate.setTextColor(Color.WHITE);
        searchPlate.setHintTextColor(Color.WHITE);
        searchPlate.setTextSize(16);
        //ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorWhite));
        //searchPlate.setBackgroundTintList(colorStateList);
        searchPlate.setPadding(2, 2, 2, 2);

        //searchPlate.setBackgroundResource(R.drawable.drawable_search_background);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    HTTPUtils.cancelRequest(WS_STUDENT_FILTER);
                    callApiToSearchStudent(query);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    HTTPUtils.cancelRequest(WS_STUDENT_FILTER);
                    callApiToSearchStudent(newText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        return true;
    }


    private void getSharedPrefrences() {
        sharedPreferences = getSharedPreferences(LOGIN_PREFRENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void findViews() {

        layoutFabSubMenu = findViewById(R.id.relFab);
        layoutContainerFabs = findViewById(R.id.linContainerFabs);
        mFabFilterAttendance = findViewById(R.id.fabFilterAttendance);

        layoutFabFilterOutRequired = findViewById(R.id.layoutFabFilterOutRequired);
        layoutFabFilterByDate = findViewById(R.id.layoutFabFilterByDates);
        layoutFabFilterBetweenDates = findViewById(R.id.layoutFabFilterBetweenDates);
        layoutFabFilterByMonth = findViewById(R.id.layoutFabFilterByMonth);
        layoutFabFilterByYear = findViewById(R.id.layoutFabFilterByYear);

        layoutFabFilterOutRequired.setOnClickListener(this);
        layoutFabFilterByMonth.setOnClickListener(this);
        layoutFabFilterBetweenDates.setOnClickListener(this);
        layoutFabFilterByDate.setOnClickListener(this);
        layoutFabFilterByYear.setOnClickListener(this);
        mFabFilterAttendance.setOnClickListener(this);

        mRvAttendanceList = findViewById(R.id.rv_attendance_list);

        relFilterCriteria = findViewById(R.id.relFilterCriteria);
        relSingalStudent = findViewById(R.id.relStudent);
        tvFilterAppliedValue = findViewById(R.id.tvFilterApliedValue);
        tvFilterAppliedValueStudent = findViewById(R.id.tvFilterApliedValueStudent);
        ivClearFilterStudent = findViewById(R.id.ivClearFilterStudent);
        ivClearFilterStudent.setOnClickListener(this);
        ivClearFilter = findViewById(R.id.ivClearFilter);
        ivClearFilter.setOnClickListener(this);
        ivStudentFilterBack = findViewById(R.id.ivStudentFilterBack);
        ivStudentFilterBack.setOnClickListener(this);

        tvNoData = findViewById(R.id.tvNoData);
        srAttendanceList = findViewById(R.id.swipeToRefreshAttendanceList);
        srAttendanceList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                defaultCallForAttendanceList(singalStudentId);
            }
        });
    }

    private void callApiToGetAttendaceList(String date, String month,
                                           String year, String student_id,
                                           String start_date, String end_date) {
        if (!CommonUtil.isInternetAvailable(AttendanceListActivity.this)) {
            //finish();
            return;
        }

        String strOutRequired = "";
        if (strRecentApply.equals("out_required")) {
            strOutRequired = "1";
        }

        if (CommonUtil.isNullString(student_id)) {
            setTitle("Attendance Report");
        }

        new OkHttpRequest(AttendanceListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_ATTENDANCE_LIST,
                RequestParam.loadAttendenceList(
                        "" + date,
                        "" + month,
                        "" + year,
                        "" + student_id,
                        "" + start_date,
                        "" + end_date,
                        "" + strOutRequired,
                        "" + getSharedPrefrencesInstance(AttendanceListActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, "")),
                RequestParam.getNull(),
                CODE_GET_ATTENDANCE_LIST,
                true, this);
    }

    private void callApiToMakOutAttendanceEntry(int position, String selectedOutTime) {
        if (!CommonUtil.isInternetAvailable(AttendanceListActivity.this)) {
            //finish();
            return;
        }

        new OkHttpRequest(AttendanceListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_OUT_ENTRY,
                RequestParam.makeOutEntry(
                        "" + mALAttendaceList.get(position).getAttendence_id(),
                        selectedOutTime,
                        "" + getSharedPrefrencesInstance(AttendanceListActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, "")),
                RequestParam.getNull(),
                CODE_MAKE_OUT_ENTRY,
                true, this);

        makeOutPosition = position;
    }

    private void callApiToSearchStudent(String strSearch) {
        if (!CommonUtil.isInternetAvailable(AttendanceListActivity.this)) {
            //finish();
            return;
        }
        new OkHttpRequest(AttendanceListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_STUDENT_FILTER,
                RequestParam.searchStudent(
                        "" + strSearch,
                        "" + getSharedPrefrencesInstance(AttendanceListActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, "")),
                RequestParam.getNull(),
                CODE_SEARCH_STUDENT,
                false, this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.action_add) {
            startActivity(new Intent(AttendanceListActivity.this, AddStudentActivity.class));
            return true;
        }

        if (id == R.id.action_pdf) {
            Toast.makeText(this, "In Progress", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_excel) {
            if (PermissionManager.hasPermissions(AttendanceListActivity.this, Permissions.STORAGE_PERMISSIONS)) {
                exportStudentDataToExcel();
            } else {
                PermissionManager.requestPermissions(AttendanceListActivity.this, Constants.CODE_RUNTIME_STORAGE_PERMISSION,
                        permissionListener, "", Permissions.STORAGE_PERMISSIONS);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {

        /*
         * {"status":"success","message":"Attendence List Found","attendences":[{"attendence_id":82,"student_id":23,"student_name":"Bharga Modi","student_image":"http:\/\/globalitians.com\/\/student\/ZeUrqb9E.jpg","date":"02-08-2019","status":"In","in_time":"2019-08-02 02:15:57","out_time":""},{"attendence_id":83,"student_id":21,"student_name":"Nik patel","student_image":"http:\/\/globalitians.com\/\/student\/J1N2CB73.jpg","date":"02-08-2019","status":"In","in_time":"2019-08-02 02:17:10","out_time":""},{"attendence_id":85,"student_id":21,"student_name":"Nik patel","student_image":"http:\/\/globalitians.com\/\/student\/J1N2CB73.jpg","date":"06-08-2019","status":"Out","in_time":"2019-08-06 07:05:19","out_time":"2019-08-06 07:05:25"},{"attendence_id":87,"student_id":13,"student_name":"Digvijaysinh jadeja","student_image":"","date":"06-08-2019","status":"Out","in_time":"2019-08-06 08:20:49","out_time":"2019-08-06 08:20:35"},{"attendence_id":88,"student_id":11,"student_name":"Niharika Patel","student_image":"","date":"07-08-2019","status":"Out","in_time":"2019-08-07 07:20:00","out_time":"2019-08-07 07:20:57"},{"attendence_id":89,"student_id":23,"student_name":"Bharga Modi","student_image":"http:\/\/globalitians.com\/\/student\/ZeUrqb9E.jpg","date":"09-08-2019","status":"In","in_time":"2019-08-09 04:01:14","out_time":""},{"attendence_id":90,"student_id":11,"student_name":"Niharika Patel","student_image":"","date":"10-08-2019","status":"In","in_time":"2019-08-10 09:00:12","out_time":""},{"attendence_id":91,"student_id":21,"student_name":"Nik patel","student_image":"http:\/\/globalitians.com\/\/student\/J1N2CB73.jpg","date":"10-08-2019","status":"Out","in_time":"2019-08-10 09:01:35","out_time":"2019-08-10 09:00:39"},{"attendence_id":92,"student_id":25,"student_name":"Jaimin Modi","student_image":"","date":"10-08-2019","status":"Out","in_time":"2019-08-10 09:01:45","out_time":"2019-08-10 09:00:56"},{"attendence_id":93,"student_id":17,"student_name":"Urvish Desai","student_image":"","date":"10-08-2019","status":"In","in_time":"2019-08-10 15:28:32","out_time":""},{"attendence_id":94,"student_id":25,"student_name":"Jaimin Modi","student_image":"","date":"11-08-2019","status":"Out","in_time":"2019-08-11 01:07:52","out_time":"2019-08-11 01:08:23"},{"attendence_id":95,"student_id":21,"student_name":"Nik patel","student_image":"http:\/\/globalitians.com\/\/student\/J1N2CB73.jpg","date":"11-08-2019","status":"Out","in_time":"2019-08-11 01:28:21","out_time":"2019-08-10 15:30:30"},{"attendence_id":96,"student_id":21,"student_name":"Nik patel","student_image":"http:\/\/globalitians.com\/\/student\/J1N2CB73.jpg","date":"12-08-2019","status":"In","in_time":"2019-08-12 04:26:45","out_time":""},{"attendence_id":97,"student_id":21,"student_name":"Nik patel","student_image":"http:\/\/globalitians.com\/\/student\/J1N2CB73.jpg","date":"12-08-2019","status":"In","in_time":"2019-08-12 23:08:02","out_time":""},{"attendence_id":98,"student_id":21,"student_name":"Nik patel","student_image":"http:\/\/globalitians.com\/\/student\/J1N2CB73.jpg","date":"14-08-2019","status":"Out","in_time":"2019-08-13 23:38:45","out_time":"2019-08-13 23:39:25"},{"attendence_id":99,"student_id":21,"student_name":"Nik patel","student_image":"http:\/\/globalitians.com\/\/student\/J1N2CB73.jpg","date":"14-08-2019","status":"Out","in_time":"2019-08-14 00:10:37","out_time":"2019-08-14 00:10:52"},{"attendence_id":100,"student_id":21,"student_name":"Nik patel","student_image":"http:\/\/globalitians.com\/\/student\/J1N2CB73.jpg","date":"14-08-2019","status":"In","in_time":"2019-08-14 00:30:22","out_time":""},{"attendence_id":101,"student_id":21,"student_name":"Nik patel","student_image":"http:\/\/globalitians.com\/\/student\/J1N2CB73.jpg","date":"13-08-2019","status":"Out","in_time":"2019-08-13 19:39:13","out_time":"2019-08-13 19:39:38"},{"attendence_id":102,"student_id":21,"student_name":"Nik patel","student_image":"http:\/\/globalitians.com\/\/student\/J1N2CB73.jpg","date":"15-08-2019","status":"Out","in_time":"2019-08-15 18:24:00","out_time":"2019-08-1
         * */

        try {
            mRvAttendanceList.setVisibility(View.VISIBLE);
            Log.e(">>> attendace : ", "" + response);
            if (srAttendanceList != null && srAttendanceList.isRefreshing()) {
                srAttendanceList.setRefreshing(false);
            }
            if (response == null) {
                Toast.makeText(this, "Error in Getting response", Toast.LENGTH_SHORT).show();
                return;
            }

            switch (requestId) {
                case CODE_GET_ATTENDANCE_LIST:
                    //relFilterCriteria.setVisibility(View.VISIBLE);
                    mALAttendaceList.clear();
                    mALAttendaceList = new ArrayList<>();
                    if (mAttendanceListAdapter != null) {
                        mAttendanceListAdapter.notifyDataSetChanged();
                    }

                    final Gson gsonAttendance = new Gson();
                    try {
                        ModelAttendanceList modelAttendanceList =
                                gsonAttendance.fromJson(response, ModelAttendanceList.class);

                        if (modelAttendanceList.getStatus().equals(Constants.SUCCESS_CODE)) {
                            if (modelAttendanceList.getAttendences() != null && modelAttendanceList.getAttendences().size() > 0) {
                                tvNoData.setVisibility(View.GONE);
//                                if (mALAttendaceList != null && mALAttendaceList.size() > 0) {
//                                    mALAttendaceList.clear();
//                                    mALAttendaceList = new ArrayList<>();
//                                    if (mAttendanceListAdapter != null) {
//                                        mAttendanceListAdapter.notifyDataSetChanged();
//                                    }
//                                }
                                for (int i = 0; i < modelAttendanceList.getAttendences().size(); i++) {
                                    if (!CommonUtil.isNullString(modelAttendanceList.getAttendences().get(i).getIn_time()) ||
                                            !CommonUtil.isNullString(modelAttendanceList.getAttendences().get(i).getOut_time())) {
                                        mALAttendaceList.add(modelAttendanceList.getAttendences().get(i));
                                    }
                                }
                                //mAttendanceListAdapter.notifyDataSetChanged();
                                setAttendanceListAdapter(mALAttendaceList);
                            } else {
                                mAttendanceListAdapter.notifyDataSetChanged();
                                tvNoData.setVisibility(View.VISIBLE);
                                Toast.makeText(AttendanceListActivity.this, "No Data found", Toast.LENGTH_SHORT).show();
                                //finish();
                            }

                            //To clear previous filter and apply recent filter if success only..
                            switch (strRecentApply) {
                                case "date":
                                    removeFilterData();

                                    editor = sharedPreferences.edit();
                                    editor.putString(KEY_FILTER_BY, "date");
                                    editor.putString(KEY_FILTER_SELECTED_DATE, "" + tvSelectDate.getText().toString());
                                    editor.commit();

                                    tvFilterAppliedValue.setText("Filter For: " + sharedPreferences.getString(KEY_FILTER_SELECTED_DATE, ""));
                                    tvFilterAppliedValue.setTextColor(Color.BLACK);
                                    relFilterCriteria.setVisibility(View.VISIBLE);

                                    break;
                                case "date_range":

                                    removeFilterData();

                                    editor = sharedPreferences.edit();
                                    editor.putString(KEY_FILTER_BY, "date_range");
                                    editor.putString(KEY_FILTER_START_DATE, "" + tvStartDate.getText().toString());
                                    editor.putString(KEY_FILTER_END_DATE, "" + tvEndDate.getText().toString());
                                    editor.commit();

                                    tvFilterAppliedValue.setText("Filter Between: " + tvStartDate.getText().toString() + " & " + tvEndDate.getText().toString());
                                    tvFilterAppliedValue.setTextColor(Color.BLACK);
                                    relFilterCriteria.setVisibility(View.VISIBLE);

                                    break;
                                case "month_year":
                                    removeFilterData();

                                    editor = sharedPreferences.edit();
                                    editor.putString(KEY_FILTER_BY, "month_year");
                                    //editor.putString(KEY_FILTER_MONTH, "" + spinnerMonth.getSelectedItem().toString());
                                    if (spinnerMonth.getSelectedItemPosition() > 9) {
                                        editor.putString(KEY_FILTER_MONTH, "" + (spinnerMonth.getSelectedItemPosition() + 1));
                                        editor.commit();
                                    } else {
                                        editor.putString(KEY_FILTER_MONTH, "0" + (spinnerMonth.getSelectedItemPosition() + 1));
                                        editor.commit();
                                    }
                                    editor.putString(KEY_FILTER_MONTH_YEAR, "" + spinnerYearWithMonth.getSelectedItem().toString());
                                    editor.commit();

                                    //tvFilterAppliedValue.setText("Filter For: " + spinnerMonth.getSelectedItem().toString() + ", " + spinnerYearWithMonth.getSelectedItem().toString());
                                    tvFilterAppliedValue.setText("Filter for Month: " + sharedPreferences.getString(KEY_FILTER_MONTH, "") + ", " + sharedPreferences.getString(KEY_FILTER_MONTH_YEAR, ""));
                                    tvFilterAppliedValue.setTextColor(Color.BLACK);
                                    relFilterCriteria.setVisibility(View.VISIBLE);

                                    if (mALAttendaceList != null && mALAttendaceList.size() > 0) {
                                        tvNoData.setVisibility(View.GONE);
                                        mRvAttendanceList.setVisibility(View.VISIBLE);
                                    } else {
                                        tvNoData.setVisibility(View.VISIBLE);
                                        mRvAttendanceList.setVisibility(View.GONE);
                                    }

                                    break;
                                case "year":
                                    removeFilterData();

                                    editor = sharedPreferences.edit();
                                    editor.putString(KEY_FILTER_BY, "year");
                                    editor.putString(KEY_FILTER_YEAR, "" + spinnerYear.getSelectedItem().toString());
                                    editor.commit();

                                    tvFilterAppliedValue.setText("Filter For: " + spinnerYear.getSelectedItem().toString());
                                    tvFilterAppliedValue.setTextColor(Color.BLACK);
                                    relFilterCriteria.setVisibility(View.VISIBLE);

                                    break;

                                case "out_required":
                                    removeFilterData();

                                    editor = sharedPreferences.edit();
                                    editor.putString(KEY_OUT_REQUIRED, "1");
                                    editor.putString(KEY_FILTER_BY, "out_required");
                                    editor.commit();

                                    tvFilterAppliedValue.setText("Filter for: OUT REQUIRED");
                                    tvFilterAppliedValue.setTextColor(Color.RED);
                                    relFilterCriteria.setVisibility(View.VISIBLE);
                                    break;
                            }

                        } else {
                            Toast.makeText(this, "No Success", Toast.LENGTH_SHORT).show();
                            Toast.makeText(this, "Something going wrong.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CODE_SEARCH_STUDENT:

                    relFilterCriteria.setVisibility(View.GONE);
                    //Toast.makeText(this, "" + response, Toast.LENGTH_SHORT).show();
                    mALAttendaceList.clear();
                    mALAttendaceList = new ArrayList<>();
                    if (mAttendanceListAdapter != null) {
                        mAttendanceListAdapter.notifyDataSetChanged();
                    }

                    final Gson gsonAttendanceSearch = new Gson();
                    try {
                        ModelAttendanceList modelAttendanceList =
                                gsonAttendanceSearch.fromJson(response, ModelAttendanceList.class);


                        if (modelAttendanceList.getStatus().equals(Constants.SUCCESS_CODE)) {
                            if (modelAttendanceList.getAttendences() != null && modelAttendanceList.getAttendences().size() > 0) {
                                tvNoData.setVisibility(View.GONE);
                                for (int i = 0; i < modelAttendanceList.getAttendences().size(); i++) {
                                    if (!CommonUtil.isNullString(modelAttendanceList.getAttendences().get(i).getIn_time()) ||
                                            !CommonUtil.isNullString(modelAttendanceList.getAttendences().get(i).getOut_time())) {
                                        mALSearchAttendaceList.add(modelAttendanceList.getAttendences().get(i));
                                    }
                                }
                                //mAttendanceListAdapter.notifyDataSetChanged();
                                setAttendanceListAdapter(mALSearchAttendaceList);
                            } else {
                                mAttendanceListAdapter.notifyDataSetChanged();
                                tvNoData.setVisibility(View.VISIBLE);
                                Toast.makeText(AttendanceListActivity.this, "No Data found", Toast.LENGTH_SHORT).show();
                                //finish();
                            }
                        } else {
                            Toast.makeText(this, "No Success", Toast.LENGTH_SHORT).show();
                            Toast.makeText(this, "Something going wrong.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CODE_MAKE_OUT_ENTRY:
                    LogUtil.e(">>> Out Response >>", "" + response);

                    JSONObject json = new JSONObject(response);
                    if (json.has("status")) {
                        if (json.getString("status").equals("success")) {
                            Toast.makeText(this, "" + json.getString("message"), Toast.LENGTH_SHORT).show();
                            playSoundForAttendance("" + json.getString("message"), AttendanceListActivity.this);
                        }
                    }
                    if (makeOutPosition != -1) {
                        mALAttendaceList.remove(makeOutPosition);
                        //mALAttendaceList.get(makeOutPosition).setStatus("out");
                        //mALAttendaceList.get(makeOutPosition).setOut_time("22:36:30");
                        mAttendanceListAdapter.notifyDataSetChanged();
                        if (mALAttendaceList.size() == 0) {
                            tvNoData.setVisibility(View.VISIBLE);
                        } else {
                            tvNoData.setVisibility(View.GONE);
                        }
                        makeOutPosition = -1;
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onAttendanceClick(int position) {
        if (relSingalStudent.getVisibility() == View.GONE) {
            /*callApiToGetAttendaceList(
                    "",
                    "",
                    "",
                    "" + mALAttendaceList.get(position).getStudentId(),
                    "",
                    "");*/
            setTitle("" + mALAttendaceList.get(position).getStudentName());
            relSingalStudent.setVisibility(View.VISIBLE);
            tvFilterAppliedValueStudent.setText("" + mALAttendaceList.get(position).getStudentName());

            singalStudentId = "" + mALAttendaceList.get(position).getStudentId();
            checkForTheStoredPrefrenceValuesAndDoChanges(singalStudentId);


        } else {
            Toast.makeText(this, "Displaying records for " + mALAttendaceList.get(position).getStudentName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttendanceDeleteClick(int position) {
        Toast.makeText(this, "In Progress", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCallClick(int position) {
        Toast.makeText(this, "In Progress", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOutClick(final int position) {
        AlertDialog dialog = new AlertDialog.Builder(AttendanceListActivity.this)
                .setMessage("Are you sure, You want to make Out entry?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        openTimePickerDialogForSettingOutTime(position);
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing, user does not want to request

                    }
                }).create();
        dialog.show();
    }

    private void openTimePickerDialogForSettingOutTime(final int position) {
        // TODO Auto-generated method stub
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AttendanceListActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (hourOfDay < 10 && minute < 10) {
                    callApiToMakOutAttendanceEntry(position, "0" + hourOfDay + ":0" + minute);
                    return;
                } else if (hourOfDay < 10) {
                    callApiToMakOutAttendanceEntry(position, "0" + hourOfDay + ":" + minute);
                    return;
                } else if (minute < 10) {
                    callApiToMakOutAttendanceEntry(position, "" + hourOfDay + ":0" + minute);
                    return;
                } else {
                    callApiToMakOutAttendanceEntry(position, "" + hourOfDay + ":" + minute);
                }

            }
        }, hour, minute, true);
        mTimePicker.setTitle("Select Out Time");
        mTimePicker.show();
    }

    private void setAttendanceListAdapter(ArrayList<ModelAttendanceList.Attendence> arrListAttendance) {
        mAttendanceListAdapter = new AttendanceListAdapter(AttendanceListActivity.this, arrListAttendance, this);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        mRvAttendanceList.setLayoutManager(manager);
        mRvAttendanceList.setAdapter(mAttendanceListAdapter);
        if (arrListAttendance != null && arrListAttendance.size() > 0) {
            mRvAttendanceList.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        } else {
            if (arrListAttendance != null && arrListAttendance.size() == 0) {
                mRvAttendanceList.setVisibility(View.GONE);
                tvNoData.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           /* case R.id.tvRefine:
                openFilterAttendanceDialog();
                break;*/
            case R.id.ivStudentFilterBack:
            case R.id.ivClearFilterStudent:
                singalStudentId = "";
                relSingalStudent.setVisibility(View.GONE);
                defaultCallForAttendanceList(singalStudentId);
                break;
            case R.id.ivClearFilter:
                strRecentApply = "";
                clearFilter();
                break;

            case R.id.fabFilterAttendance:
                if (fabExpanded == true) {
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
                break;

            case R.id.layoutFabFilterOutRequired:
                try {
                    if (!CommonUtil.isInternetAvailable(AttendanceListActivity.this)) {
                        return;
                    }

                    closeSubMenusFab();

                    strRecentApply = "out_required";

                    removeFilterData();

                    callApiToGetAttendaceList("", "", "", "", "", "");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.layoutFabFilterByDates:
                closeSubMenusFab();
                openFilterByDateDialog();
                break;

            case R.id.layoutFabFilterBetweenDates:
                closeSubMenusFab();
                openFilterByDateRangeDialog();
                break;

            case R.id.layoutFabFilterByMonth:
                closeSubMenusFab();
                openFilterByMonth();
                break;

            case R.id.layoutFabFilterByYear:
                closeSubMenusFab();
                openFilterByYear();
                break;
        }
    }

    private void clearFilter() {
        AlertDialog dialog = new AlertDialog.Builder(AttendanceListActivity.this)
                .setMessage("Are you sure, You want to clear filters ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeFilterData();
                        //relFilterCriteria.setAnimation(R.anim.layout_animation_right_to_left);
                        relFilterCriteria.setVisibility(View.GONE);
                        defaultCallForAttendanceList(singalStudentId);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        dialog.show();
    }

    private void closeSubMenusFab() {
        slideDownAnimation();
        fabExpanded = false;
        mFabFilterAttendance.setImageResource(R.drawable.ic_filter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layoutFabSubMenu.setVisibility(View.GONE);
            }
        }, 400);
    }

    //Opens FAB submenus
    private void openSubMenusFab() {
        slideUpAnimation();
        layoutFabSubMenu.setVisibility(View.VISIBLE);
        fabExpanded = true;
        mFabFilterAttendance.setImageResource(R.drawable.ic_close);
    }

    private void slideUpAnimation() {
        layoutContainerFabs.startAnimation(animSlideUp);
    }

    private void slideDownAnimation() {
        layoutContainerFabs.startAnimation(animSlideDown);
    }


    private void openFilterByDateDialog() {
        if (!CommonUtil.isInternetAvailable(AttendanceListActivity.this)) {
            //finish();
            return;
        }

        mDialogFilterByDate = new Dialog(AttendanceListActivity.this);
        mDialogFilterByDate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogFilterByDate.setCancelable(true);
        mDialogFilterByDate.setContentView(R.layout.dialog_filter_by_date);
        mDialogFilterByDate.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mDialogFilterByDate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final LinearLayout linFilterDate;
        ImageView mIvClose = mDialogFilterByDate.findViewById(R.id.ivClose);
        TextView tvApply = mDialogFilterByDate.findViewById(R.id.tvApply);
        TextView tvCancel = mDialogFilterByDate.findViewById(R.id.tvCancel);

        tvSelectDate = mDialogFilterByDate.findViewById(R.id.tvSelectDate);
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog.OnDateSetListener filterDateListener;
        filterDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                tvSelectDate.setText("" + dayOfMonth + "-" + (month + 1) + "-" + year);
                //editor = sharedPreferences.edit();
                //editor.putString(KEY_FILTER_BY, "date");
                //editor.putString(KEY_FILTER_SELECTED_DATE, "" + dayOfMonth + "-" + (month + 1) + "-" + year);
                //editor.commit();
                relFilterCriteria.setVisibility(View.VISIBLE);
            }
        };

        //initializing date filter date picker dialog
        datePickerDialog_dateFilter = new DatePickerDialog(
                AttendanceListActivity.this, filterDateListener, year, month, day);
        datePickerDialog_dateFilter.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

        if (sharedPreferences != null) {
            editor = sharedPreferences.edit();
            if (sharedPreferences.contains(KEY_FILTER_BY)) {
                String strFilterBy = "" + sharedPreferences.getString(KEY_FILTER_BY, "");
                if (strFilterBy.equals("date")) {
                    String strFilterDate = "" + sharedPreferences.getString(KEY_FILTER_SELECTED_DATE, "");
                    tvSelectDate.setText("" + strFilterDate);
                }
            }
        }

        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOrShowDialog(mDialogFilterByDate);
            }
        });
        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtil.isNullString(tvSelectDate.getText().toString())) {
                    Toast.makeText(AttendanceListActivity.this, "Please select date", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!CommonUtil.isInternetAvailable(AttendanceListActivity.this)) {
                    //finish();
                    return;
                }

                strRecentApply = "date";

                // relFilterCriteria.startAnimation(animSlideRight);
                callApiToGetAttendaceList(
                        "" + tvSelectDate.getText().toString(),
                        "",
                        "",
                        "" + singalStudentId,
                        "",
                        "");

                hideOrShowDialog(mDialogFilterByDate);
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOrShowDialog(mDialogFilterByDate);
            }
        });

        tvSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog_dateFilter.show();
            }
        });


        hideOrShowDialog(mDialogFilterByDate);
    }

    private void openFilterByDateRangeDialog() {
        mDialogFilterByDateRange = new Dialog(AttendanceListActivity.this);
        mDialogFilterByDateRange.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogFilterByDateRange.setCancelable(true);
        mDialogFilterByDateRange.setContentView(R.layout.dialog_filter_by_range);
        mDialogFilterByDateRange.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mDialogFilterByDateRange.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView mIvClose = mDialogFilterByDateRange.findViewById(R.id.ivClose);
        TextView tvApply = mDialogFilterByDateRange.findViewById(R.id.tvApply);
        TextView tvCancel = mDialogFilterByDateRange.findViewById(R.id.tvCancel);

        tvStartDate = mDialogFilterByDateRange.findViewById(R.id.tvStartDate);
        tvEndDate = mDialogFilterByDateRange.findViewById(R.id.tvEndDate);
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog.OnDateSetListener filterStartListener;
        filterStartListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                tvStartDate.setText("" + dayOfMonth + "-" + (month + 1) + "-" + year);
                sharedPreferences.edit();
                editor.putString(KEY_FILTER_SELECTED_DATE, "" + dayOfMonth + "-" + (month + 1) + "-" + year);
                editor.commit();
            }
        };

        DatePickerDialog.OnDateSetListener filterEndListener;
        filterEndListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                tvEndDate.setText("" + dayOfMonth + "-" + (month + 1) + "-" + year);
                sharedPreferences.edit();
                editor.putString(KEY_FILTER_SELECTED_DATE, "" + dayOfMonth + "-" + (month + 1) + "-" + year);
                editor.commit();
            }
        };

        //initializing date filter date picker dialog
        datePickerDialog_startDate = new DatePickerDialog(
                AttendanceListActivity.this, filterStartListener, year, month, day);
        datePickerDialog_startDate.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

        //initializing date filter date picker dialog
        datePickerDialog_endDate = new DatePickerDialog(
                AttendanceListActivity.this, filterEndListener, year, month, day);
        datePickerDialog_endDate.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);


        if (sharedPreferences != null) {
            editor = sharedPreferences.edit();
            if (sharedPreferences.contains(KEY_FILTER_BY)) {
                String strFilterBy = "" + sharedPreferences.getString(KEY_FILTER_BY, "");
                if (strFilterBy.equals("date_range")) {

                    String strStartDate = "" + sharedPreferences.getString(KEY_FILTER_START_DATE, "");
                    tvStartDate.setText("" + strStartDate);

                    String strEndDate = "" + sharedPreferences.getString(KEY_FILTER_END_DATE, "");
                    tvEndDate.setText("" + strEndDate);
                }
            }
        }

        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOrShowDialog(mDialogFilterByDateRange);
            }
        });

        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog_startDate.show();
            }
        });
        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog_endDate.show();
            }
        });
        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CommonUtil.isNullString(tvStartDate.getText().toString())) {
                    Toast.makeText(AttendanceListActivity.this, "Please select start date", Toast.LENGTH_SHORT).show();
                    return;
                } else if (CommonUtil.isNullString(tvEndDate.getText().toString())) {
                    Toast.makeText(AttendanceListActivity.this, "Please select end date", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!CommonUtil.isInternetAvailable(AttendanceListActivity.this)) {
                    //finish();
                    return;
                }

                strRecentApply = "date_range";

                // relFilterCriteria.startAnimation(animSlideRight);
                callApiToGetAttendaceList(
                        "",
                        "",
                        "",
                        "" + singalStudentId,
                        "" + tvStartDate.getText().toString(),
                        "" + tvEndDate.getText().toString());

                hideOrShowDialog(mDialogFilterByDateRange);
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOrShowDialog(mDialogFilterByDateRange);
            }
        });

        hideOrShowDialog(mDialogFilterByDateRange);
    }

    private void removeFilterData() {
        //Remove
        if (sharedPreferences != null && sharedPreferences.contains(KEY_FILTER_BY)) {
            editor = sharedPreferences.edit();
            editor.remove(KEY_FILTER_BY);
            editor.remove(KEY_FILTER_SELECTED_DATE);
            editor.remove(KEY_FILTER_START_DATE);
            editor.remove(KEY_FILTER_END_DATE);
            editor.remove(KEY_FILTER_MONTH);
            editor.remove(KEY_FILTER_MONTH_YEAR);
            editor.remove(KEY_FILTER_YEAR);
            editor.remove(KEY_OUT_REQUIRED);
            editor.commit();
        }
    }

    private void openFilterByMonth() {
        mDialogFilterByMonth = new Dialog(AttendanceListActivity.this);
        mDialogFilterByMonth.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogFilterByMonth.setCancelable(true);
        mDialogFilterByMonth.setContentView(R.layout.dialog_filter_by_month);
        mDialogFilterByMonth.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mDialogFilterByMonth.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView mIvClose = mDialogFilterByMonth.findViewById(R.id.ivClose);
        TextView tvApply = mDialogFilterByMonth.findViewById(R.id.tvApply);
        TextView tvCancel = mDialogFilterByMonth.findViewById(R.id.tvCancel);
        spinnerMonth = mDialogFilterByMonth.findViewById(R.id.spinnerMonth);
        spinnerYearWithMonth = mDialogFilterByMonth.findViewById(R.id.spinnerYearWithMonth);

        setSpinnerForMonthAndYears(spinnerMonth, spinnerYearWithMonth, null);

        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOrShowDialog(mDialogFilterByMonth);
            }
        });
        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonUtil.isInternetAvailable(AttendanceListActivity.this)) {
                    //finish();
                    return;
                }
                strRecentApply = "month_year";
                relFilterCriteria.setVisibility(View.VISIBLE);
                callApiToGetAttendaceList(
                        "",
                        "0" + (spinnerMonth.getSelectedItemPosition() + 1),
                        "" + spinnerYearWithMonth.getSelectedItem(),
                        "" + singalStudentId,
                        "",
                        "");

                hideOrShowDialog(mDialogFilterByMonth);
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOrShowDialog(mDialogFilterByMonth);
            }
        });

        hideOrShowDialog(mDialogFilterByMonth);
    }

    private void openFilterByYear() {
        mDialogFilterByYear = new Dialog(AttendanceListActivity.this);
        mDialogFilterByYear.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogFilterByYear.setCancelable(true);
        mDialogFilterByYear.setContentView(R.layout.dialog_filter_by_year);
        mDialogFilterByYear.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mDialogFilterByYear.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView mIvClose = mDialogFilterByYear.findViewById(R.id.ivClose);
        spinnerYear = (Spinner) mDialogFilterByYear.findViewById(R.id.spinnerYear);
        TextView tvApply = mDialogFilterByYear.findViewById(R.id.tvApply);
        TextView tvCancel = mDialogFilterByYear.findViewById(R.id.tvCancel);

        setSpinnerForMonthAndYears(null, null, spinnerYear);

        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOrShowDialog(mDialogFilterByYear);
            }
        });
        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!CommonUtil.isInternetAvailable(AttendanceListActivity.this)) {
                    //finish();
                    return;
                }

                // relFilterCriteria.startAnimation(animSlideRight);
                // relFilterCriteria.startAnimation(animSlideRight);
                strRecentApply = "year";
                callApiToGetAttendaceList(
                        "",
                        "",
                        "" + spinnerYear.getSelectedItem().toString(),
                        "" + singalStudentId,
                        "",
                        "");

                hideOrShowDialog(mDialogFilterByYear);
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOrShowDialog(mDialogFilterByYear);
            }
        });


        hideOrShowDialog(mDialogFilterByYear);
    }

    private void checkForTheStoredPrefrenceValuesAndDoChanges(String singalStudentId) {
        if (sharedPreferences != null && sharedPreferences.contains(KEY_FILTER_BY)) {
            String strFilterBy = sharedPreferences.getString(KEY_FILTER_BY, "");
            if (strFilterBy.equalsIgnoreCase("date")) {
                relFilterCriteria.setVisibility(View.VISIBLE);
                //  relFilterCriteria.startAnimation(animSlideRight);
                tvFilterAppliedValue.setText("Filter For: " + sharedPreferences.getString(KEY_FILTER_SELECTED_DATE, ""));
                tvFilterAppliedValue.setTextColor(Color.BLACK);
                if (sharedPreferences.contains(KEY_FILTER_SELECTED_DATE)) {
                    callApiToGetAttendaceList(
                            "" + sharedPreferences.getString(KEY_FILTER_SELECTED_DATE, ""),
                            "", "", "" + singalStudentId, "", "");
                }

            } else if (strFilterBy.equalsIgnoreCase("date_range")) {
                relFilterCriteria.setVisibility(View.VISIBLE);
                //   relFilterCriteria.startAnimation(animSlideRight);
                tvFilterAppliedValue.setText("Filter Between: " + sharedPreferences.getString(KEY_FILTER_START_DATE, "") + " & " + "" + sharedPreferences.getString(KEY_FILTER_END_DATE, ""));
                tvFilterAppliedValue.setTextColor(Color.BLACK);
                callApiToGetAttendaceList("",
                        "", "", "" + singalStudentId, "" + sharedPreferences.getString(KEY_FILTER_START_DATE, ""), "" + sharedPreferences.getString(KEY_FILTER_END_DATE, ""));
            } else if (strFilterBy.equalsIgnoreCase("month_year")) {
                relFilterCriteria.setVisibility(View.VISIBLE);
                //  relFilterCriteria.startAnimation(animSlideRight);
                tvFilterAppliedValue.setText("Filter for Month: " + sharedPreferences.getString(KEY_FILTER_MONTH, "") + ", " + sharedPreferences.getString(KEY_FILTER_MONTH_YEAR, ""));
                tvFilterAppliedValue.setTextColor(Color.BLACK);

                if (mALAttendaceList != null && mALAttendaceList.size() > 0) {
                    tvNoData.setVisibility(View.GONE);
                    mRvAttendanceList.setVisibility(View.VISIBLE);
                } else {
                    tvNoData.setVisibility(View.VISIBLE);
                    mRvAttendanceList.setVisibility(View.GONE);
                }

                callApiToGetAttendaceList("",
                        "" + sharedPreferences.getString(KEY_FILTER_MONTH, ""),
                        "" + sharedPreferences.getString(KEY_FILTER_MONTH_YEAR, ""),
                        "" + singalStudentId, "", "");
            } else if (strFilterBy.equalsIgnoreCase("year")) {
                if (sharedPreferences != null && sharedPreferences.contains(KEY_FILTER_YEAR)) {
                    relFilterCriteria.setVisibility(View.VISIBLE);
                    //    relFilterCriteria.startAnimation(animSlideRight);
                    tvFilterAppliedValue.setText("Filter for Year: " + sharedPreferences.getString(KEY_FILTER_YEAR, ""));
                    tvFilterAppliedValue.setTextColor(Color.BLACK);
                }

                if (mALAttendaceList != null && mALAttendaceList.size() > 0) {
                    tvNoData.setVisibility(View.GONE);
                    mRvAttendanceList.setVisibility(View.VISIBLE);
                } else {
                    tvNoData.setVisibility(View.VISIBLE);
                    mRvAttendanceList.setVisibility(View.GONE);
                }

                callApiToGetAttendaceList("",
                        "",
                        "" + sharedPreferences.getString(KEY_FILTER_YEAR, ""),
                        "" + singalStudentId, "", "");
            } else if (strFilterBy.equalsIgnoreCase("out_required")) {
                relFilterCriteria.setVisibility(View.VISIBLE);
                //    relFilterCriteria.startAnimation(animSlideRight);
                tvFilterAppliedValue.setText("Filter for: OUT REQUIRED");
                tvFilterAppliedValue.setTextColor(Color.RED);
                strRecentApply = "out_required";
                callApiToGetAttendaceList("",
                        "", "", "" + singalStudentId, "", "");
            }
        }
    }

    private void setSpinnerForMonthAndYears(final Spinner spinnerMonth, final Spinner spinnerYearWithMonth, final Spinner spinnerYear) {
        if (spinnerMonth != null) {
            ArrayList<String> months = new ArrayList<>();
            months.add("JAN");
            months.add("FEB");
            months.add("MAR");
            months.add("APR");
            months.add("MAY");
            months.add("JUN");
            months.add("JUL");
            months.add("AUG");
            months.add("SEP");
            months.add("OCT");
            months.add("NOV");
            months.add("DEC");
            SingleRawAdapter adapter = new SingleRawAdapter(AttendanceListActivity.this, months);
            spinnerMonth.setAdapter(adapter);
            spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (sharedPreferences != null) {
                        sharedPreferences.edit();
                        if (spinnerMonth.getSelectedItemPosition() > 9) {
                            editor.putString(KEY_FILTER_MONTH, "" + spinnerMonth.getSelectedItemPosition());
                            editor.commit();
                        } else {
                            editor.putString(KEY_FILTER_MONTH, "0" + spinnerMonth.getSelectedItemPosition());
                            editor.commit();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }


        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i >= 1990; i--) {
            years.add(Integer.toString(i));
        }
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.view_single_text, years);
        SingleRawAdapter adapterYear = new SingleRawAdapter(AttendanceListActivity.this, years);
        if (spinnerYearWithMonth != null) {
            spinnerYearWithMonth.setAdapter(adapterYear);
            spinnerYearWithMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (sharedPreferences != null) {
                        sharedPreferences.edit();
                        editor.putString(KEY_FILTER_MONTH_YEAR, "" + spinnerYearWithMonth.getSelectedItemPosition());
                        editor.commit();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        if (spinnerYear != null) {
            spinnerYear.setAdapter(adapterYear);
            spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (sharedPreferences != null) {
                        sharedPreferences.edit();
                        editor.putString(KEY_FILTER_YEAR, "" + spinnerYear.getSelectedItemPosition());
                        editor.commit();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void hideOrShowDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        } else if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }
}
