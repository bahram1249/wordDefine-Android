package com.example.worddefine.screens.profile

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.Headers
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.example.worddefine.BuildConfig
import com.example.worddefine.R
import com.example.worddefine.Token

@BindingAdapter("profileImage")
fun bindProfileImage(imageView: ImageView, userId: String?) {
    userId?.let {
        val url = BuildConfig.BASE_URL + "/api/users/$userId/profilePhoto"

        val headers: Headers =
            LazyHeaders.Builder().addHeader("x-auth-token", Token.get(imageView.context)!!).build()

        val uri = GlideUrl(url, headers)

        Glide.with(imageView.context)
            .load(uri)
            .apply(
                RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
            )
            .signature(ObjectKey(System.currentTimeMillis().toString()))
            .into(imageView)
    }
}

@BindingAdapter("username")
fun bindUsername(textView: TextView, text: String?){
    text?.let {
        textView.text = it
    }
}

@BindingAdapter("email")
fun bindEmail(textView: TextView, text: String?){
    text?.let {
        textView.text = it
    }
}