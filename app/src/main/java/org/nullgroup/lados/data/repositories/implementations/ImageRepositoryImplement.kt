package org.nullgroup.lados.data.repositories.implementations

import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.repositories.interfaces.ImageRepository

class ImageRepositoryImplement(
    private val firebaseStorage: FirebaseStorage
) : ImageRepository {

    /**
     * Sample usage:
     * ```
     * val imageRepository = ImageRepositoryImplement(firebaseStorage)
     * val userImage = drawable.toByteArray() // <- drawable is a Drawable object and uses the toByteArray extension function (which is in the ExtensionFunction.kt file)
     * val path = imageRepository.uploadImage(userImage, "users", "imageName", "png")
     * ```
     **/
    override suspend fun uploadImage(
        image: ByteArray,
        child: String,
        fileName: String,
        extension: String,
    ): String {
        val imageRef = firebaseStorage.reference
            .child("images")
            .child(child)
            .child("$fileName.$extension")

        val imageUrl: String = imageRef.putBytes(image).await().storage.downloadUrl.await().toString()

        return imageUrl
    }

    /**
     * Sample usage:
     * ```
     * val imageRepository = ImageRepositoryImplement(firebaseStorage)
     * imageRepository.deleteImage("users","imageName", "png")
     * ```
     **/
    override suspend fun deleteImage(child: String, fileName: String, extension: String) {
        val imageRef =
            firebaseStorage.reference.child("images").child(child).child("$fileName.$extension")
        imageRef.delete().addOnSuccessListener {
            Log.d("ImageRepositoryImplement", "Delete successful")
        }.addOnFailureListener {
            // Will changes to Result sealed class for better handle exception
            throw Exception("Delete failed")
        }
    }

    /**
     * Sample usage:
     * ```
     * val imageRepository = ImageRepositoryImplement(firebaseStorage)
     * val path = imageRepository.getPath("users", "userImage", ".png")
     * ```
     **/
    override suspend fun getPath(child: String, fileName: String, fileExtension: String): String {
        val imageRef =
            firebaseStorage.reference.child("images").child(child).child("$fileName.$fileExtension")
        return imageRef.path
    }

    /**
     * Sample usage:
     * ```
     * val imageRepository = ImageRepositoryImplement(firebaseStorage)
     * val link = imageRepository.getImageLink("child", "shortSkirt", ".png")
     * ```
     **/
    override suspend fun getImageUrl(
        child: String,
        fileName: String,
        fileExtension: String
    ): String {
        val imageRef =
            firebaseStorage.reference.child("images").child(child).child("$fileName.$fileExtension")
        val imageUrl = try {
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            // Will changes to Result sealed class for better handle exception
            throw Exception("Failed to get image URL")
        }

        Log.d("ImageRepositoryImplement", "image URL: $imageUrl")
        return imageUrl
    }
}