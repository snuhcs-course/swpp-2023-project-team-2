package com.goliath.emojihub.springboot.domain.emoji.service

import com.goliath.emojihub.springboot.global.common.CustomHttp404
import com.goliath.emojihub.springboot.domain.emoji.dao.EmojiDao
import com.goliath.emojihub.springboot.domain.emoji.dto.EmojiDto
import com.goliath.emojihub.springboot.domain.emoji.dto.PostEmojiRequest
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

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

    fun postEmoji(file: MultipartFile, postEmojiRequest: PostEmojiRequest) {
        emojiDao.postEmoji(file, postEmojiRequest)
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