package com.globalitians.employees.customviews

import android.content.Context
import android.graphics.Typeface

class BrandonTextThinItalic{

    private var instance: BrandonTextThinItalic? = null
    private var typeface: Typeface? = null

    fun getInstance(context: Context): BrandonTextThinItalic? {
        synchronized(BrandonTextThinItalic::class.java) {
            if (instance == null) {
                instance = BrandonTextThinItalic()
                typeface = Typeface.createFromAsset(context.resources.assets, "BrandonText-ThinItalic.otf")
            }
            return instance
        }
    }

    fun getTypeFace(): Typeface? {
        return typeface
    }

}