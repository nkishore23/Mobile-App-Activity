package com.example.myapplication

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class WeatherData(
    val city: String = "",
    val temperature: Double = 0.0,
    val description: String = "",
    val humidity: Int = 0,
    val windSpeed: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val forecast: List<DailyForecast> = emptyList()
)

data class DailyForecast(
    val date: String,
    val temperature: Double,
    val description: String
)

class WeatherViewModel : ViewModel() {
    private val _weatherData = MutableStateFlow(WeatherData(isLoading = true))
    val weatherData: StateFlow<WeatherData> = _weatherData

    fun fetchWeather(city: String) {
        // Simulating a network call
        _weatherData.value = WeatherData(
            city = city,
            temperature = 23.0,
            description = "Clear Sky",
            humidity = 65,
            windSpeed = 3.5,
            isLoading = false,
            forecast = listOf(
                DailyForecast("2025-05-09", 22.0, "Clear Sky"),
                DailyForecast("2025-05-10", 24.0, "Partly Cloudy"),
                DailyForecast("2025-05-11", 25.0, "Cloudy"),
                DailyForecast("2025-05-12", 21.0, "Rain"),
                DailyForecast("2025-05-13", 26.0, "Sunny")
            )
        )
    }
}
