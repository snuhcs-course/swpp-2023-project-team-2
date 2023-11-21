package com.goliath.emojihub.springboot.domain.post.service

import com.goliath.emojihub.springboot.domain.post.dao.PostDao
import com.goliath.emojihub.springboot.domain.post.dto.PostDto
import com.goliath.emojihub.springboot.domain.user.dao.UserDao
import com.goliath.emojihub.springboot.global.exception.CustomHttp400
import com.goliath.emojihub.springboot.global.exception.CustomHttp403
import com.goliath.emojihub.springboot.global.exception.CustomHttp404
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(PostService::class)
internal class PostServiceTest {

    @Autowired
    lateinit var postService: PostService

    @MockBean
    lateinit var postDao: PostDao

    @MockBean
    lateinit var userDao: UserDao

    companion object {
        const val CREATED_POSTS = "created_posts"
    }

    @Test
    @DisplayName("게시글 올리기")
    fun postPost() {
        // given
        val username = "test_username"
        val content = "test_content"
        val post = PostDto(
            id = "test_id",
            created_by = username,
            content = content,
            created_at = "test_created_at",
            modified_at = "test_modified_at"
        )
        Mockito.`when`(postDao.insertPost(username, content)).thenReturn(post)

        // when
        postService.postPost(username, content)

        // then
        verify(postDao, times(1)).insertPost(username, content)
        verify(userDao, times(1)).insertId(username, post.id, CREATED_POSTS)
    }

    @Test
    @DisplayName("게시글 데이터 가져오기")
    fun getPosts() {
        // given
        val list = mutableListOf<PostDto>()
        val size = 2
        for (i in 0 until size) {
            list.add(
                PostDto(
                    id = "test_id$i",
                    created_by = "test_created_by$i",
                    content = "test_content$i",
                    created_at = "test_created_at$i",
                    modified_at = "test_modified_at$i"
                )
            )
        }
        val index = 1
        val count = 10
        val wrongIndex = 0
        val wrongCount = 0
        Mockito.`when`(postDao.getPosts(index, count)).thenReturn(list)

        // when
        val result = postService.getPosts(index, count)
        val assertThrows1 = assertThrows(CustomHttp400::class.java) {
            postService.getPosts(wrongIndex, count)
        }
        val assertThrows2 = assertThrows(CustomHttp400::class.java) {
            postService.getPosts(index, wrongCount)
        }

        // then
        assertAll(
            { assertEquals(result, list) },
            { assertEquals(assertThrows1.message, "Index should be positive integer.") },
            { assertEquals(assertThrows2.message, "Count should be positive integer.") }
        )
        verify(postDao, times(1)).getPosts(index, count)
    }

    @Test
    @DisplayName("특정 게시글 데이터 가져오기")
    fun getPost() {
        // given
        val id = "test_id"
        val wrongId = "wrong_id"
        val post = PostDto(
            id = id,
            created_by = "test_created_by",
            content = "test_content",
            created_at = "test_created_at",
            modified_at = "test_modified_at"
        )
        Mockito.`when`(postDao.existPost(id)).thenReturn(true)
        Mockito.`when`(postDao.existPost(wrongId)).thenReturn(false)
        Mockito.`when`(postDao.getPost(id)).thenReturn(post)

        // when
        val result = postService.getPost(id)
        val assertThrows = assertThrows(CustomHttp404::class.java) {
            postService.getPost(wrongId)
        }

        // then
        assertAll(
            { assertEquals(result, post) },
            { assertEquals(assertThrows.message, "Post doesn't exist.") }
        )
        verify(postDao, times(1)).existPost(id)
        verify(postDao, times(1)).existPost(wrongId)
        verify(postDao, times(1)).getPost(id)
    }

    @Test
    @DisplayName("게시글 수정")
    fun patchPost() {
        // given
        val username = "test_username"
        val wrongUsername = "wrong_username"
        val id = "test_id"
        val wrongId = "wrong_id"
        val content = "new_content"
        val post = PostDto(
            id = id,
            created_by = username,
            content = "test_content",
            created_at = "test_created_at",
            modified_at = "test_modified_at"
        )
        Mockito.`when`(postDao.getPost(id)).thenReturn(post)
        Mockito.`when`(postDao.getPost(wrongId)).thenReturn(null)

        // when
        val result = postService.patchPost(username, id, content)
        val assertThrows1 = assertThrows(CustomHttp404::class.java) {
            postService.patchPost(username, wrongId, content)
        }
        val assertThrows2 = assertThrows(CustomHttp403::class.java) {
            postService.patchPost(wrongUsername, id, content)
        }

        // then
        assertAll(
            { assertEquals(result, Unit) },
            { assertEquals(assertThrows1.message, "Post doesn't exist.") },
            { assertEquals(assertThrows2.message, "You can't update this post.") }
        )
        verify(postDao, times(2)).getPost(id)
        verify(postDao, times(1)).getPost(wrongId)
        verify(postDao, times(1)).updatePost(id, content)
    }

    @Test
    @DisplayName("게시글 삭제")
    fun deletePost() {
        // given
        val username = "test_username"
        val wrongUsername = "wrong_username"
        val id = "test_id"
        val wrongId = "wrong_id"
        val post = PostDto(
            id = id,
            created_by = username,
            content = "test_content",
            created_at = "test_created_at",
            modified_at = "test_modified_at"
        )
        Mockito.`when`(postDao.getPost(id)).thenReturn(post)
        Mockito.`when`(postDao.getPost(wrongId)).thenReturn(null)

        // when
        val result = postService.deletePost(username, id)
        val assertThrows1 = assertThrows(CustomHttp404::class.java) {
            postService.deletePost(username, wrongId)
        }
        val assertThrows2 = assertThrows(CustomHttp403::class.java) {
            postService.deletePost(wrongUsername, id)
        }

        // then
        assertAll(
            { assertEquals(result, Unit) },
            { assertEquals(assertThrows1.message, "Post doesn't exist.") },
            { assertEquals(assertThrows2.message, "You can't delete this post.") }
        )
        verify(postDao, times(2)).getPost(id)
        verify(postDao, times(1)).getPost(wrongId)
        verify(userDao, times(1)).deleteId(username, id, CREATED_POSTS)
        verify(postDao, times(1)).deletePost(id)
    }
}