package com.savelyev.MobileApp.Api.Repository

import com.savelyev.MobileApp.Api.RetrofitClient
import com.savelyev.MobileApp.Api.DTO.Response.BikeDTO
import retrofit2.Call

class BikeRepository {
    fun getBicycleList(): Call<List<BikeDTO>> {
        return RetrofitClient.apiService.getBicycles()
    }

    fun getBicycle(name: String?): Call<BikeDTO> {
        return RetrofitClient.apiService.getBicycle(name)
    }
}