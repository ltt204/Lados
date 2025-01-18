package org.nullgroup.lados.data.remote.models

import com.google.firebase.firestore.DocumentId

data class CategoryRemoteModel(
    var categoryId: String = "",
    val categoryImage: String = "",
    val categoryName: Map<String, String> = emptyMap(),
    var parentCategoryId: String = "",
)