package com.widget.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView


/***
 * 由于有时候只需要很简单的列表，因此封装了这个Adapter
 * **/
abstract class SimpleAdapter<Model, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    private var dataList = emptyList<Model>()


    abstract fun onBindViewHolder(holder: VH, model: Model)

    fun setData(dataList: List<Model>) {
        this.dataList = dataList
        notifyItemRangeChanged(0, dataList.size)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        onBindViewHolder(holder, dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}

/**
 * 用于快速在父布局ViewGroup中，填充子控件resource
 * **/
fun ViewGroup.quickInflate(@LayoutRes resource: Int): View {
    return LayoutInflater.from(context).inflate(resource, this, true)
}
