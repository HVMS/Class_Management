package com.globalitians.employees.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by harsh on 14/8/17.
 */

public class TimeUtils {

    private TimeUtils() {
    }

    public static String getTime(long milliseconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return dateFormat.format(new Date(milliseconds));
    }

    public static String getDate(long milliseconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd", Locale.getDefault());
        return dateFormat.format(new Date(milliseconds));
    }

    public static String getHeaderDate(long milliseconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy", Locale.getDefault());
        return dateFormat.format(new Date(milliseconds));
    }

    public static String getDesireHeaderDate(long milliseconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy hh mm a", Locale.getDefault());
        return dateFormat.format(new Date(milliseconds));
    }

    public static long getDateAsHeader(long milliseconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy", Locale.getDefault());
        return Long.parseLong(dateFormat.format(new Date(milliseconds)));
    }


    public static String printDifference(Date startDate, Date endDate) {

        String returnValue = "";

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weekDaysMilli = daysInMilli * 7;
        long monthsInMilli = daysInMilli * 31;
        long yearsInMilli = monthsInMilli * 12;

        long elapsedYear = different / yearsInMilli;
        different = different % yearsInMilli;

        long elapsedMonth = different / monthsInMilli;
        different = different % monthsInMilli;

        long elapsedWeekDays = different / weekDaysMilli;
        different = different % weekDaysMilli;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        if (elapsedYear > 0) {
            if (elapsedYear == 1) {
                returnValue = elapsedYear + " year ago | ";
            } else {
                returnValue = elapsedYear + " years ago | ";
            }

        } else if (elapsedMonth > 0) {
            if (elapsedMonth == 1) {
                returnValue = elapsedMonth + " month ago | ";
            } else {
                returnValue = elapsedMonth + " months ago | ";
            }

        } else if (elapsedWeekDays > 0) {
            if (elapsedWeekDays == 1) {
                returnValue = elapsedWeekDays + " week ago | ";
            } else {
                returnValue = elapsedWeekDays + " weeks ago | ";
            }

        } else if (elapsedDays > 0) {
            if (elapsedDays == 1) {
                returnValue = elapsedDays + " day ago | ";
            } else {
                returnValue = elapsedDays + " days ago | ";
            }

        } else if (elapsedHours > 0) {
            if (elapsedHours == 1) {
                returnValue = elapsedHours + " hour ago | ";
            } else {
                returnValue = elapsedHours + " hours ago | ";
            }

        } else if (elapsedMinutes > 0) {
            if (elapsedMinutes == 1) {
                returnValue = elapsedMinutes + " min ago | ";
            } else {
                returnValue = elapsedMinutes + " mins ago | ";
            }

        } else if ((elapsedSeconds > 0)) {
            if (elapsedSeconds == 1) {
                returnValue = " Now | ";
            } else {
                returnValue = elapsedSeconds + " Secs ago | ";
            }
        }
///////////////////
        if (elapsedYear < 0) {
            if (elapsedYear == -1) {
                returnValue = Math.abs(elapsedYear) + " year to go | ";
            } else {
                returnValue = Math.abs(elapsedYear) + " years to go| ";
            }

        } else if (elapsedMonth < 0) {
            if (elapsedMonth == -1) {
                returnValue = Math.abs(elapsedMonth) + " month to go | ";
            } else {
                returnValue = Math.abs(elapsedMonth) + " months to go | ";
            }

        } else if (elapsedWeekDays < 0) {
            if (elapsedWeekDays == -1) {
                returnValue = Math.abs(elapsedWeekDays) + " week to go | ";
            } else {
                returnValue = Math.abs(elapsedWeekDays) + " weeks to go | ";
            }

        } else if (elapsedDays < 0) {
            if (elapsedDays == -1) {
                returnValue = Math.abs(elapsedDays) + " day to go | ";
            } else {
                returnValue = Math.abs(elapsedDays) + " days to go | ";
            }

        } else if (elapsedHours < 0) {
            if (elapsedHours == -1) {
                returnValue = Math.abs(elapsedHours) + " hour to go | ";
            } else {
                returnValue = Math.abs(elapsedHours) + " hours to go | ";
            }

        } else if (elapsedMinutes < 0) {
            if (elapsedMinutes == -1) {
                returnValue = Math.abs(elapsedMinutes) + " min to go | ";
            } else {
                returnValue = Math.abs(elapsedMinutes) + " mins to go | ";
            }

        } else if (elapsedSeconds < 0) {
            if (elapsedSeconds == -1) {
                returnValue =  " Now | ";
            } else {
                returnValue = Math.abs(elapsedSeconds) + " Secs to go | ";
            }
        }
        return returnValue;
    }
}
