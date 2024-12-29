package org.nullgroup.lados.data.repositories.implementations.wishlist

import com.google.firebase.firestore.FirebaseFirestore
import org.nullgroup.lados.data.repositories.interfaces.wishlist.WishlistItemRepository

class WishlistItemRepositoryImplement(
    private val firestore: FirebaseFirestore,
): WishlistItemRepository {
}