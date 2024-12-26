package org.nullgroup.lados.utilities

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import org.nullgroup.lados.R
import org.nullgroup.lados.data.models.Category
import org.nullgroup.lados.data.models.Color
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.data.models.Size
import org.nullgroup.lados.data.remote.models.CategoryRemoteModel
import org.nullgroup.lados.data.remote.models.ProductRemoteModel
import org.nullgroup.lados.screens.Screen
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

fun Drawable.toByteArray(
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    quality: Int = 25
): ByteArray {
    val bitmap = (this as BitmapDrawable).bitmap

    val stream = ByteArrayOutputStream()
    bitmap.compress(format, quality, stream)

    return stream.toByteArray()
}

fun Uri.toDrawable(context: Context): Drawable? {
    return try {
        // Get input stream from URI using content resolver
        context.contentResolver.openInputStream(this)?.use { inputStream ->
            // Convert stream to bitmap
            val bitmap = BitmapFactory.decodeStream(inputStream)
            // Convert bitmap to drawable
            BitmapDrawable(context.resources, bitmap)
        }
    } catch (e: Exception) {
        null
    }
}

fun Timestamp.toDate(): Date {
    return Date(this.seconds * 1000)
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatToRelativeTime(timestamp: Timestamp): String {
    val parsedTime = Instant.ofEpochSecond(
        timestamp.seconds,
        timestamp.nanoseconds.toLong()
    ) // Chuyển Timestamp sang Instant
    val now = Instant.now()
    val seconds = ChronoUnit.SECONDS.between(parsedTime, now)
    val minutes = ChronoUnit.MINUTES.between(parsedTime, now)
    val hours = ChronoUnit.HOURS.between(parsedTime, now)
    val days = ChronoUnit.DAYS.between(parsedTime, now)

    return when {
        seconds <= 1L -> "Just now"
        seconds < 60 -> "$seconds seconds ago"
        minutes == 1L -> "1 minute ago"
        minutes < 60 -> "$minutes minutes ago"
        hours == 1L -> "1 hour ago"
        hours < 24 -> "$hours hours ago"
        days == 1L -> "Yesterday"
        else -> "$days days ago"
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentUTCFormattedTime(): String {
    // Lấy thời gian hiện tại theo chuẩn UTC
    val utcInstant = Instant.now()

    // Định dạng theo chuẩn ISO 8601 với mili giây và 'Z' để chỉ UTC
    val formatter = DateTimeFormatter.ISO_INSTANT
    return formatter.format(utcInstant)
}

fun OrderStatus.getActionForButtonOfOrderProduct(): Pair<String?, ((NavController, String?, String?) -> Unit)> {
    return when (this) {
        OrderStatus.CREATED, OrderStatus.SHIPPED -> {
            "Detail" to { navController, productId, _ ->
                /*TODO: Do nothing, disable the button or hide it */
                navController.navigate("${Screen.Customer.ProductDetailScreen.route}/$productId")
            }
        }

        OrderStatus.DELIVERED -> {
            "Leave review" to { navController, productId, variantId ->
                /*TODO: Navigate to review screen*/
                navController.navigate("${Screen.Customer.ReviewProductScreen.route}/$productId/$variantId")
            }
        }

        // TODO: Consider designing user flow for these cases
        OrderStatus.CANCELLED, OrderStatus.RETURNED -> {
            "Re-order" to { navController, orderId, _ ->
                /*TODO: Allow user to re-order, which mean they will be navigated to the checkout screen with current list of products of order.*/
//                it.navigate(Screen.Customer.Checkout.route)
            }
        }

        else -> {
            null to { _, _, _ -> /*TODO*/ }
        }
    }
}

fun OrderStatus.getByLocale(context: Context): String {
    return when (this) {
        OrderStatus.CREATED -> context.getString(R.string.order_status_created)
        OrderStatus.CONFIRMED -> context.getString(R.string.order_status_confirmed)
        OrderStatus.SHIPPED -> context.getString(R.string.order_status_shipped)
        OrderStatus.DELIVERED -> context.getString(R.string.order_status_delivered)
        OrderStatus.CANCELLED -> context.getString(R.string.order_status_cancelled)
        OrderStatus.RETURNED -> context.getString(R.string.order_status_returned)
    }
}

fun String.capitalizeWords(): String {
    val words = this.lowercase(Locale.getDefault()).split(" ")
    return words.joinToString(" ") {
        it.replaceFirstChar { ch ->
            if (ch.isLowerCase())
                ch.titlecase(Locale.ROOT)
            else
                ch.toString()
        }
    }
}

fun String.toLocale(): Locale {

    return Locale.forLanguageTag(this)
}

fun getFirstFourOrderStatuses(): List<OrderStatus> {
    return OrderStatus.entries.toTypedArray().take(4)
}

fun Long.toDateTimeString(
    formatPattern: String,
    locale: Locale = Locale.getDefault()
): String {
    val date = Date(this)
    val format = SimpleDateFormat(formatPattern, locale)
    return format.format(date)
}


fun getStatusByName(name: String): OrderStatus {
    return OrderStatus.valueOf(name.uppercase())
}

fun updateLocale(context: Context, locale: Locale) {
    Log.d("updateLocale", "locale: $locale")
    Locale.setDefault(locale)
    val config = Configuration()
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}

fun ProductRemoteModel.toLocalProduct(): Product {
    val locale = Locale.getDefault()
    Log.d("toLocalProduct", "locale language: ${locale.language}")
    val productVariants: MutableList<ProductVariant> = mutableListOf()

    this.variants.forEach { variant ->
        productVariants.add(
            ProductVariant(
                id = variant.id,
                productId = this.id,
                size = Size(
                    id = variant.size.id,
                    sizeName = variant.size.sizeName.getValue(locale.language),
                    sortOrder = variant.size.sortOrder
                ),
                color = Color(
                    id = variant.color.id,
                    colorName = variant.color.colorName.getValue(locale.language),
                    hexCode = variant.color.hexCode
                ),
                quantityInStock = variant.quantityInStock,
                originalPrice = variant.originalPrice[locale.language] ?: 0.0,
                salePrice = variant.salePrice?.get(locale.language),
                images = variant.images
            )
        )
    }

    return Product(
        id = this.id,
        categoryId = this.categoryId,
        name = if (this.name.containsKey(locale.language))
            this.name.getValue(locale.language)
        else
            this.name.getValue(
                SupportedRegion.entries.first().locale.language
            ),
        description = if (this.description.containsKey(locale.language))
            this.description.getValue(locale.language)
        else
            this.description.getValue(
                SupportedRegion.entries.first().locale.language
            ),
        variants = productVariants,
        createdAt = this.createdAt,
        engagements = this.engagements
    )
}

fun CategoryRemoteModel.toLocalCategory(): Category {
    val locale = Locale.getDefault()
    Log.d("toLocalProduct", "locale language: ${locale.language}")

    return Category(
        categoryId = this.categoryId,
        categoryImage = this.categoryImage,
        categoryName = if (this.categoryName.containsKey(locale.language))
            this.categoryName.getValue(locale.language)
        else
            this.categoryName.getValue(
                SupportedRegion.entries.first().locale.language
            ),
        parentCategoryId = this.parentCategoryId
    )
}