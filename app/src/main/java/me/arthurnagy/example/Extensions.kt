package me.arthurnagy.example

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.*

fun <B : ViewDataBinding> AppCompatActivity.setBindingContentView(@LayoutRes layout: Int): B = DataBindingUtil.setContentView(this, layout)

inline fun <reified VM : ViewModel> AppCompatActivity.provideViewModel(): VM = ViewModelProviders.of(this).get()

inline fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner, crossinline observer: (T?) -> Unit) =
    this.observe(lifecycleOwner, androidx.lifecycle.Observer { observer(it) })