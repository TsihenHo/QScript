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
import org.apache.commons.jexl3.JexlBuilder
import org.apache.commons.jexl3.MapContext
import org.jsoup.Jsoup
import java.util.*
import java.util.regex.Pattern
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class ExamActivity : BaseActivity() {
    private lateinit var mViewBinding: ActivityExamBinding
    private var trueResult: String? = null
    private val handler = Handler {
        mViewBinding.showExam.text =
            mViewBinding.showExam.text.toString()
                .replace("%function%", it.data.getString("function") ?: "读取失败")
                .replace("%order%", it.data.getString("order") ?: "读取失败")
        false
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = ActivityExamBinding.inflate(layoutInflater)
        setContentView(mViewBinding.root)
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
            val order = arrayOf(1, 2, 2, 3, 3, 3, 4, 4, 4, 5)[random.nextInt(10)]
            for (i: Int in 1..30) {
                // 控制难度，每次循环有 2/9 的概率新增一项
                if (random.nextBoolean() || random.nextBoolean() || random.nextInt(9) == 0) continue
                when (random.nextInt(6)) {
                    0 -> function.append("${random.nextInt(50)}*x^2")
                    1 -> function.append("${random.nextInt(50)}*x")
                    2 -> function.append("${random.nextInt(50)}")
                    3 -> function.append(
                        "${random.nextInt(5)}*sqrt(x${
                            if (random.nextBoolean() || random.nextBoolean()) "" // 每次有 1/4 的概率在括号里面增加一次项
                            else "-${random.nextInt(20)}"
                        })"
                    )
                    4 -> function.append(
                        "${random.nextInt(5)}*cos(x${
                            if (random.nextBoolean() || random.nextBoolean()) ""
                            else "-${random.nextInt(20)}"
                        })"
                    )
                    5 -> function.append(
                        "${random.nextInt(5)}*sin(x${
                            if (random.nextBoolean() || random.nextBoolean()) ""
                            else "-${random.nextInt(20)}"
                        })"
                    )
                }
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

                // 当两者计算结果相同，就一定是对的
                if (eval(trueResult!!, x) == eval(userInput, x)) {
                    Toasts.success(this, "成功")
                    ConfigManager.getDefaultConfig()["pass_by_exam"] = true
                } else {
                    Toasts.error(this, "失败")
                }
            }
            else -> {
            }
        }
    }

    private fun eval(str: String, x: Int): Any {
        try {
            val jc = MapContext()
            jc.set("x", x)
            val jexl = JexlBuilder().create()
            var function = str
            // 计算 jexl 无法计算的，如 sqrt, sin, cos
            val reSqrt = Pattern.compile("sqrt\\(([x0-9-]*)\\)")
            val reCos = Pattern.compile("cos\\(([x0-9-]*)\\)")
            val reSin = Pattern.compile("sin\\(([x0-9-]*)\\)")

            val mTrueSqrt = reSqrt.matcher(function)
            val mTrueCos = reCos.matcher(function)
            val mTrueSin = reSin.matcher(function)

            while (mTrueSqrt.find()) {
                function = function.replace(
                    mTrueSqrt.group(0)!!,
                    sqrt(
                        jexl.createExpression(mTrueSqrt.group(1)).evaluate(jc)
                            .toString().toInt().toDouble()
                    ).toString()
                )
            }
            while (mTrueCos.find()) {
                function = function.replace(
                    mTrueCos.group(0)!!,
                    cos(
                        jexl.createExpression(mTrueCos.group(1)).evaluate(jc)
                            .toString().toInt().toDouble()
                    ).toString()
                )
            }
            while (mTrueSin.find()) {
                function = function.replace(
                    mTrueSin.group(0)!!,
                    sin(
                        jexl.createExpression(mTrueSin.group(1)).evaluate(jc)
                            .toString().toInt().toDouble()
                    ).toString()
                )
            }
            //            logd("ExamActivity : 经过处理后, function = $function , x = $x, result = $myTrueResultNum")
            return jexl.createExpression(function).evaluate(jc)
        } catch (e: java.lang.Exception) {
            log(e)
            return "error"
        }
    }
}