package com.globalitians.employees.customviews

import android.content.Context
import android.graphics.Typeface

class BrandonTextMediumItalic{

    private var instance: BrandonTextMediumItalic? = null
    private var typeface: Typeface? = null

    fun getInstance(context: Context): BrandonTextMediumItalic? {
        synchronized(BrandonTextMediumItalic::class.java) {
            if (instance == null) {
                instance = BrandonTextMediumItalic()
                typeface = Typeface.createFromAsset(context.resources.assets, "BrandonText-MediumItalic.otf")
            }
            return instance
        }
    }

    fun getTypeFace(): Typeface? {
        return typeface
    }

}