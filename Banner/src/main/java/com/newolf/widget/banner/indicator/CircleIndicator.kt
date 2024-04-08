package com.newolf.widget.banner.indicator

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import kotlin.math.max

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
class CircleIndicator(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    BaseIndicator(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var mNormalRadius = 0
    private var mSelectedRadius = 0
    init {
        mNormalRadius = config.getNormalWidth() / 2
        mSelectedRadius = config.getSelectedWidth() / 2
    }

    private var maxRadius = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val count: Int = config.getIndicatorSize()
        if (count <= 1) {
            return
        }
        mNormalRadius = config.getNormalWidth() / 2
        mSelectedRadius = config.getSelectedWidth() / 2
        //考虑当 选中和默认 的大小不一样的情况
        maxRadius = max(mSelectedRadius, mNormalRadius)
        //间距*（总数-1）+选中宽度+默认宽度*（总数-1）
        val width: Int =
            (count - 1) * config.getIndicatorSpace() + config.getSelectedWidth() + config.getNormalWidth() * (count - 1)
        setMeasuredDimension(width, max(config.getNormalWidth(), config.getSelectedWidth()))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val count: Int = config.getIndicatorSize()
        if (count <= 1) {
            return
        }
        var left = 0f
        for (i in 0 until count) {
            mPaint.setColor(if (config.getCurrentPosition() == i) config.getSelectedColor() else config.getNormalColor())
            val indicatorWidth: Int =
                if (config.getCurrentPosition() == i) config.getSelectedWidth() else config.getNormalWidth()
            val radius = if (config.getCurrentPosition() == i) mSelectedRadius else mNormalRadius
            canvas.drawCircle(left + radius, maxRadius.toFloat(), radius.toFloat(), mPaint)
            left += indicatorWidth + config.getIndicatorSpace()
        }

    }
}