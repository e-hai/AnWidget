package com.an.widget

import android.content.Context

fun Float.dip2px(context: Context): Int {
    val scale: Float = context.resources.displayMetrics.density
    return (this * scale + 0.5f).toInt()
}