package me.arthurnagy.example

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
            .apply(RequestOptions.skipMemoryCacheOf(true))
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
            .apply(RequestOptions.circleCropTransform())
            .into(this)
    }
}