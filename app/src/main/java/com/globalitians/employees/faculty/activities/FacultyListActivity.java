package com.globalitians.employees.faculty.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.globalitians.employees.admin.adapter.AssignAccessToFacultyAdapter;
import com.globalitians.employees.admin.model.AssignAccessModel;
import com.globalitians.employees.customviews.CustomButton;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.faculty.adapter.FacultyListAdapter;
import com.globalitians.employees.faculty.model.FacultyAssignedAccessModel;
import com.globalitians.employees.faculty.model.FacultyListModel;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.LogUtil;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.RequiresApi;

import static com.globalitians.employees.utility.CommonUtil.getSharedPrefrencesInstance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_ACCESS_LIST;
import static com.globalitians.employees.utility.Constants.CODE_ASSIGN_ACCESS_TO_FACULTY;
import static com.globalitians.employees.utility.Constants.CODE_DELETE_FACULTY;
import static com.globalitians.employees.utility.Constants.CODE_FACULTY_LIST;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_FACULTY_ID;

public class FacultyListActivity extends AppCompatActivity implements FacultyListAdapter.FacultyClickListener, OkHttpInterface, AssignAccessToEmployeeAdapter.AssignAccessListener, EmployeeAccessItemsAdapter.AccessItemListener, AssignAccessToFacultyAdapter.AssignAccessListener {

    private ArrayList<FacultyListModel.Faculty> mAlFaculty = new ArrayList<>();
    private ArrayList<FacultyListModel.Faculty.Access> mAlAccess = new ArrayList<>();
    private RecyclerView mRvFacultyList;
    private RecyclerView mRvAssignAccess;
    private FacultyListModel mFacultyListModel;
    private AssignAccessModel mAccessListModel;
    private SwipeRefreshLayout mSRFacultyList;
    private FacultyListAdapter mAdapterBatchList;

    private FacultyAssignedAccessModel modelAssignedAccess;
    private AssignAccessToFacultyAdapter mAdapterAssignAcess = null;

    private boolean isFromDashboard = false;

