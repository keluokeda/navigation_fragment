package com.ke.navigation_fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.orhanobut.logger.Logger

abstract class BaseFragment(layoutId: Int) : Fragment(layoutId) {

    protected fun logMethod(methodName: String) {
        Logger.d("$methodName $this")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logMethod("onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logMethod("onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logMethod("onViewCreated")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logMethod("onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        logMethod("onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        logMethod("onDetach")
    }
}