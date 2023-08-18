package com.globalitians.employees.utility;


import com.globalitians.employees.R;

public class Constants {

    public static final Boolean IS_COURSE_SUPPORT=false;
    public static final Boolean IS_MULTIPLE_ATTENDANCE_SUPPORT=true;

    public static final String LOGIN_PREFRENCES = "LoginPrefrences";
    public static final String KEY_IS_LOGGED_IN="isLoggedIn";
    public static final String KEY_IS_ACTION_VOICE="isActionVoice";
    public static final String KEY_EMPLOYEE_BRANCH_ID="login_branch_id";
    public static final String KEY_LOGGEDIN_EMPLOYEE_ID="loggedin_user_id";
    public static final String KEY_LOGGEDIN_USER_ID ="loggedin_user_id";
    public static final String KEY_LOGGEDIN_EMPLOYEE_USERNAME="loggedin_user_name";
    public static final String KEY_LOGGEDIN_EMPLOYEE_PASSWORD="loggedin_user_password";
    public static final String KEY_LOGGEDIN_EMPLOYEE_NAME="loggedin_name";
    public static final String KEY_LOGGEDIN_EMPLOYEE_IMAGE="loggedin_image";
    public static final String KEY_LOGGEDIN_EMPLOYEE_ROLE="loggedin_role";
    public static final String KEY_FILTER_BY="filterBy";
    public static final String KEY_OUT_REQUIRED="out_requird";
    public static final String KEY_FILTER_SELECTED_DATE="selected_date";
    public static final String KEY_FILTER_START_DATE="start_date";
    public static final String KEY_FILTER_END_DATE="end_date";
    public static final String KEY_FILTER_MONTH="month";
    public static final String KEY_FILTER_MONTH_YEAR="month_year";
    public static final String KEY_FILTER_YEAR="year";
    public static final String KEY_INTENT_EMPLOYEE_ID="employee_id";

    public static final String KEY_FEES_FILTER_BY="filterBy";
    public static final String KEY_FEES_FILTER_SELECTED_DATE="selected_date";
    public static final String KEY_FEES_FILTER_START_DATE="start_date";
    public static final String KEY_FEES_FILTER_END_DATE="end_date";
    public static final String KEY_FEES_FILTER_MONTH="month";
    public static final String KEY_FEES_FILTER_MONTH_YEAR="month_year";
    public static final String KEY_FEES_FILTER_YEAR="year";


    public static final String BASE_URL="http://globalitians.com/demoapp/json/";
    public static final String SEND_SMS_BASE_URL="http://ptsms1.globalwebsoft.in/vendorsms/pushsms.aspx?";

