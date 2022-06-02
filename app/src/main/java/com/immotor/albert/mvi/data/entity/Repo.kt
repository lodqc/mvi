package com.immotor.albert.mvi.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Repo(
    val id: Int?,
    val name: String?,
    val description: String?,
    val starCount: Int?
)
@JsonClass(generateAdapter = true)
data class  RepoResponse(
    val items: List<Repo>? = emptyList()
)
