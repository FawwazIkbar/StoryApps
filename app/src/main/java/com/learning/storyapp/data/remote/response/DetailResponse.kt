package com.learning.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class DetailResponse(

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("story")
    val story: ListStoryItem
)