    public static final String WS_EMPLOYEE_LOGIN=BASE_URL+"loginuser";//loginemployee
    public static final String WS_COURSE_LIST=BASE_URL+"courses";
    public static final String WS_STANDARD_LIST=BASE_URL+"standards";
    public static final String WS_COURSE_DETAILS=BASE_URL+"course_detail";
    public static final String WS_INQUIRIES=BASE_URL+"inquiries";
    public static final String WS_BRANCH_LIST=BASE_URL+"branches";
    public static final String WS_STUDENT_LIST=BASE_URL+"students";
    public static final String WS_POST_INQUIRIES=BASE_URL+"postinquiries";
    public static final String WS_DELETE_INQUIRIES=BASE_URL+"deleteinquiries";
    public static final String WS_DELETE_STUDENT=BASE_URL+"delete_student";
    public static final String WS_STUDENT_DETAILS=BASE_URL+"student_detail";
    public static final String WS_INQUIRY_DETAILS=BASE_URL+"inquiry_detail";
    public static final String WS_DELETE_COURSE=BASE_URL+"delete_course";
    public static final String WS_ADD_COURSE=BASE_URL+"add_course";
    public static final String WS_ADD_STUDENT=BASE_URL+"add_student";
    public static final String WS_POST_ATTENDENCE=BASE_URL+"post_attendence";
    public static final String WS_COURSE_DURATIONS=BASE_URL+"durations";
    public static final String WS_COURSE_CATEGORIES=BASE_URL+"categories";
    public static final String WS_ATTENDANCE_LIST=BASE_URL+"all_attendence_list";
    public static final String WS_STUDENT_FILTER=BASE_URL+"student_filter";
    public static final String WS_OUT_ENTRY=BASE_URL+"out_student";
    public static final String WS_TEST_LIST=BASE_URL+"tests";
    public static final String WS_TEST_DETAILS=BASE_URL+"test_detail";
    public static final String WS_TEST_STUDENTS=BASE_URL+"test_students";
    public static final String WS_ADD_MARKS=BASE_URL+"add_marks";
    public static final String WS_FEES_LIST=BASE_URL+"student_payments";
    public static final String WS_FILTER_PAYMENT=BASE_URL+"filter_payment";
    public static final String WS_POST_PAYMENT=BASE_URL+"post_payment";
    public static final String WS_SEARCH_COURSES=BASE_URL+"search_courses";
    public static final String WS_ADD_BATCH=BASE_URL+"add_batch";
    public static final String WS_BATCH_LIST=BASE_URL+"batches";
    public static final String WS_BATCH_DETAIL=BASE_URL+"batch_detail";
    public static final String WS_BATCH_FILTER=BASE_URL+"batch_filter";
    public static final String WS_PARTNER_LIST=BASE_URL+"partners";
    public static final String WS_FACULTY_LIST=BASE_URL+"faculties";
    public static final String WS_FACULTY_DETAILS=BASE_URL+"faculty_detail";
    public static final String WS_ADD_FACULTY=BASE_URL+"add_faculty";
    public static final String WS_DELETE_FACULTY=BASE_URL+"delete_faculty";
    public static final String WS_ADD_EMPLOYEE=BASE_URL+"add_employee";
    public static final String WS_EMPLOYEE_LIST=BASE_URL+"employees";
    public static final String WS_EMPLOYEE_DETAILS=BASE_URL+"employee_detail";
    public static final String WS_DELETE_EMPLOYEE=BASE_URL+"delete_employee";
    public static final String WS_CHECK_USERNAME=BASE_URL+"check_username";
    public static final String WS_ACCESS_LIST=BASE_URL+"access_list";
    public static final String WS_ADD_NEW_TEST=BASE_URL+"add_test";
    //
    public static final String WS_ASSIGN_STUDENTS = BASE_URL+"assign_student_in_batch";
    public static final String WS_ASSIGN_FACULTIES=BASE_URL+"assign_faculty_in_batch";

    public static final String WS_ADD_HOMEWORK=BASE_URL+"add_homework";
    public static final String WS_HOMEWORK_LIST=BASE_URL+"homeworks";
    public static final String WS_HOMEWORK_DETAILS=BASE_URL+"homework_detail";
    public static final String WS_DELETE_HOMEWORK=BASE_URL+"delete_homework";
    public static final String WS_REMOVE_STUDENTS_HOMEWORK=BASE_URL+"remove_homework_students";
    public static final String WS_TAKE_ATTENDANCE_BATCHWISE=BASE_URL+"add_st_attendence";
    public static final String WS_BATCH_WISE_ATTENDANCE_LIST=BASE_URL+"batch_attendence_list";
    public static final String WS_BATCH_WISE_ATTENDANCE_DETAILS=BASE_URL+"batch_attendence_detail";

    public static final String WS_ASSIGN_REMOVE_ACCESS_TO_EMPLOYEE=BASE_URL+"assign_employee_acces";
    public static final String WS_ASSIGN_REMOVE_ACCESS_TO_FACULTY=BASE_URL+"assign_faculty_acces";

    /*multiple Attendance starts*/
    public static final String WS_MULTIPLE_TAKE_ATTENDANCE_BATCHWISE=BASE_URL+"add_st_attendence";
    public static final String WS_MULTIPLE_BATCH_WISE_ATTENDANCE_LIST=BASE_URL+"batch_attendence_list";
    public static final String WS_MULTIPLE_BATCH_WISE_ATTENDANCE_DETAILS=BASE_URL+"batch_attendence_detail";
    public static final String WS_STUDENTS_BATCHES=BASE_URL+"student_batches";
    public static final String WS_STUDENTS_ATTENDANCE_REPORT=BASE_URL+"student_attendence_list";
    public static final String WS_ALL_STUDENTS_ATTENDANCE_LIST=BASE_URL+"all_student_attendence_list";

    public static final int REQUEST_APP_UPDATE = 9829; // A integer value
    /*multiple Attendance ends*/

    public static String SUCCESS_CODE="success";
    public static final int CODE_COURSE_LIST=1;
    public static final int CODE_COURSE_DETAILS=2;
    public static final int CODE_LOGIN_EMPLOYEE=3;
    public static final int CODE_INQUIRIES=4;
    public static final int CODE_BRANCHES=5;
    public static final int CODE_STUDENTS=6;
    public static final int CODE_POST_INQUIRIES=7;
    public static final int CODE_DELETE_INQUIRIES=8;
    public static final int CODE_DELETE_STUDENT=9;
    public static final int CODE_STUDENT_DETAILS=10;
    public static final int CODE_INQUIRY_DETAILS=11;
    public static final int CODE_DELETE_COURSE=12;
    public static final int CODE_ADD_NEW_COURSE=13;
    public static final int CODE_ADD_NEW_STUDENT=14;
    public static final int CODE_POST_ATTENDENCE=15;
    public static final int CODE_GET_COURSE_DURATIONS =16;
    public static final int CODE_GET_COURSE_CATEGORIES =17;
    public static final int CODE_GET_ATTENDANCE_LIST=18;
    public static final int CODE_SEARCH_STUDENT =19;
    public static final int CODE_SEARCH_COURSES =20;
    public static final int CODE_MAKE_OUT_ENTRY=21;
    public static final int CODE_POST_PAYMENT=22;
    public static final int CODE_GET_ALL_PAYMENT_LIST =23;
    public static final int CODE_GET_STUDENT_PAYMENT_LIST =24;
    public static final int CODE_FILTER_STUDENT =25;
    public static final int CODE_TEST_LIST=26;
    public static final int CODE_TEST_DETAILS=27;
    public static final int CODE_FILTER_PAYMENT=28;
    public static final int CODE_ADD_BATCH=29;
    public static final int CODE_BATCH_LIST=30;
    public static final int CODE_BATCH_DETAILS=31;
    public static final int CODE_BATCH_FILTER=32;
    public static final int CODE_COURSE_SELECTION_FOR_ATTENDANCE=33;
    public static final int CODE_STUDENT_SELECTION_FOR_ATTENDANCE=34;
    public static final int CODE_COURSE_SELECTION_FOR_ADD_STUDENT=35;
    public static final int CODE_PARTNERS_LIST=36;
    public static final int CODE_FACULTY_LIST=37;
    public static final int CODE_ADD_FACULTY=38;
    public static final int CODE_ADD_EMPLOYEE=39;
    public static final int CODE_EMPLOYEE_LIST=40;
    public static final int CODE_CHECK_USERNAME=41;
    public static final int CODE_FACULTY_DETAILS=42;
    public static final int CODE_EMPLOYEE_DETAILS=43;
    public static final int CODE_STANDARD_LIST=44;
    public static final int CODE_ACCESS_LIST=45;
    public static final int CODE_ADD_NEW_TEST=46;
    public static final int CODE_ADD_MARKS_FOR_TEST=47;
    public static final int CODE_TEST_STUDENTS=48;
    public static final int CODE_ASSIGN_STUDENTS=49;
    public static final int CODE_ASSIGN_FACULTIES=50;
    public static final int CODE_REMOVE_FACULTY=51;
    public static final int CODE_REMOVE_STUDENT=52;
    public static final int CODE_ADD_HOMEWORK=53;
    public static final int CODE_HOMEWORK_DETAILS=54;
    public static final int CODE_DELETE_HOMEWORK=55;
    public static final int CODE_HOMEWORK_LIST=56;
    public static final int CODE_REMOVE_STUDENTS_HOMEWORK=57;
    public static final int TAKE_ATTENDANCE_BATCHWISE=58;
    public static final int CODE_BATCH_WISE_ATTENDANCE_LIST=59;
    public static final int CODE_BATCH_WISE_ATTENDANCE_DETAILS=60;
    public static final int CODE_ASSIGN_ACCESS_TO_EMPLOYEE=61;
    public static final int CODE_ASSIGN_ACCESS_TO_FACULTY=62;
    public static final int CODE_DELETE_FACULTY=63;
    public static final int CODE_DELETE_EMPLOYEE=64;

