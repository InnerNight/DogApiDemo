package com.liye.dogapidemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
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
    var imageUrl by remember(breed.name) { mutableStateOf<String?>(null) }
    var imageLoading by remember(breed.name) { mutableStateOf(true) }
    var imageError by remember(breed.name) { mutableStateOf(false) }

    // 获取 breed 的第一张图片
    LaunchedEffect(breed.name) {
        // 只有当图片URL为空时才加载，避免重复加载
        if (imageUrl == null) {
            imageLoading = true
            imageError = false
            try {
                // 构造 API 服务
                val apiService = com.liye.dogapidemo.data.network.DogApiService()

                // 获取该品种的所有图片，使用第一张作为显示图片
                val imagesResponse = if (breed.subBreeds.isNotEmpty() && (0..1).random() == 1) {
                    // 随机选择一个子品种
                    val randomSubBreed = breed.subBreeds.random()
                    // 子品种的API格式是 breed/subbreed
                    apiService.getDogImagesByBreed("${breed.name}/$randomSubBreed")
                } else {
                    // 使用主品种
                    apiService.getDogImagesByBreed(breed.name)
                }

                // 使用第一张图片
                if (imagesResponse.message.isNotEmpty()) {
                    imageUrl = imagesResponse.message.first()
                } else {
                    // 如果没有图片，尝试获取随机图片
                    val imageResponse = if (breed.subBreeds.isNotEmpty() && (0..1).random() == 1) {
                        val randomSubBreed = breed.subBreeds.random()
                        apiService.getRandomDogImageByBreedAndSubBreed(breed.name, randomSubBreed)
                    } else {
                        apiService.getRandomDogImageByBreed(breed.name)
                    }
                    imageUrl = imageResponse.message
                }

                apiService.close()
            } catch (e: Exception) {
                imageError = true
                imageLoading = false
            } finally {
                imageLoading = false
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Breed image
            if (imageUrl != null && !imageError) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "${breed.getDisplayName()} image",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop,
                    onError = {
                        imageError = true
                    }
                )
            } else if (imageLoading) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Image",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Breed name
                Text(
                    text = breed.getDisplayName(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Sub-breeds
                if (breed.subBreeds.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = breed.subBreeds.joinToString(","),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}