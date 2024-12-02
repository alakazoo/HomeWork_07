package otus.homework.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.min

class BarGraphView @JvmOverloads constructor (
    context: Context,
    attributeSet: AttributeSet?
) : View(context, attributeSet) {

    private val dateFormat = SimpleDateFormat("dd/MM/yy")
    private var viewData: MutableMap<Long, Float> = mutableMapOf()
    private var maxAmount = 0f
    private var barWidth = 10f
    private var padding = 1f
    private var currentCategory = ""

    fun draw(data: List<Model>, category: String) {
        currentCategory = category
        viewData.clear()
        for ( m in data.sortedBy{ it.time } ) {
            if (m.category == currentCategory)
                viewData[m.time] = m.amount.toFloat()
        }
        maxAmount = viewData.maxOf { it.value }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        var offset = 0f
        var index = 0
        for (d in viewData) {
            val k = d.value / maxAmount

            var path = Path().apply {
                addRect(
                    offset,
                    height * (1 - k),
                    offset + barWidth,
                    height.toFloat(),
                    Path.Direction.CW
                )
            }

            with(canvas) {
                drawPath(path, paints[(index++) % paints.count()])
                drawPath(path, strokePaint)
                drawTextOnPath(
                    "${d.value} руб. ${dateFormat.format(Date(d.key))}",
                    path,
                    barWidth + 10f,
                    textPaint.textSize + 10f,
                    textPaint
                )
            }
            offset += barWidth + padding
        }
        super.onDraw(canvas)
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
        barWidth = (width/(viewData.count()+1)).toFloat()
        padding = barWidth/viewData.count().toFloat()
    }


    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putString(BUNDLE_BARGRAPH_CURRENT_CATEGORY, currentCategory)
        bundle.putParcelable(BUNDLE_BARGRAPH_STATE, super.onSaveInstanceState())
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val bundle = state as Bundle
        currentCategory = bundle.getString(BUNDLE_BARGRAPH_CURRENT_CATEGORY, "")
        super.onRestoreInstanceState(bundle.getParcelable(BUNDLE_BARGRAPH_STATE))
    }

    companion object {
        const val BUNDLE_BARGRAPH_CURRENT_CATEGORY = "bundle_bargraph_current_category"
        const val BUNDLE_BARGRAPH_STATE = "bundle_bargraph_state"

        val textPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            textSize = 30f
            isFakeBoldText = true
        }

        val strokePaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 2f
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