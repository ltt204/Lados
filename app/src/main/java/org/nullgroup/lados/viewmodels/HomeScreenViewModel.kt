package org.nullgroup.lados.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    fun getAll(): List<User> {
        return listOf(
            User(1, "John Doe", "example1@mail.com", "customer"),
            User(2, "Jane Doe 1", "example1@mail.com", "customer"),
            User(3, "Jane Doe 2", "example1@mail.com", "customer"),
            User(4, "Jane Doe 3", "example1@mail.com", "customer"),
            User(5, "Jane Doe 4", "example1@mail.com", "customer")
        )
    }
}