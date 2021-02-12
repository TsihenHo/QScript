/* QScript - An Xposed module to run scripts on QQ
 * Copyright (C) 2021-2022 chinese.he.amber@gmail.com
 * https://github.com/GoldenHuaji/QScript
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package me.tsihen.qscript.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import me.tsihen.qscript.R

class CodeEditView(context: Context, attrs: AttributeSet?) : AppCompatEditText(context, attrs) {
    /** whether to set the lines visible or not	 */
    private var isLineNumberVisible = false
    private val lPaint: Paint = Paint()

    /** the gap between the line number and the left margin of the text  */
    var lineNumberMarginGap = 2
    var lineNumberTextColor: Int
        get() = lPaint.color
        set(textColor) {
            lPaint.color = textColor
        }

    override fun onDraw(canvas: Canvas) {
        // TODO: Implement this method
        if (isLineNumberVisible) {
            //set the size in case it changed after the last update
            lPaint.textSize = textSize - 2
            var baseLine = baseline
            var t = ""
            for (i in 0 until lineCount) {
                t = "" + (i + 1) + " "
                canvas.drawText(t, 0f, baseLine.toFloat(), lPaint)
                baseLine += lineHeight
            }
            // set padding again, adjusting only the left padding
            setPadding(
                lPaint.measureText(t).toInt() + lineNumberMarginGap, paddingTop,
                paddingRight, paddingBottom
            )
        }
        super.onDraw(canvas)
    }

    init {
        setHorizontallyScrolling(true)
        lPaint.isAntiAlias = true
        lPaint.style = Paint.Style.FILL
        lPaint.color = Color.GRAY
        lPaint.textSize = textSize - 2
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CodeEditView,
            0,
            0
        ).apply {
            try{
                isLineNumberVisible = getBoolean(R.styleable.CodeEditView_withLineNum, false)
            } finally {
                recycle()
            }
        }
    }
}