package com.goliath.emojihub.springboot.domain.post.dao

import com.goliath.emojihub.springboot.domain.post.dto.PostDto
import com.goliath.emojihub.springboot.domain.post.dto.PostRequest
import com.goliath.emojihub.springboot.global.util.getDateTimeNow
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.QueryDocumentSnapshot
import com.google.firebase.cloud.FirestoreClient
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Repository

@Repository
@Slf4j
class PostDao {

    companion object {
        const val POST_COLLECTION_NAME = "Posts"
    }

    fun postPost(username: String, postRequest: PostRequest) {
        val dateTime = getDateTimeNow()
        val post = PostDto(username, postRequest, dateTime)
        val db: Firestore = FirestoreClient.getFirestore()
        db.collection(POST_COLLECTION_NAME)
            .document(post.id)
            .set(post)
    }

    fun getPosts(numLimit: Int): List<PostDto> {
        val list = mutableListOf<PostDto>()
        val db: Firestore = FirestoreClient.getFirestore()
        val postColl = db.collection(POST_COLLECTION_NAME).get()
        val documents: List<QueryDocumentSnapshot> = postColl.get().documents
        for (document in documents) {
            list.add(document.toObject(PostDto::class.java))
        }
        return list
    }

    fun getPost(id: String): PostDto? {
        val db: Firestore = FirestoreClient.getFirestore()
        val future = db.collection(POST_COLLECTION_NAME).document(id).get()
        val document: DocumentSnapshot = future.get()
        return document.toObject(PostDto::class.java)
    }

    fun existPost(id: String): Boolean {
        val db: Firestore = FirestoreClient.getFirestore()
        val future = db.collection(POST_COLLECTION_NAME).document(id).get()
        val document: DocumentSnapshot = future.get()
        return document.exists()
    }

    fun updatePost(id: String, postRequest: PostRequest) {
        val dateTime = getDateTimeNow()
        val db: Firestore = FirestoreClient.getFirestore()
        val post = db.collection(POST_COLLECTION_NAME).document(id)
        post.update("content", postRequest.content)
        post.update("modified_at", dateTime)
    }

    fun deletePost(id: String) {
        val db: Firestore = FirestoreClient.getFirestore()
        db.collection(POST_COLLECTION_NAME).document(id).delete()
    }
}