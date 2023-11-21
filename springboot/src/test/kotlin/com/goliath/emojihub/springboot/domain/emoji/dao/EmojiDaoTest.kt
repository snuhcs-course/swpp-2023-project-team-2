package com.goliath.emojihub.springboot.domain.emoji.dao

import com.goliath.emojihub.springboot.domain.emoji.dto.EmojiDto
import com.goliath.emojihub.springboot.domain.user.dto.UserDto
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.storage.Blob
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.Storage
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.FileInputStream
import java.net.URL
import java.util.concurrent.TimeUnit

@ExtendWith(SpringExtension::class)
@Import(EmojiDao::class)
internal class EmojiDaoTest {

    @Autowired
    lateinit var emojiDao: EmojiDao

    @MockBean
    lateinit var db: Firestore

    @MockBean
    lateinit var storage: Storage

    @MockBean
    lateinit var blob: Blob

    @MockBean
    lateinit var emojiURL: URL

    companion object {

        lateinit var testDB: Firestore
        var userList: MutableList<UserDto> = mutableListOf()
        var emojiList: MutableList<EmojiDto> = mutableListOf()
        const val EMOJI_COLLECTION_NAME = "Emojis"

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            val serviceAccount =
                FileInputStream("src/test/kotlin/com/goliath/emojihub/springboot/TestServiceAccountKey.json")
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("emojihub-e2023.appspot.com")
                .build()
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options)
            }
            testDB = FirestoreClient.getFirestore()
            userList.add(UserDto("test_email0", "test_username0", "test_password0"))
            userList.add(UserDto("test_email1", "test_username1", "test_password1"))
            for (i in 0 until 2) {
                for (j in 0 until 2) {
                    userList[i].created_posts!!.add("test_post" + i + "_" + j)
                    userList[i].created_emojis!!.add("test_emoji" + i + "_" + j)
                    if (j == 1) {
                        userList[i].saved_emojis!!.add("test_emoji" + (1 - i) + "_" + j)
                    }
                }
            }
            for (i in 0 until 2) {
                for (j in 0 until 2) {
                    emojiList.add(
                        EmojiDto(
                            id = "test_emoji" + i + "_" + j,
                            created_by = "test_username$i",
                            video_url = "test_video_url" + i + "_" + j,
                            emoji_unicode = "test_emoji_unicode" + i + "_" + j,
                            emoji_label = "test_emoji_label" + i + "_" + j,
                            created_at = "test_created_at" + i + "_" + j,
                            num_saved = j
                        )
                    )
                }
            }
        }
    }

    @Test
    fun getEmojis() {
        // given
        val sortByDate = 1
        val sorByNumSaved = 0
        val index = 1
        val count = 10
        Mockito.`when`(db.collection(EMOJI_COLLECTION_NAME))
            .thenReturn(testDB.collection(EMOJI_COLLECTION_NAME))

        // when
        val result1 = emojiDao.getEmojis(sortByDate, index, count)
        val result2 = emojiDao.getEmojis(sorByNumSaved, index, count)

        // then
        assertAll(
            { assertEquals(result1.size, emojiList.size) },
            { assertEquals(result2.size, emojiList.size) },
            { assertEquals(result1[0], emojiList[3]) },
            { assertEquals(result1[1], emojiList[2]) },
            { assertEquals(result1[2], emojiList[1]) },
            { assertEquals(result1[3], emojiList[0]) },
            { assertEquals(result2[0], emojiList[3]) },
            { assertEquals(result2[1], emojiList[1]) },
            { assertEquals(result2[2], emojiList[2]) },
            { assertEquals(result2[3], emojiList[0]) }
        )
    }

    @Test
    fun getEmoji() {
        // given
        val emojiId = emojiList[0].id
        Mockito.`when`(db.collection(EMOJI_COLLECTION_NAME))
            .thenReturn(testDB.collection(EMOJI_COLLECTION_NAME))

        // when
        val result = emojiDao.getEmoji(emojiId)

        // then
        assertEquals(result, emojiList[0])
    }

    @Test
    fun insertEmoji() {
        // given
        val username = userList[1].username
        val audioContent = ByteArray(100)
        val file = MockMultipartFile("file", "test.mp4", "audio/mp4", audioContent)
        val emojiUnicode = "test_emoji_unicode"
        val emojiLabel = "test_emoji_label"
        val emojiVideoUrl = "test_emoji_video_url"
        val dateTime = "test_date_time"
        val emojiVideoBlobId: BlobId = BlobId.of(
            "emojihub-e2023.appspot.com",
            username + "_" + dateTime + ".mp4"
        )
        Mockito.`when`(db.collection(EMOJI_COLLECTION_NAME))
            .thenReturn(testDB.collection(EMOJI_COLLECTION_NAME))
        Mockito.`when`(storage.get(emojiVideoBlobId)).thenReturn(blob)
        Mockito.`when`(blob.signUrl(100, TimeUnit.DAYS)).thenReturn(emojiURL)
        Mockito.`when`(emojiURL.toString()).thenReturn(emojiVideoUrl)

        // when
        val result = emojiDao.insertEmoji(username, file, emojiUnicode, emojiLabel, dateTime)

        // then
        verify(storage, times(1)).get(emojiVideoBlobId)
        verify(blob, times(1)).signUrl(100, TimeUnit.DAYS)

        var emojiExist = emojiDao.existsEmoji(result.id)
        var a = 1
        while (!emojiExist && a <= 5) {
            emojiExist = emojiDao.existsEmoji(result.id)
            a++
        }
        assertEquals(emojiExist, true)

        // after work
        emojiDao.deleteEmoji(result.id)
        emojiExist = emojiDao.existsEmoji(result.id)
        var b = 1
        while (emojiExist && b <= 5) {
            emojiExist = emojiDao.existsEmoji(result.id)
            b++
        }
        assertEquals(emojiExist, false)
    }

    @Test
    fun numSavedChange() {
        // given
        val emojiId = emojiList[2].id
        Mockito.`when`(db.collection(EMOJI_COLLECTION_NAME))
            .thenReturn(testDB.collection(EMOJI_COLLECTION_NAME))

        // when
        emojiDao.numSavedChange(emojiId, 1)

        // then
        var emoji = emojiDao.getEmoji(emojiId)
        var a = 1
        while (emoji!!.num_saved != 1 && a <= 5) {
            emoji = emojiDao.getEmoji(emojiId)
            a++
        }
        assertEquals(emoji.num_saved, 1)

        // after work
        emojiDao.numSavedChange(emojiId, -1)
        emoji = emojiDao.getEmoji(emojiId)
        var b = 1
        while (emoji!!.num_saved != 0 && b <= 5) {
            emoji = emojiDao.getEmoji(emojiId)
            b++
        }
        assertEquals(emoji.num_saved, 0)
    }

    @Test
    fun deleteEmoji() {
        // given
        val emojiId = emojiList[3].id
        Mockito.`when`(db.collection(EMOJI_COLLECTION_NAME))
            .thenReturn(testDB.collection(EMOJI_COLLECTION_NAME))

        // when
        emojiDao.deleteEmoji(emojiId)

        // then
        var result = emojiDao.existsEmoji(emojiId)
        var a = 1
        while (result && a <= 5) {
            result = emojiDao.existsEmoji(emojiId)
            a++
        }
        assertEquals(result, false)

        // after work
        testDB.collection(EmojiDao.EMOJI_COLLECTION_NAME)
            .document(emojiId)
            .set(emojiList[3])
    }

    @Test
    fun existsEmoji() {
        // given
        val emojiId = emojiList[1].id
        Mockito.`when`(db.collection(EMOJI_COLLECTION_NAME))
            .thenReturn(testDB.collection(EMOJI_COLLECTION_NAME))

        // when
        val result = emojiDao.existsEmoji(emojiId)

        // then
        assertEquals(result, true)
    }

    @Test
    fun deleteFileInStorage() {
        // given
        val blobName = "test_blob_name"
        val blobId = BlobId.of("emojihub-e2023.appspot.com", blobName)

        // when
        emojiDao.deleteFileInStorage(blobName)

        // given
        verify(storage, times(1)).delete(blobId)
    }
}