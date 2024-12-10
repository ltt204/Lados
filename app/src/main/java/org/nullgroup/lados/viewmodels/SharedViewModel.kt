package org.nullgroup.lados.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.nullgroup.lados.data.models.Category
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {
    var sharedData by mutableStateOf<Category?>(null)

    fun updateComplexData(data: Category) {
        sharedData = data
    }
}
