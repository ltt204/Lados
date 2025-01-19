package org.nullgroup.lados.viewmodels.admin.product

import dagger.hilt.android.lifecycle.HiltViewModel
import org.nullgroup.lados.data.repositories.interfaces.product.ProductRepository
import javax.inject.Inject


class EditProductVScreenViewModel @Inject constructor(
    private val productRepository: ProductRepository
) {
}