package com.globalitians.employees.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.globalitians.employees.R;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.globalitians.employees.utility.Constants.KEY_IS_ACTION_VOICE;
import static com.globalitians.employees.utility.Constants.LOGIN_PREFRENCES;


public class CommonUtil {

    //public final static Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9]+[_A-Za-z0-9]*(\\.[_A-Za-z0-9]+)*@[A-Za-z0-9]+([-]{0,1})[A-Za-z0-9]+\\.[A-Za-z0-9]+(\\.([A-Za-z]{2,}))?$");
    public final static Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9]+[_A-Za-z0-9-]*(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+\\.[A-Za-z0-9]+(\\.([A-Za-z]{2,}))?$");
    public final static Pattern INVALID_EMAIL_PATTERN = Pattern.compile("^[0-9-]+[_0-9-]*(\\.[_0-9-]+)*@[A-Za-z0-9]+\\.[A-Za-z0-9]+(\\.([A-Za-z]{2,}))?$");

    public final static Pattern PASSWORD_VALIDATION = Pattern.compile("[A-Za-z0-9\\#\\'\\*\\+\\-\\:\\=\\@\\^\\_\\`]+$");
    public final static Pattern WEB_URL_PATTERN = Patterns.WEB_URL;
    public static final Pattern URL_REGEX = Pattern.compile("^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$");
    public final static Pattern NUMERIC_PATTERN = Pattern.compile("^[-+]?[0-9]*\\.?[0-9]+$");
    //    public final static Pattern USER_NAME_PATTERN = Pattern.compile("^[A-Za-z]+[A-Za-z0-9-\\.\\-\\_\\']*$");
//    public final static Pattern USER_NAME_PATTERN = Pattern.compile("[A-Za-z0-9-\\.\\-\\_\\']*$");
    public final static Pattern USER_NAME_PATTERN = Pattern.compile("[A-Za-z0-9]*$");
    //    public final static Pattern FIRST_LAST_NAME_PATTERN = Pattern.compile("^[a-zA-Z]*$");
    public final static Pattern FIRST_LAST_NAME_PATTERN = Pattern.compile("^[A-Za-z]+[a-zA-Z'_.-]*$");
    public final static Pattern ONLY_NUMBERS = Pattern.compile("[0-9]+");
    public final static Pattern ONLY_ALPHABETS = Pattern.compile("[a-zA-Z]+");
    //    public final static Pattern ONLY_SPECIALLS = Pattern.compile("^[*\\-:+'#=@^_`%|<>{}\\[\\]()!$/,.&\\\\~\";?•€£¥₩₹]*$");
    public final static Pattern ONLY_SPECIALLS = Pattern.compile("^[@_#$&!=+-.,~{}\\[\\]()]*$");
    //    public final static Pattern ALL_COMBINATION = Pattern.compile("^(?=.*[a-zA-Z0-9*\\-:+'#=@^_`%|<>{}\\[\\]()!$/,.&\\\\~\";?•€£¥₩₹].*)[a-zA-Z0-9*\\-:+'#=@^_`%|<>{}\\[\\]()!$/,.&\\\\~\";?•€£¥₩₹]{7,}$");
    public final static Pattern ALL_COMBINATION = Pattern.compile("^(?=.*[a-zA-Z0-9@_#$&!=+-.,~{}\\[\\]()].*)[a-zA-Z0-9@_#$&!=+-.,~{}\\[\\]()]*$");
    public final static Pattern ALPHA_SPECIAL = Pattern.compile("^[a-zA-Z@_#$&!=+-.,~{}\\[\\]()]*$");
    public final static Pattern NUMERIC_SPECIAL = Pattern.compile("^[0-9@_#$&!=+-.,~{}\\[\\]()]*$");
    public final static Pattern ALPHA_NUMERIC = Pattern.compile("^[a-zA-Z0-9]*$");


    public static String mStrOtherUserId = "";
    public static TextToSpeech textToSpeechAttendance;

    public static String getOSversion() {
        return Build.VERSION.RELEASE;
    }

    public static String getDeviceName() {
        return Build.MODEL;
    }

    public static String getAppVersion(Context context) {
        PackageInfo pInfo = null;
        String version = "";
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
            LogUtil.i("version", "" + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static ArrayList<String> convertAarryToList(String[] arry) {
        ArrayList<String> data = new ArrayList<String>();
        for (int i = 0; i < arry.length; i++) {
            data.add(arry[i]);
        }
        return data;
    }

    public static void setFullScreenMode(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void setImageToGlide(final Context context, final ImageView imageView, String imageUrl) {
        Glide.with(context).load("" + imageUrl).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        }).into(imageView);
    }

    public static String getStringFromList(List<String> list) {
        String result = "[ ";
        int size = list.size();
        for (int i = 0; i < size; i++) {
            result += list.get(i);
            if (i == size - 1) {
                result += " ]";
            } else {
                result += ", ";
            }
        }
        return result;
    }

    public static void setCircularImageToGlide(final Context context, final CircularImageView imageView, String imageUrl) {
        Glide.with(context).load("" + imageUrl).asBitmap().placeholder(R.drawable.ic_profile_placeholder_dashboard).transform(new CircleTransform(context)).into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    public static void setCircularImageForUser(final Context context, final CircularImageView imageView, String imageUrl) {
        Glide.with(context).load("" + imageUrl).asBitmap().placeholder(R.drawable.ic_students).transform(new CircleTransform(context)).into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    public static boolean isTimeIsAfterCurrentTime(String selectedTime, String currentTime, String format) {
        boolean isValid = false;
        try {
            String string1 = selectedTime;
            //String timeInDeviceTimezone = CommonUtil.convertSToDTZByTimeFormat("UTC", string1, "yyyy-MM-dd HH:mm:ss");
            Date time1 = new SimpleDateFormat(format).parse(string1);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);
            calendar1.add(Calendar.DATE, 1);


            String string2 = currentTime;
            Date time2 = new SimpleDateFormat(format).parse(string2);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            calendar2.add(Calendar.DATE, 1);

            Date x = calendar2.getTime();
            if (x.after(calendar1.getTime())) {
                isValid = true;
            } else {
                isValid = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isValid;
    }

    public static String getPackagename(Context context) {
        return context.getPackageName();
    }


    public static boolean checkSpecialCharacter(String value) {
        Pattern ps = Pattern.compile("^[a-zA-Z ]+$");
        Matcher ms = ps.matcher(value);
        boolean bs = ms.matches();
        if (bs) {
            return true;
        }
        if (!bs) {
            return false;
        }
        return true;
    }

    public static String getCurrentDate() {
        NumberFormat f = new DecimalFormat("00");

        String curDate = "";
        Calendar cal = Calendar.getInstance();

        int thisDate = cal.get(Calendar.DAY_OF_MONTH);
        int thisMonth = cal.get(Calendar.MONTH) + 1;
        int thisYear = cal.get(Calendar.YEAR);

        return curDate = f.format(thisYear) + "/" + f.format(thisMonth) + "/" + f.format(thisDate);
    }

    public static String getCurrentDateInPreferredFormat(SimpleDateFormat simpleDateFormat) {
        Calendar c = Calendar.getInstance();

        return simpleDateFormat.format(c.getTime());
    }

    public static String getDateConversation(String date, DateFormat originalFormat, DateFormat targetFormat) {
        String formattedDate = "";
        try {
            Date d = originalFormat.parse(date);
            formattedDate = targetFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public static String getYesterday(String date, DateFormat originalFormat, DateFormat targetFormat) {
        String formattedDate = "";
        try {
            Date d = originalFormat.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.DAY_OF_YEAR, -1);
            Date newDate = cal.getTime();
            formattedDate = targetFormat.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public static long getMilliSeconds(String dateFormate, String date) {

        SimpleDateFormat formatter = new SimpleDateFormat(dateFormate, Locale.ENGLISH);
        formatter.setLenient(false);
        Date oldDate = null;
        try {
            oldDate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return oldDate.getTime();
    }

    public static SharedPreferences getSharedPrefrencesInstance(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_PREFRENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        return sharedPreferences;
    }

    public static long getMilliSecondsFromCurrentDate(String currentDateFormate, String futureDateFormate, String current, String date) {
        Date futureDate = null;
        Date currentDate = null;

        LogUtil.i("current", "" + current);
        LogUtil.i("date", "" + date);
        try {
            futureDate = new SimpleDateFormat(futureDateFormate, Locale.ENGLISH).parse(date);
            currentDate = new SimpleDateFormat(currentDateFormate, Locale.ENGLISH).parse(current);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millisecondsForFuture = futureDate.getTime();
        long millisecondsForCurrent = currentDate.getTime();
        long millisecondsFromNow = millisecondsForFuture - millisecondsForCurrent - 7200000;
        return millisecondsFromNow;
    }

    public static String convertDeviceToServerTimezone(String serverTimezone, String serverDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("" + serverTimezone));
        Date timezonedate = null;
        try {
            timezonedate = simpleDateFormat.parse("" + serverDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //TimeZone destTz = TimeZone.getTimeZone("yourtimezone");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        String result = simpleDateFormat.format(timezonedate);
        LogUtil.i("result", "" + result);

        return result;
    }

    public static String convertSToDTZByTimeFormat(String serverTimezone, String serverDate,
                                                   String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        Date timezonedate = null;
        try {
            timezonedate = simpleDateFormat.parse("" + serverDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //TimeZone destTz = TimeZone.getTimeZone("yourtimezone");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("" + serverTimezone));
        String result = simpleDateFormat.format(timezonedate);
        LogUtil.i("result", "" + result);

        return result;
    }

    public static String getDateDifference(String targetDate) {
        String dayDifference = "";
        try {
            //Dates to compare
            String CurrentDate = getCurrentDateInPreferredFormat(
                    new SimpleDateFormat("MMMM dd yyyy", Locale.getDefault()));


            Date date1;
            Date date2;

            SimpleDateFormat dates = new SimpleDateFormat("MMMM dd yyyy");

            //Setting dates
            date1 = dates.parse(CurrentDate);
            date2 = dates.parse(targetDate);

            //Comparing dates
            long difference = Math.abs(date1.getTime() - date2.getTime());
            long differenceDates = difference / (24 * 60 * 60 * 1000);

            //Convert long to String
            dayDifference = Long.toString(differenceDates);

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return dayDifference;
    }

    public static String getMilliSecondsBeforeNumberOfHours(String futureDateFormate, String date, String numberOfHour) {
        Date futureDate = null;
        String result = "";

        int hoursToSubstract = stringToInt("" + numberOfHour);

        LogUtil.i("date", "" + date);
        try {
            futureDate = new SimpleDateFormat(futureDateFormate).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(futureDate);
            result = String.format("%02d:%02d", (cal.get(Calendar.HOUR_OF_DAY) - hoursToSubstract), cal.get(Calendar.MINUTE));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean getMilliSecondsAfterTwoHours(String currentDateFormate, String futureDateFormate, String current, String date) {
        Date futureDate = null;
        Date currentDate = null;
        boolean result = false;

        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        /*LogUtil.i("current", "" + current);
        LogUtil.i("date", "" + date);*/
        try {
            currentDate = new SimpleDateFormat(currentDateFormate).parse(current);
            futureDate = new SimpleDateFormat(futureDateFormate).parse(date);

            Calendar currentCal = Calendar.getInstance();
            currentCal.setTime(currentDate);
            currentCal.add(Calendar.HOUR_OF_DAY, 3);
            String result1 = currentCal.get(Calendar.YEAR) + "-" + String.format("%02d", (currentCal.get(Calendar.MONTH) + 1)) + "-" + String.format("%02d", currentCal.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d:%02d", currentCal.get(Calendar.HOUR_OF_DAY), currentCal.get(Calendar.MINUTE));

            Calendar futureCal = Calendar.getInstance();
            futureCal.setTime(futureDate);
            String result2 = futureCal.get(Calendar.YEAR) + "-" + String.format("%02d", (futureCal.get(Calendar.MONTH) + 1)) + "-" + String.format("%02d", futureCal.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d:%02d", futureCal.get(Calendar.HOUR_OF_DAY), futureCal.get(Calendar.MINUTE));

            //LogUtil.i("currentCal Date", "" + currentCal.get(Calendar.HOUR_OF_DAY));
            //LogUtil.i("currentCal Date", "" + currentCal.get(Calendar.AM_PM + Calendar.HOUR));
            LogUtil.i("currentCal Date", "" + result1);
            LogUtil.i("futureCal Date", "" + result2);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm");
            Date date1 = dateFormat.parse(result1);
            Date date2 = dateFormat.parse(result2);

            if (date2.after(date1)) {
                LogUtil.i("CompareDate", "false");
                result = false;
            } else {
                LogUtil.i("CompareDate", "true");
                result = true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static String getLocation(Context context, String latitude, String longitude) {
        String location = "";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            location = "" + address + ", " + state + ", " + country + ", " + postalCode;
            location = location.replace(", null", "");
            location = location.replace("null", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return location;
    }

    public static String getDateFormatedTime(String time, String iHaveFormat, String iWantFormat) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(iHaveFormat);
            Date date = (Date) formatter.parse(time);
            SimpleDateFormat newFormat = new SimpleDateFormat(iWantFormat);
            String finalString = newFormat.format(date);
            return finalString;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getURLFromParams(String url, Map<String, String> params) {
        Iterator entries = params.entrySet().iterator();
        int i = 0;
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if (i == 0) {
                url = url + key + "=" + value;
            } else {
                url = url + "&" + key + "=" + value;
            }
            i++;
        }
        return url;
    }

    public static boolean checkEmail(String email) {
        if (EMAIL_PATTERN.matcher(email).matches() && !INVALID_EMAIL_PATTERN.matcher(email).matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkUserName(String name) {
        return USER_NAME_PATTERN.matcher(name).matches();
    }

    public static boolean checkFirstLastName(String name) {
        return FIRST_LAST_NAME_PATTERN.matcher(name).matches();
    }

    public static boolean checkPassword(String password) {
        return PASSWORD_VALIDATION.matcher(password).matches();
    }

    public static boolean checkURL(String text) {
        return URL_REGEX.matcher(text).matches();
    }

    public static boolean noSpecialChar(String value) {
        Pattern ps = Pattern.compile("^[a-zA-Z_0-9]+$");
        Matcher ms = ps.matcher(value);
        boolean bs = ms.matches();
        if (bs) {
            return true;
        }
        if (!bs) {
            return false;
        }
        return true;
    }

    public static boolean isNumericOnly(String name) {
        return ONLY_NUMBERS.matcher(name).matches();
    }

    public static boolean isAlphabetsOnly(String name) {
        return ONLY_ALPHABETS.matcher(name).matches();
    }

    public static boolean isAllCombination(String name) {
        return ALL_COMBINATION.matcher(name).matches();
    }

    public static boolean isAlphaNumeric(String name) {
        return ALPHA_NUMERIC.matcher(name).matches();
    }

    public static boolean isAlphaSpecial(String name) {
        return ALPHA_SPECIAL.matcher(name).matches();
    }

    public static boolean isNumericSpecial(String name) {
        return NUMERIC_SPECIAL.matcher(name).matches();
    }

    public static boolean isSpecialOnly(String name) {
        return ONLY_SPECIALLS.matcher(name).matches();
    }

    public static boolean isValidUrl(String url) {
        Matcher m = WEB_URL_PATTERN.matcher(url);
        if (m.matches())
            return true;
        else
            return false;
    }

    public static boolean isValidNumeric(String number) {
        boolean isValid = false;
        CharSequence inputStr = number;
        Matcher matcher = NUMERIC_PATTERN.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

//    public static boolean isAnyCharacter(String name) {
//        return ANY_CHARACTER.matcher(name).matches();
//    }

    public static boolean isNullString(String string) {
        try {
            if (string.equalsIgnoreCase("null") || string == null || string.length() < 0 || string.equals("")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static String getJsonString(JSONObject jsonObject, String string) {
        try {
            if (!jsonObject.has("" + string)) {
                return "";
            } else if (jsonObject.getString("" + string) == null || jsonObject.get("" + string).equals("null") || jsonObject.get("" + string).equals("Null") || jsonObject.get("" + string).equals("NULL")) {
                return "";
            } else {
                return "" + jsonObject.getString("" + string);
            }
        } catch (Exception e) {
            return "";
        }
    }

    public static int stringToInt(String str) {
        LogUtil.i("str", "" + str);
        String regexStr = "^[0-9]*$";
        if (str.matches(regexStr)) {
        } else {
            float value = stringToFloat(str);
            LogUtil.i("value", "" + value);
            str = "" + (int) Math.round(value);
        }
        return Integer.parseInt(str);
    }

    public static float stringToFloat(String str) {
        return Float.parseFloat(str);
    }

    public static double stringToDouble(String str) {
        if (CommonUtil.isNullString("" + str)) {
            return 0;
        } else {
            return Double.parseDouble(str);
        }
    }

    public static String cmToInch(String value) {
        DecimalFormat df = new DecimalFormat("#.####");

        String regexStr = "^[0-9]*$";

        if (value.matches(regexStr)) {
            int mMin = CommonUtil.stringToInt(value);

            double mMinIn = mMin / 2.54;

            return df.format(mMinIn);
        } else if (!isNullString(value)) {
            double mMin = CommonUtil.stringToDouble(value);

            double mMinIn = mMin / 2.54;

            return df.format(mMinIn);
        } else {
            return value;
        }
    }

    public static String inchToCm(String value) {
        double temp = CommonUtil.stringToDouble(value) * 2.54;
        Double d = new Double(temp);
        return "" + d.intValue();
    }

    public static String stringToIntForHeight(String value) {
        Double d = new Double(CommonUtil.stringToDouble(value));
        return "" + d.intValue();
    }

    public static String getTwoDigitTime(int string) {
        try {
            if (String.valueOf(string).length() == 1) {
                return "0" + string;
            } else {
                return "" + string;
            }
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectionDetector cd = new ConnectionDetector(context);
        Boolean isInternetPresent = cd.isConnectingToInternet();

        if (!isInternetPresent) {
            //ToastUtil.show(context, context.getResources().getString(R.string.toast_no_internet));
            Toast.makeText(context, "" + context.getResources().getString(R.string.toast_no_internet), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return isInternetPresent;
        }
    }

    public static boolean isInternetAvailableWithoutMessage(Context context) {
        ConnectionDetector cd = new ConnectionDetector(context);
        Boolean isInternetPresent = cd.isConnectingToInternet();

        if (!isInternetPresent) {
//            ToastUtil.show(context, context.getResources().getString(R.string.toast_no_internet));
            return false;
        } else {
            return isInternetPresent;
        }
    }

    public static void showSoftKeyboard(Context context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideSoftKeyboard(Context context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);

            View focusedView = ((Activity) context).getCurrentFocus();

            //If no view is focused, an NPE will be thrown
            if (focusedView != null) {
                inputMethodManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //method to hide with Adjust_resize
    public static void hideSoft_Keyboard(Context context, View focusedView) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);

            //If no view is focused, an NPE will be thrown
            if (focusedView != null) {
                inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getAgeFromBirthDay(String birthDate) {
        int y, m, d, age = 0;
        try {
            Calendar calBirth = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MM / dd / yyyy");
            calBirth.setTime(sdf.parse("" + birthDate));

            GregorianCalendar cal = new GregorianCalendar();
            y = cal.get(Calendar.YEAR);
            m = cal.get(Calendar.MONTH);
            d = cal.get(Calendar.DAY_OF_MONTH);
            cal.set(calBirth.get(Calendar.YEAR), (calBirth.get(Calendar.MONTH)), calBirth.get(Calendar.DAY_OF_MONTH));
            age = y - cal.get(Calendar.YEAR);
            if ((m < cal.get(Calendar.MONTH))
                    || ((m == cal.get(Calendar.MONTH)) && (d < cal
                    .get(Calendar.DAY_OF_MONTH)))) {
                --age;
            }

            LogUtil.e("CommanUtil", "age==>" + age);
            LogUtil.e("CommanUtil", "birthDate==>" + birthDate);
            /*if (age < 0)
                throw new IllegalArgumentException("Age < 0");*/
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return age;
    }

    public static boolean isPhotoCorrupted(File file) {
        boolean isCorrupted = true;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

            if (actualHeight <= 0 || actualWidth <= 0) {
                isCorrupted = true;
            } else {
                isCorrupted = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isCorrupted = true;
        }

        return isCorrupted;
    }

    public static File compressImage(String filePath) {

        File imageFile = new File(filePath);

        if (!imageFile.exists()) {
            return null;
        }

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        //String filename = getFilename();
        try {
            out = new FileOutputStream(imageFile);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return imageFile;

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static long getFileSize(final File file) {
        if (file == null || !file.exists())
            return 0;
        if (!file.isDirectory())
            return file.length();
        final List<File> dirs = new LinkedList<File>();
        dirs.add(file);
        long result = 0;
        while (!dirs.isEmpty()) {
            final File dir = dirs.remove(0);
            if (!dir.exists())
                continue;
            final File[] listFiles = dir.listFiles();
            if (listFiles == null || listFiles.length == 0)
                continue;
            for (final File child : listFiles) {
                result += child.length();
                if (child.isDirectory())
                    dirs.add(child);
            }
        }

        return result;
    }

    public static boolean deleteFile(final Context context, final File fileToDelete) {
        try {
            Uri uri = Uri.fromFile(fileToDelete);
            if (fileToDelete.exists()) {
                File file = new File(uri.getPath());
                file.delete();
                if (file.exists()) {
                    file.getCanonicalFile().delete();
                    if (file.exists()) {
                        context.getApplicationContext().deleteFile(file.getName());
                    }
                }
                LogUtil.i("Common file: ", "isFile exist: " + fileToDelete.exists());

                return !fileToDelete.exists();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static String getUTFEncodedString(String data) {
        byte ptext[] = data.getBytes();
        String value = null;
        try {
            value = new String(ptext, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return value;
    }


    public static void setChatImageToGlide(final Context context, final ImageView imageView, String imageUrl, final ProgressBar p) {
        Glide.with(context).load("" + imageUrl).override(20, 20).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                p.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                p.setVisibility(View.GONE);
                return false;
            }
        }).into(imageView);
    }


    public static String getFullDate(String originaldate) {
        try {

            DateFormat inputFormatter1 = new SimpleDateFormat("yyyy-MM-dd");    //2016-05-24
            Date date1 = inputFormatter1.parse(originaldate);

            DateFormat outputFormatter1 = new SimpleDateFormat("cccc, MMMM dd");    //Wednesday, February 12
            String output1 = outputFormatter1.format(date1);

            return output1;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String get12HourDate(String strDateToConvert) {
        String formattedDate = "";
        try {
            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat targetFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
            Date date = originalFormat.parse(strDateToConvert);
            formattedDate = targetFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public static void playSoundForAttendance(final String strToSpeech, Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(LOGIN_PREFRENCES, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(KEY_IS_ACTION_VOICE, "").equalsIgnoreCase("true")) {
            textToSpeechAttendance = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        textToSpeechAttendance.setLanguage(Locale.UK);
                        textToSpeechAttendance.setSpeechRate(0.9f);
                        textToSpeechAttendance.speak(strToSpeech, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void setWhiteNavigationBar(@NonNull Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            GradientDrawable dimDrawable = new GradientDrawable();
// ...customize your dim effect here

            GradientDrawable navigationBarDrawable = new GradientDrawable();
            navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
            navigationBarDrawable.setColor(Color.WHITE);

            Drawable[] layers = {dimDrawable, navigationBarDrawable};

            LayerDrawable windowBackground = new LayerDrawable(layers);
            windowBackground.setLayerInsetTop(1, metrics.heightPixels);

            window.setBackgroundDrawable(windowBackground);
        }
    }
}