    private BottomSheetDialog mDialogAssignAccess;
    private String mStrClickedFacultyId="";
    private ArrayList<String> mAlAssignedAccess = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(FacultyListActivity.this);
        setContentView(R.layout.activity_faculty_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#E1FFAE"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">Faculties</font>"));*/

        findViews();
        init();
        callApiToLoadFaculties();
        initializeAssignAccessBottomSheet();
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("from")) {
                if (intent.getStringExtra("from").equalsIgnoreCase("dashboard")) {
                    isFromDashboard = true;
                    setTitle("Select Faculty");
                    callApiToGetAccessList();
                }
            }
        }
    }

    private void init() {
        mAlFaculty = new ArrayList<>();
    }

    private void callApiToLoadFaculties() {
        if (!CommonUtil.isInternetAvailable(FacultyListActivity.this)) {
            return;
        }

        new OkHttpRequest(FacultyListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_FACULTY_LIST,
                RequestParam.getFacultyList("" + getSharedPrefrencesInstance(FacultyListActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, "")),
                RequestParam.getNull(),
                CODE_FACULTY_LIST,
                false, this);
    }

    private void callApiToGetAccessList() {
        if (!CommonUtil.isInternetAvailable(FacultyListActivity.this)) {
            return;
        }

        new OkHttpRequest(FacultyListActivity.this,
                OkHttpRequest.Method.GET,
                Constants.WS_ACCESS_LIST,
                RequestParam.getFacultyList("" + getSharedPrefrencesInstance(FacultyListActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, "")),
                RequestParam.getNull(),
                CODE_ACCESS_LIST,
                true, this);
    }

    private void findViews() {
        mRvFacultyList = findViewById(R.id.rv_faculty_list);
        mSRFacultyList = findViewById(R.id.swipeToRefreshFacultyList);
        mSRFacultyList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mSRFacultyList != null && mSRFacultyList.isRefreshing()) {
                    mSRFacultyList.setRefreshing(false);
                }
            }
        });
    }

    private void setadapterForFacultyList(ArrayList<FacultyListModel.Faculty> mAlFaculty) {
        mAdapterBatchList = new FacultyListAdapter(FacultyListActivity.this, mAlFaculty, this);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        mRvFacultyList.setLayoutManager(manager);
        mRvFacultyList.setAdapter(mAdapterBatchList);
    }

    @Override
    public void onFacultyClick(int position) {
        mStrClickedFacultyId = "" + mAlFaculty.get(position).getId();
        if (isFromDashboard) {
            //Open BottomSheet for Selection of Assignning Roles

            /*if(mAlFaculty.get(position).getAccess()!=null && mAlFaculty.get(position).getAccess().size()>0)
            {
                for(int i=0;i<mAlFaculty.size();i++)
                {
                    if(mAlFaculty.get(i).getAccess().get(0).getAssignFor().equalsIgnoreCase("Faculty"))
                    {
                        mAlAccess.add(mAlFaculty.get(position).getAccess().get(i));
                    }
                }
            }*/
            mAlAccess = mAlFaculty.get(position).getAccess();
            setAdapterForAssignAccessList();
            if (mDialogAssignAccess != null && !mDialogAssignAccess.isShowing()) {
                mDialogAssignAccess.show();
            } else {
                mDialogAssignAccess.dismiss();
            }
        } else {
            //onclick of Employee row navigating to Employee Details screen
            Intent intent = new Intent(FacultyListActivity.this, FacultyDetailsActivity.class);
            intent.putExtra(KEY_INTENT_FACULTY_ID, "" + mAlFaculty.get(position).getId());
            startActivity(intent);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDeleteFacultyFromMoreOptions(final int position, ImageView mIvmoreoptions) {
        PopupMenu popupMenu = new PopupMenu(FacultyListActivity.this,mIvmoreoptions);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(FacultyListActivity.this);
        builder.setMessage("Are you Sure ?");
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callApiToDeleteFaculty(""+mAlFaculty.get(position).getId());
                mAlFaculty.remove(position);
                mAdapterBatchList.notifyDataSetChanged();
            }
        }).setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //nothing
            }
            // Create the AlertDialog object and return it
        });

        builder.show();
    }

    private void callApiToDeleteFaculty(String mStrFacultyId) {
        if(!CommonUtil.isInternetAvailable(FacultyListActivity.this)){
            return;
        }

        new OkHttpRequest(FacultyListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_DELETE_FACULTY,
                RequestParam.deleteEmployee(""+mStrFacultyId),
                RequestParam.getNull(),
                CODE_DELETE_FACULTY,
                false,this);
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
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        Log.e("faculty list>>>", "" + response);

        if (response == null) {
            return;
        }

        switch (requestId) {
            case CODE_FACULTY_LIST:
                Log.e("faculty", "" + response);
                final Gson facultyGson = new Gson();
                try {
                    mFacultyListModel = facultyGson.fromJson(response, FacultyListModel.class);
                    if (mFacultyListModel.getFaculties() != null && mFacultyListModel.getFaculties().size() > 0) {
                        mAlFaculty = mFacultyListModel.getFaculties();
                        setadapterForFacultyList(mAlFaculty);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CODE_ASSIGN_ACCESS_TO_FACULTY:
                final Gson assignedAccessGson = new Gson();
                try {
                    modelAssignedAccess = assignedAccessGson.fromJson(response, FacultyAssignedAccessModel.class);
                    Toast.makeText(this, ""+modelAssignedAccess.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CODE_DELETE_FACULTY:
                Log.e("deleted faculty",""+response);
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse != null && jsonResponse.has("status") && jsonResponse.has("message")) {
                        Toast.makeText(this, "" + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        CommonUtil.playSoundForAttendance("" + jsonResponse.getString("message"), FacultyListActivity.this);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    private void initializeAssignAccessBottomSheet() {
        final View mViewAssignAccess = getLayoutInflater().inflate(R.layout.bottom_sheet_assign_access, null);
        mDialogAssignAccess = new BottomSheetDialog(FacultyListActivity.this);
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
                    callApiToAssignAccessToFaculty();
                }
                if (mDialogAssignAccess != null && mDialogAssignAccess.isShowing()) {
                    mDialogAssignAccess.dismiss();
                }
            }
        });
    }

    private void callApiToAssignAccessToFaculty() {
        if (!CommonUtil.isInternetAvailable(FacultyListActivity.this)) {
            return;
        }

        new OkHttpRequest(FacultyListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_ASSIGN_REMOVE_ACCESS_TO_FACULTY,
                RequestParam.assignAccessToFaculty("" + mStrClickedFacultyId, mAlAssignedAccess),
                RequestParam.getNull(),
                CODE_ASSIGN_ACCESS_TO_FACULTY,
                true, this);
    }

    /* access_id_add_update_delete i.e. access_2_1_1_0 */
    private void setAdapterForAssignAccessList() {
        mAdapterAssignAcess = new AssignAccessToFacultyAdapter(FacultyListActivity.this, mAlAccess, this, this);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        mRvAssignAccess.setLayoutManager(manager);
        mRvAssignAccess.setAdapter(mAdapterAssignAcess);
    }

    @Override
    public void onAccessItemClick(int position, int parentPosition, int b) {
        mAlAccess.get(parentPosition).getAccessItems().get(position).setIsActive(b);
    }

    @Override
    public void onAccessSelection(int position, int selectedValue) {
        mAlAccess.get(position).getAccessItems().get(0).setIsActive(selectedValue);
    }
}
