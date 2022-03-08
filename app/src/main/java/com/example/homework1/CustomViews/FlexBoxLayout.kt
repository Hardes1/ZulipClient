package com.example.homework1.CustomViews

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.example.homework1.R
import timber.log.Timber
import java.lang.Integer.max

class FlexBoxLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet) :
    ViewGroup(context, attrs) {

    init {
        inflate(context, R.layout.flexbox_layout, this)
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
            if (currentWidth + child.measuredWidth <= MeasureSpec.getSize(widthMeasureSpec)) {
                currentWidth += child.measuredWidth
                maxWidth = max(currentWidth, maxWidth)
                maxHeight = max(currentHeight, maxHeight)
            } else {
                currentWidth = child.measuredWidth
                currentHeight += child.measuredHeight
                maxWidth = max(currentWidth, maxWidth)
                maxHeight = max(currentHeight, maxHeight)
            }
        }
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
                currentHeight += child.measuredHeight
                currentWidth = 0
                child.layout(
                    currentWidth,
                    currentHeight,
                    currentWidth + child.measuredWidth,
                    currentHeight + child.measuredHeight
                )
            }
            Timber.d(
                "childLocation: l = $currentWidth," +
                        " t = $currentHeight," +
                        " r = ${currentWidth + child.measuredWidth}," +
                        " b = ${currentHeight + child.measuredHeight}"
            )
            currentWidth += child.measuredWidth
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