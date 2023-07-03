package com.widget.recycler

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2


/**
 * 解决同向滑动时，RecyclerView与ViewPager2事件冲突
 *
 * 默认情况下
 *      (1)RecyclerView不处于边界，事件快速滑动有规律被ViewPager2抢占
 *      (2)RecyclerView处于边界，事件被ViewPager2抢占
 * 分析原因
 *      由于ViewPager2内部包裹RecyclerView实现滑动，因此冲突的是父RecyclerView嵌套同向的子RecyclerView
 * 处理方案是
 *      (1)若需求是横向事件在Child RecyclerView上，无论是否处于边界，都由Child RecyclerView处理:
 *      由于ViewPager2未公开Parent RecyclerView，获取RecyclerView来处理事件，
 *      但是通过setUserInputEnabled(boolean)来禁止Parent RecyclerView的滑动可以达到目的,
 *      源码是重写Parent RecyclerView的onInterceptTouchEvent()和onTouchEvent(),
 *      先判断isUserInputEnabled后调用super方法。
 *          至于不用requestDisallowInterceptTouchEvent()，是有场景Child RecyclerView在NestScrollView中,
 *      NestScrollView在ViewPager2中，会同时禁止NestScrollView的垂直滑动拦截。
 * **/
class RecyclerViewInViewPager2 : RecyclerView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                viewPager2?.isUserInputEnabled = false
            }
            MotionEvent.ACTION_MOVE -> {
                viewPager2?.isUserInputEnabled = false
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                viewPager2?.isUserInputEnabled = true
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private var viewPager2: ViewPager2? = null

    fun attachViewPager2(viewPager: ViewPager2) {
        viewPager2 = viewPager
    }
}