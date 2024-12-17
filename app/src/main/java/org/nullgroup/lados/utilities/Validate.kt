package org.nullgroup.lados.utilities

import android.util.Patterns.EMAIL_ADDRESS
import android.util.Patterns.PHONE

abstract class Validator(val data: String) {
    private var next: Validator? = null

    fun setNext(validator: Validator): Validator {
        this.next = validator
        return validator
    }

    fun validate(): Boolean {
        return check() && (next?.validate() ?: true)
    }

    protected abstract fun check(): Boolean
}

class EmailValidator(data: String) : Validator(data) {
    override fun check(): Boolean {
        return EMAIL_ADDRESS.matcher(this.data).matches()
    }
}

class PasswordValidator(data: String) : Validator(data) {
    override fun check(): Boolean {
        val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_])(?=\\S+$).{8,}$")

        return passwordRegex.matches(data)
    }
}

class PhoneNumberValidator(data: String) : Validator(data) {
    override fun check(): Boolean {
        val phoneRegex = Regex("^(\\+84|0)\\d{9}$")

        return phoneRegex.matches(data)
    }
}

class NotEmptyValidator(data: String) : Validator(data) {
    override fun check(): Boolean {
        return this.data.isNotEmpty()
    }
}