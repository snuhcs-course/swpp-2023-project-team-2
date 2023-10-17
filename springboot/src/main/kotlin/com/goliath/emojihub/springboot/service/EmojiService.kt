package com.goliath.emojihub.springboot.service

import com.goliath.emojihub.springboot.common.CustomHttp404
import com.goliath.emojihub.springboot.dao.EmojiDao
import com.goliath.emojihub.springboot.dto.emoji.EmojiDto
import com.goliath.emojihub.springboot.dto.emoji.PostEmojiRequest
import org.springframework.stereotype.Service

@Service
class EmojiService (private val emojiDao: EmojiDao) {
    fun getEmojis(numLimit: Int): List<EmojiDto> {
        return emojiDao.getEmojis(numLimit)
    }

    fun getEmoji(emojiId: String): EmojiDto? {
        if (emojiDao.existsEmoji(emojiId).not())
            throw CustomHttp404("Emoji doesn't exist.")
        return emojiDao.getEmoji(emojiId)
    }

    fun postEmoji(postEmojiRequest: PostEmojiRequest) {
        emojiDao.postEmoji(postEmojiRequest)
    }

    fun saveEmoji(userId: String, emojiId: String) {
        // TODO: check if userId, emojiId exists
        emojiDao.saveEmoji(userId, emojiId)
    }

    fun unSaveEmoji(userId: String, emojiId: String) {
        // TODO: check if userId, emojiId exists
        emojiDao.unSaveEmoji(userId, emojiId)
    }

    fun deleteEmoji(emojiId: String) {
        if (emojiDao.existsEmoji(emojiId).not())
            throw CustomHttp404("Emoji doesn't exist.")
        emojiDao.deleteEmoji(emojiId)
    }
}