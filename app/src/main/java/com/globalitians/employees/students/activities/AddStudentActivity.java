package com.globalitians.employees.students.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.courses.activities.CourseListActivity;
import com.globalitians.employees.courses.adapters.CourseListWhileAddingOrEditingStudentAdapter;
import com.globalitians.employees.courses.model.AddNewCourseModelWhileAddingNewStudent;
import com.globalitians.employees.courses.model.ModelCourseDurationList;
import com.globalitians.employees.customviews.CustomButton;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.students.adapters.InstallmentListAdapter;
import com.globalitians.employees.students.models.InstallmentModel;
import com.globalitians.employees.students.models.ModelPartners;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.LogUtil;
import com.globalitians.employees.utility.PermissionManager;
import com.globalitians.employees.utility.Permissions;
import com.globalitians.employees.utility.PhotoPicker;
import com.globalitians.employees.utility.PhotoPickerCallback;
import com.globalitians.employees.utility.ResourceUtils;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.globalitians.employees.utility.CommonUtil.getSharedPrefrencesInstance;
import static com.globalitians.employees.utility.CommonUtil.playSoundForAttendance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.ATTACHMENT_IMAGE;
import static com.globalitians.employees.utility.Constants.ATTACHMENT_PDF;
import static com.globalitians.employees.utility.Constants.CODE_ADD_NEW_STUDENT;
import static com.globalitians.employees.utility.Constants.CODE_COURSE_LIST;
import static com.globalitians.employees.utility.Constants.CODE_COURSE_SELECTION_FOR_ADD_STUDENT;
import static com.globalitians.employees.utility.Constants.CODE_GET_COURSE_DURATIONS;
import static com.globalitians.employees.utility.Constants.CODE_PARTNERS_LIST;
import static com.globalitians.employees.utility.Constants.KEY_COURSE_SELECTION_TAG;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_COURSE_ID;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_COURSE_IMAGE;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_COURSE_NAME;

public class AddStudentActivity extends AppCompatActivity implements OkHttpInterface, View.OnClickListener, CourseListWhileAddingOrEditingStudentAdapter.OnCourseListListener, InstallmentListAdapter.InstallmentDateListener {
    private LinearLayout linAddStudent;
    private CircularImageView ivStudentImagePick;
    private EditText edtFirstname;
    private EditText edtLastname;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtConfPassword;
    private EditText edtMobileno;
    private EditText edtBirthDate;
    //private Spinner spinnerBranch;
    private EditText edtParentname;
    private EditText edtParentMobileno;
    //private EditText edtReferenceBy;
    private Spinner spinnerReferenceBy;
    private EditText edtJoiningDate;
    private EditText edtInstallmentDate;
    private EditText edtAddress;
    private Spinner spinnerCourseStatus;
    private Spinner spinnerCertificate;
    private Spinner spinnerBookMaterial;
    private Spinner spinnerBag;
    private LinearLayout linCourseDetails;
    private TextView tvCourses;
    private EditText edtCourseList;

    private TextView tvCourseFees;
    private LinearLayout llFeesAmount;
    private TextView tvDecreaseFees;
    private EditText edtCoursefees;
    private TextView tvIncreaseFees;
    private TextView tvCourseDuration;
    private TextView tvAddNewStudent;
    private TextView tvAddNewStuentCourse;
    private TextView tvViewStudentCoursesAdded;
    private EditText edtCourseDuration;
    private ModelCourseDurationList modelCourseDurationList = null;
    private static String ATTACHMENT_TYPE = ""; // IMAGE
    private File mFileSelectedStudentImage = null;

    private CourseListWhileAddingOrEditingStudentAdapter mAdapterCourseList = null;

    private ArrayList<AddNewCourseModelWhileAddingNewStudent> mArrListStudentCourseDetails = new ArrayList<>();

    private String strSelectedPartnerId="";
    private String strSelectedPartnerName="";
    private String strSelectedCourseDuration = "";
    private String strSelectedCourseId = "";
    private String strSelectedCourseName = "";
    private String strSelectedCourseImage = "";
    private String strSelectedStudentImage = "";

    private boolean isEditStudent = false;
    private DatePickerDialog mDatePickerDiaogBirthDate = null;
    private DatePickerDialog mDatePickerDialogJoinedDate = null;
    private DatePickerDialog mDatePickerDialogInstallmentDate = null;
    private Dialog dialogTotalCourseList = null;
    private Dialog dialogCourseDurations = null;
    private AddNewCourseModelWhileAddingNewStudent modelCourseData;
    private String strStudentIdToEdit = "";

    private Spinner mSpinnerStandard;
    private Spinner mSpinnerStream;
    private Spinner mSpinnerSubjects;
    private Spinner mSpinnerpartners;
    private CustomTextView mTvStream;

    private ArrayList<String> aListSchoolSubjects = new ArrayList<>();
    private ArrayList<String> aList1112Science = new ArrayList<>();
    private ArrayList<String> aList1112Commerence = new ArrayList<>();
    private ArrayList<String> aList1112Arts = new ArrayList<>();

    private ArrayList<InstallmentModel> alInstallmentDates = new ArrayList<>();
    private InstallmentListAdapter adapterInstallmentDate;

    private CustomTextView tvTapTpAdd;

    private ModelPartners modelPartners;
    private ArrayList<String> mAlpartners;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(AddStudentActivity.this);
        setContentView(R.layout.activity_add_student);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#e7d4ff"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">Add New Student</font>"));

        aListSchoolSubjects.add("English");
        aListSchoolSubjects.add("Mathematics");
        aListSchoolSubjects.add("Science");
        aListSchoolSubjects.add("Gujarati");
        aListSchoolSubjects.add("Social Science");

        aList1112Science.add("Physics");
        aList1112Science.add("Chemistry");
        aList1112Science.add("Biology");
        aList1112Science.add("English");
        aList1112Science.add("Mathematics");

        aList1112Arts.add("History");
        aList1112Arts.add("Political Science");
        aList1112Arts.add("Sociology");
        aList1112Arts.add("Economics");
        aList1112Arts.add("Geography");
        aList1112Arts.add("Psychology");

        aList1112Commerence.add("Accountancy");
        aList1112Commerence.add("Business Studies");
        aList1112Commerence.add("Statistics");
        aList1112Commerence.add("Economics");
        aList1112Commerence.add("English");
        aList1112Commerence.add("Mathematics");

