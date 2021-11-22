package com.example.moviesearch.utils.common

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.FitCenter

enum class TransformType(val value: BitmapTransformation) {
    CENTER_CROP(CenterCrop()),
    CENTER_INSIDE(CenterInside()),
    FIT_CENTER(FitCenter())
}
