package com.learning.storyapp.di

import android.content.Context
import com.learning.storyapp.data.database.StoryDatabase
import com.learning.storyapp.data.pref.UserPreference
import com.learning.storyapp.data.pref.dataStore
import com.learning.storyapp.data.remote.api.ApiConfig
import com.learning.storyapp.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.toString())
        val database = StoryDatabase.getDatabase(context)
        return UserRepository.getInstance(pref,apiService,database)
    }

}