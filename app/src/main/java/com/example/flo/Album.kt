package com.example.flo

import java.util.ArrayList
data class Album(
    var title: String? = "",
    var singer: String? = "",
    var coverImg: Int? = null,

    var second: Int = 0,
    var playTime: Int = 0,

    var songs: ArrayList<Song>? = null,
)