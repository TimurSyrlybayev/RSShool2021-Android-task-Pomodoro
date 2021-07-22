package com.example.pomodoro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.pomodoro.databinding.TimerLayoutBinding

class MyAdapter(private val listener: MainActivity) : ListAdapter<TimerData, MyViewHolder>(object : DiffUtil.ItemCallback<TimerData>() {
    override fun areItemsTheSame(oldItem: TimerData, newItem: TimerData) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: TimerData, newItem: TimerData) =
        (oldItem.time == newItem.time && oldItem.isWorking == newItem.isWorking)
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val timerLayoutBinding = TimerLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(timerLayoutBinding, listener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val list = MainActivity.listOfTimers
        if(list.size > 0) {
            holder.initListeners(getItem(position))
            holder.timeField!!.text = list[position].startingMinutes
        }
    }
}