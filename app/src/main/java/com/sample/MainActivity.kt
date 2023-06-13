package com.sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import com.sample.text.TextActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val contentLayout = findViewById<LinearLayout>(R.id.ll_func)
        listOf(
            createTextActivityBtn()
        ).forEach {
            contentLayout.addView(it)
        }
    }

    private fun createTextActivityBtn(): Button {
        return Button(this).apply {
            text = "TextActivity"
            setOnClickListener {
                startActivity(Intent(this@MainActivity, TextActivity::class.java))
            }
        }
    }
}


