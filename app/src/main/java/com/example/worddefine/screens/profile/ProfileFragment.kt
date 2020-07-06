package com.example.worddefine.screens.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.Headers
import com.bumptech.glide.load.model.LazyHeaders.Builder
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.example.worddefine.BuildConfig
import com.example.worddefine.R
import com.example.worddefine.Token
import com.example.worddefine.databinding.FragmentProfileBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*


const val PICK_IMAGE: Int = 1

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    private lateinit var binding: FragmentProfileBinding

    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =  DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_profile,
            container,
            false
        )

        binding.lifecycleOwner = this

        userId = Token.getUserId(context!!)!!

        val application = requireNotNull(activity).application

        val viewModelFactory = ProfileViewModelFactory(application)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)


        viewModel.refreshProfilePhoto.observe(this, Observer {
            it?.let {
                if (it){

                    val url = BuildConfig.BASE_URL + "/api/users/$userId/profilePhoto"

                    val headers: Headers =
                        Builder()
                            .addHeader("x-auth-token", Token.get(context!!)!!).build()

                    val uri = GlideUrl(url, headers)

                    Glide.with(context!!)
                        .load(uri)
                        .apply(
                            RequestOptions()
                            .placeholder(R.drawable.loading_animation)
                            .error(R.drawable.ic_broken_image)
                        )
                        .signature(ObjectKey(System.currentTimeMillis().toString()))
                        .into(binding.profileImageView)

                    val imageView: ImageView = ImageView(context)

                    val uriThumbnail = url + "/thumb"
                    Glide.with(context!!)
                        .load(uriThumbnail)
                        .apply(
                            RequestOptions()
                                .placeholder(R.drawable.loading_animation)
                                .error(R.drawable.ic_broken_image)
                        )
                        .signature(ObjectKey(System.currentTimeMillis().toString()))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(imageView)

                    viewModel.refreshProfilePhotoDone()
                }
            }
        })


        binding.selectPhotoImageView.setOnClickListener {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            pickIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            startActivityForResult(chooserIntent, PICK_IMAGE)
        }


        binding.viewModel = viewModel

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK){
            try {
                val requestFile: RequestBody = RequestBody.create(
                    MediaType.parse("image/jpeg"),
                    getFileBytes(data!!.data!!)!!
                )

                viewModel.uploadPicture(requestFile)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    private fun getFileBytes(uri: Uri): ByteArray? {
        val bos = ByteArrayOutputStream(200000)
        val imageStream: InputStream
        try {
            imageStream = context!!.contentResolver.openInputStream(uri)!!
            val buffer: ByteArray
            buffer = ByteArray(100000)
            while (imageStream.read(buffer) !== -1) {
                bos.write(buffer)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return bos.toByteArray()
    }
}
