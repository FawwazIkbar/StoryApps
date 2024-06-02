package com.learning.storyapp.view.upload

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.learning.storyapp.data.ResultState
import com.learning.storyapp.data.remote.response.FileUploadResponse
import com.learning.storyapp.data.repository.UserRepository
import java.io.File

class UploadViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun uploadStory(context: Context, file: File?, description: String, ): LiveData<ResultState<FileUploadResponse>> {
        return userRepository.uploadStory(context, file, description, )
    }
}
