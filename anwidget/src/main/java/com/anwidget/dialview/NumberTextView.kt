package com.anwidget.dialview

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.RelativeLayout
import com.anwidget.R
import com.robinhood.ticker.TickerUtils
import com.robinhood.ticker.TickerView


class NumberTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : RelativeLayout(context, attrs) {

    private val rect = Rect()
    private var bgColor: Int
    private var paint: Paint = Paint().also {
        it.isAntiAlias = true
        it.style = Paint.Style.FILL
    }

    private var tickerView: TickerView


    init {
        LayoutInflater.from(context).inflate(R.layout.view_number, this, true)

        val ta = context.obtainStyledAttributes(attrs, R.styleable.NumberTextView)
        bgColor = ta.getColor(R.styleable.NumberTextView_background_color, Color.BLACK)
        val textColor = ta.getColor(R.styleable.NumberTextView_text_color, Color.WHITE)
        val textSize = ta.getDimension(R.styleable.NumberTextView_text_size, 10f)
        ta.recycle()
        tickerView = findViewById(R.id.tickerView)
        tickerView.textColor = textColor
        tickerView.textSize = textSize
        tickerView.animationDuration = 360
        tickerView.gravity = Gravity.CENTER
        tickerView.setPreferredScrollingDirection(TickerView.ScrollingDirection.UP)
        tickerView.setCharacterLists(TickerUtils.provideNumberList())
        tickerView.setText("0.00", false)
        setWillNotDraw(false)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.left = w
        rect.right = w
        rect.top = 0
        rect.bottom = h

        paint.shader = LinearGradient(
            0.toFloat(),
            0.toFloat(),
            w.toFloat(),
            h.toFloat(),
            bgColor.colorWithAlpha(0f),
            bgColor.colorWithAlpha(0.5f),
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawRect(rect, paint)
        super.onDraw(canvas)
    }

    fun startAnim(duration: Long = 800) {
        ObjectAnimator.ofFloat(0f, 1f).apply {
            this.duration = duration
            addUpdateListener {
                val current = it.animatedValue as Float
                rect.left = rect.right - (rect.right * current).toInt()
                postInvalidate()
            }
            start()
        }
    }

    fun endAnim(duration: Long = 800) {
        ObjectAnimator.ofFloat(1f, 0f).apply {
            this.duration = duration
            addUpdateListener {
                val current = it.animatedValue as Float
                rect.left = rect.right - (rect.right * current).toInt()
                postInvalidate()
            }
            start()
        }
    }

    fun setText(content: String) {
        tickerView.setText(content, true)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }
}