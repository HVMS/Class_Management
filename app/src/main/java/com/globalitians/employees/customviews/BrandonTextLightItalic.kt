package com.globalitians.employees.customviews

import android.content.Context
import android.graphics.Typeface

class BrandonTextLightItalic{

    private var instance: BrandonTextLightItalic? = null
    private var typeface: Typeface? = null

    fun getInstance(context: Context): BrandonTextLightItalic? {
        synchronized(BrandonTextLightItalic::class.java) {
            if (instance == null) {
                instance = BrandonTextLightItalic()
                typeface = Typeface.createFromAsset(context.resources.assets, "BrandonText-LightItalic.otf")
            }
            return instance
        }
    }

    fun getTypeFace(): Typeface? {
        return typeface
    }

}