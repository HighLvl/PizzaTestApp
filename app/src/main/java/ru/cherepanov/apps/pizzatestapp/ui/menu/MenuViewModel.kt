package ru.cherepanov.apps.pizzatestapp.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.cherepanov.apps.pizzatestapp.domain.Repository
import ru.cherepanov.apps.pizzatestapp.domain.model.ProductDataResult
import javax.inject.Inject

sealed class Status {
    object Success : Status()
    object Loading : Status()
    data class Error(val message: String) : Status()
}

data class MenuState(
    val productDataResult: ProductDataResult? = null,
    val productDataStatus: Status = Status.Loading,
    val banners: List<Unit> = emptyList(),
    val bannersStatus: Status = Status.Loading
)

@HiltViewModel
class MenuViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(MenuState())
    val uiState: StateFlow<MenuState> = _uiState
    private var refreshProductDataJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        refreshProductData()
        refreshBanners()
    }

    private fun refreshProductData() {
        refreshProductDataJob?.cancel()
        refreshProductDataJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                productDataStatus = Status.Loading,
                productDataResult = null
            )
            repository.getProductData()
                .onFailure { throwable ->
                    _uiState.value = _uiState.value.copy(
                        productDataStatus = Status.Error(throwable.message.orEmpty()),
                        productDataResult = null
                    )
                }.onSuccess { productDataResult ->
                    _uiState.value = _uiState.value.copy(
                        productDataStatus = Status.Success,
                        productDataResult = productDataResult
                    )
                }
        }
    }

    private fun refreshBanners() {
        _uiState.value = _uiState.value.copy(
            bannersStatus = Status.Success,
            banners = (0..8).map { }
        )
    }
}