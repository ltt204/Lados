package org.nullgroup.lados.data.repositories.interfaces

interface SharedPreferencesRepository {
    fun saveData(key: String, value: String)
    fun getData(key: String): String?
    fun clearData(key: String)
}