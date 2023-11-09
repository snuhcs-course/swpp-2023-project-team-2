package com.goliath.emojihub.springboot.domain.emoji.service

import com.goliath.emojihub.springboot.global.exception.CustomHttp404
import com.goliath.emojihub.springboot.domain.emoji.dao.EmojiDao
import com.goliath.emojihub.springboot.domain.emoji.dto.EmojiDto
import com.goliath.emojihub.springboot.domain.emoji.dto.PostEmojiRequest
import com.goliath.emojihub.springboot.domain.user.dao.UserDao
import com.goliath.emojihub.springboot.global.exception.CustomHttp400
import com.goliath.emojihub.springboot.global.exception.CustomHttp403
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class EmojiService(
    private val emojiDao: EmojiDao,
    private val userDao: UserDao,
) {
    fun getEmojis(sortByDate: Int, index: Int, count: Int): List<EmojiDto> {
        // index는 양의 정수여야 함
        if (index <= 0) throw CustomHttp400("Index should be positive integer.")
        return emojiDao.getEmojis(sortByDate, index, count)
    }

    fun getEmoji(emojiId: String): EmojiDto? {
        if (emojiDao.existsEmoji(emojiId).not()) throw CustomHttp404("Emoji doesn't exist.")
        return emojiDao.getEmoji(emojiId)
    }

    fun postEmoji(username: String, file: MultipartFile, postEmojiRequest: PostEmojiRequest) {
        emojiDao.postEmoji(username, file, postEmojiRequest)
    }

    fun saveEmoji(username: String, emojiId: String) {
        if (emojiDao.existsEmoji(emojiId).not())
            throw CustomHttp404("Emoji doesn't exist.")
        val user = userDao.getUser(username) ?: throw CustomHttp404("User doesn't exist.")
        if (user.created_emojis?.contains(emojiId) == true)
            throw CustomHttp403("User created this emoji.")
        if (user.saved_emojis?.contains(emojiId) == true)
            throw CustomHttp403("User already saved this emoji.")
        emojiDao.saveEmoji(username, emojiId)
    }

    fun unSaveEmoji(username: String, emojiId: String) {
        if (emojiDao.existsEmoji(emojiId).not())
            throw CustomHttp404("Emoji doesn't exist.")
        val user = userDao.getUser(username) ?: throw CustomHttp404("User doesn't exist.")
        if (user.created_emojis?.contains(emojiId) == true)
            throw CustomHttp403("User created this emoji.")
        if (user.saved_emojis == null || !user.saved_emojis!!.contains(emojiId))
            throw CustomHttp403("User already unsaved this emoji.")
        emojiDao.unSaveEmoji(username, emojiId)
    }

    fun deleteEmoji(username: String, emojiId: String) {
        val emoji = emojiDao.getEmoji(emojiId) ?: throw CustomHttp404("Emoji doesn't exist.")
        if (username != emoji.created_by) throw CustomHttp403("You can't delete this emoji.")
        val blobName = username + "_" + emoji.created_at + ".mp4"
        emojiDao.deleteFileInStorage(blobName)
        emojiDao.deleteEmoji(username, emojiId)
    }
}