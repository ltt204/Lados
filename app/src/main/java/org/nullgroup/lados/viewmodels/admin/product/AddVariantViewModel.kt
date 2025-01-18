package org.nullgroup.lados.viewmodels.admin.product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.AddProductVariant
import javax.inject.Inject

@HiltViewModel
class AddVariantViewModel @Inject constructor() : ViewModel() {

    fun addVariant(variant: AddProductVariant) {
        viewModelScope.launch {
            try {
                VariantRepository.addVariant(variant)
            } catch (e: Exception) {
                Log.d("Error", "Error adding variant")
            }
        }
    }
}