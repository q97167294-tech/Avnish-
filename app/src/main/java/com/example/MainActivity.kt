package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.StudyVideoRepository
import com.example.ui.StudyApp
import com.example.ui.StudyViewModel
import com.example.ui.StudyViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Room Database, DAO repo, and state ViewModel
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = StudyVideoRepository(database.studyVideoDao())
        val factory = StudyViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[StudyViewModel::class.java]
        
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StudyApp(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
