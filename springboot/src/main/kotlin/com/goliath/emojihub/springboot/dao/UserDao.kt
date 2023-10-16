package com.goliath.emojihub.springboot.dao

import com.goliath.emojihub.springboot.dto.user.SignUpRequest
import com.goliath.emojihub.springboot.dto.user.UserDto
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.QueryDocumentSnapshot
import com.google.firebase.cloud.FirestoreClient
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Repository

@Repository
@Slf4j
class UserDao {

    companion object {
        const val COLLECTION_NAME = "Users"
    }

    fun getUsers(): List<UserDto> {
        val list = mutableListOf<UserDto>()
        val db: Firestore = FirestoreClient.getFirestore()
        val future = db.collection(COLLECTION_NAME).get()
        val documents: List<QueryDocumentSnapshot> = future.get().documents
        for (document in documents) {
            list.add(document.toObject(UserDto::class.java))
        }
        return list
    }

    fun getUser(username: String): UserDto? {
        val db: Firestore = FirestoreClient.getFirestore()
        val future = db.collection(COLLECTION_NAME).document(username).get()
        val document: DocumentSnapshot = future.get()
        if (document.exists()) {
            return document.toObject(UserDto::class.java)
        }
        return null
    }

    fun existUser(username: String): Boolean {
        val db: Firestore = FirestoreClient.getFirestore()
        val future = db.collection(COLLECTION_NAME).document(username).get()
        val document: DocumentSnapshot = future.get()
        return document.exists()
    }

    fun insertUser(signUpRequest: SignUpRequest) {
        val db: Firestore = FirestoreClient.getFirestore()
        db.collection(COLLECTION_NAME)
            .document(signUpRequest.username)
            .set(UserDto(signUpRequest))
    }
}