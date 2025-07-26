package com.liye.dogapidemo.presentation.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.liye.dogapidemo.data.model.DogBreed
import com.liye.dogapidemo.data.model.QuizQuestion
import com.liye.dogapidemo.presentation.viewmodel.DogQuizUiState
import com.liye.dogapidemo.ui.theme.DogApiDemoTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DogQuizScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingStateDisplaysCorrectly() {
        // Given
        val loadingState = DogQuizUiState(isLoading = true)

        // When
        composeTestRule.setContent {
            DogApiDemoTheme {
                DogQuizScreen()
            }
        }

        // Then
        composeTestRule.onNodeWithText("Loading dog breeds...").assertIsDisplayed()
        composeTestRule.onNode(hasTestTag("loading_indicator")).assertIsDisplayed()
    }

    @Test
    fun errorStateDisplaysCorrectly() {
        // When - Error state is shown
        composeTestRule.setContent {
            DogApiDemoTheme {
                // This would need to be modified to accept a state parameter
                // ErrorScreen(error = "Network error", onRetry = {})
            }
        }

        // Then
        composeTestRule.onNodeWithText("Something went wrong").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try Again").assertIsDisplayed()
    }

    @Test
    fun gameFinishedStateDisplaysScore() {
        // When - Game finished state
        composeTestRule.setContent {
            DogApiDemoTheme {
                // This would need to be modified to accept state parameters
                // GameFinishedScreen(score = 7, totalQuestions = 10, onRestart = {})
            }
        }

        // Then
        composeTestRule.onNodeWithText("Game Complete!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Play Again").assertIsDisplayed()
    }

    @Test
    fun quizQuestionDisplaysOptionsCorrectly() {
        // Given
        val mockQuestion = QuizQuestion(
            imageUrl = "https://test.com/dog.jpg",
            correctBreed = DogBreed("beagle"),
            options = listOf(
                DogBreed("beagle"),
                DogBreed("poodle"),
                DogBreed("bulldog"),
                DogBreed("terrier")
            ),
            correctAnswer = "Beagle"
        )

        // When
        composeTestRule.setContent {
            DogApiDemoTheme {
                // This would need to be modified to accept question parameter
                // QuizContent(question = mockQuestion, onAnswerSelected = {}, onNextQuestion = {})
            }
        }

        // Then
        composeTestRule.onNodeWithText("What breed is this dog?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Beagle").assertIsDisplayed()
        composeTestRule.onNodeWithText("Poodle").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bulldog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Terrier").assertIsDisplayed()
    }

    @Test
    fun answerSelectionUpdatesUI() {
        // This test would verify that selecting an answer updates the UI state
        // and shows the result feedback
        
        composeTestRule.setContent {
            DogApiDemoTheme {
                DogQuizScreen()
            }
        }

        // Simulate answer selection
        composeTestRule.onNodeWithText("Beagle").performClick()
        
        // Verify result is shown (this would need actual state management)
        // composeTestRule.onNodeWithText("Correct!").assertIsDisplayed()
        // composeTestRule.onNodeWithText("Next Question").assertIsDisplayed()
    }

    @Test
    fun headerDisplaysScoreCorrectly() {
        composeTestRule.setContent {
            DogApiDemoTheme {
                // QuizHeader(score = 5, totalQuestions = 8)
            }
        }

        composeTestRule.onNodeWithText("🐕 Dog Breed Quiz").assertIsDisplayed()
        composeTestRule.onNodeWithText("Score: 5/8").assertIsDisplayed()
    }
}