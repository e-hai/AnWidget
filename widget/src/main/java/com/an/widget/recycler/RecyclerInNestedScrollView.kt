package com.an.widget.recycler

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView

/***
 * 需求场景：在同向滑动的NestedScrollView内部嵌套了RecyclerView，希望滑动RecyclerView时，无论是否到达边界都不去拦截
 * 解决方案：通过NestedScrolling机制，在回调中不去消费RecyclerView的滑动事件
 * **/
class RVSVScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : NestedScrollView(context, attrs) {


    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        if (target is RVSVRecyclerView) {
            return
        }
        super.onNestedScroll(
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )
    }
}

class RVSVRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs)