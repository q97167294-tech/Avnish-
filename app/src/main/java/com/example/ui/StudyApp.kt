package com.example.ui

import android.content.Intent
import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.StudyVideo

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StudyApp(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val videos by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedSubject by viewModel.selectedSubject.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isBookmarkOnly by viewModel.isBookmarkOnly.collectAsStateWithLifecycle()
    val activeVideo by viewModel.currentPlayingVideo.collectAsStateWithLifecycle()
    val progressStats by viewModel.progressStats.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var videoToEdit by remember { mutableStateOf<StudyVideo?>(null) }

    Scaffold(
        modifier = modifier.testTag("study_app_scaffold"),
        topBar = {
            TopAppBar(
                title = {
                    Column(modifier = Modifier.padding(start = 4.dp)) {
                        Text(
                            text = "GSEB CLASS 10",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.2.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Study Vault",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge.copy(
                                letterSpacing = (-0.5).sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.addSampleVideos() },
                        modifier = Modifier.testTag("action_populate_samples")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Prepopulate sample videos",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "User account",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    videoToEdit = null
                    showAddDialog = true 
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .testTag("add_video_fab")
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add study video")
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .height(80.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Default Active */ },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Library",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    label = {
                        Text(
                            text = "Library",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { /* Offline tab placeholder */ },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Offline"
                        )
                    },
                    label = {
                        Text(
                            text = "Offline",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { /* Routine tab placeholder */ },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Routine"
                        )
                    },
                    label = {
                        Text(
                            text = "Routine",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { /* Setup tab placeholder */ },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Setup"
                        )
                    },
                    label = {
                        Text(
                            text = "Setup",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Study Progress Dashboard Section
            ProgressDashboard(stats = progressStats)

            // Dynamic Subject Selection Filter
            SubjectSelectionSlider(
                subjects = viewModel.subjectsList,
                selectedSubject = selectedSubject,
                videosList = viewModel.allVideosState.collectAsStateWithLifecycle(emptyList()).value,
                onSubjectSelect = { subject ->
                    viewModel.selectedSubject.value = if (selectedSubject == subject) null else subject
                }
            )

            // Search and Bookmark Filtering Rows
            SearchBarAndFilters(
                query = searchQuery,
                onQueryChange = { viewModel.searchQuery.value = it },
                isBookmarkOnly = isBookmarkOnly,
                onBookmarkToggle = { viewModel.isBookmarkOnly.value = !isBookmarkOnly }
            )

            // Main Content Area (Videos list / empty state OR playing panel)
            Box(modifier = Modifier.weight(1f)) {
                if (videos.isEmpty()) {
                    EmptyStudyState(
                        subjectFilter = selectedSubject,
                        hasQuery = searchQuery.isNotEmpty() || isBookmarkOnly,
                        onResetFilters = {
                            viewModel.selectedSubject.value = null
                            viewModel.searchQuery.value = ""
                            viewModel.isBookmarkOnly.value = false
                        },
                        onAddMockData = { viewModel.addSampleVideos() }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("video_list"),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedGuide(8.dp)
                    ) {
                        items(videos) { video ->
                            VideoItemCard(
                                video = video,
                                isPlayingNow = activeVideo?.id == video.id,
                                onPlayClick = {
                                    viewModel.selectVideoToPlay(video)
                                },
                                onCompletedToggle = {
                                    viewModel.toggleCompleted(video)
                                },
                                onBookmarkToggle = {
                                    viewModel.toggleBookmarked(video)
                                },
                                onDelete = {
                                    viewModel.deleteVideo(video)
                                },
                                onEdit = {
                                    videoToEdit = video
                                    showAddDialog = true
                                }
                            )
                        }
                    }
                }

                // Interactive Study Player Panel
                if (activeVideo != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        ActiveStudyPlayerPanel(
                            video = activeVideo!!,
                            onClose = { viewModel.selectVideoToPlay(null) },
                            onSaveNotes = { updatedNotes ->
                                viewModel.updateNotes(activeVideo!!.id, updatedNotes)
                            },
                            onCompletedToggle = { viewModel.toggleCompleted(activeVideo!!) },
                            onBookmarkToggle = { viewModel.toggleBookmarked(activeVideo!!) }
                        )
                    }
                }
            }
        }

        // Add / Edit study video Modal dialog
        if (showAddDialog) {
            AddEditVideoDialog(
                videoToEdit = videoToEdit,
                subjects = viewModel.subjectsList,
                syllabus = viewModel.syllabusChapters,
                onDismiss = { showAddDialog = false },
                onSave = { title, subject, chapter, url, notes ->
                    viewModel.saveVideo(
                        id = videoToEdit?.id ?: 0,
                        title = title,
                        subject = subject,
                        chapter = chapter,
                        videoUrl = url,
                        notes = notes
                    )
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun ProgressDashboard(stats: ProgressSnapshot) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("progress_dashboard"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Overall Learning",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${(stats.percentage * 100).toInt()}%",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // OFFLINE READY Badge
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "OFFLINE READY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Symmetric Geometric Progress Indicator
            LinearProgressIndicator(
                progress = { stats.percentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outlineVariant
            )

            if (stats.totalVideos > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Syllabus Status: Saved ${stats.completedVideos} of ${stats.totalVideos} modules",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            } else {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "No study modules cataloged yet. Tap '+' or auto-generate samples.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun SubjectSelectionSlider(
    subjects: List<SubjectInfo>,
    selectedSubject: String?,
    videosList: List<StudyVideo>,
    onSubjectSelect: (String) -> Unit
) {
    Column {
        Text(
            text = "Subjects / વિષય",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge.copy(
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.primary
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedGuide(10.dp)
        ) {
            items(subjects) { subject ->
                val videoCount = videosList.count { it.subject == subject.name }
                val isSelected = selectedSubject == subject.name
                val displayIcon = mapIconToSymbol(subject.iconName)

                Card(
                    modifier = Modifier
                        .width(136.dp)
                        .height(96.dp)
                        .testTag("subject_card_${subject.name}")
                        .clickable { onSubjectSelect(subject.name) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else subject.color
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else subject.borderColor
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = displayIcon,
                                contentDescription = subject.name,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else subject.textColor,
                                modifier = Modifier.size(24.dp)
                            )

                            Badge(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f) else subject.borderColor,
                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else subject.textColor
                            ) {
                                Text(
                                    text = videoCount.toString(),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = subject.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else subject.textColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = subject.localName.substringBefore(" ("),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else subject.textColor.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBarAndFilters(
    query: String,
    onQueryChange: (String) -> Unit,
    isBookmarkOnly: Boolean,
    onBookmarkToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search title, chapter, or notes...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = if (query.isNotEmpty()) {
                {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            } else null,
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .testTag("study_search_bar"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Bookmarks Quick Filter Toggle
        FilterChip(
            selected = isBookmarkOnly,
            onClick = onBookmarkToggle,
            label = { Text("Bookmarks") },
            leadingIcon = if (isBookmarkOnly) {
                {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Starred videos active",
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                {
                    Icon(
                        imageVector = Icons.Outlined.BookmarkBorder,
                        contentDescription = "Starred videos inactive",
                        modifier = Modifier.size(16.dp)
                    )
                }
            },
            modifier = Modifier
                .height(48.dp)
                .testTag("bookmark_filter_chip")
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoItemCard(
    video: StudyVideo,
    isPlayingNow: Boolean,
    onPlayClick: () -> Unit,
    onCompletedToggle: () -> Unit,
    onBookmarkToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    var expandedActions by remember { mutableStateOf(false) }

    val containerColor = if (isPlayingNow) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface
    val contentColor = if (isPlayingNow) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
    val borderStroke = if (isPlayingNow) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("video_item_${video.id}")
            .clip(RoundedCornerShape(20.dp))
            .combinedClickable(
                onClick = onPlayClick,
                onLongClick = { expandedActions = true }
            ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPlayingNow) 4.dp else 1.dp),
        border = borderStroke
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Checkbox
                Checkbox(
                    checked = video.isCompleted,
                    onCheckedChange = { onCompletedToggle() },
                    modifier = Modifier.testTag("checkbox_${video.id}"),
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = if (isPlayingNow) MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.width(4.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isPlayingNow) {
                            Icon(
                                imageVector = Icons.Default.PlayCircleFilled,
                                contentDescription = "Playing Now Indicator",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 4.dp)
                            )
                        }
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (video.isCompleted) contentColor.copy(alpha = 0.5f) else contentColor
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedGuide(8.dp)
                    ) {
                        SuggestionChip(
                            onClick = {},
                            label = { 
                                Text(
                                    text = video.subject, 
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isPlayingNow) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary
                                ) 
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (isPlayingNow) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            ),
                            border = null,
                            modifier = Modifier.height(24.dp)
                        )

                        Text(
                            text = video.chapter.substringBefore(":"),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isPlayingNow) MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Bookmark Icon
                IconButton(onClick = onBookmarkToggle) {
                    Icon(
                        imageVector = if (video.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark button",
                        tint = if (video.isBookmarked) Color(0xFFFFB300) else if (isPlayingNow) MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Options Menu
                Box {
                    IconButton(onClick = { expandedActions = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert, 
                            contentDescription = "Video options",
                            tint = if (isPlayingNow) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded = expandedActions,
                        onDismissRequest = { expandedActions = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Study & Take Notes") },
                            onClick = {
                                expandedActions = false
                                onPlayClick()
                            },
                            leadingIcon = { Icon(Icons.Default.School, contentDescription = null) }
                        )

                        DropdownMenuItem(
                            text = { Text("Edit Details") },
                            onClick = {
                                expandedActions = false
                                onEdit()
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                        )

                        HorizontalDivider()

                        DropdownMenuItem(
                            text = { Text("Delete Video") },
                            onClick = {
                                expandedActions = false
                                onDelete()
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                        )
                    }
                }
            }

            // Study Notes
            if (video.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = video.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isPlayingNow) MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(start = 40.dp)
                        .background(
                            color = if (isPlayingNow) Color.White.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyStudyState(
    subjectFilter: String?,
    hasQuery: Boolean,
    onResetFilters: () -> Unit,
    onAddMockData: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.MenuBook,
            contentDescription = "Empty board state icon",
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (hasQuery) "No study material found" else "Ready to Excel in Board Exams?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (hasQuery) {
                "Try refining your keyword search or checking other subjects."
            } else {
                "Catalog your GSEB standard Class 10 study videos (online tutorials, YouTube lessons, or local downloaded files) here to keep them offline-arranged by subject and chapter."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedGuide(8.dp)) {
            if (hasQuery || subjectFilter != null) {
                Button(onClick = onResetFilters) {
                    Text("Clear All Filters")
                }
            } else {
                Button(onClick = onAddMockData, modifier = Modifier.testTag("empty_prepopulate_btn")) {
                    Icon(Icons.Default.AddHome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Prepopulate Syllabus Guideline")
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActiveStudyPlayerPanel(
    video: StudyVideo,
    onClose: () -> Unit,
    onSaveNotes: (String) -> Unit,
    onCompletedToggle: () -> Unit,
    onBookmarkToggle: () -> Unit
) {
    var notepadText by remember(video.id) { mutableStateOf(video.notes) }
    var hasUnsavedNotes by remember(video.notes, notepadText) { 
        mutableStateOf(video.notes.trim() != notepadText.trim()) 
    }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .testTag("active_player_panel"),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            // Panel Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${video.subject} • ${video.chapter}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Panel control actions
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onCompletedToggle) {
                        Icon(
                            imageVector = if (video.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = "Toggle Video Completed",
                            tint = if (video.isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onBookmarkToggle) {
                        Icon(
                            imageVector = if (video.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Toggle Video Bookmark",
                            tint = if (video.isBookmarked) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close player")
                    }
                }
            }

            HorizontalDivider()

            // Playback Section & Rich Notepad Split-Screen Layout
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                // The Video view player layout box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    if (video.videoUrl.contains("youtube.com") || video.videoUrl.contains("youtu.be")) {
                        // For YouTube links, prompt user that GSEB lets them trigger YouTube safely
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.PlayCircleOutline,
                                contentDescription = "Online Video Marker",
                                tint = Color.White,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "YouTube Study Video",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.videoUrl))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000))
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Open in YouTube App")
                            }
                        }
                    } else {
                        // Standard built-in player
                        BuiltInVideoPlayer(
                            videoUrl = video.videoUrl,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // The Study Rich Note-taking notepad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "My Revision Notes / ટૂંકી નોંધ",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (hasUnsavedNotes) {
                        Button(
                            onClick = { onSaveNotes(notepadText) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("save_notes_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save", style = MaterialTheme.typography.labelMedium)
                        }
                    } else {
                        Text(
                            text = "Notes Saved",
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Notebook TextField
                OutlinedTextField(
                    value = notepadText,
                    onValueChange = { notepadText = it },
                    placeholder = {
                        Text("Draft your study highlights, formulae, and theorem derivations here offline so they stick to this chapter notes!")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("notepad_text_editor"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun BuiltInVideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var playbackError by remember(videoUrl) { mutableStateOf(false) }

    if (playbackError) {
        Box(
            modifier = modifier.background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = "Playback Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Offline Media Player",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "External player suggested for custom file formats.",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(Uri.parse(videoUrl), "video/*")
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Catch if no handler
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Trigger Android System Viewer")
                }
            }
        }
    } else {
        AndroidView(
            factory = { ctx ->
                VideoView(ctx).apply {
                    val mediaController = MediaController(ctx)
                    mediaController.setAnchorView(this)
                    this.setMediaController(mediaController)
                }
            },
            update = { videoView ->
                try {
                    val uri = Uri.parse(videoUrl)
                    if (uri.scheme != null) {
                        videoView.setVideoURI(uri)
                    } else {
                        videoView.setVideoPath(videoUrl)
                    }
                    videoView.start()
                } catch (e: Exception) {
                    playbackError = true
                }
            },
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVideoDialog(
    videoToEdit: StudyVideo?,
    subjects: List<SubjectInfo>,
    syllabus: Map<String, List<String>>,
    onDismiss: () -> Unit,
    onSave: (title: String, subject: String, chapter: String, url: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf(videoToEdit?.title ?: "") }
    var selectedSubject by remember { mutableStateOf(videoToEdit?.subject ?: subjects.first().name) }
    var selectedChapter by remember { mutableStateOf(videoToEdit?.chapter ?: syllabus[selectedSubject]?.first() ?: "General") }
    var videoUrl by remember { mutableStateOf(videoToEdit?.videoUrl ?: "") }
    var notes by remember { mutableStateOf(videoToEdit?.notes ?: "") }

    var subjectDropdownExpanded by remember { mutableStateOf(false) }
    var chapterDropdownExpanded by remember { mutableStateOf(false) }

    // File picker launcher so users can arrange offline downloaded files dynamically
    val contract = ActivityResultContracts.OpenDocument()
    val localFilePicker = rememberLauncherForActivityResult(
        contract = contract,
        onResult = { uri ->
            uri?.let {
                videoUrl = it.toString()
                if (title.isEmpty()) {
                    title = "Offline Video File"
                }
            }
        }
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("add_video_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
            ) {
                Text(
                    text = if (videoToEdit != null) "Edit Study Info" else "Arrange New Study Video",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Video Title / શીર્ષક") },
                    placeholder = { Text("e.g. Pythagoras Theorem Proof") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_title"),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Subject Select Dropdown
                ExposedDropdownMenuBox(
                    expanded = subjectDropdownExpanded,
                    onExpandedChange = { subjectDropdownExpanded = !subjectDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSubject,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Subject / વિષય") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("input_subject_menu"),
                        shape = RoundedCornerShape(8.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = subjectDropdownExpanded,
                        onDismissRequest = { subjectDropdownExpanded = false }
                    ) {
                        subjects.forEach { subj ->
                            DropdownMenuItem(
                                text = { Text(subj.localName) },
                                onClick = {
                                    selectedSubject = subj.name
                                    selectedChapter = syllabus[subj.name]?.first() ?: "General"
                                    subjectDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Chapter Selection Dropdown
                ExposedDropdownMenuBox(
                    expanded = chapterDropdownExpanded,
                    onExpandedChange = { chapterDropdownExpanded = !chapterDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedChapter,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Chapter / પ્રકરણ") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = chapterDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("input_chapter_menu"),
                        shape = RoundedCornerShape(8.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = chapterDropdownExpanded,
                        onDismissRequest = { chapterDropdownExpanded = false }
                    ) {
                        val chaptersList = syllabus[selectedSubject] ?: listOf("General Guideline")
                        chaptersList.forEach { chap ->
                            DropdownMenuItem(
                                text = { Text(chap, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                onClick = {
                                    selectedChapter = chap
                                    chapterDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Video URL Link with File Selector Trigger
                OutlinedTextField(
                    value = videoUrl,
                    onValueChange = { videoUrl = it },
                    label = { Text("Video URL or Offline File Target") },
                    placeholder = { Text("YouTube link, drive URL, or tap button below") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_url"),
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            try {
                                localFilePicker.launch(arrayOf("video/*"))
                            } catch (e: Exception) {
                                // Fallback
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = "Add local file link",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "💡 Paste any video tutorial link, or attach an offline downloaded GSEB MP4 video from device downloads directly.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Notes Input Initial Value
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Study Notes / ટૂંકી નોંધ (Optional)") },
                    placeholder = { Text("List key formulas, facts, or questions to remember...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("input_notes"),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Dialog Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.trim().isNotEmpty() && videoUrl.trim().isNotEmpty()) {
                                onSave(title, selectedSubject, selectedChapter, videoUrl, notes)
                            }
                        },
                        enabled = title.trim().isNotEmpty() && videoUrl.trim().isNotEmpty(),
                        modifier = Modifier.testTag("dialog_save_btn")
                    ) {
                        Text("Save Video")
                    }
                }
            }
        }
    }
}

// Map logical icons descriptors cleanly to default material structures
private fun mapIconToSymbol(iconName: String): ImageVector {
    return when (iconName) {
        "Calculate" -> Icons.Default.Calculate
        "Science" -> Icons.Default.Science
        "Public" -> Icons.Default.Public
        "Translate", "Language" -> Icons.Default.Translate
        "MenuBook" -> Icons.Default.MenuBook
        "HistoryEdu" -> Icons.Default.HistoryEdu
        else -> Icons.Default.Book
    }
}

// Convenient custom DP arrangement helper
private fun Arrangement.spacedGuide(size: androidx.compose.ui.unit.Dp) = Arrangement.spacedBy(size)
private fun Int.GuideDp() = this.dp
