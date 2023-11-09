package com.goliath.emojihub.springboot.domain.emoji.dto

class GetEmojisRequest (
    //sortByDate = 0 or default: save 횟수로 내림차순 정렬, 1: 최신순 정렬
    var sortByDate: Int = 0,
    var index: Int = 1,
    var count: Int = 10
)