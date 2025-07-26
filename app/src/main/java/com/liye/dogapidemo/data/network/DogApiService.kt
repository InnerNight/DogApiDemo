package com.liye.dogapidemo.data.network

import com.liye.dogapidemo.data.model.BreedsResponse
import com.liye.dogapidemo.data.model.DogImageResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// 添加新的数据模型用于获取图片列表
@kotlinx.serialization.Serializable
data class DogImagesResponse(
    val message: List<String>,
    val status: String
)

class DogApiService {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    private companion object {
        const val BASE_URL = "https://dog.ceo/api"
    }

    suspend fun getAllBreeds(): BreedsResponse {
        return client.get("$BASE_URL/breeds/list/all").body()
    }

    suspend fun getRandomDogImage(): DogImageResponse {
        return client.get("$BASE_URL/breeds/image/random").body()
    }

    suspend fun getRandomDogImageByBreed(breed: String): DogImageResponse {
        return client.get("$BASE_URL/breed/$breed/images/random").body()
    }

    suspend fun getRandomDogImageByBreedAndSubBreed(breed: String, subBreed: String): DogImageResponse {
        return client.get("$BASE_URL/breed/$breed/$subBreed/images/random").body()
    }

    // 添加获取特定品种所有图片的方法
    suspend fun getDogImagesByBreed(breed: String): DogImagesResponse {
        return client.get("$BASE_URL/breed/$breed/images").body()
    }

    fun close() {
        client.close()
    }
}