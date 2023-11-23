package com.goliath.emojihub.springboot.domain.emoji.dao

import com.goliath.emojihub.springboot.domain.emoji.dto.EmojiDto
import com.goliath.emojihub.springboot.domain.emoji.dto.PostEmojiRequest
import com.goliath.emojihub.springboot.domain.user.dto.UserDto
import com.goliath.emojihub.springboot.global.util.getDateTimeNow
import com.google.cloud.firestore.*
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.util.concurrent.TimeUnit


@Repository
@Slf4j
class EmojiDao(
    private val db: Firestore,
    private val storage: Storage
) {

    companion object {
        const val USER_COLLECTION_NAME = "Users"
        const val EMOJI_COLLECTION_NAME = "Emojis"
        const val EMOJI_STORAGE_BUCKET_NAME = "emojihub-e2023.appspot.com"
    }

    fun getEmojis(sortByDate: Int, index: Int, count: Int): List<EmojiDto> {
        val list = mutableListOf<EmojiDto>()
        // sortByDate 값에 따른 정렬
        val emojiQuery = if (sortByDate == 0) {
            db.collection(EMOJI_COLLECTION_NAME)
                .orderBy("num_saved", Query.Direction.DESCENDING)
                .orderBy("created_at", Query.Direction.DESCENDING)
        } else {
            db.collection(EMOJI_COLLECTION_NAME)
                .orderBy("created_at", Query.Direction.DESCENDING)
        }
        // 페이지네이션
        val documents: List<QueryDocumentSnapshot> = emojiQuery.offset((index - 1) * count).limit(count).get().get().documents
        for (document in documents) {
            list.add(document.toObject(EmojiDto::class.java))
        }
        return list
    }

    fun getEmoji(emojiId: String): EmojiDto? {
        val future = db.collection(EMOJI_COLLECTION_NAME).document(emojiId).get()
        val document: DocumentSnapshot = future.get()
        return document.toObject(EmojiDto::class.java)
    }

    fun postEmoji(username: String, file: MultipartFile, postEmojiRequest: PostEmojiRequest) {
        // NOTE: created_by(username)을 video이름으로 넣어주어 유저별로 올린 비디오를 구분할 수 있게 한다.
        val dateTime = getDateTimeNow()
        val emojiVideoBlobId: BlobId = BlobId.of(
            EMOJI_STORAGE_BUCKET_NAME,
            username + "_" + dateTime + ".mp4"
        )
        val emojiVideoBlob: BlobInfo = BlobInfo.newBuilder(emojiVideoBlobId)
            .setContentType("video/mp4")
            .build()
        storage.createFrom(emojiVideoBlob, ByteArrayInputStream(file.bytes))
        val emojiVideoUrl = storage.get(emojiVideoBlobId).signUrl(100, TimeUnit.DAYS)
        // upload video thumbnail to emojiBucket
        val emoji = EmojiDto(username, postEmojiRequest, emojiVideoUrl.toString(), dateTime)
        db.collection(EMOJI_COLLECTION_NAME)
            .document(emoji.id)
            .set(emoji)
        db.collection(USER_COLLECTION_NAME)
            .document(username)
            .update("created_emojis", FieldValue.arrayUnion(emoji.id))
    }

    fun saveEmoji(userId: String, emojiId: String) {
        val userRef = db.collection(USER_COLLECTION_NAME).document(userId)
        val user = userRef.get().get().toObject(UserDto::class.java)!!
        if (user.saved_emojis?.contains(emojiId) == true) {//or in user.created_emojis)
            return
        }
        userRef.update("saved_emojis", FieldValue.arrayUnion(emojiId))
        val emojiRef = db.collection(EMOJI_COLLECTION_NAME).document(emojiId)
        emojiRef.update("num_saved", FieldValue.increment(1))
    }

    fun unSaveEmoji(userId: String, emojiId: String) {
        val userRef = db.collection(USER_COLLECTION_NAME).document(userId)
        userRef.update("saved_emojis", FieldValue.arrayRemove(emojiId))
        val emojiRef = db.collection(EMOJI_COLLECTION_NAME).document(emojiId)
        emojiRef.update("num_saved", FieldValue.increment(-1))
    }

    fun deleteEmoji(username: String, emojiId: String) {
        // delete emoji from all users' saved_emojis
        val usersWithDeletedEmoji = db.collection(USER_COLLECTION_NAME)
            .whereArrayContains("saved_emojis", emojiId)
            .get().get().documents
        for (user in usersWithDeletedEmoji) {
            user.reference.update("saved_emojis", FieldValue.arrayRemove(emojiId))
        }
        // delete emoji from the user's(who created this emoji) created_emojis
        val userRef = db.collection(USER_COLLECTION_NAME).document(username)
        userRef.update("created_emojis", FieldValue.arrayRemove(emojiId))
        // delete emoji from emojiDB
        db.collection(EMOJI_COLLECTION_NAME).document(emojiId).delete()
    }

    fun existsEmoji(emojiId: String): Boolean {
        val future = db.collection(EMOJI_COLLECTION_NAME).document(emojiId).get()
        val document: DocumentSnapshot = future.get()
        return document.exists()
    }

    fun deleteFileInStorage(blobName: String) {
        val blobId = BlobId.of(EMOJI_STORAGE_BUCKET_NAME, blobName)
        storage.delete(blobId)
    }
}