package com.example.tinkoff.views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.ImageView
import com.example.tinkoff.R
import kotlin.math.max
import kotlin.random.Random

class FlexBoxLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ViewGroup(context, attrs) {

    private val marginHorizontal: Int
    private val marginVertical: Int


    companion object {
        private const val MARGIN_HORIZONTAL = 10f
        private const val MARGIN_VERTICAL = 6f
    }

    private var iteration = 0

    init {
        val defaultHorizontalMargin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            MARGIN_HORIZONTAL,
            resources.displayMetrics
        )
        val defaultVerticalMargin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            MARGIN_VERTICAL,
            resources.displayMetrics
        )

        inflate(context, R.layout.flexbox_layout, this)
        val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.FlexBoxLayout)
        marginHorizontal = typedArray.getDimension(
            R.styleable.FlexBoxLayout_marginHorizontal,
            defaultHorizontalMargin
        ).toInt()
        marginVertical = typedArray.getDimension(
            R.styleable.FlexBoxLayout_marginVertical,
            defaultVerticalMargin
        ).toInt()
        typedArray.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        var i = 0
        val randomSmile = arrayOf("\uD83D\uDE02", "\uD83D\uDE05", "\uD83D\uDE0A", "\uD83D\uDE0D")
        val bound = 1337
        val random = Random(bound)
        while (i < childCount) {
            val child = getChildAt(i)
            if (i + 1 < childCount) {
                (child as EmojiView).setOnClickListener {
                    it.isSelected = !it.isSelected
                }
            } else {
                (child as ImageView).setOnClickListener {
                    addView(
                        EmojiView.builder(
                            context,
                            resources,
                            "${random.nextInt(bound)} ${randomSmile.random()}"
                        ),
                        childCount - 1
                    )
                    forceLayout()
                }
            }
            i++
        }

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var currentWidth = 0
        var currentHeight = 0
        var maxWidth = 0
        var maxHeight = 0
        var i = 0
        while (i < childCount) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            when {
                currentWidth + child.measuredWidth + marginHorizontal <=
                        MeasureSpec.getSize(widthMeasureSpec) -> {
                    currentWidth += child.measuredWidth + marginHorizontal
                }
                currentWidth + child.measuredWidth <= MeasureSpec.getSize(widthMeasureSpec) -> {
                    currentWidth += child.measuredWidth
                }
                else -> {
                    currentWidth = child.measuredWidth + marginHorizontal
                    currentHeight += child.measuredHeight + marginVertical
                }
            }
            if (i + 1 == childCount) {
                currentHeight += child.measuredHeight
            }
            maxWidth = max(currentWidth, maxWidth)
            maxHeight = max(currentHeight, maxHeight)
            i++


        }

        maxWidth += paddingLeft + paddingRight
        maxHeight += paddingTop + paddingBottom
        val resultWidth = resolveSize(maxWidth, widthMeasureSpec)
        val resultHeight = resolveSize(maxHeight, heightMeasureSpec)
        setMeasuredDimension(
            resultWidth,
            resultHeight
        )
        iteration++
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentWidth = 0
        var currentHeight = 0
        var i = 0
        while (i < childCount) {
            val child = getChildAt(i)
            if (currentWidth + child.measuredWidth <= measuredWidth) {
                child.layout(
                    currentWidth,
                    currentHeight,
                    currentWidth + child.measuredWidth,
                    currentHeight + child.measuredHeight
                )
            } else {
                currentHeight += child.measuredHeight + marginVertical
                currentWidth = 0
                child.layout(
                    currentWidth,
                    currentHeight,
                    currentWidth + child.measuredWidth,
                    currentHeight + child.measuredHeight
                )
            }
            currentWidth += child.measuredWidth + marginHorizontal
            i++
        }
    }


    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun checkLayoutParams(p: LayoutParams): Boolean {
        return p is MarginLayoutParams
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return MarginLayoutParams(p)
    }

}
