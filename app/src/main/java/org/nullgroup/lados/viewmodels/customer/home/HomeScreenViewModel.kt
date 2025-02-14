package org.nullgroup.lados.viewmodels.customer.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.nullgroup.lados.data.repositories.interfaces.user.UserRepository
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel()