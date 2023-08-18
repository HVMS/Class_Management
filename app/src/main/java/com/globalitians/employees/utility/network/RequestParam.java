package com.globalitians.employees.utility.network;


import android.util.Log;

import com.globalitians.employees.courses.model.AddNewCourseModelWhileAddingNewStudent;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RequestParam {

    private static String TAG = "RequestParam";

    public static Map<String, String> getNull() {
        Map<String, String> mParam = new HashMap<String, String>();
        return mParam;
    }

    public static Map<String, String> loadGetRequestsData() {
        Map<String, String> requestBody = new HashMap<>();
        return requestBody;
    }

    public static Map<String, String> deleteStudent(String id, String branch_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("id", "" + id);
        requestBody.put("branch_id", branch_id);
        return requestBody;
    }

    public static Map<String, String> addNewTest(String testname,
                                                 String date,
                                                 String time,
                                                 String branch_id,
                                                 String edited_by,
                                                 String totalmarks,
                                                 String passing_marks,
                                                 String batch_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("testname", "" + testname);
        requestBody.put("date", date);
        requestBody.put("time", time);
        requestBody.put("branch_id", branch_id);
        requestBody.put("edited_by", edited_by);
        requestBody.put("totalmarks", totalmarks);
        requestBody.put("passing_marks", passing_marks);
        requestBody.put("batch_id", batch_id);
        return requestBody;
    }

    public static Map<String, String> deleteStudentHomework(String homeworkId, String studentId) {
        Map<String, String> reqeustBody = new HashMap<>();
        reqeustBody.put("id", "" + homeworkId);
        reqeustBody.put("remove_student_id", "" + studentId);
        return reqeustBody;
    }

    public static Map<String, String> addHomework(String branchId,
                                                  String batchId,
                                                  String userId,
                                                  String title,
                                                  String description,
                                                  String studentIds,
                                                  String courseId,
                                                  String id) {
        Map<String,String> requestBody = new HashMap<>();
        requestBody.put("branch_id",""+branchId);
        requestBody.put("batch_id",""+batchId);
        requestBody.put("user_id",""+userId);
        requestBody.put("title",""+title);
        requestBody.put("description",""+description);
        requestBody.put("student_id",""+studentIds);
        requestBody.put("course_id","0");

        if(!CommonUtil.isNullString(""+id)){
            requestBody.put("id",""+id);
        }

        return requestBody;
    }

    public static Map<String,File> homeworkimage(ArrayList<File> files){
        Map<String,File> requestBody = new HashMap<>();
        for(int i = 0 ; i < files.size() ; i++){
            Log.e("homework",""+files.get(i));
            requestBody.put("files["+i+"]",files.get(i));
        }
        return requestBody;
    }

    public static Map<String, String> getHomeWorkListBatchWise(String branchId, String batchId) {
        Map<String,String> requestBody = new HashMap<>();
        requestBody.put("branch_id",""+branchId);
        requestBody.put("batch_id",""+batchId);
        return requestBody;
    }

    public static Map<String, String> testDetails(String id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("id", "" + id);
        return requestBody;
    }

    public static Map<String, String> getHomeWorkDetails(String mStrhwid) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("id", "" + mStrhwid);
        return requestBody;
    }

    public static Map<String, String> removeHomeWork(String iD) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("id", "" + iD);
        return requestBody;
    }

    public static Map<String, String> testStudents(String id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("id", "" + id);
        return requestBody;
    }

    public static Map<String, String> removeFaculty(String batch_id, String removedFacultyId) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("batch_id", "" + batch_id);
        requestBody.put("remove_faculty_id", "" + removedFacultyId);
        return requestBody;
    }

    public static Map<String, String> assignFaculties(String batch_id, String facultyIds) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("batch_id", "" + batch_id);
        requestBody.put("faculty_id", "" + facultyIds);
        return requestBody;
    }

    public static Map<String, String> assignStudents(String batch_id, String studentIds) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("batch_id", "" + batch_id);
        requestBody.put("student_id", "" + studentIds);
        return requestBody;
    }

    public static Map<String, String> addStudentMarks(String id,
                                                      ArrayList<String> alMarks,
                                                      String editedby) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("id", "" + id);
        requestBody.put("edited_by", "" + editedby);
        for (int i = 0; i < alMarks.size(); i++) {
            requestBody.put("mark[" + i + "]", alMarks.get(i));
            LogUtil.e(">>marks ", "" + alMarks.get(i));
        }

        LogUtil.e(">> edited by", "" + editedby);
        LogUtil.e(">> id", "" + id);


        return requestBody;
    }

    public static Map<String, String> inquiriesList(String branch_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("branch_id", branch_id);
        return requestBody;
    }

    public static Map<String, String> fees_list(String student_id) {
        Map<String, String> requestBody = new HashMap<>();
        if (!CommonUtil.isNullString(student_id)) {
            requestBody.put("student_id", "" + student_id);
        }
        return requestBody;
    }

    public static Map<String, String> loadAllPaymentList(String date, String month,
                                                         String year, String student_id,
                                                         String start_date, String end_date,
                                                         String branch_id) {
        Map<String, String> requestBody = new HashMap<>();
        if (!CommonUtil.isNullString(date)) {
            requestBody.put("date", date);
        } else if (!CommonUtil.isNullString(month)) {
            requestBody.put("month", month);
            requestBody.put("year", year);
        } else if (!CommonUtil.isNullString(year)) {
            requestBody.put("year", year);
        } else if (!CommonUtil.isNullString(start_date)) {
            requestBody.put("start_date", start_date);
            requestBody.put("end_date", end_date);
        }

        if (!CommonUtil.isNullString(student_id)) {
            requestBody.put("student_id", student_id);
        }

        requestBody.put("branch_id", branch_id);

        return requestBody;
    }

    public static Map<String, String> deleteCourse(String id, String branch_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("id", "" + id);
        requestBody.put("branch_id", branch_id);
        return requestBody;
    }

    public static Map<String, String> deleteInquiries(String slug, String branch_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("slug", "" + slug);
        requestBody.put("branch_id", branch_id);
        return requestBody;
    }

    public static Map<String, String> loadCourseDetails(String course_id, String branch_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("id", "" + course_id);
        requestBody.put("branch_id", branch_id);
        return requestBody;
    }

    public static Map<String, String> studentList(String branch_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("branch_id", branch_id);
        return requestBody;
    }

    public static Map<String, String> filterPayment(
            String branch_id,
            String month,
            String start_date,
            String end_date) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("branch_id", branch_id);
        requestBody.put("month", month);//sample 01-2020
        if (CommonUtil.isNullString(month)) {
            requestBody.put("start_date", start_date);
            requestBody.put("end_date", end_date);

            LogUtil.e("start_date", "" + start_date);
            LogUtil.e("end_date", "" + end_date);

        }

        LogUtil.e("branchId", "" + branch_id);
        LogUtil.e("month", "" + month);
        return requestBody;
    }

    public static Map<String, String> temp() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("partnerid", "gitp1006");
        requestBody.put("password", "123456");
        return requestBody;
    }

    public static Map<String, String> searchFilterStudent(String search, String branch_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("search", search);
        requestBody.put("branch_id", branch_id);
        return requestBody;
    }

    public static Map<String, String> searchCourses(String search) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("search", search);
        return requestBody;
    }

    public static Map<String, String> filterStudent(
            String course_status,
            String course_id,
            String month,
            String search,
            String branch_id,
            String standard_id,
            String batch_id) {
        Map<String, String> requestBody = new HashMap<>();

        if (!CommonUtil.isNullString("" + course_status)) {
            LogUtil.e("<< courses_status", "" + course_status);
            requestBody.put("courses_status", course_status);
        }
        if (!CommonUtil.isNullString("" + course_id)) {
            LogUtil.e("<< course_id", "" + course_id);
            requestBody.put("courses_id", course_id);
        }
        if (!CommonUtil.isNullString("" + month)) {
            LogUtil.e("<< month", "" + month);
            requestBody.put("month", month);
        }
        if (!CommonUtil.isNullString("" + search)) {
            LogUtil.e("<< search", "" + search);
            requestBody.put("search", search);
        }
        if (!CommonUtil.isNullString("" + branch_id)) {
            LogUtil.e("<< branch_id", "" + branch_id);
            requestBody.put("branch_id", branch_id);
        }
        if (!CommonUtil.isNullString("" + standard_id)) {
            LogUtil.e("<< standard_id", "" + standard_id);
            requestBody.put("standard_id", standard_id);
        }
        if (!CommonUtil.isNullString("" + batch_id)) {
            LogUtil.e("<< batch_id", "" + batch_id);
            requestBody.put("batch_id", batch_id);
        }

        return requestBody;
    }


    public static Map<String, String> studentDetails(String id, String branch_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("id", "" + id);
        requestBody.put("branch_id", branch_id);
        return requestBody;
    }

    public static Map<String, String> inquiryDetails(String id, String branch_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("id", "" + id);
        requestBody.put("branch_id", branch_id);
        return requestBody;
    }

    public static Map<String, String> addBatch(String name, String alias, String branch_id, String status, String uid,
                                               String batch_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "" + name);
        requestBody.put("alias", "" + alias);
        requestBody.put("branch_id", "" + branch_id);
        requestBody.put("status", "" + status);
        requestBody.put("edited_by", "" + uid);
        if (!CommonUtil.isNullString("" + batch_id)) {
            requestBody.put("id", "" + batch_id);
        }
        return requestBody;
    }

    public static Map<String, String> removeStudent(String s, String s1) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("batch_id", "" + s);
        requestBody.put("remove_student_id", "" + s1);
        return requestBody;
    }

    public static Map<String, String> batchlist(String branch_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("branch_id", "" + branch_id);
        return requestBody;
    }

    public static Map<String, String> filterBatchList(String branch_id, String status, String search) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("branch_id", "" + branch_id);
        requestBody.put("status", "" + status);
        if (!CommonUtil.isNullString(search)) {
            requestBody.put("search", "" + search);
        }

        return requestBody;
    }

    public static Map<String, String> batchDetails(String batch_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("id", "" + batch_id);
        return requestBody;
    }

    public static Map<String, String> loginEmployee(String username, String password) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", "" + username);
        requestBody.put("password", "" + password);
        return requestBody;
    }

    public static Map<String, String> postAttendance(String student_id,
                                                     String in_out_val,
                                                     String branch_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("student_id", "" + student_id);
        requestBody.put("branch_id", branch_id);
        if (in_out_val.equalsIgnoreCase("in")) {
            requestBody.put("out", "" + "1");
        } else {
            requestBody.put("in", "" + "1");
        }
        return requestBody;
    }

    public static Map<String, String> deleteEmployee(String mStrEmployeeId) {
        Map<String,String> requestBody = new HashMap<>();
        requestBody.put("id",""+mStrEmployeeId);
        return requestBody;
    }

    public static Map<String, String> submitInquiry(String fname,
                                                    String lname,
                                                    String feedback,
                                                    String reference,
                                                    String mobileNo,
                                                    String date,
                                                    String courseId,
                                                    String slug,
                                                    String branch_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("fname", "" + fname);
        requestBody.put("lname", "" + lname);
        requestBody.put("feedback", "" + feedback);
        requestBody.put("reference", "" + reference);
        requestBody.put("mobileno", "" + mobileNo);
        requestBody.put("date", "" + date);
        requestBody.put("course_id", "" + courseId);
        requestBody.put("branch_id", branch_id);
        if (!CommonUtil.isNullString(slug)) {
            requestBody.put("slug", "" + slug);//slug to update inquiry
        }

        return requestBody;
    }

    public static Map<String, String> addNewCourse(String name,
                                                   String cat_id,
                                                   String duration,
                                                   String fees,
                                                   String branch_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", name);
        requestBody.put("category_id", cat_id);
        requestBody.put("duration", duration);
        requestBody.put("fees", fees);
        requestBody.put("branch_id", branch_id);
        return requestBody;
    }

    public static Map<String, String> testList() {
        Map<String, String> requestBody = new HashMap<>();
        return requestBody;
    }

    public static Map<String, String> searchStudent(String strSearch, String branch_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("search", strSearch);
        requestBody.put("branch_id", branch_id);
        return requestBody;
    }


    public static Map<String, String> makeOutEntry(String attendence_id, String time, String branch_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("attendence_id", attendence_id);//17
        requestBody.put("time", time);//10:50
        requestBody.put("branch_id", branch_id);
        return requestBody;
    }

    public static Map<String, String> postPayment(String user_id,
                                                  String date,
                                                  String amount,
                                                  String student_id,
                                                  String payment_type,
                                                  String check_no) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("user_id", user_id);
        requestBody.put("date", date);
        requestBody.put("payment", amount);
        requestBody.put("student_id", student_id);
        requestBody.put("payment_type", payment_type);
        if (payment_type.equalsIgnoreCase("cheque")) {
            requestBody.put("check_no", check_no);
        }
        return requestBody;
    }

    public static Map<String, String> loadAttendenceList(String date, String month,
                                                         String year, String student_id,
                                                         String start_date, String end_date,
                                                         String out_required, String branch_id) {
        Map<String, String> requestBody = new HashMap<>();
        if (!CommonUtil.isNullString(date)) {
            requestBody.put("date", date);
        } else if (!CommonUtil.isNullString(month)) {
            requestBody.put("month", month);
            requestBody.put("year", year);
        } else if (!CommonUtil.isNullString(year)) {
            requestBody.put("year", year);
        } else if (!CommonUtil.isNullString(start_date)) {
            requestBody.put("start_date", start_date);
            requestBody.put("end_date", end_date);
        }

        if (!CommonUtil.isNullString(student_id)) {
            requestBody.put("student_id", student_id);
        }

        if (!CommonUtil.isNullString(out_required)) {
            requestBody.put("out_required", out_required);
        }
        requestBody.put("branch_id", branch_id);

        return requestBody;
    }

    public static Map<String, String> addNewStudent(String studentId,
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
        Map<String, String> requestBody = new HashMap<>();
        if (!CommonUtil.isNullString(studentId)) {
            //EDIT STUDENT
            requestBody.put("id", studentId);
        } else {
            //password and cpassword will pass only at the time of ADD new student
            //not at the time of EDIT student.
            requestBody.put("password", password);
            requestBody.put("cpassword", password);
        }
        requestBody.put("fname", fname);
        requestBody.put("lname", lname);
        requestBody.put("mobileno", mobileNo);
        requestBody.put("dob", birthDate);
        requestBody.put("branch_id", branchId);
        requestBody.put("parentname", parentName);
        requestBody.put("parentmobileno", parentMobile);
        requestBody.put("reference", referenceBy);
        requestBody.put("joining_date", joiningDate);
        requestBody.put("address", address);
        requestBody.put("email", email);
        requestBody.put("installment_date", "15-10-2019");
        for (int i = 0; i < listStudentCourseData.size(); i++) {
            requestBody.put("fees[" + i + "]", listStudentCourseData.get(i).getStrFees());
            requestBody.put("course_id[" + i + "]", listStudentCourseData.get(i).getStrCourseId());
            requestBody.put("duration[" + i + "]", listStudentCourseData.get(i).getStrDuration());
            requestBody.put("course_status[" + i + "]", listStudentCourseData.get(i).getStrCourseStatus());
            requestBody.put("certificate[" + i + "]", listStudentCourseData.get(i).getStrCertificate());
            requestBody.put("book_material[" + i + "]", listStudentCourseData.get(i).getStrBookMaterial());
            requestBody.put("bag[" + i + "]", listStudentCourseData.get(i).getStrBag());
        }
        return requestBody;
    }

    public static Map<String, File> addNewCourseImageParam(File fileCourseImage,
                                                           File fileCoursePdf) {
        Map<String, File> requestBody = new HashMap<>();

        if (fileCourseImage != null) {
            requestBody.put("picfile", fileCourseImage);
        }

        if (fileCoursePdf != null) {
            requestBody.put("syllabusfile", fileCoursePdf);
        }
        //requestBody.put("branch_id",branch_id);

        return requestBody;
    }

    public static Map<String, File> addNewStudentImageParam(ArrayList<File> file) {
        Map<String, File> requestBody = new HashMap<>();
        for (int i = 0; i < file.size(); i++) {
            Log.e(">>> register >>", "" + file.get(i));
            requestBody.put("profileimage", file.get(i));
            //requestBody.put("register_user_photo" + i, file.get(i));
        }
        return requestBody;
    }

    /*
     * http://ptsms1.globalwebsoft.in/vendorsms/pushsms.aspx?user=abc&password=xyz&msisdn=919898xxxxxx,919898xxxxxx&sid=SenderId&msg=test%20message&fl=0&gwid=2
     *
     * */
    public static Map<String, String> sendSMS() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("user", "globalitinfosolution");//17
        requestBody.put("password", "globalitinfosolution123");//10:50
        requestBody.put("msisdn", "+919408776491,+918200893880");
        requestBody.put("sid", "GITINF");
        requestBody.put("msg", "This is test message dude.");
        requestBody.put("f1", "0");
        requestBody.put("gwid", "2");
        return requestBody;
    }


    public static Map<String, String> getFacultyList(String branch_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("branch_id", "" + branch_id);
        return requestBody;
    }

    public static Map<String, String> getEmployeeList(String branch_id) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("branch_id", "" + branch_id);
        return requestBody;
    }

    public static Map<String, String> assignAccessToEmployee(String employee_id, ArrayList<String> alAccess) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("employee_id", "" + employee_id);
        for (int i = 0; i < alAccess.size(); i++) {
            requestBody.put("access[" + i + "]", "" + alAccess.get(i));
        }
        return requestBody;
    }

    public static Map<String, String> assignAccessToFaculty(String faculty_id, ArrayList<String> alAccess) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("faculty_id", "" + faculty_id);
        for (int i = 0; i < alAccess.size(); i++) {
            requestBody.put("access[" + i + "]", "" + alAccess.get(i));
        }
        return requestBody;
    }

    public static Map<String, String> addFaculty(String branch_id,
                                                 String name,
                                                 String username,
                                                 String password,
                                                 String dob,
                                                 String address,
                                                 String job_type,
                                                 String salary,
                                                 String contactno,
                                                 String edited_by,
                                                 String joiningDate,
                                                 String email,
                                                 String designation,
                                                 String courses_id,
                                                 String standard_id,
                                                 String id) {
        Map<String, String> requestBody = new HashMap<>();

        if (CommonUtil.isNullString("" + id)) {
            requestBody.put("username", "" + username);
            requestBody.put("password", "" + password);
        }
        requestBody.put("branch_id", "" + branch_id);
        requestBody.put("name", "" + name);
        requestBody.put("dob", "" + dob);
        requestBody.put("address", "" + address);
        requestBody.put("job_type", "" + job_type);
        requestBody.put("salary", "" + salary);
        requestBody.put("contact_no", "" + contactno);
        requestBody.put("edited_by", "" + edited_by);
        requestBody.put("joining_date", "" + joiningDate);
        requestBody.put("email", "" + email);
        requestBody.put("designation", "" + designation);
        requestBody.put("course_id", "" + courses_id);
        requestBody.put("standard_id", "" + standard_id);
        requestBody.put("id", "" + id);
        return requestBody;
    }

    public static Map<String, String> addEmployee(String branch_id,
                                                  String name,
                                                  String dob,
                                                  String address,
                                                  String job_type,
                                                  String salary,
                                                  String contactno,
                                                  String edited_by,
                                                  String joiningDate,
                                                  String email,
                                                  String designation,
                                                  String username,
                                                  String password,
                                                  String id) {
        Map<String, String> requestBody = new HashMap<>();
        if (CommonUtil.isNullString("" + id)) {
            requestBody.put("username", "" + username);
            requestBody.put("password", "" + password);
        }
        requestBody.put("branch_id", "" + branch_id);
        requestBody.put("name", "" + name);
        requestBody.put("dob", "" + dob);
        requestBody.put("address", "" + address);
        requestBody.put("job_type", "" + job_type);
        requestBody.put("salary", "" + salary);
        requestBody.put("contact_no", "" + contactno);
        requestBody.put("edited_by", "" + edited_by);
        requestBody.put("joining_date", "" + joiningDate);
        requestBody.put("email", "" + email);
        requestBody.put("designation", "" + designation);
        requestBody.put("id", "" + id);
        return requestBody;
    }

    public static Map<String, String> checkUsername(String username) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", "" + username);
        return requestBody;
    }

    public static Map<String, String> getFacultyDetails(String strFacultyId) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("id", "" + strFacultyId);
        return requestBody;
    }

    public static Map<String, File> homeWorkPdf(ArrayList<File> file) {
        Map<String, File> requestBody = new HashMap<>();
        for (int i = 0; i < file.size(); i++) {
            Log.e(">>>homework file>>>", "" + file.get(i));
            requestBody.put("files[" + i + "]", file.get(i));
        }
        return requestBody;
    }

    /*Multiple Attendance start*/
    public static Map<String, String> multiplegetAttendanceDetails(String attendanceId, String date) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("id", "" + attendanceId);
        requestBody.put("date", "" + date);
        return requestBody;
    }


    public static Map<String, String> multipletakeAttendance(String batchId, String presentId, String absentId) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("batch_id", "" + batchId);

        if (!CommonUtil.isNullString("" + presentId)) {
            requestBody.put("present", "" + presentId);
        }

        if (!CommonUtil.isNullString("" + absentId)) {
            requestBody.put("absent", "" + absentId);
        }

        return requestBody;
    }

    public static Map<String, String> multiplegetAttendanceListBatchWise(String branchId, String date) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("branch_id", "" + branchId);
        requestBody.put("date", "" + date);
        return requestBody;
    }


    public static Map<String, String> multiplegetStudentsBatches(String studentId) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("id", "" + studentId);
        return requestBody;
    }

    public static Map<String, String> multiplegetMonthReport(String strStudentid, String strMonth, String strYear, String strBatchId) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("student_id", "" + strStudentid);
        requestBody.put("month", "" + strMonth);
        requestBody.put("year", "" + strYear);
        if (!CommonUtil.isNullString("" + strBatchId)) {
            requestBody.put("batch_id", "" + strBatchId);
        }
        return requestBody;
    }

    public static Map<String, String> multiplegetAllStudentsAttendanceDateWise(String branchId, String date) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("branch_id", "" + branchId);
        requestBody.put("date", ""  + date);
        return requestBody;
    }

    public static Map<String, String> multiplesearchStudentInAttendanceReport(String branchId, String date, String search) {
        Map<String,String> requestBody = new HashMap<>();
        requestBody.put("branch_id",""+branchId);
        requestBody.put("date",""+date);
        requestBody.put("search",""+search);
        return requestBody;
    }
    /*Multiple Attendance END*/
}