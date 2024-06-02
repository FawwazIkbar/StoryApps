package com.learning.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.learning.storyapp.data.ResultState
import com.learning.storyapp.data.pref.UserModel
import com.learning.storyapp.data.remote.response.ListStoryItem
import com.learning.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getStory(): LiveData<ResultState<PagingData<ListStoryItem>>> {
        return repository.getStory(viewModelScope)
    }
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }

    }
}