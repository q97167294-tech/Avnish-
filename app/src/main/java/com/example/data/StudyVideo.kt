package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_videos")
data class StudyVideo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subject: String,
    val chapter: String,
    val videoUrl: String, // Can be YouTube link, Web URL, or local file URI
    val notes: String = "",
    val isCompleted: Boolean = false,
    val isBookmarked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
