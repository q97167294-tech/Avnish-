package com.example.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.StudyVideo
import com.example.data.StudyVideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SubjectInfo(
    val name: String,
    val localName: String,
    val color: Color,          // Background Color
    val borderColor: Color,    // Matching Border Color
    val textColor: Color,      // Matching Accent Text/Icon Color
    val iconName: String       // Logical descriptor mapping to Material Icons
)

class StudyViewModel(private val repository: StudyVideoRepository) : ViewModel() {

    // Standard GSEB Class 10 Subjects with highly cohesive distinct light colors
    val subjectsList = listOf(
        SubjectInfo("Mathematics", "ગણિત (Maths)", Color(0xFFEEF2FF), Color(0xFFC7D2FE), Color(0xFF4F46E5), "Calculate"),
        SubjectInfo("Science", "વિજ્ઞાન (Science)", Color(0xFFECFDF5), Color(0xFFA7F3D0), Color(0xFF059669), "Science"),
        SubjectInfo("Social Science", "સામાજિક વિજ્ઞાન (Soc Sci)", Color(0xFFFFF7ED), Color(0xFFFFD8A8), Color(0xFFEA580C), "Public"),
        SubjectInfo("English", "અંગ્રેજી (English)", Color(0xFFEFF6FF), Color(0xFFBFDBFE), Color(0xFF2563EB), "Language"),
        SubjectInfo("Gujarati", "ગુજરાતી (Gujarati)", Color(0xFFFAF5FF), Color(0xFFE9D5FF), Color(0xFF9333EA), "MenuBook"),
        SubjectInfo("Sanskrit", "સંસ્કૃત (Sanskrit)", Color(0xFFFFF1F2), Color(0xFFFECDD3), Color(0xFFE11D48), "HistoryEdu")
    )

