package com.mammoth.kotlinflowdemo.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mammoth.kotlinflowdemo.R
import com.mammoth.kotlinflowdemo.ui.State
import com.mammoth.kotlinflowdemo.utils.Watcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private var uiStateJob: Job? = null
    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onStop() {
        uiStateJob?.cancel()
        super.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val textView = requireView().findViewById<TextView>(R.id.message)
        // Demo 1
//        demo1()

        // Demo2
//        demo2()

        // Demo3
//        demo3(textView)

        // Demo4
//        demo4()

        // Demo5
        demo5()
    }

    private fun demo1() {
        viewModel.flowDemo1()
    }

    private fun demo2() {
        viewModel.flowDemo2()
    }

    private fun demo3(textView: TextView) {
        uiStateJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(500)
            viewModel.getSampleResponse()
                .collect {
                    when (it) {
                        is State.DataState -> textView.text = "success ${it.data}"
                        is State.ErrorState -> textView.text = "error ${it.exception}"
                        is State.LoadingState -> textView.text = "loading"
                    }

                }
        }
    }

    private fun demo4() {
        // Start a coroutine in the lifecycle scope
        // Warning: Never collect a flow from the UI directly from launch or the launchIn extension function
        // if the UI needs to be updated. These functions process events even when the view is not visible.
        // This behavior can lead to app crashes. To avoid that, use the repeatOnLifecycle API as shown above.
        viewLifecycleOwner.lifecycleScope.launch {
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getSampleResult.collect {
                    when (it) {
                        is State.DataState -> {
                            Log.d(MainFragment::class.java.name, "success")
                            for (data in it.data.data) {
                                Log.d(MainFragment::class.java.name, data.toString())
                            }
                        }
                        is State.ErrorState -> Log.d(MainFragment::class.java.name, "error ${it.exception}")
                        is State.LoadingState -> Log.d(MainFragment::class.java.name, "loading")
                    }
                }
            }
        }

        viewModel.getSampleResponse2()
    }

    private fun demo5() {
        val watch = Watcher()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.flowFrom(watch).collect {
                Log.d(MainFragment::class.java.name,"onchange: $it")
            }
        }

        watch.start()
    }
}
