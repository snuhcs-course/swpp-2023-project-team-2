package com.goliath.emojihub.springboot.domain.post.dao

import com.goliath.emojihub.springboot.domain.TestDto
import com.goliath.emojihub.springboot.domain.post.dto.PostDto
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
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
@Import(PostDao::class)
internal class PostDaoTest {

    @Autowired
    lateinit var postDao: PostDao

    @MockBean
    lateinit var db: Firestore

    companion object {

        lateinit var testDB: Firestore
        const val POST_COLLECTION_NAME = "Posts"
        private val testDto = TestDto()
        val userList = testDto.userList
        val postList = testDto.postList

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
    fun insertPost() {
        // given
        val username = userList[0].username
        val content = "new_test_content"
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
        val expectedResult = mutableListOf<PostDto>()
        expectedResult.addAll(postList)
        expectedResult.sortByDescending { it.created_at }

        // when
        val result = postDao.getPosts(index, count)

        // then
        assertEquals(result, expectedResult)
    }

    @Test
    fun getMyPosts() {
        // given
        val username = userList[0].username
        val index = 1
        val count = testDto.postSize
        val postListForUser = mutableListOf<PostDto>()
        postListForUser.add(postList[1])
        postListForUser.add(postList[0])
        Mockito.`when`(db.collection(POST_COLLECTION_NAME))
            .thenReturn(testDB.collection(POST_COLLECTION_NAME))

        // when
        val result = postDao.getMyPosts(username, index, count)

        // then
        assertAll(
            { assertEquals(result, postListForUser) }
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
        testDB.collection(POST_COLLECTION_NAME)
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

    @Test
    fun insertAndDeleteReactionId() {
        // given
        val postId = postList[0].id
        val reactionId = "new_test_reaction_id"
        Mockito.`when`(db.collection(POST_COLLECTION_NAME))
            .thenReturn(testDB.collection(POST_COLLECTION_NAME))

        // when
        postDao.insertReactionId(postId, reactionId)

        // then
        var post = postDao.getPost(postId)
        var a = 1
        while (!post!!.reactions!!.contains(reactionId) && a <= 5) {
            post = postDao.getPost(postId)
            a++
        }
        assertThat(post.reactions).contains(reactionId)

        // after work
        postDao.deleteReactionId(postId, reactionId)
        post = postDao.getPost(postId)
        var b = 1
        while(post!!.reactions!!.contains(reactionId) && b <= 5) {
            post = postDao.getPost(postId)
            b++
        }
        assertThat(post.reactions).doesNotContain(reactionId)
    }
}