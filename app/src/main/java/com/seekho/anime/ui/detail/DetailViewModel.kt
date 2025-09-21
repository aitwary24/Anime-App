package com.seekho.anime.ui.detail


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.seekho.anime.db.AnimeDatabase
import com.seekho.anime.model.Anime
import com.seekho.anime.network.NetworkResult
import com.seekho.anime.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel(application: Application): AndroidViewModel(application) {
    private val repo = AnimeRepository(dao = AnimeDatabase.getInstance(application).animeDao())

    private val _state = MutableStateFlow<NetworkResult<Anime>>(NetworkResult.Loading)
    val state: StateFlow<NetworkResult<Anime>> = _state

    fun loadDetail(id: Int) {
        viewModelScope.launch {
            _state.value = NetworkResult.Loading
            val r = repo.getAnimeDetail(id)
            _state.value = r
        }
    }
}
