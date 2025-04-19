package com.savelyev.MobileApp.Api.DTO.Response

data class BikeDTO(
    val id: Long,
    val name: String,
    val weight: Double,
    val frameMaterial: String,
    val wheelSize: String,
    val typeBrakes: Long,
    val typeBicycle: Long,
    val age: Long,
    val numberSpeeds: Long,
    val maximumLoad: Long
)
