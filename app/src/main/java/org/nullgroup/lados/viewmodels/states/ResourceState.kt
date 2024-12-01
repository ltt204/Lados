package org.nullgroup.lados.viewmodels.states

sealed class ResourceState<out T> {
    data object Idle : ResourceState<Nothing>()
    data object Loading : ResourceState<Nothing>()
    data class Error(val message: String?) : ResourceState<Nothing>()
    data class Success<out T>(val data: T?) : ResourceState<T>()
}