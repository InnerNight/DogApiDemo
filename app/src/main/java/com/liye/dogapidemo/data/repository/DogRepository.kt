package com.liye.dogapidemo.data.repository

import com.liye.dogapidemo.data.model.DogBreed
import com.liye.dogapidemo.data.model.QuizQuestion
import com.liye.dogapidemo.data.network.DogApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class DogRepository {
    private val apiService = DogApiService()
    private var cachedBreeds: List<DogBreed>? = null

    suspend fun getAllBreeds(): List<DogBreed> {
        if (cachedBreeds == null) {
            val response = apiService.getAllBreeds()
            cachedBreeds = response.message.map { (breed, subBreeds) ->
                DogBreed(name = breed, subBreeds = subBreeds)
            }
        }
        return cachedBreeds!!
    }

    suspend fun generateQuizQuestion(): Result<QuizQuestion> {
        return try {
            val allBreeds = getAllBreeds()
            // 随机选择4个不同的犬种
            val randomBreeds = allBreeds.shuffled().take(4)

            // 从这4个犬种中随机选择一个作为正确答案
            val correctBreed = randomBreeds.random()
            
            val imageUrl = getRandomImageForBreed(correctBreed)
            val correctAnswer = correctBreed.getDisplayName()
            
            Result.success(
                QuizQuestion(
                    imageUrl = imageUrl,
                    correctBreed = correctBreed,
                    options = randomBreeds,
                    correctAnswer = correctAnswer
                )
            )
        } catch (e: Exception) {
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

    fun cleanup() {
        apiService.close()
    }
}