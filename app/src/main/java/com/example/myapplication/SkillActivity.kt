package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class SkillActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var incomingIntent = intent
        var textValue:String = incomingIntent.getStringExtra("textValue") ?: "No value received"
        setContent {
            MyApplicationTheme {
                SkillsScreen(textValue)
            }
        }
    }
}

data class Skill(
    val name: String,
    val proficiency: Float, // From 0.0 to 1.0
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillsScreen(value:String) {
    var context = LocalContext.current
    // Sample data - replace with your actual skills
    val skillsList = remember {
        listOf(
            Skill("Kotlin", 0.9f, "Programming"),
            Skill("Java", 0.85f, "Programming"),
            Skill("Jetpack Compose", 0.8f, "Android"),
            Skill("Android SDK", 0.85f, "Android"),
            Skill("MVVM Architecture", 0.75f, "Architecture"),
            Skill("Room Database", 0.8f, "Android"),
            Skill("Retrofit", 0.85f, "Networking"),
            Skill("Coroutines", 0.8f, "Concurrency"),
            Skill("Git", 0.9f, "Tools"),
            Skill("UI/UX Design", 0.7f, "Design")
        )
    }

    // Group skills by category
    val groupedSkills = skillsList.groupBy { it.category }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Skills") }
            )
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
            // Added additional padding and styling to make the text more visible
            Text(
                text = "Input from main activity: $value",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            IconButton(onClick = {
                val act = context as Activity
                val resultIntent = Intent().apply {
                    putExtra("returnValue", "message from skill Activity")

                }
                //resultIntent.putExtra("returnValue", "result")

                act.setResult(Activity.RESULT_OK, resultIntent)
                act.finish()
            }) {

                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }




            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                groupedSkills.forEach { (category, skills) ->
                    item {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Divider()
                    }

                    items(skills) { skill ->
                        SkillItem(skill = skill)
                    }
                }
            }
        }
    }
}

@Composable
fun SkillItem(skill: Skill) {
    var showDetails by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(targetValue = if (showDetails) skill.proficiency else 0f)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = skill.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "${(skill.proficiency * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = when {
                skill.proficiency >= 0.9f -> Color(0xFF388E3C) // Green
                skill.proficiency >= 0.7f -> Color(0xFF1976D2) // Blue
                skill.proficiency >= 0.5f -> Color(0xFFFBC02D) // Yellow
                else -> Color(0xFFE64A19) // Orange
            },
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }

    // Animation will start when the composable enters the screen
    LaunchedEffect(true) {
        showDetails = true
    }
}

@Preview(showBackground = true)
@Composable
fun SkillsScreenPreview() {
    MaterialTheme {
        SkillsScreen("test")
    }
}