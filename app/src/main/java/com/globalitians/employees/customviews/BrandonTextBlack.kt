package com.globalitians.employees.customviews

import android.content.Context
import android.graphics.Typeface

class BrandonTextBlack {

    private var instance: BrandonTextBlack? = null
    private var typeface: Typeface? = null

    fun getInstance(context: Context): BrandonTextBlack? {
        synchronized(BrandonTextBlack::class.java) {
            if (instance == null) {
                instance = BrandonTextBlack()
                typeface = Typeface.createFromAsset(context.assets, "BrandonText-Black.otf")
            }
            return instance
        }
    }

    fun getTypeFace(): Typeface? {
        return typeface
    }
}