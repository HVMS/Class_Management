package com.globalitians.employees;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import com.globalitians.employees.customviews.CustomButton;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.LogUtil;

import java.util.ArrayList;
import java.util.Calendar;

public class TempActivity extends AppCompatActivity implements
        MonthFilterSelectionAdapter.MonthClickListener,
        YearFilterSelectionAdapter.YearClickListener {

    private BottomSheetDialog mDialogFilterPayment;
    private CustomTextView tvOpen;

    private CustomTextView mStartDate;
    private CustomTextView mEndDate;

    private DatePickerDialog startDatePickerDialog = null;
    private DatePickerDialog endDatePickerDialog = null;

    private ArrayList<MonthModel> mAlMonths = new ArrayList<>();
    private ArrayList<YearModel> mAlYears = new ArrayList<>();
    private RecyclerView mRvMonths;
    private RecyclerView mRvYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        initMonthFilterData();
        initYearFilterData();
        initializeFilterPaymentBottomSheet();

        tvOpen = findViewById(R.id.tvShowBottomSheet);
        tvOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogFilterPayment != null && !mDialogFilterPayment.isShowing()) {
                    mDialogFilterPayment.show();
                }
            }
        });
    }

    private void initializeFilterPaymentBottomSheet() {
        View mViewFilterPayment = getLayoutInflater().inflate(R.layout.bottom_sheet_filter_payment, null);
        mDialogFilterPayment = new BottomSheetDialog(TempActivity.this);
        mDialogFilterPayment.setContentView(mViewFilterPayment);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            CommonUtil.setWhiteNavigationBar(mDialogFilterPayment);
        }

        CustomTextView mTitleMonth = mViewFilterPayment.findViewById(R.id.title_month);
        mRvMonths = (RecyclerView) mViewFilterPayment.findViewById(R.id.rv_months);
        setAdapterForMonthFilter();
        CustomTextView mTitleYear = mViewFilterPayment.findViewById(R.id.title_year);
        mRvYear = (RecyclerView) mViewFilterPayment.findViewById(R.id.rv_year);
        setAdapterForYearFilter();
        CustomTextView mTitleStartDate = mViewFilterPayment.findViewById(R.id.title_start_date);
        mStartDate = mViewFilterPayment.findViewById(R.id.tv_start_date);
        CustomTextView mTitleEndDate = mViewFilterPayment.findViewById(R.id.title_end_date);
        mEndDate = mViewFilterPayment.findViewById(R.id.tv_end_date);
        LinearLayout mLlStartDate = mViewFilterPayment.findViewById(R.id.ll_startDate);
        LinearLayout mLlEndDate = mViewFilterPayment.findViewById(R.id.ll_endDate);

        initDatePickers();

        mLlStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDatePickerDialog.show();
            }
        });

        mLlEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDatePickerDialog.show();
            }
        });

        CustomTextView tvCancel = mViewFilterPayment.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogFilterPayment.isShowing() == true) {
                    mDialogFilterPayment.dismiss();
                }
            }
        });

        CustomButton tvSave = mViewFilterPayment.findViewById(R.id.btnApplyFilter);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strSelectedMonths = "";
                String strSelectedYears = "";
                String startDate = "";
                String endDate = "";

                for (int i = 0; i < mAlMonths.size(); i++) {
                    if (mAlMonths.get(i).isSelected) {
                        //strSelectedMonths=strSelectedMonths
                    }
                }

                for (int i = 0; i < mAlYears.size(); i++) {

                }
                if (mDialogFilterPayment.isShowing() == true) {
                    mDialogFilterPayment.dismiss();
                }
            }
        });
    }

    private void initDatePickers() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        if (day < 10) {
            if (month < 10) {
                mStartDate.setText("0" + day + "-0" + (month + 1) + "-" + year);
                mEndDate.setText("0" + day + "-0" + (month + 1) + "-" + year);
            } else {
                mStartDate.setText("0" + day + "-" + (month + 1) + "-" + year);
                mEndDate.setText("0" + day + "-" + (month + 1) + "-" + year);
            }
        } else {
            if (month < 10) {
                mStartDate.setText(day + "-0" + (month + 1) + "-" + year);
                mEndDate.setText(day + "-0" + (month + 1) + "-" + year);
            } else {
                mStartDate.setText(day + "-" + (month + 1) + "-" + year);
                mEndDate.setText(day + "-" + (month + 1) + "-" + year);
            }
        }


        DatePickerDialog.OnDateSetListener startDateListener;
        startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (dayOfMonth < 10) {
                    if (month < 10) {
                        mStartDate.setText("0" + dayOfMonth + "-0" + (month + 1) + "-" + year);
                    } else {
                        mStartDate.setText("0" + dayOfMonth + "-" + (month + 1) + "-" + year);
                    }
                } else {
                    if (month < 10) {
                        mStartDate.setText(dayOfMonth + "-0" + (month + 1) + "-" + year);
                    } else {
                        mStartDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                    }
                }
            }
        };

        DatePickerDialog.OnDateSetListener endDateListener;
        endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (dayOfMonth < 10) {
                    if (month < 10) {
                        mEndDate.setText("0" + dayOfMonth + "-0" + (month + 1) + "-" + year);
                    } else {
                        mEndDate.setText("0" + dayOfMonth + "-" + (month + 1) + "-" + year);
                    }
                } else {
                    if (month < 10) {
                        mEndDate.setText(dayOfMonth + "-0" + (month + 1) + "-" + year);
                    } else {
                        mEndDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                    }
                }
            }
        };

        startDatePickerDialog = new DatePickerDialog(
                TempActivity.this, startDateListener, year, month, day);
        startDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

        endDatePickerDialog = new DatePickerDialog(
                TempActivity.this, endDateListener, year, month, day);
        endDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
    }

    private void initMonthFilterData() {
        mAlMonths = new ArrayList<>();
        MonthModel m1 = new MonthModel("JAN", 1, false);
        MonthModel m2 = new MonthModel("FEB", 2, false);
        MonthModel m3 = new MonthModel("MAR", 3, false);
        MonthModel m4 = new MonthModel("APR", 4, false);
        MonthModel m5 = new MonthModel("MAY", 5, false);
        MonthModel m6 = new MonthModel("JUN", 6, false);
        MonthModel m7 = new MonthModel("JUL", 7, false);
        MonthModel m8 = new MonthModel("AUG", 8, false);
        MonthModel m9 = new MonthModel("SEP", 9, false);
        MonthModel m10 = new MonthModel("OCT", 10, false);
        MonthModel m11 = new MonthModel("NOV", 11, false);
        MonthModel m12 = new MonthModel("DEC", 12, false);

        mAlMonths.add(m1);
        LogUtil.e(">> m1", "" + m1.strMonthName);
        mAlMonths.add(m2);
        LogUtil.e(">> m2", "" + m2.strMonthName);
        mAlMonths.add(m3);
        mAlMonths.add(m4);
        mAlMonths.add(m5);
        mAlMonths.add(m6);
        mAlMonths.add(m7);
        mAlMonths.add(m8);
        mAlMonths.add(m9);
        mAlMonths.add(m10);
        mAlMonths.add(m11);
        mAlMonths.add(m12);
    }

    private void initYearFilterData() {
        mAlYears = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 10; i > 1; i--) {
            YearModel y1 = new YearModel("" + currentYear, false);
            mAlYears.add(y1);
            currentYear -= 1;
        }
    }

    private void setAdapterForMonthFilter() {
        MonthFilterSelectionAdapter mMonthFilterAdapter = new MonthFilterSelectionAdapter(TempActivity.this, mAlMonths, this);
        LinearLayoutManager manager = new LinearLayoutManager(TempActivity.this);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        mRvMonths.setLayoutManager(gridLayoutManager);
        mRvMonths.setAdapter(mMonthFilterAdapter);
    }

    private void setAdapterForYearFilter() {
        YearFilterSelectionAdapter mYearFilterAdapter = new YearFilterSelectionAdapter(TempActivity.this, mAlYears, this);
        LinearLayoutManager manager = new LinearLayoutManager(TempActivity.this);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        mRvYear.setLayoutManager(gridLayoutManager);
        mRvYear.setAdapter(mYearFilterAdapter);
    }

    @Override
    public void onMonthClick(int position, boolean isChecked) {
        mAlMonths.get(position).isSelected = isChecked;
    }

    @Override
    public void onYearClick(int position, boolean isChecked) {
        mAlYears.get(position).isSelected = isChecked;
    }
}
