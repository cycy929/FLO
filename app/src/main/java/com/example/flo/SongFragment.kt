package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentSongBinding

class SongFragment: Fragment() {

    lateinit var binding : FragmentSongBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongBinding.inflate(inflater,container,false)

        binding.songMixoffTg.setOnClickListener {
            setMixStatus(true)
        }

        binding.songMixonTg.setOnClickListener {
            setMixStatus(false)
        }

        return binding.root
    }

    fun setMixStatus(isMix : Boolean){
        if(isMix){
            binding.songMixonTg.visibility = View.VISIBLE
            binding.songMixoffTg.visibility = View.GONE
        }
        else{
            binding.songMixonTg.visibility = View.GONE
            binding.songMixoffTg.visibility = View.VISIBLE
        }
    }
}