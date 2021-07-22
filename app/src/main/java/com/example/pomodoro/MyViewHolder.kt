package com.example.pomodoro

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodoro.databinding.TimerLayoutBinding

class MyViewHolder(
    private val binding: TimerLayoutBinding,
    private val listener: MainActivity
): RecyclerView.ViewHolder(binding.root) {

    var timeField: TextView? = null

    init {
        timeField = binding.timeField
    }

    fun initListeners(timer: TimerData) {
        binding.deleteTimer.setOnClickListener {
            listener.delete(timer.id)
        }
    }
}