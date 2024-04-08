package com.newolf.widget.banner

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.newolf.logutils.LogUtils
import com.newolf.widget.banner.adapter.BannerAdapter
import com.newolf.widget.banner.config.BannerConfig
import com.newolf.widget.banner.config.IndicatorConfig
import com.newolf.widget.banner.indicator.IIndicator
import com.newolf.widget.banner.listener.OnBannerListener
import com.newolf.widget.banner.listener.OnPageChangeListener
import com.newolf.widget.banner.utils.ScrollSpeedManger
import java.lang.ref.WeakReference
import kotlin.math.abs

class Banner<T, BA : BannerAdapter<T>?> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), LifecycleObserver {


    lateinit var viewPager2: ViewPager2
        private set
    private lateinit var mLoopTask: AutoLoopTask
    private var mOnPageChangeListener: OnPageChangeListener? = null
    private var mAdapter: BA? = null
    var indicator: IIndicator? = null
        private set
    private lateinit var mCompositePageTransformer: CompositePageTransformer
    private lateinit var mPageChangeCallback: BannerOnPageChangeCallback

    // 是否允许无限轮播（即首尾直接切换）
    var isInfiniteLoop = BannerConfig.IS_INFINITE_LOOP
        private set

    // 是否自动轮播
    private var mIsAutoLoop = BannerConfig.IS_AUTO_LOOP

    // 轮播切换间隔时间
    private var mLoopTime = BannerConfig.LOOP_TIME.toLong()

    // 轮播切换时间
    var scrollTime = BannerConfig.SCROLL_TIME
        private set

    // 轮播开始位置
    var startPosition = 1
        private set

    // banner圆角半径，默认没有圆角
    private var mBannerRadius = 0f

    // banner圆角方向，如果一个都不设置，默认四个角全部圆角
    private var mRoundTopLeft = false
    private var mRoundTopRight = false
    private var mRoundBottomLeft = false
    private var mRoundBottomRight = false

    // 指示器相关配置
    private var normalWidth = BannerConfig.INDICATOR_NORMAL_WIDTH
    private var selectedWidth = BannerConfig.INDICATOR_SELECTED_WIDTH
    private var normalColor = BannerConfig.INDICATOR_NORMAL_COLOR
    private var selectedColor = BannerConfig.INDICATOR_SELECTED_COLOR
    private var indicatorGravity = IndicatorConfig.Direction.CENTER
    private var indicatorSpace = 0
    private var indicatorMargin = 0
    private var indicatorMarginLeft = 0
    private var indicatorMarginTop = 0
    private var indicatorMarginRight = 0
    private var indicatorMarginBottom = BannerConfig.INDICATOR_MARGIN
    private var indicatorHeight = BannerConfig.INDICATOR_HEIGHT
    private var indicatorRadius = BannerConfig.INDICATOR_RADIUS
    private var mOrientation = HORIZONTAL

    // 滑动距离范围
    private var mTouchSlop = 0

    // 记录触摸的位置（主要用于解决事件冲突问题）
    private var mStartX = 0f
    private var mStartY = 0f

    // 记录viewpager2是否被拖动
    private var mIsViewPager2Drag = false

    // 是否要拦截事件
    private var isIntercept = true

    //绘制圆角视图
    private lateinit var mRoundPaint: Paint
    private lateinit var mImagePaint: Paint

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(HORIZONTAL, VERTICAL)
    annotation class Orientation

    private fun init(context: Context) {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop / 2
        mCompositePageTransformer = CompositePageTransformer()
        mPageChangeCallback = BannerOnPageChangeCallback()
        mLoopTask = AutoLoopTask(this)
        viewPager2 = ViewPager2(context)
        viewPager2.setLayoutParams(
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        viewPager2.setOffscreenPageLimit(2)
        viewPager2.registerOnPageChangeCallback(mPageChangeCallback)
        viewPager2.setPageTransformer(mCompositePageTransformer)
        ScrollSpeedManger.reflectLayoutManager(this)
        addView(viewPager2)
        mRoundPaint = Paint()
        mRoundPaint.setColor(Color.WHITE)
        mRoundPaint.isAntiAlias = true
        mRoundPaint.style = Paint.Style.FILL
        mRoundPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.DST_OUT))
        mImagePaint = Paint()
        mImagePaint.setXfermode(null)

