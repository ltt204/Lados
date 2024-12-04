package org.nullgroup.lados.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import java.io.ByteArrayOutputStream

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