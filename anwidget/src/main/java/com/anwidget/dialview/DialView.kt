package com.anwidget.dialview

import android.animation.*
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.anwidget.R
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin


/**
 * 给color添加透明度
 * @param alpha 透明度 0f～1f
 * @return
 */
fun Int.colorWithAlpha(alpha: Float): Int {
    val a = min(255, max(0, (alpha * 255).toInt())) shl 24
    val rgb = 0x00ffffff and this
    return a + rgb
}

fun Number.dpToPx(): Number {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )
}

enum class ViewState {
    PRE,   //预加载状态
    UPDATE,//更新中
    NONE   //空闲中
}

class DialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "DialView"
        const val PROGRESS_MAX_ANGLE = 180f
        const val PROGRESS_START_OFFSET_ANGLE = -90f
        const val TEXT_SPACE_ANGLE = 21f
        const val TEXT_START_OFFSET_ANGLE = -85f
    }

    var viewState = ViewState.PRE
    private var progressAnimator: ValueAnimator? = null
    private val indicator = Indicator(this)
    private val progressBackground = ProgressBackground(this)
    private val progress = Progress(this)
    private val text = Text(this)
    private val line = Line(this)
    private val unit = Unit(this)


    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.DialView)
        val indicatorColor = ta.getColor(R.styleable.DialView_indicator_color, Color.BLACK)
        val indicatorRadius = ta.getDimension(R.styleable.DialView_indicator_radius, 0f)
        val indicatorStrokeColor =
            ta.getColor(R.styleable.DialView_indicator_stroke_color, Color.WHITE)
        val indicatorStrokeWidth =
            ta.getDimension(R.styleable.DialView_indicator_stroke_width, 0f)
        val progressBackgroundColor =
            ta.getColor(R.styleable.DialView_progress_bg_color, Color.BLACK)
        val progressBackgroundWidth = ta.getDimension(R.styleable.DialView_progress_bg_width, 0f)
        val progressColor = ta.getColor(R.styleable.DialView_progress_color, Color.BLACK)
        val progressWidth = ta.getDimension(R.styleable.DialView_progress_width, 0f)
        val textColorNormal = ta.getColor(R.styleable.DialView_text_color_normal, Color.BLACK)
        val textColorSelect = ta.getColor(R.styleable.DialView_text_color_select, Color.BLACK)
        val textSizeNormal = ta.getDimension(R.styleable.DialView_text_size_normal, 0f)
        val textSizeSelect = ta.getDimension(R.styleable.DialView_text_size_select, 0f)
        val unitTitle = ta.getString(R.styleable.DialView_unit_title) ?: "Mb/s"
        ta.recycle()

        indicator.color = indicatorColor
        indicator.radius = indicatorRadius
        indicator.strokeColor = indicatorStrokeColor
        indicator.strokeWidth = indicatorStrokeWidth
        progressBackground.color = progressBackgroundColor
        progressBackground.width = progressBackgroundWidth
        progress.color = progressColor
        progress.width = progressWidth
        text.normalColor = textColorNormal
        text.selectColor = textColorSelect
        text.normalTextSize = textSizeNormal
        text.selectTextSize = textSizeSelect
        unit.text = unitTitle
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val maxDisplaySize = RectF(0F, 0F, w.toFloat(), h.toFloat())

        val progressPadding = indicator.radius
        progressBackground.onSizeChanged(maxDisplaySize, progressPadding)
        progress.onSizeChanged(maxDisplaySize, progressPadding)
        indicator.onSizeChanged(maxDisplaySize, progressPadding)

        val textPadding = indicator.radius * 2 + 30.dpToPx().toInt()
        text.onSizeChanged(maxDisplaySize, textPadding)

        val linePadding = textPadding + 38.dpToPx().toInt()
        line.onSizeChanged(maxDisplaySize, linePadding)

        unit.onSizeChanged(maxDisplaySize, 0f)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            canvas.translate(-width / 2f, 0f)
            progressBackground.draw(canvas)
            progress.draw(canvas)
            indicator.draw(canvas)
            text.draw(canvas)
            line.draw(canvas)
            unit.draw(canvas)
        }
    }

    fun updateText(numberList: List<Int>) {
        post {
            viewState = ViewState.PRE
            //先执行progressBackground动画
            ValueAnimator.ofFloat(0f, PROGRESS_MAX_ANGLE).apply {
                startDelay = 1000
                duration = 1000
                addUpdateListener {
                    progressBackground.sweepAngle = it.animatedValue as Float
                    postInvalidate()
                }
            }.start()
            //500ms后播放行文字动画
            val animatorList = mutableListOf<Animator>()
            text.updateText(numberList).forEachIndexed { index, item ->
                val scaleAnim = ValueAnimator.ofFloat(0f, 0.5f, 1f, 1.3f, 1f).apply {
                    startDelay = (100 * index).toLong()
                    duration = 500L
                    addUpdateListener {
                        item.scale = it.animatedValue as Float
                        postInvalidate()
                    }
                }
                val alphaAnim = ValueAnimator.ofFloat(0f, 0.1f, 0.3f, 0.5f, 0.2f).apply {
                    startDelay = (100 * index).toLong()
                    duration = 500L
                    addUpdateListener {
                        item.alpha = it.animatedValue as Float
                    }
                }
                animatorList.add(scaleAnim)
                animatorList.add(alphaAnim)
            }
            AnimatorSet().apply {
                startDelay = 2000
                playTogether(animatorList)
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        viewState = ViewState.NONE
                    }
                })
                start()
            }

            ValueAnimator.ofFloat(0f, PROGRESS_MAX_ANGLE).apply {
                startDelay = 2000
                duration = 10 * PROGRESS_MAX_ANGLE.toLong()
                addUpdateListener {
                    line.sweepAngle = it.animatedValue as Float
                    postInvalidate()
                }
            }.start()
        }
    }

    fun updateCurrentNumber(currentNumber: Float) {
        viewState = ViewState.UPDATE
        if (currentNumber.compareTo(0) <= 0) {
            updateProgress(0f)
            return
        }
        Log.d(TAG, "current=$currentNumber min=${text.numberItems.last().number} ")

        if (currentNumber.compareTo(text.numberItems.last().number) >= 0) {
            updateProgress(PROGRESS_MAX_ANGLE)
            return
        }
        text.numberItems.forEachIndexed lit@{ index, numberItem ->
            if (index < 1) return@lit
            val min = text.numberItems[index - 1].number
            val max = numberItem.number

            if (currentNumber.compareTo(min) >= 0 && currentNumber.compareTo(max) < 0) {
                val bb = (currentNumber - min) / (max - min)
                val angle = 5 + (index - 1) * TEXT_SPACE_ANGLE + bb * TEXT_SPACE_ANGLE
                updateProgress(angle)
                return
            }
        }
    }

    fun updateUnit(unitTitle: String) {
        unit.text = unitTitle
        postInvalidate()
    }

    private fun updateProgress(angle: Float) {
        progressAnimator?.cancel()
        progressAnimator = ValueAnimator.ofFloat(progress.sweepAngle, angle).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                val sweepAngle = it.animatedValue as Float
                indicator.sweepAngle = sweepAngle
                progress.sweepAngle = sweepAngle
                text.sweepAngle = sweepAngle
                postInvalidate()
            }
            start()
        }
    }
}


