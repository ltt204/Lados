package org.nullgroup.lados.data.models

import com.google.firebase.firestore.DocumentId

data class Address(
    @DocumentId var id: String = "",
    val userId: String,
    var province: String = "",
    var district: String = "",
    var ward: String = "",
    var detail: String = ""
) {
    // No-argument constructor for deserialization
    constructor() : this("", "", "", "", "", "")

    override fun toString(): String {
        return "$detail${if (detail.isEmpty()) "" else ", "}$ward, $district, $province"
    }
}

data class Province(
    val id: String,
    val name: String,
    val name_en: String,
    val full_name: String,
    val full_name_en: String,
    val longitude: String,
    val latitude: String
)

data class District(
    val id: String,
    val name: String,
    val name_en: String,
    val full_name: String,
    val full_name_en: String,
    val longitude: String,
    val latitude: String
)

data class Ward(
    val id: String,
    val name: String,
    val name_en: String,
    val full_name: String,
    val full_name_en: String,
    val longitude: String,
    val latitude: String
)
