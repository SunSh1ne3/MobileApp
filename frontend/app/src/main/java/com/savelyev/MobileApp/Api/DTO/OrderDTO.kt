package com.savelyev.MobileApp.Api.DTO

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonFormat
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime
@Parcelize
data class OrderDTO(
    val id: Int = 0,
    val userId: Int,
    val bicycleId: Int,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    val startDate: OffsetDateTime,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    val endDate: OffsetDateTime = startDate,
    var status: String,
    val price: Int,
    val countHours: Int?,
    val countDays: Int?
) : Parcelable
