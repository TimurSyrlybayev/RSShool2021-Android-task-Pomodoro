package com.example.pomodoro

import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import android.util.TypedValue
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodoro.databinding.TimerLayoutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyViewHolder(
    private val binding: TimerLayoutBinding,
    private val listener: MainActivity,
): RecyclerView.ViewHolder(binding.root) {

    private var timerBackground: LinearLayout? = null
    var timeField: TextView? = null
    private var deleteTimer: ImageButton? = null
    var startStopButton: Button? = null
    private val value = TypedValue()
    private var current = 0F

    init {
        timerBackground = binding.timerBackground
        timeField = binding.timeField
        deleteTimer = binding.deleteTimer
        startStopButton = binding.startStopButton

    }

    var timer: CountDownTimer? = null

    fun initListeners(timerData: TimerData) {

        when (timerData.isWorking) {
            false -> {
                timer?.cancel()
                timeField!!.text = calculateTime(timerData.time)
                startStopButton!!.text = itemView.context.getString(R.string.start_title_button)
            }
            true -> {
                timeField!!.text = calculateTime(timerData.time)
            }
        }

        deleteTimer!!.setOnClickListener {
            timer?.cancel()
            GlobalScope.launch(Dispatchers.Main) {
                current = 0F
                binding.customViewTwo.setCurrent(current)
            }
            listener.delete(timerData.id)
            setAppearanceOnStart()
        }
        startStopButton!!.setOnClickListener {
            when (timerData.isWorking) {
                false -> {
                    listener.start(timerData.id)
                    start(timerData)
                    startStopButton!!.text = itemView.context.getString(R.string.stop_title_button)
                }
                true -> {
                    listener.stop(timerData.id)
                    stop()
                    startStopButton!!.text = itemView.context.getString(R.string.start_title_button)
                }
            }
        }
    }

    private fun start(timerData: TimerData) {
        timerData.time
        timer?.cancel()
        setAppearanceOnStart()
        timeField!!.text = calculateTime(timerData.time)

        timer = object : CountDownTimer(timerData.time * 1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timerData.time -= 1
                timeField!!.text = calculateTime(timerData.time)
                GlobalScope.launch(Dispatchers.Main) {
                    current = (360F / timerData.initialTimePoint) * (timerData.initialTimePoint - timerData.time)
                    binding.customViewTwo.setCurrent(current)
                }
            }

            override fun onFinish() {
                setAppearanceOnEnd()
                timerData.isWorking = false
                timeField!!.text = calculateTime(timerData.time)
                timerData.time = timerData.initialTimePoint
                binding.indicator.isInvisible = true
                (binding.indicator.background as? AnimationDrawable)?.stop()
            }
        }
        timer?.start()

        binding.indicator.isInvisible = false
        (binding.indicator.background as? AnimationDrawable)?.start()
    }

    fun stop() {
        timer?.cancel()

        binding.indicator.isInvisible = true
        (binding.indicator.background as? AnimationDrawable)?.stop()
    }

    fun calculateTime(time: Long): String {
        val hours = time / 3600
        val minutes = time % 3600 / 60
        val seconds = time % 60
        return "${if(hours > 9) hours else "0$hours"}:" +
                "${if(minutes > 9) minutes else "0$minutes"}:" +
                "${if(seconds > 9) seconds else "0$seconds"}"
    }

    private fun setAppearanceOnEnd() {
        itemView.context.theme.resolveAttribute(R.attr.colorForEndedTimer, value, true)
        timerBackground!!.setBackgroundColor(value.data)
        itemView.context.theme.resolveAttribute(R.attr.colorOnPrimary, value, true)
        startStopButton!!.text = itemView.context.getString(R.string.start_title_button)
        timeField!!.setTextColor(value.data)
    }

    private fun setAppearanceOnStart() {
        startStopButton!!.text = itemView.context.getString(R.string.start_title_button)
        itemView.context.theme.resolveAttribute(R.attr.colorOnPrimary, value, true)
        timerBackground!!.setBackgroundColor(value.data)
        itemView.context.theme.resolveAttribute(R.attr.colorPrimary, value, true)
        timeField!!.setTextColor(value.data)
    }
}