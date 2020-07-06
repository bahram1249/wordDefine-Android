package com.example.worddefine.screens.word

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.Headers
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.example.worddefine.BuildConfig
import com.example.worddefine.R
import com.example.worddefine.Token
import saman.zamani.persiandate.PersianDate
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("wordViewName")
fun bindWordName(textView: TextView, text: String?) {
    if (text != null)
        textView.text = text
}

@BindingAdapter("wordViewDefinition")
fun bindWordDefinition(textView: TextView, text: String?) {
    if (text != null)
        textView.text = text
}

@BindingAdapter("wordViewExamples")
fun bindWordExamples(textView: TextView, text: String?) {
    if (text != null)
        textView.text = text
}

@BindingAdapter("wordViewLanguage")
fun bindWordLanguage(textView: TextView, text: String?) {
    if (text != null)
        textView.text = text
}


fun convertLongToEnTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy.MM.dd")
    return format.format(date)
}

fun convertLongToFaTime(time: Long): String {
    val date = PersianDate(time)
    return "${date.shYear}-${date.shMonth}-${date.shDay}"
}

@BindingAdapter("wordViewDateCreate")
fun bindWordDateCreate(textView: TextView, dateTime: Long?) {
    if (dateTime != null){
        val language = Locale.getDefault().language
        if (language == "fa")
            textView.text = convertLongToFaTime(dateTime)
        else
            textView.text = convertLongToEnTime(dateTime)
    }

}

@BindingAdapter("wordViewTitle")
fun bindWordTitle(textView: TextView, wordTitle: String?) {
    wordTitle?.let {
        textView.text = wordTitle
    }

}

@BindingAdapter("imgWordUserUrl")
fun bindImageUrl(imageView: ImageView, userId: String?){
    userId?.let {
        val url = BuildConfig.BASE_URL + "/api/users/$userId/profilePhoto/thumb"

        val headers: Headers =
            LazyHeaders.Builder().addHeader("x-auth-token", Token.get(imageView.context)!!).build()

        val uri = GlideUrl(url, headers)

        Glide.with(imageView.context)
            .load(uri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
                    .circleCrop())
            .into(imageView)
    }

}