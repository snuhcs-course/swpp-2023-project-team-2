package com.goliath.emojihub.springboot.global.util

enum class StringValue {
    ;

    enum class FilePathName(val string: String) {
        SERVICE_ACCOUNT_KEY("springboot/src/main/resources/serviceAccountKey.json"),
        TEST_SERVICE_ACCOUNT_KEY("src/test/kotlin/com/goliath/emojihub/springboot/TestServiceAccountKey.json"),
    }

    enum class JWT(val string: String) {
        JWT_TOKEN_PREFIX("Bearer "),
    }

    enum class Header(val string: String) {
        AUTHORIZATION("Authorization"),
    }

    enum class Bucket(val string: String) {
        EMOJI_STORAGE_BUCKET_NAME("emojihub-e2023.appspot.com"),
        TEST_EMOJI_STORAGE_BUCKET_NAME("emojihub-e2023.appspot.com"),
    }

    enum class Collection(val string: String) {
        EMOJI_COLLECTION_NAME("Emojis"),
        POST_COLLECTION_NAME("Posts"),
        REACTION_COLLECTION_NAME("Reactions"),
        USER_COLLECTION_NAME("Users"),
    }

    enum class EmojiField(val string: String) {
        CREATED_AT("created_at"),
        NUM_SAVED("num_saved"),
    }

    enum class PostField(val string: String) {
        CONTENT("content"),
        CREATED_AT("created_at"),
        MODIFIED_AT("modified_at"),
        REACTIONS("reactions"),
    }

    enum class ReactionField(val string: String) {
        CREATED_BY("created_by"),
        POST_ID("post_id"),
        EMOJI_ID("emoji_id"),
        CREATED_AT("created_at"),
    }

    enum class UserField(val string: String) {
        USERNAME("username"),
        CREATED_EMOJIS("created_emojis"),
        SAVED_EMOJIS("saved_emojis"),
        CREATED_POSTS("created_posts"),
    }
}
