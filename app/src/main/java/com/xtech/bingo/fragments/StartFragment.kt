package com.xtech.bingo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xtech.bingo.R
import com.xtech.bingo.databinding.FragmentStartBinding
import kotlin.random.Random

class StartFragment : Fragment(R.layout.fragment_start) {
    private lateinit var binding: FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartBinding.inflate(inflater, container, false)
        val view = binding.root
        val key = getUniqueKey()

        binding.primaryStartButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            if (isValidName(name)) {
                val message = "Ask another player to join with the code"
                addPlayerOneToServer(key, name)
                binding.keyText.text = key
                binding.keyText.visibility = View.VISIBLE
                binding.continueButton.visibility = View.VISIBLE
                binding.primaryStartButton.visibility = View.INVISIBLE
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }

        binding.continueButton.setOnClickListener {
            val dataBundle = bundleOf(Pair("priority",1),Pair("key",key))
            view.findNavController().navigate(R.id.action_startFragment_to_gameFieldFragment,dataBundle)
        }

        return view
    }

    private fun getUniqueKey(): String {
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ01234567891"
        var key = ""
        for (i in 1..6) {
            key += source[Random.nextInt(0, 36)]
        }
        return key
    }

    private fun addPlayerOneToServer(key: String, name: String) {
        val currNode:DatabaseReference = Firebase.database.getReference("users").child(key)
        currNode.child("key").setValue(key)
        currNode.child("player_one").setValue(name)
        currNode.child("player_two").setValue("")
        currNode.child("listenerVariable").setValue(0)
        currNode.child("winListener").setValue(-1)
    }

    private fun isValidName(name: String): Boolean {
        return when {
            name.length < 3 -> {
                val message = "Name should be at least 3 characters"
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
                false
            }
            name.length > 40 -> {
                val message = "Name is too long"
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
                false
            }
            else-> true
        }
    }
}

