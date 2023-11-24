package com.goliath.emojihub.springboot.domain.reaction.dao

import com.goliath.emojihub.springboot.domain.reaction.dto.ReactionDto
import com.goliath.emojihub.springboot.global.util.getDateTimeNow
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Repository

@Repository
@Slf4j
class ReactionDao(
    private val db: Firestore
) {
    companion object {
        const val REACTION_COLLECTION_NAME = "Reactions"
        const val CREATED_BY = "created_by"
        const val POST_ID = "post_id"
        const val EMOJI_ID = "emoji_id"
        const val CREATED_AT = "created_at"
    }

    fun insertReaction(username: String, postId: String, emojiId: String): ReactionDto {
        val dateTime = getDateTimeNow()
        val reaction = ReactionDto(username, postId, emojiId, dateTime)
        db.collection(REACTION_COLLECTION_NAME)
            .document(reaction.id)
            .set(reaction)
        return reaction
    }

    fun existReaction(reactionId: String): Boolean {
        val future = db.collection(REACTION_COLLECTION_NAME).document(reactionId).get()
        val document: DocumentSnapshot = future.get()
        return document.exists()
    }

    fun existSameReaction(username: String, postId: String, emojiId: String): Boolean {
        val reactions = db.collection(REACTION_COLLECTION_NAME)
            .whereEqualTo(CREATED_BY, username)
            .whereEqualTo(POST_ID, postId)
            .whereEqualTo(EMOJI_ID, emojiId)
            .get().get().documents
        return (reactions.size != 0)
    }

    fun getReaction(reactionId: String): ReactionDto? {
        val future = db.collection(REACTION_COLLECTION_NAME).document(reactionId).get()
        val document: DocumentSnapshot = future.get()
        return document.toObject(ReactionDto::class.java)
    }

    fun getReactionsWithField(value: String, field: String): List<ReactionDto> {
        val list = mutableListOf<ReactionDto>()
        val reactionRef = db.collection(REACTION_COLLECTION_NAME)
        val reactionQuery = reactionRef.whereEqualTo(field, value)
            .orderBy(CREATED_AT, Query.Direction.DESCENDING)
        val documents = reactionQuery.get().get().documents
        for (document in documents) {
            list.add(document.toObject(ReactionDto::class.java))
        }
        return list
    }

    fun deleteReaction(reactionId: String) {
        db.collection(REACTION_COLLECTION_NAME).document(reactionId).delete()
    }
}