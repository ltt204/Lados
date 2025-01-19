package org.nullgroup.lados.data.repositories.interfaces.common

interface ImageRepository {
    /**
     *  Uploads an image to the Firebase Storage
     * @param image: ByteArray - The image to be uploaded
     * @param child: String - The child to be uploaded to
     * @param fileName: String - The name of the image
     * @param extension: String - The file extension of the image
     * @return String - The path of the uploaded image
     **/
    suspend fun uploadImage(
        image: ByteArray,
        child: String,
        fileName: String,
        extension: String
    ): Result<String>

    /**
     *  Deletes an image from the Firebase Storage
     * @param child: String - The child to be deleted from
     * @param fileName: String - The name of the image
     * @param extension: String - The file extension of the image
     **/
    suspend fun deleteImage(
        child: String,
        fileName: String,
        extension: String
    )

    /**
     *  Gets the path of an image from the Firebase Storage
     * @param child: String - The child to get the image from
     * @param fileName: String - The name of the image
     * @param fileExtension: String - The file extension of the image
     * @return String - The path of the image
     **/
    suspend fun getPath(child: String, fileName: String, fileExtension: String): String

    /**
     *  Gets the image from the Firebase Storage and sets it to an ImageView
     * @param child: String - The child to get the image from
     * @param fileName: String - The name of the image
     * @param fileExtension: String - The file extension of the image
     * @return String - The link of the image
     **/
    suspend fun getImageUrl(child: String, fileName: String, fileExtension: String): String
}