package com.xtech.bingo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.xtech.bingo.R
import com.xtech.bingo.databinding.FragmentResultBinding

class ResultFragment : Fragment(R.layout.fragment_result) {
    private lateinit var binding: FragmentResultBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentResultBinding.inflate(inflater, container, false)
        val view = binding.root
        val priority = this.arguments?.getInt("priority")
        val key = this.arguments?.getString("key").toString()
        val gameResult = this.arguments?.getInt("gameResult")

        if(gameResult == 1){
            binding.wonText.visibility = View.VISIBLE
        }else binding.lostText.visibility = View.VISIBLE

        binding.playAgain.setOnClickListener {
            val dataBundle = bundleOf(Pair("priority", priority), Pair("key", key))
            view.findNavController()
                .navigate(R.id.action_resultFragment_to_gameFieldFragment, dataBundle)
        }

        return view
    }

}