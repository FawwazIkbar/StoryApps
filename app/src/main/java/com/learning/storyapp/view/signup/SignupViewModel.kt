package com.learning.storyapp.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.learning.storyapp.data.ResultState
import com.learning.storyapp.data.remote.response.RegisterResponse
import com.learning.storyapp.data.repository.UserRepository

class SignupViewModel (private val userRepository: UserRepository) : ViewModel() {
    fun register(name: String, email: String, password: String): LiveData<ResultState<RegisterResponse>> {
        return userRepository.register(name, email, password)
    }
}