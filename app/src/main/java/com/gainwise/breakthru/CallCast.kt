package com.gainwise.breakthru

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log





class CallCast : CallReceiver(){

    override fun onIncomingCallStarted(ctx: Context, number: String?) {
        super.onIncomingCallStarted(ctx, number)
        val num = Settings.Global.getInt(ctx.getContentResolver(), "zen_mode")
        when(num){
            1,2,3 ->{
                val prefs = ctx.getSharedPreferences("MASTER", Context.MODE_PRIVATE);
                val jsonList: String? = prefs.getString("list", null)
                if (jsonList != null) {
                    var list1: List<Contact> = MainActivity.gson.fromJson(jsonList, MainActivity.typeOfSource)
                    val list = list1.asSequence().filter { it.on }.map { it.number }.toList()
                    if (list.contains(number) || list.contains(number.last7())) {
                        Log.i("breakthru1", "incoming called")
                        val i = Intent(ctx, BreakThru::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            ctx.startForegroundService(i)
                        } else {
                            ctx.startService(i)
                        }
                    }
                }
            }
            else ->{}
        }
//        val audio = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        when(audio.getRingerMode()){
//     AudioManager.RINGER_MODE_VIBRATE -> {
//                val prefs = ctx.getSharedPreferences("MASTER", Context.MODE_PRIVATE);
//                val jsonList: String? = prefs.getString("list", null)
//                if (jsonList != null) {
//                    var list1: List<Contact> = MainActivity.gson.fromJson(jsonList, MainActivity.typeOfSource)
//                    val list = list1.asSequence().filter { it.on }.map { it.number }.toList()
//                    if (list.contains(number) || list.contains(number.last7())) {
//                        Log.i("breakthru1", "incoming called")
//                        val i = Intent(ctx, BreakThru::class.java)
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            ctx.startForegroundService(i)
//                        } else {
//                            ctx.startService(i)
//                        }
//                    }
//                }
//            }else ->{
//
//        }
//       }
    }

    override fun onIncomingCallEnded(ctx: Context, number: String?) {
        super.onIncomingCallEnded(ctx, number)
        Log.i("breakthru1", "incoming ended")
        val i = Intent(ctx, BreakThru::class.java)
        i.putExtra("end", "end")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ctx.startForegroundService(i)
        } else {
            ctx.startService(i)
        }
    }

    override fun onOutgoingCallEnded(ctx: Context, number: String?) {
        super.onOutgoingCallEnded(ctx, number)
        Log.i("breakthru1", "outgoing ended")
    }

    override fun onOutgoingCallStarted(ctx: Context, number: String?) {
        super.onOutgoingCallStarted(ctx, number)
        Log.i("breakthru1", "outgoing started")
    }



}
