package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyVideoDao {
    @Query("SELECT * FROM study_videos ORDER BY createdAt DESC")
    fun getAllVideos(): Flow<List<StudyVideo>>

    @Query("SELECT * FROM study_videos WHERE subject = :subject ORDER BY createdAt DESC")
    fun getVideosBySubject(subject: String): Flow<List<StudyVideo>>

    @Query("SELECT * FROM study_videos WHERE id = :id LIMIT 1")
    suspend fun getVideoById(id: Int): StudyVideo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: StudyVideo): Long

    @Delete
    suspend fun deleteVideo(video: StudyVideo)

    @Query("DELETE FROM study_videos WHERE id = :id")
    suspend fun deleteVideoById(id: Int)

    @Query("UPDATE study_videos SET notes = :notes WHERE id = :id")
    suspend fun updateVideoNotes(id: Int, notes: String)

    @Query("UPDATE study_videos SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateVideoCompleted(id: Int, isCompleted: Boolean)

    @Query("UPDATE study_videos SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateVideoBookmarked(id: Int, isBookmarked: Boolean)
}
