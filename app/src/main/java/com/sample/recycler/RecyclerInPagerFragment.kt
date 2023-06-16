package com.sample.recycler

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.sample.R
import com.widget.recycler.SimpleAdapter
import com.widget.recycler.quickInflate

/**
 * ViewPager里嵌套RecyclerView的各种场景的解决方案
 * **/
class RecyclerInPagerFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recycler_in_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        one(view)
    }

    /**
     * ViewPager横向滑动，其中一个Fragment里有垂直滑动的NestScrollView，NestScrollView里嵌套着横向滑动的RecyclerView，
     * 分析滑动事件处在RecyclerView上的各种情况。
     * 默认情况下：
     * （1）RecyclerView不处于边界，事件不会被ViewPager抢占
     *  (2)RecyclerView处于边界，事件有概率被ViewPager抢占
     * 原因是：
     * 处理方案是：
     * **/
    private fun one(view: View) {
        val fragments = listOf(OneFragment(), EmptyFragment())
        val viewPager = view.findViewById<ViewPager2>(R.id.viewpager)
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }

            override fun getItemCount(): Int {
                return fragments.size
            }
        }
    }
}

class OneFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_recycler_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.recycler_one).apply {
            layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
            adapter = object : SimpleAdapter<String, OneViewHolder>() {
                override fun onBindViewHolder(holder: OneViewHolder, model: String) {
                    (holder.itemView as TextView).apply {
                        layoutParams = LayoutParams(100, 100)
                        gravity = Gravity.CENTER
                        text = model
                    }
                }

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OneViewHolder {
                    val textView = TextView(parent.context)
                    return OneViewHolder(textView)
                }
            }.apply {
                setData(
                    listOf(
                        "A",
                        "B",
                        "C",
                        "D",
                        "E",
                        "F",
                        "G",
                        "H",
                        "I",
                        "J",
                        "K",
                        "G",
                        "1",
                        "2",
                        "3",
                        "4",
                        "5",
                        "6"
                    )
                )
            }

        }
    }

    class OneViewHolder(view: View) : ViewHolder(view)
}

class EmptyFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return LinearLayout(context).apply {
            setBackgroundColor(Color.BLUE)
        }
    }
}






