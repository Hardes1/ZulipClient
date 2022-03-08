package com.example.tinkoff.CustomViews


import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.example.tinkoff.R


class EmojiView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {

    private var text: String = ""

    private val textPaint = TextPaint().apply {
        isAntiAlias = true
    }

    private val tempBounds = Rect()
    private val tempViewPoint = PointF()


    init {
        val defaultTextSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            TEXT_SIZE,
            resources.displayMetrics
        )
        val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.EmojiView)
        text = typedArray.getString(R.styleable.EmojiView_text).orEmpty()
        textPaint.textSize =
            typedArray.getDimension(R.styleable.EmojiView_textSize, defaultTextSize)
        textPaint.color =
            typedArray.getColor(
                R.styleable.EmojiView_textColor,
                Color.WHITE
            )
        typedArray.recycle()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        textPaint.getTextBounds(text, 0, text.length, tempBounds)
        val sumWidth = tempBounds.width() + paddingLeft + paddingRight
        val sumHeight = tempBounds.height() + paddingTop + paddingBottom
        val resultWidth = resolveSize(sumWidth, widthMeasureSpec)
        val resultHeight = resolveSize(sumHeight, heightMeasureSpec)
        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        tempViewPoint.y = h / 2f + tempBounds.height() / 2f - textPaint.descent()
        tempViewPoint.x = w / 2f - tempBounds.width() / 2f

    }


    override fun onDraw(canvas: Canvas?) {
        canvas?.drawText(text, tempViewPoint.x, tempViewPoint.y, textPaint)
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + SUPPORTED_DRAWABLE_STATE.size)
        if (isSelected) {
            mergeDrawableStates(drawableState, SUPPORTED_DRAWABLE_STATE)
        }
        return drawableState
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        isSelected = !isSelected
    }

    companion object {
        private const val TEXT_SIZE = 15f
        private val SUPPORTED_DRAWABLE_STATE = intArrayOf(android.R.attr.state_selected)
    }

}
