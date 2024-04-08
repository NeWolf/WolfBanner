package com.newolf.widget.banner.listener

/**
 * ======================================================================
 *
 *
 * @author : NeWolf
 * @version : 1.0
 * @since :  2024-04-07
 *
 * =======================================================================
 */
interface OnBannerListener<T> {
    fun onBannerClick(data: T, position: Int)
}