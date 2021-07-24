package com.example.pomodoro

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes

class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var currentMs = 0F
    private var color = 0
    private val paint = Paint()

    init {
        if (attrs != null) {
            val styledAttrs = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CustomView,
                defStyleAttr,
                0
            )
            color = styledAttrs.getColor(R.styleable.CustomView_custom_color, Color.RED)
            styledAttrs.recycle()
        }

        paint.color = color
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (currentMs == 0F) return
        val sweepAngle = currentMs

        canvas.drawArc(
            0F,
            0F,
            width.toFloat(),
            height.toFloat(),
            -90F,
            sweepAngle,
            true,
            paint
        )
    }

    fun setCurrent(current: Float) {
        currentMs = current
        invalidate()
    }
}