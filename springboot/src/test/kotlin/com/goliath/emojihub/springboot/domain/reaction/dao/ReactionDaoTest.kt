package com.goliath.emojihub.springboot.domain.reaction.dao

import com.goliath.emojihub.springboot.domain.TestDto
import com.goliath.emojihub.springboot.domain.reaction.dto.ReactionDto
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
@Import(ReactionDao::class)
internal class ReactionDaoTest {

    @Autowired
    lateinit var reactionDao: ReactionDao

    @MockBean
    lateinit var db: Firestore

    companion object {

        lateinit var testDB: Firestore
        const val REACTION_COLLECTION_NAME = "Reactions"
        const val CREATED_BY = "created_by"
        const val POST_ID = "post_id"
        const val EMOJI_ID = "emoji_id"
        private val testDto = TestDto()

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
    fun insertReaction() {
        // given
        val username = "new_username"
        val postId = "new_post_id"
        val emojiId = "new_emoji_id"
        Mockito.`when`(db.collection(REACTION_COLLECTION_NAME))
            .thenReturn(testDB.collection(REACTION_COLLECTION_NAME))

        // when
        val result = reactionDao.insertReaction(username, postId, emojiId)

        // then
        var reaction = reactionDao.getReaction(result.id)
        var a = 1
        while (reaction == null && a <= 5) {
            reaction = reactionDao.getReaction(result.id)
            a++
        }
        assertEquals(result, reaction)

        // after work
        reactionDao.deleteReaction(result.id)
        var reactionExist = reactionDao.existReaction(result.id)
        var b = 1
        while (reactionExist && b <= 5) {
            reactionExist = reactionDao.existReaction(result.id)
            b++
        }
        assertEquals(reactionExist, false)
    }

    @Test
    fun existReaction() {
        // given
        val reactionId = testDto.reactionList[0].id
        Mockito.`when`(db.collection(REACTION_COLLECTION_NAME))
            .thenReturn(testDB.collection(REACTION_COLLECTION_NAME))

        // when
        val result = reactionDao.existReaction(reactionId)

        // then
        assertEquals(result, true)
    }

    @Test
    fun existSameReaction() {
        // given
        val reaction = testDto.reactionList[0]
        val username = reaction.created_by
        val postId = reaction.post_id
        val emojiId = reaction.emoji_id
        Mockito.`when`(db.collection(REACTION_COLLECTION_NAME))
            .thenReturn(testDB.collection(REACTION_COLLECTION_NAME))

        // when
        val result = reactionDao.existSameReaction(username, postId, emojiId)

        // then
        assertEquals(result, true)
    }

    @Test
    fun getReaction() {
        // given
        val reaction = testDto.reactionList[0]
        val reactionId = reaction.id
        Mockito.`when`(db.collection(REACTION_COLLECTION_NAME))
            .thenReturn(testDB.collection(REACTION_COLLECTION_NAME))

        // when
        val result = reactionDao.getReaction(reactionId)

        // then
        assertEquals(result, reaction)
    }

    @Test
    fun getReactionsWithField() {
        // given
        val reactionBase = testDto.reactionList[0]
        val createdBy = reactionBase.created_by
        val postId = reactionBase.post_id
        val emojiId = reactionBase.emoji_id
        val reactionsWithCreatedBy = mutableListOf<ReactionDto>()
        val reactionsWithPostId = mutableListOf<ReactionDto>()
        val reactionsWithEmojiId = mutableListOf<ReactionDto>()
        for (reaction in testDto.reactionList) {
            if (reaction.created_by == createdBy)
                reactionsWithCreatedBy.add(reaction)
            if (reaction.post_id == postId)
                reactionsWithPostId.add(reaction)
            if (reaction.emoji_id == emojiId)
                reactionsWithEmojiId.add(reaction)
        }
        reactionsWithCreatedBy.sortByDescending { it.created_at }
        reactionsWithPostId.sortByDescending { it.created_at }
        reactionsWithEmojiId.sortByDescending { it.created_at }
        Mockito.`when`(db.collection(REACTION_COLLECTION_NAME))
            .thenReturn(testDB.collection(REACTION_COLLECTION_NAME))

        // when
        val resultWithCreatedBy = reactionDao.getReactionsWithField(createdBy, CREATED_BY)
        val resultWithPostId = reactionDao.getReactionsWithField(postId, POST_ID)
        val resultWithEmojiId = reactionDao.getReactionsWithField(emojiId, EMOJI_ID)

        // then
        assertAll(
            { assertEquals(resultWithCreatedBy, reactionsWithCreatedBy) },
            { assertEquals(resultWithPostId, reactionsWithPostId) },
            { assertEquals(resultWithEmojiId, reactionsWithEmojiId) }
        )
    }

    @Test
    fun deleteReaction() {
        // given
        val reaction = testDto.reactionList[0]
        val reactionId = reaction.id
        Mockito.`when`(db.collection(REACTION_COLLECTION_NAME))
            .thenReturn(testDB.collection(REACTION_COLLECTION_NAME))

        // when
        reactionDao.deleteReaction(reactionId)

        // then
        var reactionExist = reactionDao.existReaction(reactionId)
        var a = 1
        while( reactionExist && a <= 5) {
            reactionExist = reactionDao.existReaction(reactionId)
            a++
        }
        assertEquals(reactionExist, false)

        // after work
        testDB.collection(REACTION_COLLECTION_NAME)
            .document(reaction.id)
            .set(reaction)
        var reactionGet = reactionDao.getReaction(reactionId)
        var b = 1
        while (reactionGet == null && b<=5) {
            reactionGet = reactionDao.getReaction(reactionId)
            b++
        }
        assertEquals(reactionGet, reaction)
    }
}