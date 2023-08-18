package com.globalitians.employees.customviews

import android.content.Context
import android.graphics.Typeface

class BrandonTextBold{

    private var instance: BrandonTextBold? = null
    private var typeface: Typeface? = null

    fun getInstance(context: Context): BrandonTextBold? {
        synchronized(BrandonTextBold::class.java) {
            if (instance == null) {
                typeface = Typeface.createFromAsset(context.resources.assets, "BrandonText-Bold.otf")
            }
            return instance
        }
    }

    fun getTypeFace(): Typeface? {
        return typeface
    }

}