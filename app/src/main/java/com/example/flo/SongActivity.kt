package com.example.flo

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding
import com.google.gson.Gson
import java.util.Timer

class SongActivity : AppCompatActivity() {

    lateinit var binding : ActivitySongBinding
    lateinit var song: Song
    lateinit var timer: Timer

    // 추후에 미디어 플레이어를 해제하기 위해 nullable로 선언
    private var mediaPlayer : MediaPlayer? = null
    private var gson : Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initSong()
        setPlayer(song)

        binding.songDownIb.setOnClickListener {
            finish()
        }

        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(true)
        }

        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(false)
        }

        binding.songRepeatIv.setOnClickListener {


        }

        if(intent.hasExtra("title") && intent.hasExtra("singer")){
            binding.songMusicTitleTv.text = intent.getStringExtra("title")
            binding.songSingerNameTv.text = intent.getStringExtra("singer")
        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
    //사용자가 포커스를 잃었을때 음악이 중지
    override fun onPause(){
        super.onPause()
        setPlayerStatus(false)
        song.second = ((binding.songProgressSb.progress*song.playTime)/100)/1000
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()// 에디터
        val songJson = gson.toJson(song)
        editor.putString("songData", songJson)

        editor.apply()

    }
    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release() // 미디어 플레이어가 갖고 있던 리소스를 해제한다.
        mediaPlayer = null // 미디어 플레이어를 해제한다.
    }

    private fun initSong(){
        if(intent.hasExtra("title") && intent.hasExtra("singer")){
            song = Song(
                intent.getStringExtra("title")!!,
                intent.getStringExtra("singer")!!,
                intent.getIntExtra("second",0),
                intent.getIntExtra("playTime",0),
                intent.getBooleanExtra("isPlaying",false),
                intent.getStringExtra("music")!!
            )
        }
        startTimer()
    }

    private fun setPlayer(song: Song){
        binding.songMusicTitleTv.text = intent.getStringExtra("title")!!
        binding.songSingerNameTv.text = intent.getStringExtra("singer")!!
        binding.songStartTimeTv.text = String.format("%02d:%02d",song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d",song.playTime / 60, song.playTime % 60)
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime)

        val music = resources.getIdentifier(song.music, "raw",this.packageName)
        mediaPlayer = MediaPlayer.create(this,music)
        setPlayerStatus(song.isPlaying)

    }

    fun setPlayerStatus(isPlaying : Boolean){
        song.isPlaying = isPlaying
        timer.isPlaying = isPlaying

        if(isPlaying){
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
            mediaPlayer?.start()
        }
        else{
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE

            // 재생 중이 아닐 때, pause를 호출하면 에러가 나기 때문에 이를 방지
            if(mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }

        }
    }

    private fun startTimer(){
        timer = Timer(song.playTime,song.isPlaying)
        timer.start()
    }
    inner class Timer(private val playTime: Int,var isPlaying: Boolean = true):Thread(){

        private var second : Int = 0
        private var mills: Float = 0f

        override fun run() {
            super.run()
            try {
                while (true){

                    if (second >= playTime){
                        break
                    }

                    if (isPlaying){
                        sleep(50)
                        mills += 50

                        runOnUiThread {
                            binding.songProgressSb.progress = ((mills / playTime)*100).toInt()
                        }

                        if (mills % 1000 == 0f){
                            runOnUiThread {
                                binding.songStartTimeTv.text = String.format("%02d:%02d",second / 60, second % 60)
                            }
                            second++
                        }

                    }

                }

            }catch (e: InterruptedException){
                Log.d("Song","쓰레드가 죽었습니다. ${e.message}")
            }


        }
    }
}
