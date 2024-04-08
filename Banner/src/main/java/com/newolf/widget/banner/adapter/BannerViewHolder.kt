package com.newolf.widget.banner.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView

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
class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    /**
     * Views indexed with their IDs
     */
    private val views: SparseArray<View> = SparseArray()


    fun <T : View> getView(@IdRes viewId: Int): T {
        val view = getViewOrNull<T>(viewId)
        checkNotNull(view) { "No view found with id $viewId" }
        return view
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : View> getViewOrNull(@IdRes viewId: Int): T? {
        val view = views.get(viewId)
        if (view == null) {
            itemView.findViewById<T>(viewId)?.let {
                views.put(viewId, it)
                return it
            }
        }
        return view as? T
    }

    fun <T : View> Int.findView(): T? {
        return itemView.findViewById(this)
    }

    fun setText(@IdRes viewId: Int, value: CharSequence?): BannerViewHolder {
        getView<TextView>(viewId).text = value
        return this
    }

    fun setText(@IdRes viewId: Int, @StringRes strId: Int): BannerViewHolder? {
        getView<TextView>(viewId).setText(strId)
        return this
    }

    fun setTextColor(@IdRes viewId: Int, @ColorInt color: Int): BannerViewHolder {
        getView<TextView>(viewId).setTextColor(color)
        return this
    }

    fun setTextColorRes(@IdRes viewId: Int, @ColorRes colorRes: Int): BannerViewHolder {
        getView<TextView>(viewId).setTextColor(itemView.resources.getColor(colorRes))
        return this
    }

    fun setImageResource(@IdRes viewId: Int, @DrawableRes imageResId: Int): BannerViewHolder {
        getView<ImageView>(viewId).setImageResource(imageResId)
        return this
    }

    fun setImageDrawable(@IdRes viewId: Int, drawable: Drawable?): BannerViewHolder {
        getView<ImageView>(viewId).setImageDrawable(drawable)
        return this
    }

    fun setImageBitmap(@IdRes viewId: Int, bitmap: Bitmap?): BannerViewHolder {
        getView<ImageView>(viewId).setImageBitmap(bitmap)
        return this
    }

    fun setBackgroundColor(@IdRes viewId: Int, @ColorInt color: Int): BannerViewHolder {
        getView<View>(viewId).setBackgroundColor(color)
        return this
    }

    fun setBackgroundResource(
        @IdRes viewId: Int,
        @DrawableRes backgroundRes: Int
    ): BannerViewHolder {
        getView<View>(viewId).setBackgroundResource(backgroundRes)
        return this
    }

    fun setVisible(@IdRes viewId: Int, isVisible: Boolean): BannerViewHolder {
        val view = getView<View>(viewId)
        view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
        return this
    }

    fun setGone(@IdRes viewId: Int, isGone: Boolean): BannerViewHolder {
        val view = getView<View>(viewId)
        view.visibility = if (isGone) View.GONE else View.VISIBLE
        return this
    }

    fun setEnabled(@IdRes viewId: Int, isEnabled: Boolean): BannerViewHolder {
        getView<View>(viewId).isEnabled = isEnabled
        return this
    }


}