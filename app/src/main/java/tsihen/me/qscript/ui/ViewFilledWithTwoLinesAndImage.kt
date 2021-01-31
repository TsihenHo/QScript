package tsihen.me.qscript.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import tsihen.me.qscript.R
import tsihen.me.qscript.databinding.ViewFilledWithTwoLinesAndImageBinding

class ViewFilledWithTwoLinesAndImage(ctx: Context, attrs: AttributeSet?) : FrameLayout(ctx, attrs) {
    private var mViewBinding: ViewFilledWithTwoLinesAndImageBinding =
        ViewFilledWithTwoLinesAndImageBinding.inflate(LayoutInflater.from(context), this, true)

    var title: CharSequence? = null
        set(value) {
            field = value
            mViewBinding.viewFilledWithTwoLinesAndImageTvTitle.text = field
            invalidate()
            requestLayout()
        }
    var desc: CharSequence? = null
        set(value) {
            field = value
            mViewBinding.viewFilledWithTwoLinesAndImageTvDesc.text = field
            invalidate()
            requestLayout()
        }
    var image: Int? = null
        set(value) {
            field = value
            mViewBinding.viewFilledWithTwoLinesAndImageIv.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    field ?: R.drawable.ic_failure_white
                )
            )
            invalidate()
            requestLayout()
        }
    var color: Drawable? = null
        set(value) {
            field = value
            mViewBinding.viewFilledWithTwoLinesAndImageRoot.background =
                field ?: ContextCompat.getDrawable(context, R.drawable.bg_ripple_yellow)
            invalidate()
            requestLayout()
        }
    private var mListener: IOnClickListenerFilled? = null
    private var mLongListener: IOnLongClickListenerFilled? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ViewFilledWithTwoLinesAndImage,
            0,
            0
        ).apply {
            try {
                title = getString(R.styleable.ViewFilledWithTwoLinesAndImage_viewFilled_title)
                    ?: "(Nothing)"
                desc = getString(R.styleable.ViewFilledWithTwoLinesAndImage_viewFilled_desc)
                    ?: "(Nothing)"
                image = getResourceId(
                    R.styleable.ViewFilledWithTwoLinesAndImage_viewFilled_image,
                    R.drawable.ic_failure_white
                )
                color = try {
                    getDrawable(R.styleable.ViewFilledWithTwoLinesAndImage_viewFilled_color)
                } catch (e: Exception) {
                    ContextCompat.getDrawable(context, R.drawable.bg_ripple_yellow)
                }
            } finally {
                recycle()
            }
        }
        mViewBinding.root.setOnClickListener { mListener?.onClick(this) } // 至关重要
        mViewBinding.root.setOnLongClickListener { mLongListener?.onLongClick(this) ?: false }
        mViewBinding.viewFilledWithTwoLinesAndImageRoot.background =
            color ?: ContextCompat.getDrawable(context, R.drawable.bg_ripple_yellow)
        mViewBinding.viewFilledWithTwoLinesAndImageTvTitle.text = title
        mViewBinding.viewFilledWithTwoLinesAndImageTvDesc.text = desc
        mViewBinding.viewFilledWithTwoLinesAndImageIv.setImageDrawable(
            ContextCompat.getDrawable(
                context, image ?: R.drawable.ic_failure_white
            )
        )
        isClickable = true
    }

    fun changeColor(isGreen: Boolean) {
        color = if (isGreen) {
            ContextCompat.getDrawable(context, R.drawable.bg_ripple_green)
        } else {
            ContextCompat.getDrawable(context, R.drawable.bg_ripple_red)
        }
        mViewBinding.root.background = color
    }

    fun setOnClickListener(l: IOnClickListenerFilled) {
        mListener = l
    }

    fun setOnLongClickListener(l: IOnLongClickListenerFilled) {
        mLongListener = l
    }

    override fun performClick(): Boolean {
        mListener?.onClick(this)
        return super.performClick()
    }
}

interface IOnClickListenerFilled {
    fun onClick(v: ViewFilledWithTwoLinesAndImage)
}

interface IOnLongClickListenerFilled {
    fun onLongClick(v: ViewFilledWithTwoLinesAndImage): Boolean
}