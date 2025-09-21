package com.seekho.anime.worker


import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.seekho.anime.db.AnimeDatabase
import com.seekho.anime.repository.AnimeRepository

class SyncWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val dao = AnimeDatabase.getInstance(applicationContext).animeDao()
        val repo = AnimeRepository(dao = dao)
        return when (val r = repo.refreshTopAnime()) {
            is com.seekho.anime.network.NetworkResult.Success -> Result.success()
            is com.seekho.anime.network.NetworkResult.Error -> Result.retry()
            else -> Result.retry()
        }
    }
}
