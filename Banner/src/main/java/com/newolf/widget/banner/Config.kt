package com.newolf.widget.banner

import com.newolf.logutils.LogUtils

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
object Config {
    const val TAG = "Config"

    fun enableLog(isEnable :Boolean = BuildConfig.DEBUG){
        LogUtils.dTag(TAG, isEnable)
        val logConfig =  LogUtils.getConfig()
        logConfig.isLogSwitch = isEnable
    }
}