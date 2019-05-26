package com.example.fitday

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.Toast
import java.util.Calendar
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element

class AboutActivity : AppCompatActivity() {




    internal val copyRightsElement: Element
        get() {
            val copyRightsElement = Element()
            val copyrights = String.format("\u00a9 WPPT, Wrocław 2019", Calendar.getInstance().get(Calendar.YEAR))
            copyRightsElement.setTitle(copyrights)
            copyRightsElement.iconNightTint = android.R.color.white
            copyRightsElement.gravity = Gravity.CENTER
            copyRightsElement.onClickListener =
                View.OnClickListener { Toast.makeText(this@AboutActivity, copyrights, Toast.LENGTH_SHORT).show() }
            return copyRightsElement
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val aboutPage = AboutPage(this)
            .setDescription("Track your calories consumption and store your progress. App created by students form" +
                    " Faculty of Fundamental Problems of Technology, as Mobile Applications course project led by dr inż. Marcin Zawada")
            .addGroup("Creators:")
            .addGroup("Piotr Andrzejewski")
            .addGroup("Sebastian Fojcik")
            .addGroup("Bartosz Stajnowski")
            .addGroup("Mateusz Trzeciak")
            .isRTL(false)
            .setImage(R.mipmap.ic_launcher)
            .addItem(Element().setTitle("Version 1.0"))
            .addGroup("Connect with us")
            .addEmail("fojcik.sebastian@gmail.com ")
            .addWebsite("http://wppt.pwr.edu.pl/")
            .addPlayStore("test")
            .addGitHub("/ketrab321/FitDay")
            .addItem(copyRightsElement)
            .create()

        setContentView(aboutPage)
    }

}
