package org.nullgroup.lados.data.models

data class Category(
    var categoryId: String = "",
    val categoryImage: String = "",
    val categoryName: String="",
    var parentCategoryId: String = "",
)