package com.goliath.emojihub.springboot.domain.post.dao

import com.goliath.emojihub.springboot.domain.post.dto.PostDto
import com.goliath.emojihub.springboot.global.util.getDateTimeNow
import com.google.cloud.firestore.*
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Repository

@Repository
@Slf4j
class PostDao(
    private val db: Firestore
) {

    companion object {
        const val USER_COLLECTION_NAME = "Users"
        const val POST_COLLECTION_NAME = "Posts"
    }

    fun postPost(username: String, content: String) {
        val dateTime = getDateTimeNow()
        val post = PostDto(username, content, dateTime)
        db.collection(POST_COLLECTION_NAME)
            .document(post.id)
            .set(post)
        db.collection(USER_COLLECTION_NAME)
            .document(username)
            .update("created_posts", FieldValue.arrayUnion(post.id))
    }

    fun getPosts(index: Int, count: Int): List<PostDto> {
        val list = mutableListOf<PostDto>()
        // 정렬
        val postQuery = db.collection(POST_COLLECTION_NAME).orderBy("created_at", Query.Direction.DESCENDING)
        // 페이지네이션
        val documents: List<QueryDocumentSnapshot> = postQuery.offset((index - 1) * count).limit(count).get().get().documents
        for (document in documents) {
            list.add(document.toObject(PostDto::class.java))
        }
        return list
    }

    fun getPost(id: String): PostDto? {
        val future = db.collection(POST_COLLECTION_NAME).document(id).get()
        val document: DocumentSnapshot = future.get()
        return document.toObject(PostDto::class.java)
    }

    fun existPost(id: String): Boolean {
        val future = db.collection(POST_COLLECTION_NAME).document(id).get()
        val document: DocumentSnapshot = future.get()
        return document.exists()
    }

    fun updatePost(id: String, content: String) {
        val dateTime = getDateTimeNow()
        val post = db.collection(POST_COLLECTION_NAME).document(id)
        post.update("content", content)
        post.update("modified_at", dateTime)
    }

    fun deletePost(id: String) {
        db.collection(POST_COLLECTION_NAME).document(id).delete()
    }
}