package com.savelyev.MobileApp.Api.Repository

import com.savelyev.MobileApp.Api.RetrofitClient
import com.savelyev.MobileApp.Api.DTO.BikeDTO
import com.savelyev.MobileApp.Api.DTO.TypeBicycleDTO
import com.savelyev.MobileApp.Api.DTO.TypeBrakesDTO
import retrofit2.Call

class BikeRepository {
    fun getBicycleList(): Call<List<BikeDTO>> {
        return RetrofitClient.apiService.getBicycles()
    }

    fun getBicycle(name: String?): Call<BikeDTO> {
        return RetrofitClient.apiService.getBicycle(name)
    }

    fun getBicycleByID(id: Int?): Call<BikeDTO> {
        return RetrofitClient.apiService.getBicycle(id)
    }

    fun getTypeBicycleByID(id_type: Int?): Call<TypeBicycleDTO> {
        return RetrofitClient.apiService.getTypeBicycle(id_type)
    }

    fun getTypeBrakesBicycleByID(id_brake: Int?): Call<TypeBrakesDTO> {
        return RetrofitClient.apiService.getTypeBrakesBicycle(id_brake)
    }
}