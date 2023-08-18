package com.globalitians.employees.customviews

import android.content.Context
import android.graphics.Typeface

class BrandonTextRegular{

    private var instance: BrandonTextRegular? = null
    private var typeface: Typeface? = null

    fun getInstance(context: Context): BrandonTextRegular? {
        synchronized(BrandonTextRegular::class.java) {
            if (instance == null) {
                instance = BrandonTextRegular()
                typeface = Typeface.createFromAsset(context.resources.assets, "BrandonText-Regular.otf")
            }
            return instance
        }
    }

    fun getTypeFace(): Typeface? {
        return typeface
    }

}