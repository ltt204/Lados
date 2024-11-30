package org.nullgroup.lados.utilities

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.ByteArrayOutputStream

fun Drawable.toByteArray(
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    quality: Int = 100
): ByteArray {
    val bitmap = (this as BitmapDrawable).bitmap
    val stream = ByteArrayOutputStream()
    bitmap.compress(format, quality, stream)

    return stream.toByteArray()
}