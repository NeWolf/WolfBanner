package com.newolf.widget.banner.indicator

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.newolf.logutils.LogUtils
import com.newolf.widget.banner.config.IndicatorConfig

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
open class BaseIndicator(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr), IIndicator {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    protected val config: IndicatorConfig by lazy { IndicatorConfig() }
    protected val mPaint: Paint by lazy { Paint() }

    init {
        mPaint.isAntiAlias = true
        mPaint.setColor(Color.TRANSPARENT)
        mPaint.setColor(config.getNormalColor())
    }


    override fun getIndicatorView(): View {
        if (config.isAttachToBanner) {
            val layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            when (config.getGravity()) {
                IndicatorConfig.Direction.LEFT -> layoutParams.gravity =
                    Gravity.BOTTOM or Gravity.START

                IndicatorConfig.Direction.CENTER -> layoutParams.gravity =
                    Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL

                IndicatorConfig.Direction.RIGHT -> layoutParams.gravity =
                    Gravity.BOTTOM or Gravity.END
            }
            layoutParams.leftMargin = config.getMargins().marginLeft
            layoutParams.rightMargin = config.getMargins().marginRight
            layoutParams.topMargin = config.getMargins().marginTop
            layoutParams.bottomMargin = config.getMargins().marginBottom
            setLayoutParams(layoutParams)
        }
        return this
    }

    override fun getIndicatorConfig(): IndicatorConfig {
        return config
    }

    override fun onPageChanged(count: Int, currentPosition: Int) {
        LogUtils.d("onPageChanged: count = $count , currentPosition = $currentPosition")
        config.setIndicatorSize(count)
        config.setCurrentPosition(currentPosition)
        requestLayout()
    }

    protected var offset: Float = 0F
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        offset = positionOffset
        invalidate()
    }

    override fun onPageSelected(position: Int) {
        config.setCurrentPosition(position)
        invalidate()
    }

    override fun onPageScrollStateChanged(state: Int) {

    }
}