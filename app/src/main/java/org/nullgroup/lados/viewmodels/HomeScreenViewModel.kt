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
) : ViewModel()