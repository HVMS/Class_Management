package com.globalitians.employees.customviews
import android.content.Context
import android.graphics.Typeface

class BrandonTextBoldItalic{

//    private var instance: BrandonTextBoldItalic? = null
//    private var typeface: Typeface? = null

   /* fun getInstance(context: Context): BrandonTextBoldItalic? {
        synchronized(BrandonTextBoldItalic::class.java) {
            if (instance == null) {
                instance = BrandonTextBoldItalic()
                typeface = Typeface.createFromAsset(context.resources.assets, "BrandonText-BoldItalic.otf")
            }
            return instance
        }
    }

    fun getTypeFace(): Typeface? {
        return typeface
    }
*/

    private var instance: BrandonTextBoldItalic? = null
    private var typeface: Typeface? = null

    fun getInstance(context: Context): BrandonTextBoldItalic? {
        synchronized(BrandonTextBoldItalic::class.java) {
            if (instance == null) {
                instance = BrandonTextBoldItalic()
                typeface = Typeface.createFromAsset(
                    context.assets,
                    "brandontextbolditalic.otf"
                )
            }
            return instance
        }
    }

    fun getTypeFace(): Typeface? {
        return typeface
    }
}