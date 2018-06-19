package me.arthurnagy.example

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

@BindingAdapter("goneIf")
fun View.goneIf(isViewGone: Boolean) {
    this.visibility = if (isViewGone) View.GONE else View.VISIBLE
}

@BindingAdapter("profileImageUrl")
fun ImageView.profileImageFromUrl(imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(this.context)
            .load(imageUrl)
            .apply(RequestOptions.circleCropTransform())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }
}