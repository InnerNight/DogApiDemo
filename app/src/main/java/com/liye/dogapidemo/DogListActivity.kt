package com.liye.dogapidemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.liye.dogapidemo.data.model.DogBreed
import com.liye.dogapidemo.presentation.viewmodel.DogListViewModel
import com.liye.dogapidemo.presentation.viewmodel.DogListViewModelFactory
import com.liye.dogapidemo.ui.theme.DogApiDemoTheme

class DogListActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DogApiDemoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("All Dog Breeds")
                            }
                        )
                    }
                ) { innerPadding ->
                    val viewModel = viewModel<DogListViewModel>(
                        factory = DogListViewModelFactory(application)
                    )
                    DogListScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun DogListScreen(
    viewModel: DogListViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        uiState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadBreeds() }) {
                        Text("Retry")
                    }
                }
            }
        }
        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.breeds) { breed ->
                    DogBreedItem(breed = breed)
                }
            }
        }
    }
}

@Composable
fun DogBreedItem(
    breed: DogBreed,
    modifier: Modifier = Modifier
) {
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var imageLoading by remember { mutableStateOf(true) }
    var imageError by remember { mutableStateOf(false) }

    // 获取 breed 的随机图片
    LaunchedEffect(breed) {
        imageLoading = true
        imageError = false
        try {
            // 构造 API 服务
            val apiService = com.liye.dogapidemo.data.network.DogApiService()

            // 如果有子品种，随机选择一个子品种或者主品种
            val imageResponse = if (breed.subBreeds.isNotEmpty() && (0..1).random() == 1) {
                // 随机选择一个子品种
                val randomSubBreed = breed.subBreeds.random()
                apiService.getRandomDogImageByBreedAndSubBreed(breed.name, randomSubBreed)
            } else {
                // 使用主品种
                apiService.getRandomDogImageByBreed(breed.name)
            }

            imageUrl = imageResponse.message
            apiService.close()
        } catch (e: Exception) {
            imageError = true
            imageLoading = false
        } finally {
            imageLoading = false
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Breed name
            Text(
                text = breed.getDisplayName(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Breed image
            if (imageUrl != null && !imageError) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "${breed.getDisplayName()} image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    onError = {
                        imageError = true
                    }
                )
            } else if (imageLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Image not available")
                }
            }
            
            // Sub-breeds
            if (breed.subBreeds.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sub-breeds:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                breed.subBreeds.forEach { subBreed ->
                    Text(
                        text = "• ${subBreed.replaceFirstChar { it.uppercase() }} ${breed.getDisplayName()}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}