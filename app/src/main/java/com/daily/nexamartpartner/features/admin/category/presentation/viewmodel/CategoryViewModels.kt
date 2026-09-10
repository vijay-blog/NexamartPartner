package com.daily.nexamartpartner.features.admin.category.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.admin.category.domain.model.Category
import com.daily.nexamartpartner.features.admin.category.domain.model.CategoryAdminAction
import com.daily.nexamartpartner.features.admin.category.domain.model.CategoryDraft
import com.daily.nexamartpartner.features.admin.category.domain.model.CategoryQuery
import com.daily.nexamartpartner.features.admin.category.domain.usecase.CreateCategoryUseCase
import com.daily.nexamartpartner.features.admin.category.domain.usecase.GetCategoriesUseCase
import com.daily.nexamartpartner.features.admin.category.domain.usecase.GetCategoryDetailsUseCase
import com.daily.nexamartpartner.features.admin.category.domain.usecase.PerformCategoryActionUseCase
import com.daily.nexamartpartner.features.admin.category.domain.usecase.UpdateCategoryUseCase
import com.daily.nexamartpartner.features.admin.category.presentation.state.CategoryDetailsUiState
import com.daily.nexamartpartner.features.admin.category.presentation.state.CategoryFormUiState
import com.daily.nexamartpartner.features.admin.category.presentation.state.CategoryListUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface CategoryEvent {
    data object SessionExpired : CategoryEvent
    data class Message(val text: String) : CategoryEvent
    data class ActionSucceeded(val action: CategoryAdminAction) : CategoryEvent
    data object Saved : CategoryEvent
}