abstract class Child(view: DialView) {
    abstract fun onSizeChanged(rectF: RectF, padding: Float)
    abstract fun draw(canvas: Canvas)
}


/**
 * 绘制进度条的背景
 * **/
internal class ProgressBackground(private val view: DialView) : Child(view) {

    var sweepAngle = -1f
    var width: Float = 0f
    var color = Color.WHITE
    private var oval = RectF()
    private var paint: Paint = Paint().also {
        it.isAntiAlias = true
        it.style = Paint.Style.STROKE
        it.strokeCap = Paint.Cap.ROUND
    }

    override fun onSizeChanged(rectF: RectF, padding: Float) {
        oval.left = rectF.left + padding
        oval.top = rectF.top + padding
        oval.right = rectF.right - padding
        oval.bottom = rectF.bottom - padding
        paint.shader = LinearGradient(
            oval.left,
            oval.top,
            oval.left,
            oval.bottom,
            color.colorWithAlpha(0.1f),
            color.colorWithAlpha(0f),
            Shader.TileMode.CLAMP
        )
    }

    override fun draw(canvas: Canvas) {
        paint.strokeWidth = width
        canvas.drawArc(
            oval,
            DialView.PROGRESS_START_OFFSET_ANGLE,
            sweepAngle,
            false,
            paint
        )
    }
}

/**
 * 绘制进度
 * **/
internal class Progress(private val view: DialView) : Child(view) {

    var sweepAngle = -1f
    var width: Float = 0f
    var color = Color.WHITE
    private var oval = RectF()
    private var paint: Paint = Paint().also {
        it.isAntiAlias = true
        it.style = Paint.Style.STROKE
    }


