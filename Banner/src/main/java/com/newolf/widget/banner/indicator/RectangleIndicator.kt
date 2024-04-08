package com.newolf.widget.banner.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet

/**
 * ======================================================================
 *
 *
 * @author : NeWolf
 * @version : 1.0
 * @since :  2024-04-08
 *
 * =======================================================================
 */
class RectangleIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    BaseIndicator(context, attrs, defStyleAttr) {

    var rectF: RectF = RectF()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val count = config.getIndicatorSize()
        if (count <= 1) {
            return
        }
        //间距*（总数-1）+默认宽度*（总数-1）+选中宽度
        val space = config.getIndicatorSpace() * (count - 1)
        val normal = config.getNormalWidth() * (count - 1)
        setMeasuredDimension(space + normal + config.getSelectedWidth(), config.getHeight())
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val count = config.getIndicatorSize()
        if (count <= 1) {
            return
        }
        var left = 0f
        for (i in 0 until count) {
            mPaint.setColor(if (config.getCurrentPosition() == i) config.getSelectedColor() else config.getNormalColor())
            val indicatorWidth =
                if (config.getCurrentPosition() == i) config.getSelectedWidth() else config.getNormalWidth()
            rectF[left, 0f, left + indicatorWidth] = config.getHeight().toFloat()
            left += indicatorWidth + config.getIndicatorSpace()
            canvas.drawRoundRect(
                rectF, config.getRadius().toFloat(),
                config.getRadius().toFloat(), mPaint
            )
        }
    }
}