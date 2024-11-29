package org.nullgroup.lados.data.repositories.implementations

import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.repositories.interfaces.ImageRepository

class ImageRepositoryImplement(
    private val firebaseStorage: FirebaseStorage
) : ImageRepository {

    /**
     * Sample usage:
     * ```
     * val imageRepository = ImageRepositoryImplement(firebaseStorage)
     * val image = drawable.toByteArray() // <- drawable is a Drawable object and uses the toByteArray extension function (which is in the ExtensionFunction.kt file)
     * val path = imageRepository.uploadImage(image, "child")
     * ```
     **/
    override suspend fun uploadImage(
        image: ByteArray,
        child: String
    ): String {
        val imageRef = firebaseStorage.reference.child("images").child(child)
        imageRef.putBytes(image).addOnSuccessListener {
            Log.d("ImageRepositoryImplement", "Upload successful")
        }.addOnFailureListener {
            throw Exception("Upload failed")
        }
        return imageRef.path
    }

    /**
     * Sample usage:
     * ```
     * val imageRepository = ImageRepositoryImplement(firebaseStorage)
     * imageRepository.deleteImage("imageId", "child")
     * ```
     **/
    override suspend fun deleteImage(imageId: String, child: String) {
        val imageRef = firebaseStorage.reference.child("images").child(child)
        imageRef.delete().addOnSuccessListener {
            Log.d("ImageRepositoryImplement", "Delete successful")
        }.addOnFailureListener {
            throw Exception("Delete failed")
        }
    }

    /**
     * Sample usage:
     * ```
     * val imageRepository = ImageRepositoryImplement(firebaseStorage)
     * val path = imageRepository.getPath("child", "name", "fileExtension")
     * ```
     **/
    override suspend fun getPath(child: String, name: String, fileExtension: String): String {
        val imageRef =
            firebaseStorage.reference.child("images").child(child).child("$name.$fileExtension")
        return imageRef.path
    }
}