    /*Multiple attendance start*/
    public static final int CODE_MULTIPLE_TAKE_ATTENDANCE_BATCHWISE=63;
    public static final int CODE_MULTIPLE_BATCH_WISE_ATTENDANCE_LIST=64;
    public static final int CODE_MULTIPLE_BATCH_WISE_ATTENDANCE_DETAILS=65;
    public static final int CODE_STUDENTS_BATCHES=66;
    public static final int CODE_STUDENTS_ATTENDANCE_REPORT=67;
    public static final int CODE_ALL_STUDENTS_ATTENDANCE_LIST=68;
    /*Multiple attendance end*/


    public static final String KEY_INTENT_COURSE_ID="course_id";
    public static final String KEY_INTENT_COURSE_NAME="course_name";
    public static final String KEY_INTENT_COURSE_IMAGE="course_image";

    public static final String KEY_INTENT_STUDENT_ID="student_id";
    public static final String KEY_INTENT_STUDENT_NAME="student_name";
    public static final String KEY_INTENT_STUDENT_COURSE="student_course";
    public static final String KEY_INTENT_STUDENT_UNPAID_AMOUNT="student_unpaid_amount";
    public static final String KEY_INTENT_STUDENT_IMAGE="student_image";
    public static final String KEY_INTENT_EMPLOYEE_IMAGE="employee_image";
    public static final String KEY_INTENT_STUDENT_INOUT="student_inout";
    public static final String KEY_INTENT_INQUIRY_ID="inquiry_id";
    public static final String KEY_INTENT_FACULTY_ID="faculty_id";
    public static final String KEY_INQUIRY_DATE="inquiry_date";
    public static final String INTENT_KEY_COURSE_ID="course_id";
    public static final String INTENT_KEY_COURSE_NAME="course_name";
    public static final String KEY_COURSE_SELECTION="course_selection";
    public static final String KEY_VALUE_COURSE_SELECTION_YES="course_selection_yes";
    public static final String KEY_STUDENT_SELECTION="student_selection";
    public static final String KEY_COURSE_SELECTION_TAG="course_selection_tag";
    public static final String KEY_STUDENT_SELECTION_FOR_UNPAID_STUDENT_FEES="student_selection_for_fees";

    public static final int REQUEST_PHONE_CALL = 1999;
    public static final int REQUEST_BATCH_ID = 2451;

    public static int[] ANIMATION_ARRAY = {
            R.anim.layout_animation_up_to_down,
            R.anim.layout_animation_right_to_left,
            R.anim.layout_animation_down_to_up,
            R.anim.layout_animation_left_to_right};

    public static String[] USER_ROLES = {
            "Admin",
            "Employee",
            "Faculty"};

    // Runtime permission code
    public static final int CODE_RUNTIME_LOCATION_PERMISSION = 21;
    public static final int CODE_RUNTIME_STORAGE_PERMISSION = 22;

    // Directory name
    public static String DIRECTORY_NAME = "GLOBAL_IT";
    public static String DIRECTORY_REGISTER_PROFILE = "GLOBAL_IT/GLOBAL_IT_Profile";
    public static String DIRECTORY_SENT_PHOTO = "GLOBAL_IT/GLOBAL_IT_Images/Sent";
    public static String DIRECTORY_RECEIVE_PHOTO = "GLOBAL_IT/GLOBAL_IT_Images";
    public static String DIRECTORY_SENT_VIDEO = "GLOBAL_IT/GLOBAL_IT_Videos/Sent";
    public static String DIRECTORY_RECEIVE_VIDEO = "GLOBAL_IT/GLOBAL_IT_Videos";
    public static String DIRECTORY_FILE_NOMEDIA = ".nomedia";
    public static String DIRECTORY_GIT_MEDIA = "GLOBAL_IT/GLOBAL_IT_Media";


    // Attachment type
    public static String ATTACHMENT_IMAGE = "IMAGE";
    public static String ATTACHMENT_VIDEO = "VIDEO";
    public static String ATTACHMENT_PDF = "PDF";
    public static String ATTACHMENT_IMAGE_VIDEO = "IMAGE_VIDEO_GALLERY";
    public static String KEY_VIDEO_URL="video_url";

    // Attachment body
    public static String IMAGE_ATTACHMENT_BODY = "Photo";
    public static String VIDEO_ATTACHMENT_BODY = "Video";
    public static String VIDEO_FORMATE = ".mp4";

}
