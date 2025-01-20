package org.nullgroup.lados.data.repositories.implementations

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.nullgroup.lados.data.models.Color
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.data.models.Size
import org.nullgroup.lados.data.remote.models.ColorRemoteModel
import org.nullgroup.lados.data.remote.models.ProductRemoteModel
import org.nullgroup.lados.data.remote.models.ProductVariantRemoteModel
import org.nullgroup.lados.data.remote.models.SizeRemoteModel
import org.nullgroup.lados.data.repositories.implementations.product.ProductRepositoryImplement
import org.nullgroup.lados.utilities.toLocalProduct

@ExperimentalCoroutinesApi
class ProductRepositoryImplementTest {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var productRepositoryImplement: ProductRepositoryImplement

    @BeforeEach
    fun setUp() {
        firestore = mock()
        productRepositoryImplement = ProductRepositoryImplement(firestore)
    }

    @Test
    fun getProductsFlow() = runBlocking {
        val mockProduct = ProductRemoteModel(
            id = "product1",
            categoryId = "category1",
            name = mapOf("en" to "product1"),
            description = mapOf("en" to "Test", "vi" to "Test"),
            variants = listOf(
                ProductVariantRemoteModel(
                    id = "variant1",
                    productId = "product1",
                    size = SizeRemoteModel(
                        id = 1,
                        sizeName = mapOf("en" to "S", "vi" to "P"),
                        sortOrder = true
                    ),
                    color = ColorRemoteModel(
                        id = 1,
                        colorName = mapOf("en" to "Red", "vi" to "Đỏ"),
                        hexCode = "#FF0000"
                    ),
                    quantityInStock = 10,
                    originalPrice = mapOf("en" to 100.0),
                    salePrice = mapOf("en" to 90.0),
                    images = emptyList()
                )
            ),
            createdAt = Timestamp(0, 0),
            engagements = emptyList()
        )

        val product = Product(
            id = "product1",
            categoryId = "category1",
            name = "product1",
            description = "test",
            variants = listOf(
                ProductVariant(
                    id = "variant1",
                    productId = "product1",
                    size = Size(
                        id = 1,
                        sizeName = "S",
                        sortOrder = true
                    ),
                    color = Color(
                        id = 1,
                        colorName = "Red",
                        hexCode = "#FF0000"
                    ),
                    quantityInStock = 10,
                    originalPrice = 100.0,
                    salePrice = 90.0,
                    images = emptyList(
                    )
                )
            ),
            createdAt = Timestamp(0, 0),
            engagements = emptyList()
        )

        assertEquals(product, mockProduct.toLocalProduct())
    }
}