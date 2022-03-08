package com.example.TFSandroid.CustomViews

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import com.example.TFSandroid.R
import timber.log.Timber
import kotlin.math.max

class FlexBoxLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ViewGroup(context, attrs) {

    private val marginHorizontal: Int
    private val marginVertical: Int


    companion object {
        private const val MARGIN_HORIZONTAL = 10f
        private const val MARGIN_VERTICAL = 6f
    }

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


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Timber.d("child count: $childCount")
        var currentWidth = 0
        var currentHeight = 0
        var maxWidth = 0
        var maxHeight = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            if (currentWidth + child.measuredWidth + marginHorizontal <=
                MeasureSpec.getSize(widthMeasureSpec)
            ) {
                currentWidth += child.measuredWidth + marginHorizontal
                maxWidth = max(currentWidth, maxWidth)
                maxHeight = max(currentHeight, maxHeight)
            } else {
                currentWidth = child.measuredWidth + marginVertical
                currentHeight += child.measuredHeight
                maxWidth = max(currentWidth, maxWidth)
                maxHeight = max(currentHeight, maxHeight)
            }
        }
        maxWidth += paddingLeft + paddingRight
        maxHeight += paddingTop + paddingBottom
        setMeasuredDimension(
            resolveSize(maxWidth, widthMeasureSpec),
            resolveSize(maxHeight, heightMeasureSpec)
        )

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentWidth = 0
        var currentHeight = 0
        for (i in 0 until childCount) {
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
            Timber.d(
                "childLocation: l = %d, t = %d, r = %d, b = %d".format(
                    currentWidth,
                    currentHeight,
                    currentWidth + child.measuredWidth,
                    currentHeight + measuredHeight
                )
            )
            currentWidth += child.measuredWidth + marginHorizontal
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