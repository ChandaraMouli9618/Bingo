package com.xtech.bingo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xtech.bingo.R
import com.xtech.bingo.databinding.FragmentGameFieldBinding
import kotlin.random.Random

class GameFieldFragment : Fragment(R.layout.fragment_game_field) {
    private lateinit var currNode: DatabaseReference
    private lateinit var winNode: DatabaseReference
    private lateinit var binding: FragmentGameFieldBinding
    private lateinit var currView: View
    private var key = "0"
    private var priority = 0
    var initializer = 0
    var winInitializer = 0

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (initializer == 0) {
                initializer = 1
                return
            }
            val update = dataSnapshot.child("listenerVariable").value.toString()
            disableButton(update)
            unlockView()
        }

        override fun onCancelled(databaseError: DatabaseError) {
            shortToast("Unknown Error")
        }
    }

    private val empty = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
        }

        override fun onCancelled(databaseError: DatabaseError) {
            shortToast("Unknown Error")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentGameFieldBinding.inflate(inflater, container, false)
        currView = binding.root
        priority = this.arguments?.getInt("priority") ?: 0
        key = this.arguments?.getString("key").toString()
        currNode = key.let { Firebase.database.getReference("users").child(it) }
        winNode = key.let { Firebase.database.getReference("users").child(it).child("winListener") }

        setText(getRandomArray())
        currNode.addValueEventListener(postListener)

        if (priority == 1) unlockView()
        else lockView()

        val winListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (winInitializer == 0) {
                    winInitializer = 1
                    return
                }
                navigateToResultFragment(0)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                shortToast("Unknown Error")
            }
        }

        winNode.addValueEventListener(winListener)

        return currView
    }


    private fun lockView() {
        binding.mainGrid.children.forEach {
            val textView = currView.findViewById<TextView>(it.id)
            if (textView.tag == "0") {
                textView.setOnClickListener(null)
            }
        }

    }

    private fun navigateToResultFragment(gameResult: Int) {
        val dataBundle =
            bundleOf(Pair("priority", priority), Pair("key", key), Pair("gameResult", gameResult))
        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.main_fragment_container) as NavHostFragment
        val navController = navHostFragment.findNavController()
        navController.navigate(R.id.action_gameFieldFragment_to_resultFragment,dataBundle)

    }

    private fun unlockView() {
        binding.mainGrid.children.forEach {
            val textView = currView.findViewById<TextView>(it.id)
            if (textView.tag == "0") {
                textView.setOnClickListener {

                    changePropOfPressedBtn(textView)
                    val btnVal = textView.text.toString().toInt()
                    initializer = 0
                    updateWinListenerVariable(btnVal)
                    if (checkWin()) {
                        winNode.addValueEventListener(empty)
                        currNode.child("winListener").setValue(priority)
                        navigateToResultFragment(1)
                    }
                    lockView()
                }
            }
        }
    }

    private fun shortToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    private fun getRandomArray(): Array<Int> {
        val arr = Array(25) { i -> i + 1 }
        var temp: Int
        var rand: Int
        for (i in 0..24) {
            temp = arr[i]
            rand = Random.nextInt(24)
            arr[i] = arr[rand]
            arr[rand] = temp
        }
        return arr
    }

    private fun setText(arr: Array<Int>) {
        var count = 0
        binding.mainGrid.children.forEach {
            val textView = currView.findViewById<TextView>(it.id)
            textView.text = arr[count].toString()
            count++
        }
    }

    private fun disableButton(update: String) {
        binding.mainGrid.children.forEach {
            val textView = currView.findViewById<TextView>(it.id)
            if (textView.text.toString() == update) {
                changePropOfPressedBtn(textView)
            }
        }
    }

    private fun updateWinListenerVariable(newData: Int) {
        currNode.child("listenerVariable").setValue(newData).addOnFailureListener {
            shortToast("data upload failed")
        }
    }

    private fun changePropOfPressedBtn(textView: TextView) {
        textView.setBackgroundResource(R.drawable.button_background)
        textView.setTextColor(resources.getColor(R.color.black))
        textView.setOnClickListener(null)
        textView.tag = "1"
    }

    private fun checkWin(): Boolean {

        val completedLines = getCompletedColumns() + getCompletedRows() + getCompletedDiagonals()

        if (completedLines >= 5) {
            return true
        }
        return false
    }

    private fun getCompletedColumns(): Int {
        var completedColumnCount = 0
        for (i in 0..4) {
            var noOfOnes = 0
            for (j in 0..4) {
                val currPos = i + j * 5
                if (binding.mainGrid.getChildAt(currPos).tag.toString() == "1") noOfOnes++
                else break
            }
            if (noOfOnes == 5) completedColumnCount++
        }
        return completedColumnCount
    }

    private fun getCompletedRows(): Int {
        var completedRowCount = 0
        for (i in 0..4) {
            var noOfOnes = 0
            for (j in 0..4) {
                val currPos = i * 5 + j
                if (binding.mainGrid.getChildAt(currPos).tag.toString() == "1") noOfOnes++
                else break
            }
            if (noOfOnes == 5) completedRowCount++
        }
        return completedRowCount
    }

    private fun getCompletedDiagonals(): Int {
        var completedDiagonalCount = 0
        var noOfOnes = 0
        for (i in 0..4) {
            val currPos = i * 6
            if (binding.mainGrid.getChildAt(currPos).tag.toString() == "1") noOfOnes++
            else break
        }
        if (noOfOnes == 5) completedDiagonalCount++
        noOfOnes = 0
        for (i in 1..5) {
            val currPos = i * 4
            if (binding.mainGrid.getChildAt(currPos).tag.toString() == "1") noOfOnes++
            else break
        }
        if (noOfOnes == 5) completedDiagonalCount++
        return completedDiagonalCount
    }

}


