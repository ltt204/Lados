package org.nullgroup.lados.data.repositories.implementations

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.Address
import org.nullgroup.lados.data.repositories.interfaces.IUserAddressRepository

class UserAddressRepositoryImplement(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : IUserAddressRepository {
    override fun getAddressesFlow(): Flow<List<Address>> = callbackFlow {
        val addressRef = firestore
            .collection("users")
            .document(firebaseAuth.currentUser?.uid!!)
            .collection("addresses")

        val subscription = addressRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val addresses = snapshot.documents.mapNotNull {
                    it.toObject(Address::class.java)
                }
                trySend(addresses).isSuccess
            }
        }

        awaitClose { subscription.remove() }
    }

    override fun getSingleAddressFlow(addressId: String): Flow<Address> = callbackFlow {
        val addressRef = firestore
            .collection("users")
            .document(firebaseAuth.currentUser?.uid!!)
            .collection("addresses")
            .document(addressId)

        val subscription = addressRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val address = snapshot.toObject(Address::class.java)
                trySend(address!!).isSuccess
            }
        }

        awaitClose { subscription.remove() }
    }

    override suspend fun getSingleAddress(addressId: String): Address {
        val addressRef = firestore
            .collection("users")
            .document(firebaseAuth.currentUser?.uid!!)
            .collection("addresses")
            .document(addressId)

        val snapshot = addressRef.get().await()
        val address = snapshot.toObject(Address::class.java)
        return address!!
    }

    override suspend fun saveAddress(address: Address) {
        try {
            val addedAddress = address.copy().apply {
                if (id.isEmpty()) {
                    id = addAddressToCollection(this).await().id
                }
            }

            val addressRef = firestore
                .collection("users")
                .document(firebaseAuth.currentUser?.uid!!)
                .collection("addresses")
                .document(addedAddress.id)
            Log.d("UserAddressRepository", "Address: $addressRef")
            addressRef.set(addedAddress).await()
            Log.d("UserAddressRepository", "Address: $addedAddress")

        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    override suspend fun addAddress(address: Address) {
        val addressRef = firestore
            .collection("users")
            .document(firebaseAuth.currentUser?.uid!!)
            .collection("addresses")
            .document(address.id)
    }

    override suspend fun deleteAddress(addressId: String) {
        val addressRef = firestore.collection("addresses").document(addressId)
        val userAddressRef = firestore
            .collection("users")
            .document(firebaseAuth.currentUser?.uid!!)
            .collection("addresses")
            .document(addressId)

        try {
            userAddressRef.delete().await()
            addressRef.delete().await()
            Log.d("AddressRepository", "Address deleted")
        } catch (e: Exception) {
            throw Exception("Failed to delete address")
        }
    }

    private suspend fun addAddressToCollection(address: Address): Task<DocumentReference> {
        val addressCol = firestore.collection("addresses")

        val existingAddress = addressCol
            .whereEqualTo("userId", address.userId)
            .whereEqualTo("province", address.province)
            .whereEqualTo("district", address.district)
            .whereEqualTo("ward", address.ward)
            .whereEqualTo("detail", address.detail)
            .get()
            .await()

        Log.d("UserAddressRepository", "Existing Address: $existingAddress")
        if (!existingAddress.isEmpty) {
            throw Exception("Address already exists")
        }

        return addressCol.add(address)
    }
}