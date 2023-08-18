package com.globalitians.employees.payments.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.students.activities.StudentListActivity;
import com.globalitians.employees.students.adapters.StudentListAdapter;
import com.globalitians.employees.students.models.ModelStudent;
import com.globalitians.employees.students.models.ModelStudentDetailsResponse;
import com.globalitians.employees.utility.CircularImageView;
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
import static com.globalitians.employees.utility.Constants.CODE_POST_PAYMENT;
import static com.globalitians.employees.utility.Constants.CODE_STUDENT_DETAILS;
import static com.globalitians.employees.utility.Constants.CODE_STUDENT_SELECTION_FOR_ATTENDANCE;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_STUDENT_COURSE;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_STUDENT_ID;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_STUDENT_IMAGE;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_STUDENT_NAME;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_STUDENT_UNPAID_AMOUNT;
import static com.globalitians.employees.utility.Constants.KEY_LOGGEDIN_USER_ID;
import static com.globalitians.employees.utility.Constants.KEY_STUDENT_SELECTION_FOR_UNPAID_STUDENT_FEES;
import static com.globalitians.employees.utility.Constants.LOGIN_PREFRENCES;

public class AddStudentFeesActivity extends AppCompatActivity implements View.OnClickListener, OkHttpInterface, StudentListAdapter.OnStudentListListener {
    private EditText edtStudentId;
    private TextView tvSearchStudent;
    private EditText edtPaymentDate;
    private EditText edtAmount;
    private Spinner spinnerPaymentMode;
    private LinearLayout linChequeView;
    private EditText edtChequeNumber;
    private TextView tvSubmitPayment;
    private CircularImageView mIvStudent;

