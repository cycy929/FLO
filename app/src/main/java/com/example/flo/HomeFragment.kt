package com.example.flo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentHomeBinding
import com.google.gson.Gson
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class HomeFragment : Fragment(),CommunicationInterface {

    lateinit var binding: FragmentHomeBinding
    private var albumDatas = ArrayList<Album>()
    private val timer = Timer()
    private val handler = Handler(Looper.getMainLooper())

    override fun sendData(album: Album) {
        if (activity is MainActivity) {
            val activity = activity as MainActivity
            Log.d("HomeFragment", "sendData called with album: ${album.title}")
            activity.updateMiniPlayer(album)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

//        binding.homeAlbumImgIv1.setOnClickListener {
//            (context as MainActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.main_frm , AlbumFragment())
//                .commitAllowingStateLoss()
//        }

        // 데이터 리스트 생성 더미 데이터
        albumDatas.apply {
            add(Album("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp))
            add(Album("Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
            add(Album("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3))
            add(Album("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4))
            add(Album("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5))
            add(Album("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp6))
        }

        // 더미데이터랑 Adapter 연결
        val albumRVAdapter = AlbumRVAdapter(albumDatas)
        // 리사이클러뷰에 어댑터를 연결
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        // 레이아웃 매니저 설정
        binding.homeTodayMusicAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener{

            override fun onItemClick(album: Album) {
                changeAlbumFragment(album)
            }

            override fun onRemoveAlbum(position: Int) {
                albumRVAdapter.removeItem(position)
            }

            override fun onPlayAlbum(album: Album) {
                sendData(album)
            }
        })

        binding.homePannelBtnMemoIv.setOnClickListener {
            val intent = Intent(requireActivity(), MemoActivity::class.java)
            val activity = requireActivity() // fragment에서 SharedPreferences에 접근하려면 context가 필요함.
            val sharedPreferences = activity.getSharedPreferences("memo", AppCompatActivity.MODE_PRIVATE)
            val tempMemo = sharedPreferences.getString("tempMemo", null)

            if(tempMemo != null) {
                val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog, null)
                val builder = AlertDialog.Builder(activity)
                    .setView(dialogView)
                    .setTitle("메모 복원하기")

                val alertDialog = builder.show()
                val yesBtn = alertDialog.findViewById<Button>(R.id.yes)
                val noBtn = alertDialog.findViewById<Button>(R.id.no)

                yesBtn!!.setOnClickListener {
                    startActivity(intent)
                }

                noBtn!!.setOnClickListener {
                    val editor = sharedPreferences.edit()
                    editor.remove("tempMemo")
                    editor.apply()
                    startActivity(intent)
                }

            } else {
                startActivity(intent)
            }
        }

            val bannerAdapter = BannerVPAdapter(this)
            bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
            bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
            binding.homeBannerVp.adapter = bannerAdapter
            binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

            val pannelAdapter = PannelVPAdapter(this)
            pannelAdapter.addFragment(PannelFragment(R.drawable.img_first_album_default))
            pannelAdapter.addFragment(PannelFragment(R.drawable.img_album_exp))
            pannelAdapter.addFragment(PannelFragment(R.drawable.img_album_exp2))
            binding.homePannelBackgroundIv.adapter = pannelAdapter
            binding.homePannelBackgroundIv.orientation = ViewPager2.ORIENTATION_HORIZONTAL

            // Indicator에 viewPager 설정
            binding.indicator.setViewPager(binding.homePannelBackgroundIv)
            //시간 설정
            startAutoSlide(pannelAdapter)
            return binding.root
        }

    private fun changeAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            })
            .commitAllowingStateLoss()
    }

    private fun startAutoSlide(adpater: PannelVPAdapter) {
            // 일정 간격으로 슬라이드 변경 (3초마다)
            timer.scheduleAtFixedRate(3000, 3000) {
                handler.post {
                    val nextItem = binding.homePannelBackgroundIv.currentItem + 1
                    if (nextItem < adpater.itemCount) {
                        binding.homePannelBackgroundIv.currentItem = nextItem
                    } else {
                        binding.homePannelBackgroundIv.currentItem = 0 // 마지막 페이지에서 첫 페이지로 순환
                    }
                }
            }
        }
    }