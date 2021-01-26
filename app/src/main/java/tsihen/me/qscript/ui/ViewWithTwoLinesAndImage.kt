@file:JvmName("ViewWithTwoLinesAndImage")

package tsihen.me.qscript.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.view_two_lines_and_image.view.*
import tsihen.me.qscript.R

class ViewWithTwoLinesAndImage(ctx: Context, attrs: AttributeSet) : FrameLayout(ctx, attrs) {
    var title: CharSequence? = null
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }
    var desc: CharSequence? = null
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }
    var image: Int? = null
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }
    var withArrow: Boolean? = null
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }
    private var mListener: IOnClickListener? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ViewWithTwoLinesAndImage,
            0,
            0
        ).apply {
            try {
                title = getString(R.styleable.ViewWithTwoLinesAndImage_title) ?: "(Nothing)"
                desc = getString(R.styleable.ViewWithTwoLinesAndImage_desc) ?: "(Nothing)"
                image = getResourceId(
                    R.styleable.ViewWithTwoLinesAndImage_image,
                    R.drawable.ic_failure_white
                )
                withArrow = getBoolean(R.styleable.ViewWithTwoLinesAndImage_arrow, false)
            } finally {
                recycle()
            }
        }
        LayoutInflater.from(context).inflate(R.layout.view_two_lines_and_image, this, true)
        view_with_two_lines_and_image_root.setOnClickListener { mListener?.onClick(this) } // 至关重要
        view_with_two_lines_and_image_tvTitle.text = title
        view_with_two_lines_and_image_tvDesc.text = desc
        view_with_two_lines_and_image_iv.setImageDrawable(ContextCompat.getDrawable(context, image!!))
        view_with_two_lines_and_image_ivArrow.isVisible = withArrow!!
        isClickable = true
    }

    fun setOnClickListener(l: IOnClickListener) {
        mListener = l
    }

    override fun performClick(): Boolean {
        mListener?.onClick(this)
        return super.performClick()
    }
}

interface IOnClickListener {
    fun onClick(v: ViewWithTwoLinesAndImage)
}