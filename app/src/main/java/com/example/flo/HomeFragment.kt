package com.example.flo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentHomeBinding
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private val timer = Timer()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FragmentHomeBinding.inflate(inflater, container, false).also { binding = it }


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
            binding.homeTodayAlbumIv.setOnClickListener {
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, AlbumFragment())
                    .commitAllowingStateLoss()

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