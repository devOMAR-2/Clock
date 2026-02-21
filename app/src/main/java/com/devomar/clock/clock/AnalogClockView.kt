package com.devomar.clock.clock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.devomar.clock.R
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class AnalogClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paintFace = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val paintBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    private val paintHour = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val paintMinute = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val paintSecond = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val paintTick = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val paintCenter = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f

    var calendar: Calendar = Calendar.getInstance()
        set(value) {
            field = value
            invalidate()
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        centerX = w / 2f
        centerY = h / 2f
        radius = min(w, h) / 2f * 0.88f
        updatePaints()
    }

    private fun updatePaints() {
        paintFace.color = context.getColor(R.color.clockFace)
        paintBorder.color = context.getColor(R.color.clockBorder)
        paintBorder.strokeWidth = radius * 0.025f
        paintHour.color = context.getColor(R.color.clockHourHand)
        paintHour.strokeWidth = radius * 0.045f
        paintMinute.color = context.getColor(R.color.clockMinuteHand)
        paintMinute.strokeWidth = radius * 0.030f
        paintSecond.color = context.getColor(R.color.clockSecondHand)
        paintSecond.strokeWidth = radius * 0.015f
        paintTick.color = context.getColor(R.color.clockTick)
        paintCenter.color = context.getColor(R.color.clockCenter)
    }

    override fun onDraw(canvas: Canvas) {
        updatePaints()

        // Clock face
        canvas.drawCircle(centerX, centerY, radius, paintFace)
        canvas.drawCircle(centerX, centerY, radius, paintBorder)

        // Tick marks
        for (i in 0 until 60) {
            val angle = Math.toRadians(i * 6.0 - 90)
            val isHour = i % 5 == 0
            val tickOuter = radius * 0.97f
            val tickInner = if (isHour) radius * 0.82f else radius * 0.90f
            paintTick.strokeWidth = if (isHour) radius * 0.028f else radius * 0.014f
            canvas.drawLine(
                (centerX + tickInner * cos(angle)).toFloat(),
                (centerY + tickInner * sin(angle)).toFloat(),
                (centerX + tickOuter * cos(angle)).toFloat(),
                (centerY + tickOuter * sin(angle)).toFloat(),
                paintTick
            )
        }

        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        // Hour hand
        val hourAngle = Math.toRadians((hour * 30 + minute * 0.5) - 90)
        drawHand(canvas, paintHour, hourAngle, radius * 0.50f)

        // Minute hand
        val minuteAngle = Math.toRadians(minute * 6.0 - 90)
        drawHand(canvas, paintMinute, minuteAngle, radius * 0.72f)

        // Second hand
        val secondAngle = Math.toRadians(second * 6.0 - 90)
        drawHand(canvas, paintSecond, secondAngle, radius * 0.82f)

        // Center dot
        canvas.drawCircle(centerX, centerY, radius * 0.04f, paintCenter)
    }

    private fun drawHand(canvas: Canvas, paint: Paint, angle: Double, length: Float) {
        canvas.drawLine(
            centerX, centerY,
            (centerX + length * cos(angle)).toFloat(),
            (centerY + length * sin(angle)).toFloat(),
            paint
        )
    }
}
