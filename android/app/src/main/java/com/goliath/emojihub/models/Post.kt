package com.goliath.emojihub.models

import com.google.gson.annotations.SerializedName

class Post(
    dto: PostDto
) {
    val createdAt: String = dto.createdAt
    val createdBy: String = dto.createdBy
    val content: String = dto.content
    val reaction: List<String> = dto.reaction
}

// TODO: replace with **Real PostDto**
data class PostDto(
    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("created_by")
    val createdBy: String,

    @SerializedName("body")
    val content: String,

    @SerializedName("reaction")
    val reaction: List<String>
)

val dummyPost = Post(
    PostDto(
    createdAt = "2023.09.16",
    createdBy = "channn",
    content = "조금 전에 앞에 계신 분이 실수로 지갑을 흘리셨다. " +
            "지갑이 하수구 구멍으로 빠지려는 찰나, 발로 굴러가는 지갑을 막아서 다행히 참사는 막을 수 있었다. " +
            "지갑 주인분께서 감사하다고 카페 드림에서 커피도 한 잔 사주셨다.",
    reaction = listOf("good", "check", "good")
)
)