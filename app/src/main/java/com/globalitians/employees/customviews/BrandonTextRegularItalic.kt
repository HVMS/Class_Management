package com.globalitians.employees.customviews

import android.content.Context
import android.graphics.Typeface

class BrandonTextRegularItalic{

    private var instance: BrandonTextRegularItalic? = null
    private var typeface: Typeface? = null

    fun getInstance(context: Context): BrandonTextRegularItalic? {
        synchronized(BrandonTextRegularItalic::class.java) {
            if (instance == null) {
                instance = BrandonTextRegularItalic()
                typeface = Typeface.createFromAsset(context.resources.assets, "BrandonText-RegularItalic.otf")
            }
            return instance
        }
    }

    fun getTypeFace(): Typeface? {
        return typeface
    }

}