package com.example.pomodoro

data class TimerData(
    val id: Int,
    var time: Long,
    var startingMinutes: String,
    var isWorking: Boolean,
    var initialTimePoint: Long
)
