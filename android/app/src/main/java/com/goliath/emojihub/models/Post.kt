package com.goliath.emojihub.models

import com.google.gson.annotations.SerializedName

class Post(
    dto: PostDto
) {
    val id: String = dto.id
    val createdAt: String = dto.createdAt
    val modifiedAt: String = dto.modifiedAt
    val createdBy: String = dto.createdBy
    val content: String = dto.content
    val reaction: List<String> = dto.reaction
}

data class PostDto(
    val id: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("modified_at") val modifiedAt: String,
    @SerializedName("created_by") val createdBy: String,
    val content: String,
    @SerializedName("reactions") val reaction: List<String>
)

data class UploadPostDto(
    @SerializedName("content") val content: String
)

val dummyPost = Post(
    PostDto(
        id = "1234",
        createdAt = "2023.09.16",
        createdBy = "channn",
        content = "조금 전에 앞에 계신 분이 실수로 지갑을 흘리셨다. " +
                "지갑이 하수구 구멍으로 빠지려는 찰나, 발로 굴러가는 지갑을 막아서 다행히 참사는 막을 수 있었다. " +
                "지갑 주인분께서 감사하다고 카페 드림에서 커피도 한 잔 사주셨다.",
        modifiedAt = "2023.10.23",
        reaction = listOf("good", "check", "good")
    )
)