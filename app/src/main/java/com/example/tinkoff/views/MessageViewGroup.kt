package com.example.tinkoff.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.tinkoff.R

class MessageViewGroup @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ViewGroup(context, attrs) {

    init {
        inflate(context, R.layout.message_layout, this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val imageView = findViewById<ImageView>(R.id.avatar_icon)
        val flexBoxLayout = findViewById<FlexBoxLayout>(R.id.flex_box_layout)
        val messageContent = findViewById<ConstraintLayout>(R.id.message_content)
        measureChildWithMargins(
            imageView,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            0
        )
        measureChildWithMargins(
            messageContent,
            widthMeasureSpec,
            imageView.measuredWidth,
            heightMeasureSpec,
            0
        )
        measureChildWithMargins(
            flexBoxLayout,
            widthMeasureSpec,
            imageView.measuredWidth,
            heightMeasureSpec,
            messageContent.measuredHeight
        )
        val contentWidth =
            imageView.measuredWidthWithMargins + flexBoxLayout.measuredWidthWithMargins
        val contentHeight =
            flexBoxLayout.measuredHeightWithMargins + messageContent.measuredHeightWithMargins
        val resultWidth = resolveSize(contentWidth, widthMeasureSpec)
        val resultHeight = resolveSize(contentHeight, heightMeasureSpec)
        setMeasuredDimension(
            resultWidth,
            resultHeight
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val imageView = findViewById<ImageView>(R.id.avatar_icon)
        val flexBoxLayout = findViewById<FlexBoxLayout>(R.id.flex_box_layout)
        val messageContent = findViewById<ConstraintLayout>(R.id.message_content)
        imageView.layout(
            imageView.marginLeft,
            imageView.marginTop,
            imageView.marginLeft + imageView.measuredWidth,
            imageView.marginTop + imageView.measuredHeight
        )
        val messageContentLeft = imageView.right + imageView.marginRight + messageContent.marginLeft
        messageContent.layout(
            messageContentLeft,
            messageContent.marginTop,
            messageContentLeft + messageContent.measuredWidth,
            messageContent.marginTop + messageContent.measuredHeight
        )

        val flexBoxLayoutLeft = imageView.right + imageView.marginRight + flexBoxLayout.marginLeft
        val flexBoxLayoutTop =
            messageContent.bottom + messageContent.marginBottom + flexBoxLayout.marginTop
        flexBoxLayout.layout(
            flexBoxLayoutLeft,
            flexBoxLayoutTop,
            flexBoxLayoutLeft + flexBoxLayout.measuredWidth,
            flexBoxLayoutTop + flexBoxLayout.measuredHeight
        )
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

    private val View.marginTop: Int
        get() = (layoutParams as MarginLayoutParams).topMargin

    private val View.marginBottom: Int
        get() = (layoutParams as MarginLayoutParams).bottomMargin

    private val View.marginRight: Int
        get() = (layoutParams as MarginLayoutParams).rightMargin

    private val View.marginLeft: Int
        get() = (layoutParams as MarginLayoutParams).leftMargin

    private val View.measuredWidthWithMargins: Int
        get() {
            val params = layoutParams as MarginLayoutParams
            return measuredWidth + params.rightMargin + params.leftMargin
        }
    private val View.measuredHeightWithMargins: Int
        get() {
            val params = layoutParams as MarginLayoutParams
            return measuredHeight + params.topMargin + params.bottomMargin
        }


}
