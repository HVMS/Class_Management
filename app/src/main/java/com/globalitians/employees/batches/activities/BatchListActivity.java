package com.globalitians.employees.batches.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;

import com.globalitians.employees.R;
import com.globalitians.employees.batches.adapter.BatchListAdapter;
import com.globalitians.employees.batches.adapter.FacultyAssignAdapter;
import com.globalitians.employees.batches.model.BatchListModel;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.globalitians.employees.utility.CommonUtil.getSharedPrefrencesInstance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_BATCH_FILTER;
import static com.globalitians.employees.utility.Constants.CODE_BATCH_LIST;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;

public class BatchListActivity extends AppCompatActivity implements BatchListAdapter.BatchClickListener, OkHttpInterface {

    private RecyclerView mRvBatchList;
    private Switch mSwitchBatch;
    private SwipeRefreshLayout mSwipeToRefresh;
    private ArrayList<BatchListModel.Batch> mArrListBatches;
    private ArrayList<BatchListModel.Batch> mArrListFilterBatches;
    private BatchListModel modelBatchList;

    private BottomSheetDialog mDialogFilterBatchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(BatchListActivity.this);
        setContentView(R.layout.activity_batch_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        finViews();
        callApiForBatchList();
    }

    private void finViews() {

        mSwitchBatch=findViewById(R.id.switch_running_completed);
        mRvBatchList=findViewById(R.id.rv_batch_list);
        mSwipeToRefresh=findViewById(R.id.swipeToRefreshBatch);

        mSwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callApiForBatchList();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_employee, menu);
        
        // Retrieve the SearchView and plug it into SearchManager
        //searchView.setMinimumWidth(Integer.MAX_VALUE);

        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);

        MenuItem itemedit = menu.findItem(R.id.action_edit);
        itemedit.setVisible(false);

        MenuItem itemadd = menu.findItem(R.id.action_add);
        itemadd.setVisible(false);

        MenuItem item1 = menu.findItem(R.id.action_settings);
        item1.setVisible(false);

        MenuItem itemFilter = menu.findItem(R.id.action_filter);
        itemFilter.setVisible(true);

        itemFilter.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (mDialogFilterBatchList != null && !mDialogFilterBatchList.isShowing()) {
                    mDialogFilterBatchList.show();
                } else {
                    mDialogFilterBatchList.dismiss();
                }
                return false;
            }
        });
        return true;
    }

    private void setadapterForBatchList() {
        BatchListAdapter mAdapterBatchList = new BatchListAdapter(BatchListActivity.this, mArrListBatches, this);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        mRvBatchList.setLayoutManager(manager);
        mRvBatchList.setAdapter(mAdapterBatchList);
    }

    @Override
    public void onBatchListClick(int position) {
       startActivity(new Intent(BatchListActivity.this,BatchDetailsActivity.class).putExtra("key_batch_id",""+mArrListBatches.get(position).getId()));
    }

    @SuppressLint("NewApi")
    @Override
    public void onMoreOptionsClick(int position, ImageView mivmoreoptions) {
        PopupMenu popupMenu = new PopupMenu(BatchListActivity.this,mivmoreoptions);
        popupMenu.setGravity(Gravity.END);
        popupMenu.inflate(R.menu.batch_options);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.assign_faculty:
                        startActivity(new Intent(BatchListActivity.this,FacultyAccessActivity.class));
                        break;
                    case R.id.assign_students:
                        startActivity(new Intent(BatchListActivity.this,AssignStudentActivity.class));
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
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

    private void callApiForBatchList() {
        if (!CommonUtil.isInternetAvailable(BatchListActivity.this)) {
            return;
        }

        new OkHttpRequest(BatchListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_BATCH_LIST,
                RequestParam.batchlist("" + getSharedPrefrencesInstance(BatchListActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, "")),
                RequestParam.getNull(),
                CODE_BATCH_LIST,
                false, this);
    }

    /*private void callApiFitlerBatchList() {
        if (!CommonUtil.isInternetAvailable(BatchListActivity.this)) {
            return;
        }

        new OkHttpRequest(BatchListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_BATCH_FILTER,
                RequestParam.filterBatchList("" + getSharedPrefrencesInstance(BatchListActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, ""),
                        ""+mSwitchBatch.getText().toString(),
                        ""),
                RequestParam.getNull(),
                CODE_BATCH_FILTER,
                true, this);
    }*/

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        if (response == null) {
            response = "null";
        }

        switch (requestId) {
            case CODE_BATCH_LIST:
                final Gson batchListGson = new Gson();
                try {
                    modelBatchList = batchListGson
                            .fromJson(response, BatchListModel.class);

                    if (modelBatchList.getStatus().equals(Constants.SUCCESS_CODE)) {

                        mArrListBatches=new ArrayList<>();

                        if (modelBatchList.getBatches() != null && modelBatchList.getBatches().size() > 0) {

                            for(int i = 0 ; i < modelBatchList.getBatches().size() ; i++){

                                if(modelBatchList.getBatches().get(i).getStatus().equals("Running")){
                                    mArrListBatches.add(modelBatchList.getBatches().get(i));
                                }

                            }

                            setadapterForBatchList();

                        }

                    }

                    if(mSwipeToRefresh!=null && mSwipeToRefresh.isRefreshing()) {
                        mSwipeToRefresh.setRefreshing(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            /*case CODE_BATCH_FILTER:
                final Gson batchListFilterGson = new Gson();
                try {
                    BatchListModel modelBatchList = batchListFilterGson
                            .fromJson(response, BatchListModel.class);

                    if (modelBatchList.getStatus().equals(Constants.SUCCESS_CODE)) {
                        mArrListFilterBatches=new ArrayList<>();
                        mArrListFilterBatches = modelBatchList.getBatches();

                        if (mArrListFilterBatches != null && mArrListFilterBatches.size() > 0) {
                            setadapterForBatchList();
                        }
                    }
                    if(mSwipeToRefresh!=null && mSwipeToRefresh.isRefreshing())
                    {
                        mSwipeToRefresh.setRefreshing(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;*/
        }
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }
}
