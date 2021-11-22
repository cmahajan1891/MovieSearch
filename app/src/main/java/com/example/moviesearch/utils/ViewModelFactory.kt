package com.example.moviesearch.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import kotlin.reflect.KClass

fun <T : ViewModel> getViewModel(
    viewModelStoreOwner: ViewModelStoreOwner,
    kClazz: KClass<T>,
    creator: () -> T
) = ViewModelProvider(
    viewModelStoreOwner,
    object : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(kClazz.java)) return creator() as T
            throw IllegalArgumentException("unsupported class name")
        }
    }
)[kClazz.java]
