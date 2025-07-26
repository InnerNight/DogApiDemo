package com.liye.dogapidemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.liye.dogapidemo.presentation.ui.DogQuizScreen
import com.liye.dogapidemo.presentation.viewmodel.DogQuizViewModel
import com.liye.dogapidemo.presentation.viewmodel.DogQuizViewModelFactory
import com.liye.dogapidemo.ui.theme.DogApiDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DogApiDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel = viewModel<DogQuizViewModel>(
                        factory = DogQuizViewModelFactory(application)
                    )
                    DogQuizScreen(viewModel = viewModel)
                }
            }
        }
    }
}