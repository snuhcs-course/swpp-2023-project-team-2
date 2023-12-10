package com.goliath.emojihub.springboot.global.exception

enum class ErrorType {
    ;

    interface ErrorTypeInterface {
        fun getMessage(): String
    }

    enum class BadRequest(private val message: String): ErrorTypeInterface {
        NO_TOKEN("There is no token."),
        INVALID_TOKEN("Token is invalid."),
        INDEX_OUT_OF_BOUND("Index should be positive integer."),
        COUNT_OUT_OF_BOUND("Count should be positive integer."),
        ;
        override fun getMessage(): String = message
    }

    enum class Unauthorized(private val message: String): ErrorTypeInterface {
        EXPIRED_TOKEN("Token is expired."),
        PASSWORD_INCORRECT("Password is incorrect."),
        ;
        override fun getMessage(): String = message
    }

    enum class Forbidden(private val message: String): ErrorTypeInterface {
        USER_CREATED("User created this emoji."),
        USER_ALREADY_SAVED("User already saved this emoji."),
        USER_ALREADY_UNSAVED("User already unsaved this emoji."),
        USER_ALREADY_REACT("User already react to this post with this emoji."),
        POST_UPDATE_FORBIDDEN("You can't update this post."),
        POST_DELETE_FORBIDDEN("You can't delete this post."),
        EMOJI_DELETE_FORBIDDEN("You can't delete this emoji."),
        REACTION_DELETE_FORBIDDEN("You can't delete this reaction."),
        ;
        override fun getMessage(): String = message
    }

    enum class NotFound(private val message: String): ErrorTypeInterface {
        USER_FROM_TOKEN_NOT_FOUND("Username from the token doesn't exist."),
        USER_NOT_FOUND("User doesn't exist."),
        ID_NOT_FOUND("Id doesn't exist."),
        POST_NOT_FOUND("Post doesn't exist"),
        EMOJI_NOT_FOUND("Emoji doesn't exist."),
        REACTION_NOT_FOUND("Reaction doesn't exist."),
        ;

        override fun getMessage(): String = message
    }

    enum class Conflict(private val message: String): ErrorTypeInterface {
        ID_EXIST("Id already exists.")
        ;
        override fun getMessage(): String = message
    }
}