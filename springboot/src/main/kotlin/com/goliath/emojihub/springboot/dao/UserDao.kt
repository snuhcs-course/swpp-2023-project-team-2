package com.goliath.emojihub.springboot.dao

import com.goliath.emojihub.springboot.model.User
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

    fun getUsers(): List<User> {
        val list = mutableListOf<User>()
        val db: Firestore = FirestoreClient.getFirestore()
        val future = db.collection(COLLECTION_NAME).get()
        val documents: List<QueryDocumentSnapshot> = future.get().documents
        for (document in documents) {
            list.add(document.toObject(User::class.java))
        }
        return list
    }
}