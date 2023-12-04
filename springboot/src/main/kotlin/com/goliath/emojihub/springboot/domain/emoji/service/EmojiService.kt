package com.goliath.emojihub.springboot.domain.emoji.service

import com.goliath.emojihub.springboot.global.exception.CustomHttp404
import com.goliath.emojihub.springboot.domain.emoji.dao.EmojiDao
import com.goliath.emojihub.springboot.domain.emoji.dto.EmojiDto
import com.goliath.emojihub.springboot.domain.post.dao.PostDao
import com.goliath.emojihub.springboot.domain.reaction.dao.ReactionDao
import com.goliath.emojihub.springboot.domain.user.dao.UserDao
import com.goliath.emojihub.springboot.global.exception.CustomHttp400
import com.goliath.emojihub.springboot.global.exception.CustomHttp403
import com.goliath.emojihub.springboot.global.exception.ErrorType.BadRequest.INDEX_OUT_OF_BOUND
import com.goliath.emojihub.springboot.global.exception.ErrorType.BadRequest.COUNT_OUT_OF_BOUND
import com.goliath.emojihub.springboot.global.exception.ErrorType.Forbidden.USER_CREATED
import com.goliath.emojihub.springboot.global.exception.ErrorType.Forbidden.USER_ALREADY_SAVED
import com.goliath.emojihub.springboot.global.exception.ErrorType.Forbidden.USER_ALREADY_UNSAVED
import com.goliath.emojihub.springboot.global.exception.ErrorType.Forbidden.EMOJI_DELETE_FORBIDDEN
import com.goliath.emojihub.springboot.global.exception.ErrorType.NotFound.USER_NOT_FOUND
import com.goliath.emojihub.springboot.global.exception.ErrorType.NotFound.EMOJI_NOT_FOUND
import com.goliath.emojihub.springboot.global.util.StringValue.UserField.CREATED_EMOJIS
import com.goliath.emojihub.springboot.global.util.StringValue.UserField.SAVED_EMOJIS
import com.goliath.emojihub.springboot.global.util.StringValue.ReactionField.EMOJI_ID
import com.goliath.emojihub.springboot.global.util.getDateTimeNow
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.lang.Integer.min

@Service
class EmojiService(
    private val emojiDao: EmojiDao,
    private val userDao: UserDao,
    private val reactionDao: ReactionDao,
    private val postDao: PostDao
) {

    fun getEmojis(sortByDate: Int, index: Int, count: Int): List<EmojiDto> {
        // index는 양의 정수여야 함
        if (index <= 0) throw CustomHttp400(INDEX_OUT_OF_BOUND)
        // count는 0보다 커야 함
        if (count <= 0) throw CustomHttp400(COUNT_OUT_OF_BOUND)
        return emojiDao.getEmojis(sortByDate, index, count)
    }

    fun getMyEmojis(username: String, field: String, index: Int, count: Int): List<EmojiDto> {
        // index는 양의 정수여야 함
        if (index <= 0) throw CustomHttp400(INDEX_OUT_OF_BOUND)
        // count는 0보다 커야 함
        if (count <= 0) throw CustomHttp400(COUNT_OUT_OF_BOUND)
        val user = userDao.getUser(username) ?: throw CustomHttp404(USER_NOT_FOUND)
        val emojiIdList = if (field == CREATED_EMOJIS.string) {
            user.created_emojis
        } else {
            user.saved_emojis
        }
        var emojiList = mutableListOf<EmojiDto>()
        if (emojiIdList != null && emojiIdList.size != 0) {
            for (emojiId in emojiIdList) {
                val emoji = emojiDao.getEmoji(emojiId) ?: continue
                emojiList.add(emoji)
            }
            // sort
            if (emojiList.size != 0) {
                emojiList.sortByDescending { it.created_at }
                // pagination
                emojiList =  emojiList.subList(
                    min((index - 1) * count, emojiList.size - 1),
                    min(index * count, emojiList.size)
                )
            }
        }
        return emojiList
    }

    fun getEmoji(emojiId: String): EmojiDto? {
        if (emojiDao.existsEmoji(emojiId).not()) throw CustomHttp404(EMOJI_NOT_FOUND)
        return emojiDao.getEmoji(emojiId)
    }

    fun postEmoji(
        username: String,
        file: MultipartFile,
        thumbnail: MultipartFile,
        emojiUnicode: String,
        emojiLabel: String
    ) {
        val dateTime = getDateTimeNow()
        val emoji = emojiDao.insertEmoji(username, file, thumbnail, emojiUnicode, emojiLabel, dateTime)
        userDao.insertId(username, emoji.id, CREATED_EMOJIS.string)
    }

    fun saveEmoji(username: String, emojiId: String) {
        if (emojiDao.existsEmoji(emojiId).not())
            throw CustomHttp404(EMOJI_NOT_FOUND)
        val user = userDao.getUser(username) ?: throw CustomHttp404(USER_NOT_FOUND)
        if (user.created_emojis?.contains(emojiId) == true)
            throw CustomHttp403(USER_CREATED)
        if (user.saved_emojis?.contains(emojiId) == true)
            throw CustomHttp403(USER_ALREADY_SAVED)
        emojiDao.numSavedChange(emojiId, 1)
        userDao.insertId(username, emojiId, SAVED_EMOJIS.string)
    }

    fun unSaveEmoji(username: String, emojiId: String) {
        if (emojiDao.existsEmoji(emojiId).not())
            throw CustomHttp404(EMOJI_NOT_FOUND)
        val user = userDao.getUser(username) ?: throw CustomHttp404(USER_NOT_FOUND)
        if (user.created_emojis?.contains(emojiId) == true)
            throw CustomHttp403(USER_CREATED)
        if (user.saved_emojis == null || !user.saved_emojis!!.contains(emojiId))
            throw CustomHttp403(USER_ALREADY_UNSAVED)
        emojiDao.numSavedChange(emojiId, -1)
        userDao.deleteId(username, emojiId, SAVED_EMOJIS.string)
    }

    fun deleteEmoji(username: String, emojiId: String) {
        val emoji = emojiDao.getEmoji(emojiId) ?: throw CustomHttp404(EMOJI_NOT_FOUND)
        if (username != emoji.created_by) throw CustomHttp403(EMOJI_DELETE_FORBIDDEN)
        // delete file and thumbnail in DB
        val fileBlobName = username + "_" + emoji.created_at + ".mp4"
        val thumbnailBlobName = username + "_" + emoji.created_at + ".jpeg"
        emojiDao.deleteFileInStorage(fileBlobName)
        emojiDao.deleteFileInStorage(thumbnailBlobName)
        // delete all reactions(and reaction id in posts) using this emoji
        val reactions = reactionDao.getReactionsWithField(emojiId, EMOJI_ID.string)
        for (reaction in reactions) {
            postDao.deleteReaction(reaction.post_id, reaction.id)
            reactionDao.deleteReaction(reaction.id)
        }
        // delete all saved_emoji ids in users
        userDao.deleteAllSavedEmojiId(emojiId)
        // delete created_emoji id in user
        userDao.deleteId(username, emojiId, CREATED_EMOJIS.string)
        // delete emoji
        emojiDao.deleteEmoji(emojiId)
    }
}