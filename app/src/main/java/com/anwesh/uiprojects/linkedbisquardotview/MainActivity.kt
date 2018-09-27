package com.anwesh.uiprojects.linkedbisquardotview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.bisquaredotview.BiSquareDotView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BiSquareDotView.create(this)
    }
}