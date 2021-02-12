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
package me.tsihen.qscript.activity

import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.annotation.RequiresApi
import me.tsihen.qscript.R
import me.tsihen.qscript.config.ConfigManager
import me.tsihen.qscript.databinding.ActivityExamBinding
import me.tsihen.qscript.util.Toasts
import me.tsihen.qscript.util.log
import me.tsihen.qscript.util.logd
import org.jsoup.Jsoup
import org.mariuszgromada.math.mxparser.Argument
import org.mariuszgromada.math.mxparser.Expression
import java.util.*
import java.util.regex.Pattern


class ExamActivity : BaseActivity() {
    private lateinit var mViewBinding: ActivityExamBinding
    private var trueResult: String? = null
    private val handler = Handler {
        when (it.what) {
            0 -> {
                mViewBinding.showExam.text =
                    mViewBinding.showExam.text.toString()
                        .replace("%function%", it.data.getString("function") ?: "读取失败")
                        .replace("%order%", it.data.getString("order") ?: "读取失败")
            }
        }
        false
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = ActivityExamBinding.inflate(layoutInflater)
        setContentView(mViewBinding.root)
        mViewBinding.topAppBar.setNavigationOnClickListener { finish() }
        refresh()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun refresh() {
        try {
            val builder = ProgressDialog(this)
            builder.setTitle("请稍等")
            builder.setMessage("应用正在初始化")
            builder.show()
            builder.setOnCancelListener { this.finish() }

            mViewBinding.showExam.text = getString(R.string.derived_function)
            val function = StringBuilder()
            val random = Random()
            val order = arrayOf(1, 2, 2, 2, 3, 3, 4, 4, 4, 5)[random.nextInt(10)]
            for (i: Int in 1..30) {
                // 控制难度，每次循环有 2/9 的概率新增一项
                if (random.nextBoolean() || random.nextBoolean() || random.nextInt(9) == 0) continue
                function.append(build(random))
                function.append(if (random.nextBoolean()) "+" else "-")
            }
            function.deleteCharAt(function.length - 1)
            Thread {
                val doc = Jsoup.connect("https://zh.numberempire.com/derivativecalculator.php")
                    .header(
                        "Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3"
                    )
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                    .header("Cache-Control", "no-cache")
                    .header("Pragma", "no-cache")
                    .header("Proxy-Connection", "keep-alive")
                    .header(
                        "User-Agent",
                        "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1 (compatible; Baiduspider-render/2.0; +http://www.baidu.com/search/spider.html)"
                    )
                    .data("function", function.toString())
                    .data("order", order.toString())
                    .post()
                trueResult = doc.getElementById("result1").text()
                val msg = Message()
                val data = Bundle()
                data.putString("function", function.toString())
                data.putString("order", order.toString())
                msg.what = 0
                msg.data = data
                handler.sendMessage(msg)
                logd("ExamActivity : TrueResult is $trueResult")
                builder.dismiss()
            }.start()
        } catch (e: Exception) {
            log(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onButtonClick(v: View) {
        when (v.id) {
            R.id.change_exam -> {
                refresh()
            }
            R.id.apply -> {
                var x = Random().nextInt(5000)
                if (x == 0) x = 501
                val userInput = mViewBinding.et.text.toString()
                val copy = userInput.replace("sqrt", "")
                    .replace("sin", "")
                    .replace("cos", "")
                    .replace("sqrt", "")
                    .replace("ln", "")
                    .replace("x", "")
                    .replace("(", "")
                    .replace(")", "")
                    .replace(".", "")
                    .replace("+", "")
                    .replace("-", "")
                    .replace("*", "")
                    .replace("/", "")
                    .replace("^", "")
                val matcher = Pattern.compile("[^\\d]+").matcher(copy)
                if (matcher.find()) {
                    Toasts.error(this, "表达式不合法")
                    return
                }

                try {
                    val trueResultNum = eval(trueResult!!, x)
                    val userResultNum = eval(userInput, x)

                    if (trueResultNum + 0.0001 > userResultNum && trueResultNum - 0.001 < userResultNum) {
                        Toasts.success(this, "成功")
                        ConfigManager.getDefaultConfig()["pass_by_exam"] = true
                    } else {
                        Toasts.error(this, "失败：结果错误")
                    }
                } catch (e: java.lang.Exception) {
                    log(e)
                    Toasts.error(this, "失败：表达式不合法")
                }
            }
            else -> {
            }
        }
    }

    private fun eval(str: String, x: Int): Double {
        val func = str.replace("ln", "log")
        val valueOfX = Argument("x = $x")
        val e = Expression(func, valueOfX)
        return e.calculate()
    }

    private fun build(random: Random): StringBuilder {
        val function = StringBuilder()

        when (random.nextInt(7)) {
            0 -> function.append("${random.nextInt(50)}*x^2")
            1 -> function.append("${random.nextInt(50)}*x")
            2 -> function.append("${random.nextInt(50)}")
            3 -> function.append(
                "${random.nextInt(5)}*sqrt(${
                    if (random.nextBoolean() || random.nextBoolean()) "x" // 每次有 1/4 的概率在括号里面增加非x项
                    else if (random.nextBoolean()) "${build(random)}"
                    else "-${build(random)}"
                }${
                    if (random.nextBoolean() || random.nextBoolean()) "" // 小概率增加
                    else if (random.nextBoolean()) "+${build(random)}" else "-${build(random)}"
                })"
            )
            4 -> function.append(
                "${random.nextInt(5)}*cos(${
                    if (random.nextBoolean() || random.nextBoolean()) "x" // 每次有 1/4 的概率在括号里面增加项
                    else if (random.nextBoolean()) "${build(random)}"
                    else "-${build(random)}"
                }${
                    if (random.nextBoolean() || random.nextBoolean()) "" // 小概率增加
                    else if (random.nextBoolean()) "+${build(random)}" else "-${build(random)}"
                })"
            )
            5 -> function.append(
                "${random.nextInt(5)}*sin(${
                    if (random.nextBoolean() || random.nextBoolean()) "x" // 每次有 1/4 的概率在括号里面增加项
                    else if (random.nextBoolean()) "${build(random)}"
                    else "-${build(random)}"
                }${
                    if (random.nextBoolean() || random.nextBoolean()) "" // 小概率增加
                    else if (random.nextBoolean()) "+${build(random)}" else "-${build(random)}"
                })"
            )
            6 -> function.append(
                "${random.nextInt(5)}*ln(${
                    if (random.nextBoolean() || random.nextBoolean()) "x" // 每次有 1/4 的概率在括号里面增加项
                    else if (random.nextBoolean()) "${build(random)}"
                    else "-${build(random)}"
                }${
                    if (random.nextBoolean() || random.nextBoolean()) "" // 小概率增加
                    else if (random.nextBoolean()) "+${build(random)}" else "-${build(random)}"
                })"
            )
        }

        return function
    }
}