package com.globalitians.employees.inquiry.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.branches.model.ModelBranchList;
import com.globalitians.employees.courses.adapters.CourseListAdapter;
import com.globalitians.employees.courses.model.ModelCourseList;
import com.globalitians.employees.inquiry.model.ModelInquiryDetailsResponse;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import static com.globalitians.employees.utility.CommonUtil.getSharedPrefrencesInstance;
import static com.globalitians.employees.utility.CommonUtil.playSoundForAttendance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_COURSE_LIST;
import static com.globalitians.employees.utility.Constants.CODE_INQUIRY_DETAILS;
import static com.globalitians.employees.utility.Constants.CODE_POST_INQUIRIES;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.KEY_INQUIRY_DATE;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_INQUIRY_ID;
import static com.globalitians.employees.utility.Constants.WS_COURSE_LIST;

public class AddInquiryActivity extends AppCompatActivity implements View.OnClickListener, OkHttpInterface, DatePickerDialog.OnDateSetListener, CourseListAdapter.OnCourseListListener {
    private EditText textInputFirstname;
    private EditText textInputLastname;
    private EditText textInputMobileno;
    private Spinner spinnerReferenceBy;
    private EditText textInputFeedback;
    private EditText textInputOtherCourse;
    private TextView tvCourses;
    private TextView tvSubmitInquiry;
    private ImageView iv_date_picker;
    private TextView tv_inquiry_date;

    private DatePickerDialog datePickerDialog = null;
    private Dialog dialogCourses = null;

    private ArrayList<ModelCourseList.Course> mArrListCourses = null;
    private ArrayList<ModelCourseList.Course> mArrListCoursesSelected = null;
    private ArrayList<ModelBranchList.Branch> mArrListBranches = null;

    private CourseListAdapter mAdapterCourseList = null;

    private String strIntentInquiryId = "";
    private String strIntentInquiryDate = "";
    private String strEditInquirySlug = "";
    private String strSelectedCourses = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(AddInquiryActivity.this);
        setContentView(R.layout.activity_add_inquiry);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViews();

