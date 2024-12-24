package org.nullgroup.lados.data.remote.models

data class CategoryRemoteModel(
    var categoryId: String = "",
    val categoryImage: String = "",
    val categoryName: Map<String, String> = emptyMap(),
    var parentCategoryId: String = "",
)