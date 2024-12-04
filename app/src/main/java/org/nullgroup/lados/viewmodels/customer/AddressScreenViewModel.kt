package org.nullgroup.lados.viewmodels.customer

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import org.nullgroup.lados.data.repositories.interfaces.VietnamProvinceService
import javax.inject.Inject

@HiltViewModel
class AddressScreenViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val provinceRepository: VietnamProvinceService
) :ViewModel() {
    private val currentUser = firebaseAuth.currentUser!!.email?.let {
        firestore.collection("users").document(
            it
        )
    }

//    private val addressList = firestore.collection("addresses").document().
}