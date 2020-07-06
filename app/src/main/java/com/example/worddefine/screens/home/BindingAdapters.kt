package com.example.worddefine.screens.home

import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.Headers
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.example.worddefine.BuildConfig
import com.example.worddefine.R
import com.example.worddefine.Token
import com.example.worddefine.database.model.WordList
import com.example.worddefine.network.WordListsVisibleFilter
import saman.zamani.persiandate.PersianDate
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("wordListsData")
fun bindRecyclerView(recyclerView: RecyclerView, data: PagedList<WordList>?) {
    val adapter = recyclerView.adapter as WordListAdapter
    adapter.submitList(data)
//    adapter.notifyDataSetChanged()
}

@BindingAdapter("imageVisible")
fun bindImage(imgView: ImageView, visible: String){
    when(visible){
        WordListsVisibleFilter.SHOW_EVERYONE.value ->
            imgView.setImageResource(R.drawable.ic_open_in_browser_green_24dp)
        WordListsVisibleFilter.SHOW_ONLY_ME.value ->
            imgView.setImageResource(R.drawable.ic_lock_red_24dp)
        WordListsVisibleFilter.SHOW_USER_WITH_PASSWORD.value ->
            imgView.setImageResource(R.drawable.ic_lock_open_blue_24d)
    }
}

@BindingAdapter("wordListTitle")
fun bindTitle(textView: TextView, wordListTitle: String){
    if(wordListTitle.length > 15)
        textView.text = wordListTitle.substring(0, 15).plus(" ..." )
    else
        textView.text = wordListTitle
}

@BindingAdapter("wordListOwner")
fun bindOwner(textView: TextView, username: String){
    textView.text = username
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

@BindingAdapter("wordListDate")
fun bindWordListDate(textView: TextView, dateTime: Long){
    val language = Locale.getDefault().language
    if (language == "fa")
        textView.text = convertLongToFaTime(dateTime)
    else
        textView.text = convertLongToEnTime(dateTime)
}

//@BindingAdapter("favorite")
fun bindFavorite(favoriteImage: ImageView, favoriteWordList: String?){
    if(favoriteWordList != null ){
        favoriteImage.setImageResource(R.drawable.ic_favorite_check_red_24dp)
    }
    else {
        favoriteImage.setImageResource(R.drawable.ic_favorite_uncheck_black_24dp)
    }
}

@BindingAdapter("favorite")
fun bindFavoriteCheckBox(checkBox: CheckBox, favoriteWordList: String?){
/*    checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
        buttonView.checke
    }*/
    checkBox.isChecked = favoriteWordList != null
}

@BindingAdapter("imgUrl")
fun bindImageUrl(imageView: ImageView, userId: String){
    val url = BuildConfig.BASE_URL + "/api/users/$userId/profilePhoto/thumb"

    val headers: Headers =
        LazyHeaders.Builder().addHeader("x-auth-token", Token.get(imageView.context)!!).build()

    val uri = GlideUrl(url, headers)

    Glide.with(imageView.context)
        .load(uri)
        .apply(RequestOptions()
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_broken_image)
            .circleCrop())
        .into(imageView)
}

@BindingAdapter("imgDescription")
fun bindImageDescription(imageView: ImageView, text: String){
    imageView.contentDescription = text
}