    // GSEB Syllabus Reference Chapters
    val syllabusChapters = mapOf(
        "Mathematics" to listOf(
            "Chapter 1: Real Numbers (વાસ્તવિક સંખ્યાઓ)",
            "Chapter 2: Polynomials (બહુપદીઓ)",
            "Chapter 3: Pair of Linear Equations (દ્વિચલ સુરેખ સમીકરણયુગ્મ)",
            "Chapter 4: Quadratic Equations (દ્વિઘાત સમીકરણ)",
            "Chapter 5: Arithmetic Progressions (સમાંતર શ્રેણી)",
            "Chapter 6: Triangles (ત્રિકોણ)",
            "Chapter 7: Coordinate Geometry (યામ ભૂમિતિ)",
            "Chapter 8: Introduction to Trigonometry (ત્રિકોણમિતિનો પરિચય)",
            "Chapter 9: Some Applications of Trigonometry (ત્રિકોણમિતિના ઉપયોગો)",
            "Chapter 10: Circles (વર્તુળ)",
            "Chapter 11: Areas Related to Circles (વર્તુળ સંબંધિત ક્ષેત્રફળ)",
            "Chapter 12: Surface Areas and Volumes (પૃષ્ઠફળ અને ઘનફળ)",
            "Chapter 13: Statistics (આંકડાશાસ્ત્ર)",
            "Chapter 14: Probability (સંભાવના)"
        ),
        "Science" to listOf(
            "Chapter 1: Chemical Reactions (રાસાયણિક પ્રક્રિયાઓ)",
            "Chapter 2: Acids, Bases and Salts (એસિડ, બેઇઝ અને ક્ષાર)",
            "Chapter 3: Metals and Non-metals (ધાતુઓ અને અધાતુઓ)",
            "Chapter 4: Carbon and its Compounds (કાર્બન અને તેના સંયોજનો)",
            "Chapter 5: Life Processes (જૈવિક ક્રિયાઓ)",
            "Chapter 6: Control and Coordination (નિયંત્રણ અને સંકલન)",
            "Chapter 7: How do Organisms Reproduce? (સજીવો કેવી રીતે પ્રજનન કરે છે?)",
            "Chapter 8: Heredity and Evolution (આનુવંશિકતા અને ઉદ્વિકાસ)",
            "Chapter 9: Light - Reflection and Refraction (પ્રકાશ-પરાવર્તન અને વક્રીભવન)",
            "Chapter 10: Human Eye & Colorful World (માનવ આંખ અને રંગબેરંગી દુનિયા)",
            "Chapter 11: Electricity (વિદ્યુત)",
            "Chapter 12: Magnetic Effects (વિદ્યુત પ્રવાહની ચુંબકીય અસરો)",
            "Chapter 13: Our Environment (આપણું પર્યાવરણ)"
        ),
        "Social Science" to listOf(
            "Chapter 1: Heritage of India (ભારતનો વારસો)",
            "Chapter 2: Cultural Heritage (ભારતનો સાંસ્કૃતિક વારસો)",
            "Chapter 3: Sculptures and Architecture (શિલ્પ અને સ્થાપત્ય)",
            "Chapter 4: Literary Heritage (ભારતનો સાહિત્યિક વારસો)",
            "Chapter 5: Science and Technology (વિજ્ઞાન અને ટેકનોલોજી)",
            "Chapter 6: Places of Cultural Heritage (સાંસ્કૃતિક વારસાના સ્થળો)",
            "Chapter 7: Preservation of Our Heritage (આપણા વારસાનું જતન)",
            "Chapter 8: Natural Resources (કુદરતી સંસાધનો)",
            "Chapter 9: Forests & Wildlife (વન અને વન્યજીવ સંસાધનો)",
            "Chapter 10: Agriculture (ભારત: કૃષિ)",
            "Chapter 11: Water Resources (જળ સંસાધનો)",
            "Chapter 12: Mineral and Energy (ખનિજ અને શક્તિ સંસાધનો)",
            "Chapter 13: Manufacturing Industries (ઉત્પાદન ઉદ્યોગો)",
            "Chapter 14: Transport, Communication & Trade (પરિવહન અને વ્યાપાર)",
            "Chapter 15: Economic Development (આર્થિક વિકાસ)",
            "Chapter 16: Liberalization & Globalization (ઉદારીકરણ)",
            "Chapter 17: Economic Problems & Challenges (ગરીબી અને બેરોજગારી)",
            "Chapter 18: Price Rise & Consumer Awareness (ભાવવધારો અને ગ્રાહક જાગૃતિ)",
            "Chapter 19: Human Development (માનવ વિકાસ)",
            "Chapter 20: Social Problems (સામાજિક સમસ્યાઓ અને પડકારો)",
            "Chapter 21: Social Change (સામાજિક પરિવર્તન)"
        ),
        "English" to listOf(
            "Unit 1: A Letter to God",
            "Unit 2: Nelson Mandela: Long Walk to Freedom",
            "Unit 3: Two Stories about Flying",
            "Unit 4: From the Diary of Anne Frank",
            "Unit 5: Glimpses of India",
            "Unit 6: Madam Rides the Bus",
            "Unit 7: The Sermon at Benares",
            "Grammar: Tenses, Direct/Indirect",
            "Grammar: Active/Passive Voice",
            "Writing Skill: Essays & Reports"
        ),
        "Gujarati" to listOf(
            "કાવ્ય ૧: મોરલી (Morali)",
            "પાઠ ૨: શરણાઈના સૂર (Sharnaina Soor)",
            "કાવ્ય ૩: પ્રયાણ (Prayan)",
            "પાઠ ૪: ભીખુ (Bhikhu)",
            "કાવ્ય ૫: દીકરી (Dikari)",
            "પાઠ ૬: વાયરલ ઇન્ફેક્શન (Viral Infection)",
            "વ્યાકરણ: સંધિ અને સમાસ (Grammar)",
            "વ્યાકરણ: જોડણી અને પ્રત્યય"
        ),
        "Sanskrit" to listOf(
            "Chapter 1: સમવદધ્વમ્ (Samgachadhvam)",
            "Chapter 2: યદ્ભવિષ્યો વિનશ્યતિ (Yadbh भविष्यो विनश्यति)",
            "Chapter 3: સ્વસ્થવૃત્તં સમાચર (Swasthavrittam Samachara)",
            "Chapter 4: જનાર્દનસ્ય પશ્ચિમઃ સન્દેશઃ (Janardanasya Sandeshah)",
            "Grammar: Vibhakti & Karak (વિભક્તિ અને કારક)"
        )
    )

    // User's active filter and sorting states
    val selectedSubject = MutableStateFlow<String?>(null)
    val searchQuery = MutableStateFlow("")
    val isBookmarkOnly = MutableStateFlow(false)

    // Active currently playing video
    val currentPlayingVideo = MutableStateFlow<StudyVideo?>(null)

    // Source of all videos raw
    val allVideosState = repository.allVideos

