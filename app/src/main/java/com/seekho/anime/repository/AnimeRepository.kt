package com.seekho.anime.repository

import android.util.Log
import com.seekho.anime.db.AnimeDao
import com.seekho.anime.di.RetrofitModule
import com.seekho.anime.model.*
import com.seekho.anime.network.JikanApi
import com.seekho.anime.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AnimeRepository(
    private val api: JikanApi = RetrofitModule.api,
    private val dao: AnimeDao
) {
    // Expose DB as flow for offline usage
    fun observeLocalAnime(): Flow<List<Anime>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    // Fetch from network and store into DB
    suspend fun refreshTopAnime(): NetworkResult<Unit> {
        return try {
            val res = api.getTopAnime()
            if (res.isSuccessful) {
                val body = res.body()
                val dtoList = body?.data ?: emptyList()
                // For simplicity, not fetching characters separately for all — we will fetch details on demand
                val entities = dtoList.map { it.toEntity() }
                dao.insertAll(entities)
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("API error: ${res.code()} ${res.message()}")
            }
        } catch (e: Exception) {
            Log.e("AnimeRepo","refreshTopAnime", e)
            NetworkResult.Error(e.localizedMessage ?: "Unknown network error")
        }
    }

    suspend fun getAnimeDetail(id: Int): NetworkResult<Anime> {
        // Try local first
        val local = dao.getById(id)
        if (local != null) return NetworkResult.Success(local.toDomain())
        // Then network
        return try {
            val res = api.getAnimeDetails(id)
            if (res.isSuccessful) {
                val dto = res.body()!!
                // extract cast if present in dto.characters (if we used separate endpoint)
                val cast = dto.characters?.data?.map { it.character.name } ?: emptyList()
                val entity = dto.toEntity(cast)
                dao.insert(entity)
                NetworkResult.Success(entity.toDomain())
            } else {
                NetworkResult.Error("API error: ${res.code()} ${res.message()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unknown error")
        }
    }
}
