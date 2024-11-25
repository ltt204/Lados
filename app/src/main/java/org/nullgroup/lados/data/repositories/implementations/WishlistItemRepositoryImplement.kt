package org.nullgroup.lados.data.repositories.implementations

import com.google.firebase.firestore.FirebaseFirestore
import org.nullgroup.lados.data.repositories.interfaces.WishlistItemRepository

class WishlistItemRepositoryImplement(
    private val firestore: FirebaseFirestore,
): WishlistItemRepository {
}