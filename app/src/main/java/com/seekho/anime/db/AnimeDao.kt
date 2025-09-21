package com.seekho.anime.db


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seekho.anime.model.AnimeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<AnimeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(anime: AnimeEntity)

    @Query("SELECT * FROM anime ORDER BY lastFetched DESC")
    fun getAll(): Flow<List<AnimeEntity>>

    @Query("SELECT * FROM anime WHERE malId = :id LIMIT 1")
    suspend fun getById(id: Int): AnimeEntity?

    @Query("DELETE FROM anime")
    suspend fun clearAll()
}
