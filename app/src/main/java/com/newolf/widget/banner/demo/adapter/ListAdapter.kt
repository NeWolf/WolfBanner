package com.newolf.widget.banner.demo.adapter

import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.newolf.library.adapt.base.BaseMultiItemQuickAdapter
import com.newolf.library.adapt.base.viewholder.BaseViewHolder
import com.newolf.widget.banner.Banner
import com.newolf.widget.banner.demo.R
import com.newolf.widget.banner.demo.data.ListItem
import com.newolf.widget.banner.indicator.CircleIndicator
import com.newolf.widget.banner.indicator.RectangleIndicator

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
class ListAdapter() :
    BaseMultiItemQuickAdapter<ListItem, BaseViewHolder>() {
        init {
            addItemType(ListItem.ITEM_TYPE_BANNER, R.layout.item_view_banner)
            addItemType(ListItem.ITEM_TYPE_IMG, R.layout.item_view_img)
        }


    override fun convert(holder: BaseViewHolder, item: ListItem) {
        when (item.itemType) {
            ListItem.ITEM_TYPE_BANNER -> {
                val banner = holder.getView<Banner<String,HomeBannerAdapter>>(R.id.banner)
                val llRoot = holder.getView<LinearLayout>(R.id.ll_root)
                Thread(){
                    run {
                        Glide.get(context.applicationContext).clearDiskCache()
                    }
                }.start()
                val adapter = HomeBannerAdapter(llRoot)
                adapter.setNewInstance(item.bannerList)
                banner.setAdapter(adapter)
                    .setIndicator(RectangleIndicator(context))
            }
            ListItem.ITEM_TYPE_IMG -> {
                val ivShow = holder.getView<ImageView>(R.id.iv_show)
                Glide.with(ivShow).setDefaultRequestOptions(RequestOptions.centerCropTransform()).load(item.imgUrl).into(ivShow)
            }
            else -> {}
        }
    }
}