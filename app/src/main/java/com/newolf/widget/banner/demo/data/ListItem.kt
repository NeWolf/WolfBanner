package com.newolf.widget.banner.demo.data

import com.newolf.library.adapt.base.entity.MultiItemEntity

/**
 * ======================================================================
 *
 *
 * @author : NeWolf
 * @version : 1.0
 * @since :  2024-04-03
 *
 * =======================================================================
 */
data class ListItem( override val itemType: Int,val imgUrl: String = "", val bannerList:MutableList<String> = mutableListOf()) : MultiItemEntity{
    companion object{
        const val ITEM_TYPE_IMG = 0x888
        const val ITEM_TYPE_BANNER = 0x999
    }
}

