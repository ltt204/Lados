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
        if (address.id.isEmpty()) addAddressToCollection(address)
        val addressRef = firestore
            .collection("users")
            .document(firebaseAuth.currentUser?.email!!)
            .collection("addresses")
            .document(address.id)
        Log.d("UserAddressRepository", "Address: $address")
        addressRef.set(address).await()
    }

    override suspend fun addAddress(address: Address) {
        val addressRef = firestore
            .collection("users")
            .document(firebaseAuth.currentUser?.email!!)
            .collection("addresses")
            .document(address.id)
    }

    override suspend fun deleteAddress(addressId: String) {
        val addressRef = firestore
            .collection("users")
            .document(firebaseAuth.currentUser?.email!!)
            .collection("addresses")
            .document(addressId)

        addressRef.delete().addOnFailureListener {
            throw Exception("Failed to delete address")
        }.addOnSuccessListener {
            Log.d("AddressRepository", "Address deleted")
        }
    }

    private suspend fun addAddressToCollection(address: Address) {
        val addressCol = firestore.collection("addresses")
        val task = addressCol.add(address)
        task.addOnSuccessListener {
            address.id = it.id
        }.await()
    }
}