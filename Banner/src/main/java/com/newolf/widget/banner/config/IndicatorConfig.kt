package com.newolf.widget.banner.config

import androidx.annotation.ColorInt
import androidx.annotation.IntDef

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
class IndicatorConfig {

    private var indicatorSize = 0
    private var currentPosition = -1
    private var gravity = Direction.CENTER
    private var normalWidth = BannerConfig.INDICATOR_NORMAL_WIDTH
    private var selectedWidth = BannerConfig.INDICATOR_SELECTED_WIDTH

    @ColorInt
    private var normalColor = BannerConfig.INDICATOR_NORMAL_COLOR

    @ColorInt
    private var selectedColor = BannerConfig.INDICATOR_SELECTED_COLOR

    private var radius = BannerConfig.INDICATOR_RADIUS
    private var height = BannerConfig.INDICATOR_HEIGHT


    @IntDef(Direction.LEFT, Direction.CENTER, Direction.RIGHT)
    @Retention(
        AnnotationRetention.SOURCE
    )
    annotation class Direction {
        companion object {
            const val LEFT = 0
            const val CENTER = 1
            const val RIGHT = 2
        }
    }

    var isAttachToBanner = true


    open class Margins(
        val marginLeft: Int,
        val marginTop: Int,
        val marginRight: Int,
        val marginBottom: Int
    ) {
        constructor(margin: Int) : this(margin, margin, margin, margin)
    }

    private var margins: Margins? = null
    fun setMargins(margins: Margins) {
        this.margins = margins
    }

    private var indicatorSpace = BannerConfig.INDICATOR_SPACE
    fun setIndicatorSpace(indicatorSpace: Int): IndicatorConfig {
        this.indicatorSpace = indicatorSpace
        return this
    }

    fun setIndicatorGravity(@Direction indicatorGravity: Int): IndicatorConfig {
        gravity = indicatorGravity
        return this
    }

    fun setIndicatorNormalWidth(normalWidth: Int): IndicatorConfig {
       this.normalWidth =normalWidth
        return this
    }

    fun setIndicatorSelectedWidth(selectedWidth: Int): IndicatorConfig {
        this.selectedWidth = selectedWidth
        return this
    }

    fun setIndicatorHeight(indicatorHeight: Int): IndicatorConfig {
        this.height = indicatorHeight
        return this
    }



    fun setIndicatorRadius(indicatorRadius: Int): IndicatorConfig {
        this.radius = indicatorRadius
        return this
    }

    fun setIndicatorNormalColor(@ColorInt normalColor: Int): IndicatorConfig {
        this.normalColor = normalColor
        return this
    }


    fun setIndicatorSelectedColor(@ColorInt selectedColor: Int): IndicatorConfig {
       this.selectedColor = selectedColor
        return this
    }

    fun getNormalColor(): Int {
        return normalColor
    }

    fun getGravity(): Int {
        return gravity
    }

    fun getMargins(): Margins {
        if (margins == null){
            margins = Margins(5.dp)
        }
        return margins as Margins
    }

    fun setIndicatorSize(count: Int): IndicatorConfig {
        this.indicatorSize = count
        return this
    }

    fun setCurrentPosition(currentPosition: Int): IndicatorConfig {
        this.currentPosition = currentPosition
        return this
    }

    fun getSelectedWidth(): Int {
        return selectedWidth
    }

    fun getNormalWidth(): Int {
        return normalWidth
    }

    fun getIndicatorSize(): Int {
        return indicatorSize
    }

    fun getIndicatorSpace(): Int {
        return indicatorSpace
    }

    fun getCurrentPosition(): Int {
        return currentPosition
    }

    fun getSelectedColor(): Int {
        return selectedColor
    }

    fun getHeight(): Int {
        return height
    }

    fun getRadius(): Int {
        return radius
    }


}