        if (context is LifecycleOwner) {
            LogUtils.dTag(TAG, "context is LifecycleOwner, addObserver this")
            context.lifecycle.addObserver(this)
        } else {
            Log.e(TAG, "Please set LifecycleOwner!!!")
        }
    }


    /**
     * **********************************************************************
     * ------------------------ 生命周期控制 --------------------------------*
     * **********************************************************************
     */
    fun addLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart(){
        LogUtils.dTag(TAG,"OnLifecycleEvent onStart")
        start()
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop(){
        LogUtils.dTag(TAG,"OnLifecycleEvent onStop")
        start()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(){
        LogUtils.dTag(TAG,"OnLifecycleEvent onDestroy")
        start()
    }






    private fun initTypedArray(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.Banner)
            mBannerRadius = a.getDimensionPixelSize(R.styleable.Banner_banner_radius, 0).toFloat()
            mLoopTime =
                a.getInt(R.styleable.Banner_banner_loop_time, BannerConfig.LOOP_TIME).toLong()
            mIsAutoLoop =
                a.getBoolean(R.styleable.Banner_banner_auto_loop, BannerConfig.IS_AUTO_LOOP)
            isInfiniteLoop =
                a.getBoolean(R.styleable.Banner_banner_infinite_loop, BannerConfig.IS_INFINITE_LOOP)
            normalWidth = a.getDimensionPixelSize(
                R.styleable.Banner_banner_indicator_normal_width,
                BannerConfig.INDICATOR_NORMAL_WIDTH
            )
            selectedWidth = a.getDimensionPixelSize(
                R.styleable.Banner_banner_indicator_selected_width,
                BannerConfig.INDICATOR_SELECTED_WIDTH
            )
            normalColor = a.getColor(
                R.styleable.Banner_banner_indicator_normal_color,
                BannerConfig.INDICATOR_NORMAL_COLOR
            )
            selectedColor = a.getColor(
                R.styleable.Banner_banner_indicator_selected_color,
                BannerConfig.INDICATOR_SELECTED_COLOR
            )
            indicatorGravity = a.getInt(
                R.styleable.Banner_banner_indicator_gravity,
                IndicatorConfig.Direction.CENTER
            )
            indicatorSpace = a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_space, 0)
            indicatorMargin = a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_margin, 0)
            indicatorMarginLeft =
                a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_marginLeft, 0)
            indicatorMarginTop =
                a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_marginTop, 0)
            indicatorMarginRight =
                a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_marginRight, 0)
            indicatorMarginBottom =
                a.getDimensionPixelSize(
                    R.styleable.Banner_banner_indicator_marginBottom,
                    BannerConfig.INDICATOR_MARGIN
                )
            indicatorHeight = a.getDimensionPixelSize(
                R.styleable.Banner_banner_indicator_height,
                BannerConfig.INDICATOR_HEIGHT
            )
            indicatorRadius = a.getDimensionPixelSize(
                R.styleable.Banner_banner_indicator_radius,
                BannerConfig.INDICATOR_RADIUS
            )
            mOrientation = a.getInt(R.styleable.Banner_banner_orientation, HORIZONTAL)
            mRoundTopLeft = a.getBoolean(R.styleable.Banner_banner_round_top_left, false)
            mRoundTopRight = a.getBoolean(R.styleable.Banner_banner_round_top_right, false)
            mRoundBottomLeft = a.getBoolean(R.styleable.Banner_banner_round_bottom_left, false)
            mRoundBottomRight = a.getBoolean(R.styleable.Banner_banner_round_bottom_right, false)
            a.recycle()
        }
        setOrientation(mOrientation)
        setInfiniteLoop()
    }

    private fun initIndicatorAttr() {
        if (indicatorMargin != 0) {
            setIndicatorMargins(IndicatorConfig.Margins(indicatorMargin))
        } else if (indicatorMarginLeft != 0 || indicatorMarginTop != 0 || indicatorMarginRight != 0 || indicatorMarginBottom != 0) {
            setIndicatorMargins(
                IndicatorConfig.Margins(
                    indicatorMarginLeft,
                    indicatorMarginTop,
                    indicatorMarginRight,
                    indicatorMarginBottom
                )
            )
        }
        if (indicatorSpace > 0) {
            setIndicatorSpace(indicatorSpace)
        }
        if (indicatorGravity != IndicatorConfig.Direction.CENTER) {
            setIndicatorGravity(indicatorGravity)
        }
        if (normalWidth > 0) {
            setIndicatorNormalWidth(normalWidth)
        }
        if (selectedWidth > 0) {
            setIndicatorSelectedWidth(selectedWidth)
        }
        if (indicatorHeight > 0) {
            setIndicatorHeight(indicatorHeight)
        }
        if (indicatorRadius > 0) {
            setIndicatorRadius(indicatorRadius)
        }
        setIndicatorNormalColor(normalColor)
        setIndicatorSelectedColor(selectedColor)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!viewPager2.isUserInputEnabled) {
            return super.dispatchTouchEvent(ev)
        }
        val action = ev.actionMasked
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
            start()
        } else if (action == MotionEvent.ACTION_DOWN) {
            stop()
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (!viewPager2.isUserInputEnabled || !isIntercept) {
            return super.onInterceptTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartX = event.x
                mStartY = event.y
                parent.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_MOVE -> {
                val endX = event.x
                val endY = event.y
                val distanceX = abs((endX - mStartX).toDouble()).toFloat()
                val distanceY = abs((endY - mStartY).toDouble()).toFloat()
                mIsViewPager2Drag =
                    if (viewPager2.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                        distanceX > mTouchSlop && distanceX > distanceY
                    } else {
                        distanceY > mTouchSlop && distanceY > distanceX
                    }
                parent.requestDisallowInterceptTouchEvent(mIsViewPager2Drag)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(
                false
            )
        }
        return super.onInterceptTouchEvent(event)
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (mBannerRadius > 0) {
            canvas.saveLayer(
                RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat()),
                mImagePaint
            )
            super.dispatchDraw(canvas)
            //绘制外圆环边框圆环
            //默认四个角都设置
            if (!mRoundTopRight && !mRoundTopLeft && !mRoundBottomRight && !mRoundBottomLeft) {
                drawTopLeft(canvas)
                drawTopRight(canvas)
                drawBottomLeft(canvas)
                drawBottomRight(canvas)
                canvas.restore()
                return
            }
            if (mRoundTopLeft) {
                drawTopLeft(canvas)
            }
            if (mRoundTopRight) {
                drawTopRight(canvas)
            }
            if (mRoundBottomLeft) {
                drawBottomLeft(canvas)
            }
            if (mRoundBottomRight) {
                drawBottomRight(canvas)
            }
            canvas.restore()
        } else {
            super.dispatchDraw(canvas)
        }
    }

    private fun drawTopLeft(canvas: Canvas) {
        val path = Path()
        path.moveTo(0f, mBannerRadius)
        path.lineTo(0f, 0f)
        path.lineTo(mBannerRadius, 0f)
        path.arcTo(RectF(0f, 0f, mBannerRadius * 2, mBannerRadius * 2), -90f, -90f)
        path.close()
        canvas.drawPath(path, mRoundPaint)
    }

    private fun drawTopRight(canvas: Canvas) {
        val width = width
        val path = Path()
        path.moveTo(width - mBannerRadius, 0f)
        path.lineTo(width.toFloat(), 0f)
        path.lineTo(width.toFloat(), mBannerRadius)
        path.arcTo(
            RectF(width - 2 * mBannerRadius, 0f, width.toFloat(), mBannerRadius * 2),
            0f,
            -90f
        )
        path.close()
        canvas.drawPath(path, mRoundPaint)
    }

    private fun drawBottomLeft(canvas: Canvas) {
        val height = height
        val path = Path()
        path.moveTo(0f, height - mBannerRadius)
        path.lineTo(0f, height.toFloat())
        path.lineTo(mBannerRadius, height.toFloat())
        path.arcTo(
            RectF(0f, height - 2 * mBannerRadius, mBannerRadius * 2, height.toFloat()),
            90f,
            90f
        )
        path.close()
        canvas.drawPath(path, mRoundPaint)
    }

    private fun drawBottomRight(canvas: Canvas) {
        val height = height
        val width = width
        val path = Path()
        path.moveTo(width - mBannerRadius, height.toFloat())
        path.lineTo(width.toFloat(), height.toFloat())
        path.lineTo(width.toFloat(), height - mBannerRadius)
        path.arcTo(
            RectF(
                width - 2 * mBannerRadius,
                height - 2 * mBannerRadius,
                width.toFloat(),
                height.toFloat()
            ), 0f, 90f
        )
        path.close()
        canvas.drawPath(path, mRoundPaint)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }

    internal inner class BannerOnPageChangeCallback : OnPageChangeCallback() {
        private var mTempPosition = INVALID_VALUE
        private var isScrolled = false
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            val realPosition = getRealPosition(isInfiniteLoop, position, realCount)
            if (realPosition == currentItem - 1) {
                mOnPageChangeListener?.onPageScrolled(
                    realPosition,
                    positionOffset,
                    positionOffsetPixels
                )
                indicator?.onPageScrolled(realPosition, positionOffset, positionOffsetPixels)
            }

        }

        override fun onPageSelected(position: Int) {
            if (isScrolled) {
                mTempPosition = position
                val realPosition = getRealPosition(isInfiniteLoop, position, realCount)
                mOnPageChangeListener?.onPageSelected(realPosition)
                indicator?.onPageSelected(realPosition)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            //手势滑动中,代码执行滑动中
            if (state == ViewPager2.SCROLL_STATE_DRAGGING || state == ViewPager2.SCROLL_STATE_SETTLING) {
                isScrolled = true
            } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                //滑动闲置或滑动结束
                isScrolled = false
                if (mTempPosition != INVALID_VALUE && isInfiniteLoop) {
                    if (mTempPosition == 0) {
                        setCurrentItem(realCount, false)
                    } else if (mTempPosition == itemCount - 1) {
                        setCurrentItem(1, false)
                    }
                }
            }
            mOnPageChangeListener?.onPageScrollStateChanged(state)
            indicator?.onPageScrollStateChanged(state)
        }
    }

    internal class AutoLoopTask(banner: Banner<*, *>) : Runnable {
        private val reference: WeakReference<Banner<*, *>>

        init {
            reference = WeakReference(banner)
        }

        override fun run() {
            val banner = reference.get()
            if (banner != null && banner.mIsAutoLoop) {
                val count = banner.itemCount
                if (count == 0) {
                    return
                }
                val next = (banner.currentItem + 1) % count
                banner.setCurrentItem(next)
                banner.postDelayed(banner.mLoopTask, banner.mLoopTime)
            }
        }
    }

    private val mAdapterDataObserver: RecyclerView.AdapterDataObserver =
        object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                if (itemCount <= 1) {
                    stop()
                } else {
                    start()
                }
                setIndicatorPageChange()
            }
        }

    init {
        Config.enableLog()
        init(context)
        initTypedArray(context, attrs)
    }

    private fun initIndicator() {
        if (indicator == null || adapter == null) {
            return
        }
        if (indicator!!.getIndicatorConfig()!!.isAttachToBanner) {
            removeIndicator()
            addView(indicator!!.getIndicatorView())
        }
        initIndicatorAttr()
        setIndicatorPageChange()
    }

    private fun setInfiniteLoop() {
        // 当不支持无限循环时，要关闭自动轮播
        if (!isInfiniteLoop) {
            isAutoLoop(false)
        }
        setStartPosition(if (isInfiniteLoop) startPosition else 0)
    }

    private fun setRecyclerViewPadding(itemPadding: Int) {
        setRecyclerViewPadding(itemPadding, itemPadding)
    }

    private fun setRecyclerViewPadding(leftItemPadding: Int, rightItemPadding: Int) {
        val recyclerView = viewPager2.getChildAt(0) as RecyclerView
        if (viewPager2.orientation == ViewPager2.ORIENTATION_VERTICAL) {
            recyclerView.setPadding(
                viewPager2.getPaddingLeft(),
                leftItemPadding,
                viewPager2.getPaddingRight(),
                rightItemPadding
            )
        } else {
            recyclerView.setPadding(
                leftItemPadding,
                viewPager2.paddingTop,
                rightItemPadding,
                viewPager2.paddingBottom
            )
        }
        recyclerView.setClipToPadding(false)
    }

    val currentItem: Int
        get() = viewPager2.currentItem

    /**
     * **********************************************************************
     * ------------------------ 对外公开API ---------------------------------*
     * **********************************************************************
     */

    val itemCount: Int
        get() = adapter?.getItemCount() ?: 0
    val adapter: BA?
        get() = mAdapter
    val indicatorConfig: IndicatorConfig?
        get() = indicator?.getIndicatorConfig()
    val realCount: Int
        /**
         * 返回banner真实总数
         */
        get() = if (adapter != null) {
            adapter!!.getRealCount()
        } else 0
    //-----------------------------------------------------------------------------------------
    /**
     * 是否要拦截事件
     * @param intercept
     * @return
     */
    fun setIntercept(intercept: Boolean): Banner<*, *> {
        isIntercept = intercept
        return this
    }

    /**
     * 跳转到指定位置（最好在设置了数据后在调用，不然没有意义）
     * @param position
     * @return
     */
    fun setCurrentItem(position: Int): Banner<*, *> {
        return setCurrentItem(position, true)
    }

    /**
     * 跳转到指定位置（最好在设置了数据后在调用，不然没有意义）
     * @param position
     * @param smoothScroll
     * @return
     */
    fun setCurrentItem(position: Int, smoothScroll: Boolean): Banner<*, *> {
        viewPager2.setCurrentItem(position, smoothScroll)
        return this
    }

    var isStart = false
    fun setIndicatorPageChange(): Banner<*, *> {
        if (indicator != null) {
            val realPosition = getRealPosition(isInfiniteLoop, currentItem, realCount)
            LogUtils.dTag(
                TAG,
                "setIndicatorPageChange: realPosition = $realPosition , isInfiniteLoop = $isInfiniteLoop , currentItem = $currentItem , realCount = $realCount"
            )
            indicator?.onPageChanged(realCount, realPosition)
        }
        return this
    }

    fun removeIndicator(): Banner<*, *> {
        if (indicator != null) {
            removeView(indicator!!.getIndicatorView())
        }
        return this
    }

    /**
     * 设置开始的位置 (需要在setAdapter或者setDatas之前调用才有效哦)
     */
    fun setStartPosition(mStartPosition: Int): Banner<*, *> {
        startPosition = mStartPosition
        return this
    }

    /**
     * 禁止手动滑动
     *
     * @param enabled true 允许，false 禁止
     */
    fun setUserInputEnabled(enabled: Boolean): Banner<*, *> {
        viewPager2.setUserInputEnabled(enabled)
        return this
    }

    /**
     * 添加PageTransformer，可以组合效果
     * [ViewPager2.PageTransformer]
     * 如果找不到请导入implementation "androidx.viewpager2:viewpager2:1.0.0"
     */
    fun addPageTransformer(transformer: ViewPager2.PageTransformer?): Banner<*, *> {
        mCompositePageTransformer.addTransformer(transformer!!)
        return this
    }

    /**
     * 设置PageTransformer，和addPageTransformer不同，这个只支持一种transformer
     */
    fun setPageTransformer(transformer: ViewPager2.PageTransformer?): Banner<*, *> {
        viewPager2.setPageTransformer(transformer)
        return this
    }

    fun removeTransformer(transformer: ViewPager2.PageTransformer?): Banner<*, *> {
        mCompositePageTransformer.removeTransformer(transformer!!)
        return this
    }

    /**
     * 添加 ItemDecoration
     */
    fun addItemDecoration(decor: RecyclerView.ItemDecoration?): Banner<*, *> {
        viewPager2.addItemDecoration(decor!!)
        return this
    }

    fun addItemDecoration(decor: RecyclerView.ItemDecoration?, index: Int): Banner<*, *> {
        viewPager2.addItemDecoration(decor!!, index)
        return this
    }

    /**
     * 是否允许自动轮播
     *
     * @param isAutoLoop ture 允许，false 不允许
     */
    fun isAutoLoop(isAutoLoop: Boolean): Banner<*, *> {
        mIsAutoLoop = isAutoLoop
        return this
    }

    /**
     * 设置轮播间隔时间
     *
     * @param loopTime 时间（毫秒）
     */
    fun setLoopTime(loopTime: Long): Banner<*, *> {
        mLoopTime = loopTime
        return this
    }

    /**
     * 设置轮播滑动过程的时间
     */
    fun setScrollTime(scrollTime: Int): Banner<*, *> {
        this.scrollTime = scrollTime
        return this
    }

    /**
     * 开始轮播
     */
    fun start(): Banner<*, *> {
        if (mIsAutoLoop) {
            stop()
            postDelayed(mLoopTask, mLoopTime)
        }
        return this
    }

    /**
     * 停止轮播
     */
    fun stop(): Banner<*, *> {
        if (mIsAutoLoop) {
            removeCallbacks(mLoopTask)
        }
        return this
    }

    /**
     * 移除一些引用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        viewPager2.unregisterOnPageChangeCallback(mPageChangeCallback)
        stop()
    }

    /**
     * 设置banner的适配器
     */
    fun setAdapter(adapter: BA): Banner<*, *> {
        mAdapter = adapter
        if (!isInfiniteLoop) {
            this.adapter!!.setIncreaseCount(0)
        }
        this.adapter!!.registerAdapterDataObserver(mAdapterDataObserver)
        viewPager2.setAdapter(adapter)
        setCurrentItem(startPosition, false)
        initIndicator()
        return this
    }

    /**
     * 设置banner的适配器
     * @param adapter
     * @param isInfiniteLoop 是否支持无限循环
     * @return
     */
    fun setAdapter(adapter: BA, isInfiniteLoop: Boolean): Banner<*, *> {
        this.isInfiniteLoop = isInfiniteLoop
        setInfiniteLoop()
        setAdapter(adapter)
        return this
    }


    /**
     * 设置banner轮播方向
     *
     * @param orientation [Orientation]
     */
    fun setOrientation(@Orientation orientation: Int): Banner<*, *> {
        viewPager2.setOrientation(orientation)
        return this
    }

    /**
     * 改变最小滑动距离
     */
    fun setTouchSlop(mTouchSlop: Int): Banner<*, *> {
        this.mTouchSlop = mTouchSlop
        return this
    }

    /**
     * 设置点击事件
     */
    fun setOnBannerListener(listener: OnBannerListener<T>): Banner<T, *> {
        adapter?.setOnBannerListener(listener)
        return this
    }

    /**
     * 添加viewpager切换事件
     *
     *
     * 在viewpager2中切换事件[ViewPager2.OnPageChangeCallback]是一个抽象类，
     * 为了方便使用习惯这里用的是和viewpager一样的[ViewPager.OnPageChangeListener]接口
     *
     */
    fun addOnPageChangeListener(pageListener: OnPageChangeListener): Banner<*, *> {
        mOnPageChangeListener = pageListener
        return this
    }

    /**
     * 设置banner圆角
     *
     *
     * 默认没有圆角，需要取消圆角把半径设置为0即可
     *
     * @param radius 圆角半径
     */
    fun setBannerRound(radius: Float): Banner<*, *> {
        mBannerRadius = radius
        return this
    }

    /**
     * 设置banner圆角(第二种方式，和上面的方法不要同时使用)，只支持5.0以上
     */
    fun setBannerRound2(radius: Float): Banner<*, *> {
        this.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, radius)
            }
        }
        setClipToOutline(true)
        return this
    }
    //    /**
    //     * 为banner添加画廊效果
    //     *
    //     * @param itemWidth  item左右展示的宽度,单位dp
    //     * @param pageMargin 页面间距,单位dp
    //     */
    //    public Banner setBannerGalleryEffect(int itemWidth, int pageMargin) {
    //        return setBannerGalleryEffect(itemWidth, pageMargin, .85f);
    //    }
    //
    //    /**
    //     * 为banner添加画廊效果
    //     *
    //     * @param leftItemWidth  item左展示的宽度,单位dp
    //     * @param rightItemWidth item右展示的宽度,单位dp
    //     * @param pageMargin     页面间距,单位dp
    //     */
    //    public Banner setBannerGalleryEffect(int leftItemWidth, int rightItemWidth, int pageMargin) {
    //        return setBannerGalleryEffect(leftItemWidth,rightItemWidth, pageMargin, .85f);
    //    }
    //
    //    /**
    //     * 为banner添加画廊效果
    //     *
    //     * @param itemWidth  item左右展示的宽度,单位dp
    //     * @param pageMargin 页面间距,单位dp
    //     * @param scale      缩放[0-1],1代表不缩放
    //     */
    //    public Banner setBannerGalleryEffect(int itemWidth, int pageMargin, float scale) {
    //        return setBannerGalleryEffect(itemWidth, itemWidth, pageMargin, scale);
    //    }
    //    /**
    //     * 为banner添加画廊效果
    //     *
    //     * @param leftItemWidth  item左展示的宽度,单位dp
    //     * @param rightItemWidth item右展示的宽度,单位dp
    //     * @param pageMargin     页面间距,单位dp
    //     * @param scale          缩放[0-1],1代表不缩放
    //     */
    //    public Banner setBannerGalleryEffect(int leftItemWidth, int rightItemWidth, int pageMargin, float scale) {
    //        if (pageMargin > 0) {
    //            addPageTransformer(new MarginPageTransformer(BannerUtils.dp2px(pageMargin)));
    //        }
    //        if (scale < 1 && scale > 0) {
    //            addPageTransformer(new ScaleInTransformer(scale));
    //        }
    //        setRecyclerViewPadding(leftItemWidth > 0 ? BannerUtils.dp2px(leftItemWidth + pageMargin) : 0,
    //                rightItemWidth > 0 ? BannerUtils.dp2px(rightItemWidth + pageMargin) : 0);
    //        return this;
    //    }
    //
    //    /**
    //     * 为banner添加魅族效果
    //     *
    //     * @param itemWidth item左右展示的宽度,单位dp
    //     */
    //    public Banner setBannerGalleryMZ(int itemWidth) {
    //        return setBannerGalleryMZ(itemWidth, .88f);
    //    }
    //
    //    /**
    //     * 为banner添加魅族效果
    //     *
    //     * @param itemWidth item左右展示的宽度,单位dp
    //     * @param scale     缩放[0-1],1代表不缩放
    //     */
    //    public Banner setBannerGalleryMZ(int itemWidth, float scale) {
    //        if (scale < 1 && scale > 0) {
    //            addPageTransformer(new MZScaleInTransformer(scale));
    //        }
    //        setRecyclerViewPadding(BannerUtils.dp2px(itemWidth));
    //        return this;
    //    }
    /**
     * **********************************************************************
     * ------------------------ 指示器相关设置 --------------------------------*
     * **********************************************************************
     */
    /**
     * 设置轮播指示器(显示在banner上)
     */
    fun setIndicator(indicator: IIndicator): Banner<*, *> {
        return setIndicator(indicator, true)
    }

    /**
     * 设置轮播指示器(如果你的指示器写在布局文件中，attachToBanner传false)
     *
     * @param attachToBanner 是否将指示器添加到banner中，false 代表你可以将指示器通过布局放在任何位置
     * 注意：设置为false后，内置的 setIndicatorGravity()和setIndicatorMargins() 方法将失效。
     * 想改变可以自己调用系统提供的属性在布局文件中进行设置。具体可以参照demo
     */
    fun setIndicator(indicator: IIndicator, attachToBanner: Boolean): Banner<*, *> {
        removeIndicator()
        indicator.getIndicatorConfig()!!.isAttachToBanner = attachToBanner
        this.indicator = indicator
        initIndicator()
        return this
    }

    fun setIndicatorSelectedColor(@ColorInt color: Int): Banner<*, *> {
        if (indicatorConfig != null) {
            indicatorConfig!!.setIndicatorSelectedColor(color)
        }
        return this
    }

    fun setIndicatorSelectedColorRes(@ColorRes color: Int): Banner<*, *> {
        setIndicatorSelectedColor(ContextCompat.getColor(context, color))
        return this
    }

    fun setIndicatorNormalColor(@ColorInt color: Int): Banner<*, *> {
        indicatorConfig?.setIndicatorNormalColor(color)
        return this
    }

    fun setIndicatorNormalColorRes(@ColorRes color: Int): Banner<*, *> {
        setIndicatorNormalColor(ContextCompat.getColor(context, color))
        return this
    }

    fun setIndicatorGravity(@IndicatorConfig.Direction gravity: Int): Banner<*, *> {
        if (indicatorConfig != null && indicatorConfig?.isAttachToBanner == true) {
            indicatorConfig?.setIndicatorGravity(gravity)
            indicator?.getIndicatorView()?.postInvalidate()
        }
        return this
    }

    fun setIndicatorSpace(indicatorSpace: Int): Banner<*, *> {

        indicatorConfig?.setIndicatorSpace(indicatorSpace)
        return this
    }

    fun setIndicatorMargins(margins: IndicatorConfig.Margins?): Banner<*, *> {
        if (indicatorConfig != null && indicatorConfig?.isAttachToBanner == true) {
            indicatorConfig?.setMargins(margins!!)
            indicator?.getIndicatorView()?.requestLayout()
        }
        return this
    }

    fun setIndicatorWidth(normalWidth: Int, selectedWidth: Int): Banner<*, *> {

        indicatorConfig?.setIndicatorNormalWidth(normalWidth)
        indicatorConfig?.setIndicatorSelectedWidth(selectedWidth)
        return this
    }

    fun setIndicatorNormalWidth(normalWidth: Int): Banner<*, *> {

        indicatorConfig?.setIndicatorNormalWidth(normalWidth)
        return this
    }

    fun setIndicatorSelectedWidth(selectedWidth: Int): Banner<*, *> {

        indicatorConfig?.setIndicatorSelectedWidth(selectedWidth)
        return this
    }

    fun setIndicatorRadius(indicatorRadius: Int): Banner<*, *> {
        indicatorConfig?.setIndicatorRadius(indicatorRadius)
        return this
    }

    fun setIndicatorHeight(indicatorHeight: Int): Banner<*, *> {
        indicatorConfig?.setIndicatorHeight(indicatorHeight)
        return this
    }

    fun updateHeight(scaledHeight: Float) {
        LogUtils.d("scaledHeight = $scaledHeight")
        val lp = layoutParams
        lp.height = scaledHeight.toInt()
        layoutParams = lp
        viewPager2.requestLayout()
        requestLayout()
        invalidate()
    }

//    fun setNewInstance(bannerList: MutableList<T>) {
//        adapter?.setNewInstance(bannerList)
//        setCurrentItem(startPosition, false)
//        setIndicatorPageChange()
//        start()
//    }

    companion object {
        const val TAG = "WolfBanner"
        const val INVALID_VALUE = -1
        const val HORIZONTAL = 0
        const val VERTICAL = 1


        fun getRealPosition(isIncrease: Boolean, position: Int, realCount: Int): Int {
//           LogUtils.dTag(TAG,"getRealPosition: isIncrease = $isIncrease , position = $position , realCount = $realCount")
            if (!isIncrease) {
                return position
            }
            val realPosition: Int = when (position) {
                0 -> {
                    realCount - 1
                }

                realCount + 1 -> {
                    0
                }

                else -> {
                    position - 1
                }
            }
            return realPosition
        }
    }
}
