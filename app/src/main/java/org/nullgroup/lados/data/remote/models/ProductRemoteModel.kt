package org.nullgroup.lados.data.remote.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import org.nullgroup.lados.data.models.Image
import org.nullgroup.lados.data.models.UserEngagement

data class ProductRemoteModel(
    @DocumentId
    var id: String = "",
    val categoryId: String = "",
    val name: Map<String, String> = emptyMap(),
    val description: Map<String, String> = emptyMap(),
    val createdAt: Timestamp = Timestamp.now(),
    var variants: List<ProductVariantRemoteModel> = emptyList(),
    var engagements: List<UserEngagement> = emptyList()
)

data class ProductVariantRemoteModel(
    @DocumentId val id: String = "",
    val productId: String = "",
    val size: SizeRemoteModel = SizeRemoteModel(),
    val color: ColorRemoteModel = ColorRemoteModel(),
    val quantityInStock: Int = 0,
    val originalPrice: Map<String, Double?> = emptyMap(),
    val salePrice: Map<String, Double?>? = emptyMap(),
    var images: List<Image> = emptyList()
)

data class SizeRemoteModel(
    val id: Int = 0,
    val sizeName: Map<String, String> = emptyMap(),
    val sortOrder: Boolean = true
)

data class ColorRemoteModel(
    val id: Int = 0,
    val colorName: Map<String, String> = emptyMap(),
    val hexCode: String = ""
)