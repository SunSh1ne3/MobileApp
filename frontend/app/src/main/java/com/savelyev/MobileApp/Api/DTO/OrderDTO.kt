package com.savelyev.MobileApp.Api.DTO

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.OffsetDateTime

data class OrderDTO(
    val id: Int = 0,
    val userId: Int,
    val bicycleId: Int,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    val startDate: OffsetDateTime,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    val endDate: OffsetDateTime = startDate,
    var status: Int = 1,
    val price: Int,
    val countHours: Int?,
    val countDays: Int?
)
