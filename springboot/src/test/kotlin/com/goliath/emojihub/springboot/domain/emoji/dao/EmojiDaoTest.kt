package com.goliath.emojihub.springboot.domain.emoji.dao

import com.goliath.emojihub.springboot.domain.TestDto
import com.goliath.emojihub.springboot.domain.emoji.dto.EmojiDto
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
    lateinit var url: URL

    companion object {

        lateinit var testDB: Firestore
        const val EMOJI_COLLECTION_NAME = "Emojis"
        private val testDto = TestDto()
        val userList = testDto.userList
        val emojiList = testDto.emojiList

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
        val expectedResult1 = mutableListOf<EmojiDto>()
        expectedResult1.addAll(emojiList)
        val expectedResult2 = mutableListOf<EmojiDto>()
        expectedResult2.addAll(emojiList)
        expectedResult1.sortByDescending { it.created_at }
        expectedResult2.sortWith(compareByDescending<EmojiDto> { it.num_saved }
            .thenByDescending { it.created_at })

        // when
        val result1 = emojiDao.getEmojis(sortByDate, index, count)
        val result2 = emojiDao.getEmojis(sorByNumSaved, index, count)

        // then
        assertAll(
            { assertEquals(result1, expectedResult1) },
            { assertEquals(result2, expectedResult2) }
        )
    }

    @Test
    fun getEmoji() {
        // given
        val emojiId = emojiList[0].id
        val wrongId = "wrong_emoji_id"
        Mockito.`when`(db.collection(EMOJI_COLLECTION_NAME))
            .thenReturn(testDB.collection(EMOJI_COLLECTION_NAME))

        // when
        val result = emojiDao.getEmoji(emojiId)
        val wrongResult = emojiDao.getEmoji(wrongId)

        // then
        assertEquals(result, emojiList[0])
        assertEquals(wrongResult, null)
    }

    @Test
    fun insertEmoji() {
        // given
        val username = userList[1].username
        val audioContent = ByteArray(100)
        val file = MockMultipartFile("file", "test.mp4", "audio/mp4", audioContent)
        val imageContent = ByteArray(100)
        val thumbnail = MockMultipartFile("thumbnail", "test.jpeg", "image/jpeg", imageContent)
        val emojiUnicode = "test_emoji_unicode"
        val emojiLabel = "test_emoji_label"
        val url = "test_url"
        val dateTime = "test_date_time"
        val blobIdPart = username + "_" + dateTime
        val emojiVideoBlobId: BlobId = BlobId.of(
            "emojihub-e2023.appspot.com",
            "$blobIdPart.mp4"
        )
        val thumbnailBlobId: BlobId = BlobId.of(
            "emojihub-e2023.appspot.com",
            "$blobIdPart.jpeg"
        )
        Mockito.`when`(db.collection(EMOJI_COLLECTION_NAME))
            .thenReturn(testDB.collection(EMOJI_COLLECTION_NAME))
        Mockito.`when`(storage.get(emojiVideoBlobId)).thenReturn(blob)
        Mockito.`when`(storage.get(thumbnailBlobId)).thenReturn(blob)
        Mockito.`when`(blob.signUrl(100, TimeUnit.DAYS)).thenReturn(this.url)
        Mockito.`when`(this.url.toString()).thenReturn(url)

        // when
        val result = emojiDao.insertEmoji(username, file, thumbnail, emojiUnicode, emojiLabel, dateTime)

        // then
        verify(storage, times(1)).get(emojiVideoBlobId)
        verify(storage, times(1)).get(thumbnailBlobId)
        verify(blob, times(2)).signUrl(100, TimeUnit.DAYS)

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
        val emoji = emojiList[0]
        val emojiId = emoji.id
        Mockito.`when`(db.collection(EMOJI_COLLECTION_NAME))
            .thenReturn(testDB.collection(EMOJI_COLLECTION_NAME))

        // when
        emojiDao.numSavedChange(emojiId, 1)

        // then
        var changedEmoji = emojiDao.getEmoji(emojiId)
        var a = 1
        while (changedEmoji!!.num_saved != emoji.num_saved + 1 && a <= 5) {
            changedEmoji = emojiDao.getEmoji(emojiId)
            a++
        }
        assertEquals(changedEmoji.num_saved, emoji.num_saved + 1)

        // after work
        emojiDao.numSavedChange(emojiId, -1)
        changedEmoji = emojiDao.getEmoji(emojiId)
        var b = 1
        while (changedEmoji!!.num_saved != emoji.num_saved && b <= 5) {
            changedEmoji = emojiDao.getEmoji(emojiId)
            b++
        }
        assertEquals(changedEmoji.num_saved, emoji.num_saved)
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