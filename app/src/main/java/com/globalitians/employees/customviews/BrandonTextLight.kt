package com.gymowner.customviews

import android.content.Context
import android.graphics.Typeface

class BrandonTextLight{

    private var instance: BrandonTextLight? = null
    private var typeface: Typeface? = null

    fun getInstance(context: Context): BrandonTextLight? {
        synchronized(BrandonTextLight::class.java) {
            if (instance == null) {
                instance = BrandonTextLight()
                typeface = Typeface.createFromAsset(context.resources.assets, "BrandonText-Light.otf")
            }
            return instance
        }
    }

    fun getTypeFace(): Typeface? {
        return typeface
    }

}