package com.kit.view.sample.text

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.kit.view.text.AnSpan
import com.kit.view.text.SpanStrBuilder
import com.kit.view.sample.R

class TextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)
        SpanStrBuilder()
            .span(
                AnSpan("ABC", 18, Color.BLUE, Color.WHITE) {
                    Toast.makeText(this, "ABC", Toast.LENGTH_SHORT).show()
                }
            ).span(
                AnSpan("DEF", 12, Color.RED, Color.WHITE) {
                    Toast.makeText(this, "DEF", Toast.LENGTH_SHORT).show()
                }
            ).span(
                AnSpan("GHI", 20, Color.YELLOW, Color.BLACK) {
                    Toast.makeText(this, "GHI", Toast.LENGTH_SHORT).show()
                }
            ).bindTextView(findViewById(R.id.tv_span))
    }
}