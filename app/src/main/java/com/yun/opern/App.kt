package com.yun.opern

import android.app.Application
import android.content.Context

/**
 * Created by Yun on 2018/3/2 0002.
 */
class App : Application() {
    private var context: Context? = null

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    fun getContext(): Context? {
        return context
    }
}