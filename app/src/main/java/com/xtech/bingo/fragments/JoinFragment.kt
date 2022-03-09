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
import com.xtech.bingo.databinding.FragmentJoinBinding

class JoinFragment : Fragment(R.layout.fragment_join) {
    private lateinit var binding: FragmentJoinBinding
    private lateinit var rootReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentJoinBinding.inflate(inflater, container, false)
        val view = binding.root
        rootReference = Firebase.database.getReference("users")

        binding.primaryJoinButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val key = binding.keyEditText.text.toString()
            if(isValidName(name)){
                enableProgressBar()
                rootReference.child(key).get().addOnSuccessListener {
                    if(!checkPropertiesOfKey(key)){
                        disableProgressBar()
                        shortToast("Invalid key")
                    }else{
                        rootReference.child(key).get().addOnSuccessListener {
                            if (it.value == null){
                                disableProgressBar()
                                shortToast("Invalid Key")

                            }else{
                                addPlayerTwoToServer(name,key)
                                val dataBundle = bundleOf(Pair("priority",0),Pair("key",key))
                                view.findNavController().navigate(R.id.action_joinFragment_to_gameFieldFragment,dataBundle)
                            }
                        }.addOnFailureListener{
                            shortToast("Data upload failed")
                            disableProgressBar()
                        }
                    }
                }.addOnFailureListener{
                    shortToast("Server Connection Failed")
                    disableProgressBar()
                }
            }
        }
        return view
    }

    private fun enableProgressBar(){
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun disableProgressBar(){
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun isValidName(name: String): Boolean {
        return when {
            name.length < 3 -> {
                val message = "Name should be at least 3 characters"
                shortToast(message)
                false
            }
            name.length > 40 -> {
                val message = "Name is too long"
                shortToast(message)
                false
            }
            else -> true
        }
    }

    private fun shortToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }


    private fun addPlayerTwoToServer(name: String, key: String) {
        rootReference.child(key).child("player_two").setValue(name).addOnFailureListener {
            shortToast("Data Upload Failed")
        }
    }


    private fun checkPropertiesOfKey(key: String): Boolean {
        return when {
            (key.length != 6) -> false
            (key.uppercase() != key) -> false
            else -> true
        }
    }

}
