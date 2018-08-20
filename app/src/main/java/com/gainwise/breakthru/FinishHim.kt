package com.gainwise.breakthru

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class FinishHim : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_him)
        val i = Intent(this, BreakThru::class.java)
        i.putExtra("end", "end")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(i)
        } else {
            this.startService(i)
        }
        finishAffinity()
    }
}
