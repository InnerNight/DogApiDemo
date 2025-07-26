package com.liye.dogapidemo.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DogImageResponse(
    val message: String,
    val status: String
)

@Serializable
data class BreedsResponse(
    val message: Map<String, List<String>>,
    val status: String
)

data class DogBreed(
    val name: String,
    val subBreeds: List<String> = emptyList()
) {
    fun getDisplayName(): String {
        return name.replaceFirstChar { it.uppercase() }
    }
    
    fun getAllVariants(): List<String> {
        return if (subBreeds.isEmpty()) {
            listOf(name)
        } else {
            subBreeds.map { "$it $name" } + listOf(name)
        }
    }
}

data class QuizQuestion(
    val imageUrl: String,
    val correctBreed: DogBreed,
    val options: List<DogBreed>,
    val correctAnswer: String
)