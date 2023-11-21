package com.goliath.emojihub.springboot.domain.post.dao

import com.goliath.emojihub.springboot.domain.post.dto.PostDto
import com.goliath.emojihub.springboot.domain.user.dto.UserDto
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
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
@Import(PostDao::class)
internal class PostDaoTest {

    @Autowired
    lateinit var postDao: PostDao

    @MockBean
    lateinit var db: Firestore

    companion object {

        lateinit var testDB: Firestore
        var userList: MutableList<UserDto> = mutableListOf()
        var postList: MutableList<PostDto> = mutableListOf()
        const val POST_COLLECTION_NAME = "Posts"

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
                }
            }
            for (i in 0 until 2) {
                for (j in 0 until 2) {
                    postList.add(
                        PostDto(
                            content = "test_content" + i + "_" + j,
                            created_at = "test_created_at" + i + "_" + j,
                            created_by = "test_username$i",
                            id = "test_post" + i + "_" + j,
                            modified_at = "test_modified_at" + i + "_" + j,

                            )
                    )
                }
            }
        }
    }

    @Test
    fun insertPost() {
        // given
        val username = userList[0].username
        val content = "test_content0_2"
        Mockito.`when`(db.collection(POST_COLLECTION_NAME))
            .thenReturn(testDB.collection(POST_COLLECTION_NAME))

        // when
        val result = postDao.insertPost(username, content)

        // then
        var postExist = postDao.existPost(result.id)
        var a = 1
        while (!postExist && a <= 5) {
            postExist = postDao.existPost(result.id)
            a++
        }
        assertEquals(postExist, true)

        // after work
        postDao.deletePost(result.id)
        postExist = postDao.existPost(result.id)
        var b = 1
        while (postExist && b <= 5) {
            postExist = postDao.existPost(result.id)
            b++
        }
        assertEquals(postExist, false)
    }

    @Test
    fun getPosts() {
        // given
        val index = 1
        val count = 10
        Mockito.`when`(db.collection(POST_COLLECTION_NAME))
            .thenReturn(testDB.collection(POST_COLLECTION_NAME))

        // when
        val result = postDao.getPosts(index, count)

        // then
        assertAll(
            { assertEquals(result.size, postList.size) },
            { assertEquals(result[0], postList[3]) },
            { assertEquals(result[1], postList[2]) },
            { assertEquals(result[2], postList[1]) },
            { assertEquals(result[3], postList[0]) }
        )
    }

    @Test
    fun getPost() {
        // given
        val id = postList[0].id
        Mockito.`when`(db.collection(POST_COLLECTION_NAME))
            .thenReturn(testDB.collection(POST_COLLECTION_NAME))

        // when
        val result = postDao.getPost(id)

        // then
        assertEquals(result, postList[0])
    }

    @Test
    fun existPost() {
        // given
        val id = postList[0].id
        Mockito.`when`(db.collection(POST_COLLECTION_NAME))
            .thenReturn(testDB.collection(POST_COLLECTION_NAME))

        // when
        val result = postDao.existPost(id)

        // then
        assertEquals(result, true)
    }

    @Test
    fun updatePost() {
        // given
        val id = postList[1].id
        val content = "new_content"
        Mockito.`when`(db.collection(POST_COLLECTION_NAME))
            .thenReturn(testDB.collection(POST_COLLECTION_NAME))

        // when
        postDao.updatePost(id, content)

        // then
        var result = postDao.getPost(id)
        var a = 1
        while (result!!.content != content && a <= 5) {
            result = postDao.getPost(id)
            a++
        }
        assertEquals(result.content, content)

        // after work
        val postRef = testDB.collection(POST_COLLECTION_NAME).document(id)
        postRef.update("content", postList[1].content)
        postRef.update("modified_at", postList[1].modified_at)
        result = postDao.getPost(id)
        var b = 1
        while ((result!!.content != postList[1].content ||
                    result.modified_at != postList[1].modified_at) && b <= 5
        ) {
            result = postDao.getPost(id)
            b++
        }
        assertEquals(result.content, postList[1].content)
        assertEquals(result.modified_at, postList[1].modified_at)
    }

    @Test
    fun deletePost() {
        // given
        val id = postList[2].id
        Mockito.`when`(db.collection(POST_COLLECTION_NAME))
            .thenReturn(testDB.collection(POST_COLLECTION_NAME))

        // when
        postDao.deletePost(id)

        // then
        var postExist = postDao.existPost(id)
        var a = 1
        while (postExist && a <= 5) {
            postExist = postDao.existPost(id)
            a++
        }
        assertEquals(postExist, false)

        // after work
        testDB.collection(PostDao.POST_COLLECTION_NAME)
            .document(id)
            .set(postList[2])
        postExist = postDao.existPost(id)
        var b = 1
        while (!postExist && b <= 5) {
            postExist = postDao.existPost(id)
            b++
        }
        assertEquals(postExist, true)
    }
}