    private DatePickerDialog mDatePickerDiaogPaymentDate = null;
    private ArrayList<ModelStudent.Student> mArrListStudents = null;
    private ArrayAdapter<String> mAdapterStudentList = null;
    private String strSelectedStudentId = "";
    private String strSelectedStudentName = "";
    private String strSelectedStudentImage="";
    private String strSelectedCourseName="";
    private String strSelectedUnpaidAmount="";
    private TextView tvDecreaseFees;
    private TextView tvIncreaseFees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(AddStudentFeesActivity.this);
        setContentView(R.layout.activity_add_student_fees);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViews();
        initializeDatePickers();
    }


    private void callApiToPostPayment() {
        if (!CommonUtil.isInternetAvailable(AddStudentFeesActivity.this)) {
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_PREFRENCES, Context.MODE_PRIVATE);
        String loggedInUserId = sharedPreferences.getString(KEY_LOGGEDIN_USER_ID, "");

        new OkHttpRequest(AddStudentFeesActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_POST_PAYMENT,
                RequestParam.postPayment(loggedInUserId,
                        "" + edtPaymentDate.getText().toString().trim(),
                        "" + edtAmount.getText().toString().trim(),
                        ""+edtStudentId.getText().toString().trim(),
                        "" + spinnerPaymentMode.getSelectedItem().toString(),
                        "" + edtChequeNumber.getText().toString().trim()),
                RequestParam.getNull(),
                CODE_POST_PAYMENT,
                true, this);
    }


    private void findViews() {
        mIvStudent=findViewById(R.id.iv_student_image_pick);
        edtStudentId = findViewById(R.id.edt_student_id);
        tvSearchStudent = findViewById(R.id.tvSearch);
        //tvSearchStudent.setText(Html.fromHtml(getResources().getString(R.string.forgotStudentId)));
        tvSearchStudent.setOnClickListener(this);
        edtPaymentDate = (EditText) findViewById(R.id.edt_payment_date);
        edtPaymentDate.setOnClickListener(this);
        edtAmount = (EditText) findViewById(R.id.edtAmount);
        tvDecreaseFees = (TextView) findViewById(R.id.tv_decrease_fees);
        tvIncreaseFees = (TextView) findViewById(R.id.tv_increase_fees);
        tvDecreaseFees.setOnClickListener(this);
        tvIncreaseFees.setOnClickListener(this);
        spinnerPaymentMode = (Spinner) findViewById(R.id.spinnerPaymentMode);
        linChequeView = (LinearLayout) findViewById(R.id.linChequeView);
        edtChequeNumber = (EditText) findViewById(R.id.edt_cheque_number);
        tvSubmitPayment = (TextView) findViewById(R.id.tvSubmit);
        tvSubmitPayment.setOnClickListener(this);

        spinnerPaymentMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    linChequeView.setVisibility(View.VISIBLE);
                } else {
                    linChequeView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                edtPaymentDate.setText("" + dayOfMonth + "-" + (month + 1) + "-" + year);
            }
        };

        //initializing date filter date picker dialog
        mDatePickerDiaogPaymentDate = new DatePickerDialog(
                AddStudentFeesActivity.this, birthdateListener, year, month, day);
        mDatePickerDiaogPaymentDate.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSearch:
                startActivityForResult(new Intent(AddStudentFeesActivity.this,
                                StudentListActivity.class)
                                .putExtra(KEY_STUDENT_SELECTION_FOR_UNPAID_STUDENT_FEES, "")
                        , CODE_STUDENT_SELECTION_FOR_ATTENDANCE);
                break;
            case R.id.tvSubmit:
                if(!CommonUtil.isNullString(strSelectedStudentId)){
                    if(strSelectedStudentId.equals(edtStudentId.getText().toString().trim())){
                        validateAndCallApiToSubmitPaymentData();
                    }else{
                        callApiToLoadStudentDetails(edtStudentId.getText().toString().trim());
                    }
                }else{
                    callApiToLoadStudentDetails(edtStudentId.getText().toString().trim());
                }

                break;
            case R.id.edt_payment_date:
                mDatePickerDiaogPaymentDate.show();
                break;

            case R.id.tv_increase_fees:
                if (CommonUtil.isNullString(edtAmount.getText().toString().trim())) {
                    edtAmount.setText("0");
                }

                int fees = Integer.parseInt(edtAmount.getText().toString().trim());

                if (fees != 99999) {
                    fees += 100;
                    edtAmount.setText("" + fees);
                }
                break;

            case R.id.tv_decrease_fees:
                if (CommonUtil.isNullString(edtAmount.getText().toString().trim())) {
                    edtAmount.setText("00");
                }

                int fees2 = Integer.parseInt(edtAmount.getText().toString().trim());
                if (fees2 != 0) {
                    fees2 -= 100;
                    edtAmount.setText("" + fees2);
                }

                break;
        }
    }

    private void validateAndCallApiToSubmitPaymentData() {
        if (!CommonUtil.isInternetAvailable(AddStudentFeesActivity.this)) {
            return;
        }

        String studentId = "";
        if (CommonUtil.isNullString("" + edtStudentId.getText().toString())) {
            edtPaymentDate.setError("Please Enter Student Id.");
            edtPaymentDate.setFocusable(true);
            Toast.makeText(this, "Please Enter Student Id", Toast.LENGTH_SHORT).show();
            playSoundForAttendance("Please Enter Student Id.", AddStudentFeesActivity.this);
            return;
        } else if (CommonUtil.isNullString(edtPaymentDate.getText().toString().trim())) {
            edtPaymentDate.setError("Please Select Payment Date.");
            edtPaymentDate.setFocusable(true);
            Toast.makeText(this, "Please Select Payment Date.", Toast.LENGTH_SHORT).show();
            return;
        } else if (CommonUtil.isNullString(edtAmount.getText().toString().trim())) {
            edtAmount.setFocusable(true);
            edtAmount.setError("Please Enter Fees Amount.");
            Toast.makeText(this, "Please Enter Fees Amount.", Toast.LENGTH_SHORT).show();
        } else if (Integer.parseInt(edtAmount.getText().toString().trim()) == 0) {
            edtAmount.setFocusable(true);
            edtAmount.setError("Please Enter Valid Amount.");
            Toast.makeText(this, "Please Enter Valid Amount.", Toast.LENGTH_SHORT).show();
        } else if (spinnerPaymentMode.getSelectedItemPosition() == 1) {
            //If Cheque is selected..
            if (CommonUtil.isNullString(edtChequeNumber.getText().toString().trim())) {
                edtChequeNumber.setFocusable(true);
                edtChequeNumber.setError("Please Enter Cheque Number.");
                Toast.makeText(this, "Please Enter Cheque Number.", Toast.LENGTH_SHORT).show();
                return;
            } else if (Integer.parseInt(edtChequeNumber.getText().toString().trim()) == 0) {
                edtChequeNumber.setFocusable(true);
                edtChequeNumber.setError("Please Enter Valid Cheque Number.");
                Toast.makeText(this, "Please Enter Valid Cheque Number.", Toast.LENGTH_SHORT).show();
            } else {
                studentId = "" + edtStudentId.getText().toString().trim();
                if (strSelectedStudentName != null && !CommonUtil.isNullString(strSelectedStudentName)) {
                    displayStudentConfirmationDialog(strSelectedStudentName, strSelectedStudentImage, strSelectedCourseName);
                } else {
                    callApiToLoadStudentDetails("" + studentId.trim());
                }
            }
        } else {
            //Call API here...
            studentId = "" + edtStudentId.getText().toString().trim();
            if (strSelectedStudentName != null && !CommonUtil.isNullString(strSelectedStudentName)) {
                displayStudentConfirmationDialog(strSelectedStudentName, strSelectedStudentImage, strSelectedCourseName);
            } else {
                callApiToLoadStudentDetails("" + studentId.trim());
            }
        }
    }

    private void displayStudentConfirmationDialog(String studentName, String studentImage, String studentCourse) {

        if(Integer.parseInt(strSelectedUnpaidAmount)>=Integer.parseInt(edtAmount.getText().toString().trim()) && Integer.parseInt(edtAmount.getText().toString())!=0) {


            final Dialog dialogAttendence = new Dialog(AddStudentFeesActivity.this);
            dialogAttendence.setContentView(R.layout.dialog_student_attendence);
            dialogAttendence.setCancelable(false);

            TextView tvStudentId;
            TextView tvStudentName;
            TextView tvCurrentCourse;
            TextView tvBranchName;
            TextView tvDateTime;
            TextView tvAmountPayInstruction;
            CircularImageView ivStudentImage;
            Button btnConfirm;
            Button btnCancel;

            tvStudentId = (TextView) dialogAttendence.findViewById(R.id.tvStudentId);
            tvStudentName = (TextView) dialogAttendence.findViewById(R.id.tvStudentName);
            tvCurrentCourse = (TextView) dialogAttendence.findViewById(R.id.tvCurrentCourse);
            tvBranchName = (TextView) dialogAttendence.findViewById(R.id.tvBranchName);
            tvDateTime = (TextView) dialogAttendence.findViewById(R.id.tvDateTime);
            tvAmountPayInstruction = (TextView) dialogAttendence.findViewById(R.id.tvAmountPayInstruction);
            btnConfirm = (Button) dialogAttendence.findViewById(R.id.btnConfirm);
            btnCancel = (Button) dialogAttendence.findViewById(R.id.btnCancel);
            ivStudentImage = dialogAttendence.findViewById(R.id.iv_student);

            tvStudentId.setVisibility(View.GONE);
            tvBranchName.setVisibility(View.GONE);
            tvDateTime.setVisibility(View.GONE);

            tvCurrentCourse.setText(""+strSelectedCourseName);
            tvStudentName.setText("" + studentName);
            tvAmountPayInstruction.setText("\nUnpaid Fees: "+strSelectedUnpaidAmount+
            "\n\nFees To Pay: "+edtAmount.getText().toString().trim());


            if (!CommonUtil.isNullString(studentImage)) {
                CommonUtil.setImageToGlide(AddStudentFeesActivity.this, ivStudentImage, studentImage);
            }

            btnConfirm.setText("Confirm");
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogAttendence != null && dialogAttendence.isShowing()) {
                        dialogAttendence.dismiss();
                    }

                    callApiToPostPayment();
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refineViewAfterSuccess();
                    if (dialogAttendence != null && dialogAttendence.isShowing()) {
                        dialogAttendence.dismiss();
                    }
                }
            });

            if (dialogAttendence != null && !dialogAttendence.isShowing()) {
                dialogAttendence.show();
                playSoundForAttendance("" + studentName, AddStudentFeesActivity.this);
            }
        }else{
            Toast.makeText(this, "Invaid fees amount", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Fees amount should be lesser or equal to total unpaid amount", Toast.LENGTH_SHORT).show();
        }
    }

    private void callApiToLoadStudentDetails(String studentId) {
        if (!CommonUtil.isInternetAvailable(AddStudentFeesActivity.this)) {
            playSoundForAttendance("No Internet Connection. Please check your Internet connection.", AddStudentFeesActivity.this);
            return;
        }

        new OkHttpRequest(AddStudentFeesActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_STUDENT_DETAILS,
                RequestParam.studentDetails(studentId,
                        ""+getSharedPrefrencesInstance(AddStudentFeesActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,"")),
                RequestParam.getNull(),
                CODE_STUDENT_DETAILS,
                true, this);
    }

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        Log.e("response:studentList>> ", "" + response);
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
            case CODE_POST_PAYMENT:
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse != null) {
                        if (jsonResponse.has("message")) {
                            if (jsonResponse.getString("status").equalsIgnoreCase("success")) {
                                refineViewAfterSuccess();
                            }
                            Toast.makeText(this, "" + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CODE_STUDENT_DETAILS:
                final Gson gson = new Gson();
                try {
                    ModelStudentDetailsResponse modelStudent = gson
                            .fromJson(response, ModelStudentDetailsResponse.class);

                    if (modelStudent.getStatus().equals(Constants.SUCCESS_CODE)) {
                        strSelectedStudentName = "" + modelStudent.getStudentDetail().getFname()
                                + " " +
                                modelStudent.getStudentDetail().getLname();
                        strSelectedStudentImage = "" + modelStudent.getStudentDetail().getImage();
                        strSelectedCourseName = "" + modelStudent.getStudentDetail().getCourses().get(0).getName();
                        strSelectedUnpaidAmount=""+modelStudent.getStudentDetail().getUnpaid_amount();
                        displayStudentConfirmationDialog(strSelectedStudentName, strSelectedStudentImage, strSelectedCourseName);
                    } else {
                        Toast.makeText(this, "Could not get response.", Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
                        playSoundForAttendance("Please Try Again.", AddStudentFeesActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

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

        return super.onOptionsItemSelected(item);
    }

    private void refineViewAfterSuccess() {
        strSelectedStudentId = "";
        strSelectedStudentName = "";
        strSelectedCourseName = "";
        strSelectedUnpaidAmount="";
        edtStudentId.setText("");
        edtChequeNumber.setText("");
        edtAmount.setText("");
        edtPaymentDate.setText("");

        edtAmount.setError(null);
        edtChequeNumber.setError(null);
        edtStudentId.setError(null);
        edtPaymentDate.setError(null);
        spinnerPaymentMode.setSelection(0);
        linChequeView.setVisibility(View.GONE);
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onStudentRawClick(int position) {
        //Nothing to do here as we have implemented this method from the StudentList Adapter
    }

    @Override
    public void onStudentMoreOptionsClick(int position, ImageView ivMoreOptions) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == CODE_STUDENT_SELECTION_FOR_ATTENDANCE) {
            if (data.getStringExtra(KEY_INTENT_STUDENT_NAME) != null) {
                strSelectedStudentName = "" + data.getStringExtra(KEY_INTENT_STUDENT_NAME);
            }

            if (data.getStringExtra(KEY_INTENT_STUDENT_IMAGE) != null) {
                strSelectedStudentImage = "" + data.getStringExtra(KEY_INTENT_STUDENT_IMAGE);
                CommonUtil.setCircularImageForUser(AddStudentFeesActivity.this,mIvStudent,strSelectedStudentImage);
            }

            if (data.getStringExtra(KEY_INTENT_STUDENT_ID) != null) {
                strSelectedStudentId = "" + data.getStringExtra(KEY_INTENT_STUDENT_ID);
            }
            if (data.getStringExtra(KEY_INTENT_STUDENT_COURSE) != null) {
                strSelectedCourseName = "" + data.getStringExtra(KEY_INTENT_STUDENT_COURSE);
            }
            if (data.getStringExtra(KEY_INTENT_STUDENT_UNPAID_AMOUNT) != null) {
                strSelectedUnpaidAmount = "" + data.getStringExtra(KEY_INTENT_STUDENT_UNPAID_AMOUNT);
            }
            edtStudentId.setText(""+strSelectedStudentId);
            edtAmount.setText(""+strSelectedUnpaidAmount);
        }
    }
}
