package com.globalitians.employees.utility;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

public class ResourceUtils {

    public static String getString(@StringRes int stringId) {
        return AppController.getInstance().getString(stringId);
    }

    public static String[] getStringArray(@ArrayRes int stringId) {
        return AppController.getInstance().getResources().getStringArray(stringId);
    }

    public static Drawable getDrawable(@DrawableRes int drawableId) {
        return ContextCompat.getDrawable(AppController.getInstance(), drawableId);
    }

    public static int getColor(@ColorRes int colorId) {
        return AppController.getInstance().getResources().getColor(colorId);
    }

    public static AssetManager getAsset() {
        return AppController.getInstance().getAssets();
    }

    public static int getDimen(@DimenRes int dimenId) {
        return (int) AppController.getInstance().getResources().getDimension(dimenId);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

}
