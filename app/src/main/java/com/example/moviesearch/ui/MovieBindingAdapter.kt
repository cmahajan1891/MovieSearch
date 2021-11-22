package com.example.moviesearch.ui

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.moviesearch.utils.common.TransformType

@SuppressLint("CheckResult")
@BindingAdapter("imageFromUrl", "placeHolder", "transform")
fun bindImageFromUrl(
    view: ImageView,
    imageUrl: String?,
    placeHolder: Drawable?,
    transform: TransformType?
) {
    val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)

    val transformationList = mutableListOf<BitmapTransformation>()
    if (transform != null) transformationList.add(transform.value)

    if (transformationList.size > 0) requestOptions.transform(MultiTransformation(transformationList))

    if (placeHolder != null) requestOptions.placeholder(placeHolder)
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .apply(requestOptions)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}
