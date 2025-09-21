package com.seekho.anime.ui.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.seekho.anime.db.AnimeDatabase
import com.seekho.anime.model.Anime
import com.seekho.anime.network.NetworkResult
import com.seekho.anime.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AnimeListViewModel(application: Application): AndroidViewModel(application) {
    private val repo = AnimeRepository(dao = AnimeDatabase.getInstance(application).animeDao())

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private var _animeList: List<Anime> = emptyList()

    init {
        observeLocal()
        refreshIfNeeded()
    }

    private fun observeLocal() {
        viewModelScope.launch {
            repo.observeLocalAnime().collectLatest { list ->
                _animeList = list // Store the full list

                _uiState.value = UiState.Success(list)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val r = repo.refreshTopAnime()) {
                is NetworkResult.Success -> {
                    // local flow will emit new values
                }
                is NetworkResult.Error -> {
                    _uiState.value = UiState.Error(r.message)
                }
                else -> {}
            }
        }
    }

    private fun refreshIfNeeded() {
        // naive: call refresh on start
        refresh()
    }

    fun search(query: String) {
        val filteredList = if (query.isEmpty()) {
            _animeList
        } else {
            _animeList.filter {
                it.title!!.startsWith(query, ignoreCase = true)
            }
        }
        _uiState.value = UiState.Success(filteredList)
    }

    sealed class UiState {
        object Loading: UiState()
        data class Success(val data: List<com.seekho.anime.model.Anime>): UiState()
        data class Error(val message: String): UiState()
    }
}