        callApiToLoadCourseList();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getStudentIdFromIntent();
            }
        }, 700);
    }

    private void getStudentIdFromIntent() {
        Intent intentInquiryId = getIntent();
        if (intentInquiryId != null) {
            strIntentInquiryId = "" + intentInquiryId.getStringExtra(KEY_INTENT_INQUIRY_ID);
            strIntentInquiryDate = "" + intentInquiryId.getStringExtra(KEY_INQUIRY_DATE);
            if (!CommonUtil.isNullString(strIntentInquiryId)) {
                getSupportActionBar().setTitle("Edit Inquiry Detail");  // provide compatibility to all the versions
                tvSubmitInquiry.setText("Save");
                callApiToLoadInquiryDetails();
            }
        }
    }

    private void findViews() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(
                AddInquiryActivity.this, AddInquiryActivity.this, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
        textInputFirstname = findViewById(R.id.edt_firstname);
        textInputLastname = findViewById(R.id.edt_lastname);
        textInputMobileno = findViewById(R.id.edt_mobileno);
        spinnerReferenceBy = findViewById(R.id.spinner_referenceBy);
        textInputFeedback = findViewById(R.id.edt_feedback);
        textInputOtherCourse = findViewById(R.id.edt_other_course);

        tvCourses = findViewById(R.id.edtCourses);

        tvSubmitInquiry = findViewById(R.id.tv_submit_inquiry);
        tvSubmitInquiry.setOnClickListener(this);

        iv_date_picker = findViewById(R.id.iv_date_picker);
        iv_date_picker.setOnClickListener(this);
        tv_inquiry_date = findViewById(R.id.tv_inquiry_date);

        mArrListCourses = new ArrayList<>();
        mArrListCoursesSelected = new ArrayList<>();
        mArrListBranches = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_date_picker:
                datePickerDialog.show();
                break;
            case R.id.tv_submit_inquiry:
                validateAndCallApiToSubmitInquiry();
                break;
        }
    }

    private void validateAndCallApiToSubmitInquiry() {

        if (!CommonUtil.isInternetAvailable(AddInquiryActivity.this)) {
            playSoundForAttendance("No Internet Connection. Please Check your internet connection.", AddInquiryActivity.this);
            return;
        }
        if (CommonUtil.isNullString(textInputFirstname.getText().toString().trim())) {
            textInputFirstname.requestFocus();
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_firstname), Toast.LENGTH_SHORT).show();
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_firstname), AddInquiryActivity.this);
            return;
        } else if (textInputFirstname.getText().toString().trim().length() < 2) {
            textInputFirstname.requestFocus();
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_length_firstname), AddInquiryActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_length_firstname), Toast.LENGTH_SHORT).show();
            return;
        } else if (CommonUtil.isNullString(textInputLastname.getText().toString().trim())) {
            textInputLastname.requestFocus();
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_lastname), AddInquiryActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_lastname), Toast.LENGTH_SHORT).show();
            return;
        } else if (textInputLastname.getText().toString().trim().length() < 2) {
            textInputLastname.requestFocus();
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_length_lastname), AddInquiryActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_length_lastname), Toast.LENGTH_SHORT).show();
            return;
        } else if (CommonUtil.isNullString(textInputMobileno.getText().toString().trim())) {
            textInputMobileno.requestFocus();
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_mobileNO), AddInquiryActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_mobileNO), Toast.LENGTH_SHORT).show();
            return;
        } else if (textInputMobileno.getText().toString().trim().length() < 10) {
            textInputMobileno.requestFocus();
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_length_mobile), AddInquiryActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_length_mobile), Toast.LENGTH_SHORT).show();
            return;
        } /*else if (Integer.parseInt(textInputMobileno.getText().toString().trim())==0) {
            textInputMobileno.setFocusable(true);
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_mobile_value), AddInquiryActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_mobile_value), Toast.LENGTH_SHORT).show();
            return;
        } */else if (!tv_inquiry_date.getText().toString().contains("-")) {
            playSoundForAttendance("" + getResources().getString(R.string.toast_select_inquiry_date), AddInquiryActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_select_inquiry_date), Toast.LENGTH_SHORT).show();
            return;
        } else {
            for (int i = 0; i < mArrListCoursesSelected.size(); i++) {
                if (CommonUtil.isNullString(strSelectedCourses)) {
                    strSelectedCourses = "" + mArrListCoursesSelected.get(i).getId();
                } else {
                    strSelectedCourses = strSelectedCourses + "," + mArrListCoursesSelected.get(i).getId();
                }
            }


            if (CommonUtil.isNullString(strSelectedCourses)) {
                Toast.makeText(this, "Please select course", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.e("<<<<<", "" + strSelectedCourses);

            if (CommonUtil.isNullString(strIntentInquiryId)) {
                new OkHttpRequest(AddInquiryActivity.this,
                        OkHttpRequest.Method.POST,
                        Constants.WS_POST_INQUIRIES,
                        RequestParam.submitInquiry(
                                "" + textInputFirstname.getText().toString().trim(),
                                "" + textInputLastname.getText().toString().trim(),
                                "" + textInputFeedback.getText().toString().trim(),
                                "" + spinnerReferenceBy.getSelectedItem().toString(),
                                "" + textInputMobileno.getText().toString().trim(),
                                "" + tv_inquiry_date.getText().toString(),
                                "" + strSelectedCourses, "",
                                ""+getSharedPrefrencesInstance(AddInquiryActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,"")),
                        RequestParam.getNull(),
                        CODE_POST_INQUIRIES,
                        true, this);
            } else {
                //Edit .. Update inquiry
                new OkHttpRequest(AddInquiryActivity.this,
                        OkHttpRequest.Method.POST,
                        Constants.WS_POST_INQUIRIES,
                        RequestParam.submitInquiry(
                                "" + textInputFirstname.getText().toString().trim(),
                                "" + textInputLastname.getText().toString().trim(),
                                "" + textInputFeedback.getText().toString().trim(),
                                "" + spinnerReferenceBy.getSelectedItem().toString(),
                                "" + textInputMobileno.getText().toString().trim(),
                                "" + tv_inquiry_date.getText().toString(),
                                "" + strSelectedCourses,
                                "" + strEditInquirySlug,
                                ""+getSharedPrefrencesInstance(AddInquiryActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,"")
                        ),
                        RequestParam.getNull(),
                        CODE_POST_INQUIRIES,
                        true, this);
            }
        }
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
            case CODE_COURSE_LIST:
                final Gson courseListGson = new Gson();
                try {
                    ModelCourseList modelCourseList = courseListGson
                            .fromJson(response, ModelCourseList.class);

                    if (modelCourseList.getStatus().equals(Constants.SUCCESS_CODE)) {
                        mArrListCourses = modelCourseList.getCourses();

                        if (mArrListCourses != null && mArrListCourses.size() > 0) {
                            setCoursesListAdapter();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CODE_POST_INQUIRIES:
                try {
                    JSONObject jsonAddInquiry = new JSONObject(response);
                    if (jsonAddInquiry != null && jsonAddInquiry.has("status")) {
                        if (jsonAddInquiry.getString("status").equalsIgnoreCase("success")) {
                            Toast.makeText(this, "" + jsonAddInquiry.getString("message"), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CODE_INQUIRY_DETAILS:
                Log.e("REsponse >>", "" + response);
                final Gson gson = new Gson();
                try {
                    ModelInquiryDetailsResponse modelInquiryDetails = gson
                            .fromJson(response, ModelInquiryDetailsResponse.class);

                    Log.e(">> inquiry date ", "" + modelInquiryDetails.getInquiryDetail().getInquiry_date());
                    if (modelInquiryDetails.getStatus().equals(Constants.SUCCESS_CODE)) {
                        setInquiryDetails(modelInquiryDetails);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setInquiryDetails(ModelInquiryDetailsResponse modelInquiryDetails) {
        tvCourses.setText("");
        strSelectedCourses = "";
        for (int i = 0; i < modelInquiryDetails.getInquiryDetail().getCourses().size(); i++) {
            for (int j = 0; j < mArrListCourses.size(); j++) {
                if (modelInquiryDetails.getInquiryDetail().getCourses().get(i).getId().equals(mArrListCourses.get(j).getId())) {
                    mArrListCourses.get(j).setSelected(true);
                    //set Selected Values...
                    if (tvCourses.getText().toString().length() > 0) {
                        tvCourses.append(", " + mArrListCourses.get(j).getName());
                        strSelectedCourses = strSelectedCourses + "," + mArrListCourses.get(j).getId();
                    } else {
                        tvCourses.setText(mArrListCourses.get(j).getName());
                        strSelectedCourses = "" + mArrListCourses.get(j).getId();
                    }
                }
            }

        }

        //textInputB.setText(""+modelInquiryDetails.getInquiryDetail().getBranch());
        textInputFirstname.setText("" + modelInquiryDetails.getInquiryDetail().getFname());
        textInputLastname.setText("" + modelInquiryDetails.getInquiryDetail().getLname());
        textInputFeedback.setText("" + modelInquiryDetails.getInquiryDetail().getFeedback());
        textInputMobileno.setText("" + modelInquiryDetails.getInquiryDetail().getMobileno());
        tv_inquiry_date.setText(strIntentInquiryDate);

        strEditInquirySlug = modelInquiryDetails.getInquiryDetail().getSlug();
    }

    private void setBranchListAdapter() {
        ArrayList<String> marrString = new ArrayList<>();
        for (int i = 0; i < mArrListBranches.size(); i++) {
            marrString.add(mArrListBranches.get(i).getName());
        }
    }

    private void setCoursesListAdapter() {

        dialogCourses = new Dialog(AddInquiryActivity.this);
        dialogCourses.setCancelable(true);
        dialogCourses.setContentView(R.layout.dialog_courses);

        ListView lvCourses = dialogCourses.findViewById(R.id.lv_courses);
        TextView tvSave = dialogCourses.findViewById(R.id.tvSave);
        TextView tvCancel = dialogCourses.findViewById(R.id.tvCancel);

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvCourses.setText("");
                strSelectedCourses = "";

                for (int i = 0; i < mArrListCourses.size(); i++) {
                    if (mArrListCourses.get(i).isSelected()) {
                        mArrListCoursesSelected.add(mArrListCourses.get(i));
                    }
                }

                for (int i = 0; i < mArrListCoursesSelected.size(); i++) {
                    if (tvCourses.getText().toString().length() > 0) {
                        tvCourses.append(", " + mArrListCoursesSelected.get(i).getName());
                    } else {
                        tvCourses.setText(mArrListCoursesSelected.get(i).getName());
                    }
                }
                dialogCourses.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCourses.dismiss();
            }
        });

        mAdapterCourseList = new CourseListAdapter(AddInquiryActivity.this, mArrListCourses, this, "add_inquiry");
        lvCourses.setAdapter(mAdapterCourseList);

        tvCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mArrListCoursesSelected != null && mArrListCoursesSelected.size() > 0) {
                    mArrListCoursesSelected.clear();
                }
                dialogCourses.show();
            }
        });

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

    private void callApiToLoadCourseList() {
        if (!CommonUtil.isInternetAvailable(AddInquiryActivity.this)) {
            return;
        }

        new OkHttpRequest(AddInquiryActivity.this,
                OkHttpRequest.Method.GET,
                WS_COURSE_LIST,
                RequestParam.loadGetRequestsData(),
                RequestParam.getNull(),
                CODE_COURSE_LIST,
                true, this);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        tv_inquiry_date.setText("" + dayOfMonth + "-" + (month + 1) + "-" + year);
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
    public void onCourseRawClick(int position) {
        if (mArrListCourses.get(position).getName().equalsIgnoreCase("--Other--")) {
            dialogCourses.dismiss();
            textInputOtherCourse.setVisibility(View.VISIBLE);
        } else {
            textInputOtherCourse.setVisibility(View.GONE);
        }

        if (mArrListCourses.get(position).isSelected()) {
            mArrListCourses.get(position).setSelected(false);
        } else {
            mArrListCourses.get(position).setSelected(true);
        }
    }

    @Override
    public void onDeleteCourse(int position) {

    }

    private void callApiToLoadInquiryDetails() {
        if (!CommonUtil.isNullString(strIntentInquiryId)) {
            if (!CommonUtil.isInternetAvailable(AddInquiryActivity.this)) {
                return;
            }

            new OkHttpRequest(AddInquiryActivity.this,
                    OkHttpRequest.Method.POST,
                    Constants.WS_INQUIRY_DETAILS,
                    RequestParam.inquiryDetails(strIntentInquiryId,
                            ""+getSharedPrefrencesInstance(AddInquiryActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,"")),
                    RequestParam.getNull(),
                    CODE_INQUIRY_DETAILS,
                    true, this);
        }
    }
}
