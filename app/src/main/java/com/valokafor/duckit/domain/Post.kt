package com.valokafor.duckit.domain

data class Post(
    val id: String,
    val headline: String,
    val image: String,
    var upvotes: Int = 0,
    var isUpvoted: Boolean = false,
    var isDownvoted: Boolean = false
)