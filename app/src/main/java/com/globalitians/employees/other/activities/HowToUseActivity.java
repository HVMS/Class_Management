package com.globalitians.employees.other.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.globalitians.employees.R;

import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;


public class HowToUseActivity extends AppCompatActivity {
    private TextView tvHowToUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(HowToUseActivity.this);
        setContentView(R.layout.activity_how_to_use);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvHowToUse = findViewById(R.id.tvHowToUse);
        tvHowToUse.setText(Html.fromHtml("<b><h2>How to use Inquiry Module ?</h2></b>" +
                "<p>In Inquiry module you can view list of all the inquiries and filter it according to date and time." +
                "You can also manipulate inquiries i.e. user can also add any new inquiries, edit the added inquiries," +
                "delete the inquiries etc.<br><br><br>" +
                "<b><h2>Managing Students: </h2></b>" +
                "<p>Managing Students involves Add, Update, View and deleting student entries along with certain student details criteria." +
                "<br><br><br>" +
                "<b><h2>Attendance Management: </h2></b>" +
                "<p>Here, You can take student's attendance with the in-time and out-time parameters. For such," +
                "you can use two features in application: 1. Use of GIT Scanner.By Using GIT Scanner, you can scan the predefined " +
                "QR code of the Student's Identity and can make attendance. 2. Taking Manual Attendance: Here, you can take" +
                "student's attendance manually." +
                "<br><br><br>" +
                "<b><h2>Attendance Report: </h2></b>" +
                "<p>You can view all the student's attendace report here and filter it according to date and time. You can also see currently present student entries with different background." +
                "<br><br><br>" +
                "<b><h2>Courses and Branches: </h2></b>" +
                "<p>In Courses, you can see the list of currently running courses. Employee can add a new course," +
                "can edit it and also delete particular course according to need. He/She can also use filter fucntionality if needed." +
                "<p>In Branches, you can see the list of all the branches added. Branches will be manipulated from the Admin panel only." +
                "<br><br><br>" +
                "<b>For more Information about application, You can contact us on:</b><hr>support@globalitians.com</hr><br><br><br>"));
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
