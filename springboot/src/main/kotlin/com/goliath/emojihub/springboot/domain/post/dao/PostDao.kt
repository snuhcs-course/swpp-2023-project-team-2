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
        const val POST_COLLECTION_NAME = "Posts"
    }

    fun insertPost(username: String, content: String): PostDto {
        val dateTime = getDateTimeNow()
        val post = PostDto(username, content, dateTime)
        db.collection(POST_COLLECTION_NAME)
            .document(post.id)
            .set(post)
        return post
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

    fun getMyPosts(username: String): List<PostDto> {
        val list = mutableListOf<PostDto>()
        val postsRef = db.collection(POST_COLLECTION_NAME)
        val postQuery = postsRef.whereEqualTo("created_by", username)
            .orderBy("created_at", Query.Direction.DESCENDING)
        val documents = postQuery.get().get().documents
        for (document in documents) {
            list.add(document.toObject(PostDto::class.java))
        }
        return list
    }

    fun getPost(postId: String): PostDto? {
        val future = db.collection(POST_COLLECTION_NAME).document(postId).get()
        val document: DocumentSnapshot = future.get()
        return document.toObject(PostDto::class.java)
    }

    fun existPost(postId: String): Boolean {
        val future = db.collection(POST_COLLECTION_NAME).document(postId).get()
        val document: DocumentSnapshot = future.get()
        return document.exists()
    }

    fun updatePost(postId: String, content: String) {
        val dateTime = getDateTimeNow()
        val postRef = db.collection(POST_COLLECTION_NAME).document(postId)
        postRef.update("content", content)
        postRef.update("modified_at", dateTime)
    }

    fun deletePost(postId: String) {
        db.collection(POST_COLLECTION_NAME).document(postId).delete()
    }

}