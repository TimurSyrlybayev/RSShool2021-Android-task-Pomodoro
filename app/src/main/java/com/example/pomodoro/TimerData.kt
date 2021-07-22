package com.example.pomodoro

const val START_TIME: String = "00:00:00:00"

data class TimerData(
    val id: Int,
    var time: Long,
    var startingMinutes: String,
    var isWorking: Boolean,
)
