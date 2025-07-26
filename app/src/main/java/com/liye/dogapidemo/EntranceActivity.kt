package com.liye.dogapidemo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.liye.dogapidemo.ui.theme.DogApiDemoTheme

class EntranceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DogApiDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EntranceScreen(
                        modifier = Modifier.padding(innerPadding),
                        onQuizClicked = {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        },
                        onListClicked = {
                            val intent = Intent(this, DogListActivity::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EntranceScreen(
    modifier: Modifier = Modifier,
    onQuizClicked: () -> Unit,
    onListClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🐶 Dog Breed App",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 48.dp)
        )
        
        Button(
            onClick = onQuizClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Start Quiz Challenge",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        Button(
            onClick = onListClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(
                text = "View All Breeds",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}