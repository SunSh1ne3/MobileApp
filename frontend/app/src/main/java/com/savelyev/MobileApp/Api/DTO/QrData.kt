package com.savelyev.MobileApp.Api.DTO

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class QrData(
    val userId: Int,
    val orderIds: List<Int>
) : Parcelable
