package com.goliath.emojihub.springboot.domain.user.dao

import com.goliath.emojihub.springboot.domain.user.dto.UserDto
import com.goliath.emojihub.springboot.global.util.StringValue.Collection.USER_COLLECTION_NAME
import com.goliath.emojihub.springboot.global.util.StringValue.UserField.SAVED_EMOJIS
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

    fun getUsers(): List<UserDto> {
        val list = mutableListOf<UserDto>()
        val future = db.collection(USER_COLLECTION_NAME.string).get()
        val documents: List<QueryDocumentSnapshot> = future.get().documents
        for (document in documents) {
            list.add(document.toObject(UserDto::class.java))
        }
        return list
    }

    fun getUser(username: String): UserDto? {
        val future = db.collection(USER_COLLECTION_NAME.string).document(username).get()
        val document: DocumentSnapshot = future.get()
        var result: UserDto? = null
        if (document.exists()) {
            result = document.toObject(UserDto::class.java)
        }
        return result
    }

    fun existUser(username: String): Boolean {
        val future = db.collection(USER_COLLECTION_NAME.string).document(username).get()
        val document: DocumentSnapshot = future.get()
        return document.exists()
    }

    fun insertUser(userDto: UserDto) {
        db.collection(USER_COLLECTION_NAME.string)
            .document(userDto.username)
            .set(userDto)
    }

    fun deleteUser(username: String) {
        db.collection(USER_COLLECTION_NAME.string).document(username).delete()
    }

    fun insertId(username: String, id: String, field: String) {
        val userRef = db.collection(USER_COLLECTION_NAME.string).document(username)
        userRef.update(field, FieldValue.arrayUnion(id))
    }

    fun deleteId(username: String, id: String, field: String) {
        val userRef = db.collection(USER_COLLECTION_NAME.string).document(username)
        userRef.update(field, FieldValue.arrayRemove(id))
    }

    fun deleteAllSavedEmojiId(emojiId: String) {
        val usersWithDeletedEmoji = db.collection(USER_COLLECTION_NAME.string)
            .whereArrayContains(SAVED_EMOJIS.string, emojiId)
            .get().get().documents
        for (user in usersWithDeletedEmoji) {
            user.reference.update(SAVED_EMOJIS.string, FieldValue.arrayRemove(emojiId))
        }
    }
}