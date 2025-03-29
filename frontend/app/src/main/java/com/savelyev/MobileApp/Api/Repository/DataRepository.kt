package com.savelyev.MobileApp.Api.Repository

import com.savelyev.MobileApp.Api.RetrofitClient
import com.savelyev.MobileApp.Api.DTO.AuthData
import retrofit2.Call

class DataRepository {
    fun getBicycleList(): Call<AuthData> {
        return RetrofitClient.apiService.getUsers()
    }
}