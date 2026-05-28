package com.example.data

import kotlinx.coroutines.flow.Flow

class StudyVideoRepository(private val studyVideoDao: StudyVideoDao) {
    val allVideos: Flow<List<StudyVideo>> = studyVideoDao.getAllVideos()

    fun getVideosBySubject(subject: String): Flow<List<StudyVideo>> {
        return studyVideoDao.getVideosBySubject(subject)
    }

    suspend fun getVideoById(id: Int): StudyVideo? {
        return studyVideoDao.getVideoById(id)
    }

    suspend fun insertVideo(video: StudyVideo): Long {
        return studyVideoDao.insertVideo(video)
    }

    suspend fun deleteVideo(video: StudyVideo) {
        studyVideoDao.deleteVideo(video)
    }

    suspend fun deleteVideoById(id: Int) {
        studyVideoDao.deleteVideoById(id)
    }

    suspend fun updateNotes(id: Int, notes: String) {
        studyVideoDao.updateVideoNotes(id, notes)
    }

    suspend fun updateCompleted(id: Int, isCompleted: Boolean) {
        studyVideoDao.updateVideoCompleted(id, isCompleted)
    }

    suspend fun updateBookmarked(id: Int, isBookmarked: Boolean) {
        studyVideoDao.updateVideoBookmarked(id, isBookmarked)
    }
}
