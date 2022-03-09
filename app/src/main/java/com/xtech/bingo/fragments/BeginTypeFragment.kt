package com.xtech.bingo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.xtech.bingo.R
import com.xtech.bingo.databinding.FragmentBeginTypeBinding

class BeginTypeFragment: Fragment() {
    private lateinit var binding: FragmentBeginTypeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentBeginTypeBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.startButton.setOnClickListener{
            view.findNavController().navigate(R.id.action_beginTypeFragment_to_startFragment)
        }

        binding.joinButton.setOnClickListener{
            view.findNavController().navigate(R.id.action_beginTypeFragment_to_joinFragment)
        }

        return view
    }
}