package com.udacity.asteroidradar.main

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.domain.Asteroid

@BindingAdapter("asteroidStatusImage")
fun ImageView.setAsteroidStatusImage(item: Asteroid?) {
    item?.let {
        setImageResource(when (item.isPotentiallyHazardous) {
            true -> R.drawable.ic_status_potentially_hazardous
            else -> R.drawable.ic_status_normal
        })
    }
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Picasso.with(imgView.context)
            .load(imgUri)
            .into(imgView)
    }
}