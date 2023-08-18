package com.globalitians.employees.customviews

import android.content.Context
import android.graphics.Typeface

class BrandonTextThin{

    private var instance: BrandonTextThin? = null
    private var typeface: Typeface? = null

    fun getInstance(context: Context): BrandonTextThin? {
        synchronized(BrandonTextThin::class.java) {
            if (instance == null) {
                instance = BrandonTextThin()
                typeface = Typeface.createFromAsset(context.resources.assets, "BrandonText-Thin.otf")
            }
            return instance
        }
    }

    fun getTypeFace(): Typeface? {
        return typeface
    }

}