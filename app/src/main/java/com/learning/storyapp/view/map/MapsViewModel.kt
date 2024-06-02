package com.learning.storyapp.view.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.learning.storyapp.data.pref.UserModel
import com.learning.storyapp.data.repository.UserRepository

class MapsViewModel(private val repository: UserRepository) : ViewModel() {

    fun getStoriesWithLocation(location: Int = 1) = repository.getStoriesWithLocation(location)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}