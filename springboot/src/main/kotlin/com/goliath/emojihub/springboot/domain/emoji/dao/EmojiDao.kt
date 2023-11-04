package com.goliath.emojihub.springboot.domain.emoji.dao

import com.goliath.emojihub.springboot.domain.emoji.dto.EmojiDto
import com.goliath.emojihub.springboot.domain.emoji.dto.PostEmojiRequest
import com.goliath.emojihub.springboot.global.util.getDateTimeNow
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.FieldValue
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.QueryDocumentSnapshot
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.cloud.StorageClient
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream


@Repository
@Slf4j
class EmojiDao {

    companion object {
        const val USER_COLLECTION_NAME = "Users"
        const val EMOJI_COLLECTION_NAME = "Emojis"
        const val EMOJI_STORAGE_BUCKET_NAME = "emojihub-e2023.appspot.com"
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
        return document.toObject(EmojiDto::class.java)
    }

    // TODO: 이 부분은 좀 더 고민해봐야 할 것 같다.
    fun postEmoji(username: String, file: MultipartFile, postEmojiRequest: PostEmojiRequest) {
        val emojiVideoStorage : Storage = StorageClient.getInstance().bucket().storage
        // NOTE: created_by(username)을 video이름으로 넣어주어 유저별로 올린 비디오를 구분할 수 있게 한다.
        val dateTime = getDateTimeNow()
        val emojiVideoBlobId : BlobId = BlobId.of(
            EMOJI_STORAGE_BUCKET_NAME,
            username + "_" + dateTime + ".mp4")
        val emojiVideoBlob : BlobInfo = BlobInfo.newBuilder(emojiVideoBlobId)
                                                .setContentType("video/mp4")
                                                .build()
        emojiVideoStorage.createFrom(emojiVideoBlob, ByteArrayInputStream(file.bytes))
        val emojiVideoUrl = emojiVideoStorage.get(emojiVideoBlobId).mediaLink

        // upload video thumbnail to emojiBucket
        val db: Firestore = FirestoreClient.getFirestore()
        val emoji = EmojiDto(username, postEmojiRequest, emojiVideoUrl, dateTime)
        db.collection(EMOJI_COLLECTION_NAME)
            .document(emoji.id)
            .set(emoji)
        db.collection(USER_COLLECTION_NAME)
            .document(username)
            .update("created_emojis", FieldValue.arrayUnion(emoji.id))
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

    fun deleteEmoji(username: String, emojiId: String) {
        val db: Firestore = FirestoreClient.getFirestore()
        // delete emoji from all users' saved_emojis
        val usersWithDeletedEmoji = db.collection(USER_COLLECTION_NAME)
                                        .whereArrayContains("saved_emojis", emojiId)
                                        .get().get().documents
        for(user in usersWithDeletedEmoji) {
            user.reference.update("saved_emojis", FieldValue.arrayRemove(emojiId))
        }
        // delete emoji from the user's(who created this emoji) created_emojis
        val userRef = db.collection(USER_COLLECTION_NAME).document(username)
        userRef.update("created_emojis", FieldValue.arrayRemove(emojiId))
        // delete emoji from emojiDB
        db.collection(EMOJI_COLLECTION_NAME).document(emojiId).delete()
        // TODO: firebase storage에서 해당 영상 제거
    }

    fun existsEmoji(emojiId: String): Boolean {
        val db: Firestore = FirestoreClient.getFirestore()
        val future = db.collection(EMOJI_COLLECTION_NAME).document(emojiId).get()
        val document: DocumentSnapshot = future.get()
        return document.exists()
    }
}