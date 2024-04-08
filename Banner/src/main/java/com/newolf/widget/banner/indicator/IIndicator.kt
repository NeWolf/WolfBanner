package com.newolf.widget.banner.indicator

import android.view.View
import com.newolf.widget.banner.config.IndicatorConfig
import com.newolf.widget.banner.listener.OnPageChangeListener


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
interface IIndicator: OnPageChangeListener {
    fun getIndicatorView(): View

    fun getIndicatorConfig(): IndicatorConfig?

    fun onPageChanged(count: Int, currentPosition: Int)
}