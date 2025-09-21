package com.seekho.anime.utils


import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadUrl(url: String?) {
    if (url.isNullOrBlank()) {
        // Optionally use a placeholder drawable
        this.setImageResource(android.R.drawable.ic_menu_report_image)
    } else {
        Glide.with(this.context)
            .load(url)
            .centerCrop()
            .into(this)
    }
}