    // Flow that filters and processes the items
    val uiState: StateFlow<List<StudyVideo>> = combine(
        allVideosState,
        selectedSubject,
        searchQuery,
        isBookmarkOnly
    ) { allVideos, subject, query, bookmarkOnly ->
        allVideos.filter { video ->
            val matchesSubject = subject == null || video.subject == subject
            val matchesQuery = query.isEmpty() || 
                    video.title.contains(query, ignoreCase = true) || 
                    video.chapter.contains(query, ignoreCase = true) || 
                    video.notes.contains(query, ignoreCase = true)
            val matchesBookmark = !bookmarkOnly || video.isBookmarked
            matchesSubject && matchesQuery && matchesBookmark
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Calculate dynamic stats
    val mathVideosCount = combine(allVideosState) { list -> list.first().filter { it.subject == "Mathematics" }.size }
    val progressStats: StateFlow<ProgressSnapshot> = combine(allVideosState) { list ->
        val videos = list.firstOrNull() ?: emptyList()
        val total = videos.size
        val completed = videos.count { it.isCompleted }
        val percentage = if (total > 0) (completed.toFloat() / total.toFloat()) else 0.0f
        ProgressSnapshot(total, completed, percentage)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProgressSnapshot(0, 0, 0.0f)
    )

    // Data Actions
    fun saveVideo(
        id: Int = 0,
        title: String,
        subject: String,
        chapter: String,
        videoUrl: String,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val video = StudyVideo(
                id = id,
                title = title.trim(),
                subject = subject,
                chapter = chapter,
                videoUrl = videoUrl.trim(),
                notes = notes.trim()
            )
            repository.insertVideo(video)
        }
    }

    fun deleteVideo(video: StudyVideo) {
        viewModelScope.launch {
            repository.deleteVideo(video)
            if (currentPlayingVideo.value?.id == video.id) {
                currentPlayingVideo.value = null
            }
        }
    }

    fun toggleCompleted(video: StudyVideo) {
        viewModelScope.launch {
            repository.updateCompleted(video.id, !video.isCompleted)
        }
    }

    fun toggleBookmarked(video: StudyVideo) {
        viewModelScope.launch {
            repository.updateBookmarked(video.id, !video.isBookmarked)
            // Update current playing video bookmark status if applicable
            if (currentPlayingVideo.value?.id == video.id) {
                currentPlayingVideo.value = currentPlayingVideo.value?.copy(isBookmarked = !video.isBookmarked)
            }
        }
    }

    fun updateNotes(id: Int, notes: String) {
        viewModelScope.launch {
            repository.updateNotes(id, notes)
            if (currentPlayingVideo.value?.id == id) {
                currentPlayingVideo.value = currentPlayingVideo.value?.copy(notes = notes)
            }
        }
    }

    fun selectVideoToPlay(video: StudyVideo?) {
        currentPlayingVideo.value = video
    }

    // Prepopulate high-quality sample Study Guidelines/Video Tips for Class 10 Board if empty
    fun addSampleVideos() {
        viewModelScope.launch {
            val sampleVideos = listOf(
                StudyVideo(
                    title = "Class 10 Board Prep Strategy",
                    subject = "Mathematics",
                    chapter = "Chapter 1: Real Numbers (વાસ્તવિક સંખ્યાઓ)",
                    videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ", // Template Placeholder
                    notes = "Tips for GSEB Science and Maths Class 10 Board:\n- Do textbook exercise first!\n- Focus on theorems in Triangles (Pytagoras and Thales) which carry 4-6 marks.\n- Maintain a separate notebook for quick formulas.",
                    isBookmarked = true
                ),
                StudyVideo(
                    title = "GSEB Vigyan Chapter 1 Full Overview",
                    subject = "Science",
                    chapter = "Chapter 1: Chemical Reactions (રાસાયણિક પ્રક્રિયાઓ)",
                    videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                    notes = "Chemical Reactions Chapter Guideline:\n- Practice balancing chemical equations securely.\n- Remember colors: Copper sulfate is blue, Copper carbonate is greenish, Lead iodide precipitate is yellow.\n- Write reactions multiple times offline.",
                    isBookmarked = false
                ),
                StudyVideo(
                    title = "Sanskrit Board Exam Presentation Tips",
                    subject = "Sanskrit",
                    chapter = "Chapter 1: સમવદધ્વમ્ (Samgachadhvam)",
                    videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                    notes = "How to score full marks in GSEB Sanskrit:\n- Hand-writing should be clean.\n- Focus on translation paragraphs of standard classical units directly from GSEB textbook.\n- Memorize Shlokas for completion.",
                    isCompleted = true,
                    isBookmarked = true
                )
            )
            for (v in sampleVideos) {
                repository.insertVideo(v)
            }
        }
    }
}

data class ProgressSnapshot(
    val totalVideos: Int,
    val completedVideos: Int,
    val percentage: Float
)

class StudyViewModelFactory(private val repository: StudyVideoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
