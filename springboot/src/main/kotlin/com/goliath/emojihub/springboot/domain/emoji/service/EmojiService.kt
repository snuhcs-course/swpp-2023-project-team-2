package com.goliath.emojihub.springboot.domain.emoji.service

import com.goliath.emojihub.springboot.global.exception.CustomHttp404
import com.goliath.emojihub.springboot.domain.emoji.dao.EmojiDao
import com.goliath.emojihub.springboot.domain.emoji.dto.EmojiDto
import com.goliath.emojihub.springboot.domain.emoji.dto.PostEmojiRequest
import com.goliath.emojihub.springboot.global.exception.CustomHttp403
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class EmojiService(private val emojiDao: EmojiDao) {
    fun getEmojis(numLimit: Int): List<EmojiDto> {
        return emojiDao.getEmojis(numLimit)
    }

    fun getEmoji(emojiId: String): EmojiDto? {
        if (emojiDao.existsEmoji(emojiId).not())
            throw CustomHttp404("Emoji doesn't exist.")
        return emojiDao.getEmoji(emojiId)
    }

    fun postEmoji(username: String, file: MultipartFile, postEmojiRequest: PostEmojiRequest) {
        emojiDao.postEmoji(username, file, postEmojiRequest)
    }

    fun saveEmoji(username: String, emojiId: String) {
        if (emojiDao.existsEmoji(emojiId).not())
            throw CustomHttp404("Emoji doesn't exist.")
        emojiDao.saveEmoji(username, emojiId)
    }

    fun unSaveEmoji(username: String, emojiId: String) {
        if (emojiDao.existsEmoji(emojiId).not())
            throw CustomHttp404("Emoji doesn't exist.")
        emojiDao.unSaveEmoji(username, emojiId)
    }

    fun deleteEmoji(username: String, emojiId: String) {
        val emoji = emojiDao.getEmoji(emojiId) ?: throw CustomHttp404("Emoji doesn't exist.")
        if (username != emoji.created_by) throw CustomHttp403("You can't delete this emoji.")
        emojiDao.deleteEmoji(username, emojiId)
    }
}