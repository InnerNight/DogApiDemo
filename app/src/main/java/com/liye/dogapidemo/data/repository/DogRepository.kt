package com.liye.dogapidemo.data.repository

import android.util.Log
import com.liye.dogapidemo.data.model.DogBreed
import com.liye.dogapidemo.data.model.QuizQuestion
import com.liye.dogapidemo.data.network.DogApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DogRepository(private val localCacheRepository: LocalCacheRepository) {
    private val apiService = DogApiService()
    private var cachedBreeds: List<DogBreed>? = null
    private val backgroundScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    companion object {
        private const val TAG = "DogRepository"
    }

    suspend fun getAllBreeds(): List<DogBreed> {
        // 首先检查内存缓存
        if (cachedBreeds != null) {
            Log.d(TAG, "Using memory cache, breeds count: ${cachedBreeds?.size}")
            // 在后台刷新数据
            backgroundScope.launch {
                refreshBreedsFromNetwork()
            }
            return cachedBreeds!!
        }
        
        // 然后检查本地缓存
        val localBreeds = localCacheRepository.getCachedBreeds()
        if (localBreeds != null) {
            Log.d(TAG, "Using local cache, breeds count: ${localBreeds.size}")
            cachedBreeds = localBreeds
            // 在后台刷新数据
            backgroundScope.launch {
                refreshBreedsFromNetwork()
            }
            return localBreeds
        }
        
        // 最后从网络获取
        Log.d(TAG, "Fetching from network")
        return fetchBreedsFromNetwork()
    }

    suspend fun getAllBreedsWithRefresh(): List<DogBreed> {
        return fetchBreedsFromNetwork()
    }

    private suspend fun fetchBreedsFromNetwork(): List<DogBreed> {
        val response = apiService.getAllBreeds()
        val breeds = response.message.map { (breed, subBreeds) ->
            DogBreed(name = breed, subBreeds = subBreeds)
        }
        
        Log.d(TAG, "Fetched from network, breeds count: ${breeds.size}")
        
        // 保存到内存缓存
        cachedBreeds = breeds
        
        // 保存到本地缓存
        localCacheRepository.saveBreeds(breeds)
        
        return breeds
    }

    private suspend fun refreshBreedsFromNetwork(): List<DogBreed> {
        return try {
            fetchBreedsFromNetwork()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to refresh breeds from network", e)
            // 如果刷新失败，保持现有缓存
            cachedBreeds ?: emptyList()
        }
    }

    suspend fun generateQuizQuestion(): Result<QuizQuestion> {
        return try {
            val allBreeds = getAllBreeds()
            Log.d(TAG, "Generating quiz question, available breeds: ${allBreeds.size}")
            
            // 检查是否有足够的犬种
            if (allBreeds.size < 4) {
                Log.w(TAG, "Not enough breeds to generate quiz question, need at least 4, got ${allBreeds.size}")
                return Result.failure(Exception("Not enough dog breeds available to generate quiz"))
            }
            
            // 随机选择4个不同的犬种
            val randomBreeds = allBreeds.shuffled().take(4)
            Log.d(TAG, "Selected ${randomBreeds.size} breeds for quiz")

            // 从这4个犬种中随机选择一个作为正确答案
            val correctBreed = randomBreeds.random()
            
            val imageUrl = getRandomImageForBreed(correctBreed)
            val correctAnswer = correctBreed.getDisplayName()
            
            val question = QuizQuestion(
                imageUrl = imageUrl,
                correctBreed = correctBreed,
                options = randomBreeds,
                correctAnswer = correctAnswer
            )
            
            Log.d(TAG, "Generated quiz question with ${question.options.size} options")
            
            Result.success(question)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate quiz question", e)
            Result.failure(e)
        }
    }

    private suspend fun getRandomImageForBreed(breed: DogBreed): String {
        return if (breed.subBreeds.isEmpty()) {
            apiService.getRandomDogImageByBreed(breed.name).message
        } else {
            // For breeds with sub-breeds, randomly choose between main breed or sub-breed
            val useSubBreed = breed.subBreeds.isNotEmpty() && (0..1).random() == 1
            if (useSubBreed) {
                val randomSubBreed = breed.subBreeds.random()
                apiService.getRandomDogImageByBreedAndSubBreed(breed.name, randomSubBreed).message
            } else {
                apiService.getRandomDogImageByBreed(breed.name).message
            }
        }
    }

    suspend fun preloadBreeds(): Result<Unit> {
        return try {
            getAllBreeds()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun preloadBreedsWithRefresh(): Result<Unit> {
        return try {
            getAllBreedsWithRefresh()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun cleanup() {
        apiService.close()
    }
}