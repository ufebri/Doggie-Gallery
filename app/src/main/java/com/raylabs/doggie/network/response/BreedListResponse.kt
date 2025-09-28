package com.raylabs.doggie.network.response

import com.google.gson.annotations.SerializedName

data class BreedListResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("message") val message: Map<String, List<String>>?
)
