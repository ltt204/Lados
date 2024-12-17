package org.nullgroup.lados.utilities

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.Date

fun decodeJWT(token: String): DecodedJWT {
    return JWT.decode(token)
}

fun getExpirationDate(token: String): Date {
    val decodedJWT = decodeJWT(token)
    return decodedJWT.expiresAt
}

fun isTokenExpired(token: String): Boolean {
    val expirationDate = getExpirationDate(token)
    return expirationDate.before(Date())
}