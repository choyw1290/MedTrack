package com.yeawon.s34598111.medtrack.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.yeawon.s34598111.medtrack.entity.Drug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MedCoachRepository(private val applicationContext: Context) {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.fda.gov/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(OpenFdaApiService::class.java)

    // Check if device has active internet connection
    fun isNetworkAvailable(): Boolean {
        // Get the ConnectivityManager system service
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // Check if the device has an active network
        val network = connectivityManager.activeNetwork ?: return false
        // Get the network capabilities for the active network
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        // Check if the network has any of the following transports:
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    // Fetch drug information based on drug name
    // Returns empty result if no network or error occurs
    suspend fun getDrugInfo(drugName: String): OpenFdaResponse{
        return try {
            if(isNetworkAvailable()) {
                val response = withContext(Dispatchers.IO) {
                    apiService.getDrugInfo(
                        search = "openfda.brand_name:\"$drugName\"",
                        limit = 1
                    )
                }
                response
            } else {
                OpenFdaResponse(results = emptyList())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            OpenFdaResponse(results = emptyList())
        }
    }
}

interface OpenFdaApiService {
    @GET("drug/label.json")
    suspend fun getDrugInfo(
        @Query("search") search: String,
        @Query("limit") limit: Int = 1
    ): OpenFdaResponse
}

data class OpenFdaResponse(
    val results: List<Drug?>
)