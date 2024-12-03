package org.nullgroup.lados.data.models

//data class Product(
//    val id: Int
//)

data class Product(
    var id: String = "",
    val categoryId: String = "",
    val name: String = "",
    val description: String = "",
    var variants: List<ProductVariant> = emptyList(),
    var engagements: List<UserEngagement> = emptyList()
)

data class ProductVariant(
    val id: String= "",
    val productId: String= "",
    val size: Size = Size(),
    val color: Color = Color(),
    val quantityInStock: Int = 0,
    val originalPrice: Double = 0.0,
    val salePrice: Double= 0.0,
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
    val id: String = "", // UUID
    val productVariantId: String = "", // UUID
    val link: String = "",
    val fileName: String = ""
)

data class UserEngagement(
    val id: String = "",
    val userId: String = "",
    val productId: String = "",
    val ratings: Int = 1,
    val reviews: String = "",
    val createdAt: String = ""
)

data class ProductAttribute(
    val id: String = "", // UUID
    val type: AttributeType,
    val value: String = "",
    val createdAt: String = ""// Ngày tạo (ISO-8601 hoặc dạng "yyyy-MM-dd")
)

enum class AttributeType {
    SIZE, // Tương ứng với Size
    COLOR // Tương ứng với Color
}
