package com.example.flo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivityMemoBinding

class MemoActivity : AppCompatActivity() {

    lateinit var binding : ActivityMemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nextBtn.setOnClickListener {
            val intent = Intent(this, MemoCheckActivity::class.java)
            val memoTxt = binding.memoEt.text.toString()
            intent.putExtra("memo", memoTxt)
            startActivity(intent)
        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
    override fun onPause() {
        super.onPause()
        val sharedPreferences = getSharedPreferences("memo", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val tempMemo = binding.memoEt.text.toString()

        if(tempMemo.isNotEmpty()) {
            editor.putString("tempMemo", tempMemo)
            Log.d("tempMemo", tempMemo)
            editor.apply()
        }
    }

    //재시작되었을 때, SharedPreferences에서 값을 가져오기 위해
    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("memo", MODE_PRIVATE)
        val tempMemo = sharedPreferences.getString("tempMemo", null)

        if(tempMemo != null) {
            binding.memoEt.setText(tempMemo)
        }
    }
}