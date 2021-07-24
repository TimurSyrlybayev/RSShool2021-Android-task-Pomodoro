package com.example.pomodoro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pomodoro.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), LifecycleObserver {
    private var binding: ActivityMainBinding? = null
    companion object {
        val listOfTimers = mutableListOf<TimerData>()
    }
    private val myAdapter = MyAdapter(this)
    private var id = 0
    private var startingMinutes: String = "00:00:00"
    private var startTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        startTime = System.currentTimeMillis()

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

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        println("asdasd")
        val startIntent = Intent(this, ForegroundService::class.java)
        startIntent.putExtra("COMMAND_ID", "COMMAND_START")
        startIntent.putExtra("STARTED_TIMER_TIME", startTime)
        startService(startIntent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra("COMMAND_ID", "COMMAND_STOP")
        startService(stopIntent)
    }
}