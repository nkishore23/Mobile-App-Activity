package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.roundToInt

class WeatherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WeatherScreen()
                }
            }
        }
    }
}

@Composable
fun WeatherScreen() {
    val viewModel: WeatherViewModel = viewModel()
    val weatherList by viewModel.weatherList.collectAsState()
    var cityInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = cityInput,
                onValueChange = { cityInput = it },
                label = { Text("Enter city") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (cityInput.isNotBlank()) {
                    viewModel.fetchWeather(cityInput.trim())
                    cityInput = ""
                }
            }) {
                Text("Search")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        for (weather in weatherList) {
            WeatherCard(weather)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun WeatherCard(weatherData: WeatherData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (weatherData.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(50.dp))
                return@Column
            }

            if (weatherData.error != null) {
                Text("Error: ${weatherData.error}", color = MaterialTheme.colorScheme.error)
                return@Column
            }

            Text(weatherData.city, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.WbSunny, contentDescription = null)
                Text("${weatherData.temperature.roundToInt()}°C")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Cloud, contentDescription = null)
                Text("${weatherData.description}")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Opacity, contentDescription = null)
                Text("Humidity: ${weatherData.humidity}%")
            }
        }
    }
}

@Composable
fun MyApplicationTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        typography = Typography(),
        content = content
    )
}

// Dummy data classes

data class WeatherData(
    val city: String = "",
    val temperature: Float = 0f,
    val description: String = "",
    val humidity: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class WeatherViewModel : ViewModel() {
    private val _weatherList = mutableStateListOf<WeatherData>()
    val weatherList: State<List<WeatherData>> = derivedStateOf { _weatherList }

    fun fetchWeather(city: String) {
        _weatherList.add(
            WeatherData(
                city = city,
                temperature = (20..30).random().toFloat(),
                description = "Clear sky",
                humidity = (50..100).random(),
                isLoading = false,
                error = null
            )
        )
    }
}
