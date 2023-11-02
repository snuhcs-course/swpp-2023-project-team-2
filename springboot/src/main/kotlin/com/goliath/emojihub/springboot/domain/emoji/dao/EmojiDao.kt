package com.goliath.emojihub.springboot.domain.emoji.dao

import com.goliath.emojihub.springboot.domain.emoji.dto.EmojiDto
import com.goliath.emojihub.springboot.domain.emoji.dto.PostEmojiRequest
import com.goliath.emojihub.springboot.global.util.getDateTimeNow
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.FieldValue
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.QueryDocumentSnapshot
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.google.firebase.cloud.FirestoreClient
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.FileInputStream


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
    fun postEmoji(file: MultipartFile, postEmojiRequest: PostEmojiRequest) {
        // TODO: upload video file to emojiBucket
        // PostEmojiRequest has byte array of video file
        val serviceAccount = FileInputStream("springboot/src/main/resources/serviceAccountKey.json")
        val emojiVideoStorage : Storage = StorageOptions.newBuilder()
            .setProjectId("emojihub-e2023")
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build().service
        print(emojiVideoStorage)
//        val emoji VideoStorage : Storage = StorageClient.getInstance().bucket().storage
        // NOTE: created_by(username)을 video이름으로 넣어주어 유저별로 올린 비디오를 구분할 수 있게 한다.
        val dateTime = getDateTimeNow()
        val emojiVideoBlobId : BlobId = BlobId.of(
            EMOJI_STORAGE_BUCKET_NAME,
            postEmojiRequest.created_by + "_" + dateTime + ".mp4")
        val emojiVideoBlob : BlobInfo = BlobInfo.newBuilder(emojiVideoBlobId)
                                                .setContentType("video/mp4")
                                                .build()
//        val byteVideoContents : ByteArray = postEmojiRequest.video_content
//                                                            .toByteArray(StandardCharsets.UTF_8)

        emojiVideoStorage.createFrom(emojiVideoBlob, ByteArrayInputStream(file.bytes))
        val emojiVideoUrl = emojiVideoStorage.get(emojiVideoBlobId).mediaLink
//        val emojiVideoUrl = emojiVideoStorage.get(BlobId.of(EMOJI_STORAGE_BUCKET_NAME, "ThumbUp.mp4"))

        // FIXME: 아직 작업중!!
        // upload video thumbnail to emojiBucket
        val db: Firestore = FirestoreClient.getFirestore()
        val emoji = EmojiDto(postEmojiRequest, emojiVideoUrl, dateTime)
        db.collection(EMOJI_COLLECTION_NAME)
            .document(emoji.id)
            .set(emoji)
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

    fun existsEmoji(emojiId: String): Boolean {
        val db: Firestore = FirestoreClient.getFirestore()
        val future = db.collection(EMOJI_COLLECTION_NAME).document(emojiId).get()
        val document: DocumentSnapshot = future.get()
        return document.exists()
    }
}