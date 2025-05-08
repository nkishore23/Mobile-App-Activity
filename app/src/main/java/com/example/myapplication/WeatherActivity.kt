package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName
import kotlin.math.roundToInt
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment

class WeatherActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private val LAST_CITY = "last_city"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = this.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    WeatherScreen()
                }
            }
        }
    }

    fun saveLastCity(city: String) {
        sharedPreferences.edit().putString(LAST_CITY, city).apply()
    }

    fun getLastCity(): String {
        return sharedPreferences.getString(LAST_CITY, null) ?: "London"
    }
}

data class WeatherData(
    val city: String = "",
    val temperature: Double = 0.0,
    val description: String = "",
    val humidity: Int = 0,
    val windSpeed: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@Composable
fun WeatherScreen() {
    val viewModel: WeatherViewModel = viewModel()
    val weatherData by viewModel.weatherData.collectAsState()
    var cityInput by remember { mutableStateOf("") }
    val context = LocalContext.current
    var activity: WeatherActivity = context as WeatherActivity
    var showHumidity by remember { mutableStateOf(true) }
    var showWindSpeed by remember { mutableStateOf(true) }

    LaunchedEffect(true) {
        cityInput = activity.getLastCity()
        viewModel.fetchWeather(cityInput)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back Button and Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { activity.onBackPressed() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Weather App",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search section
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
                if (cityInput.isNotEmpty()) {
                    viewModel.fetchWeather(cityInput)
                    activity.saveLastCity(cityInput)
                }
            }) {
                Text("Search")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filters (Checkboxes)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text("Show details:")
            Spacer(modifier = Modifier.width(16.dp))
            Checkbox(
                checked = showHumidity,
                onCheckedChange = { showHumidity = it }
            )
            Text("Humidity")

            Spacer(modifier = Modifier.width(16.dp))

            Checkbox(
                checked = showWindSpeed,
                onCheckedChange = { showWindSpeed = it }
            )
            Text("Wind Speed")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weather display with animations
        when {
            weatherData.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp)
                )
            }
            weatherData.error != null -> {
                Text(
                    text = weatherData.error!!
                )
            }
            else -> {
                // Animate weather card appearance
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(durationMillis = 500)) + slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(durationMillis = 500)
                    ),
                    exit = fadeOut(tween(durationMillis = 500)) + slideOutVertically(
                        targetOffsetY = { it / 2 },
                        animationSpec = tween(durationMillis = 500)
                    )
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // City name
                            Text(
                                text = weatherData.city,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Temperature
                            Text(
                                text = "${weatherData.temperature.roundToInt()}°C",
                                style = MaterialTheme.typography.titleMedium
                            )

                            // Description
                            Text(
                                text = weatherData.description,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Divider(modifier = Modifier.padding(vertical = 16.dp))

                            // Conditional details row based on filter
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                // Show Humidity if the checkbox is checked
                                AnimatedVisibility(visible = showHumidity) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Humidity"
                                        )
                                        Text("Humidity")
                                        Text(
                                            text = "${weatherData.humidity}%",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Show Wind Speed if the checkbox is checked
                                AnimatedVisibility(visible = showWindSpeed) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Wind"
                                        )
                                        Text("Wind")
                                        Text(
                                            text = "${weatherData.windSpeed} m/s",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

class WeatherViewModel : ViewModel() {
    private val _weatherData = MutableStateFlow(WeatherData(isLoading = true))
    val weatherData: StateFlow<WeatherData> = _weatherData.asStateFlow()

    fun fetchWeather(city: String) {
        _weatherData.value = WeatherData(isLoading = true)

        // Simulate network request (you can replace this with your actual network call)
        viewModelScope.launch {
            try {
                // Simulate a delay
                kotlinx.coroutines.delay(2000)
                // Simulated data
                _weatherData.value = WeatherData(
                    city = city,
                    temperature = 22.0,
                    description = "Clear Sky",
                    humidity = 65,
                    windSpeed = 5.5
                )
            } catch (e: Exception) {
                _weatherData.value = WeatherData(error = "Failed to load weather data")
            }
        }
    }
}
