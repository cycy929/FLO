package com.example.flo

//Fragment와 그 Fragment를 호스팅 중인 액티비티 간의 통신
interface CommunicationInterface {
    fun sendData(album: Album)
}