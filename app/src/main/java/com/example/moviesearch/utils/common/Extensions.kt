package com.example.moviesearch.utils.common

import android.view.View

fun Boolean.toVisibleOrGone(): Int = if (this) View.VISIBLE else View.GONE
