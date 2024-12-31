package com.kit.view.pager

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.kit.view.R
import com.kit.view.dip2px


/**
 * 功能一：自动轮播
 * 功能二：带指示器
 * 功能三：无限轮播
 * **/
class Banner @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RelativeLayout(context, attrs) {

    private val viewpager: ViewPager2

    //页面间隔
    private var marginPx: Int = 10f.dip2px(context)


    init {
        LayoutInflater.from(context).inflate(R.layout.pager_banner, this, true)
        viewpager = findViewById<ViewPager2>(R.id.viewpager).apply {
            isUserInputEnabled = false
            clipChildren = false
            offscreenPageLimit = 3
//            adapter = SimpleAdapter(img)
            setPageTransformer(CompositePageTransformer().apply {
                addTransformer(ZoomOutPageTransformer())
                addTransformer(MarginPageTransformer(marginPx))
            })
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                }
            })
        }
    }

    private fun setMarginPx(margin: Int) {

    }
}


class ZoomOutPageTransformer : ViewPager2.PageTransformer {

    companion object {
        //自由控制缩放比例
        private const val MIN_SCALE_Y = 0.9f //0.85f
    }

    override fun transformPage(page: View, position: Float) {
        if (position >= 1 || position <= -1) {
            page.scaleY = MIN_SCALE_Y
        } else if (position < 0) {
            //  -1 < position < 0
            //View 在再从中间往左边移动，或者从左边往中间移动
            val scaleY = MIN_SCALE_Y + (1 + position) * (1 - MIN_SCALE_Y)
            page.scaleY = scaleY
        } else {
            // 0 <= positin < 1
            //View 在从中间往右边移动 或者从右边往中间移动
            val scaleY = (1 - MIN_SCALE_Y) * (1 - position) + MIN_SCALE_Y
            page.scaleY = scaleY
        }
    }
}