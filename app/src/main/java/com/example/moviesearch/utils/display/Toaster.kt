package com.example.moviesearch.utils.display

import android.content.Context
import android.widget.Toast

object Toaster {
    fun show(context: Context, text: CharSequence) {
        val toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        toast.show()
    }
}
