package com.savelyev.MobileApp.Api.Service

import android.util.Log
import com.savelyev.MobileApp.Api.DTO.BikeDTO
import com.savelyev.MobileApp.Api.DTO.TypeBicycleDTO
import com.savelyev.MobileApp.Api.DTO.TypeBrakesDTO
import com.savelyev.MobileApp.Api.Repository.BikeRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.resume

class BikesService {
    private val bikeRepository = BikeRepository()

    fun getBikeList(callback: (List<BikeDTO>?) -> Unit) {
        val call = bikeRepository.getBicycleList()
        Log.d("BikeAPI", "Starting request to ${call.request().url}")
        Log.d("BikeAPI", "Headers: ${call.request().headers.toMultimap()}")

        call.enqueue(object : Callback<List<BikeDTO>> {
            override fun onResponse(call: Call<List<BikeDTO>>, response: Response<List<BikeDTO>>) {
                Log.d("BikeAPI", "Received response from ${call.request().url}")
                Log.d("BikeAPI", "Response code: ${response.code()}")
                Log.d("BikeAPI", "Response headers: ${response.headers().toMultimap()}")

                when {
                    response.isSuccessful -> {
                        Log.d("BikeAPI", "Request successful, body size: ${response.body()?.size ?: 0}")
                        callback(response.body())
                    }
                    response.code() == 401 -> {
                        Log.e("BikeAPI", "Unauthorized error: ${response.errorBody()?.string()}")
                        // Можно добавить автоматическое обновление токена здесь
                        callback(null)
                    }
                    response.code() == 403 -> {
                        Log.e("BikeAPI", "Forbidden error: ${response.errorBody()?.string()}")
                        Log.e("BikeAPI", "Auth token: ${call.request().header("Authorization")}")
                        callback(null)
                    }
                    else -> {
                        Log.e("BikeAPI", "Error response: ${response.code()} - ${response.errorBody()?.string()}")
                        callback(null)
                    }
                }
            }

            override fun onFailure(call: Call<List<BikeDTO>>, t: Throwable) {
                Log.e("BikeAPI", "Request failed to ${call.request().url}", t)
                when (t) {
                    is IOException -> {
                        Log.e("BikeAPI", "Network error: ${t.message}")
                    }
                    is HttpException -> {
                        Log.e("BikeAPI", "HTTP error: ${t.code()} - ${t.message()}")
                    }
                    else -> {
                        Log.e("BikeAPI", "Unexpected error: ${t.javaClass.simpleName}", t)
                    }
                }
                callback(null)
            }
        })
    }

    fun getBikeData(id: Int?, callback: (BikeDTO?) -> Unit) {
        val call = bikeRepository.getBicycleByID(id)
        call.enqueue(object : Callback<BikeDTO> {
            override fun onResponse(call: Call<BikeDTO>, response: Response<BikeDTO>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null) // TODO: Обработка ошибки
                }
            }

            override fun onFailure(call: Call<BikeDTO>, t: Throwable) {
                Log.e("GetData", "Ошибка: ${t.message}")
                callback(null)
            }
        })
    }

    fun getTypesBicycle(callback: (List<TypeBicycleDTO>?) -> Unit) {
        val call = bikeRepository.getTypesBicycle()
        call.enqueue(object : Callback<List<TypeBicycleDTO>> {
            override fun onResponse(call: Call<List<TypeBicycleDTO>>, response: Response<List<TypeBicycleDTO>>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null) // TODO: Обработка ошибки
                }
            }

            override fun onFailure(call: Call<List<TypeBicycleDTO>>, t: Throwable) {
                Log.e("GetData", "Ошибка: ${t.message}")
                callback(null)
            }
        })
    }

    fun getTypesBrakesBicycle(callback: (List<TypeBrakesDTO>?) -> Unit) {
        val call = bikeRepository.getTypesBrakes()
        call.enqueue(object : Callback<List<TypeBrakesDTO>> {
            override fun onResponse(call: Call<List<TypeBrakesDTO>>, response: Response<List<TypeBrakesDTO>>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null) // TODO: Обработка ошибки
                }
            }

            override fun onFailure(call: Call<List<TypeBrakesDTO>>, t: Throwable) {
                Log.e("GetData", "Ошибка: ${t.message}")
                callback(null)
            }
        })
    }


    private fun getTypeBicycle(idType: Int?, callback: (TypeBicycleDTO?) -> Unit) {
        val call = bikeRepository.getTypeBicycleByID(idType)
        call.enqueue(object : Callback<TypeBicycleDTO> {
            override fun onResponse(call: Call<TypeBicycleDTO>, response: Response<TypeBicycleDTO>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null) // TODO: Обработка ошибки
                }
            }

            override fun onFailure(call: Call<TypeBicycleDTO>, t: Throwable) {
                Log.e("GetData", "Ошибка: ${t.message}")
                callback(null)
            }
        })
    }

    private fun getTypeBrakesBicycle(idBrake: Int?, callback: (TypeBrakesDTO?) -> Unit) {
        val call = bikeRepository.getTypeBrakesBicycleByID(idBrake)
        call.enqueue(object : Callback<TypeBrakesDTO> {
            override fun onResponse(call: Call<TypeBrakesDTO>, response: Response<TypeBrakesDTO>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null) // TODO: Обработка ошибки
                }
            }

            override fun onFailure(call: Call<TypeBrakesDTO>, t: Throwable) {
                Log.e("GetData", "Ошибка: ${t.message}")
                callback(null)
            }
        })
    }

    suspend fun getBikeDTO(id: Int?): BikeDTO? = suspendCancellableCoroutine { continuation ->
        getBikeData(id) { bikeDTO ->
            if (bikeDTO != null) {
                continuation.resume(bikeDTO)
            } else {
                continuation.resume(null)
            }
        }
    }

    suspend fun getTypeBicycleName(idType: Int?): String? = suspendCancellableCoroutine { continuation ->
        getTypeBicycle(idType) { typeBicycle ->
            if (typeBicycle != null) {
                continuation.resume(typeBicycle.name)
            } else {
                continuation.resume(null)
            }
        }
    }

    suspend fun getTypeBrakesName(idTypeBrakes: Int?): String? = suspendCancellableCoroutine { continuation ->
        getTypeBrakesBicycle(idTypeBrakes) { typeBrakesBicycle ->
            if (typeBrakesBicycle != null) {
                continuation.resume(typeBrakesBicycle.name)
            } else {
                continuation.resume(null)
            }
        }
    }
}