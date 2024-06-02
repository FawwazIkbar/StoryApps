package com.learning.storyapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.learning.storyapp.data.ResultState
import com.learning.storyapp.data.remote.response.LoginResponse
import com.learning.storyapp.data.repository.UserRepository


class LoginViewModel(private val UserRepository: UserRepository) : ViewModel() {
    fun saveSession(email: String, password: String): LiveData<ResultState<LoginResponse>> {
        return UserRepository.login(email, password)
        }
    }

