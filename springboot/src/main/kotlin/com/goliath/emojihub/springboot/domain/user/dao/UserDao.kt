package com.goliath.emojihub.springboot.domain.user.dao

import com.goliath.emojihub.springboot.domain.user.dto.UserDto
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.FieldValue
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.QueryDocumentSnapshot
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Repository

@Repository
@Slf4j
class UserDao(
    private val db: Firestore
) {

    companion object {
        const val USER_COLLECTION_NAME = "Users"
        const val POST_COLLECTION_NAME = "Posts"
    }

    fun getUsers(): List<UserDto> {
        val list = mutableListOf<UserDto>()
        val future = db.collection(USER_COLLECTION_NAME).get()
        val documents: List<QueryDocumentSnapshot> = future.get().documents
        for (document in documents) {
            list.add(document.toObject(UserDto::class.java))
        }
        return list
    }

    fun getUser(username: String): UserDto? {
        val future = db.collection(USER_COLLECTION_NAME).document(username).get()
        val document: DocumentSnapshot = future.get()
        if (document.exists()) {
            return document.toObject(UserDto::class.java)
        }
        return null
    }

    fun existUser(username: String): Boolean {
        val future = db.collection(USER_COLLECTION_NAME).document(username).get()
        val document: DocumentSnapshot = future.get()
        return document.exists()
    }

    fun insertUser(userDto: UserDto) {
        db.collection(USER_COLLECTION_NAME)
            .document(userDto.username)
            .set(userDto)
    }

    fun deleteUser(username: String) {
        db.collection(POST_COLLECTION_NAME).whereEqualTo("created_by", username).get().get().documents
        db.collection(USER_COLLECTION_NAME).document(username).delete()
    }

    fun deleteCreatedPost(username: String, postId: String) {
        val userRef = db.collection(USER_COLLECTION_NAME).document(username)
        userRef.update("created_posts", FieldValue.arrayRemove(postId))
    }
}