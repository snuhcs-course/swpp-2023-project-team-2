package com.goliath.emojihub.springboot.domain.emoji.dao

import com.goliath.emojihub.springboot.domain.emoji.dto.EmojiDto
import com.goliath.emojihub.springboot.global.util.StringValue.Bucket.EMOJI_STORAGE_BUCKET_NAME
import com.goliath.emojihub.springboot.global.util.StringValue.Collection.EMOJI_COLLECTION_NAME
import com.goliath.emojihub.springboot.global.util.StringValue.EmojiField.NUM_SAVED
import com.goliath.emojihub.springboot.global.util.StringValue.EmojiField.CREATED_AT
import com.google.cloud.firestore.*
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import lombok.extern.slf4j.Slf4j
import org.apache.http.entity.ContentType
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

    fun getEmojis(sortByDate: Int, index: Int, count: Int): List<EmojiDto> {
        val list = mutableListOf<EmojiDto>()
        // sortByDate 값에 따른 정렬
        val emojiQuery = if (sortByDate == 0) {
            db.collection(EMOJI_COLLECTION_NAME.string)
                .orderBy(NUM_SAVED.string, Query.Direction.DESCENDING)
                .orderBy(CREATED_AT.string, Query.Direction.DESCENDING)
        } else {
            db.collection(EMOJI_COLLECTION_NAME.string)
                .orderBy(CREATED_AT.string, Query.Direction.DESCENDING)
        }
        // 페이지네이션
        val documents: List<QueryDocumentSnapshot> = emojiQuery.offset((index - 1) * count).limit(count).get().get().documents
        for (document in documents) {
            list.add(document.toObject(EmojiDto::class.java))
        }
        return list
    }

    fun getEmoji(emojiId: String): EmojiDto? {
        val future = db.collection(EMOJI_COLLECTION_NAME.string).document(emojiId).get()
        val document: DocumentSnapshot = future.get()
        return document.toObject(EmojiDto::class.java)
    }

    fun insertEmoji(username: String, file: MultipartFile, thumbnail: MultipartFile, emojiUnicode: String, emojiLabel: String, dateTime: String): EmojiDto {
        // NOTE: created_by(username)을 video이름으로 넣어주어 유저별로 올린 비디오를 구분할 수 있게 한다.
        val blobIdPart = username + "_" + dateTime
        val emojiVideoBlobId: BlobId = BlobId.of(
            EMOJI_STORAGE_BUCKET_NAME.string,
            "$blobIdPart.mp4"
        )
        val emojiVideoBlob: BlobInfo = BlobInfo.newBuilder(emojiVideoBlobId)
            .setContentType("video/mp4")
            .build()
        storage.createFrom(emojiVideoBlob, ByteArrayInputStream(file.bytes))
        val emojiVideoUrl = storage.get(emojiVideoBlobId).signUrl(100, TimeUnit.DAYS)
        // upload video thumbnail to emojiBucket
        val thumbnailBlobId: BlobId = BlobId.of(
            EMOJI_STORAGE_BUCKET_NAME.string,
            "$blobIdPart.jpeg"
        )
        val thumbnailBlob: BlobInfo = BlobInfo.newBuilder(thumbnailBlobId)
            .setContentType(ContentType.IMAGE_JPEG.toString())
            .build()
        storage.createFrom(thumbnailBlob, ByteArrayInputStream(thumbnail.bytes))
        val thumbnailUrl = storage.get(thumbnailBlobId).signUrl(100, TimeUnit.DAYS)
        val emoji = EmojiDto(username, emojiUnicode, emojiLabel, emojiVideoUrl.toString(), dateTime, thumbnailUrl.toString())
        db.collection(EMOJI_COLLECTION_NAME.string)
            .document(emoji.id)
            .set(emoji)
        return emoji
    }

    fun numSavedChange(emojiId: String, num: Long) {
        val emojiRef = db.collection(EMOJI_COLLECTION_NAME.string).document(emojiId)
        emojiRef.update(NUM_SAVED.string, FieldValue.increment(num))
    }

    fun deleteEmoji(emojiId: String) {
        db.collection(EMOJI_COLLECTION_NAME.string).document(emojiId).delete()
    }

    fun existsEmoji(emojiId: String): Boolean {
        val future = db.collection(EMOJI_COLLECTION_NAME.string).document(emojiId).get()
        val document: DocumentSnapshot = future.get()
        return document.exists()
    }

    fun deleteFileInStorage(blobName: String) {
        val blobId = BlobId.of(EMOJI_STORAGE_BUCKET_NAME.string, blobName)
        storage.delete(blobId)
    }
}