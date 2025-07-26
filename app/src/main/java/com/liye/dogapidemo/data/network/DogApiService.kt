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

    fun close() {
        client.close()
    }
}