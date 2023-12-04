package com.goliath.emojihub.springboot.domain.post.dao

import com.goliath.emojihub.springboot.domain.post.dto.PostDto
import com.goliath.emojihub.springboot.domain.post.dto.ReactionWithEmojiUnicode
import com.goliath.emojihub.springboot.global.util.getDateTimeNow
import com.google.cloud.firestore.*
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Repository

@Repository
@Slf4j
class PostDao(
    private val db: Firestore
) {
    companion object {
        const val POST_COLLECTION_NAME = "Posts"
        const val CONTENT = "content"
        const val CREATED_AT = "created_at"
        const val MODIFIED_AT = "modified_at"
        const val REACTIONS = "reactions"
    }

    fun insertPost(username: String, content: String): PostDto {
        val dateTime = getDateTimeNow()
        val post = PostDto(username, content, dateTime)
        db.collection(POST_COLLECTION_NAME)
            .document(post.id)
            .set(post)
        return post
    }

    fun getPosts(index: Int, count: Int): List<PostDto> {
        val list = mutableListOf<PostDto>()
        // 정렬
        val postQuery = db.collection(POST_COLLECTION_NAME).orderBy(CREATED_AT, Query.Direction.DESCENDING)
        // 페이지네이션
        val documents: List<QueryDocumentSnapshot> =
            postQuery.offset((index - 1) * count).limit(count).get().get().documents
        for (document in documents) {
            list.add(document.toObject(PostDto::class.java))
        }
        return list
    }

    fun getMyPosts(username: String, index: Int, count: Int): List<PostDto> {
        val list = mutableListOf<PostDto>()
        val postsRef = db.collection(POST_COLLECTION_NAME)
        val postQuery = postsRef.whereEqualTo("created_by", username)
            .orderBy(CREATED_AT, Query.Direction.DESCENDING)
        val documents: List<QueryDocumentSnapshot> =
            postQuery.offset((index - 1) * count).limit(count).get().get().documents
        for (document in documents) {
            list.add(document.toObject(PostDto::class.java))
        }
        return list
    }

    fun getPost(postId: String): PostDto? {
        val future = db.collection(POST_COLLECTION_NAME).document(postId).get()
        val document: DocumentSnapshot = future.get()
        return document.toObject(PostDto::class.java)
    }

    fun existPost(postId: String): Boolean {
        val future = db.collection(POST_COLLECTION_NAME).document(postId).get()
        val document: DocumentSnapshot = future.get()
        return document.exists()
    }

    fun updatePost(postId: String, content: String) {
        val dateTime = getDateTimeNow()
        val postRef = db.collection(POST_COLLECTION_NAME).document(postId)
        postRef.update(CONTENT, content)
        postRef.update(MODIFIED_AT, dateTime)
    }

    fun deletePost(postId: String) {
        db.collection(POST_COLLECTION_NAME).document(postId).delete()
    }

    fun insertReaction(postId: String, reactionId: String, emojiUnicode: String) {
        val postRef = db.collection(POST_COLLECTION_NAME).document(postId)
        val reactionWithEmojiUnicode = ReactionWithEmojiUnicode(
            id = reactionId,
            emoji_unicode = emojiUnicode
        )
        postRef.update(REACTIONS, FieldValue.arrayUnion(reactionWithEmojiUnicode))
    }

    fun deleteReaction(postId: String, reactionId: String) {
        val postRef = db.collection(POST_COLLECTION_NAME).document(postId)
        val post = postRef.get().get().toObject(PostDto::class.java)
        for (reaction in post!!.reactions) {
            if (reaction.id == reactionId) {
                postRef.update(REACTIONS, FieldValue.arrayRemove(reaction))
                return
            }
        }
    }
}