package com.savelyev.MobileApp.Api.DTO

data class BikeDTO(
    val id: Int,
    val name: String,
    val weight: Double,
    val frameMaterial: String?,
    val wheelSize: String?,
    val typeBrakes: Int?,
    val typeBicycle: Int?,
    val age: Int?,
    val numberSpeeds: Int?,
    val maximumLoad: Int?,
    val images: List<BicycleImage>
)