class CategoryListViewModel(
    private val getCategories: GetCategoriesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(CategoryListUiState())
    val uiState: StateFlow<CategoryListUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<CategoryEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    private val items = mutableListOf<Category>()
    private var job: Job? = null
    private var searchJob: Job? = null
    private var page = 0
    private var hasNext = true

    init {
        load(0, true)
    }

    fun search(value: String) {
        _state.update { it.copy(searchQuery = value) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(350)
            load(0, true)
        }
    }

    fun setActive(value: Boolean?) {
        _state.update { it.copy(active = value) }
        load(0, true)
    }

    fun clear() {
        _state.update { it.copy(searchQuery = "", active = null) }
        load(0, true)
    }

    fun refresh() {
        if (job?.isActive == true) return
        _state.update { it.copy(isRefreshing = true) }
        load(0, false)
    }

    fun retry() = load(0, true)

    fun next() {
        if (!hasNext || job?.isActive == true) return
        _state.update { it.copy(isLoadingMore = true) }
        load(page + 1, false, append = true)
    }

    private fun load(target: Int, showLoading: Boolean, append: Boolean = false) {
        if (job?.isActive == true) {
            if (append) return
            job?.cancel()
        }

        if (showLoading) {
            _state.update { it.copy(content = CategoryListUiState.Content.Loading) }
        }

        job = viewModelScope.launch {
            val current = _state.value
            when (
                val result = getCategories(
                    CategoryQuery(
                        page = target,
                        pageSize = 20,
                        search = current.searchQuery.trim().ifBlank { null },
                        active = current.active
                    )
                )
            ) {
                is AppResult.Success -> {
                    if (!append) items.clear()
                    items.addAll(result.data.categories)
                    page = result.data.page
                    hasNext = result.data.hasNextPage
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            isLoadingMore = false,
                            content = if (items.isEmpty()) {
                                CategoryListUiState.Content.Empty(
                                    if (current.searchQuery.isNotBlank()) {
                                        "No categories match your search."
                                    } else {
                                        "No categories found."
                                    }
                                )
                            } else {
                                CategoryListUiState.Content.Success(items.toList(), hasNext)
                            }
                        )
                    }
                }

                is AppResult.Failure -> {
                    if (result.error.type == FailureType.UNAUTHORIZED) {
                        _events.tryEmit(CategoryEvent.SessionExpired)
                    }

                    if (append && items.isNotEmpty()) {
                        _events.tryEmit(CategoryEvent.Message(result.error.message))
                        _state.update { it.copy(isLoadingMore = false, isRefreshing = false) }
                    } else {
                        _state.update {
                            it.copy(
                                isLoadingMore = false,
                                isRefreshing = false,
                                content = if (result.error.type == FailureType.CONTRACT_MISSING) {
                                    CategoryListUiState.Content.Unavailable(result.error.message)
                                } else {
                                    CategoryListUiState.Content.Error(result.error.message)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

class CategoryDetailsViewModel(
    private val id: String,
    private val get: GetCategoryDetailsUseCase,
    private val action: PerformCategoryActionUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(CategoryDetailsUiState())
    val uiState = _state.asStateFlow()

    private val _events = MutableSharedFlow<CategoryEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    private var job: Job? = null

    init {
        load(true)
    }

    fun refresh() {
        if (job?.isActive == true) return
        _state.update { it.copy(isRefreshing = true) }
        load(false)
    }

    fun retry() = load(true)

    fun perform(actionValue: CategoryAdminAction) {
        if (_state.value.actionInProgress != null) return

        viewModelScope.launch {
            _state.update { it.copy(actionInProgress = actionValue) }
            when (val result = action(id, actionValue)) {
                is AppResult.Success -> {
                    _events.emit(CategoryEvent.ActionSucceeded(actionValue))
                    load(false)
                }

                is AppResult.Failure -> {
                    if (result.error.type == FailureType.UNAUTHORIZED) {
                        _events.emit(CategoryEvent.SessionExpired)
                    } else {
                        _events.emit(CategoryEvent.Message(result.error.message))
                    }
                    _state.update { it.copy(actionInProgress = null) }
                }
            }
        }
    }

    private fun load(showLoading: Boolean) {
        job?.cancel()
        if (showLoading) {
            _state.update { it.copy(content = CategoryDetailsUiState.Content.Loading) }
        }

        job = viewModelScope.launch {
            when (val result = get(id)) {
                is AppResult.Success -> _state.update {
                    it.copy(
                        isRefreshing = false,
                        actionInProgress = null,
                        content = CategoryDetailsUiState.Content.Success(result.data)
                    )
                }

                is AppResult.Failure -> {
                    if (result.error.type == FailureType.UNAUTHORIZED) {
                        _events.emit(CategoryEvent.SessionExpired)
                    }
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            content = if (result.error.type == FailureType.CONTRACT_MISSING) {
                                CategoryDetailsUiState.Content.Unavailable(result.error.message)
                            } else {
                                CategoryDetailsUiState.Content.Error(result.error.message)
                            }
                        )
                    }
                }
            }
        }
    }
}

class CategoryFormViewModel(
    private val id: String?,
    private val get: GetCategoryDetailsUseCase,
    private val create: CreateCategoryUseCase,
    private val update: UpdateCategoryUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(CategoryFormUiState(editId = id))
    val uiState = _state.asStateFlow()

    private val _events = MutableSharedFlow<CategoryEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    init {
        if (!id.isNullOrBlank()) load()
    }

    private fun load() {
        val editId = id ?: return
        viewModelScope.launch {
            when (val result = get(editId)) {
                is AppResult.Success -> {
                    val category = result.data
                    _state.update {
                        it.copy(
                            name = category.name,
                            description = category.description.orEmpty(),
                            sortOrder = category.sortOrder?.toString().orEmpty()
                        )
                    }
                }

                is AppResult.Failure -> _state.update { it.copy(error = result.error.message) }
            }
        }
    }

    fun name(value: String) = _state.update { it.copy(name = value, error = null) }
    fun description(value: String) = _state.update { it.copy(description = value, error = null) }
    fun sort(value: String) = _state.update { it.copy(sortOrder = value, error = null) }

    fun save() {
        val state = _state.value
        if (state.name.trim().isBlank()) {
            _state.update { it.copy(error = "Category name is required.") }
            return
        }
        if (state.saving) return

        viewModelScope.launch {
            _state.update { it.copy(saving = true) }
            val draft = CategoryDraft(
                name = state.name.trim(),
                description = state.description.trim().ifBlank { null },
                sortOrder = state.sortOrder.trim().ifBlank { null }
            )

            val result = if (state.isEdit) {
                update(state.editId.orEmpty(), draft)
            } else {
                create(draft)
            }

            when (result) {
                is AppResult.Success -> {
                    _state.update { it.copy(saving = false) }
                    _events.emit(CategoryEvent.Saved)
                }

                is AppResult.Failure -> {
                    if (result.error.type == FailureType.UNAUTHORIZED) {
                        _events.emit(CategoryEvent.SessionExpired)
                    }
                    _state.update { it.copy(saving = false, error = result.error.message) }
                }
            }
        }
    }
}
