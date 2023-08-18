package com.globalitians.employees.customviews

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.support.v7.widget.AppCompatRadioButton
import android.util.AttributeSet
import com.globalitians.employees.R
import com.globalitians.employees.customviews.CustomEnumBrandonNew

class CustomRadioButton : AppCompatRadioButton {

    constructor(context: Context) : this(context, null) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, android.R.attr.radioButtonStyle) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
//        typeface = BrandonTextBoldItalic().getInstance(context!!)?.getTypeFace()
//        val myTypeface =
//            Typeface.createFromAsset(context.assets, "BrandonText-Black.otf")
//        typeface = myTypeface
        CustomRadioButton(context, attrs, 0)
    }

    /**
     * @param context:This is an abstract class whose implementation is provided by Android Operating System.
     * @param attrs:A collection of attributes, as found associated with a tag in an XML document.
     * @param defStyle:
     */
    fun CustomRadioButton(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
    ) {
        try {
            val a: TypedArray =
                context.obtainStyledAttributes(attrs, R.styleable.CustomRadioButton, defStyle, android.R.attr.radioButtonStyle)
            val customEnumValue: CustomEnumBrandonNew.CustomFontType =
                CustomEnumBrandonNew.CustomFontType.fromId(
                    a.getInt(
                        R.styleable.CustomRadioButton_font_type,
                        0
                    )
                )
            a.recycle()
            typeface = when (customEnumValue) {
                CustomEnumBrandonNew.CustomFontType.BLACK ->
                    Typeface.createFromAsset(
                        context.assets,
                        "BrandonText-Black.otf"
                    ) //BrandonTextBlack().getInstance(context)?.getTypeFace()
                CustomEnumBrandonNew.CustomFontType.BLACK_ITALIC ->
                    Typeface.createFromAsset(
                        context.assets,
                        "BrandonText-BlackItalic.otf"
                    ) //BrandonTextBlackItalic().getInstance(context)?.getTypeFace()
                CustomEnumBrandonNew.CustomFontType.BOLD ->
                    Typeface.createFromAsset(
                        context.assets,
                        "BrandonText-Bold.otf"
                    ) //BrandonTextBold().getInstance(context)?.getTypeFace()
                CustomEnumBrandonNew.CustomFontType.BOLD_ITALIC ->
                    Typeface.createFromAsset(
                        context.assets,
                        "BrandonText-BoldItalic.otf"
                    ) //BrandonTextBoldItalic().getInstance(context)?.getTypeFace()
                CustomEnumBrandonNew.CustomFontType.LIGHT ->
                    Typeface.createFromAsset(
                        context.assets,
                        "BrandonText-Light.otf"
                    ) //BrandonTextBoldItalic().getInstance(context)?.getTypeFace()
                CustomEnumBrandonNew.CustomFontType.LIGHT_ITALIC ->
                    Typeface.createFromAsset(
                        context.assets,
                        "BrandonText-LightItalic.otf"
                    ) //BrandonTextBoldItalic().getInstance(context)?.getTypeFace()
                CustomEnumBrandonNew.CustomFontType.MEDIUM ->
                    Typeface.createFromAsset(
                        context.assets,
                        "BrandonText-Medium.otf"
                    ) //BrandonTextBoldItalic().getInstance(context)?.getTypeFace()
                CustomEnumBrandonNew.CustomFontType.MEDIUM_ITALIC ->
                    Typeface.createFromAsset(
                        context.assets,
                        "BrandonText-MediumItalic.otf"
                    ) //BrandonTextBoldItalic().getInstance(context)?.getTypeFace()
                CustomEnumBrandonNew.CustomFontType.REGULAR ->
                    Typeface.createFromAsset(
                        context.assets,
                        "BrandonText-Regular.otf"
                    ) //BrandonTextBoldItalic().getInstance(context)?.getTypeFace()
                CustomEnumBrandonNew.CustomFontType.REGULAR_ITALIC ->
                    Typeface.createFromAsset(
                        context.assets,
                        "BrandonText-RegularItalic.otf"
                    ) //BrandonTextBoldItalic().getInstance(context)?.getTypeFace()
                CustomEnumBrandonNew.CustomFontType.THIN ->
                    Typeface.createFromAsset(
                        context.assets,
                        "BrandonText-Thin.otf"
                    ) //BrandonTextBoldItalic().getInstance(context)?.getTypeFace()
                CustomEnumBrandonNew.CustomFontType.THIN_ITALIC ->
                    Typeface.createFromAsset(
                        context.assets,
                        "BrandonText-ThinItalic.otf"
                    ) //BrandonTextBoldItalic().getInstance(context)?.getTypeFace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}