package com.sample.recycler

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.sample.R
import com.an.widget.recycler.RecyclerInViewPager
import com.an.widget.recycler.SimpleAdapter

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
        attachOneFragment(view)
    }

    /**
     * ViewPager2横向滑动，其中一个Fragment里有（垂直滑动的NestScrollView，
     * NestScrollView里嵌套着）横向滑动的RecyclerView，分析滑动事件处在RecyclerView上的各种情况。
     */
    private fun attachOneFragment(view: View) {
        val viewPager = view.findViewById<ViewPager2>(R.id.viewpager)
        val fragments = listOf(OneFragment(viewPager), EmptyFragment())
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

class OneFragment(private val viewPager: ViewPager2) : Fragment() {
    private val dataList = listOf(
        "ABC=======",
        "DEF=======",
        "GHI=======",
        "JKG=======",
        "123=======",
        "456=======",
        "789=======",
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_recycler_in_pager_test_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerInViewPager>(R.id.recycler).apply {
            layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
            adapter = object : SimpleAdapter<String, OneViewHolder>() {
                override fun onBindViewHolder(holder: OneViewHolder, model: String) {
                    holder.titleView.apply {
                        layoutParams = LayoutParams(WRAP_CONTENT, 100)
                        gravity = Gravity.CENTER
                        text = model
                    }
                }

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OneViewHolder {
                    return OneViewHolder(TextView(parent.context))
                }
            }.apply {
                setData(dataList)
            }
            attachViewPager2(viewPager)
        }
    }

    class OneViewHolder(val titleView: TextView) : ViewHolder(titleView)
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






