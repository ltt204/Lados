package org.nullgroup.lados.data.repositories.implementations

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.Address
import org.nullgroup.lados.data.repositories.interfaces.IUserAddressRepository

class UserAddressRepository(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : IUserAddressRepository {
    override fun getAddressesFlow(): Flow<List<Address>> = callbackFlow {
        val addressRef = firestore
            .collection("users")
            .document(firebaseAuth.currentUser?.email!!)
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
                trySend(addresses)
            }
        }

        awaitClose { subscription.remove() }
    }

    override fun getSingleAddressFlow(addressId: String): Flow<Address> = callbackFlow {
        val addressRef = firestore
            .collection("users")
            .document(firebaseAuth.currentUser?.email!!)
            .collection("addresses")

        val subscription = addressRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val addresses = snapshot.documents.mapNotNull {
                    it.toObject(Address::class.java)
                }.filter {
                    it.userId == firebaseAuth.currentUser?.email
                }
                val address = addresses.first { it.id == addressId }
                trySend(address)
            }
        }

        awaitClose { subscription.remove() }
    }

    override suspend fun getSingleAddress(address: Address): Address {
        val addressRef = firestore
            .collection("users")
            .document(firebaseAuth.currentUser?.email!!)
            .collection("addresses")
            .document(address.id)
        val snapshot = addressRef.get().await()
        val address = snapshot.toObject(Address::class.java)
        return address!!
    }

    override suspend fun saveAddress(address: Address) {
        try {
            if (address.id.isEmpty()) addAddressToCollection(address)

            Log.d("UserAddressRepository", "Address: $address")
            val addressRef = firestore
                .collection("users")
                .document(firebaseAuth.currentUser?.email!!)
                .collection("addresses")
                .document(address.id)
            addressRef.set(address).await()
            Log.d("UserAddressRepository", "Address: $address")
        } catch (e: Exception) {
            Log.d("UserAddressRepository", "Failed to save address")
            throw Exception("Failed to save address")
        }
    }

    override suspend fun addAddress(address: Address) {
        val addressRef = firestore
            .collection("users")
            .document(firebaseAuth.currentUser?.email!!)
            .collection("addresses")
            .document(address.id)
    }

    override suspend fun deleteAddress(addressId: String) {
        val addressRef = firestore.collection("addresses").document(addressId)
        val userAddressRef = firestore
            .collection("users")
            .document(firebaseAuth.currentUser?.email!!)
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

    private suspend fun addAddressToCollection(address: Address) {
        val addressCol = firestore.collection("addresses")
        try {
            val task = addressCol.add(address).await()
            address.id = task.id
            Log.d("UserAddressRepository", "$address")
        } catch (e: Exception) {
            Log.d("UserAddressRepository", e.message.toString())
            throw Exception("Failed to add address to collection")
        }
    }
}