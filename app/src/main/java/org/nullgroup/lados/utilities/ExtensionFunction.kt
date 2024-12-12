package org.nullgroup.lados.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import com.google.firebase.Timestamp
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
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