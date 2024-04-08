package com.newolf.widget.banner.config

import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.ColorInt

val Int.dp: Int
    get() {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(), Resources.getSystem().displayMetrics
        ).toInt()
    }


val Float.dp: Int
    get() {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this, Resources.getSystem().displayMetrics
        ).toInt()
    }

/**
 * ======================================================================
 *
 *
 * @author : NeWolf
 * @version : 1.0
 * @since :  2024-04-02
 *
 * =======================================================================
 */
object BannerConfig {

    const val IS_AUTO_LOOP = true
    const val IS_INFINITE_LOOP = true
    const val LOOP_TIME = 3000
    const val SCROLL_TIME = 600
    const val INCREASE_COUNT = 2

    @ColorInt
    const val INDICATOR_NORMAL_COLOR = Color.WHITE

    @ColorInt
    const val INDICATOR_SELECTED_COLOR = Color.WHITE
    @kotlin.jvm.JvmField
    val INDICATOR_NORMAL_WIDTH = 3.dp
    @kotlin.jvm.JvmField
    val INDICATOR_SELECTED_WIDTH = 8.dp
    @kotlin.jvm.JvmField
    val INDICATOR_SPACE: Int = 3.dp
    @kotlin.jvm.JvmField
    val INDICATOR_MARGIN = 8.dp
    @kotlin.jvm.JvmField
    val INDICATOR_HEIGHT = 3.dp
    @kotlin.jvm.JvmField
    val INDICATOR_RADIUS = 3.dp
}