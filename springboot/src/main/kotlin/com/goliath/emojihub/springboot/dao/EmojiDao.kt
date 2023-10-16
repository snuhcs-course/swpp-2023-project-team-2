package com.goliath.emojihub.springboot.dao

import com.goliath.emojihub.springboot.dto.emoji.EmojiDto
import com.goliath.emojihub.springboot.dto.emoji.PostEmojiRequest
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.FieldValue
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.QueryDocumentSnapshot
import com.google.firebase.cloud.FirestoreClient
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Repository


@Repository
@Slf4j
class EmojiDao {

    companion object {
        const val USER_COLLECTION_NAME = "Users"
        const val EMOJI_COLLECTION_NAME = "Emojis"
    }

    // TODO: numLimit 아직 반영 X
    fun getEmojis(numLimit: Int): List<EmojiDto> {
        val list = mutableListOf<EmojiDto>()
        val db: Firestore = FirestoreClient.getFirestore()
        val emojiColl = db.collection(EMOJI_COLLECTION_NAME).get()
        val documents: List<QueryDocumentSnapshot> = emojiColl.get().documents
        for (document in documents) {
            list.add(document.toObject(EmojiDto::class.java))
        }
        return list
    }

    fun getEmoji(emojiId: String): EmojiDto? {
        val db: Firestore = FirestoreClient.getFirestore()
        val future = db.collection(EMOJI_COLLECTION_NAME).document(emojiId).get()
        val document: DocumentSnapshot = future.get()
        if (document.exists()){
            return document.toObject(EmojiDto::class.java)
        }
        return null
    }

    // TODO: 이 부분은 좀 더 고민해봐야 할 것 같다.
    fun postEmoji(emoji: EmojiDto) {
        val db: Firestore = FirestoreClient.getFirestore()
        val future = db.collection(EMOJI_COLLECTION_NAME).document(emoji.id).set(emoji)
    }

    fun saveEmoji(userId: String, emojiId: String) {
        val db: Firestore = FirestoreClient.getFirestore()
        val userRef = db.collection(USER_COLLECTION_NAME).document(userId)
        userRef.update("saved_emojis", FieldValue.arrayUnion(emojiId))
    }

    fun unSaveEmoji(userId: String, emojiId: String) {
        val db: Firestore = FirestoreClient.getFirestore()
        val userRef = db.collection(USER_COLLECTION_NAME).document(userId)
        userRef.update("saved_emojis", FieldValue.arrayRemove(emojiId))
    }

    fun deleteEmoji(emojiId: String) {
        val db: Firestore = FirestoreClient.getFirestore()
        // delete emoji from emojiDB
        db.collection(EMOJI_COLLECTION_NAME).document(emojiId).delete()
        // delete emoji from all users' saved_emojis
        val usersWithDeletedEmoji = db.collection(USER_COLLECTION_NAME)
                                        .whereArrayContains("saved_emojis", emojiId)
                                        .get().get().documents
        // TODO: 이런거 한 번에 수정하는 방법은 없나?
        for(user in usersWithDeletedEmoji) {
            user.reference.update("saved_emojis", FieldValue.arrayRemove(emojiId))
        }
    }
}