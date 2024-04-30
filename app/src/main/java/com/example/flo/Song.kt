package com.example.flo

data class Song(
    val title : String = "",
    val singer : String = "",
    var second: Int = 0,
    var playTime:Int =0,
    var isPlaying: Boolean= false,
    var music : String = "" // 재생할 MP3 파일의 이름
)
