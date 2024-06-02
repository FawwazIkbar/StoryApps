package com.learning.storyapp.view.upload

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.learning.storyapp.R
import com.learning.storyapp.data.ResultState
import com.learning.storyapp.databinding.ActivityUploadBinding
import com.learning.storyapp.utils.getImageUri
import com.learning.storyapp.utils.uriToFile
import com.learning.storyapp.view.ViewModelFactory
import com.learning.storyapp.view.main.MainActivity
import java.io.File

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private var currentImageUri: Uri? = null
    private var file: File? = null
    private val viewModel by viewModels<UploadViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val alertBuilder = AlertDialog.Builder(this)

        binding.cameraButton.setOnClickListener {
            startCamera()
        }

        binding.galleryButton.setOnClickListener {
            startGallery()
        }
        binding.uploadButton.setOnClickListener {
            val description = binding.addDeskripsi.text.toString()
            viewModel.uploadStory(this, file, description)
                .observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is ResultState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.uploadButton.isClickable = false
                            }

                            is ResultState.Success -> {
                                binding.progressBar.visibility = View.GONE
                                binding.uploadButton.isClickable = true
                                alertBuilder.setTitle(getString(R.string.upload_success))

                                alertBuilder.setPositiveButton("OKE") { _, _ ->
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finishAffinity()
                                }.create().show()
                                alertBuilder.setOnCancelListener {
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finishAffinity()
                                }.show()
                            }

                            is ResultState.Error -> {
                                binding.progressBar.visibility = View.GONE
                                binding.uploadButton.isClickable = true
                                alertBuilder.setTitle(getString(R.string.error))
                                alertBuilder.setMessage(result.error)
                                alertBuilder.setPositiveButton("OKE") { _, _ -> }.create().show()
                            }
                        }
                    }
                }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            currentImageUri.let {
                if (it != null) {
                    file = uriToFile(it, this)
            }
        }
        showImage()
    } else {
        Log.d("Photo Picker", "No media selected")
    }
}

    private fun startCamera() {
    currentImageUri = getImageUri(this)
    launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
    ActivityResultContracts.TakePicture()
    ) { isSuccess ->
         if (isSuccess) {
            currentImageUri.let {
                if (it != null) {
                    file = uriToFile(it, this)
                }
            }
        showImage()
    }
}

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }
}
