package com.ifs21047.delcomtodo.helper

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.ifs21047.delcomtodo.data.remot.MyResult

class Utils {
    companion object{
        fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
            val observerWrapper = object : Observer<T> {
                override fun onChanged(value: T) {
                    observer(value)
                    if (value is MyResult.Success<*> || value is MyResult.Error) {
                        removeObserver(this)
                    }
                }
            }
            observeForever(observerWrapper)
        }
    }
}
