package org.nullgroup.lados.data.repositories.implementations.category

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.Category
import org.nullgroup.lados.data.remote.models.CategoryRemoteModel
import org.nullgroup.lados.data.repositories.interfaces.category.CategoryRepository
import org.nullgroup.lados.utilities.toLocalCategory

class CategoryRepositoryImplement(
    private val firestore: FirebaseFirestore
) : CategoryRepository {
    override suspend fun getAllCategoriesFromFireStore(): Result<List<Category>> {
        return try {
            val snapshots = firestore.collection("categories").get().await()
            val categoryList = if (snapshots.isEmpty) {
                emptyList()
            } else {
                snapshots.documents.mapNotNull { it.toObject(CategoryRemoteModel::class.java) }
                    .map { it.toLocalCategory() }
            }
            Result.success(categoryList)
        } catch (e: FirebaseFirestoreException) {
            Result.failure(e)
        }
    }

    override suspend fun getCategoryByIdFromFireStore(id: String): Result<Category?> {
        return try {
            val categoryDoc = firestore.collection("categories")
                .document(id)
                .get()
                .await()

            val category = categoryDoc.toObject(CategoryRemoteModel::class.java)
            Result.success(category?.toLocalCategory())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategoryRemoteByIdFromFireStore(id: String): Result<CategoryRemoteModel?> {
        return try {
            val categoryDoc = firestore.collection("categories")
                .document(id)
                .get()
                .await()

            val category = categoryDoc.toObject(CategoryRemoteModel::class.java)
            Result.success(category)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllSortedAndFilteredCategoriesFromFireStore(
        filterField: String?,
        filterValue: Any?,
        sortByField: String,
        ascending: Boolean,
    ): Result<List<Category>> {
        return try {
            val db = Firebase.firestore

            var query: Query = db.collection("categories")

            if (filterField != null && filterValue != null) {
                query = query.whereEqualTo(filterField, filterValue)
            }

            query = query.orderBy(
                sortByField,
                if (ascending) Query.Direction.ASCENDING else Query.Direction.DESCENDING
            )

            val snapshots = query.get().await()
            val categoryList = snapshots.documents.mapNotNull { document ->
                document.toObject(CategoryRemoteModel::class.java)?.toLocalCategory()
            }

            Result.success(categoryList)
        } catch (e: FirebaseFirestoreException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategoryId(): Result<String>{
        return try {
            val id = firestore.collection("categories").document().id
            Result.success(id)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun addCategory(category: CategoryRemoteModel): Result<Boolean> {
        return try{
            firestore.collection("categories").document().set(category)
            Result.success(true)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun updateCategory(id: String, category: CategoryRemoteModel): Result<Boolean> {
        return try{
            firestore.collection("categories").document(id).set(category)
            Result.success(true)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(id: String): Result<Boolean> {
        return try{
            firestore.collection("categories").document(id).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}