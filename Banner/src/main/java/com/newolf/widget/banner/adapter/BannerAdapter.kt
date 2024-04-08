package com.newolf.widget.banner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.newolf.widget.banner.Banner
import com.newolf.widget.banner.config.BannerConfig
import com.newolf.widget.banner.listener.OnBannerListener

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
abstract class BannerAdapter<T>(@LayoutRes val layoutResId: Int) :
    RecyclerView.Adapter<BannerViewHolder>() {
    companion object {
        const val TAG = "BannerAdapter"
    }

    var data: MutableList<T> = arrayListOf()
        internal set

    protected var mIncreaseCount = BannerConfig.INCREASE_COUNT


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val vh: BannerViewHolder = createBaseViewHolder(parent, layoutResId)
        return vh
    }

    protected open fun createBaseViewHolder(
        parent: ViewGroup,
        @LayoutRes layoutResId: Int
    ): BannerViewHolder {
        return BannerViewHolder(parent.getItemView(layoutResId))
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val real = getRealPosition(position)
        val data: T = data[real]
        holder.itemView.setOnClickListener {
            bannerListener?.onBannerClick(data, real)
        }
        onConvert(holder, data)

    }


    abstract fun onConvert(holder: BannerViewHolder, item: T)

    fun setIncreaseCount(count: Int) {
        mIncreaseCount = count
    }


    final override fun getItemCount(): Int {
        val result = if (getRealCount() > 1) getRealCount() + mIncreaseCount else getRealCount()
//        LogUtils.dTag(TAG,"getItemCount: result = $result")
        return result
    }

    fun getRealCount(): Int {
        return data.size
    }

    fun getRealPosition(position: Int): Int {
        return Banner.getRealPosition(
            mIncreaseCount == BannerConfig.INCREASE_COUNT,
            position,
            getRealCount()
        )
    }

    fun getRealData(position: Int): T? {
        val realPosition = getRealPosition(position)
        return if (realPosition > data.size - 1) {
            null
        } else data[realPosition]
    }

    private var bannerListener: OnBannerListener<T>? = null
    fun setOnBannerListener(listener: OnBannerListener<T>) {
        bannerListener = listener
    }

    fun setNewInstance(bannerList: MutableList<T>) {
        data.clear()
        data.addAll(bannerList)
        notifyItemRangeChanged(0, bannerList.size)
    }


}

fun ViewGroup.getItemView(@LayoutRes layoutResId: Int): View {
    return LayoutInflater.from(this.context).inflate(layoutResId, this, false)
}