package com.learning.storyapp.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.learning.storyapp.R
import com.learning.storyapp.data.ResultState
import com.learning.storyapp.data.database.StoryDatabase
import com.learning.storyapp.data.database.StoryRemoteMediator
import com.learning.storyapp.data.pref.UserModel
import com.learning.storyapp.data.pref.UserPreference
import com.learning.storyapp.data.remote.api.ApiConfig
import com.learning.storyapp.data.remote.api.ApiService
import com.learning.storyapp.data.remote.response.FileUploadResponse
import com.learning.storyapp.data.remote.response.ListStoryItem
import com.learning.storyapp.data.remote.response.LoginResponse
import com.learning.storyapp.data.remote.response.RegisterResponse
import com.learning.storyapp.data.remote.response.StoryResponse
import com.learning.storyapp.utils.reduceFileImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File


class UserRepository private constructor(
    private val userPreference: UserPreference,
    private var apiService:ApiService,
    private val storyDatabase: StoryDatabase

    ) {
    fun register(name: String, email: String, password: String): LiveData<ResultState<RegisterResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(ResultState.Success(response))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }
    fun login(email: String, password: String): LiveData<ResultState<LoginResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.login(email, password)
            userPreference.saveSession(UserModel(email, response.loginResult.token, true))
            emit(ResultState.Success(response))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }
    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStory(coroutineScope: CoroutineScope): LiveData<ResultState<PagingData<ListStoryItem>>> =
        liveData {
            emit(ResultState.Loading)
        try {
                val token = runBlocking {
                    userPreference.getToken().first()
                }
            apiService = ApiConfig.getApiService(token)
            val response = Pager(
                config = PagingConfig(pageSize = 5),
                remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
                pagingSourceFactory = { storyDatabase.storyDao().getAllStory() }
            )
            val couroutineFlow = response.flow.cachedIn(coroutineScope)
            couroutineFlow.collect { pagingData ->
                emit(ResultState.Success(pagingData))
            }
        } catch (e: HttpException) {
            val response = e.response()?.errorBody()?.string()
            val error = Gson().fromJson(response, StoryResponse::class.java)
            emit(ResultState.Error(error.message))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
        }

    fun getDetailStory(id: String): LiveData<ResultState<ListStoryItem>> = liveData {
        emit(ResultState.Loading)
        try {

            val response = apiService.detailStory(id)
            val result = response.story
            emit(ResultState.Success(result))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun uploadStory(context: Context, file: File?, description: String,

    ): LiveData<ResultState<FileUploadResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val token = runBlocking {
                userPreference.getToken().first()
            }

            apiService = ApiConfig.getApiService(token)
            if (file != null) {
                val files = file.reduceFileImage()
                val desc = description.toRequestBody("text/plain".toMediaType())
                val imageFile = files.asRequestBody("image/jpeg".toMediaType())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    files.name,
                    imageFile
                )
                val response = apiService.uploadStory(imageMultipart, desc)
                emit(ResultState.Success(response))
            } else {
                emit(ResultState.Error(context.getString(R.string.empty_image)))
            }

        } catch (e: HttpException) {
            val response = e.response()?.errorBody()?.string()
            val error = Gson().fromJson(response, FileUploadResponse::class.java)
            emit(ResultState.Error(error.message))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }
    fun getStoriesWithLocation(location: Int = 1): LiveData<ResultState<StoryResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val token = userPreference.getSession().first().token

            val api = ApiConfig.getApiService(token)

            val storiesLocResponse = api.getStoriesWithLocation(location)
            if (storiesLocResponse.error == false) {
                emit(ResultState.Success(storiesLocResponse))
            } else {
                emit(ResultState.Error(storiesLocResponse.message ?: "Location Not Found"))
            }
        } catch (e: HttpException) {
            Log.d("login", e.message.toString())
            emit(ResultState.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
            database: StoryDatabase

        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService,database)
            }.also { instance = it }


    }
}