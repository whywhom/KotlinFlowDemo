package com.mammoth.kotlinflowdemo.utils

import com.mammoth.kotlinflowdemo.ui.main.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class Watcher {
    var listener: MainViewModel.OnChangeListener? = null

    fun start() = runBlocking {
        var i = 0
        while (true) {
            listener?.onChange(i)
            i++
            delay(1000)
        }
    }
}