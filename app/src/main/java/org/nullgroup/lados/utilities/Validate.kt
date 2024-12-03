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

class EmailValidator(data: String): Validator(data) {
    override fun check(): Boolean {
        return EMAIL_ADDRESS.matcher(this.data).matches()
    }
}

class PasswordValidator(data: String): Validator(data) {
    override fun check(): Boolean {
        return this.data.length >= 8
    }
}

class PhoneNumberValidator(data: String): Validator(data) {
    override fun check(): Boolean {
        return PHONE.matcher(this.data).matches()
    }
}

class NotEmptyValidator(data: String): Validator(data) {
    override fun check(): Boolean {
        return this.data.isNotEmpty()
    }
}