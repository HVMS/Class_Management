package com.globalitians.employees.branches.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.globalitians.employees.R;
import com.globalitians.employees.branches.adapter.BranchListAdapter;
import com.globalitians.employees.branches.model.ModelBranchList;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.LogUtil;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_BRANCHES;

public class BranchListActivity extends AppCompatActivity implements OkHttpInterface, View.OnClickListener, BranchListAdapter.OnBranchListListener {

    private ShimmerFrameLayout mShimmerContainer;
    private ListView mLvBranches;
    private TextView mTvAddNewBranch;
    private SwipeRefreshLayout mSwipeToRefresh;
    private View mListFooter;

    private ArrayList<ModelBranchList.Branch> mArrListBranches = null;
    private BranchListAdapter mAdapterBranchList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(BranchListActivity.this);
        setContentView(R.layout.activity_branch_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        findViews();
        callApiToLoadBranchList();
    }

    private void init() {
        if (mArrListBranches != null && mArrListBranches.size() > 0) {
            mArrListBranches.clear();
        }
        mArrListBranches = new ArrayList<>();
        mListFooter = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_list_footer, null);
    }

    private void callApiToLoadBranchList() {
        if (!CommonUtil.isInternetAvailable(BranchListActivity.this)) {
            return;
        }

        mShimmerContainer.setVisibility(View.VISIBLE);
        mShimmerContainer.startShimmerAnimation();
        new OkHttpRequest(BranchListActivity.this,
                OkHttpRequest.Method.GET,
                Constants.WS_BRANCH_LIST,
                RequestParam.loadGetRequestsData(),
                RequestParam.getNull(),
                CODE_BRANCHES,
                false, this);
    }

    private void findViews() {
        mShimmerContainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        mShimmerContainer.setVisibility(View.GONE);
        mLvBranches = findViewById(R.id.lv_branchs);
        mTvAddNewBranch = findViewById(R.id.tv_add_new_branch);
        mTvAddNewBranch.setOnClickListener(this);
        mSwipeToRefresh = findViewById(R.id.swipeToRefreshBranchList);
        mSwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mArrListBranches != null && mArrListBranches.size() > 0) {
                    mArrListBranches.clear();
                }
                mArrListBranches = new ArrayList<>();
                callApiToLoadBranchList();
            }
        });
    }

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        LogUtil.e(">>>> RESPONSE BRANCH >>",""+response);

        if (response == null) {
            response = "null";
        }

        switch (requestId) {
            case Constants.CODE_BRANCHES:
                final Gson branchListGson = new Gson();
                try {
                    ModelBranchList modelBranchList = branchListGson
                            .fromJson(response, ModelBranchList.class);

                    if (modelBranchList.getStatus().equals(Constants.SUCCESS_CODE)) {
                        mArrListBranches = modelBranchList.getBranches();
                        if (mArrListBranches != null && mArrListBranches.size() > 0) {
                            setBranchListAdapter();
                        }
                    }
                    if (mSwipeToRefresh.isRefreshing()) {
                        mSwipeToRefresh.setRefreshing(false);
                    }
                    mShimmerContainer.stopShimmerAnimation();
                    mShimmerContainer.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    private void setBranchListAdapter() {
        mAdapterBranchList = new BranchListAdapter(BranchListActivity.this, mArrListBranches,this);
        mLvBranches.setAdapter(mAdapterBranchList);
        if (mLvBranches.getFooterViewsCount() == 0) {
            mLvBranches.addFooterView(mListFooter);
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
}
