// WeatherViewModel.kt
package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class WeatherViewModel : ViewModel() {
    private val _weatherList = MutableStateFlow<List<WeatherData>>(emptyList())
    val weatherList: StateFlow<List<WeatherData>> = _weatherList.asStateFlow()

    private interface OpenMeteoApi {
        @GET("v1/forecast")
        suspend fun getWeather(
            @Query("latitude") lat: Double,
            @Query("longitude") lon: Double,
            @Query("current") current: String = "temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m"
        ): WeatherResponse
    }

    private data class WeatherResponse(
        val latitude: Double,
        val longitude: Double,
        val current: CurrentWeather
    )

    private data class CurrentWeather(
        @SerializedName("temperature_2m") val temperature: Double,
        @SerializedName("relative_humidity_2m") val humidity: Int,
        @SerializedName("weather_code") val weatherCode: Int,
        @SerializedName("wind_speed_10m") val windSpeed: Double
    )

    private fun getWeatherDescription(code: Int): String {
        return when (code) {
            0 -> "Clear sky"
            1, 2, 3 -> "Partly cloudy"
            45, 48 -> "Fog"
            51, 53, 55 -> "Drizzle"
            61, 63, 65 -> "Rain"
            71, 73, 75 -> "Snow"
            77 -> "Snow grains"
            80, 81, 82 -> "Rain showers"
            85, 86 -> "Snow showers"
            95, 96, 99 -> "Thunderstorm"
            else -> "Unknown"
        }
    }

    private fun getCityCoordinates(city: String): Pair<Double, Double> {
        return when (city.lowercase()) {
            "london" -> Pair(51.5074, -0.1278)
            "new york" -> Pair(40.7128, -74.0060)
            "tokyo" -> Pair(35.6762, 139.6503)
            "paris" -> Pair(48.8566, 2.3522)
            "berlin" -> Pair(52.5200, 13.4050)
            "rome" -> Pair(41.9028, 12.4964)
            else -> Pair(0.0, 0.0)
        }
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val weatherApi = retrofit.create(OpenMeteoApi::class.java)

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            val (lat, lon) = getCityCoordinates(city)
            if (lat == 0.0 && lon == 0.0) {
                _weatherList.update {
                    listOf(WeatherData(city = city, isLoading = false, error = "City not found. Try London, New York, Tokyo, Paris, Berlin or Rome.")) + it
                }
                return@launch
            }

            _weatherList.update {
                listOf(WeatherData(city = city, isLoading = true)) + it.filterNot { d -> d.city.equals(city, ignoreCase = true) }
            }

            try {
                val response = weatherApi.getWeather(lat, lon)
                val newWeather = WeatherData(
                    city = city,
                    temperature = response.current.temperature,
                    description = getWeatherDescription(response.current.weatherCode),
                    humidity = response.current.humidity,
                    windSpeed = response.current.windSpeed,
                    isLoading = false,
                    error = null
                )
                _weatherList.update {
                    listOf(newWeather) + it.filterNot { d -> d.city.equals(city, ignoreCase = true) }
                }
            } catch (e: Exception) {
                _weatherList.update {
                    listOf(WeatherData(city = city, isLoading = false, error = "Error: ${e.localizedMessage}")) + it.filterNot { d -> d.city.equals(city, ignoreCase = true) }
                }
            }
        }
    }

    init {
        fetchWeather("London")
    }
}

// WeatherData.kt
data class WeatherData(
    val city: String = "",
    val temperature: Double = 0.0,
    val description: String = "",
    val humidity: Int = 0,
    val windSpeed: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)