package com.learning.storyapp

import com.learning.storyapp.data.remote.response.ListStoryItem

object DataDummy {

    fun generateDummyListStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                i.toString(),
                "author + $i",
                "story $i",
                "description $i",
                "id$i",
            )
            items.add(quote)
        }
        return items
    }
}