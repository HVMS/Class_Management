package com.globalitians.employees.multiple_attendance.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.globalitians.employees.R;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.multiple_attendance.model.MultipleMonthWiseAttendanceReportModelClass;
import com.globalitians.employees.multiple_attendance.model.MultipleStudentsBatchListModelClass;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_STUDENTS_ATTENDANCE_REPORT;
import static com.globalitians.employees.utility.Constants.CODE_STUDENTS_BATCHES;

public class MultiplePieChartActivity extends AppCompatActivity
        implements OnChartValueSelectedListener,
        OkHttpInterface, View.OnClickListener {

    private PieChart chart;
    private CircularImageView mIvstudentimage;
    private CustomTextView mTxtstudentname;
    private String mStrstudentId="";

    private float totalPresent = 0f;
    private float totalAbsent = 0f;
    private float absentPercentage=0f;
    private float presentPercentage=0f;

   /* private String[] parties = new String[]{
            "Android", "Android Java", "C & C++", "Demo App", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };*/

    private String mStrselectedmonth="";
    private String mStrselectedyear="";
    private CustomTextView mTxtwillpicker;

    private MultipleStudentsBatchListModelClass studentsBatchListModelClass;
    private ArrayList<MultipleStudentsBatchListModelClass.BatchDetail.Student> mAlbatchlistofstudents = new ArrayList<>();
    private ArrayList<String> mAlbatcheslist;

    private MultipleMonthWiseAttendanceReportModelClass monthWiseAttendanceReportModelClass;
    private ArrayList<MultipleMonthWiseAttendanceReportModelClass.Attendence> mAlattendancelist;

    private Dialog willPickerDialog = null;
    private CustomTextView mBtncancel;
    private CustomTextView mBtnokay;
    private NumberPicker numberPickermonth;
    private NumberPicker numberPickeryear;
    private String mStrpickermonth="";
    private String mStrpickeryear="";
    private String[] parties;

    private ArrayList<Float> mAlbatchwisepresent;
    private ArrayList<Float> mAlbatchwiseabsent;
    private ArrayList<Float> mAlratiolist;

    private int valueMonthChanged;
    private HashMap<String,Float> hashMap = new HashMap<>();
    private String valueYearChanged="";

    // hashmap for months and years
    private HashMap<Integer,String> hashMap1 = new HashMap<>();
    private int countForPrsent=0;
    private int countForAbsent=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(MultiplePieChartActivity.this);
        setContentView(R.layout.activity_multiple_pie_chart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Overall Attendance");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlackLight));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorBlackLight), PorterDuff.Mode.SRC_ATOP);

        findViews();
        initHashmapOne();
        init();
        getOverAllAttendance();
    }

    private void initHashmapOne() {
        hashMap1.put(0,"Jan");
        hashMap1.put(1,"Feb");
        hashMap1.put(2,"Mar");
        hashMap1.put(3,"Apr");
        hashMap1.put(4,"May");
        hashMap1.put(5,"Jun");
        hashMap1.put(6,"Jul");
        hashMap1.put(7,"Aug");
        hashMap1.put(8,"Sep");
        hashMap1.put(9,"Oct");
        hashMap1.put(10,"Nov");
        hashMap1.put(11,"Dec");
    }

    private void init() {
        mAlbatchlistofstudents = new ArrayList<>();
        mAlbatcheslist = new ArrayList<>();

        mAlbatchwisepresent = new ArrayList<>();
        mAlbatchwiseabsent = new ArrayList<>();
        mAlratiolist = new ArrayList<>();
    }

    private void getOverAllAttendance() {
        Intent intent = getIntent();
        if(intent!=null){
            mStrstudentId = ""+intent.getStringExtra("student_id");

            // call API for month wise report
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            if((month+1)<10){
                mStrselectedmonth = ""+"0"+(month+1);
            }else{
                mStrselectedmonth = ""+(month+1);
            }
            mStrselectedyear = ""+(year);

            // call API for batch list according to students
            callApiToLoadStudentsBaches(mStrstudentId);

            mTxtwillpicker.setText(""+hashMap1.get((month))+ " "+mStrselectedyear);
            callApiToGetMonthWiseReportByDefault(""+mStrstudentId,""+mStrselectedmonth,""+mStrselectedyear);
        }
    }

    private void callApiToGetMonthWiseReportByDefault(String s, String s1, String s2) {
        if(!CommonUtil.isInternetAvailable(MultiplePieChartActivity.this)){
            return;
        }

        new OkHttpRequest(MultiplePieChartActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_STUDENTS_ATTENDANCE_REPORT,
                RequestParam.multiplegetMonthReport(""+s,
                        ""+s1,
                        ""+s2,
                        ""),
                RequestParam.getNull(),
                CODE_STUDENTS_ATTENDANCE_REPORT,
                false,this);
    }

    private void callApiToLoadStudentsBaches(String mStrstudentId) {
        if(!CommonUtil.isInternetAvailable(MultiplePieChartActivity.this)){
            return;
        }

        new OkHttpRequest(MultiplePieChartActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_STUDENTS_BATCHES,
                RequestParam.multiplegetStudentsBatches(""+mStrstudentId),
                RequestParam.getNull(),
                CODE_STUDENTS_BATCHES,
                false,this);
    }

    private void findViews() {
        chart = findViewById(R.id.pie_chart1);
        mIvstudentimage = findViewById(R.id.iv_student);
        mTxtstudentname = findViewById(R.id.tv_student_name);
        mTxtwillpicker = findViewById(R.id.select_will_picker);

        mTxtwillpicker.setOnClickListener(this);
    }

    private void setPieChartStuff() {

        String formattedString = String.format("%.02f",presentPercentage);

        chart.setUsePercentValues(false);
        chart.setCenterText("Overall \n Attendance\n "+formattedString);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(40f);
        chart.setTransparentCircleRadius(43f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        chart.setOnChartValueSelectedListener(this);

        chart.animateY(2000, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(0f);
        l.setYEntrySpace(0f);
        l.setYOffset(-5f);

        // entry label styling
        chart.setEntryLabelColor(getResources().getColor(R.color.my_blue));
        /*chart.setEntryLabelTypeface(tfRegular);*/
        chart.setEntryLabelTextSize(12f);
        setData();
    }

    private void setData() {

        ArrayList<PieEntry> entries = new ArrayList<>();

        for(int i = 0 ; i < parties.length-1 ; i++){
            entries.add(new PieEntry(( mAlratiolist.get(i)),
                    parties[i % parties.length]));
        }

        entries.add(new PieEntry(absentPercentage,parties[ (parties.length-1) % parties.length]));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(11f);
        data.setValueTextColor(getResources().getColor(R.color.my_blue));
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        /*boolean toSet = !chart.isDrawRoundedSlicesEnabled() || !chart.isDrawHoleEnabled();
        chart.setDrawRoundedSlices(toSet);
        if (toSet && !chart.isDrawHoleEnabled()) {
            chart.setDrawHoleEnabled(true);
        }
        if (toSet && chart.isDrawSlicesUnderHoleEnabled()) {
            chart.setDrawSlicesUnderHole(false);
        }*/

        chart.invalidate();
    }

    /*private CharSequence generateCenterSpannableText() {
        SpannableString s = new SpannableString("");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }*/

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        if(response==null){
            return;
        }

        switch (requestId){
            case CODE_STUDENTS_BATCHES:
                Log.e("batches",""+response);
                final Gson batches = new Gson();
                try{
                    mAlbatchlistofstudents = new ArrayList<>();
                    studentsBatchListModelClass = batches.fromJson(response,MultipleStudentsBatchListModelClass.class);

                    if(studentsBatchListModelClass.getBatchDetail().getStudents()!=null
                     && studentsBatchListModelClass.getBatchDetail().getStudents().size()>0){

                        mAlbatchlistofstudents = studentsBatchListModelClass.getBatchDetail().getStudents();

                        setBatches(mAlbatchlistofstudents);
                        setImageAndName(studentsBatchListModelClass);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case CODE_STUDENTS_ATTENDANCE_REPORT:
                Log.e("all report",""+response);
                final Gson gson = new Gson();
                try{
                    monthWiseAttendanceReportModelClass =
                            gson.fromJson(response,MultipleMonthWiseAttendanceReportModelClass.class);

                    if(monthWiseAttendanceReportModelClass.getAttendences()!=null
                     && monthWiseAttendanceReportModelClass.getAttendences().size()>0){

                        mAlattendancelist = monthWiseAttendanceReportModelClass.getAttendences();

                        initializeHashMap();

                        for(int i = 0 ; i < mAlbatcheslist.size() ; i++){
                            for(int j = 0 ; j < mAlattendancelist.size()-1 ; j++){
                                if(mAlbatcheslist.get(i).equalsIgnoreCase(""+mAlattendancelist.get(j).getBatchName()) &&
                                        mAlattendancelist.get(j).getStatus().equalsIgnoreCase("P")){
                                    countForPrsent++;
                                }
                            }
                            mAlbatchwisepresent.add((float)countForPrsent);
                            countForPrsent=0;
                        }

                        for(int i = 0 ; i < mAlbatcheslist.size() ; i++){
                            for(int j = 0 ; j < mAlattendancelist.size()-1 ; j++){
                                if(mAlbatcheslist.get(i).equalsIgnoreCase(""+mAlattendancelist.get(j).getBatchName()) &&
                                        mAlattendancelist.get(j).getStatus().equalsIgnoreCase("A")){
                                    countForAbsent++;
                                }
                            }
                            mAlbatchwiseabsent.add((float)countForAbsent);
                            countForAbsent=0;
                        }

                        for(int i = 0 ; i < mAlbatchwisepresent.size() ; i++){
                            Log.e("log present",""+mAlbatchwisepresent.get(i));
                        }

                        for(int i = 0 ; i < mAlbatchwiseabsent.size() ; i++){
                            Log.e("log absent",""+mAlbatchwiseabsent.get(i));
                        }

                        for(int i = 0 ; i < mAlbatcheslist.size() ; i++){
                            mAlratiolist.add((mAlbatchwisepresent.get(i)/(mAlbatchwisepresent.get(i)+mAlbatchwiseabsent.get(i)))*100);
                            Log.e("ratio temp",""+mAlratiolist.get(i));
                        }

                        // total absent and total present values
                        totalPresent = mAlattendancelist.get(mAlattendancelist.size()-1).getTotalPresent();
                        totalAbsent = mAlattendancelist.get(mAlattendancelist.size()-1).getTotalAbsent();

                        absentPercentage = ( (totalAbsent) / ((totalAbsent+totalPresent)) * 100 );

                        presentPercentage = ( (totalPresent) / ((totalAbsent+totalPresent)) * 100 );

                        Toast.makeText(this, ""+absentPercentage, Toast.LENGTH_SHORT).show();

                        setPieChartStuff();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void initializeHashMap() {
        for(int i = 0 ; i < mAlbatcheslist.size();  i++){
            hashMap.put(""+mAlbatcheslist.get(i),0f);
        }
    }

    private void setBatches(ArrayList<MultipleStudentsBatchListModelClass.BatchDetail.Student> mAlbatchlistofstudents) {

        mAlbatcheslist = new ArrayList<>();

        for(int i = 0 ; i < mAlbatchlistofstudents.size() ; i++){
            if(mAlbatchlistofstudents.get(i).getStatus().equalsIgnoreCase("Running")){
                mAlbatcheslist.add(""+mAlbatchlistofstudents.get(i).getName());
            }
        }

        initializeHashMap();

        parties = new String[mAlbatcheslist.size()+1];

        for(int i = 0 ; i < mAlbatcheslist.size() ; i++){
            Log.e("parties name is ",""+mAlbatcheslist.get(i));
            parties[i] = mAlbatcheslist.get(i);
        }

        parties[mAlbatcheslist.size()] = "Absent";
    }

    private void setImageAndName(MultipleStudentsBatchListModelClass studentsBatchListModelClass) {
        mTxtstudentname.setText(""+studentsBatchListModelClass.getBatchDetail().getFname()+" "+studentsBatchListModelClass.getBatchDetail().getLname());
        if(!CommonUtil.isNullString(""+studentsBatchListModelClass.getBatchDetail().getImg())){
            CommonUtil.setCircularImageForUser(MultiplePieChartActivity.this,mIvstudentimage,""+studentsBatchListModelClass.getBatchDetail().getImg());
        }else{
            mIvstudentimage.setImageResource(R.drawable.ic_user_round);
        }
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.select_will_picker:
                showWillPickerDialog();
                break;
        }
    }

    private void showWillPickerDialog() {
        willPickerDialog = new Dialog(MultiplePieChartActivity.this);
        willPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        willPickerDialog.setContentView(R.layout.will_picker_dialog);
        willPickerDialog.setCancelable(false);

        findViewofdialog(willPickerDialog);

        final String[] month = new String[]{
                "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Sep", "Oct", "Nov", "Dec"
        };

        numberPickermonth.setMinValue(0);
        numberPickermonth.setMaxValue(month.length-1);
        numberPickermonth.setValue(0);
        numberPickermonth.setDisplayedValues(month);

        final String[] year = new String[]{
                "2013","2014","2015","2016","2018","2019","2020","2021","2022","2023","2024"
        };

        numberPickeryear.setMinValue(0);
        numberPickeryear.setMaxValue(year.length-1);
        numberPickeryear.setDisplayedValues(year);

        numberPickeryear.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                valueYearChanged = String.valueOf(numberPickeryear.getValue());
                mStrpickeryear = ""+year[i1];
            }
        });

        numberPickermonth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                valueMonthChanged = Integer.valueOf(numberPickermonth.getValue());
                if((valueMonthChanged+1)<10){
                    valueMonthChanged = valueMonthChanged+1;
                }else{
                    valueMonthChanged = valueMonthChanged;
                }
                mStrpickermonth = ""+month[i1];
            }
        });

        mBtnokay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mTxtwillpicker.setText("" + mStrpickermonth + " " + mStrpickeryear);
                Toast.makeText(MultiplePieChartActivity.this, ""+(valueMonthChanged+1)+" "+mStrpickeryear, Toast.LENGTH_SHORT).show();

                callApiToGetMonthWiseReport(mStrstudentId,""+valueMonthChanged+1,mStrpickeryear);

                if(willPickerDialog!=null && willPickerDialog.isShowing()){
                    willPickerDialog.dismiss();
                }

            }
        });

        mBtncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(willPickerDialog!=null && willPickerDialog.isShowing()){
                    willPickerDialog.dismiss();
                }
            }
        });

        willPickerDialog.show();

    }

    private void callApiToGetMonthWiseReport(String mStrstudentId, String mStrpickermonth, String mStrpickeryear) {
        if(!CommonUtil.isInternetAvailable(MultiplePieChartActivity.this)){
            return;
        }

        new OkHttpRequest(MultiplePieChartActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_STUDENTS_ATTENDANCE_REPORT,
                RequestParam.multiplegetMonthReport(""+mStrstudentId,
                        "0"+valueMonthChanged,
                        ""+mStrpickeryear,
                        ""),
                RequestParam.getNull(),
                CODE_STUDENTS_ATTENDANCE_REPORT,
                false,this);
    }

    private void findViewofdialog(Dialog willPickerDialog) {
        mBtncancel = willPickerDialog.findViewById(R.id.btnCancel);
        mBtnokay = willPickerDialog.findViewById(R.id.btnOkay);

        numberPickermonth = willPickerDialog.findViewById(R.id.numberpickermonth);
        numberPickeryear = willPickerDialog.findViewById(R.id.numberpickeryear);
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
}
