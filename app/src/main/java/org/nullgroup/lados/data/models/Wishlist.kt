package org.nullgroup.lados.data.models
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.util.UUID

data class WishlistItem(
    @DocumentId val id: String = UUID.randomUUID().toString(),
    val productId: String = "",
    val addedAt: Timestamp = Timestamp.now()
) {
    companion object {
        const val COLLECTION_NAME = "wishlistItems"

        fun fromJsonString(jsonString: String): WishlistItem {
            val json = jsonString.replace("{", "").replace("}", "")
            val parts = json.split(",")
            val id = parts[0].split(":")[1].trim().toInt()
            val productId = parts[1].split(":")[1].trim().toInt()
            val addedAt = parts[2].split(":")[1].trim().toLong()
            return WishlistItem(id.toString(), productId.toString(), Timestamp(addedAt, 0))
        }
    }

    fun toJsonString(): String {
        return "{id: $id, productId: $productId, addedAt: $addedAt}"
    }
}


