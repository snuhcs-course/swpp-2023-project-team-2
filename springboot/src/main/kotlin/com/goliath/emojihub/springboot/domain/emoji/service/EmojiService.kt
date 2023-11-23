package com.goliath.emojihub.springboot.domain.emoji.service

import com.goliath.emojihub.springboot.global.exception.CustomHttp404
import com.goliath.emojihub.springboot.domain.emoji.dao.EmojiDao
import com.goliath.emojihub.springboot.domain.emoji.dto.EmojiDto
import com.goliath.emojihub.springboot.domain.user.dao.UserDao
import com.goliath.emojihub.springboot.global.exception.CustomHttp400
import com.goliath.emojihub.springboot.global.exception.CustomHttp403
import com.goliath.emojihub.springboot.global.util.getDateTimeNow
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class EmojiService(
    private val emojiDao: EmojiDao,
    private val userDao: UserDao,
) {

    companion object {
        const val CREATED_EMOJIS = "created_emojis"
        const val SAVED_EMOJIS = "saved_emojis"
    }

    fun getEmojis(sortByDate: Int, index: Int, count: Int): List<EmojiDto> {
        // index는 양의 정수여야 함
        if (index <= 0) throw CustomHttp400("Index should be positive integer.")
        // count는 0보다 커야 함
        if (count <= 0) throw CustomHttp400("Count should be positive integer.")
        return emojiDao.getEmojis(sortByDate, index, count)
    }

    fun getEmoji(emojiId: String): EmojiDto? {
        if (emojiDao.existsEmoji(emojiId).not()) throw CustomHttp404("Emoji doesn't exist.")
        return emojiDao.getEmoji(emojiId)
    }

    fun postEmoji(username: String, file: MultipartFile, emojiUnicode: String, emojiLabel: String) {
        val dateTime = getDateTimeNow()
        val emoji = emojiDao.insertEmoji(username, file, emojiUnicode, emojiLabel, dateTime)
        userDao.insertId(username, emoji.id, CREATED_EMOJIS)
    }

    fun saveEmoji(username: String, emojiId: String) {
        if (emojiDao.existsEmoji(emojiId).not())
            throw CustomHttp404("Emoji doesn't exist.")
        val user = userDao.getUser(username) ?: throw CustomHttp404("User doesn't exist.")
        if (user.created_emojis?.contains(emojiId) == true)
            throw CustomHttp403("User created this emoji.")
        if (user.saved_emojis?.contains(emojiId) == true)
            throw CustomHttp403("User already saved this emoji.")
        emojiDao.numSavedChange(emojiId, 1)
        userDao.insertId(username, emojiId, SAVED_EMOJIS)
    }

    fun unSaveEmoji(username: String, emojiId: String) {
        if (emojiDao.existsEmoji(emojiId).not())
            throw CustomHttp404("Emoji doesn't exist.")
        val user = userDao.getUser(username) ?: throw CustomHttp404("User doesn't exist.")
        if (user.created_emojis?.contains(emojiId) == true)
            throw CustomHttp403("User created this emoji.")
        if (user.saved_emojis == null || !user.saved_emojis!!.contains(emojiId))
            throw CustomHttp403("User already unsaved this emoji.")
        emojiDao.numSavedChange(emojiId, -1)
        userDao.deleteId(username, emojiId, SAVED_EMOJIS)
    }

    fun deleteEmoji(username: String, emojiId: String) {
        val emoji = emojiDao.getEmoji(emojiId) ?: throw CustomHttp404("Emoji doesn't exist.")
        if (username != emoji.created_by) throw CustomHttp403("You can't delete this emoji.")
        val blobName = username + "_" + emoji.created_at + ".mp4"
        emojiDao.deleteFileInStorage(blobName)
        userDao.deleteAllSavedEmojiId(emojiId)
        userDao.deleteId(username, emojiId, CREATED_EMOJIS)
        emojiDao.deleteEmoji(emojiId)
    }
}