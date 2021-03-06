package com.mammoth.kotlinflowdemo.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mammoth.kotlinflowdemo.domain.ResponseData
import com.mammoth.kotlinflowdemo.domain.SampleUseCase
import com.mammoth.kotlinflowdemo.ui.State
import com.mammoth.kotlinflowdemo.utils.Utils
import com.mammoth.kotlinflowdemo.utils.Watcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val sampleUseCase: SampleUseCase) : ViewModel() {

    /**
     * stateIn is a Flow operator that converts a Flow to StateFlow.
     * To convert any flow to a StateFlow, use the stateIn intermediate operator.
     */
    var getSampleResult: StateFlow<State<ResponseData>> = flow {
        emit(State.LoadingState)
        try {
            delay(1000)
            val strObject = sampleUseCase().toString()
            val responseDataObject = Gson().fromJson(strObject, ResponseData::class.java)
            emit(State.DataState(responseDataObject))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Utils.resolveError(e))
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5000),
        initialValue = State.LoadingState
    )

    private fun simple(): Flow<Int> = flow {
        // flow builder
        for (i in 1..3) {
            delay(100) // pretend we are doing something useful, some long-running operation here
            Log.d(MainViewModel::class.java.name,"emit $i - after delay 100 ms")
            emit(i) // emit next value
        }
    }

    private suspend fun simple1() = flowOf("A", "B", "C")
        .onEach  { Log.d(MainViewModel::class.java.name,"simple1 1$it ") }
        .collect {
            delay(100)
            Log.d(MainViewModel::class.java.name,"simple1 collect 2$it - after delay 100 ms")
        }

    private suspend fun simple2() = flowOf("A", "B", "C")
    .onEach  { Log.d(MainViewModel::class.java.name,"simple2 1$it ") }
    .buffer()
    .collect {
        delay(100)
        Log.d(MainViewModel::class.java.name,"simple2 collect 2$it - after delay 100 ms")
    }

    fun flowDemo1() = runBlocking {
        // This Flow does not execute the terminal operation, and intermediate operation in the Flow will not be executed
        simple().map { value ->
            val squareValue = value*value
            Log.d(MainViewModel::class.java.name,"simple map $squareValue - after delay 100 ms")
        }
        Log.d(MainViewModel::class.java.name,"simple map is finished")

        // Collect the flow
        // The code inside a flow builder does not run until the flow is collected.
        simple().map { value ->
            value*value
        }.collect { value ->
            delay(100)
            Log.d(MainViewModel::class.java.name,"collect $value - after delay 100 ms")
        }
    }

    fun flowDemo2() = runBlocking {
        simple1()
        Log.d(MainViewModel::class.java.name,"simple1 ====> finished")
        simple2()
        Log.d(MainViewModel::class.java.name,"simple2 ====> finished")
    }

    fun getSampleResponse() = flow {
        emit(State.LoadingState)
        try {
            delay(1000)
            emit(State.DataState(sampleUseCase()))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Utils.resolveError(e))
        }
    }

    fun getSampleResponse2(){
        getSampleResult
    }

    @ExperimentalCoroutinesApi
    fun flowFrom(watcher: Watcher): Flow<Int> = callbackFlow {
        val callback = object : OnChangeListener {
            override fun onChange(i: Int) {
                offer(i)
            }
        }
        watcher.listener = callback

        awaitClose {
            watcher.listener = null
        }
    }

    interface OnChangeListener {
        fun onChange(i: Int)
    }
}