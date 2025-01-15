package org.nullgroup.lados.data.repositories.interfaces.common

interface UserPreferencesRepository<T> {
    suspend fun modify(data :T)
    suspend fun get(): T
    suspend fun reset()
}