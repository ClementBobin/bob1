package com.mirage.bob1.domain.model

data class AppSettings(
    val confirmationOffsetJ15: Int = 15, // days before match for 1st reminder
    val confirmationOffsetJ4: Int  = 4,  // days before match for 2nd reminder
)