    override fun onSizeChanged(rectF: RectF, padding: Float) {
        oval.left = rectF.left + padding
        oval.top = rectF.top + padding
        oval.right = rectF.right - padding
        oval.bottom = rectF.bottom - padding
    }

    override fun draw(canvas: Canvas) {
        if (view.viewState == ViewState.PRE) return
        paint.color = color
        paint.strokeWidth = width
        canvas.drawArc(
            oval,
            DialView.PROGRESS_START_OFFSET_ANGLE,
            sweepAngle,
            false,
            paint
        )
    }
}

/**
 * 绘制指示器
 * **/
internal class Indicator(private val view: DialView) : Child(view) {

    //绘制的属性
    var radius: Float = 0f
    var color: Int = Color.WHITE
    var strokeColor: Int = Color.WHITE
    var strokeWidth: Float = 0f

    var sweepAngle: Float = DialView.PROGRESS_START_OFFSET_ANGLE
        set(value) {
            field = value + DialView.PROGRESS_START_OFFSET_ANGLE
        }

    //所在的圆区域
    private var cX: Float = 0f
    private var cY: Float = 0f
    private var cRadius: Float = 0f
    private val oval = RectF()
    private val paint: Paint = Paint().also {
        it.isAntiAlias = true
    }

    override fun onSizeChanged(rectF: RectF, padding: Float) {
        oval.left = rectF.left + padding
        oval.top = rectF.top + padding
        oval.right = rectF.right - padding
        oval.bottom = rectF.bottom - padding
        cX = (oval.right + oval.left) / 2
        cY = (oval.bottom + oval.top) / 2
        cRadius = oval.width() / 2
    }

    override fun draw(canvas: Canvas) {
        if (view.viewState == ViewState.PRE) return
        //计算在圆上的坐标
        val pointX = cX + cRadius * cos(Math.toRadians(sweepAngle.toDouble())).toFloat()
        val pointY = cY + cRadius * sin(Math.toRadians(sweepAngle.toDouble())).toFloat()
        //绘制大圆
        paint.color = strokeColor
        canvas.drawCircle(pointX, pointY, radius, paint)
        //绘制小圆
        paint.color = color
        canvas.drawCircle(pointX, pointY, radius - strokeWidth, paint)
        //绘制阴影部分
        paint.setShadowLayer(radius, 0f, 0f, color.colorWithAlpha(0.5f))
    }
}


internal class Text(private val view: DialView) : Child(view) {
    val numberItems = mutableListOf<NumberItem>()
    var sweepAngle = -1f
    var normalTextSize = 0f
    var selectTextSize = 0f
    var normalColor: Int = Color.WHITE
    var selectColor: Int = Color.WHITE

    private var cRadius = 0f
    private var cX = 0f
    private var cY = 0f
    private var paint: Paint = Paint().also {
        it.textAlign = Paint.Align.CENTER //让文字水平居中
        it.isAntiAlias = true
    }

    fun updateText(numberList: List<Int>): List<NumberItem> {
        numberItems.clear()
        val bounds = Rect()
        numberList.forEachIndexed { index, number ->
            val title = if (index > 5 && number >= 1000) {
                "${number / 1000}k"
            } else {
                number.toString()
            }
            //让文字垂直居中
            paint.textSize = normalTextSize
            paint.getTextBounds(title, 0, title.length, bounds)
            val offsetY = (bounds.top + bounds.bottom) / 2

            val angle =
                (index * DialView.TEXT_SPACE_ANGLE + DialView.TEXT_START_OFFSET_ANGLE).toDouble()
            val pointX = cX + cRadius * cos(Math.toRadians(angle)).toFloat()
            val pointY = cY + cRadius * sin(Math.toRadians(angle)).toFloat() - offsetY
            numberItems.add(NumberItem(pointX, pointY, angle.toFloat(), 0F, 0F, title, number))
        }
        return numberItems
    }


    override fun onSizeChanged(rectF: RectF, padding: Float) {
        cRadius = rectF.width() / 2 - padding
        cX = rectF.right / 2
        cY = rectF.bottom / 2
    }

    override fun draw(canvas: Canvas) {
        if (view.viewState == ViewState.PRE) {
            drawInit(canvas)
        } else {
            drawNormal(canvas)
        }
    }

    private fun drawInit(canvas: Canvas) {
        paint.color = normalColor
        numberItems.forEach {
            paint.textSize = normalTextSize * it.scale
            paint.color = normalColor.colorWithAlpha(it.alpha)

            if (paint.textSize > 0) {
                canvas.drawText(it.title, it.cX, it.cY, paint)
            }
        }
    }

