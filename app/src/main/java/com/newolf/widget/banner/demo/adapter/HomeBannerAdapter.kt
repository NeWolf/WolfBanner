package com.newolf.widget.banner.demo.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.newolf.widget.banner.adapter.BannerAdapter
import com.newolf.widget.banner.adapter.BannerViewHolder
import com.newolf.widget.banner.demo.R

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
class HomeBannerAdapter(val llRoot: LinearLayout) : BannerAdapter<String>(
    R.layout.item_view_img_banner
) {


    private var scaledHeight:Int = 0
    private var width = 0

    override fun onConvert(holder: BannerViewHolder, item: String) {
        val ivShow = holder.getView<ImageView>(R.id.iv_show)

        if (scaledHeight == 0 && width == 0){
            Glide.with(ivShow).asBitmap().load(item).into(object : CustomViewTarget<ImageView,Bitmap>(ivShow){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val scale = 1.0F * ivShow.width / resource.width
                    scaledHeight = (resource.height * scale).toInt()
                    width = ivShow.width
                    Log.wtf(TAG, "onResourceReady: resource = ${resource.width}x${resource.height} ,ivShow =  ${ivShow.width}x${ivShow.height} ,scale = $scale")
                    val lp = llRoot.layoutParams
                    lp.height = scaledHeight
                    llRoot.layoutParams = lp
                    llRoot.requestLayout()
                    llRoot.requestLayout()
                    Log.wtf(TAG, "onResourceReady:updateHeight resource =  ${resource.width}x${resource.height} ,ivShow =  ${ivShow.width}x${ivShow.height} ,scale = $scale scaledHeight = $scaledHeight")
                    ivShow.setImageBitmap(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {

                }

                override fun onResourceCleared(placeholder: Drawable?) {

                }

            })
        }else{
            Glide.with(ivShow).load(item).into(ivShow)
        }


        holder.setText(R.id.tv_index,getRealPosition(holder.layoutPosition).toString())
    }
}