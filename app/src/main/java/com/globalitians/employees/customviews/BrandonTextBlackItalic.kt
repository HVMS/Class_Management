package com.globalitians.employees.customviews

import android.content.Context
import android.graphics.Typeface

class BrandonTextBlackItalic{

    private var instance: BrandonTextBlackItalic? = null
    private var typeface: Typeface? = null

    fun getInstance(context: Context): BrandonTextBlackItalic? {
        synchronized(BrandonTextBlackItalic::class.java) {
            if (instance == null) {
                instance = BrandonTextBlackItalic()
                typeface = Typeface.createFromAsset(context.resources.assets, "BrandonText-BlackItalic.otf")
            }
            return instance
        }
    }

    fun getTypeFace(): Typeface? {
        return typeface
    }

}