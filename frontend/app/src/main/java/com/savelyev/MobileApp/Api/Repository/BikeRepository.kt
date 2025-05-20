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

    fun getTypeBicycleByID(idType: Int?): Call<TypeBicycleDTO> {
        return RetrofitClient.apiService.getTypeBicycle(idType)
    }

    fun getTypeBrakesBicycleByID(idBrake: Int?): Call<TypeBrakesDTO> {
        return RetrofitClient.apiService.getTypeBrakeBicycle(idBrake)
    }

    fun getTypesBicycle(): Call<List<TypeBicycleDTO>> {
        return RetrofitClient.apiService.getTypesBicycle()
    }

    fun getTypesBrakes(): Call<List<TypeBrakesDTO>> {
        return RetrofitClient.apiService.getTypesBrakesBicycle()
    }
}