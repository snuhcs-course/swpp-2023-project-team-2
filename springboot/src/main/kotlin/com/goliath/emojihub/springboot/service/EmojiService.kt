package com.goliath.emojihub.springboot.service

import com.goliath.emojihub.springboot.dao.EmojiDao
import com.goliath.emojihub.springboot.dto.emoji.EmojiDto
import org.springframework.stereotype.Service

@Service
class EmojiService (private val emojiDao: EmojiDao) {
    fun getEmojis(numLimit: Int): List<EmojiDto> {
        return emojiDao.getEmojis(numLimit)
    }

    fun getEmoji(emojiId: String): EmojiDto? {
        return emojiDao.getEmoji(emojiId)
    }

    fun postEmoji(emoji: EmojiDto) {
        emojiDao.postEmoji(emoji)
    }

    fun saveEmoji(userId: String, emojiId: String) {
        emojiDao.saveEmoji(userId, emojiId)
    }

    fun unSaveEmoji(userId: String, emojiId: String) {
        emojiDao.unSaveEmoji(userId, emojiId)
    }

    fun deleteEmoji(emojiId: String) {
        emojiDao.deleteEmoji(emojiId)
    }
}