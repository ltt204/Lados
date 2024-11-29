package org.nullgroup.lados.data.repositories.interfaces

interface ImageRepository {
    /**
     *  Uploads an image to the Firebase Storage
     * @param image: ByteArray - The image to be uploaded
     * @param child: String - The child to be uploaded to
     * @return String - The path of the uploaded image
     **/
    suspend fun uploadImage(image: ByteArray, child: String): String

    /**
     *  Deletes an image from the Firebase Storage
     * @param imageId: String - The id of the image to be deleted
     * @param child: String - The child to be deleted from
     **/
    suspend fun deleteImage(imageId: String, child: String)

    /**
     *  Gets the path of an image from the Firebase Storage
     * @param child: String - The child to get the image from
     * @param name: String - The name of the image
     * @param fileExtension: String - The file extension of the image
     * @return String - The path of the image
     **/
    suspend fun getPath(child: String, name: String, fileExtension: String): String
}