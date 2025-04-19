package com.savelyev.MobileApp.Api.Service

import android.util.Log
import com.savelyev.MobileApp.Api.DTO.Response.BikeDTO
import com.savelyev.MobileApp.Api.Repository.BikeRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BikesService {
    private val dataRepository = BikeRepository()

    fun getBikeList(callback: (List<BikeDTO>?) -> Unit) {
        val call = dataRepository.getBicycleList()
        call.enqueue(object : Callback<List<BikeDTO>> {
            override fun onResponse(call: Call<List<BikeDTO>>, response: Response<List<BikeDTO>>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null) // TODO: Обработка ошибки
                }
            }

            override fun onFailure(call: Call<List<BikeDTO>>, t: Throwable) {
                Log.e("GetData", "Ошибка: ${t.message}")
                callback(null)
            }
        })
    }

    fun getBikeData(name: String?, callback: (BikeDTO?) -> Unit) {
        val call = dataRepository.getBicycle(name)
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
}