    private fun drawNormal(canvas: Canvas) {
        numberItems.forEach {
            if (sweepAngle + DialView.TEXT_START_OFFSET_ANGLE >= it.angle && sweepAngle + DialView.TEXT_START_OFFSET_ANGLE < it.angle + DialView.TEXT_SPACE_ANGLE) {
                paint.color = selectColor
                paint.textSize = selectTextSize
            } else {
                paint.color = normalColor.colorWithAlpha(it.alpha)
                paint.textSize = normalTextSize
            }
            canvas.drawText(it.title, it.cX, it.cY, paint)
        }
    }

    data class NumberItem(
        var cX: Float = 0F,
        var cY: Float = 0F,
        var angle: Float = 0F,
        var scale: Float = 0F,
        var alpha: Float = 0F,
        val title: String,
        val number: Int
    )
}


internal class Line(private val view: DialView) : Child(view) {
    private val pointList = mutableListOf<LinePoints>()
    private val oval = RectF()
    var sweepAngle: Float = DialView.TEXT_START_OFFSET_ANGLE
        set(value) {
            field = value + DialView.TEXT_START_OFFSET_ANGLE
        }

    private val paint: Paint = Paint().also {
        it.isAntiAlias = true
        it.strokeWidth = 1F
        it.color = Color.WHITE.colorWithAlpha(0.1F)
    }


    override fun onSizeChanged(rectF: RectF, padding: Float) {
        oval.left = rectF.left + padding
        oval.top = rectF.top + padding
        oval.right = rectF.right - padding
        oval.bottom = rectF.bottom - padding

        val cX = (oval.right + oval.left) / 2
        val cY = (oval.bottom + oval.top) / 2
        val outRadius = oval.width() / 2
        val inRadius = outRadius - 25.dpToPx().toInt()
        for (i in 0..12) {
            val sweepAngle =
                (DialView.TEXT_START_OFFSET_ANGLE + i * DialView.TEXT_SPACE_ANGLE / 2).toDouble() //根据文字间隔设置
            //长短交替
            val realRadius = if (i % 2 == 0) {
                outRadius
            } else {
                outRadius - (outRadius - inRadius) * 2 / 3
            }
            //计算外层坐标
            val outPointX =
                cX + realRadius * cos(Math.toRadians(sweepAngle)).toFloat()
            val outPointY =
                cY + realRadius * sin(Math.toRadians(sweepAngle)).toFloat()

            //计算内层坐标
            val inPointX =
                cX + inRadius * cos(Math.toRadians(sweepAngle)).toFloat()
            val intPointY =
                cY + inRadius * sin(Math.toRadians(sweepAngle)).toFloat()
            pointList.add(LinePoints(outPointX, outPointY, inPointX, intPointY, sweepAngle))
        }
    }

    override fun draw(canvas: Canvas) {
        if (view.viewState == ViewState.PRE) {
            pointList.forEach {
                if (it.angle < sweepAngle)
                    canvas.drawLine(it.iX, it.iY, it.oX, it.oY, paint)
            }
        } else {
            pointList.forEach {
                canvas.drawLine(it.iX, it.iY, it.oX, it.oY, paint)
            }
        }
    }

    data class LinePoints(
        val oX: Float,
        val oY: Float,
        val iX: Float,
        val iY: Float,
        val angle: Double
    )
}

/**
 * 单位
 * **/
class Unit(private val view: DialView) : Child(view) {
    var text = ""
    private val textOffsetX = 5.dpToPx().toFloat()
    private var textOffsetY = 0f
    private val ovalBg = RectF()
    private val ovalText = RectF()
    private val paintBg = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.WHITE.colorWithAlpha(0.05f)
    }
    private val paintText = Paint().apply {
        textSize = 12.dpToPx().toFloat()
        textAlign = Paint.Align.LEFT
        color = Color.WHITE.colorWithAlpha(0.5f)
    }


    override fun onSizeChanged(rectF: RectF, padding: Float) {
        val w = 46.dpToPx().toFloat()
        val h = 16.dpToPx().toFloat()
        ovalBg.set(
            rectF.centerX() - w,
            rectF.centerY() - h,
            rectF.centerX() + w,
            rectF.centerY() + h
        )
        ovalText.set(rectF)
        val textBounds = Rect()
        paintText.getTextBounds(text, 0, text.length, textBounds)
        textOffsetY = (textBounds.top + textBounds.bottom) / 2f
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(ovalBg, ovalBg.width() / 2, ovalBg.width() / 2, paintBg)
        canvas.drawText(
            text,
            ovalText.centerX() + textOffsetX,
            ovalText.centerY() - textOffsetY,
            paintText
        )
    }
}
