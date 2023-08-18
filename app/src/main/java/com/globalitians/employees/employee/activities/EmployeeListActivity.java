package com.globalitians.employees.employee.activities;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.admin.adapter.EmployeeAccessItemsAdapter;
import com.globalitians.employees.admin.adapter.AssignAccessToEmployeeAdapter;
import com.globalitians.employees.admin.model.AssignAccessModel;
import com.globalitians.employees.customviews.CustomButton;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.employee.adapter.EmployeeListAdapter;
import com.globalitians.employees.employee.model.EmployeeAssignedAccessModel;
import com.globalitians.employees.employee.model.ModelClassForEmployeeList;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.LogUtil;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.globalitians.employees.utility.CommonUtil.getSharedPrefrencesInstance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_ASSIGN_ACCESS_TO_EMPLOYEE;
import static com.globalitians.employees.utility.Constants.CODE_DELETE_EMPLOYEE;
import static com.globalitians.employees.utility.Constants.CODE_EMPLOYEE_LIST;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_EMPLOYEE_ID;

public class EmployeeListActivity extends AppCompatActivity
        implements OkHttpInterface,
        EmployeeListAdapter.EmployeeClick, AssignAccessToEmployeeAdapter.AssignAccessListener, EmployeeAccessItemsAdapter.AccessItemListener {

    private ArrayList<ModelClassForEmployeeList.Employee> mAlEmployees = new ArrayList<>();
    private ArrayList<ModelClassForEmployeeList.Employee.Access> mAlAccess = new ArrayList<>();
    private AssignAccessToEmployeeAdapter mAdapterAssignAcess = null;
    private ModelClassForEmployeeList modelClassForEmployeeList;
    private EmployeeAssignedAccessModel modelAssignedAccess;
    private AssignAccessModel mAccessListModel;
    private RecyclerView mRvemployeelist;
    private RecyclerView mRvAssignAccess;
    private SwipeRefreshLayout mSREmployeeList;
    private EmployeeListAdapter employeeListAdapter;

    private boolean isFromDashboard = false;

    private BottomSheetDialog mDialogAssignAccess;

    private ArrayList<String> mAlAssignedAccess = new ArrayList<>();
    private String mStrClickedEmployeeId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(EmployeeListActivity.this);
        setContentView(R.layout.activity_employee_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViews();
        init();
        initializeAssignAccessBottomSheet();
        callApiToLoadEmployeeList();

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("from")) {
                if (intent.getStringExtra("from").equalsIgnoreCase("dashboard")) {
                    isFromDashboard = true;
                    setTitle("Select Employee");
                    //callApiToGetAccessList();
                }
            }
        }
    }

    /*private void callApiToGetAccessList() {
        if (!CommonUtil.isInternetAvailable(EmployeeListActivity.this)) {
            return;
        }

        new OkHttpRequest(EmployeeListActivity.this,
                OkHttpRequest.Method.GET,
                Constants.WS_ACCESS_LIST,
                RequestParam.getFacultyList("" + getSharedPrefrencesInstance(EmployeeListActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, "")),
                RequestParam.getNull(),
                CODE_ACCESS_LIST,
                true, this);
    }*/

    private void callApiToLoadEmployeeList() {
        if (!CommonUtil.isInternetAvailable(EmployeeListActivity.this)) {
            return;
        }

        new OkHttpRequest(EmployeeListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_EMPLOYEE_LIST,
                RequestParam.getEmployeeList("" + getSharedPrefrencesInstance(EmployeeListActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, "")),
                RequestParam.getNull(),
                CODE_EMPLOYEE_LIST,
                true, this);
    }

    private void init() {
        mAlEmployees = new ArrayList<>();
    }

    private void findViews() {
        mRvemployeelist = findViewById(R.id.rv_employee_list);
        mSREmployeeList = findViewById(R.id.swipeToRefreshFacultyList);
        mSREmployeeList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mSREmployeeList != null && mSREmployeeList.isRefreshing()) {
                    mSREmployeeList.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        Log.e("employee list>>>", "" + response);

        if (response == null) {
            return;
        }

        switch (requestId) {
            case CODE_EMPLOYEE_LIST:
                Log.e("employee", "" + response);
                final Gson employeeGson = new Gson();
                try {
                    modelClassForEmployeeList = employeeGson.fromJson(response, ModelClassForEmployeeList.class);
                    if (modelClassForEmployeeList.getEmployees() != null && modelClassForEmployeeList.getEmployees().size() > 0) {
                        mAlEmployees = modelClassForEmployeeList.getEmployees();
                        setadapterForEmployeeList(mAlEmployees);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            /*case CODE_ACCESS_LIST:
                Log.e("access_list >>", "" + response);
                final Gson accessList = new Gson();
                try {
                    mAccessListModel = accessList.fromJson(response, AssignAccessModel.class);
                    if (mAccessListModel.getAccessList() != null && mAccessListModel.getAccessList().size() > 0) {
                        mAlAccess = new ArrayList<>();
                        mAlAccessItems = new ArrayList<>();
                        mAlAccess = mAccessListModel.getAccessList();
                        for (int i = 0; i < mAlAccess.size(); i++) {
                            mAlAccessItems = new ArrayList<>();

                            AccessItems objRead = new AccessItems();
                            objRead.setName("Read");
                            objRead.setSelected(false);

                            AccessItems objAdd = new AccessItems();
                            objAdd.setName("Add");
                            objAdd.setSelected(false);

                            AccessItems objEdit = new AccessItems();
                            objEdit.setName("Edit");
                            objEdit.setSelected(false);

                            AccessItems objDelete = new AccessItems();
                            objDelete.setName("Delete");
                            objDelete.setSelected(false);

                            mAlAccessItems.add(objRead);
                            mAlAccessItems.add(objAdd);
                            mAlAccessItems.add(objEdit);
                            mAlAccessItems.add(objDelete);

                            mAlAccess.get(i).setAccessItems(mAlAccessItems);
                        }

                        LogUtil.e(">>", "" + mAlAccess.get(0).getAccessItems().size());

                        setAdapterForAssignAccessList();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;*/

            case CODE_ASSIGN_ACCESS_TO_EMPLOYEE:
                final Gson assignedAccessGson = new Gson();
                try {
                    modelAssignedAccess = assignedAccessGson.fromJson(response, EmployeeAssignedAccessModel.class);
                    Toast.makeText(this, ""+modelAssignedAccess.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CODE_DELETE_EMPLOYEE:
                Log.e("delete employee",""+response);
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse != null && jsonResponse.has("status") && jsonResponse.has("message")) {
                        Toast.makeText(this, "" + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        CommonUtil.playSoundForAttendance("" + jsonResponse.getString("message"), EmployeeListActivity.this);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setadapterForEmployeeList(ArrayList<ModelClassForEmployeeList.Employee> mAlEmployees) {
        employeeListAdapter = new EmployeeListAdapter(EmployeeListActivity.this, mAlEmployees, this);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        mRvemployeelist.setLayoutManager(manager);
        mRvemployeelist.setAdapter(employeeListAdapter);
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
    public void onEmployeeListClick(int position) {
        mStrClickedEmployeeId = "" + mAlEmployees.get(position).getId();
        if (isFromDashboard) {
            //Open BottomSheet for Selection of Assignning Roles
            mAlAccess = mAlEmployees.get(position).getAccess();
            setAdapterForAssignAccessList();
            if (mDialogAssignAccess != null && !mDialogAssignAccess.isShowing()) {
                mDialogAssignAccess.show();
            } else {
                mDialogAssignAccess.dismiss();
            }
        } else {
            //onclick of Employee row navigating to Employee Details screen
            Intent intent = new Intent(EmployeeListActivity.this, EmployeeDetailsActivity.class);
            intent.putExtra(KEY_INTENT_EMPLOYEE_ID, "" + mAlEmployees.get(position).getId());
            startActivity(intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onMoreOptionsClick(final int position, ImageView mIvmoreoptions) {
        PopupMenu popupMenu = new PopupMenu(EmployeeListActivity.this,mIvmoreoptions);
        popupMenu.setGravity(Gravity.END);
        popupMenu.inflate(R.menu.homework_remove);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.remove_homework:
                        callFirstOfAllSureDialog(position);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void callFirstOfAllSureDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeListActivity.this);
        builder.setMessage("Are you Sure ?");
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callApiToDeleteEmployee(""+mAlEmployees.get(position).getId());
                mAlEmployees.remove(position);
                employeeListAdapter.notifyDataSetChanged();
            }
        }).setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //nothing
            }
            // Create the AlertDialog object and return it
        });

        builder.show();
    }

    private void callApiToDeleteEmployee(String mStrEmployeeId) {

        if(!CommonUtil.isInternetAvailable(EmployeeListActivity.this)){
            return;
        }

        new OkHttpRequest(EmployeeListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_DELETE_EMPLOYEE,
                RequestParam.deleteEmployee(""+mStrEmployeeId),
                RequestParam.getNull(),
                CODE_DELETE_EMPLOYEE,
                false,this);

    }

    private void initializeAssignAccessBottomSheet() {
        final View mViewAssignAccess = getLayoutInflater().inflate(R.layout.bottom_sheet_assign_access, null);
        mDialogAssignAccess = new BottomSheetDialog(EmployeeListActivity.this);
        mDialogAssignAccess.setContentView(mViewAssignAccess);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            CommonUtil.setWhiteNavigationBar(mDialogAssignAccess);
        }
        mRvAssignAccess = mViewAssignAccess.findViewById(R.id.rv_assign_access);
        final CustomTextView mTvCancel = mViewAssignAccess.findViewById(R.id.tv_cancel);
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialogAssignAccess != null && mDialogAssignAccess.isShowing()) {
                    mDialogAssignAccess.dismiss();
                }
            }
        });
        final CustomButton mBtnSave = mViewAssignAccess.findViewById(R.id.btn_save);
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAlAccess != null && mAlAccess.size() > 0) {
                    for (int i = 0; i < mAlAccess.size(); i++) {
                        String strAccess = "access_" + mAlAccess.get(i).getAccessId();
                        for (int j = 0; j < mAlAccess.get(i).getAccessItems().size(); j++) {
                            strAccess = "" + strAccess + "_" + mAlAccess.get(i).getAccessItems().get(j).getIsActive();
                        }
                        mAlAssignedAccess.add(strAccess);
                        LogUtil.e(">>>", "" + strAccess);
                    }
                    callApiToAssignAccessToEmployee();
                }
                if (mDialogAssignAccess != null && mDialogAssignAccess.isShowing()) {
                    mDialogAssignAccess.dismiss();
                }
            }
        });
    }

    private void callApiToAssignAccessToEmployee() {
        if (!CommonUtil.isInternetAvailable(EmployeeListActivity.this)) {
            return;
        }

        new OkHttpRequest(EmployeeListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_ASSIGN_REMOVE_ACCESS_TO_EMPLOYEE,
                RequestParam.assignAccessToEmployee("" + mStrClickedEmployeeId, mAlAssignedAccess),
                RequestParam.getNull(),
                CODE_ASSIGN_ACCESS_TO_EMPLOYEE,
                true, this);
    }

    private void setAdapterForAssignAccessList() {
        mAdapterAssignAcess = new AssignAccessToEmployeeAdapter(EmployeeListActivity.this, mAlAccess, this, this);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        mRvAssignAccess.setLayoutManager(manager);
        mRvAssignAccess.setAdapter(mAdapterAssignAcess);
    }

    @Override
    public void onAccessSelection(int position,int selected) {
        mAlAccess.get(position).getAccessItems().get(0).setIsActive(selected);
    }

    @Override
    public void onAccessItemClick(int position, int parentPosition, int b) {
        mAlAccess.get(parentPosition).getAccessItems().get(position).setIsActive(b);
    }
}
