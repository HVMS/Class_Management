package com.globalitians.employees.customviews

import android.content.Context
import android.graphics.Typeface

class BrandonTextMedium{

    private var instance: BrandonTextMedium? = null
    private var typeface: Typeface? = null

    fun getInstance(context: Context): BrandonTextMedium? {
        synchronized(BrandonTextMedium::class.java) {
            if (instance == null) {
                instance = BrandonTextMedium()
                typeface = Typeface.createFromAsset(context.resources.assets, "BrandonText-Medium.otf")
            }
            return instance
        }
    }

    fun getTypeFace(): Typeface? {
        return typeface
    }

}