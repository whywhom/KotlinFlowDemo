package com.mammoth.kotlinflowdemo.domain

data class ResponseData(
    val `data`: List<Data>,
    val page: Int,
    val per_page: Int,
    val support: Support,
    val total: Int,
    val total_pages: Int
)

data class Data(
    val avatar: String,
    val email: String,
    val first_name: String,
    val id: Int,
    val last_name: String

) {
    override fun toString(): String {
        return "avatar = $avatar, first_name = $first_name , last_name = $last_name , email = $email"
    }
}

data class Support(
    val text: String,
    val url: String
)