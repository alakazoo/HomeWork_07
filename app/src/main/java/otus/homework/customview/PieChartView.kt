package otus.homework.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.PI

import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.asin
import kotlin.math.acos
import kotlin.math.sqrt

class PieChartView @JvmOverloads constructor (
    context: Context,
    attributeSet: AttributeSet?
) : View(context, attributeSet) {

    private var viewData: MutableMap<String, PieChartModel> = mutableMapOf()
    private var sum = 0.0
    private var radius = min(width/2f, height/2f)
    private var center = Point(width/2, height/2)
    private var currentCategory = ""
    private lateinit var callback: ()->Unit

    fun draw(data: List<Model>, callback: ()->Unit ) {
        viewData.clear()
        sum = data.sumOf { it.amount }
        currentCategory = data.first().category
        this.callback = callback

        var angle = 0f
        var sweepAngle = 0f
        for ( m in data.sortedBy{ it.category } ) {
            sweepAngle = (m.amount/sum*360f).toFloat()

            if ( viewData[m.category] != null ) {
                viewData[m.category]!! += sweepAngle
            } else {
                viewData[m.category] = PieChartModel(angle, sweepAngle)
            }

            angle += sweepAngle
        }

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var offset = 0f
        val radius3 = radius/3f

        var i = 0
        for (v in viewData) {
            offset = if ( i % 2 > 0  ) 20f else 0f

            val inRect = RectF(
                center.x - radius + offset,
                center.y - radius + offset,
                center.x + radius - offset,
                center.y + radius - offset)

            val outRect = RectF(
                center.x - radius3 + offset,
                center.y - radius3 + offset,
                center.x + radius3 - offset,
                center.y + radius3 - offset)

            var path = Path().apply {
                arcTo(inRect, v.value.angle, v.value.sweepAngle)
                arcTo(outRect, v.value.angle + v.value.sweepAngle, -v.value.sweepAngle)
                close()
            }

            with(canvas) {
                drawPath(path, paints[(i++)%paints.count()])
                drawPath(path, strokePaint)
                drawTextOnPath(
                    "${((v.value.sweepAngle/360)*100).roundToInt()}%",
                    path,
                    10f,
                    textPaint.textSize+1f,
                    textPaint
                )
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 100
        val desiredHeight = 100

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(desiredWidth, widthSize)
            else -> desiredWidth
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(desiredHeight, heightSize)
            else -> desiredHeight
        }

        setMeasuredDimension(width, height)

        // сразу же тут посчитаем основные размеры графика
        radius = min(width,height)/2f
        center = Point(width/2, height/2)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when( event.action ) {
            MotionEvent.ACTION_DOWN -> {
                var x = (event.x-center.x)/radius // координаты в единичной окружности
                var y = (event.y-center.y)/radius
                if ( x <= 1 && y <= 1) {
                    var a = acos(x / sqrt(x*x+y*y)) * 180 / PI // из формулы cos угла между векторами
                    if (y<0) a = 360 - a // т.к. формула не умеет в 360 то пытаемся это исправить

                    for (m in viewData) {
                        if ( a  >= m.value.angle && a <= m.value.angle + m.value.sweepAngle) {
                            currentCategory = m.key
                            callback()
                            return true
                        }
                    }
                    false
                } else
                    false
            }
            else -> super.onTouchEvent(event)
        }
    }

    fun getCategory():String {
        return currentCategory
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putString(BUNDLE_PIECHART_CURRENT_CATEGORY, currentCategory)
        bundle.putParcelable(BUNDLE_PIECHART_STATE, super.onSaveInstanceState())
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val bundle = state as Bundle
        currentCategory = bundle.getString(BUNDLE_PIECHART_CURRENT_CATEGORY, "")
        super.onRestoreInstanceState(bundle.getParcelable(BUNDLE_PIECHART_STATE))
    }

    companion object {
        const val BUNDLE_PIECHART_CURRENT_CATEGORY = "bundle_piechart_current_category"
        const val BUNDLE_PIECHART_STATE = "bundle_piechart_state"

        val textPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            textSize = 20f
            isFakeBoldText = true
        }

        val strokePaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }

        val paints: List<Paint> = listOf(
            Paint().apply {
                isAntiAlias = true
                color = Color.rgb(239,169,74) },
            Paint().apply {
                isAntiAlias = true
                color = Color.rgb(127,181,181) },
            Paint().apply {
                isAntiAlias = true
                color = Color.rgb(93,155,155) },
            Paint().apply {
                isAntiAlias = true
                color = Color.rgb(161,133,148) },
            Paint().apply {
                isAntiAlias = true
                color = Color.rgb(119,221,119) },
            Paint().apply {
                isAntiAlias = true
                color = Color.rgb(255,117,20) },
            Paint().apply {
                isAntiAlias = true
                color = Color.rgb(255,140,105) },
            Paint().apply {
                isAntiAlias = true
                color = Color.rgb(255,155,170) },
            Paint().apply {
                isAntiAlias = true
                color = Color.rgb(255,178,139) },
            Paint().apply {
                isAntiAlias = true
                color = Color.rgb(252,232,131) },
        )

    }
}

data class PieChartModel (
    var angle: Float,
    var sweepAngle: Float
) {
    operator fun plusAssign(counter: Float)  {
        this.sweepAngle += counter
    }
}


