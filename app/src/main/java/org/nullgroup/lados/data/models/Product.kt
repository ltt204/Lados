package org.nullgroup.lados.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Product(
    @DocumentId var id: String = "",
    val categoryId: String = "",
    val name: String = "",
    val description: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    var variants: List<ProductVariant> = emptyList(),
    var engagements: List<UserEngagement> = emptyList()
)

data class ProductVariant(
    @DocumentId val id: String= "",
    val productId: String= "",
    val size: Size = Size(),
    val color: Color = Color(),
    val quantityInStock: Int = 0,
    val originalPrice: Double = 0.0,
    val salePrice: Double? = null,
    var images: List<Image> = emptyList()
)

data class Size(
    val id: Int = 0,
    val sizeName: String = "",
    val sortOrder: Boolean = true
)

data class Color(
    val id: Int = 0,
    val colorName: String = "",
    val hexCode: String = ""
)

data class Image(
    @DocumentId val id: String = "",
    val productVariantId: String = "",
    val link: String = "",
    val fileName: String = ""
)

data class UserEngagement(
    @DocumentId val id: String = "",
    val userId: String = "",
    val productId: String = "",
    val ratings: Int = 1,
    val reviews: String = "",
    val createdAt: Timestamp = Timestamp.now()
)

data class ProductAttribute(
    @DocumentId val id: String = "",
    val type: AttributeType,
    val value: String = "",
    val createdAt: Timestamp = Timestamp.now()
)

enum class AttributeType {
    SIZE, // Tương ứng với Size
    COLOR // Tương ứng với Color
}