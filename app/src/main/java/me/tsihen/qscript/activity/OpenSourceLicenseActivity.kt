package me.tsihen.qscript.activity

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.TextView
import de.psdev.licensesdialog.LicensesDialog
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20
import de.psdev.licensesdialog.licenses.BSD2ClauseLicense
import de.psdev.licensesdialog.licenses.GnuGeneralPublicLicense30
import de.psdev.licensesdialog.licenses.MITLicense
import de.psdev.licensesdialog.model.Notice
import de.psdev.licensesdialog.model.Notices
import me.tsihen.qscript.R
import me.tsihen.qscript.databinding.ActivityOpenSourceLicenseBinding

class OpenSourceLicenseActivity : BaseActivity() {
    private lateinit var mViewBinding: ActivityOpenSourceLicenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = ActivityOpenSourceLicenseBinding.inflate(layoutInflater)
        setContentView(mViewBinding.root)

        mViewBinding.topAppBar.setNavigationOnClickListener { finish() }
        val notices = Notices()
        notices.addNotice(
            Notice(
                "QNotified",
                "https://github.com/ferredoxin/QNotified",
                "Copyright (C) 2019-2020 xenonhydride@gmail.com",
                GnuGeneralPublicLicense30()
            )
        )
        notices.addNotice(
            Notice(
                "MathParser.org-mXparser",
                "http://mathparser.org/",
                "Copyright 2010-2019 MARIUSZ GROMADA. All rights reserved.",
                BSD2ClauseLicense()
            )
        )
        notices.addNotice(
            Notice(
                "jsoup",
                "https://jsoup.org/",
                "Copyright (c) 2009-2021 Jonathan Hedley <https://jsoup.org/>",
                MITLicense()
            )
        )
        notices.addNotice(
            Notice(
                "material-design-icons",
                "https://github.com/google/material-design-icons",
                "Google",
                ApacheSoftwareLicense20()
            )
        )
        notices.addNotice(LicensesDialog.LICENSES_DIALOG_NOTICE)
        val lp = ViewGroup.LayoutParams(MATCH_PARENT, 1)
        notices.notices.forEach {
            val line = View(this)
            line.setBackgroundColor(
                ColorStateList.valueOf(
                    Color.argb(
                        255,
                        128,
                        128,
                        128
                    )
                ).defaultColor
            )
            line.layoutParams = lp

            mViewBinding.oplRoot.addView(getView(it))
            mViewBinding.oplRoot.addView(line)
        }
        mViewBinding.oplRoot.removeViewAt(mViewBinding.oplRoot.childCount - 1)
    }

    @SuppressLint("SetTextI18n")
    private fun getView(notice: Notice): View {
        val convertView = layoutInflater.inflate(R.layout.item_license, mViewBinding.oplRoot, false)
        val title = convertView.findViewById<TextView>(R.id.sLicenseItem_title)
        val licenseView = convertView.findViewById<TextView>(R.id.sLicenseItem_licensePrev)
        title.setTextColor(ColorStateList.valueOf(-0x1000000))
        licenseView.setTextColor(ColorStateList.valueOf(-0x1000000))
        licenseView.typeface = Typeface.MONOSPACE
        title.highlightColor = ColorStateList.valueOf(Color.argb(255, 0, 182, 249)).defaultColor

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) title.text =
            Html.fromHtml(
                "<b>●${notice.name}</b><br />(<a href=${notice.url}>${notice.url}</a>)",
                Html.FROM_HTML_MODE_LEGACY
            )
        else
            title.text = Html.fromHtml(
                "<b>●${notice.name}</b><br />(<a href=${notice.url}>${notice.url}</a>)",
            )
        licenseView.text = """
            ${notice.copyright}
            
            ${notice.license.getSummaryText(this@OpenSourceLicenseActivity)}
            """.trimIndent()
        return convertView
    }
}