        initialize();
        findViews();
        setListners();
        initializeDatePickers();
        callApiToLoadPartnersList();
        CommonUtil.hideSoftKeyboard(AddStudentActivity.this);

        /*
         * In case of Edit Student below method will be called..
         * Checking the intent if it is not null means we have to
         * perform edit.
         * Intent coming from StudentDetailsActivity - Profile.
         * */
        try {
            if (getIntent() != null &&
                    StudentDetailsActivity.modelStudent != null &&
                    getIntent().hasExtra("modelStudent")) {
                isEditStudent = true;
                setTitle("Edit Student");
                tvAddNewStudent.setText("Save");
                CommonUtil.setCircularImageToGlide(AddStudentActivity.this,
                        ivStudentImagePick,
                        "" + StudentDetailsActivity.modelStudent.getStudentDetail().getImage());

                //Courses
                mArrListStudentCourseDetails = new ArrayList<>();
                if (StudentDetailsActivity.modelStudent.getStudentDetail().getCourses() != null &&
                        StudentDetailsActivity.modelStudent.getStudentDetail().getCourses().size() > 0) {
                    for (int i = 0; i < StudentDetailsActivity.modelStudent.getStudentDetail().getCourses().size(); i++) {
                        AddNewCourseModelWhileAddingNewStudent modelCourseData = new AddNewCourseModelWhileAddingNewStudent();
                        modelCourseData.setStrCourseStatus(StudentDetailsActivity.modelStudent.getStudentDetail().getCourses().get(i).getCourseStatus());
                        modelCourseData.setStrCourseImage(StudentDetailsActivity.modelStudent.getStudentDetail().getCourses().get(i).getImage());
                        modelCourseData.setStrCourseId(StudentDetailsActivity.modelStudent.getStudentDetail().getCourses().get(i).getId());
                        modelCourseData.setStrFees(StudentDetailsActivity.modelStudent.getStudentDetail().getCourses().get(i).getFees());
                        modelCourseData.setStrDuration(StudentDetailsActivity.modelStudent.getStudentDetail().getCourses().get(i).getDuration());
                        modelCourseData.setStrCertificate(StudentDetailsActivity.modelStudent.getStudentDetail().getCourses().get(i).getCertificate());
                        modelCourseData.setStrBookMaterial(StudentDetailsActivity.modelStudent.getStudentDetail().getCourses().get(i).getBookMaterial());
                        modelCourseData.setStrBag(StudentDetailsActivity.modelStudent.getStudentDetail().getCourses().get(i).getBag());
                        modelCourseData.setStrCourseName(StudentDetailsActivity.modelStudent.getStudentDetail().getCourses().get(i).getName());
                        mArrListStudentCourseDetails.add(modelCourseData);
                    }
                }


                //We have to set the UI values here..
                strStudentIdToEdit = "" + StudentDetailsActivity.modelStudent.getStudentDetail().getId();
                edtFirstname.setText("" + StudentDetailsActivity.modelStudent.getStudentDetail().getFname());
                edtLastname.setText("" + StudentDetailsActivity.modelStudent.getStudentDetail().getLname());
                edtEmail.setText("" + StudentDetailsActivity.modelStudent.getStudentDetail().getEmail());
                edtPassword.setText("12345");
                edtConfPassword.setText("12345");
                edtPassword.setEnabled(false);
                edtConfPassword.setEnabled(false);
                edtMobileno.setText("" + StudentDetailsActivity.modelStudent.getStudentDetail().getMobileno());
                edtParentname.setText("" + StudentDetailsActivity.modelStudent.getStudentDetail().getParentname());
                edtParentMobileno.setText("" + StudentDetailsActivity.modelStudent.getStudentDetail().getParentmobileno());
                edtBirthDate.setText("" + StudentDetailsActivity.modelStudent.getStudentDetail().getDob());
                edtJoiningDate.setText("" + StudentDetailsActivity.modelStudent.getStudentDetail().getJoiningDate());
                edtInstallmentDate.setText("" + StudentDetailsActivity.modelStudent.getStudentDetail().getInstallmentDate());
                edtAddress.setText("" + StudentDetailsActivity.modelStudent.getStudentDetail().getAddress());
                String[] arrReferenceBy = getResources().getStringArray(R.array.array_referenceBy);
                for (int i = 0; i < arrReferenceBy.length; i++) {
                    if (arrReferenceBy[i].equalsIgnoreCase("" + StudentDetailsActivity.modelStudent.getStudentDetail().getReference())) {
                        spinnerReferenceBy.setSelection(i);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Exception Occured", Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }
    }

    private void callApiToLoadPartnersList() {
        if(!CommonUtil.isInternetAvailable(AddStudentActivity.this)){
            return;
        }

        new OkHttpRequest(AddStudentActivity.this,
                OkHttpRequest.Method.GET,
                Constants.WS_PARTNER_LIST,
                RequestParam.loadGetRequestsData(),
                RequestParam.getNull(),
                CODE_PARTNERS_LIST,
                false,this);
    }

    private void initialize() {
        if (mArrListStudentCourseDetails == null) {
            mArrListStudentCourseDetails = new ArrayList<>();
        } else {
            mArrListStudentCourseDetails.clear();
        }
        mAlpartners = new ArrayList<>();
    }

    private void initializeDatePickers() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog.OnDateSetListener birthdateListener;
        birthdateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                edtBirthDate.setText("" + dayOfMonth + "-" + (month + 1) + "-" + year);
            }
        };

        DatePickerDialog.OnDateSetListener joinedDateListener;
        joinedDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                edtJoiningDate.setText("" + dayOfMonth + "-" + (month + 1) + "-" + year);
            }
        };

        DatePickerDialog.OnDateSetListener installmentDateListener;
        installmentDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                edtInstallmentDate.setText("" + dayOfMonth + "-" + (month + 1) + "-" + year);
            }
        };

        //initializing date filter date picker dialog
        mDatePickerDiaogBirthDate = new DatePickerDialog(
                AddStudentActivity.this, birthdateListener, year, month, day);
        mDatePickerDiaogBirthDate.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

        //initializing date filter date picker dialog
        mDatePickerDialogJoinedDate = new DatePickerDialog(
                AddStudentActivity.this, joinedDateListener, year, month, day);
        //mDatePickerDialogJoinedDate.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        //initializing date filter date picker dialog
        mDatePickerDialogInstallmentDate = new DatePickerDialog(
                AddStudentActivity.this, installmentDateListener, year, month, day);
        //mDatePickerDialogInstallmentDate.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
    }

    private void setListners() {
        tvAddNewStudent.setOnClickListener(this);
        ivStudentImagePick.setOnClickListener(this);
        tvAddNewStuentCourse.setOnClickListener(this);
        tvViewStudentCoursesAdded.setOnClickListener(this);
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

    private void findViews() {
        linAddStudent = (LinearLayout) findViewById(R.id.linAddStudent);
        ivStudentImagePick = (CircularImageView) findViewById(R.id.iv_student_image_pick);
        edtFirstname = (EditText) findViewById(R.id.edt_firstname);
        edtLastname = (EditText) findViewById(R.id.edt_lastname);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        edtConfPassword = (EditText) findViewById(R.id.edt_conf_password);
        edtMobileno = (EditText) findViewById(R.id.edt_mobileno);
        edtBirthDate = (EditText) findViewById(R.id.edt_birthDate);
        //spinnerBranch = (Spinner) findViewById(R.id.spinner_branch);
        edtParentname = (EditText) findViewById(R.id.edt_parentname);
        edtParentMobileno = (EditText) findViewById(R.id.edt_parent_mobileno);
        spinnerReferenceBy = (Spinner) findViewById(R.id.spinner_referenceBy);
        mSpinnerpartners = findViewById(R.id.spinner_partner);
        edtJoiningDate = (EditText) findViewById(R.id.edt_joiningDate);
        edtInstallmentDate = (EditText) findViewById(R.id.edt_installmentDate);
        edtAddress = (EditText) findViewById(R.id.edt_address);
        tvAddNewStuentCourse = (TextView) findViewById(R.id.tv_add_new);
        tvViewStudentCoursesAdded = (TextView) findViewById(R.id.tv_view_student_courses);
        tvAddNewStudent = (TextView) findViewById(R.id.tv_add_new_student);

        edtBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerDiaogBirthDate.show();
            }
        });

        edtJoiningDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerDialogJoinedDate.show();
            }
        });

        edtInstallmentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerDialogInstallmentDate.show();
            }
        });

        mSpinnerStandard = findViewById(R.id.spinner_standard);
        mSpinnerStream = findViewById(R.id.spinner_stream);
        mSpinnerSubjects = findViewById(R.id.spinner_subject);

        mTvStream = findViewById(R.id.tvStream);

        mSpinnerStandard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mSpinnerStandard.getSelectedItem().toString().contains("B.COM") || mSpinnerStandard.getSelectedItem().toString().contains("M.COM")) {
                    mSpinnerStream.setSelection(1);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddStudentActivity.this, android.R.layout.simple_list_item_1, aList1112Commerence);
                    mSpinnerSubjects.setAdapter(arrayAdapter);
                } else {
                    if (mSpinnerStandard.getSelectedItem().toString().contains("11") || mSpinnerStandard.getSelectedItem().toString().contains("12")) {
                        mSpinnerStream.setVisibility(View.VISIBLE);
                        mTvStream.setVisibility(View.VISIBLE);

                        //for 11 12
                        if (mSpinnerStream.getSelectedItem().toString().contains("Science")) {
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddStudentActivity.this, android.R.layout.simple_list_item_1, aList1112Science);
                            mSpinnerSubjects.setAdapter(arrayAdapter);
                        } else if (mSpinnerStream.getSelectedItem().toString().contains("Commerence")) {
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddStudentActivity.this, android.R.layout.simple_list_item_1, aList1112Commerence);
                            mSpinnerSubjects.setAdapter(arrayAdapter);
                        } else if (mSpinnerStream.getSelectedItem().toString().contains("Arts")) {
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddStudentActivity.this, android.R.layout.simple_list_item_1, aList1112Arts);
                            mSpinnerSubjects.setAdapter(arrayAdapter);
                        }
                    } else {

                        mSpinnerStream.setVisibility(View.GONE);
                        mTvStream.setVisibility(View.GONE);
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddStudentActivity.this, android.R.layout.simple_list_item_1, aListSchoolSubjects);
                        mSpinnerSubjects.setAdapter(arrayAdapter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSpinnerpartners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position>0){
                    strSelectedPartnerId = ""+modelPartners.getPartners().get(position-1).getId();
                    strSelectedPartnerName = ""+modelPartners.getPartners().get(position-1).getName();
                    Toast.makeText(AddStudentActivity.this, ""+strSelectedPartnerName, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void showCourseDetailsDialog(final AddNewCourseModelWhileAddingNewStudent modelEditCourse, final int clickedPosition) {

        if (dialogTotalCourseList != null && dialogTotalCourseList.isShowing()) {
            //dismissing dialog in case of EDIT
            dialogTotalCourseList.dismiss();
        }

        final Dialog dialogStudentCourseDetails = new Dialog(AddStudentActivity.this);
        dialogStudentCourseDetails.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogStudentCourseDetails.setContentView(R.layout.dialog_student_course_details);

        spinnerCourseStatus = (Spinner) dialogStudentCourseDetails.findViewById(R.id.spinner_course_status);
        spinnerCertificate = (Spinner) dialogStudentCourseDetails.findViewById(R.id.spinner_certificate);
        spinnerBookMaterial = (Spinner) dialogStudentCourseDetails.findViewById(R.id.spinner_book_material);
        spinnerBag = (Spinner) dialogStudentCourseDetails.findViewById(R.id.spinner_bag);

        linCourseDetails = (LinearLayout) dialogStudentCourseDetails.findViewById(R.id.lin_course_details);
        tvCourses = (TextView) dialogStudentCourseDetails.findViewById(R.id.tv_courses);
        edtCourseList = dialogStudentCourseDetails.findViewById(R.id.edt_course_name);
        tvCourseFees = (TextView) dialogStudentCourseDetails.findViewById(R.id.tv_course_fees);
        llFeesAmount = (LinearLayout) dialogStudentCourseDetails.findViewById(R.id.ll_fees_amount);
        tvDecreaseFees = (TextView) dialogStudentCourseDetails.findViewById(R.id.tv_decrease_fees);
        edtCoursefees = (EditText) dialogStudentCourseDetails.findViewById(R.id.edtCoursefees);
        tvIncreaseFees = (TextView) dialogStudentCourseDetails.findViewById(R.id.tv_increase_fees);
        tvCourseDuration = (TextView) dialogStudentCourseDetails.findViewById(R.id.tv_course_duration);
        edtCourseDuration = dialogStudentCourseDetails.findViewById(R.id.edt_course_duration);
        TextView tvSubmit = (TextView) dialogStudentCourseDetails.findViewById(R.id.tv_submit);
        TextView tvCancel = (TextView) dialogStudentCourseDetails.findViewById(R.id.tv_cancel);

        tvIncreaseFees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtil.isNullString(edtCoursefees.getText().toString().trim())) {
                    edtCoursefees.setText("0");
                }

                int fees = Integer.parseInt(edtCoursefees.getText().toString().trim());

                if (fees != 99999) {
                    fees += 500;
                    edtCoursefees.setText("" + fees);
                }
            }
        });

        tvDecreaseFees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtil.isNullString(edtCoursefees.getText().toString().trim())) {
                    edtCoursefees.setText("00");
                }

                int fees2 = Integer.parseInt(edtCoursefees.getText().toString().trim());
                if (fees2 > 499) {
                    fees2 -= 500;
                    edtCoursefees.setText("" + fees2);
                }

            }
        });

        edtCourseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(mArrListCourses!=null && mArrListCourses.size()>0){
                    showOrHideDialog(dialogTotalCourseList);
                }else{
                    callApiToLoadCourseList();
                }*/
                startActivityForResult(new Intent(AddStudentActivity.this,
                                CourseListActivity.class)
                                .putExtra(KEY_COURSE_SELECTION_TAG, "")
                        , CODE_COURSE_SELECTION_FOR_ADD_STUDENT);

            }
        });

        edtCourseDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelCourseDurationList != null) {
                    showOrHideDialog(dialogCourseDurations);
                } else {
                    callApiToLoadCourseDurations();
                }
            }
        });


        //Checking for COURSE_EDIT
        if (modelEditCourse != null && clickedPosition >= 0) {
            //EDIT
            edtCourseList.setText(modelEditCourse.getStrCourseName());
            edtCourseDuration.setText(modelEditCourse.getStrDuration());

            switch (modelEditCourse.getStrCourseStatus().toLowerCase()) {
                case "running":
                    spinnerCourseStatus.setSelection(0);
                    break;
                case "pending":
                    spinnerCourseStatus.setSelection(1);
                    break;
                case "suspended":
                    spinnerCourseStatus.setSelection(2);
                    break;
                case "completed":
                    spinnerCourseStatus.setSelection(3);
                    break;
                case "other":
                    spinnerCourseStatus.setSelection(4);
                    break;
            }

            switch (modelEditCourse.getStrCertificate().toLowerCase()) {
                case "yes":
                    spinnerCertificate.setSelection(1);
                    break;
                case "no":
                    spinnerCertificate.setSelection(0);
                    break;
            }

            switch (modelEditCourse.getStrBookMaterial().toLowerCase()) {
                case "yes":
                    spinnerBookMaterial.setSelection(1);
                    break;
                case "no":
                    spinnerBookMaterial.setSelection(0);
                    break;
            }

            switch (modelEditCourse.getStrBag().toLowerCase()) {
                case "yes":
                    spinnerBag.setSelection(1);
                    break;
                case "no":
                    spinnerBag.setSelection(0);
                    break;
            }
            edtCoursefees.setText(modelEditCourse.getStrFees());
        }
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtil.isNullString("" + edtCourseList.getText().toString().trim())) {
                    playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_course), AddStudentActivity.this);
                    Toast.makeText(AddStudentActivity.this, "" + getResources().getString(R.string.toast_invalid_course), Toast.LENGTH_SHORT).show();
                    edtCourseList.setFocusable(true);
                } else if (CommonUtil.isNullString("" + edtCourseDuration.getText().toString().trim())) {
                    playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_course_duration), AddStudentActivity.this);
                    Toast.makeText(AddStudentActivity.this, "" + getResources().getString(R.string.toast_invalid_course_duration), Toast.LENGTH_SHORT).show();
                    edtCourseDuration.setFocusable(true);
                } else if (CommonUtil.isNullString("" + edtCoursefees.getText().toString().trim())) {
                    playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_course_fees), AddStudentActivity.this);
                    Toast.makeText(AddStudentActivity.this, "" + getResources().getString(R.string.toast_invalid_course_fees), Toast.LENGTH_SHORT).show();
                    edtCoursefees.setFocusable(true);
                } else {
                    if (modelEditCourse != null) {
                        //EDit
                        modelCourseData = new AddNewCourseModelWhileAddingNewStudent();
                        Log.e(">>> BAG : ", "" + spinnerBag.getSelectedItem().toString());
                        Log.e(">>> BOOK MATERIAL : ", "" + spinnerBookMaterial.getSelectedItem().toString());
                        modelCourseData.setStrBag(spinnerBag.getSelectedItem().toString());
                        modelCourseData.setStrBookMaterial(spinnerBookMaterial.getSelectedItem().toString());
                        modelCourseData.setStrCertificate(spinnerCertificate.getSelectedItem().toString());
                        modelCourseData.setStrFees(edtCoursefees.getText().toString());
                        if (CommonUtil.isNullString(strSelectedCourseId)) {
                            modelCourseData.setStrCourseId(modelEditCourse.getStrCourseId());
                            modelCourseData.setStrCourseImage(modelEditCourse.getStrCourseImage());
                        } else {
                            modelCourseData.setStrCourseId(strSelectedCourseId);
                            modelCourseData.setStrCourseImage(strSelectedCourseImage);
                        }

                        modelCourseData.setStrCourseName(edtCourseList.getText().toString());
                        modelCourseData.setStrDuration(edtCourseDuration.getText().toString());
                        modelCourseData.setStrCourseStatus(spinnerCourseStatus.getSelectedItem().toString());

                        mArrListStudentCourseDetails.set(clickedPosition, modelCourseData);
                        if (dialogStudentCourseDetails != null) {
                            dialogStudentCourseDetails.dismiss();
                        }

                        if (dialogTotalCourseList != null && dialogTotalCourseList.isShowing()) {
                            dialogTotalCourseList.dismiss();
                        }

                        //EDIT LOGIC
                        strSelectedCourseId = "";
                        strSelectedCourseImage = "";

                    } else {
                        //ADD
                        int isFoundAdded = 0;
                        //Checking to avoid duplicate entries
                        if (mArrListStudentCourseDetails != null && mArrListStudentCourseDetails.size() > 0) {
                            for (int count = 0; count < mArrListStudentCourseDetails.size(); count++) {
                                if (mArrListStudentCourseDetails.get(count).getStrCourseId().equals(strSelectedCourseId)) {
                                    isFoundAdded = 1;
                                }
                            }
                            if (isFoundAdded == 0) {
                                addCourseData();
                                if (dialogStudentCourseDetails != null && dialogStudentCourseDetails.isShowing()) {
                                    dialogStudentCourseDetails.dismiss();
                                }
                                strSelectedCourseDuration = null;
                            } else {
                                //Data exists already.
                                Toast.makeText(AddStudentActivity.this, "" + getResources().getString(R.string.toast_course_already_added), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //First time
                            addCourseData();
                            if (dialogStudentCourseDetails != null && dialogStudentCourseDetails.isShowing()) {
                                dialogStudentCourseDetails.dismiss();
                            }
                            strSelectedCourseDuration = null;
                        }
                    }
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogStudentCourseDetails != null && dialogStudentCourseDetails.isShowing()) {
                    dialogStudentCourseDetails.dismiss();
                }
            }
        });

        if (dialogStudentCourseDetails != null && !dialogStudentCourseDetails.isShowing()) {
            dialogStudentCourseDetails.show();
        }
    }

    private void addCourseData() {
        modelCourseData = new AddNewCourseModelWhileAddingNewStudent();
        modelCourseData.setStrBag(spinnerBag.getSelectedItem().toString());
        modelCourseData.setStrBookMaterial(spinnerBookMaterial.getSelectedItem().toString());
        modelCourseData.setStrCertificate(spinnerCertificate.getSelectedItem().toString());
        modelCourseData.setStrFees(edtCoursefees.getText().toString());
        modelCourseData.setStrCourseId(strSelectedCourseId);
        modelCourseData.setStrCourseName(strSelectedCourseName);
        modelCourseData.setStrDuration(strSelectedCourseDuration);
        modelCourseData.setStrCourseImage(strSelectedCourseImage);
        modelCourseData.setStrCourseStatus(spinnerCourseStatus.getSelectedItem().toString());

        mArrListStudentCourseDetails.add(modelCourseData);

    }

    private void showAddedCourseListDialog() {
        final Dialog dialogCourseAddedList = new Dialog(AddStudentActivity.this);
        dialogCourseAddedList.setContentView(R.layout.dialog_view_student_courses_list);
        dialogCourseAddedList.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ListView mLvCourses = dialogCourseAddedList.findViewById(R.id.lv_courses);
        TextView tvOkay = dialogCourseAddedList.findViewById(R.id.tv_okay);

        mAdapterCourseList = new CourseListWhileAddingOrEditingStudentAdapter(AddStudentActivity.this, mArrListStudentCourseDetails, this, "add_student");
        mLvCourses.setAdapter(mAdapterCourseList);

        tvOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogCourseAddedList != null && dialogCourseAddedList.isShowing()) {
                    dialogCourseAddedList.dismiss();
                }
            }
        });

        if (dialogCourseAddedList != null && !dialogCourseAddedList.isShowing()) {
            dialogCourseAddedList.show();
        }
    }

    private void callApiToLoadCourseDurations() {
        if (!CommonUtil.isInternetAvailable(AddStudentActivity.this)) {
            return;
        }

        new OkHttpRequest(AddStudentActivity.this,
                OkHttpRequest.Method.GET,
                Constants.WS_COURSE_DURATIONS,
                RequestParam.loadGetRequestsData(),
                RequestParam.getNull(),
                CODE_GET_COURSE_DURATIONS,
                true, this);
    }

    private void callApiToLoadCourseList() {
        if (!CommonUtil.isInternetAvailable(AddStudentActivity.this)) {
            return;
        }

        new OkHttpRequest(AddStudentActivity.this,
                OkHttpRequest.Method.GET,
                Constants.WS_COURSE_LIST,
                RequestParam.loadGetRequestsData(),
                RequestParam.getNull(), CODE_COURSE_LIST,
                false, this);
    }


    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {

        Log.e(">>>", "" + response);

        if (response == null) {
            response = "null";
        }

        switch (requestId) {
            case CODE_GET_COURSE_DURATIONS:
                Log.e("categories >> ", "" + response);
                final Gson courseDurationList = new Gson();
                try {
                    modelCourseDurationList = courseDurationList
                            .fromJson(response, ModelCourseDurationList.class);

                    if (modelCourseDurationList.getAlDuration() != null && modelCourseDurationList.getAlDuration().size() > 0) {

                        setCourseDurationListAdapter(modelCourseDurationList.getAlDuration());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CODE_PARTNERS_LIST:
                Log.e("partners>>>",""+response);
                final Gson partnersGson = new Gson();
                try{
                    modelPartners = partnersGson.fromJson(response,ModelPartners.class);
                    if(modelPartners.getPartners()!=null && modelPartners.getPartners().size()>0){
                        setPartnerbaseAdapter(modelPartners.getPartners());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case CODE_ADD_NEW_STUDENT:
                try {
                    JSONObject jObjResponse = new JSONObject(response);
                    if (jObjResponse != null && jObjResponse.has("status")) {
                        if (jObjResponse.getString("status").equals("success")) {
                            finish();
                        }
                        Toast.makeText(this, "" + jObjResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        //showAlertToAddInstallmentDate();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setPartnerbaseAdapter(ArrayList<ModelPartners.Partner> partners) {
        mAlpartners = new ArrayList<>();
        if(partners.size()>0 && partners!=null){
            for(int i = 0 ; i < partners.size() ; i++){
                mAlpartners.add(i,""+partners.get(i).getName());
            }
        }
        mAlpartners.add(0,"Select Partner");
        ArrayAdapter<String> partnerAdapter = new ArrayAdapter<>(AddStudentActivity.this,android.R.layout.simple_list_item_1,mAlpartners);
        mSpinnerpartners.setAdapter(partnerAdapter);
    }

    private void showAlertToAddInstallmentDate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddStudentActivity.this);
        builder.setMessage("Do you want to add Installment Date?");
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showInstallmentDateDialog();
            }
        }).setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //nothing
            }
            // Create the AlertDialog object and return it
        });
        builder.show();
    }

    private void showOrHideDialog(Dialog dialog) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }


    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    private void setCourseDurationListAdapter(final ArrayList<ModelCourseDurationList.Duration> mArrListCoursDurations) {
        ArrayList<String> alDurations = new ArrayList<>();
        if (mArrListCoursDurations != null && mArrListCoursDurations.size() > 0) {
            for (int i = 0; i < mArrListCoursDurations.size(); i++) {
                alDurations.add("" + mArrListCoursDurations.get(i).getDuration());
            }
        }

        //alDurations.add(0, "--Select Course Duration--");
        ArrayAdapter<String> adptCourseDurations = new ArrayAdapter<String>(AddStudentActivity.this, android.R.layout.simple_list_item_1, alDurations);
        //spinnerCourseDuration.setAdapter(adptCourseDurations);
        dialogCourseDurations = new Dialog(AddStudentActivity.this);
        dialogCourseDurations.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCourseDurations.setContentView(R.layout.dialog_course_list);//list as duration (using same layout)

        ListView lvCourses = dialogCourseDurations.findViewById(R.id.lv_courses);
        lvCourses.setAdapter(adptCourseDurations);

        lvCourses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                strSelectedCourseDuration = "" + mArrListCoursDurations.get(position).getDuration();
                edtCourseDuration.setText("" + mArrListCoursDurations.get(position).getDuration());
                showOrHideDialog(dialogCourseDurations);
            }
        });
        showOrHideDialog(dialogCourseDurations);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_student_image_pick:
                showDialogToSelectUploadOption();
                break;

            case R.id.tv_add_new:
                showCourseDetailsDialog(null, -1);
                break;

            case R.id.tv_view_student_courses:
                if (mArrListStudentCourseDetails != null && mArrListStudentCourseDetails.size() > 0) {
                    showAddedCourseListDialog();
                } else {
                    playSoundForAttendance("No details added yet. Please, Add Course details first.", AddStudentActivity.this);
                    Toast.makeText(this, "No Details added yet. Add Course Details first. ", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.tv_add_new_student:
                validateStudentDataAndCallApiToAddNewStudent();
                //showAlertToAddInstallmentDate();
                break;
        }
    }

    private void showDialogToSelectUploadOption() {
        // Set attachment type
        ATTACHMENT_TYPE = ATTACHMENT_IMAGE;
        askForPermission();
    }

    private void askForPermission() {
        if (PermissionManager.hasPermissions(AddStudentActivity.this, Permissions.STORAGE_PERMISSIONS)) {
            showChooser();
        } else {
            PermissionManager.requestPermissions(AddStudentActivity.this, Constants.CODE_RUNTIME_STORAGE_PERMISSION,
                    permissionListener, "", Permissions.STORAGE_PERMISSIONS);
        }
    }

    private void updateImage() {
        // Configure image chooser option
        PhotoPicker.configuration(AddStudentActivity.this)
                .setImagesFolderName(Constants.DIRECTORY_GIT_MEDIA);

        PhotoPicker.openChooserWithGallery(AddStudentActivity.this, "" + ResourceUtils.getString(R.string.select_picture_from), 0);
    }

    private final PermissionManager.PermissionListener permissionListener = new PermissionManager.PermissionListener() {
        @Override
        public void onPermissionsGranted(List<String> perms) {
            if (perms.size() == Permissions.STORAGE_PERMISSIONS.length) {
                updateImage();
            } else {
                LogUtil.i("Add Course Activity >>", "User denied some of required permissions! "
                        + "Even though we have following permissions now, "
                        + "task will still be aborted.\n" + CommonUtil.getStringFromList(perms));
            }
        }

        @Override
        public void onPermissionsDenied(List<String> perms) {
        }

        @Override
        public void onPermissionRequestRejected() {
        }

        @Override
        public void onPermissionNeverAsked(List<String> perms) {
        }

    };

    public void showChooser() {
        if (ATTACHMENT_TYPE.equals(ATTACHMENT_IMAGE)) { // User selected image option
            // Move to specific folder
            PhotoPicker.configuration(AddStudentActivity.this).setImagesFolderName(Constants.DIRECTORY_GIT_MEDIA);
            PhotoPicker.openChooserWithGallery(AddStudentActivity.this, ResourceUtils.getString(R.string.select_picture_from), PhotoPicker.SELECT_PHOTO);
        } else if (ATTACHMENT_TYPE.equals(ATTACHMENT_PDF)) {
            playSoundForAttendance("This feature is in development mode. It will be available soon", AddStudentActivity.this);
            Toast.makeText(this, "In Progress", Toast.LENGTH_SHORT).show();
        }
    }

    private void validateStudentDataAndCallApiToAddNewStudent() {

        if (!CommonUtil.isInternetAvailable(AddStudentActivity.this)) {
            return;
        }

        if (CommonUtil.isNullString("" + edtFirstname.getText().toString().trim())) {
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_firstname), AddStudentActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_firstname), Toast.LENGTH_SHORT).show();
            return;
        }

       /* if(TextUtils.isDigitsOnly(""+edtFirstname.getText().toString().length()))
        {
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_firstname), AddStudentActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_firstname), Toast.LENGTH_SHORT).show();
            return;
        }*/

        if (CommonUtil.isNullString("" + edtLastname.getText().toString().trim())) {
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_lastname), AddStudentActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_lastname), Toast.LENGTH_SHORT).show();
            return;
        }

        /*if(TextUtils.isDigitsOnly(""+edtLastname.getText().toString().length()))
        {
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_lastname), AddStudentActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_lastname), Toast.LENGTH_SHORT).show();
            return;
        }
*/

        if (CommonUtil.isNullString("" + edtEmail.getText().toString().trim())) {
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_email), AddStudentActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_email), Toast.LENGTH_SHORT).show();
            return;
        }

        if (CommonUtil.isNullString("" + edtPassword.getText().toString().trim())) {
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_password), AddStudentActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_password), Toast.LENGTH_SHORT).show();
            return;
        }

        if (CommonUtil.isNullString("" + edtConfPassword.getText().toString().trim())) {
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_confirm_password), AddStudentActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_confirm_password), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!edtPassword.getText().toString().trim().equals(edtConfPassword.getText().toString().trim())) {
            playSoundForAttendance("" + getResources().getString(R.string.toast_password_not_matched), AddStudentActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_password_not_matched), Toast.LENGTH_SHORT).show();
            return;
        }

        if (CommonUtil.isNullString("" + edtMobileno.getText().toString().trim())) {
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_mobileNO), AddStudentActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_mobileNO), Toast.LENGTH_SHORT).show();
            return;
        }

        if (CommonUtil.isNullString("" + edtBirthDate.getText().toString().trim())) {
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_birthDate), AddStudentActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_birthDate), Toast.LENGTH_SHORT).show();
            return;
        }

        if (CommonUtil.isNullString("" + edtParentname.getText().toString().trim())) {
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_parent_name), AddStudentActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_parent_name), Toast.LENGTH_SHORT).show();
            return;
        }

        if (CommonUtil.isNullString("" + edtParentMobileno.getText().toString().trim())) {
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_parent_mobile_no), AddStudentActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_parent_mobile_no), Toast.LENGTH_SHORT).show();
            return;
        }

        if (CommonUtil.isNullString("" + edtJoiningDate.getText().toString().trim())) {
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_joining_date), AddStudentActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_joining_date), Toast.LENGTH_SHORT).show();
            return;
        }

        if (CommonUtil.isNullString("" + edtAddress.getText().toString().trim())) {
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_address), AddStudentActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_address), Toast.LENGTH_SHORT).show();
            return;
        }

        if (mArrListStudentCourseDetails != null && mArrListStudentCourseDetails.size() > 0) {
            /*
             * Same Service is used for
             * */
            callApiToAddOrEditStudent(strStudentIdToEdit, edtFirstname.getText().toString().trim(),
                    edtLastname.getText().toString().trim(),
                    edtEmail.getText().toString().trim(),
                    edtPassword.getText().toString().trim(),
                    edtMobileno.getText().toString().trim(),
                    edtBirthDate.getText().toString().trim(),
                    String.valueOf(getSharedPrefrencesInstance(AddStudentActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, "")),
                    edtParentname.getText().toString().trim(),
                    edtParentMobileno.getText().toString().trim(),
                    spinnerReferenceBy.getSelectedItem().toString().trim(),
                    edtJoiningDate.getText().toString().trim(),
                    edtAddress.getText().toString().trim(),
                    mArrListStudentCourseDetails);
        } else {
            playSoundForAttendance("Please Enter Course details for Student.", AddStudentActivity.this);
            Toast.makeText(this, "Please Enter Course details for Student.", Toast.LENGTH_SHORT).show();
            return;
        }
    }


    private void callApiToAddOrEditStudent(String studentId,
                                           String fname,
                                           String lname,
                                           String email,
                                           String password,
                                           String mobileNo,
                                           String birthDate,
                                           String branchId,
                                           String parentName,
                                           String parentMobile,
                                           String referenceBy,
                                           String joiningDate,
                                           String address,
                                           ArrayList<AddNewCourseModelWhileAddingNewStudent> listStudentCourseData) {

        if (!CommonUtil.isInternetAvailable(AddStudentActivity.this)) {
            return;
        }

        if (mFileSelectedStudentImage != null) {

            ArrayList<File> aListFiles = new ArrayList<>();
            aListFiles.add(mFileSelectedStudentImage);

            //Adding New Course with media file...
            new OkHttpRequest(AddStudentActivity.this,
                    OkHttpRequest.Method.POST,
                    Constants.WS_ADD_STUDENT,
                    RequestParam.addNewStudent(
                            studentId,
                            fname,
                            lname,
                            email,
                            password,
                            mobileNo,
                            birthDate,
                            "" + getSharedPrefrencesInstance(AddStudentActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, ""),
                            parentName,
                            parentMobile,
                            referenceBy,
                            joiningDate,
                            address,
                            mArrListStudentCourseDetails),
                    RequestParam.getNull(),
                    RequestParam.addNewStudentImageParam(aListFiles),
                    CODE_ADD_NEW_STUDENT,
                    true,
                    this);
        } else {
            new OkHttpRequest(AddStudentActivity.this,
                    OkHttpRequest.Method.POST,
                    Constants.WS_ADD_STUDENT,
                    RequestParam.addNewStudent(
                            strStudentIdToEdit,
                            fname,
                            lname,
                            email,
                            password,
                            mobileNo,
                            birthDate,
                            "" + getSharedPrefrencesInstance(AddStudentActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, ""),
                            parentName,
                            parentMobile,
                            referenceBy,
                            joiningDate,
                            address,
                            mArrListStudentCourseDetails),
                    RequestParam.getNull(),
                    CODE_ADD_NEW_STUDENT,
                    true,
                    this);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == CODE_COURSE_SELECTION_FOR_ADD_STUDENT) {
            if (data.getStringExtra(KEY_INTENT_COURSE_ID) != null) {
                strSelectedCourseId = "" + data.getStringExtra(KEY_INTENT_COURSE_ID);
            }

            if (data.getStringExtra(KEY_INTENT_COURSE_IMAGE) != null) {
                strSelectedCourseImage = "" + data.getStringExtra(KEY_INTENT_COURSE_IMAGE);
            }

            if (data.getStringExtra(KEY_INTENT_COURSE_NAME) != null) {
                strSelectedCourseName = "" + data.getStringExtra(KEY_INTENT_COURSE_NAME);
            }
            /*if (data.getStringExtra(KEY_INTENT_STUDENT_IMAGE) != null) {
                strSelectedStudentImage = "" + data.getStringExtra(KEY_INTENT_STUDENT_IMAGE);
                CommonUtil.setCircularImageForUser(AddStudentActivity.this,ivStudentImagePick,strSelectedStudentImage);
            }*/

            edtCourseList.setText("" + strSelectedCourseName);
        }

        PhotoPicker.handleActivityResult(requestCode, resultCode, data, AddStudentActivity.this, new PhotoPickerCallback() {
            @Override
            public void onImagePickerError(Exception e, PhotoPicker.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, PhotoPicker.ImageSource source, int type, int absoluteType) {
                if (imageFiles.size() > 0) {
                    onPhotosReturned(imageFiles.get(0), type, absoluteType);
                }
            }
        });
    }

    private void onPhotosReturned(File returnedFile, int type, int absoluteType) {
        if (type == PhotoPicker.SELECT_PHOTO) {
            // If photo format is not valid as well as file is corrupted.
            /*if (CommonUtil.isPhotoCorrupted(returnedFile)) {
                playSoundForAttendance("" + ResourceUtils.getString(R.string.toast_attachment_photo_size_null), AddStudentActivity.this);
                Toast.makeText(this, "" + ResourceUtils.getString(R.string.toast_attachment_photo_size_null), Toast.LENGTH_SHORT).show();
                return;
            }*/
            // Reduce the image bitmap to prevent from out of memory error.
            //returnedFile = CommonUtil.compressImage(returnedFile.getAbsolutePath());
            mFileSelectedStudentImage = returnedFile;

            //Bitmap myBitmap = BitmapFactory.decodeFile(mFileSelectedCourseImage.getAbsolutePath());
            ivStudentImagePick.setImageURI(Uri.fromFile(mFileSelectedStudentImage));
            //ivCourseImage.setImageBitmap(myBitmap);
            //ivCourseImagePick.setImageBitmap(myBitmap);
        }
        System.gc();
    }


    @Override
    public void onCourseRawClick(int position) {
        showCourseDetailsDialog(mArrListStudentCourseDetails.get(position), position);
    }

    @Override
    public void onDeleteCourse(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddStudentActivity.this);
        builder.setMessage("Are you Sure ?");
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mArrListStudentCourseDetails != null && mArrListStudentCourseDetails.size() > 0) {
                    mArrListStudentCourseDetails.remove(position);
                    mAdapterCourseList.notifyDataSetChanged();
                }
            }
        }).setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //nothing
            }
            // Create the AlertDialog object and return it
        });

        builder.show();
    }

    private void showInstallmentDateDialog() {

        final Dialog dialogInstallmentDate = new Dialog(this);
        dialogInstallmentDate.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogInstallmentDate.setContentView(R.layout.dialog_installment_date);
        dialogInstallmentDate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogInstallmentDate.setCancelable(false);


        RelativeLayout relHeader;
        CustomTextView tvCancel;
        final RecyclerView rvInstallmentDate;
        LinearLayout llBottomActions;
        CustomButton btnNotNowInstallmentDate;
        CustomButton btnSaveInstallmentDate;
        ImageView ivAddMode;

        tvTapTpAdd = (CustomTextView) dialogInstallmentDate.findViewById(R.id.tv_tap_tp_add);
        relHeader = (RelativeLayout) dialogInstallmentDate.findViewById(R.id.rel_header);
        tvCancel = (CustomTextView) dialogInstallmentDate.findViewById(R.id.tv_cancel);
        rvInstallmentDate = (RecyclerView) dialogInstallmentDate.findViewById(R.id.rv_installment_date);
        llBottomActions = (LinearLayout) dialogInstallmentDate.findViewById(R.id.ll_bottom_actions);
        ivAddMode = (ImageView) dialogInstallmentDate.findViewById(R.id.iv_add_more_installment_date);
        btnNotNowInstallmentDate = (CustomButton) dialogInstallmentDate.findViewById(R.id.btn_not_now_installment_date);
        btnSaveInstallmentDate = (CustomButton) dialogInstallmentDate.findViewById(R.id.btn_save_installment_date);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener startDateListener;
        startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (dayOfMonth < 10) {
                    if (month < 10) {
                        InstallmentModel model = new InstallmentModel();
                        model.setStrDate("0" + dayOfMonth + "-0" + (month + 1) + "-" + year);
                        model.setStrRs("");
                        alInstallmentDates.add(model);
                    } else {
                        InstallmentModel model = new InstallmentModel();
                        model.setStrDate("0" + dayOfMonth + "-0" + (month + 1) + "-" + year);
                        model.setStrRs("");
                        alInstallmentDates.add(model);
                    }
                } else {
                    if (month < 10) {
                        InstallmentModel model = new InstallmentModel();
                        model.setStrDate("0" + dayOfMonth + "-0" + (month + 1) + "-" + year);
                        model.setStrRs("");
                        alInstallmentDates.add(model);
                    } else {
                        InstallmentModel model = new InstallmentModel();
                        model.setStrDate("0" + dayOfMonth + "-0" + (month + 1) + "-" + year);
                        model.setStrRs("");
                        alInstallmentDates.add(model);
                    }
                }
                if (alInstallmentDates.size() > 0) {
                    tvTapTpAdd.setVisibility(View.GONE);
                } else {
                    tvTapTpAdd.setVisibility(View.VISIBLE);
                }
                setAdapterForInstallmentDates(alInstallmentDates, rvInstallmentDate);
            }
        };

        final DatePickerDialog installmentDatePickerDialog = new DatePickerDialog(
                this, startDateListener, year, month, day);
        installmentDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        ivAddMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                installmentDatePickerDialog.show();
            }
        });

        btnNotNowInstallmentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogInstallmentDate != null && dialogInstallmentDate.isShowing()) {
                    dialogInstallmentDate.dismiss();
                }
            }
        });

        btnSaveInstallmentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddStudentActivity.this, "Installment records saved.", Toast.LENGTH_SHORT).show();
                if (dialogInstallmentDate != null && dialogInstallmentDate.isShowing()) {

                   dialogInstallmentDate.dismiss();
                }


                //callApiToSendSMS();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogInstallmentDate != null && dialogInstallmentDate.isShowing()) {
                    dialogInstallmentDate.dismiss();
                }
            }
        });

        if (dialogInstallmentDate != null && !dialogInstallmentDate.isShowing()) {
            dialogInstallmentDate.show();
        }
    }

    private void callApiToSendSMS() {
        if (!CommonUtil.isInternetAvailable(AddStudentActivity.this)) {
            return;
        }

        new OkHttpRequest(AddStudentActivity.this,
                OkHttpRequest.Method.POST,
                Constants.SEND_SMS_BASE_URL,
                RequestParam.sendSMS(),
                RequestParam.getNull(),
                111,
                true, this);
    }

    private void setAdapterForInstallmentDates(ArrayList<InstallmentModel> list, RecyclerView rvInstallmentDate) {
        adapterInstallmentDate = new InstallmentListAdapter(AddStudentActivity.this, list, this);
        LinearLayoutManager manager = new LinearLayoutManager(AddStudentActivity.this);
        rvInstallmentDate.setLayoutManager(manager);
        rvInstallmentDate.setAdapter(adapterInstallmentDate);
    }

    @Override


    public void onInstallmentRemove(int position) {
        if (alInstallmentDates != null && alInstallmentDates.size() > 0) {
            alInstallmentDates.remove(position);
            if (adapterInstallmentDate != null) {
                adapterInstallmentDate.notifyDataSetChanged();
            }
        }
        if (alInstallmentDates.size() > 0) {
            tvTapTpAdd.setVisibility(View.GONE);
        } else {
            tvTapTpAdd.setVisibility(View.VISIBLE);
        }
    }
}
