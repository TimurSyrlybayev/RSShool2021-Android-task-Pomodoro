package com.example.pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pomodoro.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    companion object {
        val listOfTimers = mutableListOf<TimerData>()

    }
    private val myAdapter = MyAdapter(this)
    private var id = 0
    private var startingMinutes: String = "00:00:00"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.recyclerView.itemAnimator!!.removeDuration = 0L
        fun calculateInsertedTime(insertedMinutes: Long) {
            when(insertedMinutes) {
                in 1..59 -> {
                    val minutes = insertedMinutes
                    startingMinutes =
                        "00:${if(minutes > 9) minutes else "0$minutes"}:00"
                }
                in 60..5999 -> {
                    val hours = insertedMinutes / 60
                    val minutes = insertedMinutes % 60
                    startingMinutes =
                        "${if(hours > 9) hours else "0$hours"}:" +
                                "${if(minutes > 9) minutes else "0$minutes"}:" +
                                "00"
                }
            }
        }

        with(binding!!) {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = myAdapter
            }
            addTimer.setOnClickListener {
                if (
                    insertTimeField.text.toString() != "" &&
                    binding?.insertTimeField?.text.toString().toInt() in 1..5999
                ) {
                    calculateInsertedTime(binding?.insertTimeField?.text.toString().toLong())
                    listOfTimers.add(TimerData(
                        id++,
                        binding?.insertTimeField?.text.toString().toLong() * 60,
                        startingMinutes,
                        false,
                        binding?.insertTimeField?.text.toString().toLong() * 60
                    ))
                    myAdapter.submitList(listOfTimers.toList())
                    startingMinutes = "00:00:00"
                } else {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Please insert number between 1 minute and 5999 minutes",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun delete(id: Int) {
        listOfTimers.remove(listOfTimers.find { it.id == id })
        myAdapter.submitList(listOfTimers.toList())
    }

    fun start(id: Int) {
        listOfTimers.forEach {
            if (it.id == id) {
                it.isWorking = true
            }
        }
        myAdapter.submitList(listOfTimers.toList())
    }

    fun stop(id: Int) {
        listOfTimers.forEach {
            when (it.id) {
                id -> it.isWorking = false
            }
        }
        myAdapter.submitList(listOfTimers.toList())
    }
}