package com.goliath.emojihub.springboot.domain.user.dao

import com.goliath.emojihub.springboot.domain.TestDto
import com.goliath.emojihub.springboot.domain.user.dto.UserDto
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.*
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.FileInputStream

@ExtendWith(SpringExtension::class)
@Import(UserDao::class)
internal class UserDaoTest {

    @Autowired
    lateinit var userDao: UserDao

    @MockBean
    lateinit var db: Firestore

    companion object {

        lateinit var testDB: Firestore
        const val USER_COLLECTION_NAME = "Users"
        const val CREATED_POSTS = "created_posts"
        const val SAVED_EMOJIS = "saved_emojis"
        private val testDto = TestDto()
        val userList = testDto.userList

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
    fun getUsers() {
        // given
        Mockito.`when`(db.collection(USER_COLLECTION_NAME))
            .thenReturn(testDB.collection(USER_COLLECTION_NAME))

        // when
        val result = userDao.getUsers()

        // then
        assertAll(
            { assertEquals(result, userList) }
        )
    }

    @Test
    fun getUser() {
        // given
        val username = userList[0].username
        Mockito.`when`(db.collection(USER_COLLECTION_NAME))
            .thenReturn(testDB.collection(USER_COLLECTION_NAME))

        // when
        val result = userDao.getUser(username)

        // then
        assertAll(
            { assertEquals(result, userList[0]) }
        )
    }

    @Test
    fun existUser() {
        // given
        Mockito.`when`(db.collection(USER_COLLECTION_NAME))
            .thenReturn(testDB.collection(USER_COLLECTION_NAME))

        // when
        val resultYes = userDao.existUser(userList[0].username)
        val resultNo = userDao.existUser("false_username")

        // then
        assertAll(
            { assertEquals(resultYes, true) },
            { assertEquals(resultNo, false) },
        )
    }

    @Test
    fun insertUser() {
        // given
        Mockito.`when`(db.collection(USER_COLLECTION_NAME))
            .thenReturn(testDB.collection(USER_COLLECTION_NAME))
        val user = UserDto(
            email = "new_test_email",
            username = "new_test_username",
            password = "new_test_password"
        )

        // when
        userDao.insertUser(user)

        // then
        var result = userDao.getUser(user.username)
        var a = 1
        while (result == null && a <= 5) {
            result = userDao.getUser(user.username)
            a++
        }
        assertAll(
            { assertEquals(a <= 5, true) },
            { assertEquals(result, user) }
        )

        // after work
        userDao.deleteUser(user.username)
        result = userDao.getUser(user.username)
        var b = 1
        while (result != null && b <= 5) {
            result = userDao.getUser(user.username)
            b++
        }
        assertEquals(result, null)
    }

    @Test
    fun deleteUser() {
        // given
        val username = userList[1].username
        Mockito.`when`(db.collection(USER_COLLECTION_NAME))
            .thenReturn(testDB.collection(USER_COLLECTION_NAME))

        // when
        userDao.deleteUser(username)

        // then
        var result: UserDto? = userDao.getUser(username)
        var a = 1
        while (result != null && a <= 5) {
            result = userDao.getUser(username)
            a++
        }
        assertNull(result)

        // after work
        userDao.insertUser(userList[1])
        result = userDao.getUser(username)
        var b = 1
        while (result == null && b <= 5) {
            result = userDao.getUser(username)
            b++
        }
        assertEquals(result, userList[1])
    }

    @Test
    fun insertId() {
        // given
        val username = userList[1].username
        val postId = "test_postId"
        Mockito.`when`(db.collection(USER_COLLECTION_NAME))
            .thenReturn(testDB.collection(USER_COLLECTION_NAME))

        // when
        userDao.insertId(username, postId, CREATED_POSTS)

        // then
        var result = userDao.getUser(username)
        var a = 1
        while (!result!!.created_posts!!.contains(postId) && a <= 5) {
            result = userDao.getUser(username)
            a++
        }
        assertThat(result.created_posts).contains(postId)

        // after work
        userDao.deleteId(username, postId, CREATED_POSTS)
        result = userDao.getUser(username)
        var b = 1
        while (result!!.created_posts!!.contains(postId) && b <= 5) {
            result = userDao.getUser(username)
            b++
        }
        assertThat(result.created_posts).doesNotContain(postId)
    }

    @Test
    fun deleteId() {
        // given
        val username = userList[1].username
        val postId = userList[1].created_posts!![1]
        Mockito.`when`(db.collection(USER_COLLECTION_NAME))
            .thenReturn(testDB.collection(USER_COLLECTION_NAME))

        // when
        userDao.deleteId(username, postId, CREATED_POSTS)

        // then
        var result = userDao.getUser(username)
        var a = 1
        while (result!!.created_posts!!.size != 1 && a <= 5) {
            result = userDao.getUser(username)
            a++
        }
        assertThat(result.created_posts).doesNotContain(username)

        // after work
        userDao.insertId(username, postId, CREATED_POSTS)
        result = userDao.getUser(username)
        var b = 1
        while (!result!!.created_posts!!.contains(postId) && b <= 5) {
            result = userDao.getUser(username)
            b++
        }
        assertThat(result.created_posts).contains(postId)
    }

    @Test
    fun deleteAllSavedEmojiId() {
        // given
        val username = userList[0].username
        val emojiId = userList[0].saved_emojis!![0]
        Mockito.`when`(db.collection(USER_COLLECTION_NAME))
            .thenReturn(testDB.collection(USER_COLLECTION_NAME))

        // when
        userDao.deleteAllSavedEmojiId(emojiId)

        // then
        var result = userDao.getUser(username)
        var a = 1
        while (result!!.saved_emojis!!.contains(emojiId) && a <= 5) {
            result = userDao.getUser(username)
            a++
        }
        assertEquals(result.saved_emojis!!.contains(emojiId), false)

        // after work
        userDao.insertId(username, emojiId, SAVED_EMOJIS)
        result = userDao.getUser(username)
        var b = 1
        while (!result!!.saved_emojis!!.contains(emojiId) && b <= 5) {
            userDao.insertId(username, emojiId, SAVED_EMOJIS)
            result = userDao.getUser(username)
            b++
        }
        assertEquals(result.saved_emojis!!.contains(emojiId), true)
    }
}