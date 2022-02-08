package com.garfield.learandroid

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.garfield.learandroid.ui.main.MainFragment
import kotlinx.coroutines.MainScope

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
        initView()
    }

    private fun test(){
        val mAudioManager: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

    }

    private fun initView() {

        findViewById<TextView>(R.id.btnTestKotinHandeException).setOnClickListener {
            onClickTestKotlinException()
        }
    }


    fun onClickTestKotlinException() {
        TestKotlin.testException(scope = MainScope())
    }
}